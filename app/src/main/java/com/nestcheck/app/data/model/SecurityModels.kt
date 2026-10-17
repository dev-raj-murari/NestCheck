package com.nestcheck.app.data.model

data class SecurityConfig(
    val parentPin: String = "1234",
    val biometricEnabled: Boolean = true,
    val isAppLocked: Boolean = false,
    val autoLockTimeoutMinutes: Int = 5,
    val lastUnlockedTimestamp: Long = System.currentTimeMillis(),
    val failedAttempts: Int = 0,
    val lockedUntil: Long = 0L // epoch ms; PIN attempts rejected until then
)

enum class LockTimeoutOption(val label: String, val minutes: Int) {
    IMMEDIATELY("Immediately", 0),
    ONE_MINUTE("1 Minute", 1),
    FIVE_MINUTES("5 Minutes", 5),
    FIFTEEN_MINUTES("15 Minutes", 15)
}
