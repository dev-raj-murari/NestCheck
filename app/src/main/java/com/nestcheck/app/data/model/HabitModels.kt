package com.nestcheck.app.data.model

// F6: Bedtime Schedule & Curfew Enforcer
data class BedtimeConfig(
    val weekdayBedtimeStart: String = "21:00",
    val weekdayBedtimeEnd: String = "06:30",
    val weekendBedtimeStart: String = "22:00",
    val weekendBedtimeEnd: String = "07:30",
    val isCurfewEnforced: Boolean = true,
    val emergencyWhitelist: List<String> = listOf("Phone", "Emergency SOS", "Calculator", "Clock")
)

// F21: Focus / Study Mode Distraction Blocker
data class FocusSession(
    val targetMinutes: Int = 25,
    val remainingSeconds: Int = 25 * 60,
    val isActive: Boolean = false,
    val isPaused: Boolean = false,
    val sessionType: String = "Pomodoro",
    val rewardCredits: Int = 5
)

// F22: Chore Chart & Completion Workflow
enum class ChoreStatus(val label: String) {
    PENDING("Pending"),
    SUBMITTED("Submitted"),
    APPROVED("Approved")
}

data class ChoreTask(
    val id: String,
    val title: String,
    val rewardCredits: Int = 10,
    val status: ChoreStatus = ChoreStatus.PENDING,
    val submissionNote: String = "",
    val deadline: String = "Today"
)

// F24: One-Tap Device Pause ("Dinnertime Lock")
data class DevicePauseState(
    val isPaused: Boolean = false,
    val reason: String = "Dinnertime / Family Hour",
    val pausedTimestamp: Long = 0L
)

// F25: Age-Based Preset Screen Time Rules
enum class AgeGroupPreset(
    val label: String,
    val ageRange: String,
    val dailyLimitMinutes: Int,
    val bedtime: String,
    val gamingCapMinutes: Int,
    val socialAllowed: Boolean
) {
    YOUNG("Young Kid", "5 - 8 yrs", 60, "20:00", 30, false),
    PRE_TEEN("Pre-Teen", "9 - 12 yrs", 120, "21:00", 45, false),
    TEEN("Teenager", "13+ yrs", 210, "22:30", 90, true)
}

// F26: "Ask for More Time" Request Flow
enum class RequestStatus {
    PENDING,
    APPROVED,
    DECLINED
}

data class TimeExtensionRequest(
    val id: String,
    val childUid: String = "child_1",
    val requestedMinutes: Int = 15,
    val reason: String = "Finishing school assignment",
    val status: RequestStatus = RequestStatus.PENDING,
    val timestamp: Long = System.currentTimeMillis()
)

// F27: App Category-Level Quotas
data class CategoryQuota(
    val category: AppCategory,
    val dailyLimitMinutes: Int,
    val usedMinutes: Int
)

// F28: Weekly Challenges & Gamified Habit Streaks
data class HabitStreak(
    val id: String,
    val title: String,
    val currentStreakDays: Int,
    val targetDays: Int = 7,
    val badgeName: String,
    val iconEmoji: String,
    val bonusCredits: Int = 20
)

// F40: Water & Healthy Sleep Routine Reminders
data class WaterReminderConfig(
    val glassesDrank: Int = 5,
    val dailyGoalGlasses: Int = 8,
    val reminderIntervalMins: Int = 60,
    val isHydrationAlertEnabled: Boolean = true,
    val windDownMinsBeforeBedtime: Int = 30,
    val isSleepWindDownEnabled: Boolean = true
)
