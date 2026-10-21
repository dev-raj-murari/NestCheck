package com.nestcheck.app.ui.child.health

import androidx.lifecycle.ViewModel
import com.nestcheck.app.data.local.NestCheckPreferences
import com.nestcheck.app.data.model.CreditTransactionType
import com.nestcheck.app.data.model.WaterReminderConfig
import com.nestcheck.app.data.repository.CreditScreenTimeRepository
import com.nestcheck.app.data.repository.HabitRepository
import com.nestcheck.app.data.sync.RealtimeFamilySyncManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class WaterReminderViewModel @Inject constructor(
    private val habitRepository: HabitRepository,
    private val syncManager: RealtimeFamilySyncManager,
    private val creditRepository: CreditScreenTimeRepository,
    private val preferences: NestCheckPreferences
) : ViewModel() {

    val config: StateFlow<WaterReminderConfig> = habitRepository.waterConfig

    private val _rewardMessage = MutableStateFlow<String?>(null)
    val rewardMessage: StateFlow<String?> = _rewardMessage.asStateFlow()

    fun logGlass() {
        val current = config.value
        val nextGlasses = current.glassesDrank + 1
        habitRepository.logGlassOfWater()

        // 1. Award 2 credits for each completed hydration step
        creditRepository.addCredits(
            amount = 2,
            type = CreditTransactionType.HYDRATION_STEP,
            title = "Hydration Step: Glass #$nextGlasses"
        )
        syncManager.logChildActivity("hydration_step_logged", "Child drank water glass #$nextGlasses (+2 credits)")

        // 2. Award an additional 10 credits when the full daily goal is completed (awarded once per daily goal)
        if (nextGlasses >= current.dailyGoalGlasses && !preferences.hydrationGoalBonusAwarded) {
            preferences.hydrationGoalBonusAwarded = true
            creditRepository.addCredits(
                amount = 10,
                type = CreditTransactionType.BONUS_AWARDED,
                title = "Hydration Daily Goal Completed (+10 Bonus)"
            )
            syncManager.logChildActivity("hydration_goal_completed", "Daily goal achieved (${current.dailyGoalGlasses} glasses)! +10 Bonus Credits awarded")
            _rewardMessage.value = "★ Daily Hydration Goal Completed! +2 Step and +10 Bonus Credits Added!"
        } else {
            _rewardMessage.value = "+2 Credits! Keep drinking water! 💧 (Glass $nextGlasses/${current.dailyGoalGlasses})"
        }
    }

    fun clearRewardMessage() {
        _rewardMessage.value = null
    }
}
