package com.nestcheck.app.ui.sinan

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nestcheck.app.data.model.LowBatteryConfig
import com.nestcheck.app.data.model.LowBatteryLastPing
import com.nestcheck.app.data.repository.LowBatteryPingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LowBatteryPingViewModel @Inject constructor(
    private val repository: LowBatteryPingRepository
) : ViewModel() {

    val config: StateFlow<LowBatteryConfig> = repository.config
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LowBatteryConfig())

    val pings: StateFlow<List<LowBatteryLastPing>> = repository.pings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _simulatedLevel = mutableStateOf(12f)
    val simulatedLevel: State<Float> = _simulatedLevel

    private val _lastTriggeredPing = mutableStateOf<LowBatteryLastPing?>(null)
    val lastTriggeredPing: State<LowBatteryLastPing?> = _lastTriggeredPing

    fun onBatteryLevelChange(level: Float) {
        _simulatedLevel.value = level
    }

    fun updateThreshold(threshold: Int) {
        viewModelScope.launch {
            repository.updateTriggerThreshold(threshold)
        }
    }

    fun toggleAutoSms(enabled: Boolean) {
        viewModelScope.launch {
            repository.toggleAutoSms(enabled)
        }
    }

    fun toggleHighPowerFix(enabled: Boolean) {
        viewModelScope.launch {
            repository.toggleHighPowerFix(enabled)
        }
    }

    fun triggerPing(levelOverride: Int? = null) {
        val level = levelOverride ?: _simulatedLevel.value.toInt()
        val ping = repository.triggerEmergencyFix(level)
        _lastTriggeredPing.value = ping
    }
}
