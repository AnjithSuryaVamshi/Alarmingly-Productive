package com.example.todo_alarms.alarmmanager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.todo_alarms.R

class AlarmReceiver :  BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        val  alarmId = intent?.getIntExtra("id", 0)
        val title = intent?.getStringExtra("title")
        val todo = intent?.getStringExtra("todo")
        val isSnooze = intent?.getBooleanExtra("isSnooze", false)
        Log.d("AlarmReceiver", "onReceive triggered: action=$action, id=$alarmId, title=$title, todo=$todo")
        when(action){
            "ACTION_DISMISS"->{
                if (context != null) {
                    AlarmManagerClass(context).cancelAlarm(alarmId)
                }
            }
            "ACTION_SNOOZE"->{
                if (context != null) {
                    AlarmManagerClass(context).scheduleSnooze(alarmId!!, 5 * 60 * 1000, title, todo)
                }

            }
            else->{
                showFullScreenNotification(context!!,alarmId!!, title!!, todo!!)
            }
        }
    }

    private fun showFullScreenNotification(
        context: Context,
        alarmId: Int,
        title: String,
        todo: String
    ) {
        createNotificationChannel(context)
        Log.d("AlarmReceiver", "Building notification for alarmId=$alarmId, title=$title, todo=$todo")
        val snoozeIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = "ACTION_SNOOZE"
            putExtra("id", alarmId)
            putExtra("title", title)
            putExtra("todo", todo)
        }

        val dismissIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = "ACTION_DISMISS"
            putExtra("id", alarmId)
        }

        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val dismissPendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId,
            dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val fullScreenIntent = Intent(context, AlarmActivity::class.java).apply {
            putExtra("id", alarmId)
            putExtra("title", title)
            putExtra("todo", todo)
        }

        val fullScreenPendingIntent = PendingIntent.getActivity(
            context,
            alarmId,
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, "alarm_channel")
            .setSmallIcon(R.drawable.ic_alarm)
            .setContentTitle(title ?: "Alarm")
            .setContentText(todo ?: "To-do Reminder")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .addAction(R.drawable.ic_snooze, "Snooze", snoozePendingIntent)
            .addAction(R.drawable.ic_dismiss, "Dismiss", dismissPendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(alarmId, notification)
    }

    private fun createNotificationChannel(context: Context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Log.d("AlarmReceiver", "Creating notification channel")
            val channel = NotificationChannel(
                "alarm_channel",
                "Alarm Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for alarm notifications"
            }

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}