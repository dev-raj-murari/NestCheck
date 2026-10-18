package com.nestcheck.app.ui.parent.shell

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
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
                onAlertsClick = { navController.navigate(Screen.NotificationsHub.route) },
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
        // Child Switcher Pill Button (Like Family Link)
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
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Alerts",
                    tint = GflTextDark,
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
                        val hours = (180 - syncState.remainingMinutes) / 60
                        val mins = (180 - syncState.remainingMinutes) % 60
                        Text(
                            text = "${hours}h ${mins}m",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = GflTextDark
                        )
                        Text(
                            text = "Time spent today",
                            fontSize = 14.sp,
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

        // 2. Device Card with Instant Remote Lock Button
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
                                Text("Google Pixel 9", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = GflTextDark)
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

                    // Authentic Lock / Unlock Pill Button
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
                            text = if (syncState.isDeviceLocked) "Unlock device" else "Lock",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }

        // 3. Time Limits Card
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
                        Text("Daily limit: 3 hrs • Bedtime off", fontSize = 13.sp, color = GflTextMuted)
                    }
                    Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = GflTextMuted)
                }
            }
        }

        // 4. Schedules Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate(Screen.BedtimeSchedule.route) },
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
                        Icon(Icons.Outlined.CalendarToday, contentDescription = null, tint = GflTextDark)
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Schedules", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = GflTextDark)
                        Text("Downtime & school time off", fontSize = 13.sp, color = GflTextMuted)
                    }
                    Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = GflTextMuted)
                }
            }
        }

        // 5. School Schedule & Exam Countdown Banner (Requested Feature)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("SCHOOL & EXAM COUNTDOWN", color = GflGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("Term 2", color = GflTextMuted, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("📐 Mathematics Finals in 4 days ⏳", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = GflTextDark)
                    Text("Mon-Fri: 8:30 AM - 3:15 PM • Oakridge High", fontSize = 13.sp, color = GflTextMuted)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("🏖️ Summer Vacation begins in 18 days", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = GflGreen)
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
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
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = GflCardWhite,
                    border = androidx.compose.foundation.BorderStroke(1.dp, GflBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("G", fontWeight = FontWeight.Bold, color = GflBlue, fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Account settings", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = GflTextDark)
                    }
                }
            }
        }

        // 1. Google Play & In-App Purchases Lock (User's Critical Request)
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

        // 2. YouTube Content Controls
        item {
            ControlCardItem(
                title = "YouTube",
                subtitle = "Tools for parents, kids & teens • Restricted Mode ON",
                icon = Icons.Outlined.PlayCircle,
                iconTint = Color(0xFFFF0000),
                onClick = { navController.navigate(Screen.ContentFilter.route) }
            )
        }

        // 3. Google Chrome & Web
        item {
            ControlCardItem(
                title = "Google Chrome and Web",
                subtitle = "Website & browser restrictions • Explicit sites blocked",
                icon = Icons.Outlined.Public,
                iconTint = Color(0xFF4285F4),
                onClick = { navController.navigate(Screen.BlockedUrl.route) }
            )
        }

        // 4. Google Search & SafeSearch
        item {
            ControlCardItem(
                title = "Google Search",
                subtitle = "SafeSearch Lock ON",
                icon = Icons.Outlined.Search,
                iconTint = GflAmber,
                onClick = { navController.navigate(Screen.SafeSearch.route) }
            )
        }

        // 5. App Quota & Restrictions
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

        // Bottom Child Location Sheet (Like Google Family Link)
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
                // Segmented Pill: [ Kids ] [ Family places ]
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(GflBgLight)
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(GflCardWhite)
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Kids", fontWeight = FontWeight.Bold, color = GflTextDark, fontSize = 13.sp)
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Family places", fontWeight = FontWeight.Medium, color = GflTextMuted, fontSize = 13.sp)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Olivia Location Row
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
                            Text("${syncState.kidBattery}% ⚡ • Updated live", fontSize = 12.sp, color = GflGreen, fontWeight = FontWeight.Medium)
                        }
                    }
                    IconButton(onClick = {
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(childLocation, 16f)
                    }) {
                        Icon(Icons.Outlined.Refresh, contentDescription = "Refresh", tint = GflBlue)
                    }
                }
            }
        }
    }
}
