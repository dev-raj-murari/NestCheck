package com.nestcheck.app.data.model

data class ParentContactInfo(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String = "",
    val role: String = "Father", // "Father", "Mother", "Guardian"
    val email: String = "",
    val phone: String = "",
    val isPrimary: Boolean = false,
    val isEmergencyContact: Boolean = false
)

data class HouseholdParentConfig(
    val father: ParentContactInfo = ParentContactInfo(name = "Dev Raj", role = "Father", email = "devraj@nestcheck.app", phone = "+91 98765 43210", isPrimary = true, isEmergencyContact = true),
    val mother: ParentContactInfo = ParentContactInfo(name = "Priya", role = "Mother", email = "priya@nestcheck.app", phone = "+91 98765 43212"),
    val guardian: ParentContactInfo = ParentContactInfo(name = "Rohit", role = "Guardian", email = "rohit@nestcheck.app", phone = "+91 98765 43211"),
    val activeParentRole: String = "Father"
)

data class ParentUser(
    val uid: String = "",
    val email: String = "",
    val name: String = "",
    val roleInHouse: String = "Father", // e.g. Father, Mother, Guardian
    val phone: String = "",
    val emergencyContactPhone: String = "+91 98765 43210",
    val children: List<ChildProfile> = emptyList()
)

data class KidUser(
    val uid: String = "",
    val name: String = "",
    val parentEmail: String = "",
    val age: Int = 11,
    val gender: String = "Boy",
    val avatarUrl: String = "🦊",
    val emergencyContactPhone: String = "+91 98765 43210"
)
