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

            IconButton(onClick = viewModel::openAddZoneDialog) {
                Icon(
                    imageVector = Icons.Outlined.AddLocationAlt,
                    contentDescription = "Add Safe Zone",
                    tint = CyberTextBright
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // TAB SELECTOR
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        when (currentTab) {
            LocationTab.LIVE_TELEMETRY -> {
                LiveTelemetryTabContent(
                    liveLoc = liveLoc,
                    onRefresh = viewModel::refreshLiveGps
                )
            }
            LocationTab.BREADCRUMBS -> {
                BreadcrumbTrailTabContent(breadcrumbs = breadcrumbs)
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
    onRefresh: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }

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

        // REFRESH BUTTON
        item {
            Button(
                onClick = onRefresh,
                modifier = Modifier.fillMaxWidth().height(44.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = Black)
            ) {
                Icon(Icons.Outlined.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Refresh GPS Fix", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        // HARDWARE TELEMETRY DETAILS CARD
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CyberCard)
                    .border(1.dp, CyberBorder, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Hardware Telemetry Details",
                        color = CyberTextBright,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    TelemetryRow("Location Provider", liveLoc.provider)
                    TelemetryRow("Compass Heading", "${liveLoc.headingDegrees}° SE")
                    TelemetryRow("Update Frequency", "Adaptive Real-Time (30s interval)")
                    TelemetryRow("Signal Quality", "Excellent (7 Satellites locked)")
                    TelemetryRow("Geodesic Coordinate Datum", "WGS 84 (GPS Standard)")
                }
            }
        }
    }
}

@Composable
private fun TelemetryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = CyberTextDim, fontSize = 12.sp)
        Text(value, color = CyberTextBright, fontSize = 12.sp, fontFamily = TechMono)
    }
}

// ============================================================================
// F10: GEOFENCES TAB CONTENT
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
    val safeZonesCount = zones.count { it.zoneType == ZoneType.SAFE_ZONE && it.isActive }
    val restrictedZonesCount = zones.count { it.zoneType == ZoneType.RESTRICTED_ZONE && it.isActive }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // SUMMARY ROW
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ZoneMetricCard(
                    title = "Safe Zones",
                    count = safeZonesCount,
                    accentColor = SuccessGreen,
                    modifier = Modifier.weight(1f)
                )
                ZoneMetricCard(
                    title = "Restricted Zones",
                    count = restrictedZonesCount,
                    accentColor = AlertRed,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // POSITION SIMULATOR CARD
        item {
            PositionSimulatorCard(
                lat = testLatInput,
                lng = testLngInput,
                result = testResult,
                onLatChange = onLatChange,
                onLngChange = onLngChange,
                onTestClick = {
                    val lat = testLatInput.toDoubleOrNull() ?: 0.0
                    val lng = testLngInput.toDoubleOrNull() ?: 0.0
                    onCheckPosition(lat, lng)
                }
            )
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

        // ACTIVE GEOFENCE ZONES HEADER
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Configured Boundaries (${zones.size})",
                    color = CyberTextBright,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onOpenAddDialog) {
                    Text("+ Add Zone", color = White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // LIST OF ZONES
        if (zones.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberCard)
                        .border(1.dp, CyberBorder, RoundedCornerShape(8.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No geofence boundaries configured.", color = CyberTextDim, fontSize = 13.sp)
                }
            }
        } else {
            items(zones, key = { it.id }) { zone ->
                GeofenceZoneCard(
                    zone = zone,
                    onToggleActive = { onToggleZone(zone.id) },
                    onDelete = { onDeleteZone(zone.id) }
                )
            }
        }
    }
}

