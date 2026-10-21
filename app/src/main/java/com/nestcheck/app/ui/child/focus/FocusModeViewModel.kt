package com.nestcheck.app.ui.child.focus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nestcheck.app.data.model.CreditTransactionType
import com.nestcheck.app.data.model.FocusSession
import com.nestcheck.app.data.repository.CreditScreenTimeRepository
import com.nestcheck.app.data.sync.RealtimeFamilySyncManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FocusUiState(
    val targetMinutes: Int = 25,
    val remainingSeconds: Int = 25 * 60,
    val isActive: Boolean = false,
    val isPaused: Boolean = false,
    val isCompleted: Boolean = false,
    val isClaimed: Boolean = false,
    val sessionType: String = "Pomodoro Study",
    val rewardCredits: Int = 5
) {
    val formattedTime: String
        get() {
            val hours = remainingSeconds / 3600
            val minutes = (remainingSeconds % 3600) / 60
            val seconds = remainingSeconds % 60
            return if (hours > 0) {
                String.format("%02d:%02d:%02d", hours, minutes, seconds)
            } else {
                String.format("%02d:%02d", minutes, seconds)
            }
        }
}

@HiltViewModel
class FocusModeViewModel @Inject constructor(
    private val creditRepository: CreditScreenTimeRepository,
    private val syncManager: RealtimeFamilySyncManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(FocusUiState())
    val uiState: StateFlow<FocusUiState> = _uiState.asStateFlow()

    // Backward-compatible focusSession for components expecting FocusSession model
    val focusSession: StateFlow<FocusSession>
        get() = MutableStateFlow(
            FocusSession(
                targetMinutes = _uiState.value.targetMinutes,
                remainingSeconds = _uiState.value.remainingSeconds,
                isActive = _uiState.value.isActive,
                isPaused = _uiState.value.isPaused,
                sessionType = _uiState.value.sessionType,
                rewardCredits = _uiState.value.rewardCredits
            )
        )

    private var timerJob: Job? = null

    fun startSession(minutes: Int, sessionType: String, rewardCredits: Int) {
        timerJob?.cancel()
        val totalSecs = minutes * 60
        _uiState.value = FocusUiState(
            targetMinutes = minutes,
            remainingSeconds = totalSecs,
            isActive = true,
            isPaused = false,
            isCompleted = false,
            isClaimed = false,
            sessionType = sessionType,
            rewardCredits = rewardCredits
        )

        syncManager.logChildActivity(
            event = "focus_session_started",
            details = "Started focus session: $sessionType ($minutes mins) for +$rewardCredits credits"
        )

        startTimerLoop()
    }

    fun pauseSession() {
        if (!_uiState.value.isActive || _uiState.value.isCompleted) return
        _uiState.value = _uiState.value.copy(isPaused = true)
        syncManager.logChildActivity(
            event = "focus_session_paused",
            details = "Paused focus session at ${_uiState.value.formattedTime} remaining"
        )
    }

    fun resumeSession() {
        if (!_uiState.value.isActive || !_uiState.value.isPaused || _uiState.value.isCompleted) return
        _uiState.value = _uiState.value.copy(isPaused = false)
        syncManager.logChildActivity(
            event = "focus_session_resumed",
            details = "Resumed focus session from ${_uiState.value.formattedTime} remaining"
        )
    }

    private fun startTimerLoop() {
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000L)
                val current = _uiState.value
                if (current.isActive && !current.isPaused && !current.isCompleted) {
                    val nextSecs = current.remainingSeconds - 1
                    if (nextSecs <= 0) {
                        _uiState.value = current.copy(
                            remainingSeconds = 0,
                            isCompleted = true,
                            isPaused = false
                        )
                        syncManager.logChildActivity(
                            event = "focus_session_timer_finished",
                            details = "Focus timer reached zero for ${current.sessionType}. Ready to claim ${current.rewardCredits} credits."
                        )
                        break
                    } else {
                        _uiState.value = current.copy(remainingSeconds = nextSecs)
                    }
                }
            }
        }
    }

    /**
     * Credits can ONLY be claimed when remainingSeconds == 0 and isCompleted is true.
     * Prevents duplicate claims.
     */
    fun claimCredits(): Boolean {
        val current = _uiState.value
        if (!current.isCompleted || current.remainingSeconds > 0 || current.isClaimed) {
            return false // Blocked!
        }

        _uiState.value = current.copy(isClaimed = true, isActive = false)
        timerJob?.cancel()

        // Award configured credits exactly once
        creditRepository.addCredits(
            amount = current.rewardCredits,
            type = CreditTransactionType.FOCUS_SESSION_COMPLETED,
            title = "Completed Focus Session (${current.sessionType})"
        )

        syncManager.logChildActivity(
            event = "focus_credits_claimed",
            details = "Claimed +${current.rewardCredits} credits for completing ${current.sessionType}"
        )
        return true
    }

    fun completeSession() {
        claimCredits()
    }

    fun stopSession() {
        timerJob?.cancel()
        syncManager.logChildActivity(
            event = "focus_session_cancelled",
            details = "Cancelled focus session before completion (${_uiState.value.formattedTime} left)"
        )
        _uiState.value = _uiState.value.copy(isActive = false, isPaused = false)
    }

    // Quick test helper to test timer reaching zero without waiting 25 minutes
    fun fastForwardToFinish() {
        if (_uiState.value.isActive && !_uiState.value.isCompleted) {
            _uiState.value = _uiState.value.copy(remainingSeconds = 0, isCompleted = true, isPaused = false)
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
