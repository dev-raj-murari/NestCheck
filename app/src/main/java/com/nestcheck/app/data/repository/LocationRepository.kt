package com.nestcheck.app.data.repository

import com.nestcheck.app.data.model.DefaultGeofences
import com.nestcheck.app.data.model.GeofenceZone
import com.nestcheck.app.data.model.LiveLocationPoint
import com.nestcheck.app.data.model.LocationBreadcrumb
import com.nestcheck.app.data.model.GeofenceTransitionAlert
import com.nestcheck.app.data.model.HighVelocityAlert
import com.nestcheck.app.data.model.TransitionType
import com.nestcheck.app.data.model.ZoneType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.*

data class GeofenceStatusResult(
    val currentZone: GeofenceZone?,
    val isInsideSafeZone: Boolean,
    val isInsideRestrictedZone: Boolean,
    val distanceToNearestMeters: Float
)

@Singleton
class LocationRepository @Inject constructor() {

    private val _zones = MutableStateFlow<List<GeofenceZone>>(
        listOf(
            DefaultGeofences.SCHOOL,
            DefaultGeofences.HOME,
            DefaultGeofences.DANGEROUS_QUARRY,
            GeofenceZone(
                id = "zone_library",
                name = "City Central Library",
                latitude = 19.1120,
                longitude = 72.8400,
                radiusMeters = 250f,
                isActive = true,
                zoneType = ZoneType.SAFE_ZONE,
                description = "Study & Homework Safe Zone"
            ),
            GeofenceZone(
                id = "zone_sports",
                name = "Andheri Sports Complex",
                latitude = 19.1310,
                longitude = 72.8360,
                radiusMeters = 350f,
                isActive = true,
                zoneType = ZoneType.SAFE_ZONE,
                description = "After-school Activity Field"
            )
        )
    )
    val zones: StateFlow<List<GeofenceZone>> = _zones.asStateFlow()

    fun addZone(
        name: String,
        latitude: Double,
        longitude: Double,
        radiusMeters: Float,
        zoneType: ZoneType,
        description: String = ""
    ) {
        val newZone = GeofenceZone(
            id = UUID.randomUUID().toString(),
            name = name.trim(),
            latitude = latitude,
            longitude = longitude,
            radiusMeters = radiusMeters,
            isActive = true,
            zoneType = zoneType,
            description = description.trim(),
            notificationEnabled = true
        )
        _zones.value = listOf(newZone) + _zones.value
    }

    fun toggleZoneActive(id: String) {
        _zones.value = _zones.value.map {
            if (it.id == id) it.copy(isActive = !it.isActive) else it
        }
    }

    fun deleteZone(id: String) {
        _zones.value = _zones.value.filterNot { it.id == id }
    }

    fun checkPosition(lat: Double, lng: Double): GeofenceStatusResult {
        var insideSafe = false
        var insideRestricted = false
        var matchingZone: GeofenceZone? = null
        var minDistance = Float.MAX_VALUE

        for (zone in _zones.value.filter { it.isActive }) {
            val dist = calculateDistanceMeters(lat, lng, zone.latitude, zone.longitude)
            if (dist < minDistance) {
                minDistance = dist
            }
            if (dist <= zone.radiusMeters) {
                matchingZone = zone
                if (zone.zoneType == ZoneType.SAFE_ZONE) insideSafe = true
                if (zone.zoneType == ZoneType.RESTRICTED_ZONE) insideRestricted = true
            }
        }

        return GeofenceStatusResult(
            currentZone = matchingZone,
            isInsideSafeZone = insideSafe,
            isInsideRestrictedZone = insideRestricted,
            distanceToNearestMeters = if (minDistance == Float.MAX_VALUE) 0f else minDistance
        )
    }

