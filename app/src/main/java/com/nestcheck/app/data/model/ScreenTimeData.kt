package com.nestcheck.app.data.model

enum class AppCategory(val displayName: String) {
    GAMING("Games"),
    SOCIAL("Social"),
    EDUCATION("Education"),
    ENTERTAINMENT("Entertainment"),
    UTILITY("Utility")
}

enum class AppControlStatus(val displayName: String) {
    ALLOWED("Allowed"),
    TIME_LIMITED("Time-Limited"),
    BLOCKED("Blocked")
}

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

data class AppUsageDetail(
    val packageName: String,
    val appName: String,
    val category: AppCategory,
    val minutesUsed: Int,
    val status: AppControlStatus = AppControlStatus.ALLOWED,
    val dailyLimitMinutes: Int = -1
)

data class DaySchedule(
    val dayName: String,
    val limitMinutes: Int
)

data class DailyUsagePoint(
    val day: String,
    val minutes: Int
)
