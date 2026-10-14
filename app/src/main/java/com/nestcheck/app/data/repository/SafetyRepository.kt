package com.nestcheck.app.data.repository

import com.nestcheck.app.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SafetyRepository @Inject constructor() {

    // ========================================================================
    // F7: ON-DEVICE NSFW DETECTION STATE
    // ========================================================================

    private val _scannerConfig = MutableStateFlow(SafetyScannerConfig())
    val scannerConfig: StateFlow<SafetyScannerConfig> = _scannerConfig.asStateFlow()

    private val _incidents = MutableStateFlow<List<NsfwIncident>>(
        listOf(
            NsfwIncident(
                id = "inc_1",
                mediaName = "received_img_9281.jpg",
                sourceApp = "WhatsApp",
                confidenceScore = 0.94f,
                timestamp = System.currentTimeMillis() - 1000 * 60 * 35,
                severity = NsfwSeverity.HIGH,
                status = IncidentStatus.BLURRED,
                isBlurred = true,
                flagReason = "High-confidence explicit nudity detected"
            ),
            NsfwIncident(
                id = "inc_2",
                mediaName = "web_download_cache_44.png",
                sourceApp = "Chrome",
                confidenceScore = 0.72f,
                timestamp = System.currentTimeMillis() - 1000 * 60 * 180,
                severity = NsfwSeverity.MEDIUM,
                status = IncidentStatus.BLURRED,
                isBlurred = true,
                flagReason = "Suggestive visual content flagged"
            ),
            NsfwIncident(
                id = "inc_3",
                mediaName = "snap_shared_0029.jpg",
                sourceApp = "Messaging",
                confidenceScore = 0.88f,
                timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 18,
                severity = NsfwSeverity.HIGH,
                status = IncidentStatus.QUARANTINED,
                isBlurred = true,
                flagReason = "Inappropriate media automatically quarantined"
            )
        )
    )
    val incidents: StateFlow<List<NsfwIncident>> = _incidents.asStateFlow()

    fun updateSensitivity(level: NsfwSensitivityLevel) {
        _scannerConfig.value = _scannerConfig.value.copy(sensitivityLevel = level)
    }

    fun toggleRealtimeBlur(enabled: Boolean) {
        _scannerConfig.value = _scannerConfig.value.copy(realtimeBlurEnabled = enabled)
    }

    fun toggleInstantNotification(enabled: Boolean) {
        _scannerConfig.value = _scannerConfig.value.copy(notifyParentInstant = enabled)
    }

    fun dismissIncident(id: String) {
        _incidents.value = _incidents.value.filterNot { it.id == id }
    }

    fun toggleIncidentBlur(id: String) {
        _incidents.value = _incidents.value.map {
            if (it.id == id) it.copy(isBlurred = !it.isBlurred) else it
        }
    }

    fun simulateMediaScan(
        source: String = "WhatsApp",
        mediaName: String = "incoming_media_${System.currentTimeMillis() % 1000}.jpg",
        simulatedScore: Float = 0.89f
    ) {
        val config = _scannerConfig.value
        val isExplicit = simulatedScore >= config.sensitivityLevel.threshold
        val currentScanned = config.totalScannedCount + 1
        val currentBlocked = if (isExplicit) config.totalBlockedCount + 1 else config.totalBlockedCount

        _scannerConfig.value = config.copy(
            totalScannedCount = currentScanned,
            totalBlockedCount = currentBlocked
        )

        if (isExplicit) {
            val severity = when {
                simulatedScore >= 0.80f -> NsfwSeverity.HIGH
                simulatedScore >= 0.60f -> NsfwSeverity.MEDIUM
                else -> NsfwSeverity.LOW
            }
            val newIncident = NsfwIncident(
                id = UUID.randomUUID().toString(),
                mediaName = mediaName,
                sourceApp = source,
                confidenceScore = simulatedScore,
                timestamp = System.currentTimeMillis(),
                severity = severity,
                status = if (config.realtimeBlurEnabled) IncidentStatus.BLURRED else IncidentStatus.QUARANTINED,
                isBlurred = config.realtimeBlurEnabled,
                flagReason = "TensorFlow Lite detector triggered (confidence ${(simulatedScore * 100).toInt()}%)"
            )
            _incidents.value = listOf(newIncident) + _incidents.value
        }
    }

    // ========================================================================
    // F8: WEB CATEGORY CONTENT FILTERING STATE
    // ========================================================================

    private val _categoryRules = MutableStateFlow<List<WebCategoryRule>>(
        listOf(
            WebCategoryRule(WebCategory.ADULT, isBlocked = true, blockCount = 14),
            WebCategoryRule(WebCategory.GAMBLING, isBlocked = true, blockCount = 6),
            WebCategoryRule(WebCategory.VIOLENCE, isBlocked = true, blockCount = 3),
            WebCategoryRule(WebCategory.WEAPONS, isBlocked = true, blockCount = 1),
            WebCategoryRule(WebCategory.PIRACY, isBlocked = true, blockCount = 9),
            WebCategoryRule(WebCategory.SOCIAL, isBlocked = false, blockCount = 0),
            WebCategoryRule(WebCategory.GAMING, isBlocked = false, blockCount = 0)
        )
    )
    val categoryRules: StateFlow<List<WebCategoryRule>> = _categoryRules.asStateFlow()

    private val _customDomains = MutableStateFlow<List<CustomDomainRule>>(
        listOf(
            CustomDomainRule("cd_1", "badsite.example.com", isBlocked = true, note = "Reported gambling domain"),
            CustomDomainRule("cd_2", "safe-school.edu", isBlocked = false, note = "School homework portal"),
            CustomDomainRule("cd_3", "distraction-arcade.net", isBlocked = true, note = "Gaming portal during school")
        )
    )
    val customDomains: StateFlow<List<CustomDomainRule>> = _customDomains.asStateFlow()

    private val _accessLogs = MutableStateFlow<List<WebAccessLog>>(
        listOf(
            WebAccessLog("log_1", "https://adult-content-hub.test", "adult-content-hub.test", WebCategory.ADULT, System.currentTimeMillis() - 1000 * 60 * 12, true, "Category 'Adult & NSFW' blocked"),
            WebAccessLog("log_2", "https://safe-school.edu/math", "safe-school.edu", WebCategory.SOCIAL, System.currentTimeMillis() - 1000 * 60 * 55, false, "Allowed via parent whitelist"),
            WebAccessLog("log_3", "https://online-casino-win.test", "online-casino-win.test", WebCategory.GAMBLING, System.currentTimeMillis() - 1000 * 60 * 110, true, "Category 'Gambling & Betting' blocked")
        )
    )
    val accessLogs: StateFlow<List<WebAccessLog>> = _accessLogs.asStateFlow()

    fun toggleCategoryBlock(category: WebCategory) {
        _categoryRules.value = _categoryRules.value.map {
            if (it.category == category) it.copy(isBlocked = !it.isBlocked) else it
        }
    }

    fun addCustomDomain(domain: String, isBlocked: Boolean, note: String = "") {
        val cleanDomain = domain.trim().lowercase().removePrefix("https://").removePrefix("http://").split("/").firstOrNull() ?: domain
        val newRule = CustomDomainRule(
            id = UUID.randomUUID().toString(),
            domain = cleanDomain,
            isBlocked = isBlocked,
            addedTimestamp = System.currentTimeMillis(),
            note = note
        )
        _customDomains.value = listOf(newRule) + _customDomains.value
    }

    fun removeCustomDomain(id: String) {
        _customDomains.value = _customDomains.value.filterNot { it.id == id }
    }

    fun evaluateUrl(inputUrl: String): WebAccessLog {
        val clean = inputUrl.trim().lowercase()
        val domain = clean.removePrefix("https://").removePrefix("http://").split("/").firstOrNull() ?: clean

        // 1. Check custom domain rules first (explicit whitelist or blacklist)
        val customRule = _customDomains.value.firstOrNull { it.domain.equals(domain, ignoreCase = true) }
        if (customRule != null) {
            val log = WebAccessLog(
                id = UUID.randomUUID().toString(),
                url = inputUrl,
                domain = domain,
                category = WebCategory.SOCIAL,
                timestamp = System.currentTimeMillis(),
                wasBlocked = customRule.isBlocked,
                blockedReason = if (customRule.isBlocked) "Domain is on parent blacklist" else "Allowed by custom parent whitelist"
            )
            _accessLogs.value = listOf(log) + _accessLogs.value
            return log
        }

        // 2. Category matching simulation
        val inferredCategory = when {
            clean.contains("adult") || clean.contains("xxx") || clean.contains("porn") || clean.contains("nsfw") -> WebCategory.ADULT
            clean.contains("casino") || clean.contains("bet") || clean.contains("poker") || clean.contains("gambl") -> WebCategory.GAMBLING
            clean.contains("gore") || clean.contains("fight") || clean.contains("weapon") || clean.contains("gun") -> WebCategory.VIOLENCE
            clean.contains("torrent") || clean.contains("pirate") || clean.contains("crack") -> WebCategory.PIRACY
            clean.contains("game") || clean.contains("arcade") || clean.contains("play") -> WebCategory.GAMING
            else -> WebCategory.SOCIAL
        }

        val rule = _categoryRules.value.firstOrNull { it.category == inferredCategory }
        val isBlocked = rule?.isBlocked ?: false

        if (isBlocked && rule != null) {
            _categoryRules.value = _categoryRules.value.map {
                if (it.category == inferredCategory) it.copy(blockCount = it.blockCount + 1) else it
            }
        }

        val log = WebAccessLog(
            id = UUID.randomUUID().toString(),
            url = inputUrl,
            domain = domain,
            category = inferredCategory,
            timestamp = System.currentTimeMillis(),
            wasBlocked = isBlocked,
            blockedReason = if (isBlocked) "Category '${inferredCategory.displayName}' is locked" else "Passed web safety verification"
        )
        _accessLogs.value = listOf(log) + _accessLogs.value
        return log
    }

    // ========================================================================
    // F9: NEW APP INSTALL APPROVAL & QUARANTINE STATE
    // ========================================================================

    private val _quarantinedApps = MutableStateFlow<List<QuarantinedApp>>(
        listOf(
            QuarantinedApp(
                id = "app_q1",
                packageName = "com.shadow.vpn.proxy",
                appName = "Shadow Turbo VPN",
                versionName = "2.4.1",
                developer = "Unknown Proxy Labs",
                installTimestamp = System.currentTimeMillis() - 1000 * 60 * 20,
                contentRating = "18+",
                permissions = listOf("android.permission.INTERNET", "android.permission.BIND_VPN_SERVICE", "android.permission.ACCESS_NETWORK_STATE"),
                status = QuarantineStatus.PENDING_APPROVAL,
                riskRating = RiskRating.HIGH_RISK,
                category = "Network / Bypass Tool"
            ),
            QuarantinedApp(
                id = "app_q2",
                packageName = "com.anonymous.secretchat",
                appName = "Whisper Anonymous Chat",
                versionName = "1.0.3",
                developer = "Incognito Media",
                installTimestamp = System.currentTimeMillis() - 1000 * 60 * 90,
                contentRating = "18+",
                permissions = listOf("android.permission.CAMERA", "android.permission.RECORD_AUDIO", "android.permission.READ_CONTACTS"),
                status = QuarantineStatus.PENDING_APPROVAL,
                riskRating = RiskRating.HIGH_RISK,
                category = "Social / Messaging"
            ),
            QuarantinedApp(
                id = "app_q3",
                packageName = "com.math.calculator.solver",
                appName = "Smart Math Solver 3D",
                versionName = "3.1.0",
                developer = "EduLearn Studio",
                installTimestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 5,
                contentRating = "Everyone",
                permissions = listOf("android.permission.INTERNET", "android.permission.CAMERA"),
                status = QuarantineStatus.APPROVED,
                riskRating = RiskRating.SAFE,
                category = "Education"
            )
        )
    )
    val quarantinedApps: StateFlow<List<QuarantinedApp>> = _quarantinedApps.asStateFlow()

    fun approveApp(id: String) {
        _quarantinedApps.value = _quarantinedApps.value.map {
            if (it.id == id) it.copy(status = QuarantineStatus.APPROVED) else it
        }
    }

    fun blockApp(id: String) {
        _quarantinedApps.value = _quarantinedApps.value.map {
            if (it.id == id) it.copy(status = QuarantineStatus.BLOCKED) else it
        }
    }

    fun simulateNewInstall(
        appName: String = "Action Battle Royale X",
        packageName: String = "com.battle.shooter.arena",
        permissions: List<String> = listOf("ACCESS_FINE_LOCATION", "RECORD_AUDIO", "CAMERA", "INTERNET"),
        contentRating: String = "16+"
    ) {
        val newApp = QuarantinedApp(
            id = UUID.randomUUID().toString(),
            packageName = packageName,
            appName = appName,
            versionName = "1.2.0",
            developer = "Apex Gaming Guild",
            installTimestamp = System.currentTimeMillis(),
            contentRating = contentRating,
            permissions = permissions,
            status = QuarantineStatus.PENDING_APPROVAL,
            riskRating = if (permissions.contains("ACCESS_FINE_LOCATION") || permissions.contains("RECORD_AUDIO")) RiskRating.HIGH_RISK else RiskRating.MODERATE,
            category = "Gaming"
        )
        _quarantinedApps.value = listOf(newApp) + _quarantinedApps.value
    }
}
