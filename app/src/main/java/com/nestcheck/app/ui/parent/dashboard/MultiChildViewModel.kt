package com.nestcheck.app.ui.parent.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nestcheck.app.data.model.HouseholdChild
import com.nestcheck.app.data.repository.HouseholdRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MultiChildUiState(
    val children: List<HouseholdChild> = emptyList(),
    val selectedChildId: String = "",
    val showAddChildDialog: Boolean = false,
    val newChildName: String = "",
    val newChildAge: String = "",
    val newChildGrade: String = "",
    val newChildDevice: String = "",
    val createdPairingCode: String? = null,
    val toastMessage: String? = null
)

@HiltViewModel
class MultiChildViewModel @Inject constructor(
    private val repository: HouseholdRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MultiChildUiState())
    val uiState: StateFlow<MultiChildUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.children.collect { list ->
                _uiState.value = _uiState.value.copy(children = list)
            }
        }
        viewModelScope.launch {
            repository.selectedChildId.collect { id ->
                _uiState.value = _uiState.value.copy(selectedChildId = id)
            }
        }
    }

    fun selectChild(childId: String) {
        repository.selectChild(childId)
    }

    fun openAddChildDialog() {
        _uiState.value = _uiState.value.copy(
            showAddChildDialog = true,
            newChildName = "",
            newChildAge = "",
            newChildGrade = "",
            newChildDevice = "",
            createdPairingCode = null
        )
    }

    fun closeAddChildDialog() {
        _uiState.value = _uiState.value.copy(showAddChildDialog = false, createdPairingCode = null)
    }

    fun setNewChildName(name: String) { _uiState.value = _uiState.value.copy(newChildName = name) }
    fun setNewChildAge(age: String) { _uiState.value = _uiState.value.copy(newChildAge = age) }
    fun setNewChildGrade(grade: String) { _uiState.value = _uiState.value.copy(newChildGrade = grade) }
    fun setNewChildDevice(device: String) { _uiState.value = _uiState.value.copy(newChildDevice = device) }

    fun submitNewChild() {
        val s = _uiState.value
        val ageInt = s.newChildAge.toIntOrNull() ?: 8
        if (s.newChildName.isBlank()) return

        val added = repository.addChild(
            name = s.newChildName,
            age = ageInt,
            grade = s.newChildGrade,
            deviceName = s.newChildDevice
        )
        _uiState.value = _uiState.value.copy(
            createdPairingCode = added.pairingCode,
            toastMessage = "Added ${added.name}! Pairing Code: ${added.pairingCode}"
        )
    }

    fun removeChild(childId: String) {
        repository.removeChild(childId)
    }

    fun clearToast() {
        _uiState.value = _uiState.value.copy(toastMessage = null)
    }
}
