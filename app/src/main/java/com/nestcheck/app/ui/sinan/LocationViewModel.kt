package com.nestcheck.app.ui.sinan

import androidx.lifecycle.ViewModel
import com.nestcheck.app.data.model.GeofenceZone
import com.nestcheck.app.data.model.ZoneType
import com.nestcheck.app.data.repository.GeofenceStatusResult
import com.nestcheck.app.data.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val locationRepository: LocationRepository
) : ViewModel() {

    val zones: StateFlow<List<GeofenceZone>> = locationRepository.zones

    // Dialog state for adding a new zone
    private val _isAddZoneDialogOpen = MutableStateFlow(false)
    val isAddZoneDialogOpen: StateFlow<Boolean> = _isAddZoneDialogOpen.asStateFlow()

    // Test position evaluation state
    private val _testResult = MutableStateFlow<GeofenceStatusResult?>(null)
    val testResult: StateFlow<GeofenceStatusResult?> = _testResult.asStateFlow()

    fun openAddZoneDialog() {
        _isAddZoneDialogOpen.value = true
    }

    fun closeAddZoneDialog() {
        _isAddZoneDialogOpen.value = false
    }

    fun addGeofence(
        name: String,
        latitude: Double,
        longitude: Double,
        radiusMeters: Float,
        zoneType: ZoneType,
        description: String
    ) {
        if (name.isNotBlank()) {
            locationRepository.addZone(
                name = name,
                latitude = latitude,
                longitude = longitude,
                radiusMeters = radiusMeters,
                zoneType = zoneType,
                description = description
            )
            _isAddZoneDialogOpen.value = false
        }
    }

    fun toggleZone(id: String) {
        locationRepository.toggleZoneActive(id)
    }

    fun deleteZone(id: String) {
        locationRepository.deleteZone(id)
    }

    fun checkPosition(latitude: Double, longitude: Double) {
        _testResult.value = locationRepository.checkPosition(latitude, longitude)
    }

    fun clearPositionCheck() {
        _testResult.value = null
    }
}
