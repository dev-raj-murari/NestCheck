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
    SCREEN_TIME("Screen time"),
    CONTROLS("Controls"),
    LOCATION("Location")
}

@Composable
fun ParentShellScreen(
    navController: NavController,
    syncManager: RealtimeFamilySyncManager = hiltViewModel<ParentShellViewModel>().syncManager
) {
    val syncState by syncManager.syncState.collectAsState()
    var selectedTab by remember { mutableStateOf(FamilyLinkTab.SCREEN_TIME) }

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
                    selected = selectedTab == FamilyLinkTab.SCREEN_TIME,
                    onClick = { selectedTab = FamilyLinkTab.SCREEN_TIME },
                    icon = { Icon(Icons.Outlined.BarChart, contentDescription = "Screen time") },
                    label = { Text("Screen time", fontSize = 12.sp, fontWeight = FontWeight.Medium) },
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
                    label = { Text("Controls", fontSize = 12.sp, fontWeight = FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = GflBlue,
                        indicatorColor = GflBlueContainer,
                        selectedTextColor = GflBlue,
                        unselectedIconColor = GflTextMuted,
                        unselectedTextColor = GflTextMuted
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == FamilyLinkTab.LOCATION,
                    onClick = { selectedTab = FamilyLinkTab.LOCATION },
                    icon = { Icon(Icons.Outlined.LocationOn, contentDescription = "Location") },
                    label = { Text("Location", fontSize = 12.sp, fontWeight = FontWeight.Medium) },
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
                FamilyLinkTab.SCREEN_TIME -> ScreenTimeTab(navController, syncState, syncManager)
                FamilyLinkTab.CONTROLS -> ControlsTab(navController, syncState, syncManager)
                FamilyLinkTab.LOCATION -> LocationMapTab(navController, syncState)
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
                        title = "Bedtime",
                        subtitle = syncState.bedtime,
                        icon = Icons.Outlined.Bedtime,
                        color = Color(0xFF2563EB),
                        onClick = { navController.navigate(Screen.BedtimeSchedule.route) }
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

                    // 3 Authentic Vertical App Capsule Badges
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        VerticalAppPill(name = "YouTube", color = Color(0xFFFF0000), icon = Icons.Outlined.PlayCircle)
                        VerticalAppPill(name = "Play Store", color = Color(0xFF01875F), icon = Icons.Outlined.Shop, isTall = true)
                        VerticalAppPill(name = "Chrome", color = Color(0xFF4285F4), icon = Icons.Outlined.Public)
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
                        ProfileMetric(title = "Bedtime", value = syncState.bedtime)
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
                                    .background(Color(0xFF202124)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Outlined.Smartphone, contentDescription = null, tint = Color.White)
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
                        Text("Daily limit: ${syncState.dailyLimitMinutes / 60} hrs • Bedtime: ${syncState.bedtime}", fontSize = 13.sp, color = GflTextMuted)
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
fun VerticalAppPill(name: String, color: Color, icon: androidx.compose.ui.graphics.vector.ImageVector, isTall: Boolean = false) {
    Box(
        modifier = Modifier
            .width(36.dp)
            .height(if (isTall) 74.dp else 58.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(GflBgLight)
            .border(1.dp, GflBorder, RoundedCornerShape(18.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = name,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
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
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Child Profile Flower Card
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(GflBlueContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = syncState.activeChildName.take(1),
                        fontWeight = FontWeight.Bold,
                        color = GflBlue,
                        fontSize = 32.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${syncState.activeChildName.lowercase()}@nestcheck.family",
                    fontSize = 14.sp,
                    color = GflTextMuted
                )
            }
        }

        // Dedicated Live Map & Location Tracking Card
        item {
            ControlCardItem(
                title = "Live Location & Safe Geofences",
                subtitle = "Google Maps full view • ${syncState.locationAddress}",
                icon = Icons.Outlined.LocationOn,
                iconTint = GflBlue,
                onClick = { navController.navigate(Screen.LiveMap.route) }
            )
        }

        // Dedicated Notification History Tracker Card
        item {
            ControlCardItem(
                title = "App Notification History",
                subtitle = "Track messages & alerts received by child's apps",
                icon = Icons.Outlined.NotificationsActive,
                iconTint = Color(0xFF7C3AED),
                onClick = { navController.navigate(Screen.NotificationHistory.route) }
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

        // YouTube Content Controls
        item {
            ControlCardItem(
                title = "YouTube",
                subtitle = "Restricted Mode ON • Age appropriate content",
                icon = Icons.Outlined.PlayCircle,
                iconTint = Color(0xFFFF0000),
                onClick = { navController.navigate(Screen.ContentFilter.route) }
            )
        }

        // Google Chrome & Web
        item {
            ControlCardItem(
                title = "Google Chrome and Web",
                subtitle = "Explicit websites blocked • Safe browsing active",
                icon = Icons.Outlined.Public,
                iconTint = Color(0xFF4285F4),
                onClick = { navController.navigate(Screen.BlockedUrl.route) }
            )
        }

        // App Quota & Restrictions
        item {
            ControlCardItem(
                title = "App Management & Limits",
                subtitle = "Configure daily quotas for individual apps",
                icon = Icons.Outlined.Apps,
                iconTint = GflBlue,
                onClick = { navController.navigate(Screen.AppControl.route) }
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
