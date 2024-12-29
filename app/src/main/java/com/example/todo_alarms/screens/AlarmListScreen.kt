package com.example.todo_alarms.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import com.example.todo_alarms.Data.AlarmEntity
import com.example.todo_alarms.R
import com.example.todo_alarms.viewmodels.AlarmViewmodel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

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
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFEDEDED),
            contentColor = Color.Black
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                alarm.title?.let {
                    Text(
                        text = it,
                        fontSize = 16.sp,
                        color = Color(0xFF666666)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${formatTime(alarm.time)}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )
            }
            Switch(
                checked = alarm.isActive,
                onCheckedChange = { onToggle(alarm.copy(isActive = it)) },
                modifier = Modifier.padding(end = 16.dp),
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    uncheckedThumbColor = MaterialTheme.colorScheme.error,
                    checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    uncheckedTrackColor = MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
                )
            )
            IconButton(
                onClick = { onDelete(alarm) },
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp)
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
fun AlarmListScreen(
    alarmViewmodel: AlarmViewmodel,
    modifier: Modifier = Modifier
) {
    val activeAlarms by alarmViewmodel.activeAlarms.observeAsState(initial = emptyList())
    var currentTime by remember { mutableStateOf(com.example.todo_alarms.alarmmanager.getFormattedTime()) }
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = com.example.todo_alarms.alarmmanager.getFormattedTime()
            delay(1000L)
        }
    }
    if (activeAlarms.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.sleepygirl),
                contentDescription = "No alarms",
                modifier = Modifier.size(150.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No active alarms",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
            )


        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),

                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = currentTime,
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
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
fun getFormattedTime(): String {
    val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return dateFormat.format(Date())
}
