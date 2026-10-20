package com.nestcheck.app.ui.child.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.nestcheck.app.data.model.ChildProfile
import com.nestcheck.app.data.repository.ProfileRepository
import com.nestcheck.app.navigation.Screen
import com.nestcheck.app.ui.child.credits.CreditRequestHistoryCard
import com.nestcheck.app.ui.child.credits.CreditRequestViewModel
import com.nestcheck.app.ui.child.credits.RequestCreditsDialog
import com.nestcheck.app.ui.child.home.components.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

val KidBgLight = Color(0xFFF0F4F9)
val KidCardWhite = Color(0xFFFFFFFF)
val KidTextDark = Color(0xFF1E293B)
val KidTextMuted = Color(0xFF64748B)
val KidBorder = Color(0xFFE2E8F0)
val KidBlue = Color(0xFF0B57D0)
val KidGreen = Color(0xFF10B981)
val KidRed = Color(0xFFEF4444)

@HiltViewModel
class KidHomeViewModel @Inject constructor(
    profileRepository: ProfileRepository
) : ViewModel() {
    val childProfile: StateFlow<ChildProfile> = profileRepository.childProfile
}

@Composable
fun KidHomeScreen(
    navController: NavController,
    viewModel: ChildDashboardViewModel = hiltViewModel(),
    creditRequestViewModel: CreditRequestViewModel = hiltViewModel()
) {
    val child by viewModel.childProfile.collectAsState()
    val wallet by viewModel.wallet.collectAsState()
    val pendingTasks by viewModel.pendingTasks.collectAsState()
    val upcomingExams by viewModel.upcomingExams.collectAsState()
    val appUsageStats by viewModel.appUsageStats.collectAsState()
    val calendarEvents by viewModel.calendarEvents.collectAsState()
    val creditRequests by creditRequestViewModel.requests.collectAsState()

    val geofenceBreach by viewModel.geofenceBreach
    val isRequestCreditsDialogOpen by viewModel.isRequestCreditsDialogOpen

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KidBgLight)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. TOP HEADER & PROFILE STRIP (NO switch role button - child app is strictly isolated)
        ChildDashboardHeader(
            profile = child,
            isCurfewLocked = wallet.isCurfewLocked || wallet.activeRemainingSeconds <= 0L,
            onSosClick = { navController.navigate(Screen.SOS.route) }
        )

        // 2. CREDIT-TO-SCREEN TIME HERO CARD (2 CREDITS = 1 MIN RULE)
        CreditScreenTimeHeroCard(
            wallet = wallet
        )

        // 3. QUICK TIME CONTROLS WIDGET (+15m, +30m, Lock Now)
        QuickTimeControlsWidget(
            onSpendCreditsForMinutes = { credits -> viewModel.quickConvertCredits(credits) },
            onQuickAddMinutes = { mins -> viewModel.quickAddMinutes(mins) },
            onLockNow = { viewModel.lockDeviceImmediately() }
        )

        // 4. PENDING TASKS & CHORES CARD
        PendingTasksCard(
            tasks = pendingTasks,
            onToggleTask = { taskId -> viewModel.toggleTask(taskId) },
            onRequestCreditsClick = { viewModel.openRequestCreditsDialog() }
        )

        // 5. UPCOMING SCHOOL EXAMS & PREPARATION SCHEDULE
        UpcomingExamsCard(
            exams = upcomingExams
        )

        // 6. APP USAGE METERS & BLOCKED QUARANTINE APPS
        AppUsageMonitorCard(
            appStats = appUsageStats
        )

        // 7. INTERACTIVE WEEKLY STUDY & EXAM CALENDAR
        ChildCalendarCard(
            events = calendarEvents
        )

        // 8. CREDIT REQUEST STATUS AUDIT HISTORY
        CreditRequestHistoryCard(
            requests = creditRequests
        )

        // 9. GEOFENCE PERIMETER CROSSING SIMULATOR (Kept at bottom)
        GeofenceCrossingSimulator(
            breachResult = geofenceBreach,
            onSimulateBreach = { viewModel.simulateCrossingGeofence() },
            onDismissBreach = { viewModel.dismissGeofenceAlert() }
        )

        // 10. EMERGENCY SOS BUTTON
        Surface(
            color = Color(0xFFFEF2F2),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFECACA)),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { navController.navigate(Screen.SOS.route) }
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFFEE2E2)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Emergency SOS",
                        tint = KidRed,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column {
                    Text(
                        text = "Emergency SOS",
                        color = KidRed,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Instantly notify parents and dial emergency services",
                        color = KidTextMuted,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // FOOTER
        Text(
            text = "NestCheck Real-Time Guardian • 2 Credits = 1 Min Screen Time Active",
            color = KidTextMuted,
            fontSize = 11.sp,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 8.dp)
        )
    }

    // MODAL DIALOG: REQUEST EXTRA CREDITS FROM PARENTS
    if (isRequestCreditsDialogOpen) {
        RequestCreditsDialog(
            viewModel = creditRequestViewModel,
            onDismiss = { viewModel.closeRequestCreditsDialog() }
        )
    }
}
