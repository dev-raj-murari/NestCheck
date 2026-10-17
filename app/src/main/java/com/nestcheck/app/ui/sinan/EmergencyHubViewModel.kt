package com.nestcheck.app.ui.sinan

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nestcheck.app.data.model.EmergencyDispatchRecord
import com.nestcheck.app.data.model.EmergencyHubConfig
import com.nestcheck.app.data.model.FastDialEmergencyContact
import com.nestcheck.app.data.repository.EmergencyHubRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmergencyHubViewModel @Inject constructor(
    private val repository: EmergencyHubRepository
) : ViewModel() {

    val contacts: StateFlow<List<FastDialEmergencyContact>> = repository.contacts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dispatchLogs: StateFlow<List<EmergencyDispatchRecord>> = repository.dispatchLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val config: StateFlow<EmergencyHubConfig> = repository.config
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), EmergencyHubConfig())

    private val _countdownContact = mutableStateOf<FastDialEmergencyContact?>(null)
    val countdownContact: State<FastDialEmergencyContact?> = _countdownContact

    private val _countdownSeconds = mutableStateOf(3)
    val countdownSeconds: State<Int> = _countdownSeconds

    private val _lastDispatched = mutableStateOf<EmergencyDispatchRecord?>(null)
    val lastDispatched: State<EmergencyDispatchRecord?> = _lastDispatched

    private var countdownJob: Job? = null

    fun initiateCall(contact: FastDialEmergencyContact) {
        val cfg = config.value
        if (cfg.isCountdownEnabled) {
            _countdownContact.value = contact
            _countdownSeconds.value = cfg.countdownSeconds
            countdownJob?.cancel()
            countdownJob = viewModelScope.launch {
                for (i in cfg.countdownSeconds downTo 1) {
                    _countdownSeconds.value = i
                    delay(1000)
                }
                executeDial(contact)
                _countdownContact.value = null
            }
        } else {
            executeDial(contact)
        }
    }

    fun cancelCountdown() {
        countdownJob?.cancel()
        _countdownContact.value = null
    }

    fun executeDial(contact: FastDialEmergencyContact) {
        val record = repository.dispatchCall(contact)
        _lastDispatched.value = record
    }

    fun broadcastSosAll() {
        viewModelScope.launch {
            val records = repository.broadcastSosToAll()
            _lastDispatched.value = records.firstOrNull()
        }
    }

    fun toggleCountdown(enabled: Boolean) {
        viewModelScope.launch {
            repository.toggleCountdown(enabled)
        }
    }

    fun toggleAutoLocation(enabled: Boolean) {
        viewModelScope.launch {
            repository.toggleAutoLocationSms(enabled)
        }
    }
}
