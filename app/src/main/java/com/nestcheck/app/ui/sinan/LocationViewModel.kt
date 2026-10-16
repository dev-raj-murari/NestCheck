package com.nestcheck.app.ui.sinan

import androidx.lifecycle.ViewModel
import com.nestcheck.app.data.model.GeofenceZone
import com.nestcheck.app.data.model.LiveLocationPoint
import com.nestcheck.app.data.model.LocationBreadcrumb
import com.nestcheck.app.data.model.GeofenceTransitionAlert
import com.nestcheck.app.data.model.HighVelocityAlert
import com.nestcheck.app.data.model.TransitionType
import com.nestcheck.app.data.model.ZoneType
import com.nestcheck.app.data.repository.GeofenceStatusResult
import com.nestcheck.app.data.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

enum class LocationTab(val label: String) {
    LIVE_TELEMETRY("Live GPS"),
    GEOFENCES("Safe Zones"),
    SPEED_MONITOR("Speed Guard"),
    BREADCRUMBS("Trail Log")
}

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val locationRepository: LocationRepository
) : ViewModel() {

    // Tab Selection
    private val _currentTab = MutableStateFlow(LocationTab.LIVE_TELEMETRY)
    val currentTab: StateFlow<LocationTab> = _currentTab.asStateFlow()

    fun selectTab(tab: LocationTab) {
        _currentTab.value = tab
    }



    // F30: Speeding Alerts
    val speedAlerts: StateFlow<List<HighVelocityAlert>> = locationRepository.speedAlerts
    val speedThresholdKmh: StateFlow<Float> = locationRepository.speedThresholdKmh
    val inTransitMode: StateFlow<Boolean> = locationRepository.inTransitMode

    fun setSpeedThreshold(threshold: Float) {
        locationRepository.setSpeedThreshold(threshold)
    }

    fun toggleTransitMode() {
        locationRepository.toggleTransitMode()
    }

    fun triggerSpeedSimulation(speed: Float) {
        locationRepository.simulateSpeedReading(speed)
    }

    fun dismissSpeedAlert(id: String) {
        locationRepository.dismissSpeedAlert(id)
    }

    // F29: Geofence Transition Alerts
    val transitionAlerts: StateFlow<List<GeofenceTransitionAlert>> = locationRepository.transitionAlerts

    fun simulateTransition(zoneName: String, type: TransitionType, zoneType: ZoneType) {
        locationRepository.triggerGeofenceTransition(zoneName, type, zoneType)
    }

    // F12: Breadcrumbs
    val breadcrumbs: StateFlow<List<LocationBreadcrumb>> = locationRepository.breadcrumbs

    // F11: Live Telemetry
    val liveLocation: StateFlow<LiveLocationPoint> = locationRepository.liveLocation

    fun refreshLiveGps() {
        locationRepository.refreshLiveLocation()
    }

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
