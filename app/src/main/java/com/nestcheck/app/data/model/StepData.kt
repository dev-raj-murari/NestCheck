package com.nestcheck.app.data.model

data class StepData(
    val childUid: String = "",
    val date: String = "",
    val steps: Int = 0,
    val goal: Int = 5000,
    val goalCompleted: Boolean = false,
    val streakDays: Int = 0
)
