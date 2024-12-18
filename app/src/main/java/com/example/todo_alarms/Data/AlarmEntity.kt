package com.example.todo_alarms.Data

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity("AlarmEntity")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String?,
    val todo: String?,
    val isActive : Boolean,
    val time: Long,
)
