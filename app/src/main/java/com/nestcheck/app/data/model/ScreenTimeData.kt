package com.nestcheck.app.data.model

data class ScreenTimeData(
    val childUid: String = "",
    val date: String = "",
    val totalMinutes: Int = 0,
    val appUsage: Map<String, Int> = emptyMap(), // packageName -> minutes
    val dailyLimit: Int = 120, // minutes
    val remainingMinutes: Int = 120
)

data class AppControlEntry(
    val packageName: String = "",
    val appName: String = "",
    val isBlocked: Boolean = false,
    val dailyLimitMinutes: Int = -1, // -1 = unlimited
    val usedMinutes: Int = 0
)
