package com.example.simplenote.data.repo

import android.content.Context
import androidx.paging.*
import com.example.simplenote.data.local.AppDb
import com.example.simplenote.data.local.NoteEntity
import com.example.simplenote.data.remote.*
import kotlinx.coroutines.flow.Flow
import java.time.Instant
import java.util.UUID

class Repository(
    private val context: Context,
    private val baseUrl: String = "https://simple.darkube.app/"
) {
    private val db = AppDb.create(context)
    private val api = apiService(context, baseUrl)
    private val tokens = TokenStore(context)

    suspend fun login(username: String, password: String) {
        val t = api.login(TokenObtainPairRequest(username, password))
        tokens.save(t.access, t.refresh ?: "")
    }

    suspend fun register(first: String, last: String, username: String, email: String, password: String) {
        api.register(RegisterRequest(username = username, password = password, email = email, first_name = first, last_name = last))
    }

    suspend fun logout() { tokens.clear() }

    suspend fun refresh() {
        val r = tokens.refresh() ?: return
        val t = api.refresh(TokenRefreshRequest(r))
        tokens.save(t.access, r)
    }

    suspend fun userInfo(): UserInfo = api.userinfo()

    suspend fun changePassword(old: String, new: String) {
        api.changePassword(ChangePasswordRequest(old_password = old, new_password = new))
    }

    fun notesPaged(query: String): Flow<PagingData<NoteEntity>> =
        Pager(PagingConfig(pageSize = 10)) { db.notes().paging("%$query%") }.flow

    suspend fun syncOnce(query: String = "") {
        try {
            var page = 1
            while (true) {
                val p = if (query.isBlank())
                    api.notes(page = page, pageSize = 10)
                else
                    api.notesFilter(title = query, description = query, page = page, pageSize = 10)

                p.results.forEach {
                    val idStr = it.id.toString()
                    val updated = Instant.parse(it.updatedAt).toEpochMilli()
                    val created = Instant.parse(it.createdAt).toEpochMilli()
                    db.notes().upsert(
                        NoteEntity(
                            id = idStr,
                            title = it.title,
                            content = it.description,
                            updatedAt = updated,
                            createdAt = created,
                            dirty = false,
                            deleted = false
                        )
                    )
                }
                if (p.next == null) break
                page += 1
            }
        } catch (_: Exception) { }
    }

    suspend fun create(title: String, content: String): String {
        return try {
            val dto = api.createNote(NoteCreate(title = title, description = content))
            val updated = Instant.parse(dto.updatedAt).toEpochMilli()
            val created = Instant.parse(dto.createdAt).toEpochMilli()
            db.notes().upsert(
                NoteEntity(
                    id = dto.id.toString(),
                    title = dto.title,
                    content = dto.description,
                    updatedAt = updated,
                    createdAt = created,
                    dirty = false,
                    deleted = false
                )
            )
            dto.id.toString()
        } catch (_: Exception) {
            val id = UUID.randomUUID().toString()
            val now = System.currentTimeMillis()
            db.notes().upsert(NoteEntity(id, title, content, now, now, true, false))
            id
        }
    }

    suspend fun update(id: String, title: String, content: String) {
        try {
            val nid = id.toIntOrNull()
            if (nid != null) {
                val dto = api.update(nid, NoteCreate(title = title, description = content))
                val updated = Instant.parse(dto.updatedAt).toEpochMilli()
                val created = Instant.parse(dto.createdAt).toEpochMilli()
                db.notes().upsert(
                    NoteEntity(dto.id.toString(), dto.title, dto.description, updated, created, false, false)
                )
            } else {
                val cur = db.notes().byId(id) ?: return
                db.notes().upsert(cur.copy(title = title, content = content, updatedAt = System.currentTimeMillis(), dirty = true))
            }
        } catch (_: Exception) {
            val cur = db.notes().byId(id) ?: return
            db.notes().upsert(cur.copy(title = title, content = content, updatedAt = System.currentTimeMillis(), dirty = true))
        }
    }

    suspend fun delete(id: String) {
        try {
            val nid = id.toIntOrNull()
            if (nid != null) {
                api.delete(nid)
                db.notes().delete(id)
            } else {
                val cur = db.notes().byId(id) ?: return
                db.notes().upsert(cur.copy(deleted = true))
            }
        } catch (_: Exception) {
            val cur = db.notes().byId(id) ?: return
            db.notes().upsert(cur.copy(deleted = true))
        }
    }

    suspend fun note(id: String): NoteEntity? = db.notes().byId(id)
}
