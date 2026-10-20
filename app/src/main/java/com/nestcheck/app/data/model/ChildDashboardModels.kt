package com.nestcheck.app.data.model

import java.util.UUID

enum class TaskCategory(val label: String) {
    STUDY("Academic & Study"),
    CHORE("Household Chore"),
    WELLBEING("Health & Wellbeing"),
    READING("Reading Goal")
}

enum class TaskApprovalStatus(val label: String) {
    NOT_COMPLETED("Not Completed"),
    PENDING_PARENT_APPROVAL("Pending Parent Approval"),
    APPROVED_AND_CREDITED("Approved & Credited")
}

data class PendingTaskItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val category: TaskCategory,
    val creditsReward: Int,
    val isCompleted: Boolean = false,
    val approvalStatus: TaskApprovalStatus = if (isCompleted) TaskApprovalStatus.APPROVED_AND_CREDITED else TaskApprovalStatus.NOT_COMPLETED,
    val dueDate: String = "Today, 6:00 PM"
) {
    val screenTimeBonusMinutes: Int
        get() = creditsReward / 2
}

data class UpcomingExam(
    val id: String = UUID.randomUUID().toString(),
    val subject: String,
    val examTitle: String,
    val examDate: String,
    val daysRemaining: Int,
    val syllabusTopics: List<String>,
    val targetGrade: String = "A+"
)

data class AppUsageInfo(
    val packageName: String,
    val appName: String,
    val category: String,
    val timeSpentMinutes: Int,
    val limitMinutes: Int,
    val isBlocked: Boolean = false
) {
    val usagePercentage: Float
        get() = if (limitMinutes > 0) (timeSpentMinutes.toFloat() / limitMinutes).coerceIn(0f, 1f) else 0f
}

enum class CalendarEventType(val label: String, val badgeColorHex: Long) {
    EXAM("Exam", 0xFFFF5252),
    HOMEWORK("Homework", 0xFFFFAB00),
    STUDY_SESSION("Study Block", 0xFF00E5FF),
    BEDTIME("Bedtime Curfew", 0xFF94A3B8),
    REWARD_TIME("Free Play", 0xFF00E676)
}

data class CalendarEventItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val eventType: CalendarEventType,
    val timeRange: String,
    val dayOfWeek: String,
    val dayOfMonth: Int,
    val isExam: Boolean = eventType == CalendarEventType.EXAM
)

data class GeofenceBreachSimResult(
    val id: String = UUID.randomUUID().toString(),
    val zoneName: String = "Oakridge High School",
    val breachType: String = "EXIT (Perimeter Crossing)",
    val latitude: Double = 19.1075,
    val longitude: Double = 72.8368,
    val timestamp: Long = System.currentTimeMillis(),
    val isParentNotified: Boolean = true,
    val alertMessage: String = "Geofence Exit Detected: Child crossed Oakridge High School boundary"
)
