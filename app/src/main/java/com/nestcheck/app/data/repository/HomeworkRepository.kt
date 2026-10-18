package com.nestcheck.app.data.repository

import com.nestcheck.app.data.model.HomeworkTask
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeworkRepository @Inject constructor(
    private val creditRepository: CreditRepository
) {

    private val _homeworkTasks = MutableStateFlow(
        listOf(
            HomeworkTask(
                id = "hw_1",
                childUid = "child_1",
                title = "Solve Exercise 4.2 Fractions",
                subject = "Mathematics",
                dueDate = "Today 5:00 PM",
                creditReward = 6,
                isCompleted = true,
                isVerifiedByParent = false,
                notes = "Pages 45-48 completed in workbook",
                createdAt = System.currentTimeMillis() - 86400000L
            ),
            HomeworkTask(
                id = "hw_2",
                childUid = "child_1",
                title = "Read Chapter 3 Solar System",
                subject = "Science",
                dueDate = "Tomorrow",
                creditReward = 6,
                isCompleted = false,
                isVerifiedByParent = false,
                createdAt = System.currentTimeMillis() - 43200000L
            ),
            HomeworkTask(
                id = "hw_3",
                childUid = "child_1",
                title = "Grammar Essay - My Role Model",
                subject = "English",
                dueDate = "Friday",
                creditReward = 8,
                isCompleted = false,
                isVerifiedByParent = false,
                createdAt = System.currentTimeMillis() - 21600000L
            )
        )
    )
    val homeworkTasks: StateFlow<List<HomeworkTask>> = _homeworkTasks.asStateFlow()

    fun addTask(title: String, subject: String, dueDate: String, rewardCredits: Int = 6) {
        val newTask = HomeworkTask(
            id = UUID.randomUUID().toString(),
            childUid = "child_1",
            title = title,
            subject = subject,
            dueDate = dueDate,
            creditReward = rewardCredits,
            createdAt = System.currentTimeMillis()
        )
        _homeworkTasks.value = listOf(newTask) + _homeworkTasks.value
    }

    fun submitTaskCompletion(taskId: String, notes: String = "") {
        _homeworkTasks.value = _homeworkTasks.value.map { task ->
            if (task.id == taskId) {
                task.copy(
                    isCompleted = true,
                    notes = notes,
                    completedAt = System.currentTimeMillis()
                )
            } else {
                task
            }
        }
    }

    fun verifyAndAwardCredits(taskId: String) {
        val task = _homeworkTasks.value.find { it.id == taskId } ?: return
        if (!task.bonusCreditAwarded) {
            creditRepository.awardCredits(task.creditReward, "Verified Homework: ${task.title}")
            _homeworkTasks.value = _homeworkTasks.value.map {
                if (it.id == taskId) {
                    it.copy(
                        isVerifiedByParent = true,
                        bonusCreditAwarded = true
                    )
                } else {
                    it
                }
            }
        }
    }

    fun requestRedo(taskId: String) {
        _homeworkTasks.value = _homeworkTasks.value.map {
            if (it.id == taskId) {
                it.copy(
                    isCompleted = false,
                    isVerifiedByParent = false,
                    notes = "Redo requested by parent"
                )
            } else {
                it
            }
        }
    }
}
