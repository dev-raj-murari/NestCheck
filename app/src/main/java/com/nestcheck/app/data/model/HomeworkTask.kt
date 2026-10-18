package com.nestcheck.app.data.model

data class HomeworkTask(
    val id: String = "",
    val childUid: String = "",
    val title: String = "",
    val subject: String = "General",
    val dueDate: String = "Today",
    val creditReward: Int = 6,
    val isCompleted: Boolean = false,
    val bonusCreditAwarded: Boolean = false,
    val isVerifiedByParent: Boolean = false,
    val notes: String = "",
    val createdAt: Long = 0L,
    val completedAt: Long? = null
)
