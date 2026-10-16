package com.nestcheck.app.ui.child.streaks

import androidx.lifecycle.ViewModel
import com.nestcheck.app.data.model.HabitStreak
import com.nestcheck.app.data.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class HabitStreaksViewModel @Inject constructor(
    private val habitRepository: HabitRepository
) : ViewModel() {

    val streaks: StateFlow<List<HabitStreak>> = habitRepository.habitStreaks
}
