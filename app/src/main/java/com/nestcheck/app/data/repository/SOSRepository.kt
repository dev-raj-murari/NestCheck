package com.nestcheck.app.data.repository

import com.nestcheck.app.data.model.SOSAlert
import com.nestcheck.app.data.model.SOSContactInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SOSRepository @Inject constructor() {

    private val _sosState = MutableStateFlow(SOSAlert())
    val sosState: StateFlow<SOSAlert> = _sosState.asStateFlow()

    fun triggerEmergency(
        childName: String = "Olivia",
        lat: Double = 19.1075,
        lng: Double = 72.8372,
        battery: Int = 84,
        emergencyPhone: String = "+91 98765 43210",
        contacts: List<SOSContactInfo> = emptyList()
    ) {
        _sosState.value = _sosState.value.copy(
            childName = childName,
            latitude = lat,
            longitude = lng,
            batteryPercent = battery,
            emergencyContactPhone = emergencyPhone,
            notifiedContacts = if (contacts.isNotEmpty()) contacts else _sosState.value.notifiedContacts,
            timestamp = System.currentTimeMillis(),
            isEmergencyActive = true
        )
    }

    fun cancelEmergency() {
        _sosState.value = _sosState.value.copy(isEmergencyActive = false)
    }
}
