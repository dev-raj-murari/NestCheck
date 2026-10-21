package com.nestcheck.app.ui.parent.location

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nestcheck.app.data.model.*
import com.nestcheck.app.data.repository.GeofenceStatusResult
import com.nestcheck.app.navigation.Screen
import com.nestcheck.app.ui.sinan.LocationTab
import com.nestcheck.app.ui.sinan.LocationViewModel
import com.nestcheck.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LocationScreen(
    navController: NavController,
    viewModel: LocationViewModel = hiltViewModel()
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val liveLoc by viewModel.liveLocation.collectAsState()
    val zones by viewModel.zones.collectAsState()
    val breadcrumbs by viewModel.breadcrumbs.collectAsState()
    val transitionAlerts by viewModel.transitionAlerts.collectAsState()
    val speedAlerts by viewModel.speedAlerts.collectAsState()
    val speedThreshold by viewModel.speedThresholdKmh.collectAsState()
    val inTransitMode by viewModel.inTransitMode.collectAsState()
    val checkIns by viewModel.checkIns.collectAsState()
    val isAddZoneDialogOpen by viewModel.isAddZoneDialogOpen.collectAsState()
    val testResult by viewModel.testResult.collectAsState()

    var testLatInput by remember { mutableStateOf("19.1050") }
    var testLngInput by remember { mutableStateOf("72.8350") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBlack)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // TOP APP BAR
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back",
                        tint = CyberTextBright
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Location & Telemetry",
                    color = CyberTextBright,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Row {
                IconButton(onClick = { navController.navigate(Screen.ChildCheckIn.route) }) {
                    Icon(
                        imageVector = Icons.Outlined.NearMe,
                        contentDescription = "Child Check-In",
                        tint = Color(0xFF00E5FF)
                    )
                }
                IconButton(onClick = viewModel::openAddZoneDialog) {
                    Icon(
                        imageVector = Icons.Outlined.AddLocationAlt,
                        contentDescription = "Add Safe Zone",
                        tint = CyberTextBright
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // TAB SELECTOR: LIVE GPS | SAFE ZONES | SPEED GUARD | TRAIL LOG
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            LocationTab.values().forEach { tab ->
                val isSelected = currentTab == tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) White else CyberCard)
                        .border(1.dp, if (isSelected) White else CyberBorder, RoundedCornerShape(6.dp))
                        .clickable { viewModel.selectTab(tab) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab.label,
                        color = if (isSelected) Black else CyberTextDim,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // TAB CONTENTS
        when (currentTab) {
            LocationTab.LIVE_TELEMETRY -> {
                LiveTelemetryTabContent(
                    liveLoc = liveLoc,
                    checkIns = checkIns,
                    onRefresh = viewModel::refreshLiveGps,
                    onAcknowledgeCheckIn = viewModel::acknowledgeCheckIn,
                    onOpenCheckInScreen = { navController.navigate(Screen.ChildCheckIn.route) }
                )
            }
            LocationTab.GEOFENCES -> {
                GeofencesTabContent(
                    zones = zones,
                    transitionAlerts = transitionAlerts,
                    testResult = testResult,
                    testLatInput = testLatInput,
                    testLngInput = testLngInput,
                    onLatChange = { testLatInput = it },
                    onLngChange = { testLngInput = it },
                    onCheckPosition = viewModel::checkPosition,
                    onToggleZone = viewModel::toggleZone,
                    onDeleteZone = viewModel::deleteZone,
                    onOpenAddDialog = viewModel::openAddZoneDialog,
                    onSimulateTransition = viewModel::simulateTransition
                )
            }
            LocationTab.SPEED_MONITOR -> {
                SpeedMonitorTabContent(
                    liveLoc = liveLoc,
                    speedAlerts = speedAlerts,
                    speedThreshold = speedThreshold,
                    inTransitMode = inTransitMode,
                    onSetThreshold = viewModel::setSpeedThreshold,
                    onToggleTransit = viewModel::toggleTransitMode,
                    onTriggerSimulation = viewModel::triggerSpeedSimulation,
                    onDismissAlert = viewModel::dismissSpeedAlert
                )
            }
            LocationTab.BREADCRUMBS -> {
                BreadcrumbTrailTabContent(breadcrumbs = breadcrumbs)
            }
        }
    }

    if (isAddZoneDialogOpen) {
        AddGeofenceDialog(
            onDismiss = viewModel::closeAddZoneDialog,
            onConfirm = { name, lat, lng, radius, type, desc ->
                viewModel.addGeofence(name, lat, lng, radius, type, desc)
            }
        )
    }
}

// ============================================================================
// F11: LIVE GPS LOCATION TELEMETRY COMPOSABLES
// ============================================================================

@Composable
private fun LiveTelemetryTabContent(
    liveLoc: LiveLocationPoint,
    checkIns: List<ChildArrivalCheckIn>,
    onRefresh: () -> Unit,
    onAcknowledgeCheckIn: (String) -> Unit,
    onOpenCheckInScreen: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }

    // Subtle radar pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "radar")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // RADAR / STATUS HERO CARD
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CyberCard)
                    .border(1.dp, CyberBorder, RoundedCornerShape(12.dp))
                    .padding(18.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .scale(pulseScale)
                                    .clip(CircleShape)
                                    .background(SuccessGreen)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "LIVE SATELLITE FIX ACQUIRED",
                                color = SuccessGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "Updated: ${dateFormat.format(Date(liveLoc.timestamp))}",
                            color = CyberTextDim,
                            fontSize = 11.sp,
                            fontFamily = TechMono
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "${liveLoc.latitude}° N, ${liveLoc.longitude}° E",
                        color = CyberTextBright,
                        fontSize = 24.sp,
                        fontFamily = TechMono,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = liveLoc.addressName,
                        color = Color(0xFF00E5FF),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = CyberBorder, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    // 4 TELEMETRY PILL METRICS
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Accuracy", color = CyberTextDim, fontSize = 11.sp)
                            Text("±${liveLoc.accuracyMeters} m", color = CyberTextBright, fontSize = 13.sp, fontFamily = TechMono, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text("Altitude", color = CyberTextDim, fontSize = 11.sp)
                            Text("${liveLoc.altitudeMeters} m", color = CyberTextBright, fontSize = 13.sp, fontFamily = TechMono, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text("Velocity", color = CyberTextDim, fontSize = 11.sp)
                            Text("${liveLoc.speedKmh} km/h", color = if (liveLoc.speedKmh > 45) AlertRed else CyberTextBright, fontSize = 13.sp, fontFamily = TechMono, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text("Battery", color = CyberTextDim, fontSize = 11.sp)
                            Text("${liveLoc.batteryPercent}%", color = SuccessGreen, fontSize = 13.sp, fontFamily = TechMono, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // QUICK ACTIONS ROW
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onRefresh,
                    modifier = Modifier.weight(1f).height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = Black)
                ) {
                    Icon(Icons.Outlined.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Refresh GPS Fix", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = onOpenCheckInScreen,
                    modifier = Modifier.weight(1f).height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E5FF)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF00E5FF))
                ) {
                    Icon(Icons.Outlined.NearMe, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Kid Check-In UI", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        // F36: CHILD ARRIVAL CHECK-IN SECTION
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Arrival Check-In Pings (F36)",
                    color = CyberTextBright,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${checkIns.size} pings",
                    color = CyberTextDim,
                    fontSize = 11.sp
                )
            }
        }

        if (checkIns.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberCard)
                        .border(1.dp, CyberBorder, RoundedCornerShape(8.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No arrival check-in pings yet.", color = CyberTextDim, fontSize = 12.sp)
                }
            }
        } else {
            items(checkIns, key = { it.id }) { checkIn ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(CyberCard)
                        .border(1.dp, if (checkIn.isParentAcknowledged) CyberBorder else SuccessGreen, RoundedCornerShape(10.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(SuccessGreen)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("ARRIVED", color = Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(checkIn.locationLabel, color = CyberTextBright, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(checkIn.customNote, color = CyberTextDim, fontSize = 12.sp)
                            Text("Battery: ${checkIn.batteryLevel}% • Coordinates: ${checkIn.latitude}° N, ${checkIn.longitude}° E", color = CyberTextDim, fontSize = 10.sp, fontFamily = TechMono)
                        }

                        if (!checkIn.isParentAcknowledged) {
                            Button(
                                onClick = { onAcknowledgeCheckIn(checkIn.id) },
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen, contentColor = Black),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text("Acknowledge", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Text("Verified", color = CyberTextDim, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ============================================================================
// F10 & F29: GEOFENCES & TRANSITION ALERTS COMPOSABLES
// ============================================================================

@Composable
private fun GeofencesTabContent(
    zones: List<GeofenceZone>,
    transitionAlerts: List<GeofenceTransitionAlert>,
    testResult: GeofenceStatusResult?,
    testLatInput: String,
    testLngInput: String,
    onLatChange: (String) -> Unit,
    onLngChange: (String) -> Unit,
    onCheckPosition: (Double, Double) -> Unit,
    onToggleZone: (String) -> Unit,
    onDeleteZone: (String) -> Unit,
    onOpenAddDialog: () -> Unit,
    onSimulateTransition: (String, TransitionType, ZoneType) -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()) }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // GEOFENCE SUMMARY HERO
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CyberCard)
                    .border(1.dp, CyberBorder, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CIRCULAR SAFE ZONES (F10)",
                            color = CyberTextDim,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${zones.size} Zones Active",
                            color = SuccessGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Automated Boundary Transitions & Geofencing",
                        color = CyberTextBright,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Detects when child enters or leaves school, home, or restricted boundaries, logging instant transition events.",
                        color = CyberTextDim,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onOpenAddDialog,
                        modifier = Modifier.fillMaxWidth().height(40.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = Black)
                    ) {
                        Icon(Icons.Outlined.AddLocation, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Create Safe or Restricted Zone", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // F29: TRANSITION EVENTS FEED
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Transition Alert Log (F29)",
                    color = CyberTextBright,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                TextButton(
                    onClick = {
                        onSimulateTransition("MPSTME NMIMS", TransitionType.ENTERED, ZoneType.SAFE_ZONE)
                    }
                ) {
                    Text("+ Simulate Transition", color = Color(0xFF00E5FF), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (transitionAlerts.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(CyberCard).padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No geofence transitions recorded.", color = CyberTextDim, fontSize = 12.sp)
                }
            }
        } else {
            items(transitionAlerts, key = { it.id }) { alert ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberCard)
                        .border(
                            1.dp,
                            if (alert.zoneType == ZoneType.RESTRICTED_ZONE) AlertRed.copy(alpha = 0.5f) else CyberBorder,
                            RoundedCornerShape(8.dp)
                        )
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (alert.transitionType == TransitionType.ENTERED) SuccessGreen else WarningAmber)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = alert.transitionType.displayName.uppercase(),
                                    color = Black,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(alert.zoneName, color = CyberTextBright, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text(alert.address, color = CyberTextDim, fontSize = 11.sp)
                            }
                        }

                        Text(
                            text = dateFormat.format(Date(alert.timestamp)),
                            color = CyberTextDim,
                            fontSize = 10.sp,
                            fontFamily = TechMono
                        )
                    }
                }
            }
        }

        // CONFIGURED ZONES LIST
        item {
            Text(
                text = "Configured Perimeters (${zones.size})",
                color = CyberTextBright,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        items(zones, key = { it.id }) { zone ->
            GeofenceZoneCard(
                zone = zone,
                onToggleActive = { onToggleZone(zone.id) },
                onDelete = { onDeleteZone(zone.id) }
            )
        }

        // POSITION CHECKER
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(CyberCard)
                    .border(1.dp, CyberBorder, RoundedCornerShape(10.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Text("Check Coordinates Against Geofences", color = CyberTextBright, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = testLatInput,
                            onValueChange = onLatChange,
                            label = { Text("Lat", color = CyberTextDim) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = CyberTextBright, unfocusedTextColor = CyberTextBright)
                        )
                        OutlinedTextField(
                            value = testLngInput,
                            onValueChange = onLngChange,
                            label = { Text("Lng", color = CyberTextDim) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = CyberTextBright, unfocusedTextColor = CyberTextBright)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            val lat = testLatInput.toDoubleOrNull() ?: 19.1050
                            val lng = testLngInput.toDoubleOrNull() ?: 72.8350
                            onCheckPosition(lat, lng)
                        },
                        modifier = Modifier.fillMaxWidth().height(36.dp),
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = Black)
                    ) {
                        Text("Calculate Position", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    testResult?.let { res ->
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (res.isInsideSafeZone) "Child INSIDE SAFE ZONE (${res.currentZone?.name})" else if (res.isInsideRestrictedZone) "WARNING: Child INSIDE RESTRICTED ZONE" else "Outside all configured zones (${(res.distanceToNearestMeters).toInt()}m away)",
                            color = if (res.isInsideSafeZone) SuccessGreen else if (res.isInsideRestrictedZone) AlertRed else CyberTextDim,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ============================================================================
// F30: HIGH-VELOCITY TRAVEL ALERT COMPOSABLES
// ============================================================================

@Composable
private fun SpeedMonitorTabContent(
    liveLoc: LiveLocationPoint,
    speedAlerts: List<HighVelocityAlert>,
    speedThreshold: Float,
    inTransitMode: Boolean,
    onSetThreshold: (Float) -> Unit,
    onToggleTransit: () -> Unit,
    onTriggerSimulation: (Float) -> Unit,
    onDismissAlert: (String) -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // VELOCITY SPEEDOMETER CARD
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CyberCard)
                    .border(
                        1.dp,
                        if (liveLoc.speedKmh > speedThreshold && !inTransitMode) AlertRed else CyberBorder,
                        RoundedCornerShape(12.dp)
                    )
                    .padding(18.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "VELOCITY TELEMETRY (F30)",
                            color = CyberTextDim,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (inTransitMode) WarningAmber else if (liveLoc.speedKmh > speedThreshold) AlertRed else SuccessGreen)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (inTransitMode) "TRANSIT MODE ON" else if (liveLoc.speedKmh > speedThreshold) "SPEEDING DETECTED" else "NORMAL SPEEDS",
                                color = Black,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "${liveLoc.speedKmh.toInt()}",
                            color = if (liveLoc.speedKmh > speedThreshold && !inTransitMode) AlertRed else CyberTextBright,
                            fontSize = 44.sp,
                            fontFamily = TechMono,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "km/h",
                            color = CyberTextDim,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "Threshold: ${speedThreshold.toInt()} km/h",
                            color = CyberTextDim,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // SPEED THRESHOLD SLIDER
                    Text("Speed Warning Trigger Threshold", color = CyberTextDim, fontSize = 11.sp)
                    Slider(
                        value = speedThreshold,
                        onValueChange = onSetThreshold,
                        valueRange = 20f..90f,

                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF00E5FF),
                            activeTrackColor = Color(0xFF00E5FF),
                            inactiveTrackColor = Color(0xFFCBD5E1)
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Designate Transit Mode", color = CyberTextBright, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("Mutes speeding alerts when child travels by authorized bus/car", color = CyberTextDim, fontSize = 11.sp)
                        }
                        Switch(
                            checked = inTransitMode,
                            onCheckedChange = { onToggleTransit() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Black,
                                checkedTrackColor = White,
                                uncheckedThumbColor = CyberTextDim,
                                uncheckedTrackColor = CyberBlack
                            )
                        )
                    }
                }
            }
        }

        // SIMULATE SPEED BURSTS
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onTriggerSimulation(62.5f) },
                    modifier = Modifier.weight(1f).height(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AlertRed, contentColor = White)
                ) {
                    Text("Simulate Speeding (62 km/h)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { onTriggerSimulation(12.0f) },
                    modifier = Modifier.weight(1f).height(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCard, contentColor = CyberTextBright),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
                ) {
                    Text("Simulate Walking (12 km/h)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // HIGH-VELOCITY INCIDENT FEED
        item {
            Text(
                text = "Speeding Trigger Alerts (${speedAlerts.size})",
                color = CyberTextBright,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (speedAlerts.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(CyberCard).padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No high-velocity triggers recorded.", color = CyberTextDim, fontSize = 12.sp)
                }
            }
        } else {
            items(speedAlerts, key = { it.id }) { alert ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(CyberCard)
                        .border(1.dp, AlertRed.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(AlertRed)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("${alert.recordedSpeedKmh.toInt()} KM/H", color = White, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = TechMono)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Exceeded ${alert.speedThresholdKmh.toInt()} km/h cap", color = CyberTextBright, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(alert.locationAddress, color = CyberTextDim, fontSize = 11.sp)
                            Text("Logged at: ${dateFormat.format(Date(alert.timestamp))}", color = CyberTextDim, fontSize = 10.sp, fontFamily = TechMono)
                        }

                        IconButton(onClick = { onDismissAlert(alert.id) }) {
                            Icon(Icons.Outlined.Check, contentDescription = "Dismiss", tint = CyberTextDim)
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ============================================================================
// F12: HISTORICAL BREADCRUMB TRAIL COMPOSABLES
// ============================================================================

@Composable
private fun BreadcrumbTrailTabContent(
    breadcrumbs: List<LocationBreadcrumb>
) {
    val timeFormat = remember { SimpleDateFormat("HH:mm a", Locale.getDefault()) }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CyberCard)
                    .border(1.dp, CyberBorder, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "PHYSICAL MOVEMENT TIMELINE (F12)",
                            color = CyberTextDim,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Today's Itinerary",
                            color = Color(0xFF00E5FF),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Timestamped Movement Waypoints",
                        color = CyberTextBright,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Continuous historical breadcrumbs tracking child transit, arrival, and stay durations across known waypoints.",
                        color = CyberTextDim,
                        fontSize = 12.sp
                    )
                }
            }
        }

        item {
            Text(
                text = "Chronological Waypoints (${breadcrumbs.size})",
                color = CyberTextBright,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        items(breadcrumbs, key = { it.id }) { crumb ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(CyberCard)
                    .border(1.dp, CyberBorder, RoundedCornerShape(10.dp))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00E5FF))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(crumb.title, color = CyberTextBright, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(timeFormat.format(Date(crumb.timestamp)), color = CyberTextDim, fontSize = 10.sp, fontFamily = TechMono)
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${crumb.category} • Stay: ${crumb.durationSpentMinutes} mins", color = CyberTextDim, fontSize = 11.sp)
                            Text("${crumb.latitude}° N, ${crumb.longitude}° E", color = CyberTextDim, fontSize = 10.sp, fontFamily = TechMono)
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ============================================================================
// SHARED DIALOGS & CARDS
// ============================================================================

@Composable
private fun GeofenceZoneCard(
    zone: GeofenceZone,
    onToggleActive: () -> Unit,
    onDelete: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(CyberCard)
            .border(
                1.dp,
                if (zone.zoneType == ZoneType.RESTRICTED_ZONE) AlertRed.copy(alpha = 0.5f) else CyberBorder,
                RoundedCornerShape(10.dp)
            )
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (zone.zoneType == ZoneType.SAFE_ZONE) SuccessGreen else AlertRed)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = zone.zoneType.displayName.uppercase(),
                            color = if (zone.zoneType == ZoneType.SAFE_ZONE) Black else White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(CyberBlack)
                            .border(0.5.dp, CyberBorderBright, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${zone.radiusMeters.toInt()}m Radius",
                            color = CyberTextBright,
                            fontSize = 10.sp,
                            fontFamily = TechMono
                        )
                    }
                }

                Switch(
                    checked = zone.isActive,
                    onCheckedChange = { onToggleActive() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Black,
                        checkedTrackColor = if (zone.zoneType == ZoneType.SAFE_ZONE) SuccessGreen else AlertRed,
                        uncheckedThumbColor = CyberTextDim,
                        uncheckedTrackColor = CyberBlack
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = zone.name,
                color = CyberTextBright,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            if (zone.description.isNotBlank()) {
                Text(
                    text = zone.description,
                    color = CyberTextDim,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${zone.latitude}° N, ${zone.longitude}° E",
                    color = CyberTextDim,
                    fontSize = 11.sp,
                    fontFamily = TechMono
                )

                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Delete Zone",
                        tint = CyberTextDim,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AddGeofenceDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Double, Double, Float, ZoneType, String) -> Unit
) {
    var nameInput by remember { mutableStateOf("") }
    var latInput by remember { mutableStateOf("19.1150") }
    var lngInput by remember { mutableStateOf("72.8420") }
    var radiusSlider by remember { mutableFloatStateOf(300f) }
    var selectedType by remember { mutableStateOf(ZoneType.SAFE_ZONE) }
    var descriptionInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CyberCard,
        title = {
            Text("Create Geofence Zone", color = CyberTextBright, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("Zone Name (e.g. Grandma's House)", color = CyberTextDim) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = CyberTextBright,
                        unfocusedTextColor = CyberTextBright,
                        focusedBorderColor = White,
                        unfocusedBorderColor = CyberBorder
                    )
                )

                // ZONE TYPE SELECTOR
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (selectedType == ZoneType.SAFE_ZONE) SuccessGreen else CyberBlack)
                            .border(1.dp, if (selectedType == ZoneType.SAFE_ZONE) SuccessGreen else CyberBorder, RoundedCornerShape(6.dp))
                            .clickable { selectedType = ZoneType.SAFE_ZONE },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Safe Zone", color = if (selectedType == ZoneType.SAFE_ZONE) Black else White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (selectedType == ZoneType.RESTRICTED_ZONE) AlertRed else CyberBlack)
                            .border(1.dp, if (selectedType == ZoneType.RESTRICTED_ZONE) AlertRed else CyberBorder, RoundedCornerShape(6.dp))
                            .clickable { selectedType = ZoneType.RESTRICTED_ZONE },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Restricted", color = White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = latInput,
                        onValueChange = { latInput = it },
                        label = { Text("Latitude", color = CyberTextDim) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = CyberTextBright,
                            unfocusedTextColor = CyberTextBright,
                            focusedBorderColor = White,
                            unfocusedBorderColor = CyberBorder
                        )
                    )
                    OutlinedTextField(
                        value = lngInput,
                        onValueChange = { lngInput = it },
                        label = { Text("Longitude", color = CyberTextDim) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = CyberTextBright,
                            unfocusedTextColor = CyberTextBright,
                            focusedBorderColor = White,
                            unfocusedBorderColor = CyberBorder
                        )
                    )
                }

                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Radius", color = CyberTextDim, fontSize = 12.sp)
                        Text("${radiusSlider.toInt()} meters", color = CyberTextBright, fontSize = 12.sp, fontFamily = TechMono, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = radiusSlider,
                        onValueChange = { radiusSlider = it },
                        valueRange = 100f..1000f,

                        colors = SliderDefaults.colors(
                            thumbColor = if (selectedType == ZoneType.SAFE_ZONE) SuccessGreen else AlertRed,
                            activeTrackColor = if (selectedType == ZoneType.SAFE_ZONE) SuccessGreen else AlertRed,
                            inactiveTrackColor = Color(0xFFCBD5E1)
                        )
                    )
                }

                OutlinedTextField(
                    value = descriptionInput,
                    onValueChange = { descriptionInput = it },
                    label = { Text("Description Note", color = CyberTextDim) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = CyberTextBright,
                        unfocusedTextColor = CyberTextBright,
                        focusedBorderColor = White,
                        unfocusedBorderColor = CyberBorder
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nameInput.isNotBlank()) {
                        val lat = latInput.toDoubleOrNull() ?: 19.1150
                        val lng = lngInput.toDoubleOrNull() ?: 72.8420
                        onConfirm(nameInput, lat, lng, radiusSlider, selectedType, descriptionInput)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = Black)
            ) {
                Text("Save Zone", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = CyberTextDim)
            }
        }
    )
}
