package com.example.todo_alarms.nav

import android.view.WindowInsets
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.ChecklistRtl
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

sealed class Screen(val route: String, val label: String) {
    object Todo : Screen(Routes.todo_screen, "To-do")
    object Alarm : Screen(Routes.alarm_screen, "Alarm")
}

@Composable
fun BottomNavBar(navController: NavController) {
    val items = remember { listOf(Screen.Todo, Screen.Alarm) }

    
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .height(56.dp),
        tonalElevation = 4.dp,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        items.forEach { screen ->
            val isSelected = currentRoute == screen.route
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = getIconForScreen(screen),
                        contentDescription = screen.label
                    )
                },
                label = { Text(screen.label) },
                selected = isSelected,
                onClick = {
                    if (!isSelected) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}

private fun getIconForScreen(screen: Screen): ImageVector {
    return when (screen) {
        Screen.Todo -> Icons.Default.ChecklistRtl
        Screen.Alarm -> Icons.Default.Alarm
    }
}
