package com.nestcheck.app.data.repository

import com.nestcheck.app.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChildDashboardRepository @Inject constructor(
    private val creditRepository: CreditScreenTimeRepository
) {

    private val _pendingTasks = MutableStateFlow(
        listOf(
            PendingTaskItem(
                id = "task_1",
                title = "Clean Up Bedroom & Study Desk",
                category = TaskCategory.CHORE,
                creditsReward = 30, // 15 mins screen time
                isCompleted = false,
                approvalStatus = TaskApprovalStatus.NOT_COMPLETED,
                dueDate = "Today, 5:30 PM"
            ),
            PendingTaskItem(
                id = "task_2",
                title = "Complete Algebra Worksheet (Ch 4)",
                category = TaskCategory.STUDY,
                creditsReward = 40, // 20 mins screen time
                isCompleted = false,
                approvalStatus = TaskApprovalStatus.NOT_COMPLETED,
                dueDate = "Today, 7:00 PM"
            ),
            PendingTaskItem(
                id = "task_3",
                title = "Read 20 Pages of Science Book",
                category = TaskCategory.READING,
                creditsReward = 20, // 10 mins screen time
                isCompleted = false,
                approvalStatus = TaskApprovalStatus.NOT_COMPLETED,
                dueDate = "Tonight, 8:30 PM"
            ),
            PendingTaskItem(
                id = "task_4",
                title = "Drink 4 Glasses of Water & Stretch",
                category = TaskCategory.WELLBEING,
                creditsReward = 10, // 5 mins screen time
                isCompleted = true,
                approvalStatus = TaskApprovalStatus.APPROVED_AND_CREDITED,
                dueDate = "Completed"
            )
        )
    )
    val pendingTasks: StateFlow<List<PendingTaskItem>> = _pendingTasks.asStateFlow()

    private val _upcomingExams = MutableStateFlow(
        listOf(
            UpcomingExam(
                id = "exam_1",
                subject = "Mathematics",
                examTitle = "Midterm: Linear Equations & Geometry",
                examDate = "Friday, Oct 24",
                daysRemaining = 4,
                syllabusTopics = listOf("Linear Equations in 2 Variables", "Pythagoras Theorem", "Coordinate Geometry"),
                targetGrade = "A+"
            ),
            UpcomingExam(
                id = "exam_2",
                subject = "Science",
                examTitle = "Physics & Chemistry Unit Test",
                examDate = "Tuesday, Oct 28",
                daysRemaining = 8,
                syllabusTopics = listOf("Laws of Motion", "Chemical Bonding & Valency", "Energy Conservation"),
                targetGrade = "A"
            ),
            UpcomingExam(
                id = "exam_3",
                subject = "English Literature",
                examTitle = "Poetry Comprehension & Grammar",
                examDate = "Monday, Nov 3",
                daysRemaining = 14,
                syllabusTopics = listOf("Essay Writing", "Tenses & Voice", "Reading Passages"),
                targetGrade = "A+"
            )
        )
    )
    val upcomingExams: StateFlow<List<UpcomingExam>> = _upcomingExams.asStateFlow()

    private val _appUsageStats = MutableStateFlow(
        listOf(
            AppUsageInfo("com.google.android.youtube.kids", "YouTube Kids", "Education & Media", 28, 45, false),
            AppUsageInfo("com.duolingo", "Duolingo Languages", "Education", 22, 60, false),
            AppUsageInfo("org.khanacademy.android", "Khan Academy", "Learning", 18, 60, false),
            AppUsageInfo("com.zhiliaoapp.musically", "TikTok", "Social Media", 0, 0, true),
            AppUsageInfo("com.instagram.android", "Instagram", "Social Media", 0, 0, true),
            AppUsageInfo("com.roblox.client", "Roblox", "Gaming", 0, 0, true)
        )
    )
    val appUsageStats: StateFlow<List<AppUsageInfo>> = _appUsageStats.asStateFlow()

    private val _calendarEvents = MutableStateFlow(
        listOf(
            CalendarEventItem(UUID.randomUUID().toString(), "Math Midterm Exam", CalendarEventType.EXAM, "9:00 AM - 11:30 AM", "Fri", 24, true),
            CalendarEventItem(UUID.randomUUID().toString(), "Science Revision Block", CalendarEventType.STUDY_SESSION, "4:00 PM - 5:30 PM", "Mon", 20, false),
            CalendarEventItem(UUID.randomUUID().toString(), "Homework Submission", CalendarEventType.HOMEWORK, "6:00 PM", "Tue", 21, false),
            CalendarEventItem(UUID.randomUUID().toString(), "Free Screen Play", CalendarEventType.REWARD_TIME, "6:30 PM - 7:30 PM", "Wed", 22, false),
            CalendarEventItem(UUID.randomUUID().toString(), "Bedtime Device Lockout", CalendarEventType.BEDTIME, "9:00 PM", "Daily", 20, false)
        )
    )
    val calendarEvents: StateFlow<List<CalendarEventItem>> = _calendarEvents.asStateFlow()

    fun toggleTask(taskId: String): PendingTaskItem? {
        var updatedTask: PendingTaskItem? = null
        val updated = _pendingTasks.value.map { task ->
            if (task.id == taskId) {
                val nextTask = when (task.approvalStatus) {
                    TaskApprovalStatus.NOT_COMPLETED,
                    TaskApprovalStatus.PENDING,
                    TaskApprovalStatus.IN_PROGRESS,
                    TaskApprovalStatus.REJECTED -> {
                        // Mark as pending parent approval! DO NOT credit points yet!
                        task.copy(
                            approvalStatus = TaskApprovalStatus.PENDING_PARENT_APPROVAL,
                            isCompleted = false
                        )
                    }
                    TaskApprovalStatus.SUBMITTED_PENDING_APPROVAL,
                    TaskApprovalStatus.PENDING_PARENT_APPROVAL -> {
                        // Child can uncheck if clicked by mistake
                        task.copy(
                            approvalStatus = TaskApprovalStatus.NOT_COMPLETED,
                            isCompleted = false
                        )
                    }
                    TaskApprovalStatus.APPROVED_AND_CREDITED,
                    TaskApprovalStatus.APPROVED -> {
                        task // Already approved and credited by parents
                    }
                }
                updatedTask = nextTask
                nextTask
            } else task
        }
        _pendingTasks.value = updated
        return updatedTask
    }

    fun approveTaskByParent(taskId: String): Boolean {
        var approved = false
        val updated = _pendingTasks.value.map { task ->
            if (task.id == taskId && task.approvalStatus == TaskApprovalStatus.PENDING_PARENT_APPROVAL) {
                approved = true
                // Award credits upon parent approval!
                creditRepository.addCredits(
                    amount = task.creditsReward,
                    type = CreditTransactionType.TASK_COMPLETED,
                    title = "Parent Verified: ${task.title}"
                )
                task.copy(
                    approvalStatus = TaskApprovalStatus.APPROVED_AND_CREDITED,
                    isCompleted = true
                )
            } else task
        }
        _pendingTasks.value = updated
        return approved
    }
}
