package com.nestcheck.app.ui.parent.chores

import androidx.lifecycle.ViewModel
import com.nestcheck.app.data.model.ChoreTask
import com.nestcheck.app.data.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ChoresViewModel @Inject constructor(
    private val habitRepository: HabitRepository
) : ViewModel() {

    val chores: StateFlow<List<ChoreTask>> = habitRepository.chores

    fun addChore(title: String, rewardCredits: Int = 10, deadline: String = "Today") {
        habitRepository.addChore(title, rewardCredits, deadline)
    }

    fun submitChore(choreId: String, note: String) {
        habitRepository.submitChore(choreId, note)
    }

    fun approveChore(choreId: String) {
        habitRepository.approveChore(choreId)
    }
}
