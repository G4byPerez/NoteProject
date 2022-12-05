package com.gabyperez.notes.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.gabyperez.notes.model.Reminder

@Dao
interface RerminderDAO{

    @Insert
    fun insert(reminder: Reminder)

    @Query("SELECT * FROM Reminder WHERE noteID=:id")
    fun getAllReminders(id: Int): MutableList<Reminder>

    @Query("DELETE FROM Reminder WHERE noteID = :id")
    fun deleteAllReminders(id: Int)

    @Delete
    fun deleteReminder(reminder: Reminder)

    @Query("SELECT MAX(ID) FROM reminder")
    fun getMaxId(): Int
}