package com.nestcheck.app.data.repository

import com.nestcheck.app.data.model.AppCategory
import com.nestcheck.app.data.model.AppControlStatus
import com.nestcheck.app.data.model.AppUsageDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppControlRepository @Inject constructor() {

    private val _installedApps = MutableStateFlow(
        listOf(
            AppUsageDetail(
                packageName = "com.roblox.client",
                appName = "Roblox",
                category = AppCategory.GAMING,
                minutesUsed = 45,
                status = AppControlStatus.TIME_LIMITED,
                dailyLimitMinutes = 60
            ),
            AppUsageDetail(
                packageName = "com.google.android.youtube",
                appName = "YouTube Kids",
                category = AppCategory.ENTERTAINMENT,
                minutesUsed = 30,
                status = AppControlStatus.ALLOWED,
                dailyLimitMinutes = -1
            ),
            AppUsageDetail(
                packageName = "com.duolingo",
                appName = "Duolingo",
                category = AppCategory.EDUCATION,
                minutesUsed = 20,
                status = AppControlStatus.ALLOWED,
                dailyLimitMinutes = -1
            ),
            AppUsageDetail(
                packageName = "com.instagram.android",
                appName = "Instagram",
                category = AppCategory.SOCIAL,
                minutesUsed = 10,
                status = AppControlStatus.BLOCKED,
                dailyLimitMinutes = 0
            ),
            AppUsageDetail(
                packageName = "com.zhiliaoapp.musically",
                appName = "TikTok",
                category = AppCategory.SOCIAL,
                minutesUsed = 0,
                status = AppControlStatus.BLOCKED,
                dailyLimitMinutes = 0
            ),
            AppUsageDetail(
                packageName = "com.minecraft.world",
                appName = "Minecraft",
                category = AppCategory.GAMING,
                minutesUsed = 25,
                status = AppControlStatus.TIME_LIMITED,
                dailyLimitMinutes = 45
            ),
            AppUsageDetail(
                packageName = "com.spotify.music",
                appName = "Spotify Kids",
                category = AppCategory.ENTERTAINMENT,
                minutesUsed = 15,
                status = AppControlStatus.ALLOWED,
                dailyLimitMinutes = -1
            ),
            AppUsageDetail(
                packageName = "com.google.android.apps.classroom",
                appName = "Google Classroom",
                category = AppCategory.EDUCATION,
                minutesUsed = 35,
                status = AppControlStatus.ALLOWED,
                dailyLimitMinutes = -1
            )
        )
    )
    val installedApps: StateFlow<List<AppUsageDetail>> = _installedApps.asStateFlow()

    fun updateAppStatus(packageName: String, newStatus: AppControlStatus) {
        _installedApps.value = _installedApps.value.map { app ->
            if (app.packageName == packageName) {
                app.copy(
                    status = newStatus,
                    dailyLimitMinutes = when (newStatus) {
                        AppControlStatus.BLOCKED -> 0
                        AppControlStatus.ALLOWED -> -1
                        AppControlStatus.TIME_LIMITED -> if (app.dailyLimitMinutes > 0) app.dailyLimitMinutes else 60
                    }
                )
            } else {
                app
            }
        }
    }

    fun setAppLimit(packageName: String, limitMinutes: Int) {
        _installedApps.value = _installedApps.value.map { app ->
            if (app.packageName == packageName) {
                app.copy(
                    dailyLimitMinutes = limitMinutes,
                    status = if (limitMinutes == 0) AppControlStatus.BLOCKED else AppControlStatus.TIME_LIMITED
                )
            } else {
                app
            }
        }
    }

    fun toggleAppBlock(packageName: String) {
        _installedApps.value = _installedApps.value.map { app ->
            if (app.packageName == packageName) {
                val newStatus = if (app.status == AppControlStatus.BLOCKED) {
                    AppControlStatus.ALLOWED
                } else {
                    AppControlStatus.BLOCKED
                }
                app.copy(status = newStatus)
            } else {
                app
            }
        }
    }
}
