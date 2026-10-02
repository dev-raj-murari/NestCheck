package com.nestcheck.app.data.model

data class GeofenceZone(
    val id: String = "",
    val name: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val radiusMeters: Float = 300f,
    val isActive: Boolean = true
)

// Default zones
object DefaultGeofences {
    val SCHOOL = GeofenceZone(
        id = "school_mpstme",
        name = "MPSTME NMIMS",
        latitude = 19.1050,
        longitude = 72.8350,
        radiusMeters = 300f
    )
    val HOME = GeofenceZone(
        id = "home_dn_nagar",
        name = "DN Nagar Metro",
        latitude = 19.1265,
        longitude = 72.8310,
        radiusMeters = 500f
    )
}
