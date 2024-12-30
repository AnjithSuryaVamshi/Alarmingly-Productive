package com.example.todo_alarms.alarmmanager

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.todo_alarms.Data.AlarmEntity

class AlarmManagerClass(
    private val context: Context
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleAlarm(alarm: AlarmEntity) {
        // Check for exact alarm permission on Android 12+ devices
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                requestExactAlarmPermission()
                Log.w("AlarmManagerClass", "Exact alarm permission not granted.")
                return
            }
        }

        if (alarm.isActive) {
            val intent = createAlarmIntent(alarm, isSnooze = false)

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                alarm.id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                alarm.time,
                pendingIntent
            )
            Log.d("AlarmManagerClass", "Alarm scheduled: ID=${alarm.id}, Time=${alarm.time}")
        } else {
            Log.w("AlarmManagerClass", "Attempted to schedule inactive alarm: ID=${alarm.id}")
        }
    }

    fun scheduleSnooze(alarmId: Int, delayMillis: Long, title: String?, todo: String?) {
        val snoozeIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = "ACTION_ALARM"
            putExtra("id", alarmId)
            putExtra("title", title ?: "Alarm")
            putExtra("todo", todo ?: "To-do Reminder")
            putExtra("isSnooze", true) // Indicates this is a snooze alarm
        }

        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerAtMillis = System.currentTimeMillis() + delayMillis
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            snoozePendingIntent
        )

        Log.d("AlarmManagerClass", "Snooze scheduled: ID=$alarmId, TriggerAt=$triggerAtMillis")
    }

    fun cancelAlarm(alarmId: Int?) {
        alarmId?.let {
            val intent = Intent(context, AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                it,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
            Log.d("AlarmManagerClass", "Alarm canceled: ID=$alarmId")
        } ?: run {
            Log.w("AlarmManagerClass", "Attempted to cancel alarm with null ID")
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun requestExactAlarmPermission() {
        try {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e("AlarmManagerClass", "Failed to request exact alarm permission", e)
        }
    }

    private fun createAlarmIntent(alarm: AlarmEntity, isSnooze: Boolean): Intent {
        return Intent(context, AlarmReceiver::class.java).apply {
            putExtra("title", alarm.title)
            putExtra("todo", alarm.todo)
            putExtra("id", alarm.id)
            putExtra("isSnooze", isSnooze)
        }
    }
}