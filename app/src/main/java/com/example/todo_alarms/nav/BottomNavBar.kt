package com.example.todo_alarms.nav

import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.ChecklistRtl
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController



sealed class Screen(val route: String, val label: String) {
    object Todo : Screen(Routes.todo_screen, "To-do")
    object Alarm : Screen(Routes.alarm_screen, "Alarm")
}

@Composable
fun BottomNavBar(navController: NavController) {

    val items = remember { listOf(Screen.Todo, Screen.Alarm) }

    NavigationBar(
        modifier = Modifier.height(56.dp)
    ) {
        items.forEach { screen ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = getIconForScreen(screen),
                        contentDescription = screen.label
                    )
                },
                label = { Text(screen.label) },
                selected = navController.currentDestination?.route == screen.route,
                onClick = {
                    navController.navigate(screen.route) {

                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
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
