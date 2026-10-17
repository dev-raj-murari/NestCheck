package com.nestcheck.app.data.repository

import com.nestcheck.app.data.model.AuditedApp
import com.nestcheck.app.data.model.PermissionAuditorSummary
import com.nestcheck.app.data.model.PermissionRiskLevel
import com.nestcheck.app.data.model.SensitivePermission
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PermissionAuditorRepository @Inject constructor() {

    private val commonPermissions = mapOf(
        "CAMERA" to SensitivePermission("android.permission.CAMERA", "Camera Access", "Hardware Camera", PermissionRiskLevel.CRITICAL),
        "MIC" to SensitivePermission("android.permission.RECORD_AUDIO", "Microphone / Audio", "Hardware Mic", PermissionRiskLevel.CRITICAL),
        "LOCATION" to SensitivePermission("android.permission.ACCESS_FINE_LOCATION", "Precise GPS Location", "GNSS Chip", PermissionRiskLevel.ELEVATED),
        "CONTACTS" to SensitivePermission("android.permission.READ_CONTACTS", "Read Address Book", "User Directory", PermissionRiskLevel.CRITICAL),
        "SMS" to SensitivePermission("android.permission.READ_SMS", "Read SMS Messages", "Telephony Provider", PermissionRiskLevel.CRITICAL),
        "OVERLAY" to SensitivePermission("android.permission.SYSTEM_ALERT_WINDOW", "Draw Over Other Apps", "Window Manager", PermissionRiskLevel.ELEVATED),
        "STORAGE" to SensitivePermission("android.permission.READ_MEDIA_IMAGES", "Photo & Media Storage", "File System", PermissionRiskLevel.SAFE)
    )

    private val _apps = MutableStateFlow(
        listOf(
            AuditedApp(
                id = UUID.randomUUID().toString(),
                packageName = "com.bright.neon.flashlight",
                appName = "Neon Bright Flashlight HD",
                category = "Utility & Tools",
                permissionsGranted = listOf(
                    commonPermissions["CAMERA"]!!,
                    commonPermissions["MIC"]!!,
                    commonPermissions["CONTACTS"]!!,
                    commonPermissions["SMS"]!!
                ),
                overallRisk = PermissionRiskLevel.CRITICAL,
                isSystemApp = false,
                isQuarantined = false,
                lastAuditedTimestamp = System.currentTimeMillis() - 1000L * 60 * 15
            ),
            AuditedApp(
                id = UUID.randomUUID().toString(),
                packageName = "com.speedup.ramcleaner",
                appName = "Super Speed RAM Cleaner 2026",
                category = "System Booster",
                permissionsGranted = listOf(
                    commonPermissions["OVERLAY"]!!,
                    commonPermissions["LOCATION"]!!,
                    commonPermissions["SMS"]!!
                ),
                overallRisk = PermissionRiskLevel.CRITICAL,
                isSystemApp = false,
                isQuarantined = false,
                lastAuditedTimestamp = System.currentTimeMillis() - 1000L * 60 * 28
            ),
            AuditedApp(
                id = UUID.randomUUID().toString(),
                packageName = "com.candy.selfiecam",
                appName = "Candy Filter Selfie Camera",
                category = "Photography",
                permissionsGranted = listOf(
                    commonPermissions["CAMERA"]!!,
                    commonPermissions["MIC"]!!,
                    commonPermissions["LOCATION"]!!
                ),
                overallRisk = PermissionRiskLevel.ELEVATED,
                isSystemApp = false,
                isQuarantined = false,
                lastAuditedTimestamp = System.currentTimeMillis() - 1000L * 60 * 60
            ),
            AuditedApp(
                id = UUID.randomUUID().toString(),
                packageName = "com.battlechess.online",
                appName = "Global Online Battle Chess",
                category = "Gaming",
                permissionsGranted = listOf(
                    commonPermissions["LOCATION"]!!
                ),
                overallRisk = PermissionRiskLevel.ELEVATED,
                isSystemApp = false,
                isQuarantined = false,
                lastAuditedTimestamp = System.currentTimeMillis() - 1000L * 60 * 120
            ),
            AuditedApp(
                id = UUID.randomUUID().toString(),
                packageName = "org.khankids.android",
                appName = "Khan Academy Kids",
                category = "Education",
                permissionsGranted = listOf(
                    commonPermissions["MIC"]!!.copy(riskLevel = PermissionRiskLevel.SAFE)
                ),
                overallRisk = PermissionRiskLevel.SAFE,
                isSystemApp = false,
                isQuarantined = false,
                lastAuditedTimestamp = System.currentTimeMillis() - 1000L * 60 * 180
            ),
            AuditedApp(
                id = UUID.randomUUID().toString(),
                packageName = "com.duolingo.kids",
                appName = "Duolingo Language Practice",
                category = "Education",
                permissionsGranted = listOf(
                    commonPermissions["MIC"]!!.copy(riskLevel = PermissionRiskLevel.SAFE)
                ),
                overallRisk = PermissionRiskLevel.SAFE,
                isSystemApp = false,
                isQuarantined = false,
                lastAuditedTimestamp = System.currentTimeMillis() - 1000L * 60 * 240
            ),
            AuditedApp(
                id = UUID.randomUUID().toString(),
                packageName = "com.spotify.kids",
                appName = "Spotify Kids Music",
                category = "Music & Audio",
                permissionsGranted = listOf(
                    commonPermissions["STORAGE"]!!
                ),
                overallRisk = PermissionRiskLevel.SAFE,
                isSystemApp = false,
                isQuarantined = false,
                lastAuditedTimestamp = System.currentTimeMillis() - 1000L * 60 * 300
            )
        )
    )
    val apps: StateFlow<List<AuditedApp>> = _apps.asStateFlow()

    private val _summary = MutableStateFlow(
        PermissionAuditorSummary(
            totalAppsAudited = 42,
            criticalRiskAppsCount = 2,
            elevatedRiskAppsCount = 2,
            safeAppsCount = 38,
            lastScanTimestamp = System.currentTimeMillis()
        )
    )
    val summary: StateFlow<PermissionAuditorSummary> = _summary.asStateFlow()

    fun runFreshScan() {
        val current = _apps.value
        val critical = current.count { it.overallRisk == PermissionRiskLevel.CRITICAL && !it.isQuarantined }
        val elevated = current.count { it.overallRisk == PermissionRiskLevel.ELEVATED && !it.isQuarantined }
        val safe = current.size - critical - elevated

        _summary.value = PermissionAuditorSummary(
            totalAppsAudited = 42,
            criticalRiskAppsCount = critical,
            elevatedRiskAppsCount = elevated,
            safeAppsCount = 42 - critical - elevated,
            lastScanTimestamp = System.currentTimeMillis()
        )
    }

    fun quarantineApp(packageName: String) {
        _apps.value = _apps.value.map { app ->
            if (app.packageName == packageName) {
                app.copy(isQuarantined = true)
            } else app
        }
        runFreshScan()
    }

    fun trustApp(packageName: String) {
        _apps.value = _apps.value.map { app ->
            if (app.packageName == packageName) {
                app.copy(overallRisk = PermissionRiskLevel.SAFE, isQuarantined = false)
            } else app
        }
        runFreshScan()
    }

    fun revokePermission(packageName: String, permissionKey: String) {
        _apps.value = _apps.value.map { app ->
            if (app.packageName == packageName) {
                val remaining = app.permissionsGranted.filter { it.permissionKey != permissionKey }
                val newRisk = when {
                    remaining.any { it.riskLevel == PermissionRiskLevel.CRITICAL } -> PermissionRiskLevel.CRITICAL
                    remaining.any { it.riskLevel == PermissionRiskLevel.ELEVATED } -> PermissionRiskLevel.ELEVATED
                    else -> PermissionRiskLevel.SAFE
                }
                app.copy(permissionsGranted = remaining, overallRisk = newRisk)
            } else app
        }
        runFreshScan()
    }
}
