
package com.example.simplenote.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey val id: String,
    val title: String,
    val content: String,
    val updatedAt: Long,
    val createdAt: Long,
    val dirty: Boolean,
    val deleted: Boolean
)
