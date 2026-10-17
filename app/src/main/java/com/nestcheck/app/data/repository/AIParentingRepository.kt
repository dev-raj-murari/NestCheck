package com.nestcheck.app.data.repository

import com.nestcheck.app.data.model.AiParentingMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AIParentingRepository @Inject constructor() {

    private val _messages = MutableStateFlow<List<AiParentingMessage>>(
        listOf(
            AiParentingMessage(
                id = "init_1",
                text = "Hello Dev Raj! I am your NestCheck AI Parenting Assistant. I synthesize real-time device telemetry with child developmental psychology to help you navigate screen time, digital safety, and healthy family boundaries.\n\nHow can I support your household today?",
                isFromUser = false,
                timestamp = System.currentTimeMillis() - 1000 * 60 * 10,
                category = "Welcome",
                actionPoints = listOf(
                    "Ask for transition strategies when screentime ends",
                    "Request advice on age-appropriate social media guidelines",
                    "Get tips on fostering intrinsic study habits"
                ),
                suggestedFollowUps = listOf(
                    "How to handle screentime bedtime resistance?",
                    "What daily limit is recommended for a 10-year-old?",
                    "How to praise chores without just giving screen tokens?"
                )
            )
        )
    )
    val messages: StateFlow<List<AiParentingMessage>> = _messages.asStateFlow()

    fun sendMessage(userText: String) {
        val userMsg = AiParentingMessage(
            id = UUID.randomUUID().toString(),
            text = userText.trim(),
            isFromUser = true,
            timestamp = System.currentTimeMillis()
        )
        _messages.value = _messages.value + userMsg

        // Generate context-aware AI response based on keyword heuristics
        val aiResponse = generateAdvisorResponse(userText)
        _messages.value = _messages.value + aiResponse
    }

    private fun generateAdvisorResponse(query: String): AiParentingMessage {
        val lower = query.lowercase()

        return when {
            lower.contains("bedtime") || lower.contains("sleep") || lower.contains("night") -> {
                AiParentingMessage(
                    id = UUID.randomUUID().toString(),
                    text = "Screen blue light delays melatonin secretion by up to 90 minutes in adolescents. For Maya and Leo, the most effective approach is a 'Predictable Digital Sunset' rather than an abrupt shutdown.",
                    isFromUser = false,
                    category = "Sleep Hygiene & Circadian Health",
                    actionPoints = listOf(
                        "Enforce a 45-minute zero-screen buffer before target lights-out (e.g., 08:30 PM for 09:15 PM sleep).",
                        "Use the NestCheck Bedtime Curfew (F6) with the dim warm amber lock screen.",
                        "Substitute evening scrolling with a 15-minute physical book or family recap."
                    ),
                    suggestedFollowUps = listOf(
                        "Should devices charge inside or outside bedrooms?",
                        "How to handle homework that requires screens late at night?"
                    )
                )
            }
            lower.contains("limit") || lower.contains("hours") || lower.contains("how much") -> {
                AiParentingMessage(
                    id = UUID.randomUUID().toString(),
                    text = "According to the American Academy of Pediatrics (AAP), recommended recreational screen time balances are:\n• Ages 5–8: 1.0 to 1.5 hours/day\n• Ages 9–12: 1.5 to 2.0 hours/day\n• Teens 13+: 2.0 to 3.0 hours/day with mandatory physical & study breaks.",
                    isFromUser = false,
                    category = "Screen Quota Strategy",
                    actionPoints = listOf(
                        "Leo (Age 10): Keep recreational apps capped at 2h daily, separating educational apps.",
                        "Maya (Age 14): Involve her in co-setting app category quotas (F27) to build autonomy.",
                        "Use 'Bonus Time' earned through chores or reading rather than default open allowances."
                    ),
                    suggestedFollowUps = listOf(
                        "How do I set category quotas for TikTok vs Duolingo?",
                        "What is a reasonable weekend screen time allowance?"
                    )
                )
            }
            lower.contains("tantrum") || lower.contains("upset") || lower.contains("angry") || lower.contains("resist") -> {
                AiParentingMessage(
                    id = UUID.randomUUID().toString(),
                    text = "Sudden device stops trigger an abrupt dopamine drop, which triggers emotional fight-or-flight in children. The secret is the '5-Minute Transition Bridge'.",
                    isFromUser = false,
                    category = "Emotional Regulation & Transitions",
                    actionPoints = listOf(
                        "Give an explicit verbal preview: '5 minutes left in this level, Leo! Next is dinner.'",
                        "Acknowledge the fun: 'It looks like you were building something cool in Minecraft!'",
                        "Enable 'Ask for +15 mins' (F26) so they learn to negotiate respectfully instead of protesting."
                    ),
                    suggestedFollowUps = listOf(
                        "How to react if they refuse to put the tablet down?",
                        "How do credits help motivate calm transitions?"
                    )
                )
            }
            lower.contains("chore") || lower.contains("homework") || lower.contains("reward") || lower.contains("credit") -> {
                AiParentingMessage(
                    id = UUID.randomUUID().toString(),
                    text = "Gamifying household contributions using token economies (like NestCheck F4 Credits) builds executive function and work-life balance habits early.",
                    isFromUser = false,
                    category = "Positive Reinforcement & Habits",
                    actionPoints = listOf(
                        "Attach clear credit values: Cleaning room (+15 credits), Math homework (+25 credits).",
                        "Ensure the Reward Shop (F23) includes non-screen rewards (e.g., Choosing Friday dinner: 80 credits).",
                        "Never deduct earned chore credits for bad behavior; keep chore earnings sacred."
                    ),
                    suggestedFollowUps = listOf(
                        "What are great non-screen privilege rewards?",
                        "How to set recurring chores for weekdays?"
                    )
                )
            }
            else -> {
                AiParentingMessage(
                    id = UUID.randomUUID().toString(),
                    text = "That is a great parenting consideration. Consistency and clear mutual expectations produce the healthiest digital habits. When children understand *why* boundaries exist (sleep, eye health, physical activity), compliance increases by over 70%.",
                    isFromUser = false,
                    category = "Mindful Digital Parenting",
                    actionPoints = listOf(
                        "Review weekly activity telemetry (F17) together every Sunday as a family check-in.",
                        "Praise proactive compliance when devices are docked on time without prompting.",
                        "Lead by example: establish family screen-free meal zones."
                    ),
                    suggestedFollowUps = listOf(
                        "How to handle screentime bedtime resistance?",
                        "What daily limit is recommended for a 10-year-old?",
                        "How to praise chores without just giving screen tokens?"
                    )
                )
            }
        }
    }
}
