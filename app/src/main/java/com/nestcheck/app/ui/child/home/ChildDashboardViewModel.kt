package com.nestcheck.app.ui.child.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nestcheck.app.data.model.*
import com.nestcheck.app.data.repository.ChildDashboardRepository
import com.nestcheck.app.data.repository.CreditScreenTimeRepository
import com.nestcheck.app.data.repository.LocationRepository
import com.nestcheck.app.data.repository.NotificationRepository
import com.nestcheck.app.data.repository.ProfileRepository
import com.nestcheck.app.data.sync.RealtimeFamilySyncManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChildDashboardViewModel @Inject constructor(
    private val creditRepository: CreditScreenTimeRepository,
    private val dashboardRepository: ChildDashboardRepository,
    private val locationRepository: LocationRepository,
    private val notificationRepository: NotificationRepository,
    private val syncManager: RealtimeFamilySyncManager,
    profileRepository: ProfileRepository
) : ViewModel() {

    val childProfile: StateFlow<ChildProfile> = profileRepository.childProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ChildProfile())

    val wallet: StateFlow<CreditScreenTimeWallet> = creditRepository.wallet
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CreditScreenTimeWallet())

    val pendingTasks: StateFlow<List<PendingTaskItem>> = dashboardRepository.pendingTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val upcomingExams: StateFlow<List<UpcomingExam>> = dashboardRepository.upcomingExams
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val appUsageStats: StateFlow<List<AppUsageInfo>> = dashboardRepository.appUsageStats
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val calendarEvents: StateFlow<List<CalendarEventItem>> = dashboardRepository.calendarEvents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _geofenceBreach = mutableStateOf<GeofenceBreachSimResult?>(null)
    val geofenceBreach: State<GeofenceBreachSimResult?> = _geofenceBreach

    private val _isRequestCreditsDialogOpen = mutableStateOf(false)
    val isRequestCreditsDialogOpen: State<Boolean> = _isRequestCreditsDialogOpen

    fun toggleTask(taskId: String) {
        viewModelScope.launch {
            dashboardRepository.toggleTask(taskId)
        }
    }

    fun simulateCrossingGeofence() {
        val alert = locationRepository.simulateChildPerimeterBreach("Oakridge High School")
        
        // 1. Update live telemetry to sync with Parent app
        syncManager.pushKidTelemetry(
            lat = alert.latitude,
            lng = alert.longitude,
            battery = syncManager.syncState.value.kidBattery,
            address = alert.address
        )

        // 2. Dispatch real-time high-priority push notification to parent hub
        val timeNow = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
        notificationRepository.addNotification(
            type = NotificationType.GEOFENCE_EXIT,
            title = "🚨 Oakridge School Geofence Alert",
            message = "${childProfile.value.name} exited Oakridge High School boundary at $timeNow. Live GPS tracking active."
        )

        // 3. Set local breach state
        _geofenceBreach.value = GeofenceBreachSimResult(
            id = UUID.randomUUID().toString(),
            zoneName = alert.zoneName,
            breachType = "UNAUTHORIZED PERIMETER CROSSING (EXIT)",
            latitude = alert.latitude,
            longitude = alert.longitude,
            timestamp = alert.timestamp,
            isParentNotified = true,
            alertMessage = "Child crossed Oakridge High School safe perimeter! Parent app immediately notified via instant push & GPS telemetry dispatch."
        )
    }

    fun dismissGeofenceAlert() {
        _geofenceBreach.value = null
    }

    fun openRequestCreditsDialog() {
        _isRequestCreditsDialogOpen.value = true
    }

    fun closeRequestCreditsDialog() {
        _isRequestCreditsDialogOpen.value = false
    }

    fun quickConvertCredits(credits: Int) {
        viewModelScope.launch {
            creditRepository.redeemCreditsForScreenTime(credits)
        }
    }

    fun quickAddMinutes(minutes: Int) {
        viewModelScope.launch {
            val credits = minutes * 2
            creditRepository.addCredits(
                amount = credits,
                type = CreditTransactionType.PARENT_GRANTED,
                title = "Quick Extension: +$minutes mins"
            )
        }
    }

    fun lockDeviceImmediately() {
        viewModelScope.launch {
            creditRepository.setRemainingMinutesDirect(0)
        }
    }
}
