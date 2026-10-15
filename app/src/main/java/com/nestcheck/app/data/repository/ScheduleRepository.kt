package com.nestcheck.app.data.repository

import com.nestcheck.app.data.model.BedtimeConfig
import com.nestcheck.app.data.model.DevicePauseState
import com.nestcheck.app.data.model.RequestStatus
import com.nestcheck.app.data.model.TimeExtensionRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScheduleRepository @Inject constructor(
    private val screenTimeRepository: ScreenTimeRepository
) {

    private val _bedtimeConfig = MutableStateFlow(
        BedtimeConfig(
            weekdayBedtimeStart = "21:00",
            weekdayBedtimeEnd = "06:30",
            weekendBedtimeStart = "22:00",
            weekendBedtimeEnd = "07:30",
            isCurfewEnforced = true
        )
    )
    val bedtimeConfig: StateFlow<BedtimeConfig> = _bedtimeConfig.asStateFlow()

    private val _pauseState = MutableStateFlow(DevicePauseState(isPaused = false))
    val pauseState: StateFlow<DevicePauseState> = _pauseState.asStateFlow()

    private val _timeRequests = MutableStateFlow(
        listOf(
            TimeExtensionRequest(
                id = "req_1",
                childUid = "child_1",
                requestedMinutes = 15,
                reason = "Need 15 more minutes to submit science homework",
                status = RequestStatus.PENDING,
                timestamp = System.currentTimeMillis() - 600000L
            )
        )
    )
    val timeRequests: StateFlow<List<TimeExtensionRequest>> = _timeRequests.asStateFlow()

    fun updateBedtimeSchedule(
        weekdayStart: String,
        weekdayEnd: String,
        weekendStart: String,
        weekendEnd: String,
        isEnforced: Boolean
    ) {
        _bedtimeConfig.value = _bedtimeConfig.value.copy(
            weekdayBedtimeStart = weekdayStart,
            weekdayBedtimeEnd = weekdayEnd,
            weekendBedtimeStart = weekendStart,
            weekendBedtimeEnd = weekendEnd,
            isCurfewEnforced = isEnforced
        )
    }

    // F24: One-Tap Device Pause
    fun pauseDevice(reason: String = "Dinnertime / Family Hour") {
        _pauseState.value = DevicePauseState(
            isPaused = true,
            reason = reason,
            pausedTimestamp = System.currentTimeMillis()
        )
    }

    fun resumeDevice() {
        _pauseState.value = DevicePauseState(isPaused = false)
    }

    // F26: "Ask for More Time" Flow
    fun submitExtensionRequest(minutes: Int, reason: String) {
        val newReq = TimeExtensionRequest(
            id = UUID.randomUUID().toString(),
            requestedMinutes = minutes,
            reason = reason,
            status = RequestStatus.PENDING,
            timestamp = System.currentTimeMillis()
        )
        _timeRequests.value = listOf(newReq) + _timeRequests.value
    }

    fun approveExtensionRequest(requestId: String) {
        val req = _timeRequests.value.find { it.id == requestId } ?: return
        if (req.status == RequestStatus.PENDING) {
            screenTimeRepository.addBonusMinutes(req.requestedMinutes)
            _timeRequests.value = _timeRequests.value.map {
                if (it.id == requestId) it.copy(status = RequestStatus.APPROVED) else it
            }
        }
    }

    fun declineExtensionRequest(requestId: String) {
        _timeRequests.value = _timeRequests.value.map {
            if (it.id == requestId) it.copy(status = RequestStatus.DECLINED) else it
        }
    }
}
