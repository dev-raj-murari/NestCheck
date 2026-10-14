package com.nestcheck.app.data.repository

import com.nestcheck.app.data.model.SOSAlert
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SOSRepository @Inject constructor() {

    private val _sosState = MutableStateFlow(SOSAlert())
    val sosState: StateFlow<SOSAlert> = _sosState.asStateFlow()

    fun triggerEmergency(childName: String = "Alex", lat: Double = 12.9716, lng: Double = 77.5946, battery: Int = 84) {
        _sosState.value = _sosState.value.copy(
            childName = childName,
            latitude = lat,
            longitude = lng,
            batteryPercent = battery,
            timestamp = System.currentTimeMillis(),
            isEmergencyActive = true
        )
    }

    fun cancelEmergency() {
        _sosState.value = _sosState.value.copy(isEmergencyActive = false)
    }
}
