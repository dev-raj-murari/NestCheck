package com.nestcheck.app.data.repository

import com.nestcheck.app.data.model.AgeGroupPreset
import com.nestcheck.app.data.model.AppCategory
import com.nestcheck.app.data.model.CategoryQuota
import com.nestcheck.app.data.model.ChoreStatus
import com.nestcheck.app.data.model.ChoreTask
import com.nestcheck.app.data.model.FocusSession
import com.nestcheck.app.data.model.HabitStreak
import com.nestcheck.app.data.model.WaterReminderConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HabitRepository @Inject constructor(
    private val creditRepository: CreditRepository,
    private val screenTimeRepository: ScreenTimeRepository,
    private val scheduleRepository: ScheduleRepository
) {

    // F21: Focus Mode
    private val _focusSession = MutableStateFlow(
        FocusSession(
            targetMinutes = 25,
            remainingSeconds = 25 * 60,
            isActive = false,
            sessionType = "25m Pomodoro Study",
            rewardCredits = 5
        )
    )
    val focusSession: StateFlow<FocusSession> = _focusSession.asStateFlow()

    // F22: Chores
    private val _chores = MutableStateFlow(
        listOf(
            ChoreTask("chore_1", "Clean up bedroom & desk", 10, ChoreStatus.APPROVED, "Completed before 6 PM"),
            ChoreTask("chore_2", "Water garden plants", 5, ChoreStatus.SUBMITTED, "Watered the balcony pots"),
            ChoreTask("chore_3", "Empty dishwasher & fold napkins", 10, ChoreStatus.PENDING, "")
        )
    )
    val chores: StateFlow<List<ChoreTask>> = _chores.asStateFlow()

    // F27: Category Quotas
    private val _categoryQuotas = MutableStateFlow(
        listOf(
            CategoryQuota(AppCategory.GAMING, dailyLimitMinutes = 45, usedMinutes = 45),
            CategoryQuota(AppCategory.SOCIAL, dailyLimitMinutes = 30, usedMinutes = 10),
            CategoryQuota(AppCategory.ENTERTAINMENT, dailyLimitMinutes = 60, usedMinutes = 30),
            CategoryQuota(AppCategory.EDUCATION, dailyLimitMinutes = 120, usedMinutes = 20)
        )
    )
    val categoryQuotas: StateFlow<List<CategoryQuota>> = _categoryQuotas.asStateFlow()

    // F28: Habit Streaks
    private val _habitStreaks = MutableStateFlow(
        listOf(
            HabitStreak("streak_1", "Bedtime On-Time Streak", currentStreakDays = 4, targetDays = 7, badgeName = "Sleep Champion", iconEmoji = "🌙", bonusCredits = 25),
            HabitStreak("streak_2", "Daily Homework Checkoff", currentStreakDays = 5, targetDays = 5, badgeName = "Study Master", iconEmoji = "📚", bonusCredits = 30),
            HabitStreak("streak_3", "Zero Over-Limit Violations", currentStreakDays = 3, targetDays = 7, badgeName = "Digital Wellbeing", iconEmoji = "🛡️", bonusCredits = 20)
        )
    )
    val habitStreaks: StateFlow<List<HabitStreak>> = _habitStreaks.asStateFlow()

    // F40: Water & Sleep Reminder
    private val _waterConfig = MutableStateFlow(
        WaterReminderConfig(
            glassesDrank = 5,
            dailyGoalGlasses = 8,
            reminderIntervalMins = 60,
            isHydrationAlertEnabled = true,
            windDownMinsBeforeBedtime = 30,
            isSleepWindDownEnabled = true
        )
    )
    val waterConfig: StateFlow<WaterReminderConfig> = _waterConfig.asStateFlow()

    // Focus Methods
    fun startFocusSession(minutes: Int, sessionType: String, rewardCredits: Int) {
        _focusSession.value = FocusSession(
            targetMinutes = minutes,
            remainingSeconds = minutes * 60,
            isActive = true,
            isPaused = false,
            sessionType = sessionType,
            rewardCredits = rewardCredits
        )
    }

    fun completeFocusSession() {
        val current = _focusSession.value
        creditRepository.awardCredits(current.rewardCredits, "Focus Mode Completed (${current.sessionType})")
        _focusSession.value = current.copy(isActive = false, remainingSeconds = 0)
    }

    fun stopFocusSession() {
        _focusSession.value = _focusSession.value.copy(isActive = false)
    }

    // Chore Methods
    fun addChore(title: String, rewardCredits: Int = 10, deadline: String = "Today") {
        val newChore = ChoreTask(
            id = UUID.randomUUID().toString(),
            title = title,
            rewardCredits = rewardCredits,
            status = ChoreStatus.PENDING,
            deadline = deadline
        )
        _chores.value = listOf(newChore) + _chores.value
    }

    fun submitChore(choreId: String, note: String) {
        _chores.value = _chores.value.map {
            if (it.id == choreId) it.copy(status = ChoreStatus.SUBMITTED, submissionNote = note) else it
        }
    }

    fun approveChore(choreId: String) {
        val chore = _chores.value.find { it.id == choreId } ?: return
        if (chore.status != ChoreStatus.APPROVED) {
            creditRepository.awardCredits(chore.rewardCredits, "Chore Completed: ${chore.title}")
            _chores.value = _chores.value.map {
                if (it.id == choreId) it.copy(status = ChoreStatus.APPROVED) else it
            }
        }
    }

    // F25: Apply Age Preset
    fun applyAgePreset(preset: AgeGroupPreset) {
        screenTimeRepository.updateDailyLimit(preset.dailyLimitMinutes)
        scheduleRepository.updateBedtimeSchedule(
            weekdayStart = preset.bedtime,
            weekdayEnd = "06:30",
            weekendStart = preset.bedtime,
            weekendEnd = "07:30",
            isEnforced = true
        )
        _categoryQuotas.value = _categoryQuotas.value.map {
            when (it.category) {
                AppCategory.GAMING -> it.copy(dailyLimitMinutes = preset.gamingCapMinutes)
                AppCategory.SOCIAL -> it.copy(dailyLimitMinutes = if (preset.socialAllowed) 45 else 0)
                else -> it
            }
        }
    }

    // F27: Category Quotas
    fun updateCategoryLimit(category: AppCategory, limitMinutes: Int) {
        _categoryQuotas.value = _categoryQuotas.value.map {
            if (it.category == category) it.copy(dailyLimitMinutes = limitMinutes) else it
        }
    }

    // F40: Water Log
    fun logGlassOfWater() {
        val current = _waterConfig.value
        val newCount = current.glassesDrank + 1
        _waterConfig.value = current.copy(glassesDrank = newCount)
    }
}
