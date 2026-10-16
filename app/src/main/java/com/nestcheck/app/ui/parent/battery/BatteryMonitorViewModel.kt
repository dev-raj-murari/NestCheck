package com.nestcheck.app.ui.parent.battery

import androidx.lifecycle.ViewModel
import com.nestcheck.app.data.repository.BatteryRepository
import com.nestcheck.app.data.repository.BatteryTelemetry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class BatteryMonitorViewModel @Inject constructor(
    private val batteryRepository: BatteryRepository
) : ViewModel() {

    val batteryState: StateFlow<BatteryTelemetry> = batteryRepository.batteryState

    fun togglePowerSaving(enabled: Boolean) {
        batteryRepository.togglePowerSaving(enabled)
    }

    fun setLowBatteryThreshold(threshold: Int) {
        batteryRepository.setLowBatteryThreshold(threshold)
    }

    fun simulateBatteryDrain() {
        batteryRepository.simulateBatteryDrain(5)
    }

    fun toggleCharging() {
        batteryRepository.toggleChargingSimulation()
    }
}
