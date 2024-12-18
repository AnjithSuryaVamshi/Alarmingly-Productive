package com.example.todo_alarms.screens

import android.icu.util.Calendar
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun TimePickerDialog(
    onTimeSelected : (String) -> Unit,
    onDismiss : () -> Unit
) {
    val context = LocalContext.current
    val  calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    android.app.TimePickerDialog(
        context,
        { _, selectedHour, selectedMinute ->
            val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
            onTimeSelected(formattedTime)
        },
        hour,
        minute,
        true

    ).apply {
        setOnDismissListener {
            onDismiss()
        }
        show()
    }
}