package com.example.todo_alarms.screens

import android.text.Layout
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.example.todo_alarms.viewmodels.TodoViewmodel
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todo_alarms.Data.TodoEntity
import com.example.todo_alarms.R


@Composable
fun TodoItem(
    todo: TodoEntity,
    onDelete: (TodoEntity) -> Unit,
    onComplete: (TodoEntity) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = if(todo.isCompleted)MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = todo.isCompleted,
                onCheckedChange = {isCheccked->
                    onComplete(todo)
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = MaterialTheme.colorScheme.onSurface,
                    checkmarkColor = MaterialTheme.colorScheme.onPrimary
            )

            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = todo.todo,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (todo.isCompleted) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface,
                    textDecoration = if (todo.isCompleted) TextDecoration.LineThrough else null,
                    maxLines = 2
                )
                if (todo.isCompleted) {
                    Text(
                        text = "Completed",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }


            IconButton(
                onClick = { onDelete(todo) },
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete To-Do",
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}
@Composable
fun TodoListScreen(todoViewmodel: TodoViewmodel) {
    val allTodos by todoViewmodel.todos.observeAsState(initial = emptyList())
    Log.d("TodoListScreen", "Observed Todos: $allTodos")
    if (allTodos.isEmpty()) {
        Log.d("TodoScreen", "Empty todo list state triggered.")
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                Image(
                    painter = painterResource(id = R.drawable.todolist),
                    contentDescription = "No tasks to do",
                    modifier = Modifier.size(200.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "No tasks to do", modifier = Modifier, fontSize = 24.sp)
            }

        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
                .fillMaxSize()
                .padding(16.dp),
        ) {
            items(allTodos) { todoEntity ->
                TodoItem(
                    todo = todoEntity,
                    onDelete = { todoViewmodel.deleteTodo(it) },
                    onComplete = { todoViewmodel.updateTodo(it.copy(isCompleted = !it.isCompleted)) }
                )


            }
        }
    }
}