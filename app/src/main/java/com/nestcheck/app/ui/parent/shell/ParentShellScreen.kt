package com.nestcheck.app.ui.parent.shell

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nestcheck.app.data.model.ChildProfile
import com.nestcheck.app.navigation.Screen
import com.nestcheck.app.ui.parent.dashboard.ParentDashboardViewModel
import com.nestcheck.app.ui.theme.*

enum class ParentTab(val label: String, val icon: ImageVector) {
    TODAY("Today", Icons.Outlined.Dashboard),
    CONTROLS("Controls", Icons.Outlined.Tune),
    SAFETY("Safety", Icons.Outlined.Shield),
    REWARDS("Rewards", Icons.Outlined.Stars),
    MORE("More", Icons.Outlined.MoreHoriz)
}

@Composable
fun ParentShellScreen(
    navController: NavController,
    viewModel: ParentDashboardViewModel = hiltViewModel()
) {
    val child by viewModel.childProfile.collectAsState()
    var selectedTab by remember { mutableStateOf(ParentTab.TODAY) }

    Scaffold(
        containerColor = CyberBlack,
        bottomBar = {
            NavigationBar(
                containerColor = CyberCard,
                contentColor = CyberTextBright,
                tonalElevation = 8.dp
            ) {
                ParentTab.values().forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.label,
                                tint = if (selectedTab == tab) NeonCyan else CyberTextDim
                            )
                        },
                        label = {
                            Text(
                                text = tab.label,
                                color = if (selectedTab == tab) NeonCyan else CyberTextDim,
                                fontSize = 11.sp,
                                fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = CyberBorder
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
                ParentTab.TODAY -> TodayOverviewTab(navController, child)
                ParentTab.CONTROLS -> ControlsTab(navController)
                ParentTab.SAFETY -> SafetyTab(navController)
                ParentTab.REWARDS -> RewardsTab(navController)
                ParentTab.MORE -> MoreSettingsTab(navController)
            }
        }
    }
}

@Composable
fun TodayOverviewTab(navController: NavController, child: ChildProfile) {
    var selectedChild by remember { mutableStateOf("Leo") }
    val children = listOf("Leo (Age 10)", "Maya (Age 14)")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("NestCheck Parent", color = CyberTextBright, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Household Live Monitoring", color = CyberTextDim, fontSize = 12.sp)
                }
                IconButton(onClick = { navController.navigate(Screen.NotificationsHub.route) }) {
                    Icon(Icons.Outlined.Notifications, contentDescription = "Alerts", tint = NeonCyan)
                }
            }
        }

        // Child Switcher Chips
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                children.forEach { name ->
                    val isSelected = selectedChild == name.split(" ")[0]
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) NeonCyan.copy(alpha = 0.2f) else CyberCard)
                            .border(1.dp, if (isSelected) NeonCyan else CyberBorder, RoundedCornerShape(20.dp))
                            .clickable { selectedChild = name.split(" ")[0] }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = name,
                            color = if (isSelected) NeonCyan else CyberTextBright,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                IconButton(onClick = { navController.navigate(Screen.MultiChild.route) }) {
                    Icon(Icons.Outlined.Add, contentDescription = "Add Child", tint = CyberTextDim)
                }
            }
        }

        // Screen Time Dial / Ring Summary Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CyberCard),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("TODAY'S SCREEN TIME", color = CyberTextDim, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("Limit: 3h 00m", color = CyberTextDim, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text("1h 45m", color = CyberTextBright, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                            Text("Used today", color = CyberTextDim, fontSize = 12.sp)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(SuccessGreen.copy(alpha = 0.15f))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text("1h 15m remaining", color = SuccessGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = { 1.75f / 3.0f },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = NeonCyan,
                        trackColor = CyberBorder
                    )
                }
            }
        }

        // Live Device Status Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CyberCard),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("ACTIVE DEVICE TELEMETRY", color = CyberTextDim, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Smartphone, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Google Pixel 7a", color = CyberTextBright, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.BatteryChargingFull, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("82% ⚡", color = SuccessGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Oakridge School (Safe Zone)", color = CyberTextBright, fontSize = 12.sp)
                    }
                }
            }
        }

        // Quick Action Grid
        item {
            Text("Quick Actions", color = CyberTextBright, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { navController.navigate(Screen.RemoteLock.route) },
                    modifier = Modifier.weight(1f).height(46.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CyberBorder, contentColor = AlertRed),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Outlined.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Lock Device", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = { navController.navigate(Screen.Location.route) },
                    modifier = Modifier.weight(1f).height(46.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CyberBorder, contentColor = NeonCyan),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Outlined.NearMe, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Locate", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Pending Decisions & Approvals
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CyberCard),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("PENDING REQUESTS", color = CyberTextDim, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("1 New", color = WarningAmber, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Maya requested +30m screen time for Khan Academy homework.", color = CyberTextBright, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { navController.navigate(Screen.TimeRequests.route) },
                            modifier = Modifier.weight(1f).height(38.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen, contentColor = Black),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text("Review Request", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ControlsTab(navController: NavController) {
    val items = listOf(
        Pair("Screen Time & Limits", Screen.ScreenTime.route),
        Pair("App Usage & Blocking", Screen.AppControl.route),
        Pair("Bedtime Curfew & Schedules", Screen.BedtimeSchedule.route),
        Pair("App Quarantine Guard", Screen.AppQuarantine.route),
        Pair("Remote Device Lockout", Screen.RemoteLock.route),
        Pair("Category Quotas & App Caps", Screen.CategoryQuotas.route),
        Pair("Age-Based Safety Presets", Screen.AgePresets.route)
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("Device & Time Controls", color = CyberTextBright, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("Set daily schedules, app access, and instantaneous lockouts", color = CyberTextDim, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(10.dp))
        }

        items(items) { (title, route) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate(route) },
                colors = CardDefaults.cardColors(containerColor = CyberCard),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(title, color = CyberTextBright, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = CyberTextDim)
                }
            }
        }
    }
}

@Composable
fun SafetyTab(navController: NavController) {
    val items = listOf(
        Pair("Live Location & Geofencing", Screen.Location.route),
        Pair("Content & Web Filtering", Screen.ContentFilter.route),
        Pair("SafeSearch Lock Enforcement", Screen.SafeSearch.route),
        Pair("Toxic Text & Cyberbullying Monitor", Screen.ToxicTextMonitor.route),
        Pair("Sensitive Permissions Auditor", Screen.PermissionAuditor.route),
        Pair("Trusted Contacts Directory", Screen.TrustedContacts.route),
        Pair("Emergency Contacts & Fast Dial", Screen.EmergencyHub.route),
        Pair("Low Battery Emergency Ping", Screen.LowBatteryPing.route)
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("Child Safety & Protection", color = CyberTextBright, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("Geofence boundaries, content shields, and communication guards", color = CyberTextDim, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(10.dp))
        }

        items(items) { (title, route) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate(route) },
                colors = CardDefaults.cardColors(containerColor = CyberCard),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(title, color = CyberTextBright, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = CyberTextDim)
                }
            }
        }
    }
}

