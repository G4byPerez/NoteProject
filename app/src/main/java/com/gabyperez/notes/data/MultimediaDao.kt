package com.gabyperez.notes.data

import androidx.room.Dao
import androidx.room.Insert
import com.gabyperez.notes.model.Multimedia

@Dao
interface MultimediaDao {
    @Insert
    fun insert(multimedia: Multimedia): Long
}