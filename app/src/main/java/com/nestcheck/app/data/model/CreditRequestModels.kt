package com.nestcheck.app.data.model

import java.util.UUID

enum class CreditRequestStatus(val label: String, val badgeColorHex: Long) {
    PENDING("Pending Parent Review", 0xFFFFAB00),
    APPROVED("Approved & Credited", 0xFF00E676),
    DECLINED("Declined by Parent", 0xFFFF5252)
}

enum class CreditReasonCategory(val title: String) {
    EXTRA_CHORE("Extra Household Chore"),
    HOMEWORK_EXCELLENCE("Homework & Assignment"),
    EXAM_PREPARATION("School Exam Preparation"),
    READING_GOAL("Reading Books & Literature"),
    GOOD_BEHAVIOR("Good Behavior & Kindness"),
    OTHER("Special Request")
}

data class CreditRequest(
    val id: String = UUID.randomUUID().toString(),
    val childName: String = "Alex",
    val taskTitle: String,
    val creditsRequested: Int, // e.g. 40 credits
    val reasonCategory: CreditReasonCategory = CreditReasonCategory.EXTRA_CHORE,
    val justificationNote: String = "",
    val status: CreditRequestStatus = CreditRequestStatus.PENDING,
    val requestTimestamp: Long = System.currentTimeMillis(),
    val responseNote: String = ""
) {
    val screenTimeMinutesEquivalent: Int
        get() = creditsRequested / 2
}