@Composable
fun RewardsTab(navController: NavController) {
    val items = listOf(
        Pair("Homework Review & Approvals", Screen.Homework.route),
        Pair("Chores & Task Management", Screen.Chores.route),
        Pair("Screen-Time Credit Ledger", Screen.Credits.route),
        Pair("Habit Streaks & Goals", Screen.HabitChallenges.route),
        Pair("Child Time Requests Queue", Screen.TimeRequests.route)
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("Tasks, Chores & Rewards", color = CyberTextBright, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("Incentivize study habits and household chores with bonus minutes", color = CyberTextDim, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(10.dp))
        }

        items(items) { (title, route) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate(route) },
                colors = CardDefaults.cardColors(containerColor = CyberCard),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(title, color = CyberTextBright, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = CyberTextDim)
                }
            }
        }
    }
}

@Composable
fun MoreSettingsTab(navController: NavController) {
    val items = listOf(
        Pair("Multi-Child Family Profiles", Screen.MultiChild.route),
        Pair("AI Parenting Assistant", Screen.AIAssistant.route),
        Pair("Weekly & Daily Reports", Screen.Reports.route),
        Pair("AI Daily Telemetry Digest", Screen.AIDigest.route),
        Pair("Family Sharing & Co-Parents", Screen.FamilySharing.route),
        Pair("Parent Security PIN & Biometrics", Screen.ParentSecurity.route),
        Pair("Hardware Battery Telemetry", Screen.BatteryMonitor.route),
        Pair("Notification & Incident Feed", Screen.NotificationsHub.route),
        Pair("Language & Regional Settings", Screen.LanguageSettings.route),
        Pair("Offline Sync Queue", Screen.OfflineSync.route)
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("More & Family Settings", color = CyberTextBright, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("Household configuration, analytics, AI assistant, and security", color = CyberTextDim, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(10.dp))
        }

        items(items) { (title, route) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate(route) },
                colors = CardDefaults.cardColors(containerColor = CyberCard),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(title, color = CyberTextBright, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = CyberTextDim)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AlertRed.copy(alpha = 0.2f), contentColor = AlertRed),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, AlertRed.copy(alpha = 0.4f))
            ) {
                Text("Log Out of Parent Portal", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}
