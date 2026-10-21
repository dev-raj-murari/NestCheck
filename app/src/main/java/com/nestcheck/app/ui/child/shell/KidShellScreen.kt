package com.nestcheck.app.ui.child.shell

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nestcheck.app.data.model.AppUsageInfo
import com.nestcheck.app.data.model.CalendarEventItem
import com.nestcheck.app.data.model.CreditRequest
import com.nestcheck.app.data.model.CreditScreenTimeWallet
import com.nestcheck.app.data.model.GeofenceBreachSimResult
import com.nestcheck.app.data.model.PendingTaskItem
import com.nestcheck.app.data.model.UpcomingExam
import com.nestcheck.app.data.sync.RealtimeFamilySyncManager
import com.nestcheck.app.navigation.Screen
import com.nestcheck.app.ui.child.credits.CreditRequestHistoryCard
import com.nestcheck.app.ui.child.credits.CreditRequestViewModel
import com.nestcheck.app.ui.child.credits.RequestCreditsDialog
import com.nestcheck.app.ui.child.home.ChildDashboardViewModel
import com.nestcheck.app.ui.child.home.components.*

enum class KidTab(val label: String, val icon: ImageVector) {
    HOME("Home", Icons.Outlined.Home),
    TASKS("Tasks & Rewards", Icons.Outlined.EmojiEvents),
    WELLNESS("Wellness", Icons.Outlined.SelfImprovement),
    SOS("Emergency SOS", Icons.Outlined.WarningAmber)
}

val KidBgLight = Color(0xFFF8F9FA)
val KidCardWhite = Color(0xFFFFFFFF)
val KidTextDark = Color(0xFF1F1F1F)
val KidTextMuted = Color(0xFF5E5E5E)
val KidBorder = Color(0xFFE0E2EC)
val KidBlue = Color(0xFF0B57D0)
val KidBlueContainer = Color(0xFFD3E3FD)
val KidGreen = Color(0xFF1E8E3E)
val KidRed = Color(0xFFD93025)
val KidAmber = Color(0xFFF9AB00)

