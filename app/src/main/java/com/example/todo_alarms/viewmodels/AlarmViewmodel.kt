package com.example.todo_alarms.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo_alarms.Data.AlarmEntity
import com.example.todo_alarms.repositories.AlarmRepository
import kotlinx.coroutines.launch

class AlarmViewmodel(private val repository: AlarmRepository) : ViewModel()  {
    val activeAlarms : LiveData<List<AlarmEntity>> = repository.activeAlarms

     fun updateAlarm(alarm: AlarmEntity) = viewModelScope.launch {
        repository.updateAlarm(alarm)
    }
     fun insertAlarm(alarm: AlarmEntity) = viewModelScope.launch{
        repository.insertAlarm(alarm)
    }
     fun deleteAlarm(alarm: AlarmEntity) = viewModelScope.launch {
        repository.deleteAlarm(alarm)
    }

}