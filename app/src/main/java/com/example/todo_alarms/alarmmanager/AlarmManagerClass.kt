package com.example.todo_alarms.alarmmanager

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.todo_alarms.Data.AlarmEntity

class AlarmManagerClass(
    private val context: Context
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    fun ScheduleAlarm(alarm : AlarmEntity) {
        if(alarm.isActive){
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
            Log.d("AlarmManagerClass", "Alarm scheduled:")
        }
    }
    fun scheduleSnooze(alarmId:Int, snoozeTime: Long, title: String?, todo: String?) {
        val intent =  Intent(context, AlarmReceiver::class.java).apply {
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
        }
    fun cancelAlarm(alarm: Int?) {
        alarm?.let {
            val intent = Intent(context, AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                it,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
        }
    }

}
