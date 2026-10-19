package com.nestcheck.app.ui.child.credits

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nestcheck.app.data.model.CreditReasonCategory
import com.nestcheck.app.data.model.CreditRequest
import com.nestcheck.app.data.repository.CreditRequestRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreditRequestViewModel @Inject constructor(
    private val repository: CreditRequestRepository
) : ViewModel() {

    val requests: StateFlow<List<CreditRequest>> = repository.requests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _taskTitle = mutableStateOf("")
    val taskTitle: State<String> = _taskTitle

    private val _creditsRequested = mutableIntStateOf(40) // 40 pts = 20 mins
    val creditsRequested: State<Int> = _creditsRequested

    private val _selectedCategory = mutableStateOf(CreditReasonCategory.EXTRA_CHORE)
    val selectedCategory: State<CreditReasonCategory> = _selectedCategory

    private val _justification = mutableStateOf("")
    val justification: State<String> = _justification

    private val _isSubmittedSuccess = mutableStateOf(false)
    val isSubmittedSuccess: State<Boolean> = _isSubmittedSuccess

    fun onTitleChange(title: String) {
        _taskTitle.value = title
    }

    fun onCreditsChange(credits: Int) {
        _creditsRequested.intValue = credits.coerceIn(10, 120)
    }

    fun onCategoryChange(category: CreditReasonCategory) {
        _selectedCategory.value = category
    }

    fun onJustificationChange(text: String) {
        _justification.value = text
    }

    fun submitRequest() {
        if (_taskTitle.value.isBlank()) return
        viewModelScope.launch {
            repository.submitRequest(
                taskTitle = _taskTitle.value,
                creditsRequested = _creditsRequested.intValue,
                category = _selectedCategory.value,
                justification = _justification.value
            )
            _isSubmittedSuccess.value = true
        }
    }

    fun resetForm() {
        _taskTitle.value = ""
        _creditsRequested.intValue = 40
        _justification.value = ""
        _isSubmittedSuccess.value = false
    }
}
