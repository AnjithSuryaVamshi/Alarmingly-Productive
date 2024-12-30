package com.example.todo_alarms.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.todo_alarms.Data.AlarmEntity
import com.example.todo_alarms.viewmodels.AlarmViewmodel
import com.example.todo_alarms.viewmodels.TodoViewmodel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmDialog(
    openDialog: Boolean,
    todoViewmodel: TodoViewmodel,
    alarmViewmodel: AlarmViewmodel,
    onDismiss: () -> Unit
) {
    if (openDialog) {
        var alarmTime by remember { mutableStateOf("") }
        var alarmTitle by remember { mutableStateOf("") }
        var alarmTodo by remember { mutableStateOf("") }
        var isTimePickerVisible by remember { mutableStateOf(false) }

        val todos by todoViewmodel.todos.observeAsState(initial = emptyList())
        var showToast by remember { mutableStateOf(false) }

        if (showToast) {
            Toast.makeText(
                LocalContext.current,
                "To-do is mandatory for creating an alarm",
                Toast.LENGTH_SHORT
            ).show()
            showToast = false
        }

        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = {
                Text(
                    text = "Set Alarm",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    TextField(
                        value = alarmTitle,
                        onValueChange = { alarmTitle = it },
                        label = { Text("Alarm Title") },
                        colors = TextFieldDefaults.textFieldColors(
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            containerColor = MaterialTheme.colorScheme.surface,
                            cursorColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = { isTimePickerVisible = true },
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (alarmTime.isEmpty()) "Select Time" else "Time: $alarmTime"
                        )
                    }
                    if (isTimePickerVisible) {
                        TimePickerDialog(
                            onTimeSelected = {
                                alarmTime = it
                                isTimePickerVisible = false
                            },
                            onDismiss = { isTimePickerVisible = false }
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    if (todos.isEmpty()) {
                        TextField(
                            value = alarmTodo,
                            onValueChange = { alarmTodo = it },
                            label = { Text("Your Todo list is empty! Add a task") },
                            colors = TextFieldDefaults.textFieldColors(
                                focusedLabelColor = MaterialTheme.colorScheme.primary,
                                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                                unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                containerColor = MaterialTheme.colorScheme.surface,
                                cursorColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Adding this To-do will also save it in your To-do list.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            confirmButton = {
                OutlinedButton(
                    onClick = {
                        if (alarmTitle.isNotEmpty() && alarmTime.isNotEmpty()) {
                            if (todos.isEmpty() && alarmTodo.isEmpty()) {
                                showToast = true // Trigger toast
                                return@OutlinedButton
                            }
                            if (todos.isEmpty() && alarmTodo.isNotEmpty()) {
                                todoViewmodel.insertTodo(
                                    com.example.todo_alarms.Data.TodoEntity(
                                        todo = alarmTodo,
                                        isCompleted = false
                                    )
                                )
                            }
                            alarmViewmodel.insertAlarm(
                                AlarmEntity(
                                    id = 0,
                                    title = alarmTitle,
                                    todo = if (todos.isEmpty()) alarmTodo else "",
                                    isActive = true,
                                    time = parseTimeToMillis(alarmTime)
                                )
                            )
                            onDismiss()
                        }
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { onDismiss() },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Text("Cancel")
                }
            },
            containerColor = MaterialTheme.colorScheme.background,
            tonalElevation = 4.dp
        )
    }
}

fun parseTimeToMillis(time: String): Long {
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault()) // 24-hour time format
    val date = timeFormat.parse(time) ?: throw IllegalArgumentException("Invalid time format")
    val calendar = Calendar.getInstance()
    calendar.time = date

    val now = Calendar.getInstance()
    calendar.set(Calendar.YEAR, now.get(Calendar.YEAR))
    calendar.set(Calendar.MONTH, now.get(Calendar.MONTH))
    calendar.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH))

    return calendar.timeInMillis
}
