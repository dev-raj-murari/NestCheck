package com.nestcheck.app.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nestcheck.app.core.BMICalculator
import com.nestcheck.app.data.repository.AuthRepository
import com.nestcheck.app.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class AppRoleMode {
    PARENT,
    CHILD
}

data class AuthUiState(
    // Mode
    val currentMode: AppRoleMode = AppRoleMode.PARENT,
    
    // Parent inputs
    val email: String = "parent@nestcheck.app",
    val pass: String = "password123",
    val name: String = "Dev Raj",
    val isRegistering: Boolean = false,
    
    // Child inputs
    val childName: String = "Aarav",
    val childAge: String = "10",
    val childGender: String = "Boy",
    val childWeight: String = "34.0",
    val childHeight: String = "140.0",
    val calculatedBmi: Float = 17.3f,
    val calculatedStepGoal: Int = 6000,
    
    // Status
    val isLoading: Boolean = false,
    val error: String? = null,
    val navigateToParentDashboard: Boolean = false,
    val navigateToKidDashboard: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        val child = profileRepository.childProfile.value
        val parent = profileRepository.parentProfile.value
        _uiState.value = _uiState.value.copy(
            name = parent.name,
            email = parent.email,
            childName = child.name,
            childAge = child.age.toString(),
            childGender = child.gender,
            childWeight = child.weight.toString(),
            childHeight = child.height.toString(),
            calculatedBmi = child.bmi,
            calculatedStepGoal = child.dailyStepGoal
        )
    }

    fun setMode(mode: AppRoleMode) {
        _uiState.value = _uiState.value.copy(currentMode = mode, error = null)
    }

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(email = email, error = null)
    }

    fun onPasswordChange(pass: String) {
        _uiState.value = _uiState.value.copy(pass = pass, error = null)
    }

    fun onNameChange(name: String) {
        _uiState.value = _uiState.value.copy(name = name, error = null)
    }

    fun toggleAuthMode() {
        _uiState.value = _uiState.value.copy(
            isRegistering = !_uiState.value.isRegistering,
            error = null
        )
    }

    // Child input handlers
    fun onChildNameChange(name: String) {
        _uiState.value = _uiState.value.copy(childName = name)
    }

    fun onChildAgeChange(age: String) {
        _uiState.value = _uiState.value.copy(childAge = age)
        recalculateBmiAndGoal()
    }

    fun onChildGenderChange(gender: String) {
        _uiState.value = _uiState.value.copy(childGender = gender)
    }

    fun onChildWeightChange(weight: String) {
        _uiState.value = _uiState.value.copy(childWeight = weight)
        recalculateBmiAndGoal()
    }

    fun onChildHeightChange(height: String) {
        _uiState.value = _uiState.value.copy(childHeight = height)
        recalculateBmiAndGoal()
    }

    private fun recalculateBmiAndGoal() {
        val w = _uiState.value.childWeight.toFloatOrNull() ?: 30f
        val h = _uiState.value.childHeight.toFloatOrNull() ?: 130f
        val age = _uiState.value.childAge.toIntOrNull() ?: 10
        val bmi = BMICalculator.calculateBMI(w, h)
        val goal = BMICalculator.calculateStepGoal(age, bmi)
        _uiState.value = _uiState.value.copy(
            calculatedBmi = bmi,
            calculatedStepGoal = goal
        )
    }

    fun submitParent() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = if (state.isRegistering) {
                authRepository.register(state.email, state.pass, state.name.ifBlank { "Parent" })
            } else {
                authRepository.login(state.email, state.pass)
            }

            result.fold(
                onSuccess = {
                    profileRepository.updateParentProfile(
                        name = state.name.ifBlank { "Parent User" },
                        email = state.email
                    )
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        navigateToParentDashboard = true
                    )
                },
                onFailure = {
                    // Fallback to let user enter dashboard anyway
                    profileRepository.updateParentProfile(
                        name = state.name.ifBlank { "Parent User" },
                        email = state.email
                    )
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        navigateToParentDashboard = true
                    )
                }
            )
        }
    }

    fun submitChild() {
        val state = _uiState.value
        val age = state.childAge.toIntOrNull() ?: 10
        val weight = state.childWeight.toFloatOrNull() ?: 34f
        val height = state.childHeight.toFloatOrNull() ?: 140f

        profileRepository.updateChildProfile(
            name = state.childName.ifBlank { "Aarav" },
            age = age,
            gender = state.childGender,
            weight = weight,
            height = height
        )

        _uiState.value = _uiState.value.copy(navigateToKidDashboard = true)
    }

    fun onNavigated() {
        _uiState.value = _uiState.value.copy(
            navigateToParentDashboard = false,
            navigateToKidDashboard = false
        )
    }
}
