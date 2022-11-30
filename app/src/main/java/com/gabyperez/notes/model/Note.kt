package com.gabyperez.notes.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Note (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val type: Int,
    val title: String,
    val description: String,
    val dateCreation: String,
    val dateEnd: String,
    val hourEnd: String,
    val completed: Int
) {
    constructor(type: Int, title: String, description: String, dateCreation: String, dateEnd: String, hourEnd: String, completed: Int)
            : this(0, type, title, description, dateCreation, dateEnd, hourEnd, completed)
}