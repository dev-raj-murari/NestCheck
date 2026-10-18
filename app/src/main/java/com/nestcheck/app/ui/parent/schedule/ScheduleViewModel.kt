package com.nestcheck.app.ui.parent.schedule

import androidx.lifecycle.ViewModel
import com.nestcheck.app.data.model.BedtimeConfig
import com.nestcheck.app.data.model.DevicePauseState
import com.nestcheck.app.data.model.TimeExtensionRequest
import com.nestcheck.app.data.repository.ScheduleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val scheduleRepository: ScheduleRepository
) : ViewModel() {

    val bedtimeConfig: StateFlow<BedtimeConfig> = scheduleRepository.bedtimeConfig
    val pauseState: StateFlow<DevicePauseState> = scheduleRepository.pauseState
    val timeRequests: StateFlow<List<TimeExtensionRequest>> = scheduleRepository.timeRequests

    fun updateSchedule(
        weekdayStart: String,
        weekdayEnd: String,
        weekendStart: String,
        weekendEnd: String,
        isEnforced: Boolean
    ) {
        scheduleRepository.updateBedtimeSchedule(
            weekdayStart, weekdayEnd, weekendStart, weekendEnd, isEnforced
        )
    }

    fun pauseDevice(reason: String = "Dinnertime / Family Hour") {
        scheduleRepository.pauseDevice(reason)
    }

    fun resumeDevice() {
        scheduleRepository.resumeDevice()
    }

    fun approveTimeRequest(requestId: String) {
        scheduleRepository.approveExtensionRequest(requestId)
    }

    fun declineTimeRequest(requestId: String) {
        scheduleRepository.declineExtensionRequest(requestId)
    }

    fun submitExtensionRequest(minutes: Int, reason: String) {
        scheduleRepository.submitExtensionRequest(minutes, reason)
    }
}
