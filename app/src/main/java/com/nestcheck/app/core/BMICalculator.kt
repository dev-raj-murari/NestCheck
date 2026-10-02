package com.nestcheck.app.core

object BMICalculator {
    fun calculateBMI(weightKg: Float, heightCm: Float): Float {
        if (heightCm <= 0f) return 0f
        val heightM = heightCm / 100f
        return weightKg / (heightM * heightM)
    }

    fun calculateStepGoal(age: Int, bmi: Float): Int {
        // Simplified logic
        return when {
            age < 6 -> 4000
            age in 6..12 && bmi > 25 -> 7000
            age in 6..12 -> 6000
            age > 12 && bmi > 25 -> 8000
            else -> 5000
        }
    }
}
