package com.example.todo_alarms.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.todo_alarms.repositories.AlarmRepository
import com.example.todo_alarms.repositories.TodoRepository

class AlarmViewmodelFactory(private  val repository: AlarmRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass :Class<T>):T{
        if (modelClass.isAssignableFrom(AlarmViewmodel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return AlarmViewmodel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
class TodoViewmodelFactory(private val repository: TodoRepository):ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(TodoViewmodel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return TodoViewmodel(repository) as T

        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
