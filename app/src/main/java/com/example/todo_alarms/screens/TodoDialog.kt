package com.example.todo_alarms.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.example.todo_alarms.Data.TodoEntity
import com.example.todo_alarms.viewmodels.TodoViewmodel
import kotlinx.coroutines.launch

@Composable
fun TodoDialog(
    openDialog: Boolean,
    todoViewmodel: TodoViewmodel,
    onDismiss: () -> Unit
) {
    if (openDialog) {
        var taskText by remember { mutableStateOf("") }
        var isInputValid by remember { mutableStateOf(true) }
        val coroutineScope = rememberCoroutineScope()

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = "Add New Todo") },
            text = {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = taskText,
                        onValueChange = {
                            taskText = it
                            isInputValid = it.isNotEmpty()
                        },
                        label = { Text("Task") },
                        isError = !isInputValid
                    )
                    if (!isInputValid) {
                        Text(
                            text = "Task cannot be empty",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (taskText.isNotEmpty()) {
                        coroutineScope.launch {
                            todoViewmodel.insertTodo(
                                TodoEntity(
                                    id = 0,
                                    todo = taskText,
                                    isCompleted = false
                                )
                            )
                            onDismiss()
                        }
                    } else {
                        isInputValid = false
                    }
                }) {
                    Text("Add")
                }
            },
            dismissButton = {
                Button(onClick = onDismiss) {
                    Text("Cancel")
                }
            },
            modifier = Modifier.padding(16.dp),
            shape = MaterialTheme.shapes.medium,
            containerColor = Color.White,
            tonalElevation = 4.dp,
            properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
        )
    }
}