@Composable
fun KidShellScreen(
    navController: NavController,
    viewModel: KidShellViewModel = hiltViewModel(),
    dashboardViewModel: ChildDashboardViewModel = hiltViewModel(),
    creditRequestViewModel: CreditRequestViewModel = hiltViewModel()
) {
    val syncState by viewModel.syncManager.syncState.collectAsState()
    val wallet by dashboardViewModel.wallet.collectAsState()
    val pendingTasks by dashboardViewModel.pendingTasks.collectAsState()
    val upcomingExams by dashboardViewModel.upcomingExams.collectAsState()
    val appUsageStats by dashboardViewModel.appUsageStats.collectAsState()
    val calendarEvents by dashboardViewModel.calendarEvents.collectAsState()
    val creditRequests by creditRequestViewModel.requests.collectAsState()

    val geofenceBreach by dashboardViewModel.geofenceBreach
    val isRequestCreditsDialogOpen by dashboardViewModel.isRequestCreditsDialogOpen

    var selectedTab by remember { mutableStateOf(KidTab.HOME) }
    var showPinDialog by remember { mutableStateOf(false) }
    var enteredPin by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf(false) }

    // Real-Time Parent Approval Gate (Kid cannot enter until parent approves)
    if (syncState.deviceApprovalStatus == com.nestcheck.app.data.sync.DeviceApprovalStatus.PENDING) {
        WaitingForParentApprovalScreen(
            deviceName = syncState.pendingDeviceName,
            onBackToLogin = {
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.KidHome.route) { inclusive = true }
                }
            }
        )
        return
    }

    if (syncState.deviceApprovalStatus == com.nestcheck.app.data.sync.DeviceApprovalStatus.REJECTED) {
        DeviceRejectedScreen(
            onBackToLogin = {
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.KidHome.route) { inclusive = true }
                }
            }
        )
        return
    }

    // Real-Time Full Screen Lockout Overlay if Device is Locked
    if (syncState.isDeviceLocked) {
        DeviceLockedOverlay(
            onUnlockAttempt = { pin ->
                if (pin == "1234" || pin == "8294") {
                    viewModel.syncManager.setDeviceLocked(false)
                    true
                } else {
                    false
                }
            }
        )
        return
    }

    Scaffold(
        containerColor = KidBgLight,
        topBar = {
            KidTopBar(
                childName = syncState.activeChildName,
                battery = syncState.kidBattery,
                onExitClick = { showPinDialog = true }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = KidCardWhite,
                tonalElevation = 4.dp,
                modifier = Modifier.border(1.dp, Color(0xFFF1F5F9))
            ) {
                KidTab.values().forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.label
                            )
                        },
                        label = {
                            Text(
                                text = tab.label,
                                fontSize = 11.sp,
                                fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = if (tab == KidTab.SOS) KidRed else KidBlue,
                            selectedTextColor = if (tab == KidTab.SOS) KidRed else KidBlue,
                            indicatorColor = if (tab == KidTab.SOS) Color(0xFFFEE2E2) else Color(0xFFDBEAFE),
                            unselectedIconColor = KidTextMuted,
                            unselectedTextColor = KidTextMuted
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                KidTab.HOME -> KidHomeContent(
                    navController = navController,
                    syncState = syncState,
                    wallet = wallet,
                    geofenceBreach = geofenceBreach,
                    upcomingExams = upcomingExams,
                    appUsageStats = appUsageStats,
                    calendarEvents = calendarEvents,
                    onSpendCredits = { dashboardViewModel.quickConvertCredits(it) },
                    onQuickAddMinutes = { dashboardViewModel.quickAddMinutes(it) },
                    onLockNow = { dashboardViewModel.lockDeviceImmediately() },
                    onSimulateBreach = { dashboardViewModel.simulateCrossingGeofence() },
                    onDismissBreach = { dashboardViewModel.dismissGeofenceAlert() }
                )
                KidTab.TASKS -> KidTasksContent(
                    wallet = wallet,
                    tasks = pendingTasks,
                    requests = creditRequests,
                    onToggleTask = { dashboardViewModel.toggleTask(it) },
                    onRequestCreditsClick = { dashboardViewModel.openRequestCreditsDialog() },
                    onSpendCredits = { dashboardViewModel.quickConvertCredits(it) }
                )
                KidTab.WELLNESS -> KidWellnessTab(navController)
                KidTab.SOS -> KidSosContent(viewModel.syncManager, syncState)
            }
        }

        // Parent Master PIN dialog to exit child space (No switch role button in UI)
        if (showPinDialog) {
            AlertDialog(
                onDismissRequest = {
                    showPinDialog = false
                    enteredPin = ""
                    pinError = false
                },
                title = { Text("Parent Exit Gate", fontWeight = FontWeight.Bold, color = KidTextDark) },
                text = {
                    Column {
                        Text("Enter the Parent Master PIN to exit child space:", color = KidTextMuted, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = enteredPin,
                            onValueChange = { if (it.length <= 4) enteredPin = it },
                            label = { Text("Parent PIN") },
                            visualTransformation = PasswordVisualTransformation(),
                            isError = pinError,
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (pinError) {
                            Text("Incorrect PIN. Access denied.", color = KidRed, fontSize = 12.sp)
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (enteredPin == "1234" || enteredPin == "8294") {
                                showPinDialog = false
                                navController.navigate(Screen.ParentDashboard.route) {
                                    popUpTo(Screen.KidHome.route) { inclusive = true }
                                }
                            } else {
                                pinError = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = KidBlue)
                    ) {
                        Text("Exit to Parent")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showPinDialog = false }) {
                        Text("Cancel", color = KidTextMuted)
                    }
                }
            )
        }

        // Modal Sheet: Request Extra Credits
        if (isRequestCreditsDialogOpen) {
            RequestCreditsDialog(
                viewModel = creditRequestViewModel,
                onDismiss = { dashboardViewModel.closeRequestCreditsDialog() }
            )
        }
    }
}

