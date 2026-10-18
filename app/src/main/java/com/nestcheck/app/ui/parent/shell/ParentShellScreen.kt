package com.nestcheck.app.ui.parent.shell

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.nestcheck.app.data.sync.DeviceApprovalStatus
import com.nestcheck.app.data.sync.RealtimeFamilySyncManager
import com.nestcheck.app.navigation.Screen

// Authentic Google Family Link Color Palette
val GflBgLight = Color(0xFFF8F9FA)
val GflCardWhite = Color(0xFFFFFFFF)
val GflTextDark = Color(0xFF1F1F1F)
val GflTextMuted = Color(0xFF5E5E5E)
val GflBorder = Color(0xFFE0E2EC)
val GflBlue = Color(0xFF0B57D0)
val GflBlueContainer = Color(0xFFD3E3FD)
val GflGreen = Color(0xFF1E8E3E)
val GflRed = Color(0xFFD93025)
val GflAmber = Color(0xFFF9AB00)

enum class FamilyLinkTab(val label: String) {
    TODAY("Today"),
    CONTROLS("Controls"),
    SAFETY("Safety"),
    REWARDS("Rewards"),
    MORE("More")
}

@Composable
fun ParentShellScreen(
    navController: NavController,
    syncManager: RealtimeFamilySyncManager = hiltViewModel<ParentShellViewModel>().syncManager
) {
    val syncState by syncManager.syncState.collectAsState()
    var selectedTab by remember { mutableStateOf(FamilyLinkTab.TODAY) }

    Scaffold(
        containerColor = GflBgLight,
        topBar = {
            FamilyLinkTopBar(
                childName = syncState.activeChildName,
                onAlertsClick = { navController.navigate(Screen.NotificationHistory.route) },
                onProfileClick = { navController.navigate(Screen.ChildProfile.route) }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = GflCardWhite,
                tonalElevation = 3.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == FamilyLinkTab.TODAY,
                    onClick = { selectedTab = FamilyLinkTab.TODAY },
                    icon = { Icon(Icons.Outlined.BarChart, contentDescription = "Today") },
                    label = { Text("Today", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = GflBlue,
                        indicatorColor = GflBlueContainer,
                        selectedTextColor = GflBlue,
                        unselectedIconColor = GflTextMuted,
                        unselectedTextColor = GflTextMuted
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == FamilyLinkTab.CONTROLS,
                    onClick = { selectedTab = FamilyLinkTab.CONTROLS },
                    icon = { Icon(Icons.Outlined.Tune, contentDescription = "Controls") },
                    label = { Text("Controls", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = GflBlue,
                        indicatorColor = GflBlueContainer,
                        selectedTextColor = GflBlue,
                        unselectedIconColor = GflTextMuted,
                        unselectedTextColor = GflTextMuted
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == FamilyLinkTab.SAFETY,
                    onClick = { selectedTab = FamilyLinkTab.SAFETY },
                    icon = { Icon(Icons.Outlined.Shield, contentDescription = "Safety") },
                    label = { Text("Safety", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = GflBlue,
                        indicatorColor = GflBlueContainer,
                        selectedTextColor = GflBlue,
                        unselectedIconColor = GflTextMuted,
                        unselectedTextColor = GflTextMuted
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == FamilyLinkTab.REWARDS,
                    onClick = { selectedTab = FamilyLinkTab.REWARDS },
                    icon = { Icon(Icons.Outlined.Stars, contentDescription = "Rewards") },
                    label = { Text("Rewards", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = GflBlue,
                        indicatorColor = GflBlueContainer,
                        selectedTextColor = GflBlue,
                        unselectedIconColor = GflTextMuted,
                        unselectedTextColor = GflTextMuted
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == FamilyLinkTab.MORE,
                    onClick = { selectedTab = FamilyLinkTab.MORE },
                    icon = { Icon(Icons.Outlined.MoreHoriz, contentDescription = "More") },
                    label = { Text("More", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = GflBlue,
                        indicatorColor = GflBlueContainer,
                        selectedTextColor = GflBlue,
                        unselectedIconColor = GflTextMuted,
                        unselectedTextColor = GflTextMuted
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                FamilyLinkTab.TODAY -> ScreenTimeTab(navController, syncState, syncManager)
                FamilyLinkTab.CONTROLS -> ControlsTab(navController, syncState, syncManager)
                FamilyLinkTab.SAFETY -> SafetyTab(navController, syncState)
                FamilyLinkTab.REWARDS -> RewardsTab(navController, syncState)
                FamilyLinkTab.MORE -> MoreTab(navController, syncState)
            }
        }
    }
}

@Composable
fun FamilyLinkTopBar(
    childName: String,
    onAlertsClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = GflCardWhite,
            border = androidx.compose.foundation.BorderStroke(1.dp, GflBorder),
            modifier = Modifier.clickable(onClick = onProfileClick)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(GflBlueContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = childName.take(1),
                        fontWeight = FontWeight.Bold,
                        color = GflBlue,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = childName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = GflTextDark
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Outlined.KeyboardArrowDown,
                    contentDescription = null,
                    tint = GflTextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = onAlertsClick,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(GflCardWhite)
            ) {
                Icon(
                    imageVector = Icons.Outlined.NotificationsActive,
                    contentDescription = "Notification History",
                    tint = GflBlue,
                    modifier = Modifier.size(20.dp)
                )
            }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(GflBlue)
                    .clickable(onClick = onProfileClick),
                contentAlignment = Alignment.Center
            ) {
                Text("P", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun ScreenTimeTab(
    navController: NavController,
    syncState: com.nestcheck.app.data.sync.FamilySyncState,
    syncManager: RealtimeFamilySyncManager
) {
    var showEditChildDialog by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf(syncState.activeChildName) }
    var editAge by remember { mutableStateOf(syncState.childAge.toString()) }
    var editLimit by remember { mutableStateOf((syncState.dailyLimitMinutes / 60f).toString()) }
    var editBedtime by remember { mutableStateOf(syncState.bedtime) }
    var editSchoolHours by remember { mutableStateOf(syncState.schoolHours) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Critical SOS Banner if Active
        if (syncState.sosActive) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = GflRed)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Warning, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("EMERGENCY SOS ACTIVE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "${syncState.activeChildName} pressed emergency button at ${syncState.locationAddress}",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        val context = LocalContext.current
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:112"))
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = GflRed),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text("Call 112 Emergency", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Child Device Approval Gate Request Card (Parent Approval Gate)
        if (syncState.deviceApprovalStatus == DeviceApprovalStatus.PENDING) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFF59E0B))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.DevicesOther, contentDescription = null, tint = Color(0xFFD97706))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("NEW DEVICE PAIRING REQUEST", color = Color(0xFFB45309), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "${syncState.pendingDeviceName} is requesting approval to join ${syncState.activeChildName}'s space.",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF78350F)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = { syncManager.approveChildDevice() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Icon(Icons.Outlined.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Approve Device", fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(
                                onClick = { syncManager.rejectChildDevice() },
                                shape = RoundedCornerShape(20.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDC2626)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626))
                            ) {
                                Text("Decline")
                            }
                        }
                    }
                }
            }
        }

        // Fancy Horizontal Quick-Actions Carousel
        item {
            Text("Quick Family Actions", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = GflTextDark)
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    QuickActionPillCard(
                        title = "Live Map",
                        subtitle = "Track GPS & Geofence",
                        icon = Icons.Outlined.LocationOn,
                        color = Color(0xFF0B57D0),
                        onClick = { navController.navigate(Screen.LiveMap.route) }
                    )
                }
                item {
                    QuickActionPillCard(
                        title = "App Alerts",
                        subtitle = "Notification History",
                        icon = Icons.Outlined.NotificationsActive,
                        color = Color(0xFF7C3AED),
                        onClick = { navController.navigate(Screen.NotificationHistory.route) }
                    )
                }
                item {
                    QuickActionPillCard(
                        title = if (syncState.isDeviceLocked) "Device Locked" else "Unlocked",
                        subtitle = "Tap to toggle",
                        icon = if (syncState.isDeviceLocked) Icons.Outlined.Lock else Icons.Outlined.LockOpen,
                        color = if (syncState.isDeviceLocked) Color(0xFFDC2626) else Color(0xFF16A34A),
                        onClick = { syncManager.setDeviceLocked(!syncState.isDeviceLocked) }
                    )
                }
                item {
                    QuickActionPillCard(
                        title = "App Limits",
                        subtitle = "Quotas & Locks",
                        icon = Icons.Outlined.Apps,
                        color = Color(0xFFEA580C),
                        onClick = { navController.navigate(Screen.AppControl.route) }
                    )
                }
                item {
                    QuickActionPillCard(
                        title = "Timetable",
                        subtitle = "PDF & Calendar",
                        icon = Icons.Outlined.CalendarMonth,
                        color = Color(0xFF0B57D0),
                        onClick = { navController.navigate(Screen.Timetable.route) }
                    )
                }
            }
        }

        // 1. Google Family Link Screen Time Card with Vertical App Capsules
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = GflCardWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, GflBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        val hours = (syncState.dailyLimitMinutes - syncState.remainingMinutes).coerceAtLeast(0) / 60
                        val mins = (syncState.dailyLimitMinutes - syncState.remainingMinutes).coerceAtLeast(0) % 60
                        Text(
                            text = "${hours}h ${mins}m",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = GflTextDark
                        )
                        Text(
                            text = "Time spent today (Limit: ${syncState.dailyLimitMinutes / 60}h)",
                            fontSize = 13.sp,
                            color = GflTextMuted
                        )
                    }
                }
            }
        }

        // 2. Child Profile Card with Setup / Edit Section (Requested Feature)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = GflCardWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, GflBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(GflBlueContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(syncState.activeChildName.take(1), fontWeight = FontWeight.Bold, color = GflBlue, fontSize = 20.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(syncState.activeChildName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = GflTextDark)
                                Text("${syncState.childAge} years old • ${syncState.schoolHours}", fontSize = 12.sp, color = GflTextMuted)
                            }
                        }

                        IconButton(onClick = {
                            editName = syncState.activeChildName
                            editAge = syncState.childAge.toString()
                            editLimit = (syncState.dailyLimitMinutes / 60f).toString()
                            editBedtime = syncState.bedtime
                            editSchoolHours = syncState.schoolHours
                            showEditChildDialog = true
                        }) {
                            Icon(Icons.Outlined.Edit, contentDescription = "Edit Child Profile", tint = GflBlue)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        ProfileMetric(title = "Daily Limit", value = "${syncState.dailyLimitMinutes / 60} hrs")
                        ProfileMetric(title = "School Hours", value = syncState.schoolHours)
                        ProfileMetric(title = "Pairing Code", value = syncState.familyCode)
                    }
                }
            }
        }

        // 3. Device Card with Instant Remote Lock Button
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = GflCardWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, GflBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFD3E3FD)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Outlined.Smartphone, contentDescription = null, tint = Color(0xFF0B57D0))
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(syncState.pendingDeviceName, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = GflTextDark)
                                Text(
                                    text = if (syncState.isDeviceLocked) "Locked by parent" else "Unlocked",
                                    color = if (syncState.isDeviceLocked) GflRed else GflBlue,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = GflTextMuted)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            syncManager.setDeviceLocked(!syncState.isDeviceLocked)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (syncState.isDeviceLocked) Color(0xFFFEE7E6) else GflBlueContainer,
                            contentColor = if (syncState.isDeviceLocked) GflRed else GflBlue
                        )
                    ) {
                        Icon(
                            imageVector = if (syncState.isDeviceLocked) Icons.Outlined.LockOpen else Icons.Outlined.Lock,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (syncState.isDeviceLocked) "Unlock device" else "Lock device",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }

        // 4. Time Limits Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate(Screen.TimeLimits.route) },
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = GflCardWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, GflBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(GflBgLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.HourglassEmpty, contentDescription = null, tint = GflTextDark)
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Time limits", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = GflTextDark)
                        Text("Daily limit: ${syncState.dailyLimitMinutes / 60} hrs • School: ${syncState.schoolHours}", fontSize = 13.sp, color = GflTextMuted)
                    }
                    Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = GflTextMuted)
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }

    if (showEditChildDialog) {
        AlertDialog(
            onDismissRequest = { showEditChildDialog = false },
            title = { Text("Edit Child Profile", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Child Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editAge,
                        onValueChange = { editAge = it },
                        label = { Text("Age (Years)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editLimit,
                        onValueChange = { editLimit = it },
                        label = { Text("Daily Limit (Hours)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editBedtime,
                        onValueChange = { editBedtime = it },
                        label = { Text("Bedtime") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val ageNum = editAge.toIntOrNull() ?: 11
                        val limitMins = ((editLimit.toFloatOrNull() ?: 3f) * 60).toInt()
                        syncManager.updateChildProfile(
                            name = editName.trim().ifBlank { "Olivia" },
                            age = ageNum,
                            dailyLimitMinutes = limitMins,
                            bedtime = editBedtime,
                            schoolHours = editSchoolHours
                        )
                        showEditChildDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GflBlue)
                ) {
                    Text("Save Changes", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditChildDialog = false }) {
                    Text("Cancel", color = GflTextMuted)
                }
            }
        )
    }
}

@Composable
fun ProfileMetric(title: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = GflTextDark)
        Text(title, fontSize = 11.sp, color = GflTextMuted)
    }
}

@Composable
fun QuickActionPillCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GflCardWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, GflBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GflTextDark, maxLines = 1)
            Text(subtitle, fontSize = 11.sp, color = GflTextMuted, maxLines = 1)
        }
    }
}


