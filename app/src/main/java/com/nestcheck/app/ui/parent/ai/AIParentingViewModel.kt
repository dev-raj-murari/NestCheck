package com.nestcheck.app.ui.parent.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nestcheck.app.data.model.AiParentingMessage
import com.nestcheck.app.data.repository.AIParentingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AIParentingUiState(
    val messages: List<AiParentingMessage> = emptyList(),
    val inputText: String = "",
    val isThinking: Boolean = false,
    val quickPrompts: List<String> = listOf(
        "How to handle screentime bedtime resistance?",
        "What daily limit is recommended for a 10-year-old?",
        "How to praise chores without just giving screen tokens?",
        "How should I talk to my child about explicit content?"
    )
)

@HiltViewModel
class AIParentingViewModel @Inject constructor(
    private val repository: AIParentingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AIParentingUiState())
    val uiState: StateFlow<AIParentingUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.messages.collect { msgs ->
                _uiState.value = _uiState.value.copy(messages = msgs)
            }
        }
    }

    fun setInputText(text: String) {
        _uiState.value = _uiState.value.copy(inputText = text)
    }

    fun sendMessage(query: String? = null) {
        val toSend = query ?: _uiState.value.inputText
        if (toSend.isBlank()) return

        _uiState.value = _uiState.value.copy(inputText = "", isThinking = true)
        viewModelScope.launch {
            repository.sendMessage(toSend)
            _uiState.value = _uiState.value.copy(isThinking = false)
        }
    }
}
