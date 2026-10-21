package com.nestcheck.app.data.sync

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.nestcheck.app.core.NestCheckNotificationManager
import com.nestcheck.app.data.local.NestCheckPreferences
import com.nestcheck.app.data.model.ChildProfile
import com.nestcheck.app.data.model.ParentContactInfo
import com.nestcheck.app.data.model.HouseholdParentConfig
import com.nestcheck.app.data.model.PendingTaskItem
import com.nestcheck.app.data.model.TaskApprovalStatus
import com.nestcheck.app.data.model.TaskCategory
import com.nestcheck.app.data.model.CreditTransactionType
import com.nestcheck.app.data.model.ChildArrivalCheckIn
import com.nestcheck.app.data.repository.CreditScreenTimeRepository
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
    val dayOfWeek: String = "Monday",
    val subject: String = "",
    val timeSlot: String = "",
    val roomOrTeacher: String = "MPSTME Room 402"
)

data class GeofenceAlertEvent(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String = "PERIMETER CROSSING DETECTED",
    val message: String = "The child crossed the configured school perimeter.",
    val childName: String = "Leo",
    val schoolOrGeofenceName: String = "MPSTME NMIMS Campus",
    val timestamp: Long = System.currentTimeMillis(),
    val formattedTime: String = "",
    val isTestEvent: Boolean = true,
    val isAcknowledged: Boolean = false,
    val isDismissed: Boolean = false,
    val latitude: Double = 19.1075,
    val longitude: Double = 72.8372
)

