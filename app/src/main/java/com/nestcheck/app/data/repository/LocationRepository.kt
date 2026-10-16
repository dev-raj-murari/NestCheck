package com.nestcheck.app.data.repository

import com.nestcheck.app.data.model.DefaultGeofences
import com.nestcheck.app.data.model.GeofenceZone
import com.nestcheck.app.data.model.LiveLocationPoint
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
}
