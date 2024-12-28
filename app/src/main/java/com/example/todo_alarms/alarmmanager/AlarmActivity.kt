package com.example.todo_alarms.alarmmanager

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.todo_alarms.Data.AppDataBase
import com.example.todo_alarms.repositories.TodoRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AlarmActivity : ComponentActivity() {
    private val repository: TodoRepository by lazy {
        val database = AppDataBase.getDatabase(applicationContext)
        TodoRepository(database.todoDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("AlarmActivity", "AlarmActivity started with intent data: ${intent.extras}")
        val alarmId = intent.getIntExtra("id", 0)
        val title = intent.getStringExtra("title") ?: "Alarm"
        val todo = intent.getStringExtra("todo") ?: "To-do Reminder"
        val action = intent.action
        AlarmReceiver().playAudio(this)
        if (action == "ACTION_SNOOZE") {
            handleSnooze(alarmId, "")
        } else if (action == "ACTION_DISMISS") {
            handleDismiss(alarmId, "")
        }
        setContent {
            FullScreenAlarmUI(
                title = title,
                expectedTodo = todo,
                onSnooze = { userInput ->
                    handleSnooze(alarmId, userInput)
                },
                onDismiss = { userInput ->
                    handleDismiss(alarmId, userInput)
                }
            )
        }
    }
    private fun handleSnooze(alarmId: Int, userInput: String) {
        lifecycleScope.launch {
            val todos = repository.getActiveTodos().value
            if (todos.isNullOrEmpty() && userInput.isBlank()) {
                proceedWithSnooze(alarmId)
            } else if (repository.isTodoPartiallyValid(userInput)) {
                Toast.makeText(this@AlarmActivity, "Task is already completed. Enter a valid To-Do.", Toast.LENGTH_SHORT).show()
            } else if (userInput.isBlank()) {
                Toast.makeText(this@AlarmActivity, "Please enter a valid To-Do.", Toast.LENGTH_SHORT).show()
            } else {
                proceedWithSnooze(alarmId)
            }
        }
    }

    private fun handleDismiss(alarmId: Int, userInput: String) {
        lifecycleScope.launch {
            val todos = repository.getActiveTodos().value
            if (todos.isNullOrEmpty() && userInput.isBlank()) {
                proceedWithDismiss(alarmId)
            } else if (repository.isTodoPartiallyValid(userInput)) {
                Toast.makeText(this@AlarmActivity, "Task is already completed. Enter a valid To-Do.", Toast.LENGTH_SHORT).show()
            } else if (userInput.isBlank()) {
                Toast.makeText(this@AlarmActivity, "Please enter a valid To-Do.", Toast.LENGTH_SHORT).show()
            } else {
                proceedWithDismiss(alarmId)
            }
        }
    }

    private fun proceedWithSnooze(alarmId: Int) {
        AlarmReceiver().stopAudio()
        AlarmManagerClass(this@AlarmActivity).scheduleSnooze(alarmId, 5 * 60 * 1000, null, null)
        Toast.makeText(this@AlarmActivity, "Alarm snoozed!", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun proceedWithDismiss(alarmId: Int) {
        AlarmReceiver().stopAudio()
        AlarmManagerClass(this@AlarmActivity).cancelAlarm(alarmId)
        Toast.makeText(this@AlarmActivity, "Alarm dismissed!", Toast.LENGTH_SHORT).show()
        finish()
    }

}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullScreenAlarmUI(
    title: String,
    expectedTodo: String,
    onSnooze: (String) -> Unit,
    onDismiss: (String) -> Unit
) {
    var userInput by remember { mutableStateOf("") }
    var timeRemaining by remember { mutableStateOf(180) }
    val context = LocalContext.current
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary

    LaunchedEffect(Unit) {
        while (timeRemaining > 0) {
            delay(1000)
            timeRemaining--
        }
        Toast.makeText(context, "Time expired. Alarm stopped.", Toast.LENGTH_SHORT).show()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = primaryColor,
                modifier = Modifier.padding(8.dp)
            )
            Text(
                text = "Enter a valid To-Do to stop the alarm:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(8.dp)
            )
        }

        // Input Field
        OutlinedTextField(
            value = userInput,
            onValueChange = { userInput = it },
            label = { Text("Enter To-Do") },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = primaryColor,
                unfocusedBorderColor = secondaryColor,
                cursorColor = primaryColor
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        // Timer and Actions
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Text(
                text = "Time remaining: $timeRemaining seconds",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(8.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { onSnooze(userInput) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = secondaryColor,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                ) {
                    Text("Snooze")
                }
                Button(
                    onClick = { onDismiss(userInput) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryColor,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                ) {
                    Text("Dismiss")
                }
            }
        }
    }
}
