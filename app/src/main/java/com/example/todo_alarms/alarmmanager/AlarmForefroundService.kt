package com.example.todo_alarms.alarmmanager
import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder

class AlarmForegroundService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notificationId = intent?.getIntExtra("notificationId", 0) ?: 0
        val notification = intent?.getParcelableExtra<Notification>("notification")

        if (notification != null) {
            startForeground(notificationId, notification)
        }

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
