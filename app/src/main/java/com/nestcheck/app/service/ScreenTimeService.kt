package com.nestcheck.app.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ScreenTimeService : Service() {

    @Inject
    lateinit var tracker: RealtimeScreenTimeTracker

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification("NestCheck Active: Tracking child screen time & credits"))
        tracker.startRealtimeTracking()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        tracker.startRealtimeTracking()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        tracker.pauseRealtimeTracking()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Screen Time Tracking Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Monitors real-time screen usage and enforces credit-based time limits"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun createNotification(content: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("NestCheck Real-Time Guard")
            .setContentText(content)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    companion object {
        const val CHANNEL_ID = "nestcheck_screentime_channel"
        const val NOTIFICATION_ID = 4040
    }
}
