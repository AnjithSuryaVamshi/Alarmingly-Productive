package com.example.todo_alarms.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo_alarms.Data.AlarmEntity
import com.example.todo_alarms.alarmmanager.AlarmManagerClass
import com.example.todo_alarms.repositories.AlarmRepository
import kotlinx.coroutines.launch


class AlarmViewmodel(
    application: Application,
    private val repository: AlarmRepository

) :  AndroidViewModel(application)  {

    private val alarmManager = AlarmManagerClass(application)

    val activeAlarms: LiveData<List<AlarmEntity>> = repository.activeAlarms

    fun updateAlarm(alarm: AlarmEntity) = viewModelScope.launch {
        repository.updateAlarm(alarm)

        alarmManager.scheduleAlarm(alarm)
    }

    fun insertAlarm(alarm: AlarmEntity) = viewModelScope.launch {
        repository.insertAlarm(alarm)

        alarmManager.scheduleAlarm(alarm)
    }

    fun deleteAlarm(alarm: AlarmEntity) = viewModelScope.launch {
        repository.deleteAlarm(alarm)

        alarmManager.cancelAlarm(alarm.id)
    }
}
