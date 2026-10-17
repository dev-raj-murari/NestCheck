package com.nestcheck.app.data.model

// ============================================================================
// F7: ON-DEVICE NSFW & EXPLICIT CONTENT DETECTION MODELS
// ============================================================================

enum class NsfwSeverity(val label: String) {
    LOW("Low Severity"),
    MEDIUM("Medium Risk"),
    HIGH("High Risk")
}

enum class IncidentStatus(val displayName: String) {
    BLURRED("Auto-Blurred"),
    QUARANTINED("Quarantined"),
    REVIEWED("Reviewed"),
    DISMISSED("Dismissed")
}

enum class NsfwSensitivityLevel(val displayName: String, val threshold: Float) {
    LOW("Lenient (0.85)", 0.85f),
    MEDIUM("Balanced (0.65)", 0.65f),
    HIGH("Strict (0.45)", 0.45f)
}

data class NsfwIncident(
    val id: String = "",
    val mediaName: String = "",
    val sourceApp: String = "",
    val confidenceScore: Float = 0.0f,
    val timestamp: Long = System.currentTimeMillis(),
    val severity: NsfwSeverity = NsfwSeverity.MEDIUM,
    val status: IncidentStatus = IncidentStatus.BLURRED,
    val isBlurred: Boolean = true,
    val flagReason: String = "Explicit visual patterns detected"
)

data class SafetyScannerConfig(
    val sensitivityLevel: NsfwSensitivityLevel = NsfwSensitivityLevel.MEDIUM,
    val realtimeBlurEnabled: Boolean = true,
    val notifyParentInstant: Boolean = true,
    val scanIncomingMedia: Boolean = true,
    val totalScannedCount: Int = 142,
    val totalBlockedCount: Int = 8
)

// ============================================================================
// F8: WEB CATEGORY CONTENT FILTERING MODELS
// ============================================================================

enum class WebCategory(val displayName: String, val description: String) {
    ADULT("Adult & NSFW", "Explicit or adult-oriented material"),
    GAMBLING("Gambling & Betting", "Casinos, lotteries, and sports betting"),
    VIOLENCE("Violence & Gore", "Graphic gore and physical harm"),
    WEAPONS("Weapons & Firearms", "Firearms, ammunition, and explosives"),
    PIRACY("Piracy & Torrenting", "Illegal software and streaming downloads"),
    SOCIAL("Social Media", "Public chatrooms and unrestrained feeds"),
    GAMING("Browser Games", "Web-based distraction games")
}

data class WebCategoryRule(
    val category: WebCategory,
    val isBlocked: Boolean = true,
    val blockCount: Int = 0
)

data class CustomDomainRule(
    val id: String = "",
    val domain: String = "",
    val isBlocked: Boolean = true,
    val addedTimestamp: Long = System.currentTimeMillis(),
    val note: String = ""
)

data class WebAccessLog(
    val id: String = "",
    val url: String = "",
    val domain: String = "",
    val category: WebCategory = WebCategory.ADULT,
    val timestamp: Long = System.currentTimeMillis(),
    val wasBlocked: Boolean = true,
    val blockedReason: String = "Category Blocked"
)

// ============================================================================
// F9: NEW APP INSTALL APPROVAL & QUARANTINE MODELS
// ============================================================================

enum class QuarantineStatus(val displayName: String) {
    PENDING_APPROVAL("Pending Approval"),
    APPROVED("Approved"),
    BLOCKED("Quarantined & Blocked")
}

enum class RiskRating(val displayName: String) {
    SAFE("Low Risk"),
    MODERATE("Moderate Risk"),
    HIGH_RISK("High Risk")
}

data class QuarantinedApp(
    val id: String = "",
    val packageName: String = "",
    val appName: String = "",
    val versionName: String = "1.0.0",
    val developer: String = "Unknown Developer",
    val installTimestamp: Long = System.currentTimeMillis(),
    val contentRating: String = "12+",
    val permissions: List<String> = emptyList(),
    val status: QuarantineStatus = QuarantineStatus.PENDING_APPROVAL,
    val riskRating: RiskRating = RiskRating.MODERATE,
    val category: String = "Utility"
)

// ============================================================================
// F11: LIVE GPS LOCATION TELEMETRY MODELS
// ============================================================================

