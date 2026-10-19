package com.nestcheck.app.ui.parent.requests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nestcheck.app.data.model.CreditRequest
import com.nestcheck.app.data.repository.CreditRequestRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ParentCreditRequestViewModel @Inject constructor(
    private val repository: CreditRequestRepository
) : ViewModel() {

    val creditRequests: StateFlow<List<CreditRequest>> = repository.requests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun approveCreditRequest(requestId: String) {
        viewModelScope.launch {
            repository.approveRequest(requestId)
        }
    }

    fun declineCreditRequest(requestId: String, reason: String = "Please focus on homework first.") {
        viewModelScope.launch {
            repository.declineRequest(requestId, reason)
        }
    }
}
