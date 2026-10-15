package com.nestcheck.app.ui.parent.homework

import androidx.lifecycle.ViewModel
import com.nestcheck.app.data.model.HomeworkTask
import com.nestcheck.app.data.repository.HomeworkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class HomeworkViewModel @Inject constructor(
    private val homeworkRepository: HomeworkRepository
) : ViewModel() {

    val tasks: StateFlow<List<HomeworkTask>> = homeworkRepository.homeworkTasks

    fun addTask(title: String, subject: String, dueDate: String, rewardCredits: Int = 6) {
        homeworkRepository.addTask(title, subject, dueDate, rewardCredits)
    }

    fun verifyAndAward(taskId: String) {
        homeworkRepository.verifyAndAwardCredits(taskId)
    }

    fun requestRedo(taskId: String) {
        homeworkRepository.requestRedo(taskId)
    }

    fun submitTask(taskId: String, notes: String) {
        homeworkRepository.submitTaskCompletion(taskId, notes)
    }
}
