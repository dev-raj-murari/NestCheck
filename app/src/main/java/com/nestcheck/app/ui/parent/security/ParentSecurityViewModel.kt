package com.nestcheck.app.ui.parent.security

import androidx.lifecycle.ViewModel
import com.nestcheck.app.data.model.LockTimeoutOption
import com.nestcheck.app.data.model.SecurityConfig
import com.nestcheck.app.data.repository.SecurityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ParentSecurityViewModel @Inject constructor(
    private val securityRepository: SecurityRepository
) : ViewModel() {

    val securityConfig: StateFlow<SecurityConfig> = securityRepository.securityConfig

    private val _enteredPin = MutableStateFlow("")
    val enteredPin: StateFlow<String> = _enteredPin.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    fun onKeypadDigit(digit: String) {
        if (_enteredPin.value.length < 4) {
            val updated = _enteredPin.value + digit
            _enteredPin.value = updated
            if (updated.length == 4) {
                verifyCurrentPin(updated)
            }
        }
    }

    fun onKeypadDelete() {
        if (_enteredPin.value.isNotEmpty()) {
            _enteredPin.value = _enteredPin.value.dropLast(1)
            _statusMessage.value = null
        }
    }

    fun onKeypadClear() {
        _enteredPin.value = ""
        _statusMessage.value = null
    }

    private fun verifyCurrentPin(pin: String) {
        val success = securityRepository.verifyPin(pin)
        if (success) {
            _statusMessage.value = "PIN Verified! Access Granted"
            _enteredPin.value = ""
        } else {
            _statusMessage.value = "Incorrect PIN. Try again."
            _enteredPin.value = ""
        }
    }

    fun simulateBiometricUnlock(): Boolean {
        val success = securityRepository.unlockWithBiometrics()
        _statusMessage.value = if (success) "Biometric Authenticated!" else "Biometrics Disabled"
        return success
    }

    fun toggleBiometrics(enabled: Boolean) {
        securityRepository.toggleBiometrics(enabled)
    }

    fun updatePin(oldPin: String, newPin: String): Boolean {
        val success = securityRepository.updatePin(oldPin, newPin)
        _statusMessage.value = if (success) "PIN Changed Successfully!" else "Failed to update PIN"
        return success
    }

    fun setLockTimeout(option: LockTimeoutOption) {
        securityRepository.setAutoLockTimeout(option)
    }

    fun lockNow() {
        securityRepository.lockNow()
    }
}
