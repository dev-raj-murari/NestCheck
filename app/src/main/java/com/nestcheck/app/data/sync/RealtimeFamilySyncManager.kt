package com.nestcheck.app.data.sync

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.nestcheck.app.core.NestCheckNotificationManager
import com.nestcheck.app.data.local.NestCheckPreferences
import com.nestcheck.app.data.model.ChildProfile
import com.nestcheck.app.data.model.ParentContactInfo
import com.nestcheck.app.data.model.HouseholdParentConfig
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
    val activeChildName: String = "Leo",
    val activeChildAvatar: String = "🦊",
    val isDeviceLocked: Boolean = false,
    val remainingMinutes: Int = 105,
    val dailyLimitMinutes: Int = 180,
    val kidLatitude: Double = 19.1075,
<<<<<<< HEAD
    val kidLongitude: Double = 72.8369,
    val kidBattery: Int = 88,
    val isCharging: Boolean = false,
    val locationAddress: String = "MPSTME NMIMS, Vile Parle West, Mumbai",
=======
    val kidLongitude: Double = 72.8372,
    val kidBattery: Int = 84,
    val isCharging: Boolean = false,
    val locationAddress: String = "MPSTME NMIMS, Vile Parle West, Mumbai, Maharashtra",
>>>>>>> origin/main
    val lastLocationUpdate: Long = System.currentTimeMillis(),
    val sosActive: Boolean = false,
    val sosTimestamp: Long = 0L,
    val sosLocation: String = "",
    val emergencyContactPhone: String = "+91 98765 43210",
    val emergencyContactName: String = "Father (Dev Raj)",
    val activeParentRole: String = "Father",
    val activeParentName: String = "Dev Raj",
    val fatherName: String = "Dev Raj",
    val fatherPhone: String = "+91 98765 43210",
    val fatherEmail: String = "devraj@nestcheck.app",
    val motherName: String = "Priya",
    val motherPhone: String = "+91 98765 43212",
    val motherEmail: String = "priya@nestcheck.app",
    val guardianName: String = "Rohit",
    val guardianPhone: String = "+91 98765 43211",
    val guardianEmail: String = "rohit@nestcheck.app",
    val geofenceBreachActive: Boolean = false,
    val geofenceBreachZone: String = "",
    val breachZoneName: String = "",
    val householdParents: HouseholdParentConfig = HouseholdParentConfig(
        father = ParentContactInfo(name = "Dev Raj", role = "Father", email = "devraj@nestcheck.app", phone = "+91 98765 43210"),
        mother = ParentContactInfo(name = "Priya", role = "Mother", email = "priya@nestcheck.app", phone = "+91 98765 43212"),
        guardian = ParentContactInfo(name = "Rohit", role = "Guardian", email = "rohit@nestcheck.app", phone = "+91 98765 43211")
    ),
    val geofenceBreachAddress: String = "",
    val geofenceBreachTimestamp: Long = 0L,
    val householdChildren: List<ChildProfile> = listOf(
        ChildProfile(uid = "child_leo", name = "Leo", age = 11, gender = "Boy", avatarUrl = "🦊", grade = "Grade 6"),
        ChildProfile(uid = "child_olivia", name = "Olivia", age = 9, gender = "Girl", avatarUrl = "🦄", grade = "Grade 4"),
        ChildProfile(uid = "child_aarav", name = "Aarav", age = 10, gender = "Boy", avatarUrl = "🚀", grade = "Grade 5")
    ),
    val lockedApps: List<String> = listOf("com.instagram.android", "com.zhiliaoapp.musically"),
    val inAppPurchasesBlocked: Boolean = true, // Google Play Payment Block
    val appApprovalsRequired: Boolean = true,
    val deviceApprovalStatus: DeviceApprovalStatus = DeviceApprovalStatus.APPROVED,
    val pendingDeviceName: String = "Google Pixel 8",
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
            activeChildAvatar = preferences.activeChildAvatar,
            isDeviceLocked = preferences.isDeviceLocked,
            remainingMinutes = preferences.remainingMinutes,
            dailyLimitMinutes = preferences.dailyLimitMinutes,
