package com.example.todo_alarms.nav

import android.app.Application
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.todo_alarms.Data.AppDataBase
import com.example.todo_alarms.repositories.AlarmRepository
import com.example.todo_alarms.repositories.TodoRepository
import com.example.todo_alarms.screens.AlarmScreen
import com.example.todo_alarms.screens.todo
import com.example.todo_alarms.viewmodels.AlarmViewmodel
import com.example.todo_alarms.viewmodels.AlarmViewmodelFactory
import com.example.todo_alarms.viewmodels.TodoViewmodel
import com.example.todo_alarms.viewmodels.TodoViewmodelFactory
@Composable
fun NavigationGraph(navController: NavHostController) {
    val context = LocalContext.current
    val application = context.applicationContext as Application


    val db = AppDataBase.getDatabase(application)
    val alarmDao = db.alarmDao()
    val alarmRepository = AlarmRepository(alarmDao)
    val todoDao = db.todoDao()
    val todoRepository = TodoRepository(todoDao)


    val alarmViewmodel: AlarmViewmodel = viewModel(
        factory = AlarmViewmodelFactory(application, alarmRepository)
    )
    val todoViewmodel: TodoViewmodel = viewModel(
        factory = TodoViewmodelFactory(todoRepository)
    )


    NavHost(navController = navController, startDestination = Routes.todo_screen) {
        composable(Routes.todo_screen) {
            todo(navController = navController, todoViewmodel = todoViewmodel)
        }
        composable(Routes.alarm_screen) {
            AlarmScreen(
                navController = navController,
                alarmViewmodel = alarmViewmodel,
                todoViewmodel = todoViewmodel
            )
        }
    }
}
