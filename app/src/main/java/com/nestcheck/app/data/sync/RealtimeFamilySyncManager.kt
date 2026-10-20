package com.nestcheck.app.data.sync

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.nestcheck.app.core.NestCheckNotificationManager
import com.nestcheck.app.data.local.NestCheckPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

enum class DeviceApprovalStatus {
    PENDING,
    APPROVED,
    REJECTED
}

data class TimetableItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val dayOfWeek: String, // "Monday", "Tuesday", etc.
    val subject: String,
    val timeSlot: String, // e.g. "08:30 AM - 09:30 AM"
    val roomOrTeacher: String = "MPSTME Room 402"
)

data class FamilySyncState(
    val familyCode: String = "NC-8294",
    val activeChildName: String = "Olivia",
    val isDeviceLocked: Boolean = false,
    val remainingMinutes: Int = 135,
    val dailyLimitMinutes: Int = 180,
    val kidLatitude: Double = 19.1075,
    val kidLongitude: Double = 72.8369,
    val kidBattery: Int = 88,
    val isCharging: Boolean = false,
    val locationAddress: String = "MPSTME NMIMS, Vile Parle West, Mumbai",
    val lastLocationUpdate: Long = System.currentTimeMillis(),
    val sosActive: Boolean = false,
    val sosTimestamp: Long = 0L,
    val sosLocation: String = "",
    val lockedApps: List<String> = listOf("com.instagram.android", "com.zhiliaoapp.musically"),
    val inAppPurchasesBlocked: Boolean = true, // Google Play Payment Block
    val appApprovalsRequired: Boolean = true,
    val deviceApprovalStatus: DeviceApprovalStatus = DeviceApprovalStatus.APPROVED,
    val pendingDeviceName: String = "Google Pixel 9",
    val childAge: Int = 11,
    val bedtime: String = "9:00 PM",
    val schoolHours: String = "8:30 AM - 3:30 PM",
    val timetablePdfName: String = "MPSTME_NMIMS_Timetable.pdf",
    val timetableSyncedToCalendar: Boolean = false,
    val timetableItems: List<TimetableItem> = listOf(
        TimetableItem(dayOfWeek = "Monday", subject = "Engineering Mathematics", timeSlot = "08:30 AM - 09:30 AM", roomOrTeacher = "MPSTME Room 402 • Dr. Sharma"),
        TimetableItem(dayOfWeek = "Monday", subject = "Data Structures & Algorithms", timeSlot = "09:45 AM - 11:00 AM", roomOrTeacher = "Lab 5 • Prof. Rao"),
        TimetableItem(dayOfWeek = "Monday", subject = "Digital Logic & Computer Design", timeSlot = "11:15 AM - 12:15 PM", roomOrTeacher = "MPSTME Room 301 • Dr. Davis"),
        TimetableItem(dayOfWeek = "Monday", subject = "Campus Lunch & Break", timeSlot = "12:15 PM - 01:15 PM", roomOrTeacher = "MPSTME Canteen"),
        TimetableItem(dayOfWeek = "Monday", subject = "Object Oriented Programming (Java)", timeSlot = "01:15 PM - 02:15 PM", roomOrTeacher = "IT Lab 2 • Prof. Patel"),
        TimetableItem(dayOfWeek = "Monday", subject = "Database Management Systems", timeSlot = "02:30 PM - 03:30 PM", roomOrTeacher = "MPSTME Room 405"),

        TimetableItem(dayOfWeek = "Tuesday", subject = "Operating Systems", timeSlot = "08:30 AM - 09:30 AM", roomOrTeacher = "MPSTME Room 402 • Dr. Mehta"),
        TimetableItem(dayOfWeek = "Tuesday", subject = "Computer Networks", timeSlot = "09:45 AM - 11:00 AM", roomOrTeacher = "Lab 3 • Prof. Gupta"),
        TimetableItem(dayOfWeek = "Tuesday", subject = "Software Engineering", timeSlot = "11:15 AM - 12:15 PM", roomOrTeacher = "MPSTME Room 302 • Dr. Roy"),
        TimetableItem(dayOfWeek = "Tuesday", subject = "AI & Machine Learning Seminar", timeSlot = "01:15 PM - 02:30 PM", roomOrTeacher = "MPSTME Auditorium"),

        TimetableItem(dayOfWeek = "Wednesday", subject = "Discrete Mathematics", timeSlot = "08:30 AM - 09:30 AM", roomOrTeacher = "MPSTME Room 402 • Dr. Sharma"),
        TimetableItem(dayOfWeek = "Wednesday", subject = "Python & Web Technologies Lab", timeSlot = "09:45 AM - 11:00 AM", roomOrTeacher = "Lab 1 • Prof. Wilson"),
        TimetableItem(dayOfWeek = "Wednesday", subject = "Microprocessors & Interfacing", timeSlot = "11:15 AM - 12:15 PM", roomOrTeacher = "Hardware Lab • Dr. Sen"),
        TimetableItem(dayOfWeek = "Wednesday", subject = "Technical Communication & Ethics", timeSlot = "01:15 PM - 02:30 PM", roomOrTeacher = "MPSTME Room 204"),

        TimetableItem(dayOfWeek = "Thursday", subject = "Cloud Computing & DevOps", timeSlot = "08:30 AM - 09:30 AM", roomOrTeacher = "Cloud Lab • Prof. Rao"),
        TimetableItem(dayOfWeek = "Thursday", subject = "Information Security", timeSlot = "09:45 AM - 11:00 AM", roomOrTeacher = "MPSTME Room 301 • Dr. Desai"),
        TimetableItem(dayOfWeek = "Thursday", subject = "Algorithms Analysis Lab", timeSlot = "11:15 AM - 12:15 PM", roomOrTeacher = "Lab 5 • Prof. Rao"),
        TimetableItem(dayOfWeek = "Thursday", subject = "Robotics & IoT Project Work", timeSlot = "01:15 PM - 02:30 PM", roomOrTeacher = "Maker Space"),

        TimetableItem(dayOfWeek = "Friday", subject = "Weekly Engineering Quiz", timeSlot = "08:30 AM - 09:30 AM", roomOrTeacher = "MPSTME Room 402"),
        TimetableItem(dayOfWeek = "Friday", subject = "Applied Statistics & Probability", timeSlot = "09:45 AM - 11:00 AM", roomOrTeacher = "MPSTME Room 405"),
        TimetableItem(dayOfWeek = "Friday", subject = "Library / Independent Research", timeSlot = "11:15 AM - 12:15 PM", roomOrTeacher = "MPSTME Central Library"),
        TimetableItem(dayOfWeek = "Friday", subject = "Student Project Mentorship", timeSlot = "01:15 PM - 03:00 PM", roomOrTeacher = "Innovation Lab")
    )
)