<<<<<<< HEAD
            kidLatitude = preferences.kidLatitude,
            kidLongitude = preferences.kidLongitude,
            locationAddress = preferences.locationAddress,
            kidBattery = preferences.kidBattery,
            timetableSyncedToCalendar = preferences.timetableSyncedToCalendar
=======
            emergencyContactPhone = preferences.emergencyContactPhone,
            emergencyContactName = preferences.emergencyContactName,
            activeParentRole = preferences.activeParentRole,
            activeParentName = preferences.parentName,
            fatherName = preferences.fatherName,
            fatherPhone = preferences.fatherPhone,
            fatherEmail = preferences.fatherEmail,
            motherName = preferences.motherName,
            motherPhone = preferences.motherPhone,
            motherEmail = preferences.motherEmail,
            guardianName = preferences.guardianName,
            guardianPhone = preferences.guardianPhone,
            guardianEmail = preferences.guardianEmail
>>>>>>> origin/main
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
                    val rawLat = snapshot.getDouble("kidLatitude") ?: _syncState.value.kidLatitude
                    val rawLng = snapshot.getDouble("kidLongitude") ?: _syncState.value.kidLongitude
                    // Ensure dummy location defaults to MPSTME NMIMS Mumbai (19.1075, 72.8372) and not legacy SF emulator coordinates
                    val (lat, lng) = if (rawLat > 30.0 || rawLng < 0.0) {
                        19.1075 to 72.8372
                    } else {
                        rawLat to rawLng
                    }
                    val battery = snapshot.getLong("kidBattery")?.toInt() ?: _syncState.value.kidBattery
                    val rawAddress = snapshot.getString("locationAddress") ?: _syncState.value.locationAddress
                    val address = if (rawAddress.isBlank() || rawAddress.contains("San Francisco") || rawAddress.contains("Market St")) {
                        "MPSTME NMIMS, Vile Parle West, Mumbai, Maharashtra"
                    } else {
                        rawAddress
                    }
                    val sos = snapshot.getBoolean("sosActive") ?: false
                    val purchasesBlocked = snapshot.getBoolean("inAppPurchasesBlocked") ?: true
                    val activeChild = snapshot.getString("activeChildName") ?: _syncState.value.activeChildName
                    val activeAvatar = snapshot.getString("activeChildAvatar") ?: _syncState.value.activeChildAvatar
                    val emergencyPhone = snapshot.getString("emergencyContactPhone") ?: _syncState.value.emergencyContactPhone

                    val previousSos = _syncState.value.sosActive
                    val previousLocked = _syncState.value.isDeviceLocked

                    val newState = _syncState.value.copy(
                        familyCode = familyCode,
                        activeChildName = activeChild,
                        activeChildAvatar = activeAvatar,
                        isDeviceLocked = isLocked,
                        remainingMinutes = remainingMins,
                        dailyLimitMinutes = dailyLimit,
                        kidLatitude = lat,
                        kidLongitude = lng,
                        kidBattery = battery,
                        locationAddress = address,
                        lastLocationUpdate = System.currentTimeMillis(),
                        sosActive = sos,
                        emergencyContactPhone = emergencyPhone,
                        inAppPurchasesBlocked = purchasesBlocked
                    )

                    _syncState.value = newState
                    preferences.activeChildName = activeChild
                    preferences.activeChildAvatar = activeAvatar
                    preferences.isDeviceLocked = isLocked
                    preferences.remainingMinutes = remainingMins
                    preferences.dailyLimitMinutes = dailyLimit
<<<<<<< HEAD
                    preferences.kidLatitude = lat
                    preferences.kidLongitude = lng
                    preferences.kidBattery = battery
                    preferences.locationAddress = address
=======
                    preferences.emergencyContactPhone = emergencyPhone
