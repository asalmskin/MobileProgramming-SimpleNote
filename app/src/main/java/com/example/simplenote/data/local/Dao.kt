
package com.example.simplenote.data.local

import androidx.paging.PagingSource
import androidx.room.*

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes WHERE deleted=0 AND (title LIKE :q OR content LIKE :q) ORDER BY updatedAt DESC")
    fun paging(q: String): PagingSource<Int, NoteEntity>

    @Query("SELECT * FROM notes WHERE id=:id")
    suspend fun byId(id: String): NoteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(note: NoteEntity)

    @Query("DELETE FROM notes WHERE id=:id")
    suspend fun delete(id: String)
}
