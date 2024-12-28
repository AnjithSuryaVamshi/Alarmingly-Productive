package com.example.todo_alarms.repositories

import androidx.lifecycle.LiveData
import com.example.todo_alarms.Data.TodoEntity
import com.example.todo_alarms.Data.Todo_Dao

class TodoRepository(private val todoDao: Todo_Dao)  {
    val todos : LiveData<List<TodoEntity>> = todoDao.getTodos()

    suspend fun updateTodo(todo: TodoEntity) {
        todoDao.updateTodo(todo)
    }
    suspend fun insertTodo(todo: TodoEntity) {
        todoDao.InsertTodo(todo)
    }
    suspend fun deleteTodo(todo: TodoEntity) {
        todoDao.deleteTodo(todo)
    }
    suspend fun isTodoPartiallyValid(userInput: String): Boolean {
        return todoDao.isTodoPartiallyValid(userInput)

    }
    suspend fun getActiveTodos(): LiveData<List<TodoEntity>> {
        return todoDao.getActiveTodos()

    }
}