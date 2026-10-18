package com.nestcheck.app.core

data class PasswordValidationResult(
    val hasMinLength: Boolean,
    val hasUppercase: Boolean,
    val hasLowercase: Boolean,
    val hasDigit: Boolean,
    val hasSpecialChar: Boolean
) {
    val isValid: Boolean
        get() = hasMinLength && hasUppercase && hasLowercase && hasDigit && hasSpecialChar

    val strengthScore: Float
        get() {
            var count = 0
            if (hasMinLength) count++
            if (hasUppercase) count++
            if (hasLowercase) count++
            if (hasDigit) count++
            if (hasSpecialChar) count++
            return count / 5f
        }
}

object PasswordValidator {
    fun validate(password: String): PasswordValidationResult {
        return PasswordValidationResult(
            hasMinLength = password.length >= 8,
            hasUppercase = password.any { it.isUpperCase() },
            hasLowercase = password.any { it.isLowerCase() },
            hasDigit = password.any { it.isDigit() },
            hasSpecialChar = password.any { !it.isLetterOrDigit() }
        )
    }

    fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
    }
}
