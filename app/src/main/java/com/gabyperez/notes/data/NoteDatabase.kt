package com.gabyperez.notes.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.gabyperez.notes.model.Multimedia
import com.gabyperez.notes.model.Note
import com.gabyperez.notes.model.Reminder

@Database(entities = [Note::class, Multimedia::class, Reminder::class], version = 1, exportSchema = false)
abstract class NoteDatabase : RoomDatabase(){
    abstract fun NoteDao(): NoteDao
    abstract fun MultimediaDao(): MultimediaDao
    abstract fun RerminderDAO(): RerminderDAO

    companion object {
        private var INSTANCE: NoteDatabase? = null
        fun getDatabase(context: Context): NoteDatabase {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE =
                        Room.databaseBuilder(context,NoteDatabase::class.java, "note")
                            .allowMainThreadQueries()
                            .build()
                }
            }
            return INSTANCE!!
        }
    }
}