package com.nestcheck.app.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

data class AIDigest(
    val title: String = "NestCheck AI Daily Telemetry Synthesis",
    val generatedDate: String = "Today, Oct 16",
    val executiveSummary: String = "Alex had a balanced and healthy day. Educational apps accounted for 65% of screen time. Physical activity exceeded the target goal by 15%, and emotional mood check-ins showed positive sentiment.",
    val wellbeingScore: Int = 92,
    val screenTimeAnalysis: String = "2h 10m total usage. Screen time remained 35 minutes below the configured daily limit. No late-night curfew violations detected.",
    val physicalActivityAnalysis: String = "9,240 steps recorded via hardware accelerometer. 4.8 km traversed with 280 kcal burned during school sports period.",
    val safetyIncidentAnalysis: String = "Zero safety policy breaches. 1 unapproved adult domain was blocked cleanly by Web Filter with zero exposure.",
    val aiRecommendations: List<String> = listOf(
        "Screen time balance is optimal; consider awarding +25 bonus chore credits for consistent curfew compliance.",
        "Hydration was steady, but encourage a 10-minute eye break after study sessions."
    )
)

@Singleton
class AIDigestRepository @Inject constructor() {

    private val _digest = MutableStateFlow(AIDigest())
    val digest: StateFlow<AIDigest> = _digest.asStateFlow()

    fun regenerateDigest() {
        _digest.value = _digest.value.copy(
            wellbeingScore = 95,
            executiveSummary = "Updated On-Device AI Assessment: All parameters optimal. Step goals fully accomplished and device locked safely on bedtime schedule."
        )
    }
}
