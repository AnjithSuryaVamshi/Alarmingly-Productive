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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.todo_alarms.Data.AppDataBase
import com.example.todo_alarms.repositories.TodoRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
        when (action) {
            "ACTION_SNOOZE" -> handleSnooze(alarmId, "")
            "ACTION_DISMISS" -> handleDismiss(alarmId, "")
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
                },
                onTimeExpired = {
                    lifecycleScope.launch {
                        AlarmReceiver().stopAudio()
                        Toast.makeText(
                            this@AlarmActivity,
                            "Alarm dismissed due to timeout.",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                }
            )
        }
    }
        private fun handleSnooze(alarmId: Int, userInput: String) {
        lifecycleScope.launch {
            val todos = repository.getActiveTodos()
            if (todos.isEmpty() && userInput.isBlank()) {
                proceedWithSnooze(alarmId)
            } else if (!repository.isTodoPartiallyValid(userInput)) {
                Toast.makeText(this@AlarmActivity, "Enter a valid To-Do.", Toast.LENGTH_SHORT).show()
            } else if (userInput.isBlank()) {
                Toast.makeText(this@AlarmActivity, "Please enter a valid To-Do.", Toast.LENGTH_SHORT).show()
            } else {
                proceedWithSnooze(alarmId)
            }
        }
    }

    private fun handleDismiss(alarmId: Int, userInput: String) {
        lifecycleScope.launch {
            val todos = repository.getActiveTodos()
            if (todos.isEmpty() && userInput.isBlank()) {
                proceedWithDismiss(alarmId)
            } else if (!repository.isTodoPartiallyValid(userInput)) {
                Toast.makeText(this@AlarmActivity, "Enter a valid To-Do.", Toast.LENGTH_SHORT).show()
            } else if (userInput.isBlank()) {
                Toast.makeText(this@AlarmActivity, "Please enter a valid To-Do.", Toast.LENGTH_SHORT).show()
            } else {
                proceedWithDismiss(alarmId)
            }
        }
    }

    private fun proceedWithSnooze(alarmId: Int) {
        AlarmReceiver().stopAudio()
        AlarmManagerClass(this).scheduleSnooze(
            alarmId = alarmId,
            delayMillis = 1 * 60 * 1000,
            title = "Snoozed Alarm",
            todo = null
        )
        Toast.makeText(this, "Alarm snoozed for 5 minutes!", Toast.LENGTH_SHORT).show()
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
    onDismiss: (String) -> Unit,
    onTimeExpired: () -> Unit
) {
    var userInput by remember { mutableStateOf("") }
    var timeRemaining by remember { mutableStateOf(180) }
    val context = LocalContext.current
    var currentTime by remember { mutableStateOf(getFormattedTime()) }
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = getFormattedTime()
            delay(1000L)
        }
    }



    LaunchedEffect(Unit) {
        while (timeRemaining > 0) {
            delay(1000)
            timeRemaining--
        }
        Toast.makeText(context, "Time expired. Alarm stopped.", Toast.LENGTH_SHORT).show()
        onTimeExpired()
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onBackground
        )


        Text(
            text = currentTime,
            style = MaterialTheme.typography.displayLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onBackground
        )


        Text(
            text = "ENTER A VALID TO-DO",
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onBackground
        )


        OutlinedTextField(
            value = userInput,
            onValueChange = { userInput = it },
            placeholder = { Text("Enter TO-DO Here") },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                unfocusedBorderColor = MaterialTheme.colorScheme.onBackground,
                cursorColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .background(MaterialTheme.colorScheme.surface)
        )


        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "TIME REMAINING",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "$timeRemaining",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "SECONDS",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
        }


        Row(
            modifier = Modifier.fillMaxWidth(0.8f),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { onSnooze(userInput) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                Text("SNOOZE", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { onDismiss(userInput) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                Text("DISMISS", fontWeight = FontWeight.Bold)
            }
        }
    }
}

fun getFormattedTime(): String {
    val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return dateFormat.format(Date())
}
