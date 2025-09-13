
package com.example.simplenote.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [NoteEntity::class], version = 1, exportSchema = false)
abstract class AppDb : RoomDatabase() {
    abstract fun notes(): NoteDao
    companion object {
        fun create(context: Context) = Room.databaseBuilder(context, AppDb::class.java, "simplenote.db").build()
    }
}
