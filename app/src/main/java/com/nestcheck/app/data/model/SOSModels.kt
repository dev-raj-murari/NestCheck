package com.nestcheck.app.data.model

data class SOSAlert(
    val id: String = java.util.UUID.randomUUID().toString(),
    val childName: String = "Alex",
    val latitude: Double = 12.9716,
    val longitude: Double = 77.5946,
    val batteryPercent: Int = 84,
    val timestamp: Long = System.currentTimeMillis(),
    val isEmergencyActive: Boolean = false,
    val notifiedContacts: List<String> = listOf("Mother (Primary)", "Father", "Local Police (112)")
)