>>>>>>> origin/main

                    // Real-time notifications for critical safety state changes
                    if (!previousSos && sos) {
                        notificationManager.showSosAlert(newState.activeChildName, address, lat, lng, emergencyPhone)
                    }
                    if (previousLocked != isLocked) {
                        notificationManager.showDeviceLockNotification(newState.activeChildName, isLocked)
                    }
                }
        } catch (e: Exception) {
            // Offline / graceful local fallback
        }
    }

    fun switchActiveChild(name: String, avatar: String = "🦊") {
        val found = _syncState.value.householdChildren.find { it.name.equals(name, ignoreCase = true) }
        val finalAvatar = found?.avatarUrl ?: avatar
        val finalAge = found?.age ?: _syncState.value.childAge

        _syncState.value = _syncState.value.copy(
            activeChildName = name,
            activeChildAvatar = finalAvatar,
            childAge = finalAge
        )
        preferences.activeChildName = name
        preferences.activeChildAvatar = finalAvatar

        scope.launch {
            try {
                firestore?.collection("nestcheck_families")
                    ?.document(_syncState.value.familyCode)
                    ?.set(
                        mapOf(
                            "activeChildName" to name,
                            "activeChildAvatar" to finalAvatar,
                            "childAge" to finalAge
                        ),
                        com.google.firebase.firestore.SetOptions.merge()
                    )
            } catch (e: Exception) {}
        }
    }

    fun updateChildAvatar(avatar: String) {
        val updatedKids = _syncState.value.householdChildren.map {
            if (it.name.equals(_syncState.value.activeChildName, ignoreCase = true)) {
                it.copy(avatarUrl = avatar)
            } else it
        }

        _syncState.value = _syncState.value.copy(
            activeChildAvatar = avatar,
            householdChildren = updatedKids
        )
        preferences.activeChildAvatar = avatar

        scope.launch {
            try {
                firestore?.collection("nestcheck_families")
                    ?.document(_syncState.value.familyCode)
                    ?.set(
                        mapOf("activeChildAvatar" to avatar),
                        com.google.firebase.firestore.SetOptions.merge()
                    )
            } catch (e: Exception) {}
        }
    }

    fun switchActiveParent(role: String) {
        val name = when (role) {
            "Father" -> _syncState.value.fatherName
            "Mother" -> _syncState.value.motherName
            "Guardian" -> _syncState.value.guardianName
            else -> _syncState.value.fatherName
        }
        _syncState.value = _syncState.value.copy(
            activeParentRole = role,
            activeParentName = name
        )
        preferences.activeParentRole = role
        preferences.parentName = name
    }

    fun updateParentContactDetails(
        father: ParentContactInfo,
        mother: ParentContactInfo,
        guardian: ParentContactInfo,
        emergencyPhone: String,
        activeRole: String = "Father"
    ) {
        updateParentContactDetails(
            fatherName = father.name,
            fatherEmail = father.email,
            fatherPhone = father.phone,
            motherName = mother.name,
            motherEmail = mother.email,
            motherPhone = mother.phone,
            guardianName = guardian.name,
            guardianEmail = guardian.email,
            guardianPhone = guardian.phone,
            emergencyPhone = emergencyPhone,
            emergencyName = activeRole
        )
        switchActiveParent(activeRole)
        _syncState.value = _syncState.value.copy(
            householdParents = HouseholdParentConfig(father, mother, guardian)
        )
    }

    fun updateParentContactDetails(
        fatherName: String,
        fatherEmail: String,
        fatherPhone: String,
        motherName: String,
        motherEmail: String,
        motherPhone: String,
        guardianName: String,
        guardianEmail: String,
        guardianPhone: String,
        emergencyPhone: String,
        emergencyName: String = "Parent"
    ) {
        preferences.fatherName = fatherName
        preferences.fatherEmail = fatherEmail
        preferences.fatherPhone = fatherPhone
        preferences.motherName = motherName
        preferences.motherEmail = motherEmail
        preferences.motherPhone = motherPhone
        preferences.guardianName = guardianName
        preferences.guardianEmail = guardianEmail
        preferences.guardianPhone = guardianPhone
        preferences.emergencyContactPhone = emergencyPhone
        preferences.emergencyContactName = emergencyName

        val currentRole = preferences.activeParentRole
        val currentName = when (currentRole) {
            "Father" -> fatherName
            "Mother" -> motherName
            "Guardian" -> guardianName
            else -> fatherName
        }

        _syncState.value = _syncState.value.copy(
            fatherName = fatherName,
            fatherEmail = fatherEmail,
            fatherPhone = fatherPhone,
            motherName = motherName,
            motherEmail = motherEmail,
            motherPhone = motherPhone,
            guardianName = guardianName,
            guardianEmail = guardianEmail,
            guardianPhone = guardianPhone,
            emergencyContactPhone = emergencyPhone,
            emergencyContactName = emergencyName,
            activeParentName = currentName
        )

        scope.launch {
            try {
                firestore?.collection("nestcheck_families")
                    ?.document(_syncState.value.familyCode)
                    ?.set(
                        mapOf(
                            "emergencyContactPhone" to emergencyPhone,
                            "emergencyContactName" to emergencyName,
                            "fatherName" to fatherName,
                            "fatherPhone" to fatherPhone,
                            "motherName" to motherName,
                            "motherPhone" to motherPhone,
                            "guardianName" to guardianName,
                            "guardianPhone" to guardianPhone
                        ),
                        com.google.firebase.firestore.SetOptions.merge()
                    )
            } catch (e: Exception) {}
        }
    }

    fun triggerGeofenceBreach(zoneName: String, address: String, lat: Double, lng: Double, isExit: Boolean = true) {
        _syncState.value = _syncState.value.copy(
            geofenceBreachActive = true,
            geofenceBreachZone = zoneName,
            geofenceBreachAddress = address,
            geofenceBreachTimestamp = System.currentTimeMillis(),
            kidLatitude = lat,
            kidLongitude = lng,
            locationAddress = address,
            lastLocationUpdate = System.currentTimeMillis()
        )

        notificationManager.showGeofenceAlert(
            childName = _syncState.value.activeChildName,
            zoneName = zoneName,
            isExit = isExit,
            address = address,
            lat = lat,
            lng = lng
        )

        scope.launch {
            try {
                firestore?.collection("nestcheck_families")
                    ?.document(_syncState.value.familyCode)
                    ?.set(
                        mapOf(
                            "geofenceBreachActive" to true,
                            "geofenceBreachZone" to zoneName,
                            "geofenceBreachAddress" to address,
                            "kidLatitude" to lat,
                            "kidLongitude" to lng,
                            "locationAddress" to address,
                            "geofenceBreachTimestamp" to System.currentTimeMillis()
                        ),
                        com.google.firebase.firestore.SetOptions.merge()
                    )
            } catch (e: Exception) {}
        }
    }

    fun dismissGeofenceBreach() {
        _syncState.value = _syncState.value.copy(geofenceBreachActive = false)
        scope.launch {
            try {
                firestore?.collection("nestcheck_families")
                    ?.document(_syncState.value.familyCode)
                    ?.set(mapOf("geofenceBreachActive" to false), com.google.firebase.firestore.SetOptions.merge())
            } catch (e: Exception) {}
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
            } catch (e: Exception) {}
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
            notificationManager.showSosAlert(
                childName = _syncState.value.activeChildName,
                locationText = location,
                lat = lat,
                lng = lng,
                parentPhone = _syncState.value.emergencyContactPhone
            )
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

    fun triggerChildEmergencySOS(location: String = "MPSTME NMIMS Campus, Vile Parle West, Mumbai", lat: Double = 19.1075, lng: Double = 72.8372) {
        triggerKidSos(true, location, lat, lng)
    }

    fun dismissSosEmergency() {
        triggerKidSos(false, "", _syncState.value.kidLatitude, _syncState.value.kidLongitude)
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

    fun updateChildProfile(name: String, age: Int, dailyLimitMinutes: Int, bedtime: String, schoolHours: String, avatar: String = "🦊") {
        _syncState.value = _syncState.value.copy(
            activeChildName = name,
            activeChildAvatar = avatar,
            childAge = age,
            dailyLimitMinutes = dailyLimitMinutes,
            bedtime = bedtime,
            schoolHours = schoolHours
        )
        preferences.activeChildName = name
        preferences.activeChildAvatar = avatar
        preferences.dailyLimitMinutes = dailyLimitMinutes

        scope.launch {
            try {
                firestore?.collection("nestcheck_families")
                    ?.document(_syncState.value.familyCode)
                    ?.set(
                        mapOf(
                            "activeChildName" to name,
                            "activeChildAvatar" to avatar,
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
