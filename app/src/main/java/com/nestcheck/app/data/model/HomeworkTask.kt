package com.nestcheck.app.data.model

data class HomeworkTask(
    val id: String = "",
    val childUid: String = "",
    val title: String = "",
    val isCompleted: Boolean = false,
    val bonusCreditAwarded: Boolean = false,
    val createdAt: Long = 0L,
    val completedAt: Long? = null
)
