package com.nestcheck.app.data.local

import android.content.Context
import android.content.SharedPreferences
import com.nestcheck.app.data.model.ChildProfile
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

    var activeChildAge: Int
        get() = prefs.getInt(KEY_ACTIVE_CHILD_AGE, 11)
        set(value) = prefs.edit().putInt(KEY_ACTIVE_CHILD_AGE, value).apply()

    var activeChildGender: String
        get() = prefs.getString(KEY_ACTIVE_CHILD_GENDER, "Boy") ?: "Boy"
        set(value) = prefs.edit().putString(KEY_ACTIVE_CHILD_GENDER, value).apply()

    var activeChildWeight: Float
        get() = prefs.getFloat(KEY_ACTIVE_CHILD_WEIGHT, 34.0f)
        set(value) = prefs.edit().putFloat(KEY_ACTIVE_CHILD_WEIGHT, value).apply()

    var activeChildHeight: Float
        get() = prefs.getFloat(KEY_ACTIVE_CHILD_HEIGHT, 140.0f)
        set(value) = prefs.edit().putFloat(KEY_ACTIVE_CHILD_HEIGHT, value).apply()

    var activeChildGrade: String
        get() = prefs.getString(KEY_ACTIVE_CHILD_GRADE, "Grade 6") ?: "Grade 6"
        set(value) = prefs.edit().putString(KEY_ACTIVE_CHILD_GRADE, value).apply()

    var activeChildDob: String
        get() = prefs.getString(KEY_ACTIVE_CHILD_DOB, "2015-05-15") ?: "2015-05-15"
        set(value) = prefs.edit().putString(KEY_ACTIVE_CHILD_DOB, value).apply()

    var activeChildDailyStepGoal: Int
        get() = prefs.getInt(KEY_ACTIVE_CHILD_STEP_GOAL, 6000)
        set(value) = prefs.edit().putInt(KEY_ACTIVE_CHILD_STEP_GOAL, value).apply()

    fun getPersistedChildProfile(): ChildProfile {
        val w = activeChildWeight
        val h = activeChildHeight
        val bmi = if (h > 0f) w / ((h / 100f) * (h / 100f)) else 17.3f
        return ChildProfile(
            uid = "child_1",
            parentUid = "parent_1",
            name = activeChildName,
            age = activeChildAge,
            gender = activeChildGender,
            weight = w,
            height = h,
            grade = activeChildGrade,
            dateOfBirth = activeChildDob,
            avatarUrl = activeChildAvatar,
            bmi = (Math.round(bmi * 10f) / 10f),
            dailyStepGoal = activeChildDailyStepGoal
        )
    }

    fun saveChildProfile(profile: ChildProfile) {
        prefs.edit()
            .putString(KEY_ACTIVE_CHILD_NAME, profile.name)
            .putString(KEY_ACTIVE_CHILD_AVATAR, profile.avatarUrl)
            .putInt(KEY_ACTIVE_CHILD_AGE, profile.age)
            .putString(KEY_ACTIVE_CHILD_GENDER, profile.gender)
            .putFloat(KEY_ACTIVE_CHILD_WEIGHT, profile.weight)
            .putFloat(KEY_ACTIVE_CHILD_HEIGHT, profile.height)
            .putString(KEY_ACTIVE_CHILD_GRADE, profile.grade)
            .putString(KEY_ACTIVE_CHILD_DOB, profile.dateOfBirth)
            .putInt(KEY_ACTIVE_CHILD_STEP_GOAL, profile.dailyStepGoal)
            .apply()
    }

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

    var kidLatitude: Double
        get() = prefs.getString(KEY_KID_LAT, "19.1075")?.toDoubleOrNull() ?: 19.1075
        set(value) = prefs.edit().putString(KEY_KID_LAT, value.toString()).apply()

    var kidLongitude: Double
        get() = prefs.getString(KEY_KID_LNG, "72.8369")?.toDoubleOrNull() ?: 72.8369
        set(value) = prefs.edit().putString(KEY_KID_LNG, value.toString()).apply()

    var locationAddress: String
        get() = prefs.getString(KEY_LOCATION_ADDRESS, "MPSTME NMIMS, Vile Parle West, Mumbai") ?: "MPSTME NMIMS, Vile Parle West, Mumbai"
        set(value) = prefs.edit().putString(KEY_LOCATION_ADDRESS, value).apply()

    var kidBattery: Int
        get() = prefs.getInt(KEY_KID_BATTERY, 88)
        set(value) = prefs.edit().putInt(KEY_KID_BATTERY, value).apply()

    var timetableSyncedToCalendar: Boolean
        get() = prefs.getBoolean(KEY_TIMETABLE_CALENDAR_SYNCED, false)
        set(value) = prefs.edit().putBoolean(KEY_TIMETABLE_CALENDAR_SYNCED, value).apply()

    var sosActive: Boolean
        get() = prefs.getBoolean(KEY_SOS_ACTIVE, false)
        set(value) = prefs.edit().putBoolean(KEY_SOS_ACTIVE, value).apply()

    var geofenceBreachActive: Boolean
        get() = prefs.getBoolean(KEY_GEOFENCE_BREACH_ACTIVE, false)
        set(value) = prefs.edit().putBoolean(KEY_GEOFENCE_BREACH_ACTIVE, value).apply()

    var childCredits: Int
        get() = prefs.getInt(KEY_CHILD_CREDITS, 180)
        set(value) = prefs.edit().putInt(KEY_CHILD_CREDITS, value).apply()

    var isParentLoggedIn: Boolean
        get() = prefs.getBoolean(KEY_IS_PARENT_LOGGED_IN, false)
        set(value) = prefs.edit().putBoolean(KEY_IS_PARENT_LOGGED_IN, value).apply()

    var isKidLoggedIn: Boolean
        get() = prefs.getBoolean(KEY_IS_KID_LOGGED_IN, false)
        set(value) = prefs.edit().putBoolean(KEY_IS_KID_LOGGED_IN, value).apply()

    var hydrationGoalBonusAwarded: Boolean
        get() = prefs.getBoolean(KEY_HYDRATION_BONUS_AWARDED, false)
        set(value) = prefs.edit().putBoolean(KEY_HYDRATION_BONUS_AWARDED, value).apply()

    fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        prefs.registerOnSharedPreferenceChangeListener(listener)
    }

    fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        prefs.unregisterOnSharedPreferenceChangeListener(listener)
    }

    fun clear() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_TYPE = "user_type"
        private const val KEY_PARENT_NAME = "parent_name"
        private const val KEY_PARENT_EMAIL = "parent_email"
        private const val KEY_ACTIVE_PARENT_ROLE = "active_parent_role"
        const val KEY_ACTIVE_CHILD_NAME = "active_child_name"
        const val KEY_ACTIVE_CHILD_AVATAR = "active_child_avatar"
        const val KEY_ACTIVE_CHILD_AGE = "active_child_age"
        const val KEY_ACTIVE_CHILD_GENDER = "active_child_gender"
        const val KEY_ACTIVE_CHILD_WEIGHT = "active_child_weight"
        const val KEY_ACTIVE_CHILD_HEIGHT = "active_child_height"
        const val KEY_ACTIVE_CHILD_GRADE = "active_child_grade"
        const val KEY_ACTIVE_CHILD_DOB = "active_child_dob"
        const val KEY_ACTIVE_CHILD_STEP_GOAL = "active_child_step_goal"
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
        private const val KEY_KID_LAT = "kid_latitude"
        private const val KEY_KID_LNG = "kid_longitude"
        private const val KEY_LOCATION_ADDRESS = "location_address"
        private const val KEY_KID_BATTERY = "kid_battery"
        private const val KEY_TIMETABLE_CALENDAR_SYNCED = "timetable_calendar_synced"
        private const val KEY_SOS_ACTIVE = "sos_active"
        private const val KEY_GEOFENCE_BREACH_ACTIVE = "geofence_breach_active"
        private const val KEY_CHILD_CREDITS = "child_credits"
        private const val KEY_IS_PARENT_LOGGED_IN = "is_parent_logged_in"
        private const val KEY_IS_KID_LOGGED_IN = "is_kid_logged_in"
        private const val KEY_HYDRATION_BONUS_AWARDED = "hydration_bonus_awarded"
    }
}
