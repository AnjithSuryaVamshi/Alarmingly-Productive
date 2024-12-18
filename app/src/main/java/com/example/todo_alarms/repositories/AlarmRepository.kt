package com.example.todo_alarms.repositories

import androidx.lifecycle.LiveData
import com.example.todo_alarms.Data.AlarmEntity
import com.example.todo_alarms.Data.Alarm_Dao

class AlarmRepository(private val alarmDao: Alarm_Dao) {
    val activeAlarms : LiveData<List<AlarmEntity>> = alarmDao.getAlarms()
    suspend fun updateAlarm(alarm: AlarmEntity) {
        alarmDao.updateAlarm(alarm)
    }
    suspend fun insertAlarm(alarm: AlarmEntity) {
        alarmDao.InsertAlarm(alarm)
    }
    suspend fun deleteAlarm(alarm: AlarmEntity) {
        alarmDao.deleteAlarm(alarm)
    }
}