data class LiveLocationPoint(
    val latitude: Double = 19.1050,
    val longitude: Double = 72.8350,
    val accuracyMeters: Float = 4.5f,
    val altitudeMeters: Double = 22.0,
    val speedKmh: Float = 0.0f,
    val headingDegrees: Float = 145f,
    val batteryPercent: Int = 88,
    val timestamp: Long = System.currentTimeMillis(),
    val addressName: String = "MPSTME NMIMS Campus, Vile Parle West, Mumbai",
    val provider: String = "GPS (High Accuracy Fused)"
)

// ============================================================================
// F12: HISTORICAL LOCATION BREADCRUMB TRAIL MODELS
// ============================================================================

data class LocationBreadcrumb(
    val id: String = "",
    val title: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val timestamp: Long = 0L,
    val durationSpentMinutes: Int = 0,
    val category: String = "Stay",
    val speedKmh: Float = 0.0f
)

// ============================================================================
// F29: GEOFENCE TRANSITION ALERTS MODELS
// ============================================================================

enum class TransitionType(val displayName: String, val verb: String) {
    ENTERED("Arrived", "entered"),
    EXITED("Departed", "left")
}

data class GeofenceTransitionAlert(
    val id: String = "",
    val zoneName: String = "",
    val transitionType: TransitionType = TransitionType.ENTERED,
    val zoneType: ZoneType = ZoneType.SAFE_ZONE,
    val timestamp: Long = System.currentTimeMillis(),
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val address: String = ""
)

// ============================================================================
// F30: HIGH-VELOCITY TRAVEL ALERT MODELS
// ============================================================================

data class HighVelocityAlert(
    val id: String = "",
    val recordedSpeedKmh: Float = 0.0f,
    val speedThresholdKmh: Float = 45.0f,
    val timestamp: Long = System.currentTimeMillis(),
    val locationAddress: String = "",
    val inTransitDesignated: Boolean = false,
    val isDismissed: Boolean = false
)

// ============================================================================
// F36: "I'M HERE" ONE-TAP CHECK-IN MODELS
// ============================================================================

data class ChildArrivalCheckIn(
    val id: String = "",
    val childName: String = "Alex",
    val timestamp: Long = System.currentTimeMillis(),
    val latitude: Double = 19.1050,
    val longitude: Double = 72.8350,
    val locationLabel: String = "Arrived at School",
    val customNote: String = "I made it safely to campus!",
    val batteryLevel: Int = 88,
    val isParentAcknowledged: Boolean = false
)

// ============================================================================
// F31: TRUSTED CONTACTS WHITELIST DIRECTORY MODELS
// ============================================================================

enum class ContactRelationship(val displayName: String) {
    MOM("Mother"),
    DAD("Father"),
    GUARDIAN("Guardian"),
    TUTOR("Teacher / Tutor"),
    SIBLING("Sibling"),
    FRIEND("Approved Friend"),
    OTHER("Trusted Other")
}

enum class CommunicationType(val label: String) {
    CALL("Voice Call"),
    SMS("Text Message")
}

data class TrustedContact(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String = "",
    val phoneNumber: String = "",
    val email: String = "",
    val relationship: ContactRelationship = ContactRelationship.FRIEND,
    val isCallAllowed: Boolean = true,
    val isSmsAllowed: Boolean = true,
    val addedTimestamp: Long = System.currentTimeMillis()
)

data class UnknownCommunicationAlert(
    val id: String = java.util.UUID.randomUUID().toString(),
    val senderAddress: String = "",
    val communicationType: CommunicationType = CommunicationType.CALL,
    val snippet: String = "Incoming communication from unregistered caller",
    val timestamp: Long = System.currentTimeMillis(),
    val isBlocked: Boolean = true,
    val isReviewed: Boolean = false
)

// ============================================================================
// F32: CYBERBULLYING & TOXIC TEXT DETECTION MODELS
// ============================================================================

enum class ToxicityCategory(val displayName: String, val description: String) {
    BULLYING("Cyberbullying & Insults", "Targeted verbal harassment, name-calling, or exclusion"),
    PREDATORY("Predatory Solicitation", "Inappropriate meetups, secret requests, or coercion"),
    PROFANITY("Severe Profanity", "Vulgar or degrading language"),
    HARASSMENT("Persistent Harassment", "Intimidation, repeated hostility, or stalking"),
    THREAT("Physical Threat", "Threats of physical harm, violence, or self-harm")
}

