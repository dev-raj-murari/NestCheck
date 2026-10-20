package com.nestcheck.app.ui.parent.lock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nestcheck.app.data.model.DeviceLockState
import com.nestcheck.app.data.repository.DeviceLockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RemoteLockUiState(
    val selectedChildId: String = "child_1",
    val selectedChildName: String = "Leo",
    val lockState: DeviceLockState? = null,
    val selectedDurationMinutes: Int? = 30,
    val customMessage: String = "Dinner time! Device paused until we finish eating.",
    val allowEmergencyCalls: Boolean = true,
    val isSendingCommand: Boolean = false,
    val feedbackMessage: String? = null
)

@HiltViewModel
class RemoteDeviceLockViewModel @Inject constructor(
    private val repository: DeviceLockRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RemoteLockUiState())
    val uiState: StateFlow<RemoteLockUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.lockStates.collect { states ->
                val current = states[_uiState.value.selectedChildId]
                _uiState.value = _uiState.value.copy(lockState = current)
            }
        }
    }

    fun selectChild(childId: String, childName: String) {
        val currentLock = repository.lockStates.value[childId]
        _uiState.value = _uiState.value.copy(
            selectedChildId = childId,
            selectedChildName = childName,
            lockState = currentLock
        )
    }

    fun setDuration(minutes: Int?) {
        _uiState.value = _uiState.value.copy(selectedDurationMinutes = minutes)
    }

    fun setCustomMessage(msg: String) {
        _uiState.value = _uiState.value.copy(customMessage = msg)
    }

    fun toggleEmergencyCalls(allow: Boolean) {
        _uiState.value = _uiState.value.copy(allowEmergencyCalls = allow)
    }

    fun lockCurrentDevice() {
        val s = _uiState.value
        repository.lockDevice(
            childId = s.selectedChildId,
            childName = s.selectedChildName,
            durationMinutes = s.selectedDurationMinutes,
            customMessage = s.customMessage,
            emergencyCallsAllowed = s.allowEmergencyCalls,
            reason = "Manual Parent Command"
        )
        _uiState.value = _uiState.value.copy(
            feedbackMessage = "Remote lock command sent to ${s.selectedChildName}'s device."
        )
    }

    fun unlockCurrentDevice() {
        val s = _uiState.value
        repository.unlockDevice(s.selectedChildId)
        _uiState.value = _uiState.value.copy(
            feedbackMessage = "${s.selectedChildName}'s device has been unlocked."
        )
    }

    fun clearFeedback() {
        _uiState.value = _uiState.value.copy(feedbackMessage = null)
    }

    fun formatUnlockTime(unlockAt: Long?): String {
        return repository.getFormattedUnlockTime(unlockAt)
    }
}
