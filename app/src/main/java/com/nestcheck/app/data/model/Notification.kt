package com.nestcheck.app.data.model

data class NestCheckNotification(
    val id: String = "",
    val parentUid: String = "",
    val childUid: String = "",
    val type: NotificationType = NotificationType.INFO,
    val title: String = "",
    val message: String = "",
    val timestamp: Long = 0L,
    val isRead: Boolean = false
)

enum class NotificationType {
    INFO, GEOFENCE_EXIT, GEOFENCE_ENTER, SOS, NSFW_ALERT,
    SCREEN_TIME_EXCEEDED, APP_INSTALL_REQUEST, STEP_GOAL_COMPLETE,
    HOMEWORK_COMPLETE, LOW_BATTERY, DEVICE_TAMPER
}
