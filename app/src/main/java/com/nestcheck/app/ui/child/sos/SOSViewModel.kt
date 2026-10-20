package com.nestcheck.app.ui.child.sos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nestcheck.app.core.NestCheckNotificationManager
import com.nestcheck.app.data.model.SOSAlert
import com.nestcheck.app.data.model.SOSContactInfo
import com.nestcheck.app.data.repository.SOSRepository
import com.nestcheck.app.data.sync.RealtimeFamilySyncManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SOSViewModel @Inject constructor(
    private val sosRepository: SOSRepository,
    val syncManager: RealtimeFamilySyncManager,
    private val notificationManager: NestCheckNotificationManager
) : ViewModel() {

    val sosState: StateFlow<SOSAlert> = sosRepository.sosState

    private val _countdownSeconds = MutableStateFlow(3)
    val countdownSeconds: StateFlow<Int> = _countdownSeconds.asStateFlow()

    private val _isHolding = MutableStateFlow(false)
    val isHolding: StateFlow<Boolean> = _isHolding.asStateFlow()

    private var countdownJob: Job? = null

    fun onStartHold() {
        if (_isHolding.value || sosState.value.isEmergencyActive) return
        _isHolding.value = true
        _countdownSeconds.value = 3

        countdownJob = viewModelScope.launch {
            for (i in 3 downTo 1) {
                _countdownSeconds.value = i
                delay(1000)
            }
            _isHolding.value = false
            triggerImmediateSOS()
        }
    }

    fun onCancelHold() {
        countdownJob?.cancel()
        countdownJob = null
        _isHolding.value = false
        _countdownSeconds.value = 3
    }

    fun triggerImmediateSOS() {
        val currentSync = syncManager.syncState.value
        val childName = currentSync.activeChildName.ifBlank { "Olivia" }
        val lat = currentSync.kidLatitude
        val lng = currentSync.kidLongitude
        val phone = currentSync.emergencyContactPhone.ifBlank { "+91 98765 43210" }

        val contacts = mutableListOf<SOSContactInfo>()
        val dad = currentSync.householdParents.father
        if (dad.name.isNotBlank() && dad.phone.isNotBlank()) {
            contacts.add(SOSContactInfo("${dad.name} (Dad)", "Father", dad.phone))
        }
        val mom = currentSync.householdParents.mother
        if (mom.name.isNotBlank() && mom.phone.isNotBlank()) {
            contacts.add(SOSContactInfo("${mom.name} (Mom)", "Mother", mom.phone))
        }
        val guardian = currentSync.householdParents.guardian
        if (guardian.name.isNotBlank() && guardian.phone.isNotBlank()) {
            contacts.add(SOSContactInfo("${guardian.name} (Guardian)", "Guardian", guardian.phone))
        }
        if (contacts.isEmpty()) {
            contacts.add(SOSContactInfo("Primary Emergency Parent", "Primary", phone))
        }

        // 1. Sync real-time to Parent App via RealtimeFamilySyncManager
        syncManager.triggerChildEmergencySOS(
            location = "MPSTME NMIMS Campus, Vile Parle West, Mumbai",
            lat = lat,
            lng = lng
        )

        // 2. High priority notification to Parent App with parent fast-dial
        notificationManager.showSosAlert(
            childName = childName,
            locationText = "MPSTME NMIMS Campus, Vile Parle West, Mumbai",
            lat = lat,
            lng = lng,
            parentPhone = phone
        )

        // 3. Update local SOS state
        sosRepository.triggerEmergency(
            childName = childName,
            lat = lat,
            lng = lng,
            battery = currentSync.kidBattery,
            emergencyPhone = phone,
            contacts = contacts
        )
    }

    fun cancelActiveEmergency() {
        syncManager.dismissSosEmergency()
        sosRepository.cancelEmergency()
        _isHolding.value = false
        _countdownSeconds.value = 3
    }
}
