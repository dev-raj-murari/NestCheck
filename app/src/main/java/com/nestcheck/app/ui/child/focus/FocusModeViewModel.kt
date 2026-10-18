package com.nestcheck.app.ui.child.focus

import androidx.lifecycle.ViewModel
import com.nestcheck.app.data.model.FocusSession
import com.nestcheck.app.data.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class FocusModeViewModel @Inject constructor(
    private val habitRepository: HabitRepository
) : ViewModel() {

    val focusSession: StateFlow<FocusSession> = habitRepository.focusSession

    fun startSession(minutes: Int, sessionType: String, rewardCredits: Int) {
        habitRepository.startFocusSession(minutes, sessionType, rewardCredits)
    }

    fun completeSession() {
        habitRepository.completeFocusSession()
    }

    fun stopSession() {
        habitRepository.stopFocusSession()
    }
}
