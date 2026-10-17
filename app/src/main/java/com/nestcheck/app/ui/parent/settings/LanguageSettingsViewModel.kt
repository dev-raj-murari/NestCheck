package com.nestcheck.app.ui.parent.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nestcheck.app.data.model.LanguageOption
import com.nestcheck.app.data.model.LocalizationConfig
import com.nestcheck.app.data.repository.LocalizationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LanguageSettingsUiState(
    val supportedLanguages: List<LanguageOption> = emptyList(),
    val config: LocalizationConfig = LocalizationConfig(),
    val previewStrings: Map<String, String> = emptyMap(),
    val toastMessage: String? = null
)

@HiltViewModel
class LanguageSettingsViewModel @Inject constructor(
    private val repository: LocalizationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        LanguageSettingsUiState(
            supportedLanguages = repository.supportedLanguages,
            config = repository.config.value,
            previewStrings = loadPreviewStrings(repository.config.value.currentLanguageCode)
        )
    )
    val uiState: StateFlow<LanguageSettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.config.collect { conf ->
                _uiState.value = _uiState.value.copy(
                    config = conf,
                    previewStrings = loadPreviewStrings(conf.currentLanguageCode)
                )
            }
        }
    }

    fun selectLanguage(langCode: String) {
        repository.setLanguage(langCode)
        val langName = repository.supportedLanguages.find { it.code == langCode }?.englishName ?: langCode
        _uiState.value = _uiState.value.copy(
            toastMessage = "Application language switched to $langName."
        )
    }

    fun toggle24Hour(enabled: Boolean) {
        repository.toggle24HourTime(enabled)
    }

    fun toggleMetric(enabled: Boolean) {
        repository.toggleMetricSystem(enabled)
    }

    fun clearToast() {
        _uiState.value = _uiState.value.copy(toastMessage = null)
    }

    private fun loadPreviewStrings(code: String): Map<String, String> {
        val keys = listOf(
            "dashboard_title", "screen_time", "bedtime_curfew", "device_lock",
            "emergency_sos", "battery_status", "family_sharing", "ai_assistant"
        )
        return keys.associateWith { repository.getString(it) }
    }
}
