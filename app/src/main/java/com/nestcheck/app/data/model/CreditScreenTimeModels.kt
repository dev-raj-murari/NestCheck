package com.nestcheck.app.data.model

import java.util.UUID

/**
 * Core Credit-to-ScreenTime Conversion System
 * Standard Rule: 2 Credits = 1 Minute of Screen Time
 */
data class CreditConversionRule(
    val creditsPerMinute: Int = 2,
    val minutesPerCredit: Double = 0.5,
    val description: String = "2 Credits = 1 Minute of Screen Time"
)

enum class CreditTransactionType(val displayName: String, val isPositive: Boolean) {
    TASK_COMPLETED("Task / Chore Completed", true),
    HOMEWORK_DONE("Homework Verified", true),
    EXAM_STUDY_BONUS("Exam Preparation Bonus", true),
    PARENT_GRANTED("Granted by Parent", true),
    SCREEN_TIME_BURN("Screen Time Consumed", false),
    MANUAL_REDEEM("Redeemed for Screen Time", false),
    PENALTY_DEDUCTION("Safety Rule Penalty", false)
}

data class CreditLedgerRecord(
    val id: String = UUID.randomUUID().toString(),
    val amount: Int = 0,
    val transactionType: CreditTransactionType = CreditTransactionType.TASK_COMPLETED,
    val title: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val equivalentMinutes: Int = amount / 2
)

data class CreditScreenTimeWallet(
    val totalCredits: Int = 180,
    val activeRemainingSeconds: Long = (180 / 2) * 60L, // 90 minutes
    val isTrackingActive: Boolean = true,
    val conversionRateCreditsPerMin: Int = 2,
    val todayEarnedCredits: Int = 60,
    val todayBurnedCredits: Int = 40,
    val lastSyncTimestamp: Long = System.currentTimeMillis()
) {
    val remainingMinutes: Int
        get() = (activeRemainingSeconds / 60).toInt()

    val equivalentScreenTimeFormatted: String
        get() = "${remainingMinutes / 60}h ${remainingMinutes % 60}m"
}
