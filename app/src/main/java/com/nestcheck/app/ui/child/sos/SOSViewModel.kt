package com.nestcheck.app.ui.child.sos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nestcheck.app.data.model.SOSAlert
import com.nestcheck.app.data.repository.SOSRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SOSViewModel @Inject constructor(
    private val sosRepository: SOSRepository
) : ViewModel() {

    val sosState: StateFlow<SOSAlert> = sosRepository.sosState

    private val _countdownSeconds = MutableStateFlow(3)
    val countdownSeconds: StateFlow<Int> = _countdownSeconds.asStateFlow()

    private val _isHolding = MutableStateFlow(false)
    val isHolding: StateFlow<Boolean> = _isHolding.asStateFlow()

    private var countdownJob: Job? = null

    fun onStartHold() {
        if (_isHolding.value || sosState.value.isEmergencyActive) return
        _isHolding.value = true
        _countdownSeconds.value = 3

        countdownJob = viewModelScope.launch {
            for (i in 3 downTo 1) {
                _countdownSeconds.value = i
                delay(1000)
            }
            _isHolding.value = false
            sosRepository.triggerEmergency()
        }
    }

    fun onCancelHold() {
        countdownJob?.cancel()
        countdownJob = null
        _isHolding.value = false
        _countdownSeconds.value = 3
    }

    fun triggerImmediateSOS() {
        sosRepository.triggerEmergency()
    }

    fun cancelActiveEmergency() {
        sosRepository.cancelEmergency()
        _isHolding.value = false
        _countdownSeconds.value = 3
    }
}