@Composable
private fun ZoneMetricCard(
    title: String,
    count: Int,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(CyberCard)
            .border(1.dp, CyberBorder, RoundedCornerShape(10.dp))
            .padding(14.dp)
    ) {
        Column {
            Text(text = title, color = CyberTextDim, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(accentColor)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "$count Active",
                    color = CyberTextBright,
                    fontSize = 18.sp,
                    fontFamily = TechMono,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun PositionSimulatorCard(
    lat: String,
    lng: String,
    result: GeofenceStatusResult?,
    onLatChange: (String) -> Unit,
    onLngChange: (String) -> Unit,
    onTestClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(CyberCard)
            .border(1.dp, CyberBorder, RoundedCornerShape(10.dp))
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "GPS Coordinates Simulator",
                    color = CyberTextBright,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "TEST HARNESS",
                    color = CyberTextDim,
                    fontSize = 10.sp,
                    fontFamily = TechMono
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = lat,
                    onValueChange = onLatChange,
                    label = { Text("Latitude", fontSize = 10.sp) },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = CyberTextBright,
                        unfocusedTextColor = CyberTextBright,
                        focusedBorderColor = White,
                        unfocusedBorderColor = CyberBorder
                    ),
                    singleLine = true
                )
                OutlinedTextField(
                    value = lng,
                    onValueChange = onLngChange,
                    label = { Text("Longitude", fontSize = 10.sp) },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = CyberTextBright,
                        unfocusedTextColor = CyberTextBright,
                        focusedBorderColor = White,
                        unfocusedBorderColor = CyberBorder
                    ),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onTestClick,
                modifier = Modifier.fillMaxWidth().height(40.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = Black)
            ) {
                Text("Evaluate Boundary Status", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            if (result != null) {
                Spacer(modifier = Modifier.height(10.dp))
                val isSafe = result.isInsideSafeZone
                val isRestricted = result.isInsideRestrictedZone

                val bannerColor = when {
                    isRestricted -> AlertRed
                    isSafe -> SuccessGreen
                    else -> WarningAmber
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(bannerColor.copy(alpha = 0.15f))
                        .border(1.dp, bannerColor, RoundedCornerShape(6.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        val message = when {
                            isSafe -> "Child is safely inside: ${result.currentZone?.name}"
                            isRestricted -> "ALERT: Child inside restricted boundary: ${result.currentZone?.name}"
                            else -> "Outside all boundaries (${result.distanceToNearestMeters.toInt()}m from nearest)"
                        }
                        Text(
                            text = message,
                            color = bannerColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GeofenceZoneCard(
    zone: GeofenceZone,
    onToggleActive: () -> Unit,
    onDelete: () -> Unit
) {
    val isSafe = zone.zoneType == ZoneType.SAFE_ZONE
    val typeColor = if (isSafe) SuccessGreen else AlertRed

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(CyberCard)
            .border(1.dp, CyberBorder, RoundedCornerShape(10.dp))
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
                            .background(typeColor.copy(alpha = 0.2f))
                            .border(1.dp, typeColor, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (isSafe) "SAFE" else "RESTRICTED",
                            color = typeColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = zone.name,
                        color = CyberTextBright,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = zone.isActive,
                        onCheckedChange = { onToggleActive() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Black,
                            checkedTrackColor = White,
                            uncheckedThumbColor = CyberTextDim,
                            uncheckedTrackColor = CyberBlack
                        )
                    )
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Outlined.DeleteOutline,
                            contentDescription = "Delete",
                            tint = CyberTextDim,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = zone.description,
                color = CyberTextDim,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${zone.latitude}° N, ${zone.longitude}° E",
                    color = CyberTextDim,
                    fontSize = 11.sp,
                    fontFamily = TechMono
                )
                Text(
                    text = "Radius: ${zone.radiusMeters.toInt()}m",
                    color = CyberTextBright,
                    fontSize = 11.sp,
                    fontFamily = TechMono
                )
            }
        }
    }
}

@Composable
private fun AddGeofenceDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Double, Double, Float, ZoneType, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var lat by remember { mutableStateOf("19.1050") }
    var lng by remember { mutableStateOf("72.8350") }
    var radius by remember { mutableStateOf("250") }
    var selectedType by remember { mutableStateOf(ZoneType.SAFE_ZONE) }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Safe/Restricted Zone", color = CyberTextBright, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Zone Name (e.g. School)") },
                    singleLine = true
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = lat,
                        onValueChange = { lat = it },
                        label = { Text("Latitude") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = lng,
                        onValueChange = { lng = it },
                        label = { Text("Longitude") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
                OutlinedTextField(
                    value = radius,
                    onValueChange = { radius = it },
                    label = { Text("Radius (meters)") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description / Notes") }
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    FilterChip(
                        selected = selectedType == ZoneType.SAFE_ZONE,
                        onClick = { selectedType = ZoneType.SAFE_ZONE },
                        label = { Text("Safe Zone") }
                    )
                    FilterChip(
                        selected = selectedType == ZoneType.RESTRICTED_ZONE,
                        onClick = { selectedType = ZoneType.RESTRICTED_ZONE },
                        label = { Text("Restricted Zone") }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val latVal = lat.toDoubleOrNull() ?: 0.0
                    val lngVal = lng.toDoubleOrNull() ?: 0.0
                    val radVal = radius.toFloatOrNull() ?: 200f
                    onConfirm(name, latVal, lngVal, radVal, selectedType, description)
                },
                colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = Black)
            ) {
                Text("Save Zone")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = CyberTextDim)
            }
        },
        containerColor = CyberCard,
        shape = RoundedCornerShape(12.dp)
    )
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
