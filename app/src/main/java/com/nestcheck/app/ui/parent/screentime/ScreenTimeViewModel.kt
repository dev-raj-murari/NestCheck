package com.nestcheck.app.ui.parent.screentime

import androidx.lifecycle.ViewModel
import com.nestcheck.app.data.model.AppUsageDetail
import com.nestcheck.app.data.model.DailyUsagePoint
import com.nestcheck.app.data.model.ScreenTimeData
import com.nestcheck.app.data.repository.ScreenTimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

enum class TimeRangeFilter {
    TODAY, WEEK, MONTH
}

@HiltViewModel
class ScreenTimeViewModel @Inject constructor(
    private val screenTimeRepository: ScreenTimeRepository
) : ViewModel() {

    val screenTimeData: StateFlow<ScreenTimeData> = screenTimeRepository.screenTimeData
    val appUsageList: StateFlow<List<AppUsageDetail>> = screenTimeRepository.appUsageList
    val weeklyHistory: StateFlow<List<DailyUsagePoint>> = screenTimeRepository.weeklyHistory

    fun updateDailyLimit(newLimitMinutes: Int) {
        screenTimeRepository.updateDailyLimit(newLimitMinutes)
    }

    fun addBonusMinutes(bonusMinutes: Int) {
        screenTimeRepository.addBonusMinutes(bonusMinutes)
    }
}
