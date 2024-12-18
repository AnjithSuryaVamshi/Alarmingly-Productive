package com.example.todo_alarms.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.todo_alarms.Data.AlarmEntity
import com.example.todo_alarms.viewmodels.AlarmViewmodel
import com.example.todo_alarms.viewmodels.TodoViewmodel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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
            title = { Text(text = "Set Alarm") },
            text = {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    TextField(
                        value = alarmTitle,
                        onValueChange = { alarmTitle = it },
                        label = { Text("Alarm Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = { isTimePickerVisible = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = if (alarmTime.isEmpty()) "Select Time" else "Time: $alarmTime")
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
                            label = { Text("Your Todo list is empty !! Add a task") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Adding this To-do will also save it in your To-do list.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
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
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { onDismiss() }) {
                    Text("Cancel")
                }
            }
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