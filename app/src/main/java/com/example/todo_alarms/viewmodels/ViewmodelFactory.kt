package com.example.todo_alarms.viewmodels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.todo_alarms.repositories.AlarmRepository
import com.example.todo_alarms.repositories.TodoRepository
import android.app.Application


class AlarmViewmodelFactory(private val application: Application,private  val repository: AlarmRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass :Class<T>):T{
        if (modelClass.isAssignableFrom(AlarmViewmodel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return AlarmViewmodel(application,repository) as T
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
