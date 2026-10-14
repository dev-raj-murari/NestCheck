package com.nestcheck.app.ui.sinan

import androidx.lifecycle.ViewModel
import com.nestcheck.app.data.model.*
import com.nestcheck.app.data.repository.SafetyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

enum class SafetyContentTab {
    NSFW_DETECTION,
    WEB_FILTERING
}

@HiltViewModel
class SafetyViewModel @Inject constructor(
    private val safetyRepository: SafetyRepository
) : ViewModel() {

    // Tab Selection
    private val _currentTab = MutableStateFlow(SafetyContentTab.NSFW_DETECTION)
    val currentTab: StateFlow<SafetyContentTab> = _currentTab.asStateFlow()

    fun selectTab(tab: SafetyContentTab) {
        _currentTab.value = tab
    }

    // ========================================================================
    // F7: NSFW DETECTION STATE & ACTIONS
    // ========================================================================
    val scannerConfig: StateFlow<SafetyScannerConfig> = safetyRepository.scannerConfig
    val incidents: StateFlow<List<NsfwIncident>> = safetyRepository.incidents

    fun setSensitivity(level: NsfwSensitivityLevel) {
        safetyRepository.updateSensitivity(level)
    }

    fun toggleRealtimeBlur(enabled: Boolean) {
        safetyRepository.toggleRealtimeBlur(enabled)
    }

    fun toggleInstantNotification(enabled: Boolean) {
        safetyRepository.toggleInstantNotification(enabled)
    }

    fun dismissIncident(id: String) {
        safetyRepository.dismissIncident(id)
    }

    fun toggleIncidentBlur(id: String) {
        safetyRepository.toggleIncidentBlur(id)
    }

    fun triggerSimulatedScan() {
        safetyRepository.simulateMediaScan(
            source = listOf("WhatsApp", "Instagram Cache", "Chrome Downloads", "Telegram").random(),
            mediaName = "media_sample_${System.currentTimeMillis() % 1000}.jpg",
            simulatedScore = listOf(0.92f, 0.78f, 0.68f, 0.85f).random()
        )
    }

    // ========================================================================
    // F8: WEB CATEGORY FILTERING STATE & ACTIONS
    // ========================================================================
    val categoryRules: StateFlow<List<WebCategoryRule>> = safetyRepository.categoryRules
    val customDomains: StateFlow<List<CustomDomainRule>> = safetyRepository.customDomains
    val accessLogs: StateFlow<List<WebAccessLog>> = safetyRepository.accessLogs

    private val _testUrlInput = MutableStateFlow("")
    val testUrlInput: StateFlow<String> = _testUrlInput.asStateFlow()

    private val _evaluationResult = MutableStateFlow<WebAccessLog?>(null)
    val evaluationResult: StateFlow<WebAccessLog?> = _evaluationResult.asStateFlow()

    fun onTestUrlChanged(url: String) {
        _testUrlInput.value = url
    }

    fun testUrlAccess() {
        val url = _testUrlInput.value.trim()
        if (url.isNotEmpty()) {
            _evaluationResult.value = safetyRepository.evaluateUrl(url)
        }
    }

    fun clearEvaluation() {
        _evaluationResult.value = null
    }

    fun toggleCategory(category: WebCategory) {
        safetyRepository.toggleCategoryBlock(category)
    }

    fun addDomain(domain: String, isBlocked: Boolean, note: String) {
        if (domain.isNotBlank()) {
            safetyRepository.addCustomDomain(domain, isBlocked, note)
        }
    }

    fun removeDomain(id: String) {
        safetyRepository.removeCustomDomain(id)
    }
}
