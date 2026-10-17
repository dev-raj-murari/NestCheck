package com.nestcheck.app.data.repository

import com.nestcheck.app.data.model.DeviceLockState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceLockRepository @Inject constructor() {

    private val _lockStates = MutableStateFlow<Map<String, DeviceLockState>>(
        mapOf(
            "child_1" to DeviceLockState(
                childId = "child_1",
                childName = "Leo Murari",
                isLocked = false,
                lockReason = "None",
                customMessage = "Device paused by parent for dinner.",
                emergencyCallsAllowed = true
            ),
            "child_2" to DeviceLockState(
                childId = "child_2",
                childName = "Maya Murari",
                isLocked = true,
                lockReason = "Bedtime Curfew Enforcement",
                customMessage = "Time to wind down and sleep! Device locked until 06:30 AM.",
                lockedAt = System.currentTimeMillis() - 1000 * 60 * 45,
                unlockAt = System.currentTimeMillis() + 1000 * 60 * 360,
                durationMinutes = 480,
                emergencyCallsAllowed = true
            )
        )
    )
    val lockStates: StateFlow<Map<String, DeviceLockState>> = _lockStates.asStateFlow()

    fun lockDevice(
        childId: String,
        childName: String,
        durationMinutes: Int?,
        customMessage: String,
        emergencyCallsAllowed: Boolean,
        reason: String = "Remote Manual Lock"
    ) {
        val now = System.currentTimeMillis()
        val unlockAt = durationMinutes?.let { now + it * 60 * 1000L }

        val updated = _lockStates.value.toMutableMap()
        updated[childId] = DeviceLockState(
            childId = childId,
            childName = childName,
            isLocked = true,
            lockReason = reason,
            customMessage = customMessage.ifBlank { "Device locked by parent." },
            lockedAt = now,
            unlockAt = unlockAt,
            durationMinutes = durationMinutes,
            emergencyCallsAllowed = emergencyCallsAllowed
        )
        _lockStates.value = updated
    }

    fun unlockDevice(childId: String) {
        val current = _lockStates.value[childId] ?: return
        val updated = _lockStates.value.toMutableMap()
        updated[childId] = current.copy(
            isLocked = false,
            lockedAt = null,
            unlockAt = null,
            durationMinutes = null
        )
        _lockStates.value = updated
    }

    fun getFormattedUnlockTime(unlockAt: Long?): String {
        if (unlockAt == null) return "Until Manually Resumed"
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return sdf.format(Date(unlockAt))
    }
}
