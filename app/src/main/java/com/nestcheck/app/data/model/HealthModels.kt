package com.nestcheck.app.data.model

data class StepGoal(
    val currentSteps: Int = 5420,
    val targetGoal: Int = 8000,
    val caloriesBurnt: Int = 238,
    val distanceKm: Float = 4.1f,
    val activeMinutes: Int = 48
)

data class DailyStepRecord(
    val dayName: String,
    val steps: Int,
    val targetGoal: Int = 8000
)

enum class MoodType(val emoji: String, val label: String) {
    GREAT("😄", "Great"),
    GOOD("🙂", "Good"),
    NEUTRAL("😐", "Okay"),
    SAD("😔", "Sad"),
    ANXIOUS("😰", "Anxious")
}

data class MoodEntry(
    val id: String = java.util.UUID.randomUUID().toString(),
    val mood: MoodType,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