@Singleton
class RealtimeFamilySyncManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferences: NestCheckPreferences,
    private val notificationManager: NestCheckNotificationManager
) {
    private val firestore by lazy {
        try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            null
        }
    }

    private val _syncState = MutableStateFlow(
        FamilySyncState(
            familyCode = preferences.householdPairingCode,
            activeChildName = preferences.activeChildName,
            isDeviceLocked = preferences.isDeviceLocked,
            remainingMinutes = preferences.remainingMinutes,
            dailyLimitMinutes = preferences.dailyLimitMinutes,
            kidLatitude = preferences.kidLatitude,
            kidLongitude = preferences.kidLongitude,
            locationAddress = preferences.locationAddress,
            kidBattery = preferences.kidBattery,
            timetableSyncedToCalendar = preferences.timetableSyncedToCalendar
        )
    )
    val syncState: StateFlow<FamilySyncState> = _syncState.asStateFlow()

    private var firestoreListener: ListenerRegistration? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    private val prefChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
        _syncState.value = _syncState.value.copy(
            isDeviceLocked = preferences.isDeviceLocked,
            remainingMinutes = preferences.remainingMinutes,
            dailyLimitMinutes = preferences.dailyLimitMinutes,
            kidLatitude = preferences.kidLatitude,
            kidLongitude = preferences.kidLongitude,
            locationAddress = preferences.locationAddress,
            kidBattery = preferences.kidBattery,
            timetableSyncedToCalendar = preferences.timetableSyncedToCalendar,
            activeChildName = preferences.activeChildName
        )
    }

    init {
        preferences.registerOnSharedPreferenceChangeListener(prefChangeListener)
        startSync(preferences.householdPairingCode)
    }

    fun startSync(familyCode: String) {
        firestoreListener?.remove()

        try {
            firestore?.collection("nestcheck_families")
                ?.document(familyCode)
                ?.addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null || !snapshot.exists()) {
                        return@addSnapshotListener
                    }

                    val isLocked = snapshot.getBoolean("isDeviceLocked") ?: _syncState.value.isDeviceLocked
                    val remainingMins = snapshot.getLong("remainingMinutes")?.toInt() ?: _syncState.value.remainingMinutes
                    val dailyLimit = snapshot.getLong("dailyLimitMinutes")?.toInt() ?: _syncState.value.dailyLimitMinutes
                    val lat = snapshot.getDouble("kidLatitude") ?: _syncState.value.kidLatitude
                    val lng = snapshot.getDouble("kidLongitude") ?: _syncState.value.kidLongitude
                    val battery = snapshot.getLong("kidBattery")?.toInt() ?: _syncState.value.kidBattery
                    val address = snapshot.getString("locationAddress") ?: _syncState.value.locationAddress
                    val sos = snapshot.getBoolean("sosActive") ?: false
                    val purchasesBlocked = snapshot.getBoolean("inAppPurchasesBlocked") ?: true

                    val previousSos = _syncState.value.sosActive
                    val previousLocked = _syncState.value.isDeviceLocked

                    val newState = _syncState.value.copy(
                        familyCode = familyCode,
                        isDeviceLocked = isLocked,
                        remainingMinutes = remainingMins,
                        dailyLimitMinutes = dailyLimit,
                        kidLatitude = lat,
                        kidLongitude = lng,
                        kidBattery = battery,
                        locationAddress = address,
                        lastLocationUpdate = System.currentTimeMillis(),
                        sosActive = sos,
                        inAppPurchasesBlocked = purchasesBlocked
                    )

                    _syncState.value = newState
                    preferences.isDeviceLocked = isLocked
                    preferences.remainingMinutes = remainingMins
                    preferences.dailyLimitMinutes = dailyLimit
                    preferences.kidLatitude = lat
                    preferences.kidLongitude = lng
                    preferences.kidBattery = battery
                    preferences.locationAddress = address

                    // Real-time notifications for critical safety state changes
                    if (!previousSos && sos) {
                        notificationManager.showSosAlert(newState.activeChildName, address, lat, lng)
                    }
                    if (previousLocked != isLocked) {
                        notificationManager.showDeviceLockNotification(newState.activeChildName, isLocked)
                    }
                }
        } catch (e: Exception) {
            // Offline / graceful local fallback
        }
    }

    fun setDeviceLocked(isLocked: Boolean) {
        _syncState.value = _syncState.value.copy(isDeviceLocked = isLocked)
        preferences.isDeviceLocked = isLocked
        notificationManager.showDeviceLockNotification(_syncState.value.activeChildName, isLocked)

        scope.launch {
            try {
                firestore?.collection("nestcheck_families")
                    ?.document(_syncState.value.familyCode)
                    ?.set(
                        mapOf(
                            "isDeviceLocked" to isLocked,
                            "updatedAt" to System.currentTimeMillis()
                        ),
                        com.google.firebase.firestore.SetOptions.merge()
                    )
            } catch (e: Exception) {
                // local fallback maintained
            }
        }
    }

    fun setDailyLimitMinutes(minutes: Int) {
        _syncState.value = _syncState.value.copy(dailyLimitMinutes = minutes)
        preferences.dailyLimitMinutes = minutes

        scope.launch {
            try {
                firestore?.collection("nestcheck_families")
                    ?.document(_syncState.value.familyCode)
                    ?.set(
                        mapOf("dailyLimitMinutes" to minutes),
                        com.google.firebase.firestore.SetOptions.merge()
                    )
            } catch (e: Exception) {}
        }
    }

    fun setInAppPurchasesBlocked(blocked: Boolean) {
        _syncState.value = _syncState.value.copy(inAppPurchasesBlocked = blocked)
        scope.launch {
            try {
                firestore?.collection("nestcheck_families")
                    ?.document(_syncState.value.familyCode)
                    ?.set(
                        mapOf("inAppPurchasesBlocked" to blocked),
                        com.google.firebase.firestore.SetOptions.merge()
                    )
            } catch (e: Exception) {}
        }
    }

    fun pushKidTelemetry(lat: Double, lng: Double, battery: Int, address: String) {
        _syncState.value = _syncState.value.copy(
            kidLatitude = lat,
            kidLongitude = lng,
            kidBattery = battery,
            locationAddress = address,
            lastLocationUpdate = System.currentTimeMillis()
        )
        preferences.kidLatitude = lat
        preferences.kidLongitude = lng
        preferences.kidBattery = battery
        preferences.locationAddress = address

        scope.launch {
            try {
                firestore?.collection("nestcheck_families")
                    ?.document(_syncState.value.familyCode)
                    ?.set(
                        mapOf(
                            "kidLatitude" to lat,
                            "kidLongitude" to lng,
                            "kidBattery" to battery,
                            "locationAddress" to address,
                            "lastLocationUpdate" to System.currentTimeMillis()
                        ),
                        com.google.firebase.firestore.SetOptions.merge()
                    )
            } catch (e: Exception) {}
        }
    }

    fun triggerKidSos(active: Boolean, location: String, lat: Double, lng: Double) {
        _syncState.value = _syncState.value.copy(
            sosActive = active,
            sosTimestamp = System.currentTimeMillis(),
            sosLocation = location,
            kidLatitude = lat,
            kidLongitude = lng
        )
        preferences.kidLatitude = lat
        preferences.kidLongitude = lng
        preferences.locationAddress = location

        if (active) {
            notificationManager.showSosAlert(_syncState.value.activeChildName, location, lat, lng)
        }

        scope.launch {
            try {
                firestore?.collection("nestcheck_families")
                    ?.document(_syncState.value.familyCode)
                    ?.set(
                        mapOf(
                            "sosActive" to active,
                            "sosTimestamp" to System.currentTimeMillis(),
                            "sosLocation" to location,
                            "kidLatitude" to lat,
                            "kidLongitude" to lng
                        ),
                        com.google.firebase.firestore.SetOptions.merge()
                    )
            } catch (e: Exception) {}
        }
    }

    fun approveChildDevice() {
        _syncState.value = _syncState.value.copy(deviceApprovalStatus = DeviceApprovalStatus.APPROVED)
        scope.launch {
            try {
                firestore?.collection("nestcheck_families")
                    ?.document(_syncState.value.familyCode)
                    ?.set(mapOf("deviceApprovalStatus" to "APPROVED"), com.google.firebase.firestore.SetOptions.merge())
            } catch (e: Exception) {}
        }
    }

    fun rejectChildDevice() {
        _syncState.value = _syncState.value.copy(deviceApprovalStatus = DeviceApprovalStatus.REJECTED)
        scope.launch {
            try {
                firestore?.collection("nestcheck_families")
                    ?.document(_syncState.value.familyCode)
                    ?.set(mapOf("deviceApprovalStatus" to "REJECTED"), com.google.firebase.firestore.SetOptions.merge())
            } catch (e: Exception) {}
        }
    }

    fun requestChildDeviceApproval(deviceName: String) {
        _syncState.value = _syncState.value.copy(
            deviceApprovalStatus = DeviceApprovalStatus.PENDING,
            pendingDeviceName = deviceName
        )
        scope.launch {
            try {
                firestore?.collection("nestcheck_families")
                    ?.document(_syncState.value.familyCode)
                    ?.set(
                        mapOf(
                            "deviceApprovalStatus" to "PENDING",
                            "pendingDeviceName" to deviceName
                        ),
                        com.google.firebase.firestore.SetOptions.merge()
                    )
            } catch (e: Exception) {}
        }
    }

    fun updateChildProfile(name: String, age: Int, dailyLimitMinutes: Int, bedtime: String, schoolHours: String) {
        _syncState.value = _syncState.value.copy(
            activeChildName = name,
            childAge = age,
            dailyLimitMinutes = dailyLimitMinutes,
            bedtime = bedtime,
            schoolHours = schoolHours
        )
        preferences.activeChildName = name
        preferences.dailyLimitMinutes = dailyLimitMinutes

        scope.launch {
            try {
                firestore?.collection("nestcheck_families")
                    ?.document(_syncState.value.familyCode)
                    ?.set(
                        mapOf(
                            "activeChildName" to name,
                            "childAge" to age,
                            "dailyLimitMinutes" to dailyLimitMinutes,
                            "bedtime" to bedtime,
                            "schoolHours" to schoolHours
                        ),
                        com.google.firebase.firestore.SetOptions.merge()
                    )
            } catch (e: Exception) {}
        }
    }

    fun uploadTimetablePdf(fileName: String, newItems: List<TimetableItem>) {
        _syncState.value = _syncState.value.copy(
            timetablePdfName = fileName,
            timetableItems = newItems,
            timetableSyncedToCalendar = false
        )
        preferences.timetableSyncedToCalendar = false
        scope.launch {
            try {
                firestore?.collection("nestcheck_families")
                    ?.document(_syncState.value.familyCode)
                    ?.set(
                        mapOf(
                            "timetablePdfName" to fileName,
                            "timetableSyncedToCalendar" to false
                        ),
                        com.google.firebase.firestore.SetOptions.merge()
                    )
            } catch (e: Exception) {}
        }
    }

    fun setTimetableCalendarSynced(synced: Boolean) {
        _syncState.value = _syncState.value.copy(timetableSyncedToCalendar = synced)
        preferences.timetableSyncedToCalendar = synced
        scope.launch {
            try {
                firestore?.collection("nestcheck_families")
                    ?.document(_syncState.value.familyCode)
                    ?.set(
                        mapOf("timetableSyncedToCalendar" to synced),
                        com.google.firebase.firestore.SetOptions.merge()
                    )
            } catch (e: Exception) {}
        }
    }
}
