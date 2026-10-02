package com.nestcheck.app.data.model

data class ChildProfile(
    val uid: String = "",
    val parentUid: String = "",
    val name: String = "",
    val age: Int = 0,
    val dateOfBirth: String = "",
    val weight: Float = 0f, // kg
    val height: Float = 0f, // cm
    val grade: String = "",
    val avatarUrl: String = "",
    val bmi: Float = 0f,
    val dailyStepGoal: Int = 5000
)
