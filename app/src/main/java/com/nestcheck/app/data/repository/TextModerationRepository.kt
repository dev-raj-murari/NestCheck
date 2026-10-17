package com.nestcheck.app.data.repository

import com.nestcheck.app.data.model.ToxicScannerConfig
import com.nestcheck.app.data.model.ToxicTextIncident
import com.nestcheck.app.data.model.ToxicityCategory
import com.nestcheck.app.data.model.ToxicitySensitivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TextModerationRepository @Inject constructor() {

    // ========================================================================
    // F32: CYBERBULLYING & TOXIC TEXT DETECTION ENGINE
    // ========================================================================

    private val _config = MutableStateFlow(ToxicScannerConfig())
    val config: StateFlow<ToxicScannerConfig> = _config.asStateFlow()

    private val _incidents = MutableStateFlow<List<ToxicTextIncident>>(
        listOf(
            ToxicTextIncident(
                id = "inc_t1",
                senderNumber = "+1 (555) 892-1049",
                flaggedCategory = ToxicityCategory.BULLYING,
                confidenceScore = 0.92f,
                obfuscatedSnippet = "Hey l***r nobody likes you at school, go away",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 25,
                sourceApp = "SMS Messaging",
                isReviewed = false
            ),
            ToxicTextIncident(
                id = "inc_t2",
                senderNumber = "+1 (555) 401-9281",
                flaggedCategory = ToxicityCategory.PREDATORY,
                confidenceScore = 0.86f,
                obfuscatedSnippet = "Don't tell your mom, meet me alone behind the p**k",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 190,
                sourceApp = "WhatsApp",
                isReviewed = false
            ),
            ToxicTextIncident(
                id = "inc_t3",
                senderNumber = "+1 (555) 320-1928",
                flaggedCategory = ToxicityCategory.PROFANITY,
                confidenceScore = 0.74f,
                obfuscatedSnippet = "What the f*** is wrong with you",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 14,
                sourceApp = "Instagram DM",
                isReviewed = true
            )
        )
    )
    val incidents: StateFlow<List<ToxicTextIncident>> = _incidents.asStateFlow()

    fun updateSensitivity(sensitivity: ToxicitySensitivity) {
        _config.value = _config.value.copy(sensitivity = sensitivity)
    }

    fun dismissIncident(id: String) {
        _incidents.value = _incidents.value.filterNot { it.id == id }
    }

    fun markReviewed(id: String) {
        _incidents.value = _incidents.value.map {
            if (it.id == id) it.copy(isReviewed = true) else it
        }
    }

    fun analyzeAndIngest(
        sender: String,
        rawText: String,
        sourceApp: String = "SMS Messaging"
    ): ToxicTextIncident? {
        val lower = rawText.lowercase()
        val currentConfig = _config.value

        var category: ToxicityCategory? = null
        var score = 0.15f
        var maskedText = rawText

        when {
            lower.contains("alone") || lower.contains("secret") || lower.contains("meet me") || lower.contains("don't tell") -> {
                category = ToxicityCategory.PREDATORY
                score = 0.88f
                maskedText = rawText.replace("alone", "a***e", ignoreCase = true)
                    .replace("secret", "s****t", ignoreCase = true)
            }
            lower.contains("loser") || lower.contains("hate you") || lower.contains("ugly") || lower.contains("kill yourself") -> {
                category = ToxicityCategory.BULLYING
                score = 0.94f
                maskedText = rawText.replace("loser", "l***r", ignoreCase = true)
                    .replace("hate", "h**e", ignoreCase = true)
            }
            lower.contains("fuck") || lower.contains("shit") || lower.contains("bitch") || lower.contains("damn") -> {
                category = ToxicityCategory.PROFANITY
                score = 0.78f
                maskedText = rawText.replace("fuck", "f***k", ignoreCase = true)
                    .replace("shit", "s**t", ignoreCase = true)
                    .replace("bitch", "b***h", ignoreCase = true)
            }
            lower.contains("fight") || lower.contains("hurt") || lower.contains("beat you") -> {
                category = ToxicityCategory.THREAT
                score = 0.89f
                maskedText = rawText.replace("hurt", "h**t", ignoreCase = true)
            }
        }

        val newScanned = currentConfig.totalScannedCount + 1
        val shouldFlag = category != null && score >= currentConfig.sensitivity.threshold
        val newFlagged = if (shouldFlag) currentConfig.totalFlaggedCount + 1 else currentConfig.totalFlaggedCount

        _config.value = currentConfig.copy(
            totalScannedCount = newScanned,
            totalFlaggedCount = newFlagged
        )

        return if (shouldFlag) {
            val incident = ToxicTextIncident(
                id = UUID.randomUUID().toString(),
                senderNumber = sender.ifBlank { "+1 (555) 782-9901" },
                flaggedCategory = category,
                confidenceScore = score,
                obfuscatedSnippet = maskedText,
                timestamp = System.currentTimeMillis(),
                sourceApp = sourceApp,
                isReviewed = false,
                isAlertSent = currentConfig.notifyParentInstant
            )
            _incidents.value = listOf(incident) + _incidents.value
            incident
        } else null
    }
}