data class FamilySyncState(
    val childCredits: Int = 180,
    val activeGeofenceAlert: GeofenceAlertEvent? = null,
    val geofenceAlertHistory: List<GeofenceAlertEvent> = emptyList(),
    val latestCheckIn: ChildArrivalCheckIn? = null,
    val checkInHistory: List<ChildArrivalCheckIn> = emptyList(),
    val tasks: List<PendingTaskItem> = listOf(
        PendingTaskItem(
            id = "task_1",
            title = "Clean Up Bedroom & Study Desk",
            category = TaskCategory.CHORE,
            creditsReward = 30,
            isCompleted = false,
            approvalStatus = TaskApprovalStatus.NOT_COMPLETED,
            dueDate = "Today, 5:30 PM",
            childName = "Leo"
        ),
        PendingTaskItem(
            id = "task_2",
            title = "Complete Algebra Worksheet (Ch 4)",
            category = TaskCategory.STUDY,
            creditsReward = 40,
            isCompleted = false,
            approvalStatus = TaskApprovalStatus.NOT_COMPLETED,
            dueDate = "Today, 7:00 PM",
            childName = "Leo"
        ),
        PendingTaskItem(
            id = "task_3",
            title = "Read 20 Pages of Science Book",
            category = TaskCategory.READING,
            creditsReward = 20,
            isCompleted = false,
            approvalStatus = TaskApprovalStatus.NOT_COMPLETED,
            dueDate = "Tonight, 8:30 PM",
            childName = "Leo"
        ),
        PendingTaskItem(
            id = "task_4",
            title = "Drink 4 Glasses of Water & Stretch",
            category = TaskCategory.WELLBEING,
            creditsReward = 10,
            isCompleted = true,
            approvalStatus = TaskApprovalStatus.APPROVED_AND_CREDITED,
            dueDate = "Completed",
            childName = "Leo"
        )
    ),
    val familyCode: String = "NC-8294",
    val activeChildName: String = "Leo",
    val activeChildAvatar: String = "🦊",
    val isDeviceLocked: Boolean = false,
    val remainingMinutes: Int = 105,
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
        // Monday
        TimetableItem(dayOfWeek = "Monday", subject = "Human Computer Interaction [MMO]", timeSlot = "02:00 PM - 03:00 PM", roomOrTeacher = "CR-502 • Dr. Masooda Modak"),
        TimetableItem(dayOfWeek = "Monday", subject = "Visual Analytics Lab [SVE]", timeSlot = "02:00 PM - 03:00 PM", roomOrTeacher = "CL-505 • Dr. Saurav Verma"),
        TimetableItem(dayOfWeek = "Monday", subject = "HCI Practical Batch A1 / A2", timeSlot = "03:00 PM - 04:00 PM", roomOrTeacher = "CL-502 / CL-505"),
        TimetableItem(dayOfWeek = "Monday", subject = "Open Elective I (OE I)", timeSlot = "04:00 PM - 05:00 PM", roomOrTeacher = "CR-502"),
        TimetableItem(dayOfWeek = "Monday", subject = "Open Elective I (OE I)", timeSlot = "05:00 PM - 06:00 PM", roomOrTeacher = "CR-502"),

        // Tuesday
        TimetableItem(dayOfWeek = "Tuesday", subject = "Human Computer Interaction [MMO]", timeSlot = "11:00 AM - 12:00 PM", roomOrTeacher = "CR-508 • Dr. Masooda Modak"),
        TimetableItem(dayOfWeek = "Tuesday", subject = "Embedded Systems [VKU]", timeSlot = "12:00 PM - 01:00 PM", roomOrTeacher = "CR-508 • Dr. Vikram Kulkarni"),
        TimetableItem(dayOfWeek = "Tuesday", subject = "Campus Lunch & Break", timeSlot = "01:00 PM - 02:00 PM", roomOrTeacher = "MPSTME Canteen"),
        TimetableItem(dayOfWeek = "Tuesday", subject = "MIS [PDA] / ADS [ARA] / AI & Robotics [SVE]", timeSlot = "02:00 PM - 03:00 PM", roomOrTeacher = "CR-501 / CR-507 / CR-502"),
        TimetableItem(dayOfWeek = "Tuesday", subject = "Open Elective I (OE I)", timeSlot = "04:00 PM - 05:00 PM", roomOrTeacher = "CR-508"),
        TimetableItem(dayOfWeek = "Tuesday", subject = "Open Elective I (OE I)", timeSlot = "05:00 PM - 06:00 PM", roomOrTeacher = "CR-508"),

        // Wednesday
        TimetableItem(dayOfWeek = "Wednesday", subject = "Human Computer Interaction [MMO]", timeSlot = "11:00 AM - 12:00 PM", roomOrTeacher = "CR-502 • Dr. Masooda Modak"),
        TimetableItem(dayOfWeek = "Wednesday", subject = "Visual Analytics [SVE] / ES [VKU]", timeSlot = "12:00 PM - 01:00 PM", roomOrTeacher = "CL-501 / CL-502"),
        TimetableItem(dayOfWeek = "Wednesday", subject = "Lab Practical Batch A1 / A2", timeSlot = "01:00 PM - 02:00 PM", roomOrTeacher = "CL-501 / CL-502"),
        TimetableItem(dayOfWeek = "Wednesday", subject = "Campus Lunch & Break", timeSlot = "02:00 PM - 03:00 PM", roomOrTeacher = "MPSTME Canteen"),
        TimetableItem(dayOfWeek = "Wednesday", subject = "Software Engineering [DRA]", timeSlot = "03:00 PM - 04:00 PM", roomOrTeacher = "CR-501 • Dr. Dharmesh Rathod"),
        TimetableItem(dayOfWeek = "Wednesday", subject = "Open Elective I (OE I)", timeSlot = "04:00 PM - 05:00 PM", roomOrTeacher = "CR-501"),
        TimetableItem(dayOfWeek = "Wednesday", subject = "Open Elective I (OE I)", timeSlot = "05:00 PM - 06:00 PM", roomOrTeacher = "CR-501"),

        // Thursday
        TimetableItem(dayOfWeek = "Thursday", subject = "MIS* [PDA] / ADS [ARA] / AI & Robotics* [SVE]", timeSlot = "10:00 AM - 11:00 AM", roomOrTeacher = "CR-502"),
        TimetableItem(dayOfWeek = "Thursday", subject = "Advanced Communication Lab", timeSlot = "11:00 AM - 12:00 PM", roomOrTeacher = "CL-501 • 6th Floor"),
        TimetableItem(dayOfWeek = "Thursday", subject = "Software Engineering [DRA]", timeSlot = "12:00 PM - 01:00 PM", roomOrTeacher = "CR-508 • Dr. Dharmesh Rathod"),
        TimetableItem(dayOfWeek = "Thursday", subject = "Campus Lunch & Break", timeSlot = "01:00 PM - 02:00 PM", roomOrTeacher = "MPSTME Canteen"),
        TimetableItem(dayOfWeek = "Thursday", subject = "Embedded Systems [VKU]", timeSlot = "02:00 PM - 03:00 PM", roomOrTeacher = "ES Lab • 4th Floor"),
        TimetableItem(dayOfWeek = "Thursday", subject = "ES Lab A1 / A2 Practical", timeSlot = "03:00 PM - 04:00 PM", roomOrTeacher = "ES Lab / CL-502"),
        TimetableItem(dayOfWeek = "Thursday", subject = "Open Elective II (OE II)", timeSlot = "04:00 PM - 05:00 PM", roomOrTeacher = "CR-508"),
        TimetableItem(dayOfWeek = "Thursday", subject = "Open Elective II (OE II)", timeSlot = "05:00 PM - 06:00 PM", roomOrTeacher = "CR-508"),

        // Friday
        TimetableItem(dayOfWeek = "Friday", subject = "Embedded Systems [VKU]", timeSlot = "12:00 PM - 01:00 PM", roomOrTeacher = "CR-501 • Dr. Vikram Kulkarni"),
        TimetableItem(dayOfWeek = "Friday", subject = "Campus Lunch & Break", timeSlot = "01:00 PM - 02:00 PM", roomOrTeacher = "MPSTME Canteen"),
        TimetableItem(dayOfWeek = "Friday", subject = "Software Engineering [DRA] / HCI [MMO]", timeSlot = "02:00 PM - 03:00 PM", roomOrTeacher = "CR-501 / CR-502"),
        TimetableItem(dayOfWeek = "Friday", subject = "Practical Batch A1 / A2", timeSlot = "03:00 PM - 04:00 PM", roomOrTeacher = "CL-501 / CL-502"),
        TimetableItem(dayOfWeek = "Friday", subject = "Open Elective II (OE II)", timeSlot = "04:00 PM - 05:00 PM", roomOrTeacher = "CR-501"),
        TimetableItem(dayOfWeek = "Friday", subject = "Open Elective II (OE II)", timeSlot = "05:00 PM - 06:00 PM", roomOrTeacher = "CR-501"),

        // Saturday
        TimetableItem(dayOfWeek = "Saturday", subject = "AI & Robotics Project Mentorship", timeSlot = "10:00 AM - 12:00 PM", roomOrTeacher = "Maker Space • Prof. SVE"),
        TimetableItem(dayOfWeek = "Saturday", subject = "Embedded Systems Practical Review", timeSlot = "12:00 PM - 01:00 PM", roomOrTeacher = "CR-501"),
        TimetableItem(dayOfWeek = "Saturday", subject = "Campus Lunch & Break", timeSlot = "01:00 PM - 02:00 PM", roomOrTeacher = "MPSTME Canteen"),
        TimetableItem(dayOfWeek = "Saturday", subject = "Software Engineering Capstone", timeSlot = "02:00 PM - 04:00 PM", roomOrTeacher = "IT Lab 1")
    )
)

