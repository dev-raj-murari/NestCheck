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
        get() = prefs.getString(KEY_PARENT_NAME, "Dev Raj Murari") ?: "Dev Raj Murari"
        set(value) = prefs.edit().putString(KEY_PARENT_NAME, value).apply()

    var parentEmail: String
        get() = prefs.getString(KEY_PARENT_EMAIL, "") ?: ""
        set(value) = prefs.edit().putString(KEY_PARENT_EMAIL, value).apply()

    var activeChildName: String
        get() = prefs.getString(KEY_ACTIVE_CHILD_NAME, "Leo") ?: "Leo"
        set(value) = prefs.edit().putString(KEY_ACTIVE_CHILD_NAME, value).apply()

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

    var childCredits: Int
        get() = prefs.getInt(KEY_CHILD_CREDITS, 210)
        set(value) = prefs.edit().putInt(KEY_CHILD_CREDITS, value).apply()

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
        private const val KEY_ACTIVE_CHILD_NAME = "active_child_name"
        private const val KEY_REMAINING_MINS = "remaining_minutes"
        private const val KEY_DAILY_LIMIT_MINS = "daily_limit_minutes"
        private const val KEY_PAIRING_CODE = "pairing_code"
        private const val KEY_DEVICE_LOCKED = "device_locked"
        private const val KEY_KID_LAT = "kid_latitude"
        private const val KEY_KID_LNG = "kid_longitude"
        private const val KEY_LOCATION_ADDRESS = "location_address"
        private const val KEY_KID_BATTERY = "kid_battery"
        private const val KEY_TIMETABLE_CALENDAR_SYNCED = "timetable_calendar_synced"
        private const val KEY_CHILD_CREDITS = "child_credits"
    }
}
