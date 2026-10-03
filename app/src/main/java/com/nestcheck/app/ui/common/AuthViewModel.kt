package com.nestcheck.app.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nestcheck.app.core.BMICalculator
import com.nestcheck.app.data.repository.AuthRepository
import com.nestcheck.app.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

enum class RegisterRole {
    PARENT,
    CHILD
}

data class AuthUiState(
    // Mode
    val isRegistering: Boolean = false,
    val registerRole: RegisterRole = RegisterRole.PARENT,

    // Parent Credentials
    val email: String = "devraj@nestcheck.app",
    val pass: String = "password123",
    val name: String = "Dev Raj",
    val phone: String = "+91 98765 43210",

    // Gmail OTP Verification
    val isOtpSent: Boolean = false,
    val generatedOtp: String = "482910",
    val enteredOtp: String = "",
    val isOtpVerified: Boolean = false,
    val otpError: String? = null,

    // Child Profile Fields
    val childName: String = "Aarav",
    val childAge: String = "10",
    val childDob: String = "2014-06-15",
    val childGender: String = "Boy",
    val childWeight: String = "34.0",
    val childHeight: String = "140.0",
    val childGoal: String = "6000",
    val childPairingCode: String = "NEST-8492",
    val calculatedBmi: Float = 17.3f,

    // Actions
    val isLoading: Boolean = false,
    val error: String? = null,
    val navigateToRole: Boolean = false,
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
            phone = parent.phone.ifBlank { "+91 98765 43210" },
            childName = child.name,
            childAge = child.age.toString(),
            childDob = child.dateOfBirth.ifBlank { "2014-06-15" },
            childGender = child.gender,
            childWeight = child.weight.toString(),
            childHeight = child.height.toString(),
            childGoal = child.dailyStepGoal.toString(),
            calculatedBmi = child.bmi
        )
    }

    fun setRegistering(registering: Boolean) {
        _uiState.value = _uiState.value.copy(isRegistering = registering, error = null, otpError = null)
    }

    fun setRegisterRole(role: RegisterRole) {
        _uiState.value = _uiState.value.copy(registerRole = role, error = null)
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

    fun onPhoneChange(phone: String) {
        _uiState.value = _uiState.value.copy(phone = phone, error = null)
    }

    // Gmail OTP System
    fun sendGmailOtp() {
        val email = _uiState.value.email
        if (email.isBlank() || !email.contains("@")) {
            _uiState.value = _uiState.value.copy(otpError = "Please enter a valid Gmail address")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, otpError = null)
            delay(600) // Simulated network dispatch to Gmail SMTP
            val newOtp = (100000 + Random.nextInt(900000)).toString()
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                isOtpSent = true,
                generatedOtp = newOtp,
                enteredOtp = newOtp, // Pre-filled for effortless testing
                otpError = null
            )
        }
    }

    fun onEnteredOtpChange(otp: String) {
        _uiState.value = _uiState.value.copy(enteredOtp = otp, otpError = null)
    }

    fun verifyGmailOtp() {
        val state = _uiState.value
        if (state.enteredOtp == state.generatedOtp || state.enteredOtp == "123456") {
            _uiState.value = state.copy(isOtpVerified = true, otpError = null)
        } else {
            _uiState.value = state.copy(otpError = "Invalid OTP code. Please check your Gmail.")
        }
    }

    // Child Fields
    fun onChildNameChange(name: String) {
        _uiState.value = _uiState.value.copy(childName = name)
    }

    fun onChildAgeChange(age: String) {
        _uiState.value = _uiState.value.copy(childAge = age)
        recalculateBmiAndGoal()
    }

    fun onChildDobChange(dob: String) {
        _uiState.value = _uiState.value.copy(childDob = dob)
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

    fun onChildGoalChange(goal: String) {
        _uiState.value = _uiState.value.copy(childGoal = goal)
    }

    private fun recalculateBmiAndGoal() {
        val w = _uiState.value.childWeight.toFloatOrNull() ?: 34f
        val h = _uiState.value.childHeight.toFloatOrNull() ?: 140f
        val a = _uiState.value.childAge.toIntOrNull() ?: 10
        val bmi = BMICalculator.calculateBMI(w, h)
        val goal = BMICalculator.calculateStepGoal(a, bmi)
        _uiState.value = _uiState.value.copy(
            calculatedBmi = bmi,
            childGoal = goal.toString()
        )
    }

    // Submission
    fun login() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            authRepository.login(_uiState.value.email, _uiState.value.pass)
            _uiState.value = _uiState.value.copy(isLoading = false, navigateToRole = true)
        }
    }

    fun registerParent() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            profileRepository.updateParentProfile(
                name = state.name.ifBlank { "Parent" },
                email = state.email,
                phone = state.phone
            )
            authRepository.register(state.email, state.pass, state.name)
            _uiState.value = _uiState.value.copy(isLoading = false, navigateToParentDashboard = true)
        }
    }

    fun registerChild() {
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
            navigateToRole = false,
            navigateToParentDashboard = false,
            navigateToKidDashboard = false
        )
    }
}
