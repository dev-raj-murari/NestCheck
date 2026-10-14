package com.nestcheck.app.ui.child.steps

import androidx.lifecycle.ViewModel
import com.nestcheck.app.data.model.DailyStepRecord
import com.nestcheck.app.data.model.StepGoal
import com.nestcheck.app.data.repository.HealthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class StepCounterViewModel @Inject constructor(
    private val healthRepository: HealthRepository
) : ViewModel() {

    val stepGoal: StateFlow<StepGoal> = healthRepository.stepGoal
    val weeklySteps: StateFlow<List<DailyStepRecord>> = healthRepository.weeklySteps

    fun updateGoal(newGoal: Int) {
        healthRepository.updateTargetGoal(newGoal)
    }

    fun addWalkSteps(steps: Int = 500) {
        healthRepository.addSimulatedSteps(steps)
    }
}
