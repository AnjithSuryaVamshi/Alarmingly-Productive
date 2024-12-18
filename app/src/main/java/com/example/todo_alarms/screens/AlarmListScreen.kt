package com.example.todo_alarms.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todo_alarms.Data.AlarmEntity
import com.example.todo_alarms.R
import com.example.todo_alarms.viewmodels.AlarmViewmodel
import java.util.Calendar

fun formatTime(alarmTimeInMillis: Long): String {
    val calendar = Calendar.getInstance().apply { timeInMillis = alarmTimeInMillis }
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)
    return String.format("%02d:%02d", hour, minute)
}


@Composable
fun AlarmItemCard(
    alarm: AlarmEntity,
    onToggle: (AlarmEntity) -> Unit,
    onDelete: (AlarmEntity) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${alarm.title} at ${formatTime(alarm.time)}",
                fontSize = 18.sp,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = alarm.isActive,
                onCheckedChange = { onToggle(alarm.copy(isActive = it)) }
            )
            IconButton(onClick = { onDelete(alarm) }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Alarm", tint = Color.Red)
            }
        }
    }
}

@Composable
fun AlarmListScreen(
    alarmViewmodel: AlarmViewmodel,
    modifier: Modifier = Modifier
) {
    val activeAlarms by alarmViewmodel.activeAlarms.observeAsState(initial = emptyList())
    if (activeAlarms.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(painter = painterResource(id = R.drawable.sleepygirl), contentDescription = "No alarms",modifier = Modifier.size(150.dp) )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "No active alarms", modifier = Modifier, fontSize = 24.sp)
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(activeAlarms) { alarmEntity ->
                AlarmItemCard(
                    alarm = alarmEntity,
                    onToggle = { updatedAlarm ->
                        alarmViewmodel.updateAlarm(updatedAlarm)
                    },
                    onDelete = { alarmToDelete ->
                        alarmViewmodel.deleteAlarm(alarmToDelete)
                    }
                )
            }
        }
    }
}
