package com.nestcheck.app.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

data class BatteryTelemetry(
    val percentage: Int = 78,
    val isCharging: Boolean = false,
    val chargingSource: String = "Not Charging", // AC, USB, Wireless
    val temperatureCelsius: Float = 31.5f,
    val voltageVolts: Float = 4.12f,
    val healthStatus: String = "Good",
    val estimatedRemainingMinutes: Int = 340,
    val powerSavingEnabled: Boolean = false,
    val lowBatteryAlertThreshold: Int = 15,
    val lastUpdatedTimestamp: Long = System.currentTimeMillis()
)

@Singleton
class BatteryRepository @Inject constructor() {

    private val _batteryState = MutableStateFlow(BatteryTelemetry())
    val batteryState: StateFlow<BatteryTelemetry> = _batteryState.asStateFlow()

    fun updateBatteryLevel(level: Int, charging: Boolean, source: String = "AC") {
        _batteryState.value = _batteryState.value.copy(
            percentage = level.coerceIn(0, 100),
            isCharging = charging,
            chargingSource = if (charging) source else "Not Charging",
            lastUpdatedTimestamp = System.currentTimeMillis()
        )
    }

    fun togglePowerSaving(enabled: Boolean) {
        _batteryState.value = _batteryState.value.copy(powerSavingEnabled = enabled)
    }

    fun setLowBatteryThreshold(threshold: Int) {
        _batteryState.value = _batteryState.value.copy(lowBatteryAlertThreshold = threshold)
    }

    fun simulateBatteryDrain(decrement: Int = 5) {
        val current = _batteryState.value.percentage
        val newLevel = (current - decrement).coerceAtLeast(1)
        _batteryState.value = _batteryState.value.copy(
            percentage = newLevel,
            lastUpdatedTimestamp = System.currentTimeMillis()
        )
    }

    fun toggleChargingSimulation() {
        val current = _batteryState.value
        val newCharging = !current.isCharging
        _batteryState.value = current.copy(
            isCharging = newCharging,
            chargingSource = if (newCharging) "Fast AC Adapter" else "Not Charging",
            lastUpdatedTimestamp = System.currentTimeMillis()
        )
    }
}
