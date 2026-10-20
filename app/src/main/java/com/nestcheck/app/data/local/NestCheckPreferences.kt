package com.nestcheck.app.data.local

import android.content.Context
import android.content.SharedPreferences
import com.nestcheck.app.ui.common.UserType
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NestCheckPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("nestcheck_secure_prefs", Context.MODE_PRIVATE)

    var isLoggedIn: Boolean
        get() = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
        set(value) = prefs.edit().putBoolean(KEY_IS_LOGGED_IN, value).apply()

    var userType: UserType
        get() {
            val typeStr = prefs.getString(KEY_USER_TYPE, UserType.PARENT.name) ?: UserType.PARENT.name
            return try { UserType.valueOf(typeStr) } catch (e: Exception) { UserType.PARENT }
        }
        set(value) = prefs.edit().putString(KEY_USER_TYPE, value.name).apply()

    var parentName: String
        get() = prefs.getString(KEY_PARENT_NAME, "Dev Raj") ?: "Dev Raj"
        set(value) = prefs.edit().putString(KEY_PARENT_NAME, value).apply()

    var parentEmail: String
        get() = prefs.getString(KEY_PARENT_EMAIL, "devraj@nestcheck.app") ?: "devraj@nestcheck.app"
        set(value) = prefs.edit().putString(KEY_PARENT_EMAIL, value).apply()

    var activeParentRole: String
        get() = prefs.getString(KEY_ACTIVE_PARENT_ROLE, "Father") ?: "Father"
        set(value) = prefs.edit().putString(KEY_ACTIVE_PARENT_ROLE, value).apply()

    var activeChildName: String
        get() = prefs.getString(KEY_ACTIVE_CHILD_NAME, "Leo") ?: "Leo"
        set(value) = prefs.edit().putString(KEY_ACTIVE_CHILD_NAME, value).apply()

    var activeChildAvatar: String
        get() = prefs.getString(KEY_ACTIVE_CHILD_AVATAR, "🦊") ?: "🦊"
        set(value) = prefs.edit().putString(KEY_ACTIVE_CHILD_AVATAR, value).apply()

    var emergencyContactPhone: String
        get() = prefs.getString(KEY_EMERGENCY_CONTACT_PHONE, "+91 98765 43210") ?: "+91 98765 43210"
        set(value) = prefs.edit().putString(KEY_EMERGENCY_CONTACT_PHONE, value).apply()

    var emergencyContactName: String
        get() = prefs.getString(KEY_EMERGENCY_CONTACT_NAME, "Father (Dev Raj)") ?: "Father (Dev Raj)"
        set(value) = prefs.edit().putString(KEY_EMERGENCY_CONTACT_NAME, value).apply()

    var fatherName: String
        get() = prefs.getString(KEY_FATHER_NAME, "Dev Raj") ?: "Dev Raj"
        set(value) = prefs.edit().putString(KEY_FATHER_NAME, value).apply()

    var fatherEmail: String
        get() = prefs.getString(KEY_FATHER_EMAIL, "devraj@nestcheck.app") ?: "devraj@nestcheck.app"
        set(value) = prefs.edit().putString(KEY_FATHER_EMAIL, value).apply()

    var fatherPhone: String
        get() = prefs.getString(KEY_FATHER_PHONE, "+91 98765 43210") ?: "+91 98765 43210"
        set(value) = prefs.edit().putString(KEY_FATHER_PHONE, value).apply()

    var motherName: String
        get() = prefs.getString(KEY_MOTHER_NAME, "Priya") ?: "Priya"
        set(value) = prefs.edit().putString(KEY_MOTHER_NAME, value).apply()

    var motherEmail: String
        get() = prefs.getString(KEY_MOTHER_EMAIL, "priya@nestcheck.app") ?: "priya@nestcheck.app"
        set(value) = prefs.edit().putString(KEY_MOTHER_EMAIL, value).apply()

    var motherPhone: String
        get() = prefs.getString(KEY_MOTHER_PHONE, "+91 98765 43212") ?: "+91 98765 43212"
        set(value) = prefs.edit().putString(KEY_MOTHER_PHONE, value).apply()

    var guardianName: String
        get() = prefs.getString(KEY_GUARDIAN_NAME, "Rohit") ?: "Rohit"
        set(value) = prefs.edit().putString(KEY_GUARDIAN_NAME, value).apply()

    var guardianEmail: String
        get() = prefs.getString(KEY_GUARDIAN_EMAIL, "rohit@nestcheck.app") ?: "rohit@nestcheck.app"
        set(value) = prefs.edit().putString(KEY_GUARDIAN_EMAIL, value).apply()

    var guardianPhone: String
        get() = prefs.getString(KEY_GUARDIAN_PHONE, "+91 98765 43211") ?: "+91 98765 43211"
        set(value) = prefs.edit().putString(KEY_GUARDIAN_PHONE, value).apply()

    var remainingMinutes: Int
        get() = prefs.getInt(KEY_REMAINING_MINS, 105)
        set(value) = prefs.edit().putInt(KEY_REMAINING_MINS, value).apply()

    var dailyLimitMinutes: Int
        get() = prefs.getInt(KEY_DAILY_LIMIT_MINS, 180)
        set(value) = prefs.edit().putInt(KEY_DAILY_LIMIT_MINS, value).apply()

    var householdPairingCode: String
        get() = prefs.getString(KEY_PAIRING_CODE, "NC-8294") ?: "NC-8294"
        set(value) = prefs.edit().putString(KEY_PAIRING_CODE, value).apply()

    var isDeviceLocked: Boolean
        get() = prefs.getBoolean(KEY_DEVICE_LOCKED, false)
        set(value) = prefs.edit().putBoolean(KEY_DEVICE_LOCKED, value).apply()

    fun clear() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_TYPE = "user_type"
        private const val KEY_PARENT_NAME = "parent_name"
        private const val KEY_PARENT_EMAIL = "parent_email"
        private const val KEY_ACTIVE_PARENT_ROLE = "active_parent_role"
        private const val KEY_ACTIVE_CHILD_NAME = "active_child_name"
        private const val KEY_ACTIVE_CHILD_AVATAR = "active_child_avatar"
        private const val KEY_EMERGENCY_CONTACT_PHONE = "emergency_contact_phone"
        private const val KEY_EMERGENCY_CONTACT_NAME = "emergency_contact_name"
        private const val KEY_FATHER_NAME = "father_name"
        private const val KEY_FATHER_EMAIL = "father_email"
        private const val KEY_FATHER_PHONE = "father_phone"
        private const val KEY_MOTHER_NAME = "mother_name"
        private const val KEY_MOTHER_EMAIL = "mother_email"
        private const val KEY_MOTHER_PHONE = "mother_phone"
        private const val KEY_GUARDIAN_NAME = "guardian_name"
        private const val KEY_GUARDIAN_EMAIL = "guardian_email"
        private const val KEY_GUARDIAN_PHONE = "guardian_phone"
        private const val KEY_REMAINING_MINS = "remaining_minutes"
        private const val KEY_DAILY_LIMIT_MINS = "daily_limit_minutes"
        private const val KEY_PAIRING_CODE = "pairing_code"
        private const val KEY_DEVICE_LOCKED = "device_locked"
    }
}