@Singleton
class RealtimeFamilySyncManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferences: NestCheckPreferences,
    private val notificationManager: NestCheckNotificationManager,
    private val creditRepository: CreditScreenTimeRepository
) {
    private val firestore by lazy {
        try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            null
        }
    }

    val rtdb by lazy {
        try {
            FirebaseDatabase.getInstance("https://nestcheck-f0f6e-default-rtdb.firebaseio.com")
        } catch (e: Exception) {
            try {
                FirebaseDatabase.getInstance()
            } catch (e2: Exception) {
                null
            }
        }
    }


    private val _syncState = MutableStateFlow(
        FamilySyncState(
            familyCode = preferences.householdPairingCode,
            activeChildName = preferences.activeChildName,
            activeChildAvatar = preferences.activeChildAvatar,
            isDeviceLocked = preferences.isDeviceLocked,
            geofenceBreachActive = preferences.geofenceBreachActive,
            remainingMinutes = preferences.remainingMinutes,
            dailyLimitMinutes = preferences.dailyLimitMinutes,
            kidLatitude = preferences.kidLatitude,
            kidLongitude = preferences.kidLongitude,
            locationAddress = preferences.locationAddress,
            kidBattery = preferences.kidBattery,
            timetableSyncedToCalendar = preferences.timetableSyncedToCalendar,
            emergencyContactPhone = preferences.emergencyContactPhone,
            emergencyContactName = preferences.emergencyContactName,
            activeParentRole = preferences.activeParentRole,
            activeParentName = preferences.parentName,
            childAge = preferences.activeChildAge,
            householdChildren = listOf(
                ChildProfile(uid = "child_leo", name = "Leo", age = 11, gender = "Boy", avatarUrl = "🦊", grade = "Grade 6"),
                ChildProfile(uid = "child_olivia", name = "Olivia", age = 9, gender = "Girl", avatarUrl = "🦄", grade = "Grade 4"),
                ChildProfile(uid = "child_aarav", name = "Aarav", age = 10, gender = "Boy", avatarUrl = "🚀", grade = "Grade 5")
            ),
            fatherName = preferences.fatherName,
            fatherPhone = preferences.fatherPhone,
            fatherEmail = preferences.fatherEmail,
            motherName = preferences.motherName,
            motherPhone = preferences.motherPhone,
            motherEmail = preferences.motherEmail,
            guardianName = preferences.guardianName,
            guardianPhone = preferences.guardianPhone,
            guardianEmail = preferences.guardianEmail,
            childCredits = preferences.childCredits,
            timetablePdfName = preferences.timetablePdfName.ifBlank { "MPSTME_NMIMS_Timetable.pdf" },
            timetableItems = if (preferences.timetableItemsJson.isNotBlank()) deserializeTimetable(preferences.timetableItemsJson) else FamilySyncState().timetableItems
        )
    )
    val syncState: StateFlow<FamilySyncState> = _syncState.asStateFlow()

    private var firestoreListener: ListenerRegistration? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    private val prefChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
        val activeName = preferences.activeChildName
        val activeAvatar = preferences.activeChildAvatar
        val activeAge = preferences.activeChildAge
        _syncState.value = _syncState.value.copy(
            isDeviceLocked = preferences.isDeviceLocked,
            geofenceBreachActive = preferences.geofenceBreachActive,
            remainingMinutes = preferences.remainingMinutes,
            dailyLimitMinutes = preferences.dailyLimitMinutes,
            kidLatitude = preferences.kidLatitude,
            kidLongitude = preferences.kidLongitude,
            locationAddress = preferences.locationAddress,
            kidBattery = preferences.kidBattery,
            timetableSyncedToCalendar = preferences.timetableSyncedToCalendar,
            activeChildName = if (activeName.isNotBlank()) activeName else _syncState.value.activeChildName,
            activeChildAvatar = if (activeAvatar.isNotBlank()) activeAvatar else _syncState.value.activeChildAvatar,
            childAge = if (activeAge > 0) activeAge else _syncState.value.childAge,
            childCredits = preferences.childCredits,
            timetablePdfName = preferences.timetablePdfName,
            timetableItems = if (preferences.timetableItemsJson.isNotBlank()) deserializeTimetable(preferences.timetableItemsJson) else _syncState.value.timetableItems
        )
    }

    init {
        preferences.registerOnSharedPreferenceChangeListener(prefChangeListener)
        startSync(preferences.householdPairingCode)
    }

    fun startSync(familyCode: String) {
        firestoreListener?.remove()

        try {
            // 1. Children status listener
            rtdb?.getReference("children")?.child("status")
                ?.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val locked = snapshot.child("isDeviceLocked").getValue(Boolean::class.java)
                        val mins = snapshot.child("remainingMinutes").getValue(Long::class.java)?.toInt()
                        val sos = snapshot.child("sosActive").getValue(Boolean::class.java)
                        if (locked != null && locked != preferences.isDeviceLocked) {
                            preferences.isDeviceLocked = locked
                            _syncState.value = _syncState.value.copy(isDeviceLocked = locked)
                        }
                        if (mins != null && mins != preferences.remainingMinutes) {
                            preferences.remainingMinutes = mins
                            _syncState.value = _syncState.value.copy(remainingMinutes = mins)
                        }
                        if (sos != null && sos != preferences.sosActive) {
                            preferences.sosActive = sos
                            _syncState.value = _syncState.value.copy(sosActive = sos)
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {}
                })

            // 2. Real-Time Profile Listener: Parent updates child name/age/avatar -> child updates immediately!
            rtdb?.getReference("children")?.child("profile")
                ?.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (!snapshot.exists()) return
                        val name = snapshot.child("name").getValue(String::class.java)
                        val age = snapshot.child("age").getValue(Long::class.java)?.toInt()
                        val avatar = snapshot.child("avatar").getValue(String::class.java)
                        if (name != null && name != _syncState.value.activeChildName) {
                            preferences.activeChildName = name
                            _syncState.value = _syncState.value.copy(activeChildName = name)
                        }
                        if (age != null && age != _syncState.value.childAge) {
                            _syncState.value = _syncState.value.copy(childAge = age)
                        }
                        if (avatar != null && avatar != _syncState.value.activeChildAvatar) {
                            preferences.activeChildAvatar = avatar
                            _syncState.value = _syncState.value.copy(activeChildAvatar = avatar)
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {}
                })

            // 3. Real-Time Authoritative Credit Balance Listener
            rtdb?.getReference("children")?.child("credits")?.child("balance")
                ?.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val balance = snapshot.getValue(Long::class.java)?.toInt()
                        if (balance != null) {
                            _syncState.value = _syncState.value.copy(childCredits = balance)
                            preferences.childCredits = balance
                            creditRepository.syncWithExternalBalance(balance)
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {}
                })

            // 4. Real-Time Geofence Alert Listener (Parent console receives breach alert)
            rtdb?.getReference("children")?.child("geofence_alerts")?.child("active")
                ?.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (!snapshot.exists()) {
                            _syncState.value = _syncState.value.copy(activeGeofenceAlert = null, geofenceBreachActive = false)
                            preferences.geofenceBreachActive = false
                            return
                        }
                        val id = snapshot.child("id").getValue(String::class.java) ?: ""
                        val title = snapshot.child("title").getValue(String::class.java) ?: "PERIMETER CROSSING DETECTED"
                        val message = snapshot.child("message").getValue(String::class.java) ?: "The child crossed the configured school perimeter."
                        val child = snapshot.child("childName").getValue(String::class.java) ?: _syncState.value.activeChildName
                        val school = snapshot.child("schoolOrGeofenceName").getValue(String::class.java) ?: "MPSTME NMIMS Campus"
                        val time = snapshot.child("formattedTime").getValue(String::class.java) ?: ""
                        val isTest = snapshot.child("isTestEvent").getValue(Boolean::class.java) ?: true
                        val isAck = snapshot.child("isAcknowledged").getValue(Boolean::class.java) ?: false
                        val isDismiss = snapshot.child("isDismissed").getValue(Boolean::class.java) ?: false
                        val lat = snapshot.child("latitude").getValue(Double::class.java) ?: 19.1075
                        val lng = snapshot.child("longitude").getValue(Double::class.java) ?: 72.8372

                        val alert = GeofenceAlertEvent(
                            id = id,
                            title = title,
                            message = message,
                            childName = child,
                            schoolOrGeofenceName = school,
                            timestamp = snapshot.child("timestamp").getValue(Long::class.java) ?: System.currentTimeMillis(),
                            formattedTime = time,
                            isTestEvent = isTest,
                            isAcknowledged = isAck,
                            isDismissed = isDismiss,
                            latitude = lat,
                            longitude = lng
                        )
                        val prevAlert = _syncState.value.activeGeofenceAlert
                        _syncState.value = _syncState.value.copy(activeGeofenceAlert = alert, geofenceBreachActive = !isDismiss)
                        preferences.geofenceBreachActive = !isDismiss

                        if (!isDismiss && (prevAlert == null || prevAlert.id != id)) {
                            val addr = if (time.isNotBlank()) "$school ($time)" else school
                            notificationManager.showGeofenceAlert(child, school, isExit = true, address = addr, lat = lat, lng = lng)
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {}
                })

            // 5. Real-Time Safe Check-In Listener (Parent receives arrival notification)
            rtdb?.getReference("children")?.child("checkins")?.child("latest")
                ?.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (!snapshot.exists()) return
                        val id = snapshot.child("id").getValue(String::class.java) ?: ""
                        val child = snapshot.child("childName").getValue(String::class.java) ?: _syncState.value.activeChildName
                        val label = snapshot.child("locationLabel").getValue(String::class.java) ?: ""
                        val note = snapshot.child("customNote").getValue(String::class.java) ?: ""
                        val ts = snapshot.child("timestamp").getValue(Long::class.java) ?: System.currentTimeMillis()
                        val lat = snapshot.child("latitude").getValue(Double::class.java) ?: 19.1075
                        val lng = snapshot.child("longitude").getValue(Double::class.java) ?: 72.8372
                        val ack = snapshot.child("isParentAcknowledged").getValue(Boolean::class.java) ?: false

                        val checkIn = ChildArrivalCheckIn(
                            id = id,
                            childName = child,
                            timestamp = ts,
                            latitude = lat,
                            longitude = lng,
                            locationLabel = label,
                            customNote = note,
                            isParentAcknowledged = ack
                        )
                        val prevCheckIn = _syncState.value.latestCheckIn
                        _syncState.value = _syncState.value.copy(latestCheckIn = checkIn)
                        if (!ack && (prevCheckIn == null || prevCheckIn.id != id)) {
                            val timeStr = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()).format(java.util.Date(ts))
                            notificationManager.showSafeCheckInNotification(child, label.ifBlank { "MPSTME Campus" }, timeStr)
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {}
                })

            // 6. Real-Time Timetable Sync Listener
            rtdb?.getReference("children")?.child("timetable")
                ?.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (!snapshot.exists()) return
                        val pdfName = snapshot.child("pdfName").getValue(String::class.java) ?: _syncState.value.timetablePdfName
                        val synced = snapshot.child("syncedToCalendar").getValue(Boolean::class.java) ?: _syncState.value.timetableSyncedToCalendar
                        val itemsSnapshot = snapshot.child("items")
                        val items = mutableListOf<TimetableItem>()
                        for (childSnap in itemsSnapshot.children) {
                            val day = childSnap.child("dayOfWeek").getValue(String::class.java) ?: ""
                            val subject = childSnap.child("subject").getValue(String::class.java) ?: ""
                            val timeSlot = childSnap.child("timeSlot").getValue(String::class.java) ?: ""
                            val room = childSnap.child("roomOrTeacher").getValue(String::class.java) ?: ""
                            val id = childSnap.child("id").getValue(String::class.java) ?: java.util.UUID.randomUUID().toString()
                            if (subject.isNotBlank()) {
                                items.add(TimetableItem(id = id, dayOfWeek = day, subject = subject, timeSlot = timeSlot, roomOrTeacher = room))
                            }
                        }
                        if (items.isNotEmpty()) {
                            _syncState.value = _syncState.value.copy(
                                timetablePdfName = pdfName,
                                timetableSyncedToCalendar = synced,
                                timetableItems = items
                            )
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {}
                })
        } catch (_: Exception) {}

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
                    preferences.kidLatitude = lat
                    preferences.kidLongitude = lng
                    preferences.kidBattery = battery
                    preferences.locationAddress = address
                    preferences.emergencyContactPhone = emergencyPhone

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

        preferences.activeChildName = name
        preferences.activeChildAvatar = finalAvatar
        preferences.activeChildAge = finalAge

        _syncState.value = _syncState.value.copy(
            activeChildName = name,
            activeChildAvatar = finalAvatar,
            childAge = finalAge
        )

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

    fun triggerGeofenceBreach(
        zoneName: String,
        address: String,
        lat: Double,
        lng: Double,
        isExit: Boolean = true,
        isTestEvent: Boolean = true
    ) {
        val timeNow = java.text.SimpleDateFormat("hh:mm a, MMM d", java.util.Locale.getDefault()).format(java.util.Date())
        val event = GeofenceAlertEvent(
            id = java.util.UUID.randomUUID().toString(),
            title = "PERIMETER CROSSING DETECTED",
            message = "The child crossed the configured school perimeter.",
            childName = _syncState.value.activeChildName,
            schoolOrGeofenceName = zoneName,
            timestamp = System.currentTimeMillis(),
            formattedTime = timeNow,
            isTestEvent = isTestEvent,
            isAcknowledged = false,
            isDismissed = false,
            latitude = lat,
            longitude = lng
        )

        _syncState.value = _syncState.value.copy(
            activeGeofenceAlert = event,
            geofenceBreachActive = true,
            geofenceBreachZone = zoneName,
            geofenceBreachAddress = address,
            geofenceBreachTimestamp = System.currentTimeMillis(),
            kidLatitude = lat,
            kidLongitude = lng,
            locationAddress = address,
            lastLocationUpdate = System.currentTimeMillis()
        )
        preferences.geofenceBreachActive = true

        notificationManager.showGeofenceAlert(
            childName = _syncState.value.activeChildName,
            zoneName = zoneName,
            isExit = isExit,
            address = address,
            lat = lat,
            lng = lng
        )

        logChildActivity(
            if (isTestEvent) "geofence_test_triggered" else "geofence_breach_detected",
            if (isTestEvent) "Simulated perimeter test event sent to Parent App" else "Boundary crossing detected"
        )

        // Save active breach event to Firebase RTDB & Firestore
        try {
            rtdb?.getReference("children")?.child("geofence_alerts")?.child("active")?.setValue(
                mapOf(
                    "id" to event.id,
                    "title" to event.title,
                    "message" to event.message,
                    "childName" to event.childName,
                    "schoolOrGeofenceName" to event.schoolOrGeofenceName,
                    "timestamp" to event.timestamp,
                    "formattedTime" to event.formattedTime,
                    "isTestEvent" to event.isTestEvent,
                    "isAcknowledged" to event.isAcknowledged,
                    "isDismissed" to event.isDismissed,
                    "latitude" to event.latitude,
                    "longitude" to event.longitude
                )
            )
        } catch (_: Exception) {}

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

    fun acknowledgeGeofenceAlert(id: String) {
        val current = _syncState.value.activeGeofenceAlert ?: return
        val updated = current.copy(isAcknowledged = true)
        _syncState.value = _syncState.value.copy(activeGeofenceAlert = updated)
        logParentActivity("geofence_alert_acknowledged", "Parent acknowledged perimeter alert for ${updated.childName}")
        try {
            rtdb?.getReference("children")?.child("geofence_alerts")?.child("active")?.child("isAcknowledged")?.setValue(true)
        } catch (_: Exception) {}
    }

    fun dismissGeofenceAlert(id: String) {
        val current = _syncState.value.activeGeofenceAlert
        val history = if (current != null) listOf(current.copy(isDismissed = true)) + _syncState.value.geofenceAlertHistory else _syncState.value.geofenceAlertHistory
        _syncState.value = _syncState.value.copy(
            activeGeofenceAlert = null,
            geofenceAlertHistory = history,
            geofenceBreachActive = false
        )
        preferences.geofenceBreachActive = false
        logParentActivity("geofence_alert_dismissed", "Parent dismissed active perimeter alert")
        try {
            rtdb?.getReference("children")?.child("geofence_alerts")?.child("active")?.removeValue()
            if (current != null) {
                rtdb?.getReference("children")?.child("geofence_alerts")?.child("history")?.child(current.id)?.setValue(current)
            }
        } catch (_: Exception) {}
        scope.launch {
            try {
                firestore?.collection("nestcheck_families")
                    ?.document(_syncState.value.familyCode)
                    ?.set(mapOf("geofenceBreachActive" to false), com.google.firebase.firestore.SetOptions.merge())
            } catch (e: Exception) {}
        }
    }

    fun dismissGeofenceBreach() {
        val alert = _syncState.value.activeGeofenceAlert
        if (alert != null) dismissGeofenceAlert(alert.id) else {
            preferences.geofenceBreachActive = false
            _syncState.value = _syncState.value.copy(geofenceBreachActive = false)
        }
    }

    fun setDeviceLocked(isLocked: Boolean) {
        _syncState.value = _syncState.value.copy(isDeviceLocked = isLocked)
        preferences.isDeviceLocked = isLocked
        notificationManager.showDeviceLockNotification(_syncState.value.activeChildName, isLocked)
        logParentActivity("device_lock_toggle", if (isLocked) "Device locked remotely by parent" else "Device unlocked remotely by parent")
        try {
            rtdb?.getReference("children")?.child("status")?.child("isDeviceLocked")?.setValue(isLocked)
        } catch (_: Exception) {}

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

    fun logParentActivity(event: String, details: String, extraData: Map<String, Any?> = emptyMap()) {
        val timestamp = System.currentTimeMillis()
        val entry = hashMapOf<String, Any>(
            "event" to event,
            "details" to details,
            "timestamp" to timestamp,
            "device" to android.os.Build.MODEL,
            "role" to "PARENT",
            "parentName" to _syncState.value.activeParentName,
            "parentRole" to _syncState.value.activeParentRole,
            "familyCode" to _syncState.value.familyCode
        )
        extraData.forEach { (k, v) -> if (v != null) entry[k] = v }

        // 1. Firebase Realtime Database: /parents/activity_logs/
        try {
            rtdb?.getReference("parents")?.child("activity_logs")?.push()?.setValue(entry)
            rtdb?.getReference("parents")?.child("status")?.updateChildren(
                mapOf(
                    "lastActive" to timestamp,
                    "activeParent" to _syncState.value.activeParentName,
                    "activeParentRole" to _syncState.value.activeParentRole,
                    "device" to android.os.Build.MODEL
                )
            )
        } catch (_: Exception) {}

        // 2. Cloud Firestore: parent_activity_logs
        try {
            firestore?.collection("parent_activity_logs")?.add(entry)
            firestore?.collection("parents")?.document(_syncState.value.activeParentRole.lowercase())
                ?.collection("activity_logs")?.add(entry)
            firestore?.collection("user_activity_logs")?.add(entry)
        } catch (_: Exception) {}
    }

    fun logChildActivity(event: String, details: String, extraData: Map<String, Any?> = emptyMap()) {
        val timestamp = System.currentTimeMillis()
        val entry = hashMapOf<String, Any>(
            "event" to event,
            "details" to details,
            "timestamp" to timestamp,
            "device" to android.os.Build.MODEL,
            "role" to "CHILD",
            "childName" to _syncState.value.activeChildName,
            "childAvatar" to _syncState.value.activeChildAvatar,
            "familyCode" to _syncState.value.familyCode
        )
        extraData.forEach { (k, v) -> if (v != null) entry[k] = v }

        // 1. Firebase Realtime Database: /children/activity_logs/
        try {
            rtdb?.getReference("children")?.child("activity_logs")?.push()?.setValue(entry)
            rtdb?.getReference("children")?.child("status")?.updateChildren(
                mapOf(
                    "lastActive" to timestamp,
                    "activeChild" to _syncState.value.activeChildName,
                    "activeChildAvatar" to _syncState.value.activeChildAvatar,
                    "device" to android.os.Build.MODEL,
                    "isDeviceLocked" to _syncState.value.isDeviceLocked,
                    "remainingMinutes" to _syncState.value.remainingMinutes,
                    "kidLatitude" to _syncState.value.kidLatitude,
                    "kidLongitude" to _syncState.value.kidLongitude,
                    "geofenceBreachActive" to _syncState.value.geofenceBreachActive,
                    "sosActive" to _syncState.value.sosActive
                )
            )
        } catch (_: Exception) {}

        // 2. Cloud Firestore: child_activity_logs
        try {
            firestore?.collection("child_activity_logs")?.add(entry)
            firestore?.collection("children")?.document(_syncState.value.activeChildName.lowercase())
                ?.collection("activity_logs")?.add(entry)
            firestore?.collection("user_activity_logs")?.add(entry)
        } catch (_: Exception) {}
    }

    fun logUserActivity(event: String, details: String) {
        val parentKeywords = listOf("parent", "admin", "lock_toggle", "screen_time", "rule", "calendar", "pin_setup", "timetable", "switch_parent")
        if (parentKeywords.any { event.contains(it, ignoreCase = true) }) {
            logParentActivity(event, details)
        } else {
            logChildActivity(event, details)
        }
    }

    fun addCredits(
        amount: Int,
        type: CreditTransactionType = CreditTransactionType.TASK_COMPLETED,
        title: String = "Credit Reward"
    ) {
        creditRepository.addCredits(amount, type, title)
        val updated = creditRepository.wallet.value.totalCredits
        _syncState.value = _syncState.value.copy(childCredits = updated)
        preferences.childCredits = updated

        try {
            rtdb?.getReference("children")?.child("credits")?.child("balance")?.setValue(updated)
            rtdb?.getReference("children")?.child("credits")?.child("lastUpdated")?.setValue(System.currentTimeMillis())
        } catch (_: Exception) {}

        scope.launch {
            try {
                firestore?.collection("family_state")?.document("current")
                    ?.set(mapOf("childCredits" to updated), com.google.firebase.firestore.SetOptions.merge())
            } catch (_: Exception) {}
        }
    }

    // Safe Check-In Implementation (Requirement 10)
    fun onChildCheckIn(label: String, note: String, lat: Double, lng: Double) {
        val timeNow = java.text.SimpleDateFormat("hh:mm a, MMM d", java.util.Locale.getDefault()).format(java.util.Date())
        val checkIn = ChildArrivalCheckIn(
            id = java.util.UUID.randomUUID().toString(),
            childName = _syncState.value.activeChildName,
            timestamp = System.currentTimeMillis(),
            latitude = lat,
            longitude = lng,
            locationLabel = label,
            customNote = note,
            batteryLevel = _syncState.value.kidBattery,
            isParentAcknowledged = false
        )
        _syncState.value = _syncState.value.copy(
            latestCheckIn = checkIn,
            checkInHistory = listOf(checkIn) + _syncState.value.checkInHistory
        )
        logChildActivity("safe_checkin_dispatched", "Child checked in safely: $label ($note)")
        notificationManager.showSafeCheckInNotification(_syncState.value.activeChildName, label, timeNow)

        try {
            rtdb?.getReference("children")?.child("checkins")?.child("latest")?.setValue(checkIn)
            rtdb?.getReference("children")?.child("checkins")?.child("history")?.child(checkIn.id)?.setValue(checkIn)
        } catch (_: Exception) {}
    }

    fun acknowledgeCheckIn(id: String) {
        val current = _syncState.value.latestCheckIn ?: return
        val updated = current.copy(isParentAcknowledged = true)
        _syncState.value = _syncState.value.copy(latestCheckIn = updated)
        logParentActivity("checkin_acknowledged", "Parent acknowledged arrival check-in for ${updated.childName}")
        try {
            rtdb?.getReference("children")?.child("checkins")?.child("latest")?.child("isParentAcknowledged")?.setValue(true)
        } catch (_: Exception) {}
    }

    // Task & Reward Workflow Implementation (Requirement 5)
    fun createTask(title: String, creditsReward: Int, category: TaskCategory = TaskCategory.CHORE) {
        val newTask = PendingTaskItem(
            id = java.util.UUID.randomUUID().toString(),
            title = title,
            category = category,
            creditsReward = creditsReward,
            isCompleted = false,
            approvalStatus = TaskApprovalStatus.NOT_COMPLETED,
            dueDate = "Today, 6:00 PM",
            childName = _syncState.value.activeChildName
        )
        val updatedTasks = listOf(newTask) + _syncState.value.tasks
        _syncState.value = _syncState.value.copy(tasks = updatedTasks)
        logParentActivity("task_created", "Parent created task: '$title' (+${creditsReward} credits)")
        syncTasksToBackend(updatedTasks)
    }

    fun submitTaskByChild(taskId: String) {
        val updatedTasks = _syncState.value.tasks.map {
            if (it.id == taskId) {
                it.copy(approvalStatus = TaskApprovalStatus.SUBMITTED_PENDING_APPROVAL)
            } else it
        }
        _syncState.value = _syncState.value.copy(tasks = updatedTasks)
        val task = updatedTasks.find { it.id == taskId }
        logChildActivity("task_submitted", "Submitted task '${task?.title}' for parent approval")
        notificationManager.showNotification(
            "📋 Task Awaiting Approval",
            "${_syncState.value.activeChildName} marked '${task?.title}' as done. Parent review required."
        )
        syncTasksToBackend(updatedTasks)
    }

    fun approveTaskByParent(taskId: String): Boolean {
        val task = _syncState.value.tasks.find { it.id == taskId } ?: return false
        if (task.approvalStatus == TaskApprovalStatus.APPROVED_AND_CREDITED) {
            return false // Prevent duplicate awards!
        }

        val updatedTasks = _syncState.value.tasks.map {
            if (it.id == taskId) {
                it.copy(
                    approvalStatus = TaskApprovalStatus.APPROVED_AND_CREDITED,
                    isCompleted = true
                )
            } else it
        }
        _syncState.value = _syncState.value.copy(tasks = updatedTasks)

        // Award configured credits exactly once!
        addCredits(
            amount = task.creditsReward,
            type = CreditTransactionType.TASK_COMPLETED,
            title = "Parent Approved: ${task.title}"
        )

        logParentActivity("task_approved", "Parent approved '${task.title}' and awarded +${task.creditsReward} credits")
        syncTasksToBackend(updatedTasks)
        return true
    }

    fun rejectTaskByParent(taskId: String, reason: String = "Please review and complete"): Boolean {
        val updatedTasks = _syncState.value.tasks.map {
            if (it.id == taskId) {
                it.copy(
                    approvalStatus = TaskApprovalStatus.REJECTED,
                    rejectionReason = reason,
                    isCompleted = false
                )
            } else it
        }
        _syncState.value = _syncState.value.copy(tasks = updatedTasks)
        val task = updatedTasks.find { it.id == taskId }
        logParentActivity("task_rejected", "Parent rejected '${task?.title}': $reason")
        syncTasksToBackend(updatedTasks)
        return true
    }

    private fun syncTasksToBackend(tasks: List<PendingTaskItem>) {
        try {
            rtdb?.getReference("children")?.child("tasks")?.setValue(
                tasks.map {
                    mapOf(
                        "id" to it.id,
                        "title" to it.title,
                        "category" to it.category.name,
                        "creditsReward" to it.creditsReward,
                        "isCompleted" to it.isCompleted,
                        "approvalStatus" to it.approvalStatus.name,
                        "dueDate" to it.dueDate,
                        "childName" to it.childName,
                        "rejectionReason" to (it.rejectionReason ?: "")
                    )
                }
            )
        } catch (_: Exception) {}
    }

    fun clearSOS() {
        logUserActivity("sos_dismissed", "Parent/Child dismissed SOS")
        preferences.sosActive = false
        _syncState.value = _syncState.value.copy(sosActive = false, sosTimestamp = 0L, sosLocation = "")
        scope.launch {
            try {
                firestore?.collection("nestcheck_families")
                    ?.document(_syncState.value.familyCode)
                    ?.set(
                        mapOf(
                            "sosActive" to false,
                            "sosTimestamp" to 0L,
                            "sosLocation" to ""
                        ),
                        com.google.firebase.firestore.SetOptions.merge()
                    )
            } catch (e: Exception) {}
        }
    }

    fun dismissSosEmergency() {
        clearSOS()
    }

    private fun legacy_dismissSosEmergency() {
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

    fun updateChildProfile(name: String, age: Int, dailyLimitMinutes: Int, bedtime: String, schoolHours: String, avatar: String = preferences.activeChildAvatar) {
        preferences.activeChildName = name
        preferences.activeChildAvatar = avatar
        preferences.activeChildAge = age
        preferences.dailyLimitMinutes = dailyLimitMinutes

        val updatedKids = _syncState.value.householdChildren.map {
            if (it.name.equals(name, ignoreCase = true) || it.name.equals(_syncState.value.activeChildName, ignoreCase = true)) {
                it.copy(name = name, age = age, avatarUrl = avatar)
            } else it
        }

        _syncState.value = _syncState.value.copy(
            activeChildName = name,
            activeChildAvatar = avatar,
            childAge = age,
            dailyLimitMinutes = dailyLimitMinutes,
            bedtime = bedtime,
            schoolHours = schoolHours,
            householdChildren = updatedKids
        )

        logParentActivity("child_profile_updated", "Updated profile for $name (Age $age, Avatar $avatar)")

        // Real-Time Database synchronization across parent and child apps (Requirement 3)
        try {
            rtdb?.getReference("children")?.child("profile")?.setValue(
                mapOf(
                    "name" to name,
                    "age" to age,
                    "avatar" to avatar,
                    "dailyLimitMinutes" to dailyLimitMinutes,
                    "bedtime" to bedtime,
                    "schoolHours" to schoolHours
                )
            )
        } catch (_: Exception) {}

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

    fun serializeTimetable(items: List<TimetableItem>): String {
        val array = org.json.JSONArray()
        for (item in items) {
            val obj = org.json.JSONObject()
            obj.put("id", item.id)
            obj.put("dayOfWeek", item.dayOfWeek)
            obj.put("subject", item.subject)
            obj.put("timeSlot", item.timeSlot)
            obj.put("roomOrTeacher", item.roomOrTeacher)
            array.put(obj)
        }
        return array.toString()
    }

    fun deserializeTimetable(json: String): List<TimetableItem> {
        if (json.isBlank()) return emptyList()
        val list = mutableListOf<TimetableItem>()
        try {
            val array = org.json.JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    TimetableItem(
                        id = obj.optString("id", java.util.UUID.randomUUID().toString()),
                        dayOfWeek = obj.optString("dayOfWeek", "Monday"),
                        subject = obj.optString("subject", ""),
                        timeSlot = obj.optString("timeSlot", ""),
                        roomOrTeacher = obj.optString("roomOrTeacher", "MPSTME")
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    fun uploadTimetablePdf(fileName: String, newItems: List<TimetableItem>) {
        _syncState.value = _syncState.value.copy(
            timetablePdfName = fileName,
            timetableItems = newItems,
            timetableSyncedToCalendar = false
        )
        preferences.timetablePdfName = fileName
        preferences.timetableItemsJson = serializeTimetable(newItems)
        preferences.timetableSyncedToCalendar = false
        try {
            val timetableRef = rtdb?.getReference("children")?.child("timetable")
            timetableRef?.child("pdfName")?.setValue(fileName)
            timetableRef?.child("syncedToCalendar")?.setValue(false)
            val itemsMap = newItems.map {
                mapOf(
                    "id" to it.id,
                    "dayOfWeek" to it.dayOfWeek,
                    "subject" to it.subject,
                    "timeSlot" to it.timeSlot,
                    "roomOrTeacher" to it.roomOrTeacher
                )
            }
            timetableRef?.child("items")?.setValue(itemsMap)
        } catch (e: Exception) {}
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

    fun addTimetableItem(item: TimetableItem) {
        val updated = _syncState.value.timetableItems + item
        uploadTimetablePdf(_syncState.value.timetablePdfName, updated)
    }

    fun deleteTimetableItem(itemId: String) {
        val updated = _syncState.value.timetableItems.filter { it.id != itemId }
        uploadTimetablePdf(_syncState.value.timetablePdfName, updated)
    }

    fun setTimetableCalendarSynced(synced: Boolean) {
        _syncState.value = _syncState.value.copy(timetableSyncedToCalendar = synced)
        preferences.timetableSyncedToCalendar = synced
        try {
            rtdb?.getReference("children")?.child("timetable")?.child("syncedToCalendar")?.setValue(synced)
        } catch (e: Exception) {}
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