    private fun calculateDistanceMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val r = 6371000.0 // Earth radius in meters
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2.0) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return (r * c).toFloat()
    }
    // ========================================================================
    // F11: LIVE GPS LOCATION TELEMETRY
    // ========================================================================

    private val _liveLocation = MutableStateFlow(
        LiveLocationPoint(
            latitude = 19.1050,
            longitude = 72.8350,
            accuracyMeters = 4.2f,
            altitudeMeters = 24.0,
            speedKmh = 0.0f,
            headingDegrees = 120.0f,
            batteryPercent = 88,
            timestamp = System.currentTimeMillis(),
            addressName = "MPSTME NMIMS Campus, Vile Parle West, Mumbai",
            provider = "GPS (High Accuracy Fused)"
        )
    )
    val liveLocation: StateFlow<LiveLocationPoint> = _liveLocation.asStateFlow()

    fun refreshLiveLocation() {
        val current = _liveLocation.value
        val jitterLat = (Math.random() - 0.5) * 0.0004
        val jitterLng = (Math.random() - 0.5) * 0.0004
        val newAccuracy = 3.5f + (Math.random() * 2.0).toFloat()

        _liveLocation.value = current.copy(
            latitude = current.latitude + jitterLat,
            longitude = current.longitude + jitterLng,
            accuracyMeters = newAccuracy,
            timestamp = System.currentTimeMillis()
        )
    }

    // ========================================================================
    // F12: HISTORICAL LOCATION BREADCRUMB TRAIL
    // ========================================================================

    private val _breadcrumbs = MutableStateFlow<List<LocationBreadcrumb>>(
        listOf(
            LocationBreadcrumb(
                id = "bc_1",
                title = "Arrived at MPSTME NMIMS Campus",
                latitude = 19.1050,
                longitude = 72.8350,
                timestamp = System.currentTimeMillis() - 1000 * 60 * 360,
                durationSpentMinutes = 320,
                category = "School Stay",
                speedKmh = 0.0f
            ),
            LocationBreadcrumb(
                id = "bc_2",
                title = "Transit via SV Road Bus Transit",
                latitude = 19.1150,
                longitude = 72.8380,
                timestamp = System.currentTimeMillis() - 1000 * 60 * 385,
                durationSpentMinutes = 25,
                category = "Transit",
                speedKmh = 38.5f
            ),
            LocationBreadcrumb(
                id = "bc_3",
                title = "Study Session at City Central Library",
                latitude = 19.1120,
                longitude = 72.8400,
                timestamp = System.currentTimeMillis() - 1000 * 60 * 500,
                durationSpentMinutes = 90,
                category = "Study",
                speedKmh = 0.0f
            ),
            LocationBreadcrumb(
                id = "bc_4",
                title = "Departed Home Residence",
                latitude = 19.1265,
                longitude = 72.8310,
                timestamp = System.currentTimeMillis() - 1000 * 60 * 520,
                durationSpentMinutes = 15,
                category = "Departure",
                speedKmh = 4.2f
            )
        )
    )
    val breadcrumbs: StateFlow<List<LocationBreadcrumb>> = _breadcrumbs.asStateFlow()

    fun addBreadcrumb(
        title: String,
        latitude: Double,
        longitude: Double,
        durationMinutes: Int = 0,
        category: String = "Waypoint"
    ) {
        val item = LocationBreadcrumb(
            id = UUID.randomUUID().toString(),
            title = title,
            latitude = latitude,
            longitude = longitude,
            timestamp = System.currentTimeMillis(),
            durationSpentMinutes = durationMinutes,
            category = category,
            speedKmh = _liveLocation.value.speedKmh
        )
        _breadcrumbs.value = listOf(item) + _breadcrumbs.value
    }

    // ========================================================================
    // F29: GEOFENCE TRANSITION ALERTS
    // ========================================================================

    private val _transitionAlerts = MutableStateFlow<List<GeofenceTransitionAlert>>(
        listOf(
            GeofenceTransitionAlert(
                id = "trans_1",
                zoneName = "MPSTME NMIMS",
                transitionType = TransitionType.ENTERED,
                zoneType = ZoneType.SAFE_ZONE,
                timestamp = System.currentTimeMillis() - 1000 * 60 * 45,
                latitude = 19.1050,
                longitude = 72.8350,
                address = "Vile Parle West, Mumbai"
            ),
            GeofenceTransitionAlert(
                id = "trans_2",
                zoneName = "Home Residence",
                transitionType = TransitionType.EXITED,
                zoneType = ZoneType.SAFE_ZONE,
                timestamp = System.currentTimeMillis() - 1000 * 60 * 120,
                latitude = 19.1265,
                longitude = 72.8310,
                address = "DN Nagar Metro Area"
            ),
            GeofenceTransitionAlert(
                id = "trans_3",
                zoneName = "City Central Library",
                transitionType = TransitionType.ENTERED,
                zoneType = ZoneType.SAFE_ZONE,
                timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 24,
                latitude = 19.1120,
                longitude = 72.8400,
                address = "Central Plaza"
            )
        )
    )
    val transitionAlerts: StateFlow<List<GeofenceTransitionAlert>> = _transitionAlerts.asStateFlow()

    fun triggerGeofenceTransition(
        zoneName: String,
        transitionType: TransitionType,
        zoneType: ZoneType
    ) {
        val loc = _liveLocation.value
        val alert = GeofenceTransitionAlert(
            id = UUID.randomUUID().toString(),
            zoneName = zoneName,
            transitionType = transitionType,
            zoneType = zoneType,
            timestamp = System.currentTimeMillis(),
            latitude = loc.latitude,
            longitude = loc.longitude,
            address = loc.addressName
        )
        _transitionAlerts.value = listOf(alert) + _transitionAlerts.value
    }

    // ========================================================================
    // F30: HIGH-VELOCITY TRAVEL & SPEEDING TRIGGER ALERT
    // ========================================================================

    private val _speedThresholdKmh = MutableStateFlow(45.0f)
    val speedThresholdKmh: StateFlow<Float> = _speedThresholdKmh.asStateFlow()

    private val _inTransitMode = MutableStateFlow(false)
    val inTransitMode: StateFlow<Boolean> = _inTransitMode.asStateFlow()

    private val _speedAlerts = MutableStateFlow<List<HighVelocityAlert>>(
        listOf(
            HighVelocityAlert(
                id = "spd_1",
                recordedSpeedKmh = 58.4f,
                speedThresholdKmh = 45.0f,
                timestamp = System.currentTimeMillis() - 1000 * 60 * 85,
                locationAddress = "Western Express Highway Near Andheri Flyover",
                inTransitDesignated = false,
                isDismissed = false
            )
        )
    )
    val speedAlerts: StateFlow<List<HighVelocityAlert>> = _speedAlerts.asStateFlow()

    fun setSpeedThreshold(threshold: Float) {
        _speedThresholdKmh.value = threshold
    }

    fun toggleTransitMode() {
        _inTransitMode.value = !_inTransitMode.value
    }

    fun simulateSpeedReading(speedKmh: Float) {
        val current = _liveLocation.value
        _liveLocation.value = current.copy(speedKmh = speedKmh)

        if (speedKmh > _speedThresholdKmh.value && !_inTransitMode.value) {
            val alert = HighVelocityAlert(
                id = UUID.randomUUID().toString(),
                recordedSpeedKmh = speedKmh,
                speedThresholdKmh = _speedThresholdKmh.value,
                timestamp = System.currentTimeMillis(),
                locationAddress = current.addressName,
                inTransitDesignated = false,
                isDismissed = false
            )
            _speedAlerts.value = listOf(alert) + _speedAlerts.value
        }
    }

    fun dismissSpeedAlert(id: String) {
        _speedAlerts.value = _speedAlerts.value.filterNot { it.id == id }
    }
}
