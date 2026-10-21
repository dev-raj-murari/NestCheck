package com.nestcheck.app.core

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.nestcheck.app.ParentMainActivity
import com.nestcheck.app.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NestCheckNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()

            // 1. SOS Critical Channel
            val sosChannel = NotificationChannel(
                CHANNEL_SOS,
                "Critical SOS Safety Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "High-priority alarms when child triggers SOS"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500, 200, 500)
                setSound(alarmSound, audioAttributes)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }

            // 2. Lock & Controls Channel
            val controlsChannel = NotificationChannel(
                CHANNEL_CONTROLS,
                "Device Lock & Bedtime Rules",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifies when device is remotely locked or curfew begins"
                enableVibration(true)
            }

            // 3. Anti-Tamper Security Channel
            val tamperChannel = NotificationChannel(
                CHANNEL_TAMPER,
                "Anti-Tamper & Security Guards",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifies when clock changes or offline airplane mode is detected"
                enableVibration(true)
            }

            // 4. Geofence Perimeter Safety Channel
            val geofenceChannel = NotificationChannel(
                CHANNEL_GEOFENCE,
                "Geofence Safety Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifies when child enters or leaves safe zones like MPSTME or Home"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 300, 150, 300)
            }

            notificationManager.createNotificationChannel(sosChannel)
            notificationManager.createNotificationChannel(controlsChannel)
            notificationManager.createNotificationChannel(tamperChannel)
            notificationManager.createNotificationChannel(geofenceChannel)
        }
    }

    fun showSosAlert(childName: String, locationText: String, lat: Double, lng: Double, parentPhone: String = "+91 98765 43210") {
        val openAppIntent = Intent(context, ParentMainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val appPendingIntent = PendingIntent.getActivity(
            context,
            1001,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Real Dial Parent Emergency Contact Intent (NOT 112)
        val phoneToCall = if (parentPhone.isNotBlank()) parentPhone else "+91 98765 43210"
        val dialIntent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneToCall")
        }
        val dialPendingIntent = PendingIntent.getActivity(
            context,
            1002,
            dialIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Real Google Maps Navigation Intent
        val mapIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("geo:$lat,$lng?q=$lat,$lng($childName+Emergency)")
        }
        val mapPendingIntent = PendingIntent.getActivity(
            context,
            1003,
            mapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_SOS)
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentTitle("🚨 EMERGENCY SOS: $childName")
            .setContentText("Emergency triggered! Last seen: $locationText")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("🚨 EMERGENCY SOS: $childName activated emergency broadcast!\nLocation: $locationText\nEmergency Contact: $phoneToCall\nImmediate parental attention required.")
            )
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setColor(0xFFD93025.toInt()) // Google Red
            .setContentIntent(appPendingIntent)
            .addAction(android.R.drawable.ic_menu_call, "Call Parent ($phoneToCall)", dialPendingIntent)
            .addAction(android.R.drawable.ic_dialog_map, "Open in Maps", mapPendingIntent)
            .setAutoCancel(false)
            .setOngoing(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID_SOS, notification)
    }

    fun showGeofenceAlert(childName: String, zoneName: String, isExit: Boolean, address: String, lat: Double, lng: Double) {
        val openAppIntent = Intent(context, ParentMainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val appPendingIntent = PendingIntent.getActivity(
            context,
            1004,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val mapIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("geo:$lat,$lng?q=$lat,$lng($childName+Geofence+Breach)")
        }
        val mapPendingIntent = PendingIntent.getActivity(
            context,
            1005,
            mapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val actionText = if (isExit) "EXITED SAFE ZONE" else "ENTERED RESTRICTED AREA"
        val notification = NotificationCompat.Builder(context, CHANNEL_GEOFENCE)
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentTitle("⚠️ GEOFENCE ALERT: $childName $actionText")
            .setContentText("$childName has crossed the $zoneName boundary! Location: $address")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("⚠️ GEOFENCE ALERT: $childName $actionText\nBoundary: $zoneName\nLocation: $address\nLive GPS telemetry tracking active.")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setColor(0xFFF9AB00.toInt()) // Google Amber
            .setContentIntent(appPendingIntent)
            .addAction(android.R.drawable.ic_dialog_map, "View on Map", mapPendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID_GEOFENCE, notification)
    }

    fun showDeviceLockNotification(childName: String, isLocked: Boolean) {
        val title = if (isLocked) "🔒 Device Locked: $childName" else "🔓 Device Unlocked: $childName"
        val message = if (isLocked) {
            "Parent remote lockout is active. Applications and Play Store purchases are restricted."
        } else {
            "Device access resumed. Standard time limits apply."
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_CONTROLS)
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID_CONTROLS, notification)
    }

    fun showTamperAlert(reason: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_TAMPER)
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentTitle("⚠️ Security Tamper Alert")
            .setContentText(reason)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setColor(0xFFF9AB00.toInt()) // Google Amber
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID_TAMPER, notification)
    }

    fun showSafeCheckInNotification(childName: String, locationLabel: String, time: String) {
        val openAppIntent = Intent(context, ParentMainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val appPendingIntent = PendingIntent.getActivity(
            context,
            1006,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_CONTROLS)
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentTitle("SAFE CHECK-IN RECEIVED")
            .setContentText("$childName has checked in safely at $locationLabel ($time).")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("SAFE CHECK-IN RECEIVED\n$childName has checked in safely.\nLocation: $locationLabel\nTime: $time")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setColor(0xFF1E8E3E.toInt())
            .setContentIntent(appPendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID_CHECKIN, notification)
    }

    fun showNotification(title: String, message: String) {
        val openAppIntent = Intent(context, ParentMainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val appPendingIntent = PendingIntent.getActivity(
            context,
            1007,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_CONTROLS)
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(appPendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    companion object {
        const val CHANNEL_SOS = "critical_sos_alerts"
        const val CHANNEL_CONTROLS = "parental_device_locks"
        const val CHANNEL_TAMPER = "tamper_security_alerts"
        const val CHANNEL_GEOFENCE = "geofence_safety_alerts"

        const val NOTIFICATION_ID_SOS = 9001
        const val NOTIFICATION_ID_CONTROLS = 9002
        const val NOTIFICATION_ID_TAMPER = 9003
        const val NOTIFICATION_ID_GEOFENCE = 9004
        const val NOTIFICATION_ID_CHECKIN = 9005
    }
}
