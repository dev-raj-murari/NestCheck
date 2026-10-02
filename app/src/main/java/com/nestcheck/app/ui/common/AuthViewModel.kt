package com.nestcheck.app.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nestcheck.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val email: String = "",
    val pass: String = "",
    val name: String = "",
    val isRegistering: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAuthenticated: Boolean = false,
    val selectedRole: String? = null // "parent" or "kid"
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        if (authRepository.currentUser != null) {
            _uiState.value = _uiState.value.copy(isAuthenticated = true)
        }
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

    fun selectRole(role: String) {
        _uiState.value = _uiState.value.copy(selectedRole = role)
    }

    fun submit() {
        val state = _uiState.value
        if (state.email.isBlank() || state.pass.isBlank()) {
            _uiState.value = state.copy(error = "Please fill in all fields")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = if (state.isRegistering) {
                authRepository.register(state.email, state.pass, state.name.ifBlank { "Parent User" })
            } else {
                authRepository.login(state.email, state.pass)
            }

            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(isLoading = false, isAuthenticated = true)
                },
                onFailure = { ex ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = ex.localizedMessage ?: "Authentication failed"
                    )
                }
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.value = AuthUiState()
        }
    }
}
