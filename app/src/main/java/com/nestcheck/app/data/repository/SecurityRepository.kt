package com.nestcheck.app.data.repository

import com.nestcheck.app.data.model.LockTimeoutOption
import com.nestcheck.app.data.model.SecurityConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecurityRepository @Inject constructor() {

    private val _securityConfig = MutableStateFlow(SecurityConfig())
    val securityConfig: StateFlow<SecurityConfig> = _securityConfig.asStateFlow()

    fun verifyPin(enteredPin: String): Boolean {
        val current = _securityConfig.value
        return if (enteredPin == current.parentPin) {
            _securityConfig.value = current.copy(
                isAppLocked = false,
                lastUnlockedTimestamp = System.currentTimeMillis(),
                failedAttempts = 0
            )
            true
        } else {
            _securityConfig.value = current.copy(
                failedAttempts = current.failedAttempts + 1
            )
            false
        }
    }

    fun updatePin(oldPin: String, newPin: String): Boolean {
        val current = _securityConfig.value
        return if (oldPin == current.parentPin && newPin.length == 4) {
            _securityConfig.value = current.copy(parentPin = newPin)
            true
        } else {
            false
        }
    }

    fun toggleBiometrics(enabled: Boolean) {
        _securityConfig.value = _securityConfig.value.copy(biometricEnabled = enabled)
    }

    fun setAutoLockTimeout(option: LockTimeoutOption) {
        _securityConfig.value = _securityConfig.value.copy(autoLockTimeoutMinutes = option.minutes)
    }

    fun lockNow() {
        _securityConfig.value = _securityConfig.value.copy(isAppLocked = true)
    }

    fun unlockWithBiometrics(): Boolean {
        val current = _securityConfig.value
        return if (current.biometricEnabled) {
            _securityConfig.value = current.copy(
                isAppLocked = false,
                lastUnlockedTimestamp = System.currentTimeMillis(),
                failedAttempts = 0
            )
            true
        } else {
            false
        }
    }
}
