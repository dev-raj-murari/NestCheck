package com.nestcheck.app.data.sync

import android.content.Context
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

data class FamilySyncState(
    val familyCode: String = "NC-8294",
    val activeChildName: String = "Olivia",
    val isDeviceLocked: Boolean = false,
    val remainingMinutes: Int = 135,
    val dailyLimitMinutes: Int = 180,
    val kidLatitude: Double = 37.7749,
    val kidLongitude: Double = -122.4194,
    val kidBattery: Int = 84,
    val isCharging: Boolean = false,
    val locationAddress: String = "1825 Jersey Street, San Francisco",
    val lastLocationUpdate: Long = System.currentTimeMillis(),
    val sosActive: Boolean = false,
    val sosTimestamp: Long = 0L,
    val sosLocation: String = "",
    val lockedApps: List<String> = listOf("com.instagram.android", "com.zhiliaoapp.musically"),
    val inAppPurchasesBlocked: Boolean = true, // Google Play Payment Block
    val appApprovalsRequired: Boolean = true
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
            dailyLimitMinutes = preferences.dailyLimitMinutes
        )
    )
    val syncState: StateFlow<FamilySyncState> = _syncState.asStateFlow()

    private var firestoreListener: ListenerRegistration? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    init {
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
}
