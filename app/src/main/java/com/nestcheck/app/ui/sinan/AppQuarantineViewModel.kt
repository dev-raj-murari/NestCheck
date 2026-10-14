package com.nestcheck.app.ui.sinan

import androidx.lifecycle.ViewModel
import com.nestcheck.app.data.model.QuarantineStatus
import com.nestcheck.app.data.model.QuarantinedApp
import com.nestcheck.app.data.repository.SafetyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import androidx.lifecycle.viewModelScope
import javax.inject.Inject

enum class QuarantineFilter(val label: String) {
    ALL("All Packages"),
    PENDING("Pending Review"),
    APPROVED("Approved"),
    BLOCKED("Blocked / Restricted")
}

@HiltViewModel
class AppQuarantineViewModel @Inject constructor(
    private val safetyRepository: SafetyRepository
) : ViewModel() {

    private val _selectedFilter = MutableStateFlow(QuarantineFilter.ALL)
    val selectedFilter: StateFlow<QuarantineFilter> = _selectedFilter.asStateFlow()

    val quarantinedApps: StateFlow<List<QuarantinedApp>> = combine(
        safetyRepository.quarantinedApps,
        _selectedFilter
    ) { apps, filter ->
        when (filter) {
            QuarantineFilter.ALL -> apps
            QuarantineFilter.PENDING -> apps.filter { it.status == QuarantineStatus.PENDING_APPROVAL }
            QuarantineFilter.APPROVED -> apps.filter { it.status == QuarantineStatus.APPROVED }
            QuarantineFilter.BLOCKED -> apps.filter { it.status == QuarantineStatus.BLOCKED }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectFilter(filter: QuarantineFilter) {
        _selectedFilter.value = filter
    }

    fun approveApp(id: String) {
        safetyRepository.approveApp(id)
    }

    fun blockApp(id: String) {
        safetyRepository.blockApp(id)
    }

    fun simulateIncomingApp() {
        val samples = listOf(
            Triple("Apex Legends Mobile", "com.ea.gp.apexlegends", listOf("CAMERA", "RECORD_AUDIO", "INTERNET")),
            Triple("TikTok Lite", "com.zhiliaoapp.musically.go", listOf("CAMERA", "RECORD_AUDIO", "READ_CONTACTS", "ACCESS_FINE_LOCATION")),
            Triple("Tor Browser for Android", "org.torproject.torbrowser", listOf("INTERNET", "ACCESS_NETWORK_STATE")),
            Triple("Roblox Studio", "com.roblox.client", listOf("INTERNET", "RECORD_AUDIO"))
        )
        val selected = samples.random()
        safetyRepository.simulateNewInstall(
            appName = selected.first,
            packageName = selected.second,
            permissions = selected.third,
            contentRating = "16+"
        )
    }
}
