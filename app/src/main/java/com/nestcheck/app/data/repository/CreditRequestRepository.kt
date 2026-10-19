package com.nestcheck.app.data.repository

import com.nestcheck.app.data.model.CreditReasonCategory
import com.nestcheck.app.data.model.CreditRequest
import com.nestcheck.app.data.model.CreditRequestStatus
import com.nestcheck.app.data.model.CreditTransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreditRequestRepository @Inject constructor(
    private val creditRepository: CreditScreenTimeRepository
) {

    private val _requests = MutableStateFlow(
        listOf(
            CreditRequest(
                id = UUID.randomUUID().toString(),
                childName = "Alex",
                taskTitle = "Completed Extra Chapter 5 Algebra Problems",
                creditsRequested = 40, // 20 mins
                reasonCategory = CreditReasonCategory.EXAM_PREPARATION,
                justificationNote = "Finished all 15 review problems in textbook ahead of Friday test.",
                status = CreditRequestStatus.APPROVED,
                requestTimestamp = System.currentTimeMillis() - 1000L * 60 * 60 * 24,
                responseNote = "Great job Alex! 20 extra minutes granted."
            ),
            CreditRequest(
                id = UUID.randomUUID().toString(),
                childName = "Alex",
                taskTitle = "Washed Family Car & Organized Garage",
                creditsRequested = 50, // 25 mins
                reasonCategory = CreditReasonCategory.EXTRA_CHORE,
                justificationNote = "Helped clean garage shelving and swept entire floor.",
                status = CreditRequestStatus.PENDING,
                requestTimestamp = System.currentTimeMillis() - 1000L * 60 * 35
            )
        )
    )
    val requests: StateFlow<List<CreditRequest>> = _requests.asStateFlow()

    fun submitRequest(
        taskTitle: String,
        creditsRequested: Int,
        category: CreditReasonCategory,
        justification: String
    ): CreditRequest {
        val request = CreditRequest(
            id = UUID.randomUUID().toString(),
            childName = "Alex",
            taskTitle = taskTitle.trim(),
            creditsRequested = creditsRequested,
            reasonCategory = category,
            justificationNote = justification.trim(),
            status = CreditRequestStatus.PENDING,
            requestTimestamp = System.currentTimeMillis()
        )
        _requests.value = listOf(request) + _requests.value
        return request
    }

    fun approveRequest(requestId: String) {
        val req = _requests.value.find { it.id == requestId }
        if (req != null && req.status == CreditRequestStatus.PENDING) {
            // Credit the child's wallet: 2 credits = 1 min
            creditRepository.addCredits(
                amount = req.creditsRequested,
                type = CreditTransactionType.PARENT_GRANTED,
                title = "Parent Approved: ${req.taskTitle}"
            )
            _requests.value = _requests.value.map {
                if (it.id == requestId) {
                    it.copy(
                        status = CreditRequestStatus.APPROVED,
                        responseNote = "Approved by Parent (+${req.creditsRequested} credits / +${req.screenTimeMinutesEquivalent}m)"
                    )
                } else it
            }
        }
    }

    fun declineRequest(requestId: String, note: String = "Please complete homework first.") {
        _requests.value = _requests.value.map {
            if (it.id == requestId) {
                it.copy(
                    status = CreditRequestStatus.DECLINED,
                    responseNote = note
                )
            } else it
        }
    }
}
