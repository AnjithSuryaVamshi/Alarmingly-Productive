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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                requestExactAlarmPermission()
                Log.d("AlarmManagerClass", "Exact alarm permission not granted.")
                return
            }
        }

        if (alarm.isActive) {
            val intent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra("title", alarm.title)
                putExtra("todo", alarm.todo)
                putExtra("id", alarm.id)
                putExtra("isSnooze", false)
            }

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
            Log.d("AlarmManagerClass", "Alarm scheduled for time: ${alarm.time}")
        }
    }

    fun scheduleSnooze(alarmId: Int, snoozeTime: Long, title: String?, todo: String?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                requestExactAlarmPermission()
                return
            }
        }

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("title", title)
            putExtra("todo", todo)
            putExtra("id", alarmId)
            putExtra("isSnooze", true)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + snoozeTime,
            pendingIntent
        )
        Log.d("AlarmManagerClass", "Snooze scheduled for $snoozeTime ms")
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
            Log.d("AlarmManagerClass", "Alarm canceled for ID: $alarmId")
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun requestExactAlarmPermission() {
        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
        context.startActivity(intent)
    }
}
