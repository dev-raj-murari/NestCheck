package com.nestcheck.app.data.model

data class DeviceLockState(
    val childId: String,
    val childName: String,
    val isLocked: Boolean = false,
    val lockReason: String = "Parent Remote Lock",
    val customMessage: String = "Device temporarily paused by parent.",
    val lockedAt: Long? = null,
    val unlockAt: Long? = null,
    val durationMinutes: Int? = null,
    val emergencyCallsAllowed: Boolean = true
)

data class HouseholdChild(
    val id: String,
    val name: String,
    val age: Int,
    val grade: String,
    val avatarColorHex: Long,
    val deviceName: String,
    val isOnline: Boolean,
    val batteryPct: Int,
    val screenTimeMinutes: Int,
    val screenTimeLimitMinutes: Int,
    val locationSummary: String,
    val stepsToday: Int,
    val pendingAlertsCount: Int,
    val pairingCode: String,
    val isDeviceLocked: Boolean
)

data class AiParentingMessage(
    val id: String,
    val text: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val category: String? = null,
    val actionPoints: List<String> = emptyList(),
    val suggestedFollowUps: List<String> = emptyList()
)

enum class CoParentRole(val displayName: String, val description: String) {
    ADMIN("Co-Parent (Admin)", "Full access: screen time, app blocking, remote lock, and geofences."),
    GUARDIAN("Guardian / Sitter", "Supervisory: view live location, emergency SOS pings, dinnertime pause."),
    EMERGENCY_ONLY("Emergency Contact", "Receives emergency panic broadcasts and critical safety alerts only.")
}

enum class InviteStatus {
    ACTIVE, PENDING, EXPIRED
}

data class CoParentMember(
    val id: String,
    val name: String,
    val email: String,
    val role: CoParentRole,
    val status: InviteStatus,
    val invitedAt: String,
    val lastActive: String
)

data class HouseholdInviteCode(
    val code: String,
    val expiresAtFormatted: String,
    val targetRole: CoParentRole
)

data class LanguageOption(
    val code: String,
    val englishName: String,
    val nativeName: String,
    val flagEmoji: String,
    val isRtl: Boolean = false
)

data class LocalizationConfig(
    val currentLanguageCode: String = "en",
    val use24HourTime: Boolean = true,
    val useMetricSystem: Boolean = true,
    val dateFormatPattern: String = "DD/MM/YYYY"
)
