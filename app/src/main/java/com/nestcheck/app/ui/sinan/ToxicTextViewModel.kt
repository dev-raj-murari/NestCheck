package com.nestcheck.app.ui.sinan

import androidx.lifecycle.ViewModel
import com.nestcheck.app.data.model.ToxicScannerConfig
import com.nestcheck.app.data.model.ToxicTextIncident
import com.nestcheck.app.data.model.ToxicitySensitivity
import com.nestcheck.app.data.repository.TextModerationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ToxicTextViewModel @Inject constructor(
    private val textModerationRepository: TextModerationRepository
) : ViewModel() {

    val config: StateFlow<ToxicScannerConfig> = textModerationRepository.config
    val incidents: StateFlow<List<ToxicTextIncident>> = textModerationRepository.incidents

    private val _simulatedAnalysisResult = MutableStateFlow<ToxicTextIncident?>(null)
    val simulatedAnalysisResult: StateFlow<ToxicTextIncident?> = _simulatedAnalysisResult.asStateFlow()

    fun updateSensitivity(sensitivity: ToxicitySensitivity) {
        textModerationRepository.updateSensitivity(sensitivity)
    }

    fun dismissIncident(id: String) {
        textModerationRepository.dismissIncident(id)
    }

    fun markReviewed(id: String) {
        textModerationRepository.markReviewed(id)
    }

    fun testMessageScan(sender: String, message: String) {
        val result = textModerationRepository.analyzeAndIngest(sender, message)
        _simulatedAnalysisResult.value = result
    }

    fun clearTestResult() {
        _simulatedAnalysisResult.value = null
    }
}
