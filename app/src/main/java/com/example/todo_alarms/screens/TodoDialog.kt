package com.example.todo_alarms.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.example.todo_alarms.Data.TodoEntity
import com.example.todo_alarms.viewmodels.TodoViewmodel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
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
            title = {
                Text(
                    text = "Add New Todo",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = taskText,
                        onValueChange = {
                            taskText = it
                            isInputValid = it.isNotEmpty()
                        },
                        label = { Text("Task", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        isError = !isInputValid,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            containerColor = MaterialTheme.colorScheme.surface,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            errorBorderColor = MaterialTheme.colorScheme.error,
                            cursorColor = MaterialTheme.colorScheme.primary,
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
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
                Button(
                    onClick = {
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
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = onDismiss,
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Text("Cancel")
                }
            },
            modifier = Modifier.padding(16.dp),
            shape = MaterialTheme.shapes.medium,
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp,
            properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
        )
    }
}
