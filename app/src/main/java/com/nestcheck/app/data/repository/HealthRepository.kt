package com.nestcheck.app.data.repository

import com.nestcheck.app.data.model.DailyStepRecord
import com.nestcheck.app.data.model.MoodEntry
import com.nestcheck.app.data.model.MoodType
import com.nestcheck.app.data.model.StepGoal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HealthRepository @Inject constructor() {

    // Step Counter Telemetry
    private val _stepGoal = MutableStateFlow(
        StepGoal(
            currentSteps = 5420,
            targetGoal = 8000,
            caloriesBurnt = 238,
            distanceKm = 4.1f,
            activeMinutes = 48
        )
    )
    val stepGoal: StateFlow<StepGoal> = _stepGoal.asStateFlow()

    private val _weeklySteps = MutableStateFlow(
        listOf(
            DailyStepRecord("Mon", 7200, 8000),
            DailyStepRecord("Tue", 8450, 8000),
            DailyStepRecord("Wed", 6100, 8000),
            DailyStepRecord("Thu", 9300, 8000),
            DailyStepRecord("Fri", 5420, 8000),
            DailyStepRecord("Sat", 4100, 8000),
            DailyStepRecord("Sun", 3800, 8000)
        )
    )
    val weeklySteps: StateFlow<List<DailyStepRecord>> = _weeklySteps.asStateFlow()

    // Mood Log Telemetry
    private val _moodHistory = MutableStateFlow(
        listOf(
            MoodEntry(mood = MoodType.GREAT, note = "Finished science project!", timestamp = System.currentTimeMillis() - 86400000),
            MoodEntry(mood = MoodType.GOOD, note = "Played football with friends", timestamp = System.currentTimeMillis() - 172800000),
            MoodEntry(mood = MoodType.NEUTRAL, note = "Regular school day", timestamp = System.currentTimeMillis() - 259200000)
        )
    )
    val moodHistory: StateFlow<List<MoodEntry>> = _moodHistory.asStateFlow()

    fun updateTargetGoal(newGoal: Int) {
        _stepGoal.value = _stepGoal.value.copy(targetGoal = newGoal)
    }

    fun addSimulatedSteps(stepsToAdd: Int) {
        val current = _stepGoal.value
        val updatedSteps = current.currentSteps + stepsToAdd
        val addedDistance = stepsToAdd * 0.00075f
        val addedCalories = (stepsToAdd * 0.04f).toInt()
        val addedMinutes = (stepsToAdd / 115)

        _stepGoal.value = current.copy(
            currentSteps = updatedSteps,
            distanceKm = (current.distanceKm + addedDistance * 10).toInt() / 10f,
            caloriesBurnt = current.caloriesBurnt + addedCalories,
            activeMinutes = current.activeMinutes + addedMinutes
        )
    }

    fun recordMood(mood: MoodType, note: String) {
        val entry = MoodEntry(mood = mood, note = note, timestamp = System.currentTimeMillis())
        _moodHistory.value = listOf(entry) + _moodHistory.value
    }
}