enum class ToxicitySensitivity(val displayName: String, val threshold: Float) {
    STRICT("Strict (0.45)", 0.45f),
    BALANCED("Balanced (0.65)", 0.65f),
    LENIENT("Lenient (0.85)", 0.85f)
}

data class ToxicTextIncident(
    val id: String = java.util.UUID.randomUUID().toString(),
    val senderNumber: String = "",
    val flaggedCategory: ToxicityCategory = ToxicityCategory.BULLYING,
    val confidenceScore: Float = 0.88f,
    val obfuscatedSnippet: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val sourceApp: String = "SMS Messaging",
    val isReviewed: Boolean = false,
    val isAlertSent: Boolean = true
)

data class ToxicScannerConfig(
    val sensitivity: ToxicitySensitivity = ToxicitySensitivity.BALANCED,
    val autoQuarantineSender: Boolean = true,
    val notifyParentInstant: Boolean = true,
    val totalScannedCount: Int = 384,
    val totalFlaggedCount: Int = 12
)

// ============================================================================
// F33: SYSTEM-WIDE SAFESEARCH LOCK ENFORCEMENT MODELS
// ============================================================================

enum class SearchEngineType(
    val displayName: String,
    val hostDomain: String,
    val queryParam: String,
    val safeParam: String,
    val safeValue: String
) {
    GOOGLE("Google Search", "google.com", "q", "safe", "active"),
    BING("Microsoft Bing", "bing.com", "q", "adlt", "strict"),
    YOUTUBE("YouTube Search", "youtube.com", "search_query", "safe", "active"),
    DUCKDUCKGO("DuckDuckGo", "duckduckgo.com", "q", "kp", "1"),
    YAHOO("Yahoo Search", "search.yahoo.com", "p", "vm", "r"),
    ECOSIA("Ecosia", "ecosia.org", "q", "safesearch", "2")
}

data class SafeSearchEngineRule(
    val engine: SearchEngineType,
    val isEnforced: Boolean = true,
    val requestsIntercepted: Int = 0
)

data class SafeSearchQueryLog(
    val id: String = java.util.UUID.randomUUID().toString(),
    val query: String = "",
    val engine: SearchEngineType = SearchEngineType.GOOGLE,
    val originalUrl: String = "",
    val rewrittenUrl: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isEnforced: Boolean = true
)

data class SafeSearchConfig(
    val isTamperLockEnabled: Boolean = true,
    val blockIncognitoBrowsing: Boolean = true,
    val enforceRestrictedYouTubeMode: Boolean = true,
    val totalQueriesFiltered: Int = 1420,
    val engines: List<SafeSearchEngineRule> = listOf(
        SafeSearchEngineRule(SearchEngineType.GOOGLE, true, 892),
        SafeSearchEngineRule(SearchEngineType.BING, true, 215),
        SafeSearchEngineRule(SearchEngineType.YOUTUBE, true, 194),
        SafeSearchEngineRule(SearchEngineType.DUCKDUCKGO, true, 82),
        SafeSearchEngineRule(SearchEngineType.YAHOO, true, 37)
    )
)

// ============================================================================
// F34: LOW-BATTERY LAST PING TELEMETRY MODELS
// ============================================================================

data class LowBatteryLastPing(
    val id: String = java.util.UUID.randomUUID().toString(),
    val batteryLevel: Int = 12,
    val isCriticalShutdown: Boolean = false,
    val latitude: Double = 19.1054,
    val longitude: Double = 72.8362,
    val accuracyMeters: Float = 3.8f,
    val timestamp: Long = System.currentTimeMillis(),
    val addressName: String = "NMIMS MPSTME Building, Vile Parle West, Mumbai",
    val networkType: String = "5G SA (Jio) / Wi-Fi Connected",
    val isUploadedToCloud: Boolean = true,
    val isSmsDispatched: Boolean = true
)

data class LowBatteryConfig(
    val triggerThresholdPercent: Int = 15,
    val criticalThresholdPercent: Int = 5,
    val autoDispatchSmsToEmergencyContacts: Boolean = true,
    val enableGpsHighPowerFixOnDrain: Boolean = true,
    val totalEmergencyPingsLogged: Int = 8
)


