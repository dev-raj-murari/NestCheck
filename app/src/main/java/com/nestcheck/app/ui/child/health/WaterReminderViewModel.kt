package com.nestcheck.app.ui.child.health

import androidx.lifecycle.ViewModel
import com.nestcheck.app.data.model.WaterReminderConfig
import com.nestcheck.app.data.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class WaterReminderViewModel @Inject constructor(
    private val habitRepository: HabitRepository
) : ViewModel() {

    val config: StateFlow<WaterReminderConfig> = habitRepository.waterConfig

    fun logGlass() {
        habitRepository.logGlassOfWater()
    }
}
