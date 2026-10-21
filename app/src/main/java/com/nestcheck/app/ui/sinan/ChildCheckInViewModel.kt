package com.nestcheck.app.ui.sinan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nestcheck.app.data.model.ChildArrivalCheckIn
import com.nestcheck.app.data.model.LiveLocationPoint
import com.nestcheck.app.data.repository.LocationRepository
import com.nestcheck.app.data.sync.RealtimeFamilySyncManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChildCheckInViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val syncManager: RealtimeFamilySyncManager
) : ViewModel() {

    val liveLocation: StateFlow<LiveLocationPoint> = locationRepository.liveLocation

    private val _lastSentCheckIn = MutableStateFlow<ChildArrivalCheckIn?>(null)
    val lastSentCheckIn: StateFlow<ChildArrivalCheckIn?> = _lastSentCheckIn.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    fun sendCheckIn(label: String, note: String) {
        if (_isSubmitting.value) return
        _isSubmitting.value = true

        viewModelScope.launch {
            delay(300)
            val loc = liveLocation.value
            val childName = syncManager.syncState.value.activeChildName
            val result = locationRepository.sendChildCheckIn(label, note, childName)

            // Save to shared backend & notify parent app immediately (Requirement 10)
            syncManager.onChildCheckIn(label, note, loc.latitude, loc.longitude)

            _lastSentCheckIn.value = result
            _isSubmitting.value = false
        }
    }

    fun dismissConfirmation() {
        _lastSentCheckIn.value = null
    }
}
