package com.example.todo_alarms.Data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface Alarm_Dao {
    @Insert
    suspend fun InsertAlarm(alarm: AlarmEntity)

    @Update
    suspend fun updateAlarm(alarm: AlarmEntity)

    @Delete
    suspend fun deleteAlarm(alarm: AlarmEntity)

    @Query("SELECT * FROM AlarmEntity ORDER BY time ASC")
    fun getAlarms(): LiveData<List<AlarmEntity>>


}