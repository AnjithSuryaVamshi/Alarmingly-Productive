package com.example.todo_alarms.Data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("todo_entity")
data class TodoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val todo: String,
    val isCompleted : Boolean,
)
