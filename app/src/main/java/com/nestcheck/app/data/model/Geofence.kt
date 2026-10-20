package com.nestcheck.app.data.model

enum class ZoneType(val displayName: String) {
    SAFE_ZONE("Safe Zone"),
    RESTRICTED_ZONE("Restricted Zone")
}

data class GeofenceZone(
    val id: String = "",
    val name: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val radiusMeters: Float = 300f,
    val isActive: Boolean = true,
    val zoneType: ZoneType = ZoneType.SAFE_ZONE,
    val description: String = "",
    val notificationEnabled: Boolean = true
)

// Default zones for MPSTME Mumbai Maharashtra
object DefaultGeofences {
    val SCHOOL = GeofenceZone(
        id = "school_mpstme",
        name = "MPSTME NMIMS Mumbai",
        latitude = 19.1075,
        longitude = 72.8372,
        radiusMeters = 300f,
        isActive = true,
        zoneType = ZoneType.SAFE_ZONE,
        description = "MPSTME College Campus Safe Zone (Vile Parle West)"
    )
    val HOME = GeofenceZone(
        id = "home_mumbai",
        name = "Home Residence",
        latitude = 19.1265,
        longitude = 72.8310,
        radiusMeters = 500f,
        isActive = true,
        zoneType = ZoneType.SAFE_ZONE,
        description = "Home Safe Perimeter"
    )
    val DANGEROUS_QUARRY = GeofenceZone(
        id = "restricted_area",
        name = "Restricted Industrial Zone",
        latitude = 19.1420,
        longitude = 72.8250,
        radiusMeters = 400f,
        isActive = true,
        zoneType = ZoneType.RESTRICTED_ZONE,
        description = "Hazardous Out-of-bounds Zone"
    )
}
