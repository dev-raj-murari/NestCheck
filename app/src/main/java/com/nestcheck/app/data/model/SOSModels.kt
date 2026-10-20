package com.nestcheck.app.data.model

data class SOSContactInfo(
    val name: String,
    val relation: String,
    val phone: String
)

data class SOSAlert(
    val id: String = java.util.UUID.randomUUID().toString(),
    val childName: String = "Olivia",
    val latitude: Double = 19.1075,
    val longitude: Double = 72.8372,
    val batteryPercent: Int = 84,
    val timestamp: Long = System.currentTimeMillis(),
    val isEmergencyActive: Boolean = false,
    val emergencyContactPhone: String = "+91 98765 43210",
    val notifiedContacts: List<SOSContactInfo> = listOf(
        SOSContactInfo("Rohit Patil (Dad)", "Father", "+91 98765 43210"),
        SOSContactInfo("Priya Patil (Mom)", "Mother", "+91 98765 43211"),
        SOSContactInfo("Ramesh (Guardian)", "Guardian", "+91 98765 43212")
    )
)
