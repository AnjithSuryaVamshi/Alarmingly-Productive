package com.example.todo_alarms.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.todo_alarms.viewmodels.AlarmViewmodel
import com.example.todo_alarms.viewmodels.TodoViewmodel

@Composable
fun AlarmScreen(
    navController: NavController,
    alarmViewmodel: AlarmViewmodel,
    todoViewmodel: TodoViewmodel
) {
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showDialog = true
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Alarm")
            }
        }
    ) { padding ->
        AlarmListScreen(
            alarmViewmodel = alarmViewmodel,
            modifier = Modifier.padding(padding)
        )

        if (showDialog) {
            AlarmDialog(
                openDialog = showDialog,
                alarmViewmodel = alarmViewmodel,
                todoViewmodel = todoViewmodel,
                onDismiss = { showDialog = false }
            )
        }
    }
}
