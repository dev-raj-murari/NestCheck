package com.nestcheck.app.ui.sinan

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nestcheck.app.data.model.AuditedApp
import com.nestcheck.app.data.model.PermissionAuditorSummary
import com.nestcheck.app.data.model.PermissionRiskLevel
import com.nestcheck.app.data.repository.PermissionAuditorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PermissionAuditorViewModel @Inject constructor(
    private val repository: PermissionAuditorRepository
) : ViewModel() {

    val apps: StateFlow<List<AuditedApp>> = repository.apps
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val summary: StateFlow<PermissionAuditorSummary> = repository.summary
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PermissionAuditorSummary())

    private val _selectedFilter = mutableStateOf<PermissionRiskLevel?>(null)
    val selectedFilter: State<PermissionRiskLevel?> = _selectedFilter

    private val _isScanning = mutableStateOf(false)
    val isScanning: State<Boolean> = _isScanning

    fun setFilter(risk: PermissionRiskLevel?) {
        _selectedFilter.value = risk
    }

    fun runScan() {
        viewModelScope.launch {
            _isScanning.value = true
            delay(1200)
            repository.runFreshScan()
            _isScanning.value = false
        }
    }

    fun quarantine(packageName: String) {
        viewModelScope.launch {
            repository.quarantineApp(packageName)
        }
    }

    fun trust(packageName: String) {
        viewModelScope.launch {
            repository.trustApp(packageName)
        }
    }

    fun revokePermission(packageName: String, permissionKey: String) {
        viewModelScope.launch {
            repository.revokePermission(packageName, permissionKey)
        }
    }
}
