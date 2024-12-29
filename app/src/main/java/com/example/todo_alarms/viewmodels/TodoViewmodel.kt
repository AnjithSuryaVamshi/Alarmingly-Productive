package com.example.todo_alarms.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo_alarms.Data.TodoEntity
import com.example.todo_alarms.repositories.TodoRepository
import kotlinx.coroutines.launch

class TodoViewmodel(private val repository: TodoRepository) : ViewModel()  {

    val todos  : LiveData<List<TodoEntity>> = repository.todos
     fun updateTodo(todo: TodoEntity)= viewModelScope.launch{
        repository.updateTodo(todo)
    }
    fun insertTodo(todo: TodoEntity) = viewModelScope.launch{
        repository.insertTodo(todo)
    }
    fun deleteTodo(todo: TodoEntity) = viewModelScope.launch {
        repository.deleteTodo(todo)
    }
     fun isTodoPartiallyValid(userInput: String): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch {
            result.postValue(repository.isTodoPartiallyValid(userInput))
        }
        return result
    }
     suspend fun getActiveTodos(): List<TodoEntity> {
        return repository.getActiveTodos()

    }


}