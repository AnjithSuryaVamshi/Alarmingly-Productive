package com.example.todo_alarms.alarmmanager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.todo_alarms.R

class AlarmReceiver : BroadcastReceiver() {
    companion object {
        private var mediaPlayer: MediaPlayer? = null
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        var action = intent.action
        val alarmId = intent.getIntExtra("id", 0)
        val title = intent.getStringExtra("title") ?: "Alarm"
        val todo = intent.getStringExtra("todo") ?: "To-do Reminder"

        Log.d("AlarmReceiver", "onReceive triggered: action=$action, id=$alarmId, title=$title, todo=$todo")

        when (action) {
            "ACTION_DISMISS", "ACTION_SNOOZE" -> {
                val activityIntent = Intent(context, AlarmActivity::class.java).apply {
                    putExtra("id", alarmId)
                    putExtra("title", title)C:\Users\anjit\AndroidStudioProjects\TodoAlarms\app
                    putExtra("todo", todo)
                }
                activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(activityIntent)
                stopAudio()
            }
            else -> {
                playAudio(context)
                showFullScreenNotification(context, alarmId, title, todo)
            }
        }

    }

    fun playAudio(context: Context) {
        stopAudio()
        mediaPlayer = MediaPlayer.create(context, R.raw.gksong).apply {
            isLooping = true
            start()
            Log.d("AlarmReceiver", "Audio playback started")
        }
    }

    fun stopAudio() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
                Log.d("AlarmReceiver", "Audio playback stopped")
            }
            it.release()
        }
        mediaPlayer = null
    }

    private fun showFullScreenNotification(
        context: Context,
        alarmId: Int,
        title: String,
        todo: String
    ) {
        createNotificationChannel(context)

        val snoozeIntent = Intent(context, AlarmActivity::class.java).apply {
            action = "ACTION_SNOOZE"
            putExtra("id", alarmId)
            putExtra("title", title)
            putExtra("todo", todo)
        }


        val dismissIntent = Intent(context, AlarmActivity::class.java).apply {
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
            .setContentTitle(title)
            .setContentText(todo)
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
