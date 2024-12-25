package com.example.todo_alarms.alarmmanager

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.todo_alarms.alarmmanager.AlarmManagerClass

class AlarmActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("AlarmActivity", "AlarmActivity started with intent data: ${intent.extras}")
        val alarmId = intent.getIntExtra("id", 0)
        val title = intent.getStringExtra("title")
        val todo = intent.getStringExtra("todo")

        setContent {
            FullScreenAlarmUI(
                title = title ?: "Alarm",
                todo = todo ?: "To-do Reminder",
                onDismiss = {
                    AlarmManagerClass(this).cancelAlarm(alarmId)
                    finish()
                },
                onSnooze = {
                    AlarmManagerClass(this).scheduleSnooze(alarmId, 5 * 60 * 1000, title, todo)
                    finish()
                }
            )
        }
    }
}

@Composable
fun FullScreenAlarmUI(title: String, todo: String, onDismiss: () -> Unit, onSnooze: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Alarm: $title", modifier = Modifier.padding(8.dp))
        Text(text = "To-do: $todo", modifier = Modifier.padding(8.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onSnooze, modifier = Modifier.padding(8.dp)) {
            Text("Snooze")
        }
        Button(onClick = onDismiss, modifier = Modifier.padding(8.dp)) {
            Text("Dismiss")
        }
    }
}
