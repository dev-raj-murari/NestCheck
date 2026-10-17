package com.nestcheck.app.data.repository

import com.nestcheck.app.data.model.LowBatteryConfig
import com.nestcheck.app.data.model.LowBatteryLastPing
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LowBatteryPingRepository @Inject constructor() {

    private val _config = MutableStateFlow(LowBatteryConfig())
    val config: StateFlow<LowBatteryConfig> = _config.asStateFlow()

    private val _pings = MutableStateFlow(
        listOf(
            LowBatteryLastPing(
                id = UUID.randomUUID().toString(),
                batteryLevel = 14,
                isCriticalShutdown = false,
                latitude = 19.1054,
                longitude = 72.8362,
                accuracyMeters = 3.8f,
                timestamp = System.currentTimeMillis() - 1000L * 60 * 42,
                addressName = "NMIMS MPSTME Building, Vile Parle West, Mumbai",
                networkType = "5G SA (Jio) / Wi-Fi Active",
                isUploadedToCloud = true,
                isSmsDispatched = true
            ),
            LowBatteryLastPing(
                id = UUID.randomUUID().toString(),
                batteryLevel = 4,
                isCriticalShutdown = true,
                latitude = 19.1028,
                longitude = 72.8375,
                accuracyMeters = 5.2f,
                timestamp = System.currentTimeMillis() - 1000L * 60 * 60 * 26,
                addressName = "Irla Nursing Home Lane, Vile Parle West, Mumbai",
                networkType = "LTE 4G (Airtel)",
                isUploadedToCloud = true,
                isSmsDispatched = true
            ),
            LowBatteryLastPing(
                id = UUID.randomUUID().toString(),
                batteryLevel = 15,
                isCriticalShutdown = false,
                latitude = 19.1080,
                longitude = 72.8290,
                accuracyMeters = 4.1f,
                timestamp = System.currentTimeMillis() - 1000L * 60 * 60 * 48,
                addressName = "Juhu Beach Promenade, Juhu, Mumbai",
                networkType = "5G NSA (Jio)",
                isUploadedToCloud = true,
                isSmsDispatched = true
            )
        )
    )
    val pings: StateFlow<List<LowBatteryLastPing>> = _pings.asStateFlow()

    fun updateTriggerThreshold(percent: Int) {
        _config.value = _config.value.copy(triggerThresholdPercent = percent.coerceIn(5, 30))
    }

    fun toggleAutoSms(enabled: Boolean) {
        _config.value = _config.value.copy(autoDispatchSmsToEmergencyContacts = enabled)
    }

    fun toggleHighPowerFix(enabled: Boolean) {
        _config.value = _config.value.copy(enableGpsHighPowerFixOnDrain = enabled)
    }

    fun triggerEmergencyFix(batteryLevel: Int, customAddress: String? = null): LowBatteryLastPing {
        val isCritical = batteryLevel <= _config.value.criticalThresholdPercent
        val addresses = listOf(
            "Vile Parle Station West, SV Road, Mumbai",
            "Mithibai College Main Gate, Bhaktivedanta Swami Marg, Mumbai",
            "Cooper Hospital Emergency Gate, Juhu, Mumbai",
            "NMIMS Campus Gate 2, Vile Parle West, Mumbai"
        )
        val selectedAddress = customAddress ?: addresses.random()

        val ping = LowBatteryLastPing(
            id = UUID.randomUUID().toString(),
            batteryLevel = batteryLevel,
            isCriticalShutdown = isCritical,
            latitude = 19.1030 + (Math.random() - 0.5) * 0.005,
            longitude = 72.8350 + (Math.random() - 0.5) * 0.005,
            accuracyMeters = if (isCritical) 2.9f else 4.5f,
            timestamp = System.currentTimeMillis(),
            addressName = selectedAddress,
            networkType = if (isCritical) "GPS Cold Fix / GSM Cell-ID" else "5G SA (Jio) / Fused GNSS",
            isUploadedToCloud = true,
            isSmsDispatched = _config.value.autoDispatchSmsToEmergencyContacts
        )

        _pings.value = listOf(ping) + _pings.value
        _config.value = _config.value.copy(
            totalEmergencyPingsLogged = _config.value.totalEmergencyPingsLogged + 1
        )
        return ping
    }
}
