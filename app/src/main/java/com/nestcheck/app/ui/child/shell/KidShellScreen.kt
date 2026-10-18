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
import com.nestcheck.app.data.sync.RealtimeFamilySyncManager
import com.nestcheck.app.navigation.Screen

enum class KidTab(val label: String, val icon: ImageVector) {
    HOME("Home", Icons.Outlined.Home),
    TASKS("Tasks & Rewards", Icons.Outlined.EmojiEvents),
    SOS("Emergency SOS", Icons.Outlined.WarningAmber)
}

val KidBgLight = Color(0xFFF0F4F9)
val KidCardWhite = Color(0xFFFFFFFF)
val KidTextDark = Color(0xFF1E293B)
val KidTextMuted = Color(0xFF64748B)
val KidBorder = Color(0xFFE2E8F0)
val KidBlue = Color(0xFF2563EB)
val KidGreen = Color(0xFF10B981)
val KidRed = Color(0xFFEF4444)

@Composable
fun KidShellScreen(
    navController: NavController,
    viewModel: KidShellViewModel = hiltViewModel()
) {
    val syncState by viewModel.syncManager.syncState.collectAsState()
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
                tonalElevation = 4.dp
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
                KidTab.HOME -> KidHomeContent(navController, syncState)
                KidTab.TASKS -> KidTasksContent(navController)
                KidTab.SOS -> KidSosContent(viewModel.syncManager, syncState)
            }
        }

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
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFDBEAFE)),
                contentAlignment = Alignment.Center
            ) {
                Text(childName.take(1), fontWeight = FontWeight.Bold, color = KidBlue)
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
            Icon(Icons.Outlined.Lock, contentDescription = "Exit to Parent", tint = KidTextMuted, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
fun KidHomeContent(
    navController: NavController,
    syncState: com.nestcheck.app.data.sync.FamilySyncState
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Payment Lock Pill Banner (User Request: In-app purchases blocked)
        if (syncState.inAppPurchasesBlocked) {
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFFEF2F2),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFECACA)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.Lock, contentDescription = null, tint = KidRed, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            "Google Play purchases and in-app payments are locked by parent.",
                            fontSize = 12.sp,
                            color = KidRed,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Screen Time Remaining Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = KidCardWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, KidBorder)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("REMAINING SCREEN TIME", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = KidTextMuted)
                    Spacer(modifier = Modifier.height(8.dp))
                    val hours = syncState.remainingMinutes / 60
                    val mins = syncState.remainingMinutes % 60
                    Text("${hours}h ${mins}m", fontSize = 34.sp, fontWeight = FontWeight.Bold, color = KidBlue)
                    Spacer(modifier = Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress = { syncState.remainingMinutes.toFloat() / syncState.dailyLimitMinutes.toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = KidBlue,
                        trackColor = Color(0xFFDBEAFE)
                    )
                }
            }
        }

        // Exam & School Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("UPCOMING EXAM 📐", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = KidGreen)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Math Finals in 4 days ⏳", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = KidTextDark)
                    Text("Complete daily homework before 7:00 PM for +50 Bonus Credits", fontSize = 13.sp, color = KidTextMuted)
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
fun KidTasksContent(navController: NavController) {
    val tasks = listOf(
        Pair("Finish Math Homework Exercises 1-10", "+50 pts"),
        Pair("Read 20 minutes before bedtime", "+30 pts"),
        Pair("Clean desk and organize school bag", "+20 pts")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Today's Tasks & Quests", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = KidTextDark)
            Text("Complete tasks to unlock extra weekend play time", fontSize = 13.sp, color = KidTextMuted)
            Spacer(modifier = Modifier.height(4.dp))
        }

        items(tasks.size) { i ->
            val (task, pts) = tasks[i]
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = KidCardWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, KidBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(task, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = KidTextDark, modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFECFDF5)
                    ) {
                        Text(pts, color = KidGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                    }
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
            else "Press and hold for 2 seconds to alert parents and fast-dial 112 with your live location.",
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

