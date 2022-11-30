package com.gabyperez.notes.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.gabyperez.notes.model.Multimedia

@Dao
interface MultimediaDao {
    @Insert
    fun insert(multimedia: Multimedia): Long

    @Query("SELECT * FROM multimedia WHERE idNote=:idNote")
    fun getMultimedia(idNote: Int): List<Multimedia>
}