@Composable
fun KidTopBar(childName: String, battery: Int, onExitClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8F0FE)),
                contentAlignment = Alignment.Center
            ) {
                Text(childName.take(1), fontWeight = FontWeight.Bold, color = KidBlue, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text("Hey, $childName! 👋", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = KidTextDark)
                Text("$battery% Battery ⚡ • Safe Mode", fontSize = 12.sp, color = KidGreen, fontWeight = FontWeight.Medium)
            }
        }

        IconButton(
            onClick = onExitClick,
            modifier = Modifier
                .clip(CircleShape)
                .background(KidCardWhite)
        ) {
            Icon(Icons.Outlined.Lock, contentDescription = "Parent Gate", tint = KidTextMuted, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
fun KidHomeContent(
    navController: NavController,
    syncState: com.nestcheck.app.data.sync.FamilySyncState,
    wallet: CreditScreenTimeWallet,
    geofenceBreach: GeofenceBreachSimResult?,
    upcomingExams: List<UpcomingExam>,
    appUsageStats: List<AppUsageInfo>,
    calendarEvents: List<CalendarEventItem>,
    onSpendCredits: (Int) -> Unit,
    onQuickAddMinutes: (Int) -> Unit,
    onLockNow: () -> Unit,
    onSimulateBreach: () -> Unit,
    onDismissBreach: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Credit-to-Screen Time Hero Card (2 Credits = 1 Min rule)
        item {
            CreditScreenTimeHeroCard(wallet = wallet)
        }

        // 2. Quick Time Controls Widget (+15m, +30m, Lock Now)
        item {
            QuickTimeControlsWidget(
                onSpendCreditsForMinutes = onSpendCredits,
                onQuickAddMinutes = onQuickAddMinutes,
                onLockNow = onLockNow
            )
        }

        // 3. App Usage Meters & Blocked Quarantine Apps
        item {
            AppUsageMonitorCard(appStats = appUsageStats)
        }

        // 4. Upcoming School Exams & Preparation Schedule
        item {
            UpcomingExamsCard(exams = upcomingExams)
        }

        // 5. Interactive Weekly Study & Exam Calendar
        item {
            ChildCalendarCard(
                timetableItems = syncState.timetableItems,
                isCalendarSynced = syncState.timetableSyncedToCalendar,
                timetablePdfName = syncState.timetablePdfName
            )
        }

        // 6. Geofence Perimeter Crossing Simulator (Kept at bottom)
        item {
            GeofenceCrossingSimulator(
                breachResult = geofenceBreach,
                onSimulateBreach = onSimulateBreach,
                onDismissBreach = onDismissBreach
            )
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
fun KidTasksContent(
    wallet: CreditScreenTimeWallet,
    tasks: List<PendingTaskItem>,
    requests: List<CreditRequest>,
    onToggleTask: (String) -> Unit,
    onRequestCreditsClick: () -> Unit,
    onSpendCredits: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        // Consolidated Task Credit (2:1) Rule & Authoritative Balance Hero
        item {
            TaskCreditHeroCard(
                wallet = wallet,
                onRequestCreditsClick = onRequestCreditsClick,
                onSpendCredits = onSpendCredits
            )
        }

        // Pending Tasks with points & tap-to-complete
        item {
            PendingTasksCard(
                tasks = tasks,
                onToggleTask = onToggleTask,
                onRequestCreditsClick = onRequestCreditsClick
            )
        }

        // Credit Request Audit History
        item {
            CreditRequestHistoryCard(requests = requests)
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
fun TaskCreditHeroCard(
    wallet: CreditScreenTimeWallet,
    onRequestCreditsClick: () -> Unit,
    onSpendCredits: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = KidCardWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, KidBorder)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0xFFEFF6FF),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "TASK CREDIT RULE (2:1)",
                        color = KidBlue,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }

                Surface(
                    color = Color(0xFFEFF6FF),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE))
                ) {
                    Text(
                        text = "2 Credits = 1 Min Screen Time",
                        color = KidBlue,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "${wallet.totalCredits} Credits Available",
                color = KidTextDark,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Equivalent to ${wallet.remainingMinutes} mins (${wallet.equivalentScreenTimeFormatted}) screen time",
                color = KidTextMuted,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Convert & Request merged section
            Text(
                text = "Quick Convert to Screen Time:",
                color = KidTextDark,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(10 to 5, 20 to 10, 40 to 20).forEach { (pts, mins) ->
                    val isAffordable = wallet.totalCredits >= pts
                    OutlinedButton(
                        onClick = { onSpendCredits(pts) },
                        enabled = isAffordable,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isAffordable) Color(0xFFEFF6FF) else Color(0xFFF1F5F9),
                            contentColor = if (isAffordable) KidBlue else KidTextMuted
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isAffordable) Color(0xFFBFDBFE) else Color(0xFFE2E8F0)),
                        contentPadding = PaddingValues(vertical = 6.dp, horizontal = 4.dp)
                    ) {
                        Text(
                            text = "+${mins}m ($pts pts)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Complete parent quests to earn credits, or request rewards for custom tasks.",
                    color = KidTextMuted,
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Button(
                    onClick = onRequestCreditsClick,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = KidBlue,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text("+ Request", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun KidSosContent(
    syncManager: RealtimeFamilySyncManager,
    syncState: com.nestcheck.app.data.sync.FamilySyncState
) {
    val context = LocalContext.current
    var sosSent by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .background(Color(0xFFFEE2E2))
                .clickable {
                    syncManager.triggerKidSos(true, syncState.locationAddress, syncState.kidLatitude, syncState.kidLongitude)
                    sosSent = true
                    // Real Emergency Dial 112
                    val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:112"))
                    context.startActivity(dialIntent)
                },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(KidRed),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Outlined.Warning, contentDescription = "SOS", tint = Color.White, modifier = Modifier.size(40.dp))
                    Text("SOS", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            if (sosSent) "🚨 SOS ALERT BROADCASTED!" else "Emergency SOS Button",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = if (sosSent) KidRed else KidTextDark
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            if (sosSent) "Parent phone has received your live GPS coordinates. Dialing emergency 112..."
            else "Press and hold to alert parents and fast-dial 112 with your live location.",
            fontSize = 14.sp,
            color = KidTextMuted,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        if (sosSent) {
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedButton(
                onClick = {
                    syncManager.triggerKidSos(false, syncState.locationAddress, syncState.kidLatitude, syncState.kidLongitude)
                    sosSent = false
                },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = KidTextMuted),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("Cancel SOS Alert")
            }
        }
    }
}

@Composable
fun DeviceLockedOverlay(onUnlockAttempt: (String) -> Boolean) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var pin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFEE2E2)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Lock, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(48.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Device Paused", color = Color(0xFF0F172A), fontSize = 26.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Your parent paused this device or your daily screen time is up.",
                color = Color(0xFF64748B),
                fontSize = 15.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:112"))
                    context.startActivity(dialIntent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
            ) {
                Icon(Icons.Outlined.Phone, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Emergency Call (112)", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedButton(
                onClick = { showDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF0B57D0)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF0B57D0))
            ) {
                Text("Parent Unlock", fontWeight = FontWeight.SemiBold)
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = {
                    showDialog = false
                    pin = ""
                    error = false
                },
                title = { Text("Parent PIN", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        OutlinedTextField(
                            value = pin,
                            onValueChange = { if (it.length <= 4) pin = it },
                            label = { Text("Enter 4-digit PIN") },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            isError = error
                        )
                        if (error) {
                            Text("Wrong PIN", color = Color(0xFFDC2626), fontSize = 12.sp)
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (onUnlockAttempt(pin)) {
                                showDialog = false
                            } else {
                                error = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0B57D0))
                    ) {
                        Text("Unlock")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancel", color = Color(0xFF64748B))
                    }
                }
            )
        }
    }
}

@Composable
fun WaitingForParentApprovalScreen(
    deviceName: String,
    onBackToLogin: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFD3E3FD))
                    .border(2.dp, Color(0xFF0B57D0), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.HourglassTop,
                    contentDescription = null,
                    tint = Color(0xFF0B57D0),
                    modifier = Modifier.size(60.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Waiting for Parent Approval",
                color = Color(0xFF0F172A),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Device '$deviceName' has requested to pair with your household family space.",
                color = Color(0xFF64748B),
                fontSize = 14.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFF8FAFC),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color(0xFF0B57D0),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Listening for parent approval signal...",
                        color = Color(0xFF0F172A),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        OutlinedButton(
            onClick = onBackToLogin,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF0B57D0)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF0B57D0))
        ) {
            Text("Cancel & Return to Login", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun DeviceRejectedScreen(
    onBackToLogin: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color(0xFFEF4444).copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Outlined.Cancel, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(54.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Device Request Declined", color = Color(0xFF0F172A), fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Your parent declined access for this device.",
            color = Color(0xFF64748B),
            fontSize = 14.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onBackToLogin,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0B57D0)),
            shape = RoundedCornerShape(25.dp),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Back to Login", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun KidWellnessTab(navController: androidx.navigation.NavController) {
    androidx.compose.foundation.lazy.LazyColumn(
        modifier = androidx.compose.ui.Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(modifier = androidx.compose.ui.Modifier.height(12.dp)) }
        item {
            Text(
                "Wellness & Focus",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = KidTextDark,
                modifier = androidx.compose.ui.Modifier.padding(bottom = 4.dp)
            )
        }
        item {
            KidFeatureCard("Focus Mode & Study Timer", "Distraction-free study sessions", Icons.Outlined.SelfImprovement, KidBlue) {
                navController.navigate(com.nestcheck.app.navigation.Screen.FocusMode.route)
            }
        }
        item {
            KidFeatureCard("Hydration & Health Reminders", "Water intake goals & health alerts", Icons.Outlined.WaterDrop, Color(0xFF0284C7)) {
                navController.navigate(com.nestcheck.app.navigation.Screen.WaterReminder.route)
            }
        }
        item {
            KidFeatureCard("Safe Check-In", "Ping your arrival at school or home", Icons.Outlined.WhereToVote, KidGreen) {
                navController.navigate(com.nestcheck.app.navigation.Screen.ChildCheckIn.route)
            }
        }
        item { Spacer(modifier = androidx.compose.ui.Modifier.height(24.dp)) }
    }
}

@Composable
private fun KidFeatureCard(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector, tint: Color, onClick: () -> Unit) {
    Card(
        modifier = androidx.compose.ui.Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = KidCardWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, KidBorder)
    ) {
        Row(
            modifier = androidx.compose.ui.Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = androidx.compose.ui.Modifier.size(44.dp).clip(CircleShape).background(tint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) { Icon(icon, contentDescription = null, tint = tint, modifier = androidx.compose.ui.Modifier.size(22.dp)) }
            Spacer(modifier = androidx.compose.ui.Modifier.width(14.dp))
            Column(modifier = androidx.compose.ui.Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = KidTextDark)
                Text(subtitle, fontSize = 12.sp, color = KidTextMuted)
            }
            Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = KidTextMuted)
        }
    }
}
