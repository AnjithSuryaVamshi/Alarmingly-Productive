package com.example.todo_alarms.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.todo_alarms.viewmodels.TodoViewmodel

@Composable
fun todo(navController: NavController,todoViewmodel: TodoViewmodel) {
    var showTodoDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showTodoDialog = true
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add To-do")
            }
        }
    ) {
            paddingValues->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TodoListScreen(todoViewmodel)
            if (showTodoDialog) {
                Log.d("TodoScreen", "Displaying TodoDialog")
                TodoDialog(
                    openDialog = showTodoDialog,
                    todoViewmodel = todoViewmodel,
                    onDismiss = { showTodoDialog = false },
                )
            }
        }
    }
}