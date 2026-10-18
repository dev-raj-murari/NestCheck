package com.nestcheck.app.data.repository

import com.nestcheck.app.data.model.AppCategory
import com.nestcheck.app.data.model.AppControlStatus
import com.nestcheck.app.data.model.AppUsageDetail
import com.nestcheck.app.data.model.DailyUsagePoint
import com.nestcheck.app.data.model.DaySchedule
import com.nestcheck.app.data.model.ScreenTimeData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScreenTimeRepository @Inject constructor() {

    private val todayString: String
        get() = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    private val _screenTimeData = MutableStateFlow(
        ScreenTimeData(
            childUid = "child_1",
            date = todayString,
            totalMinutes = 105,
            dailyLimit = 120,
            remainingMinutes = 15,
            appUsage = mapOf(
                "com.roblox.client" to 45,
                "com.google.android.youtube" to 30,
                "com.duolingo" to 20,
                "com.instagram.android" to 10
            )
        )
    )
    val screenTimeData: StateFlow<ScreenTimeData> = _screenTimeData.asStateFlow()

    private val _appUsageList = MutableStateFlow(
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
                packageName = "com.spotify.music",
                appName = "Spotify Kids",
                category = AppCategory.ENTERTAINMENT,
                minutesUsed = 0,
                status = AppControlStatus.ALLOWED,
                dailyLimitMinutes = -1
            ),
            AppUsageDetail(
                packageName = "com.google.android.apps.classroom",
                appName = "Google Classroom",
                category = AppCategory.EDUCATION,
                minutesUsed = 0,
                status = AppControlStatus.ALLOWED,
                dailyLimitMinutes = -1
            )
        )
    )
    val appUsageList: StateFlow<List<AppUsageDetail>> = _appUsageList.asStateFlow()

    private val _weeklyHistory = MutableStateFlow(
        listOf(
            DailyUsagePoint("Mon", 85),
            DailyUsagePoint("Tue", 110),
            DailyUsagePoint("Wed", 95),
            DailyUsagePoint("Thu", 130),
            DailyUsagePoint("Fri", 140),
            DailyUsagePoint("Sat", 115),
            DailyUsagePoint("Sun", 105)
        )
    )
    val weeklyHistory: StateFlow<List<DailyUsagePoint>> = _weeklyHistory.asStateFlow()

    private val _daySchedules = MutableStateFlow(
        listOf(
            DaySchedule("Sunday", 120),
            DaySchedule("Monday", 90),
            DaySchedule("Tuesday", 90),
            DaySchedule("Wednesday", 90),
            DaySchedule("Thursday", 90),
            DaySchedule("Friday", 120),
            DaySchedule("Saturday", 150)
        )
    )
    val daySchedules: StateFlow<List<DaySchedule>> = _daySchedules.asStateFlow()

    fun updateDailyLimit(newLimitMinutes: Int) {
        val current = _screenTimeData.value
        val newRemaining = (newLimitMinutes - current.totalMinutes).coerceAtLeast(0)
        _screenTimeData.value = current.copy(
            dailyLimit = newLimitMinutes,
            remainingMinutes = newRemaining
        )
    }

    fun addBonusMinutes(bonusMinutes: Int) {
        val current = _screenTimeData.value
        val newLimit = current.dailyLimit + bonusMinutes
        val newRemaining = current.remainingMinutes + bonusMinutes
        _screenTimeData.value = current.copy(
            dailyLimit = newLimit,
            remainingMinutes = newRemaining
        )
    }

    fun updateDaySchedule(dayName: String, limitMinutes: Int) {
        _daySchedules.value = _daySchedules.value.map {
            if (it.dayName.equals(dayName, ignoreCase = true)) {
                it.copy(limitMinutes = limitMinutes)
            } else {
                it
            }
        }
    }

    fun getCategoryBreakdown(): Map<AppCategory, Int> {
        val breakdown = mutableMapOf<AppCategory, Int>()
        _appUsageList.value.forEach { detail ->
            breakdown[detail.category] = (breakdown[detail.category] ?: 0) + detail.minutesUsed
        }
        return breakdown
    }
}
