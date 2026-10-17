package com.nestcheck.app.data.repository

import com.nestcheck.app.data.model.LockTimeoutOption
import com.nestcheck.app.data.model.SecurityConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecurityRepository @Inject constructor() {

    private companion object {
        const val MAX_ATTEMPTS = 5
    }

    private val _securityConfig = MutableStateFlow(SecurityConfig())
    val securityConfig: StateFlow<SecurityConfig> = _securityConfig.asStateFlow()

    fun verifyPin(enteredPin: String): Boolean {
        val current = _securityConfig.value
        val now = System.currentTimeMillis()
        if (now < current.lockedUntil) return false // still in cooldown

        val ok = MessageDigest.isEqual(enteredPin.toByteArray(), current.parentPin.toByteArray())
        return if (ok) {
            _securityConfig.value = current.copy(
                isAppLocked = false,
                lastUnlockedTimestamp = now,
                failedAttempts = 0,
                lockedUntil = 0L
            )
            true
        } else {
            val attempts = current.failedAttempts + 1
            // 5 misses -> 30s cooldown, doubling each further miss, capped at 15 min
            val cooldownMs = if (attempts >= MAX_ATTEMPTS) {
                minOf(30_000L shl (attempts - MAX_ATTEMPTS).coerceAtMost(5), 15 * 60_000L)
            } else 0L
            _securityConfig.value = current.copy(
                failedAttempts = attempts,
                lockedUntil = if (cooldownMs > 0) now + cooldownMs else 0L
            )
            false
        }
    }

    fun lockoutSecondsRemaining(): Long =
        ((_securityConfig.value.lockedUntil - System.currentTimeMillis()) / 1000).coerceAtLeast(0)

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
