package com.nestcheck.app.data.repository

import com.nestcheck.app.data.model.NestCheckNotification
import com.nestcheck.app.data.model.NotificationType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor() {

    private val _notifications = MutableStateFlow(
        listOf(
            NestCheckNotification(
                id = "notif_1",
                type = NotificationType.SOS,
                title = "SOS Emergency Triggered",
                message = "Alex pressed SOS button. Live GPS fix broadcasted to parents.",
                timestamp = System.currentTimeMillis() - 120000,
                isRead = false
            ),
            NestCheckNotification(
                id = "notif_2",
                type = NotificationType.LOW_BATTERY,
                title = "Low Battery Alert (14%)",
                message = "Alex's device reached critical battery. Last known location saved.",
                timestamp = System.currentTimeMillis() - 900000,
                isRead = false
            ),
            NestCheckNotification(
                id = "notif_3",
                type = NotificationType.GEOFENCE_ENTER,
                title = "Arrived at School Safe Zone",
                message = "Alex entered Oakridge School zone at 08:15 AM.",
                timestamp = System.currentTimeMillis() - 3600000,
                isRead = true
            ),
            NestCheckNotification(
                id = "notif_4",
                type = NotificationType.SCREEN_TIME_EXCEEDED,
                title = "Daily Screen Time Approaching Limit",
                message = "Alex has 15 minutes of screen time remaining today.",
                timestamp = System.currentTimeMillis() - 7200000,
                isRead = true
            ),
            NestCheckNotification(
                id = "notif_5",
                type = NotificationType.STEP_GOAL_COMPLETE,
                title = "Daily Step Goal Reached!",
                message = "Alex completed 8,000 steps today. 50 bonus credits unlocked.",
                timestamp = System.currentTimeMillis() - 14400000,
                isRead = true
            )
        )
    )
    val notifications: StateFlow<List<NestCheckNotification>> = _notifications.asStateFlow()

    fun markAsRead(id: String) {
        _notifications.value = _notifications.value.map {
            if (it.id == id) it.copy(isRead = true) else it
        }
    }

    fun markAllAsRead() {
        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
    }

    fun dismissNotification(id: String) {
        _notifications.value = _notifications.value.filter { it.id != id }
    }

    fun clearAll() {
        _notifications.value = emptyList()
    }

    fun addNotification(type: NotificationType, title: String, message: String) {
        val newNotif = NestCheckNotification(
            id = "notif_${System.currentTimeMillis()}",
            type = type,
            title = title,
            message = message,
            timestamp = System.currentTimeMillis(),
            isRead = false
        )
        _notifications.value = listOf(newNotif) + _notifications.value
    }
}
