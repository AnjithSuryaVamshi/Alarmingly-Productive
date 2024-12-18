package com.example.todo_alarms.Data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert

@Dao
interface Todo_Dao {
    @Insert
    suspend fun InsertTodo(todo: TodoEntity)

    @Delete
    suspend fun deleteTodo(todo: TodoEntity)

    @Query("SELECT * FROM todo_entity ")
    fun getTodos(): LiveData<List<TodoEntity>>

    @Update
    suspend fun updateTodo(todo: TodoEntity)

    @Query("SELECT COUNT(*) > 0 FROM todo_entity WHERE todo LIKE '%' || :userInput || '%'")
    suspend fun isTodoPartiallyValid(userInput: String): Boolean

}