package com.nestcheck.app.data.model

data class ChildProfile(
    val uid: String = "child_1",
    val parentUid: String = "parent_1",
    val name: String = "Aarav",
    val age: Int = 10,
    val gender: String = "Boy",
    val weight: Float = 34.0f, // in kg
    val height: Float = 140.0f, // in cm
    val grade: String = "Grade 5",
    val dateOfBirth: String = "2016-05-15",
    val avatarUrl: String = "",
    val bmi: Float = 17.3f,
    val dailyStepGoal: Int = 6000
)
