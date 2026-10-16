package com.nestcheck.app.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

data class AppUsageStat(
    val appName: String,
    val category: String,
    val minutesSpent: Int,
    val percentageOfTotal: Float
)

data class WeeklyActivityReport(
    val reportTitle: String = "Weekly Telemetry Report",
    val dateRange: String = "Oct 08 - Oct 14, 2026",
    val wellnessGrade: String = "A (Balanced)",
    val totalScreenTimeHours: Float = 14.5f,
    val dailyAverageScreenTimeHours: Float = 2.1f,
    val totalSteps: Int = 42300,
    val dailyAverageSteps: Int = 6042,
    val safetyAlertsResolved: Int = 2,
    val homeworkTasksApproved: Int = 8,
    val mostUsedApps: List<AppUsageStat> = listOf(
        AppUsageStat("Duolingo", "Education", 180, 0.35f),
        AppUsageStat("Minecraft", "Gaming", 120, 0.23f),
        AppUsageStat("Khan Academy", "Education", 90, 0.17f),
        AppUsageStat("YouTube Kids", "Streaming", 75, 0.14f),
        AppUsageStat("Spotify", "Music", 55, 0.11f)
    )
)

@Singleton
class ReportRepository @Inject constructor() {

    private val _weeklyReport = MutableStateFlow(WeeklyActivityReport())
    val weeklyReport: StateFlow<WeeklyActivityReport> = _weeklyReport.asStateFlow()

    fun refreshReport() {
        _weeklyReport.value = _weeklyReport.value.copy(
            dateRange = "Oct 10 - Oct 16, 2026"
        )
    }
}
