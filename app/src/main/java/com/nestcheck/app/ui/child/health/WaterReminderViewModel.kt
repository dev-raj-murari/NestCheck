package com.nestcheck.app.ui.child.health

import androidx.lifecycle.ViewModel
import com.nestcheck.app.data.model.WaterReminderConfig
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
    private val syncManager: RealtimeFamilySyncManager
) : ViewModel() {

    val config: StateFlow<WaterReminderConfig> = habitRepository.waterConfig

    private val _rewardMessage = MutableStateFlow<String?>(null)
    val rewardMessage: StateFlow<String?> = _rewardMessage.asStateFlow()

    fun logGlass() {
        val current = config.value
        val nextGlasses = current.glassesDrank + 1
        habitRepository.logGlassOfWater()

        if (nextGlasses >= current.dailyGoalGlasses) {
            syncManager.addCredits(10)
            _rewardMessage.value = "★ Hydration Goal Complete! +10 Credits Added to Your Wallet!"
        } else {
            syncManager.addCredits(2)
            _rewardMessage.value = "+2 Credits! Keep drinking water! 💧"
        }
    }

    fun clearRewardMessage() {
        _rewardMessage.value = null
    }
}