@Composable
fun ControlsTab(
    navController: NavController,
    syncState: com.nestcheck.app.data.sync.FamilySyncState,
    syncManager: RealtimeFamilySyncManager
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(GflBlueContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = syncState.activeChildName.take(1),
                        fontWeight = FontWeight.Bold,
                        color = GflBlue,
                        fontSize = 30.sp
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "${syncState.activeChildName} • Controls & Limits",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = GflTextDark
                )
                Text(
                    text = "Configure device lockouts, app quotas & schedules",
                    fontSize = 12.sp,
                    color = GflTextMuted
                )
            }
        }

        // Rohit's F1 & F2: Screen Time & Limits
        item {
            ControlCardItem(
                title = "Screen Time & Limits",
                subtitle = "Daily caps, countdown & bedtimes • ${syncState.dailyLimitMinutes / 60}h limit",
                icon = Icons.Outlined.HourglassEmpty,
                iconTint = GflBlue,
                onClick = { navController.navigate(Screen.TimeLimits.route) }
            )
        }

        // Rohit's F3: App Usage & Blocking
        item {
            ControlCardItem(
                title = "App Usage & Blocking",
                subtitle = "Approve, allow, or block installed apps",
                icon = Icons.Outlined.Apps,
                iconTint = Color(0xFF0284C7),
                onClick = { navController.navigate(Screen.AppControl.route) }
            )
        }

        // Child School Timetable & Google Calendar Integration
        item {
            ControlCardItem(
                title = "School Timetable & Google Calendar",
                subtitle = if (syncState.timetableSyncedToCalendar) "Synced to Google Calendar 📅 • PDF Active" else "Upload PDF timetable & sync events to Calendar",
                icon = Icons.Outlined.CalendarMonth,
                iconTint = GflBlue,
                onClick = { navController.navigate(Screen.Timetable.route) }
            )
        }

        // Rohit's F24: Remote Device Lockout
        item {
            ControlCardItem(
                title = "Remote Device Lockout",
                subtitle = if (syncState.isDeviceLocked) "Device is currently LOCKED 🔒" else "Device unlocked • Tap to freeze screen",
                icon = if (syncState.isDeviceLocked) Icons.Outlined.Lock else Icons.Outlined.LockOpen,
                iconTint = if (syncState.isDeviceLocked) GflRed else GflGreen,
                onClick = { navController.navigate(Screen.RemoteLock.route) }
            )
        }

        // Rohit's F27: Category Quotas & App Caps
        item {
            ControlCardItem(
                title = "Category Quotas & App Caps",
                subtitle = "Separate daily limits for Games, Social Media & Study",
                icon = Icons.Outlined.Category,
                iconTint = Color(0xFFD97706),
                onClick = { navController.navigate(Screen.CategoryQuotas.route) }
            )
        }

        // Rohit's F25: Age-Based Safety Presets
        item {
            ControlCardItem(
                title = "Age-Based Safety Presets",
                subtitle = "1-tap rules tuned for Ages 4-7, 8-12, or 13-17",
                icon = Icons.Outlined.ChildCare,
                iconTint = Color(0xFF059669),
                onClick = { navController.navigate(Screen.AgePresets.route) }
            )
        }

        // Rohit's F26: Time Extension Requests
        item {
            ControlCardItem(
                title = "Time Extension Requests",
                subtitle = "Review and approve extra screen time asked by child",
                icon = Icons.Outlined.AccessTime,
                iconTint = Color(0xFF7C3AED),
                onClick = { navController.navigate(Screen.TimeRequests.route) }
            )
        }

        // Google Play & In-App Purchases Lock
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = GflCardWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, GflBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Shop, contentDescription = null, tint = Color(0xFF01875F), modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text("Google Play Purchases", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = GflTextDark)
                            Text(
                                if (syncState.inAppPurchasesBlocked) "In-app purchases BLOCKED 🔒" else "Purchases allowed",
                                fontSize = 13.sp,
                                color = if (syncState.inAppPurchasesBlocked) GflRed else GflTextMuted,
                                fontWeight = if (syncState.inAppPurchasesBlocked) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                    Switch(
                        checked = syncState.inAppPurchasesBlocked,
                        onCheckedChange = { syncManager.setInAppPurchasesBlocked(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = GflRed, checkedTrackColor = Color(0xFFFEE7E6))
                    )
                }
            }
        }

        // Google Chrome & Web
        item {
            ControlCardItem(
                title = "Google Chrome & Web Filtering",
                subtitle = "Explicit website blocker • Safe browsing active",
                icon = Icons.Outlined.Public,
                iconTint = Color(0xFF4285F4),
                onClick = { navController.navigate(Screen.BlockedUrl.route) }
            )
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
fun SafetyTab(
    navController: NavController,
    syncState: com.nestcheck.app.data.sync.FamilySyncState
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFEF2F2)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Shield, contentDescription = null, tint = GflRed, modifier = Modifier.size(36.dp))
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Child Safety & Security Hub",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = GflTextDark
                )
                Text(
                    text = "GPS telemetry, emergency dispatchers & permissions",
                    fontSize = 12.sp,
                    color = GflTextMuted
                )
            }
        }

        // Dedicated Live Location Map
        item {
            ControlCardItem(
                title = "Live Location & Safe Geofences",
                subtitle = "Google Maps full view • ${syncState.locationAddress}",
                icon = Icons.Outlined.LocationOn,
                iconTint = GflBlue,
                onClick = { navController.navigate(Screen.LiveMap.route) }
            )
        }

        // Dedicated App Notification History
        item {
            ControlCardItem(
                title = "App Notification History",
                subtitle = "Track messages & alerts received by child's apps",
                icon = Icons.Outlined.NotificationsActive,
                iconTint = Color(0xFF7C3AED),
                onClick = { navController.navigate(Screen.NotificationHistory.route) }
            )
        }

        // Sinan's F41: Emergency Contacts Hub & Fast-Dial Dispatcher
        item {
            ControlCardItem(
                title = "Emergency Contacts Hub & Fast-Dial",
                subtitle = "One-tap emergency dispatcher & 112 police hotline",
                icon = Icons.Outlined.WarningAmber,
                iconTint = GflRed,
                onClick = { navController.navigate(Screen.EmergencyHub.route) }
            )
        }

        // Sinan's F35: App Permission Auditor & Sensitive Scanner
        item {
            ControlCardItem(
                title = "App Permission Auditor",
                subtitle = "Audit Camera, Microphone & Location access in child apps",
                icon = Icons.Outlined.Security,
                iconTint = Color(0xFFD97706),
                onClick = { navController.navigate(Screen.PermissionAuditor.route) }
            )
        }

        // Sinan's F34: Low-Battery Last Ping Telemetry
        item {
            ControlCardItem(
                title = "Low-Battery Last Ping Telemetry",
                subtitle = "Emergency GPS beacon transmitted when battery < 15%",
                icon = Icons.Outlined.BatteryAlert,
                iconTint = Color(0xFFEA580C),
                onClick = { navController.navigate(Screen.LowBatteryPing.route) }
            )
        }

        // Sinan's F33: System-Wide SafeSearch Lock
        item {
            ControlCardItem(
                title = "System-Wide SafeSearch Lock",
                subtitle = "Enforce SafeSearch filtering across Google, Bing & YouTube",
                icon = Icons.Outlined.Shield,
                iconTint = Color(0xFF2563EB),
                onClick = { navController.navigate(Screen.SafeSearch.route) }
            )
        }

        // Sinan's F31: Trusted Contacts Whitelist Directory
        item {
            ControlCardItem(
                title = "Trusted Contacts Whitelist Directory",
                subtitle = "Whitelist phone numbers & automatically block unknown callers",
                icon = Icons.Outlined.ContactPhone,
                iconTint = Color(0xFF059669),
                onClick = { navController.navigate(Screen.TrustedContacts.route) }
            )
        }

        // Sinan's F9: App Quarantine Guard
        item {
            ControlCardItem(
                title = "App Quarantine Guard",
                subtitle = "Sandbox and isolate unverified newly installed applications",
                icon = Icons.Outlined.FolderDelete,
                iconTint = Color(0xFF475569),
                onClick = { navController.navigate(Screen.AppQuarantine.route) }
            )
        }

        // Sinan's F10: Child Safe Check-In
        item {
            ControlCardItem(
                title = "Child Safe Check-In",
                subtitle = "Instant arrival pings for school, home & sports activities",
                icon = Icons.Outlined.WhereToVote,
                iconTint = Color(0xFF0284C7),
                onClick = { navController.navigate(Screen.ChildCheckIn.route) }
            )
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
fun RewardsTab(
    navController: NavController,
    syncState: com.nestcheck.app.data.sync.FamilySyncState
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFEF3C7)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Stars, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(36.dp))
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Habits, Routines & Rewards",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = GflTextDark
                )
                Text(
                    text = "Chores, homework, stars ledger & focus challenges",
                    fontSize = 12.sp,
                    color = GflTextMuted
                )
            }
        }

        // Rohit's F22: Chore Chart & Daily Task Assignment
        item {
            ControlCardItem(
                title = "Chores & Daily Duty Chart",
                subtitle = "Assign tasks, approve completed chores & award points",
                icon = Icons.Outlined.CheckCircle,
                iconTint = Color(0xFF16A34A),
                onClick = { navController.navigate(Screen.Chores.route) }
            )
        }

        // Rohit's F5: Homework Check-Off & Verification
        item {
            ControlCardItem(
                title = "Homework Check-Off Tracker",
                subtitle = "Track assignments, verify completion & unlock play time",
                icon = Icons.Outlined.MenuBook,
                iconTint = Color(0xFF2563EB),
                onClick = { navController.navigate(Screen.Homework.route) }
            )
        }

        // Rohit's F4: Credit System & Reward Ledger
        item {
            ControlCardItem(
                title = "Credits Ledger & Reward System",
                subtitle = "Award stars for good behavior & view credit transactions",
                icon = Icons.Outlined.Stars,
                iconTint = Color(0xFFD97706),
                onClick = { navController.navigate(Screen.Credits.route) }
            )
        }

        // Rohit's F23: Reward Shop
        item {
            ControlCardItem(
                title = "Reward Shop Catalog",
                subtitle = "Exchange stars for treats, playtime & exciting rewards",
                icon = Icons.Outlined.ShoppingBag,
                iconTint = Color(0xFFDB2777),
                onClick = { navController.navigate(Screen.RewardShop.route) }
            )
        }

        // Rohit's F21: Focus Mode Study Timer
        item {
            ControlCardItem(
                title = "Focus Mode Study Timer",
                subtitle = "Timed distraction-free focus sessions with app lockouts",
                icon = Icons.Outlined.SelfImprovement,
                iconTint = Color(0xFF7C3AED),
                onClick = { navController.navigate(Screen.FocusMode.route) }
            )
        }

        // Rohit's F28: Habit Challenges & Streaks
        item {
            ControlCardItem(
                title = "Habit Streaks & Challenges",
                subtitle = "Daily routine streaks, badges & consistency milestones",
                icon = Icons.Outlined.EmojiEvents,
                iconTint = Color(0xFFF59E0B),
                onClick = { navController.navigate(Screen.HabitChallenges.route) }
            )
        }

        // Rohit's F40: Hydration / Water Reminder
        item {
            ControlCardItem(
                title = "Hydration & Health Reminders",
                subtitle = "Daily water drinking alerts & health telemetry goals",
                icon = Icons.Outlined.WaterDrop,
                iconTint = Color(0xFF0284C7),
                onClick = { navController.navigate(Screen.WaterReminder.route) }
            )
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
fun MoreTab(
    navController: NavController,
    syncState: com.nestcheck.app.data.sync.FamilySyncState
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEFF6FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.MoreHoriz, contentDescription = null, tint = GflBlue, modifier = Modifier.size(36.dp))
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Family Management & Settings",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = GflTextDark
                )
                Text(
                    text = "AI assistant, multiple children & security PIN",
                    fontSize = 12.sp,
                    color = GflTextMuted
                )
            }
        }


        // Multi-Child Dashboard
        item {
            ControlCardItem(
                title = "Multi-Child Family Dashboard",
                subtitle = "Manage multiple children and switch active profiles",
                icon = Icons.Outlined.Groups,
                iconTint = GflBlue,
                onClick = { navController.navigate(Screen.MultiChild.route) }
            )
        }

        // Family Sharing & Co-Parents
        item {
            ControlCardItem(
                title = "Family Sharing & Co-Parents",
                subtitle = "Invite spouse, grandparents, or trusted guardians",
                icon = Icons.Outlined.Share,
                iconTint = Color(0xFF059669),
                onClick = { navController.navigate(Screen.FamilySharing.route) }
            )
        }

        // Parent Master PIN Security
        item {
            ControlCardItem(
                title = "Parent Master PIN Security",
                subtitle = "Change parental verification PIN & emergency passcodes",
                icon = Icons.Outlined.VpnKey,
                iconTint = Color(0xFFD97706),
                onClick = { navController.navigate(Screen.ParentSecurity.route) }
            )
        }

        // Battery Monitor Telemetry
        item {
            ControlCardItem(
                title = "Device Battery & Hardware Telemetry",
                subtitle = "Child battery level: ${syncState.kidBattery}% • Realtime status",
                icon = Icons.Outlined.BatteryStd,
                iconTint = Color(0xFF16A34A),
                onClick = { navController.navigate(Screen.BatteryMonitor.route) }
            )
        }

        // Weekly Screen Time Reports
        item {
            ControlCardItem(
                title = "Weekly Screen Time Reports",
                subtitle = "Weekly trends, category breakdowns & usage analytics",
                icon = Icons.Outlined.Assessment,
                iconTint = Color(0xFF4F46E5),
                onClick = { navController.navigate(Screen.Reports.route) }
            )
        }

        // Language & Localization Settings
        item {
            ControlCardItem(
                title = "Language & Regional Settings",
                subtitle = "Choose interface language & regional preferences",
                icon = Icons.Outlined.Language,
                iconTint = Color(0xFF0284C7),
                onClick = { navController.navigate(Screen.LanguageSettings.route) }
            )
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
fun ControlCardItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = GflCardWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, GflBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(26.dp))
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = GflTextDark)
                Text(subtitle, fontSize = 13.sp, color = GflTextMuted)
            }
            Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = GflTextMuted)
        }
    }
}

@Composable
fun LocationMapTab(
    navController: NavController,
    syncState: com.nestcheck.app.data.sync.FamilySyncState
) {
    val childLocation = LatLng(syncState.kidLatitude, syncState.kidLongitude)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(childLocation, 15f)
    }

    LaunchedEffect(syncState.kidLatitude, syncState.kidLongitude) {
        cameraPositionState.position = CameraPosition.fromLatLngZoom(childLocation, 15f)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Real Google Maps View
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(zoomControlsEnabled = false, myLocationButtonEnabled = true)
        ) {
            Marker(
                state = MarkerState(position = childLocation),
                title = syncState.activeChildName,
                snippet = "${syncState.kidBattery}% battery • ${syncState.locationAddress}"
            )
            Circle(
                center = childLocation,
                radius = 350.0,
                fillColor = Color(0x224285F4),
                strokeColor = Color(0xFF4285F4),
                strokeWidth = 3f
            )
        }

        // Top button to expand into Dedicated Live Map Screen
        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            shadowElevation = 6.dp
        ) {
            Row(
                modifier = Modifier
                    .clickable { navController.navigate(Screen.LiveMap.route) }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.Fullscreen, contentDescription = null, tint = GflBlue, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Full Map View", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GflBlue)
            }
        }

        // Bottom Child Location Sheet
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = GflCardWhite,
            shadowElevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(GflBlueContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(syncState.activeChildName.take(1), fontWeight = FontWeight.Bold, color = GflBlue)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(syncState.activeChildName, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = GflTextDark)
                            Text(syncState.locationAddress, fontSize = 13.sp, color = GflTextMuted)
                            Text("${syncState.kidBattery}% ⚡ • Live geofence safe", fontSize = 12.sp, color = GflGreen, fontWeight = FontWeight.Medium)
                        }
                    }
                    IconButton(onClick = {
                        navController.navigate(Screen.LiveMap.route)
                    }) {
                        Icon(Icons.Outlined.Navigation, contentDescription = "Navigate", tint = GflBlue)
                    }
                }
            }
        }
    }
}
