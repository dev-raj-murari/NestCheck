package com.nestcheck.app.ui.parent.location

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.*
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
import androidx.navigation.NavController
import com.nestcheck.app.data.model.GeofenceZone
import com.nestcheck.app.data.model.ZoneType
import com.nestcheck.app.ui.sinan.LocationViewModel
import com.nestcheck.app.ui.theme.*

@Composable
fun LocationScreen(
    navController: NavController,
    viewModel: LocationViewModel = hiltViewModel()
) {
    val zones by viewModel.zones.collectAsState()
    val isAddZoneDialogOpen by viewModel.isAddZoneDialogOpen.collectAsState()
    val testResult by viewModel.testResult.collectAsState()

    var testLatInput by remember { mutableStateOf("19.1050") }
    var testLngInput by remember { mutableStateOf("72.8350") }

    val safeZonesCount = zones.count { it.zoneType == ZoneType.SAFE_ZONE && it.isActive }
    val restrictedZonesCount = zones.count { it.zoneType == ZoneType.RESTRICTED_ZONE && it.isActive }

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
                    text = "Geofencing & Safe Zones",
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

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // HERO GEOFENCE STATUS CARD
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
                                text = "CIRCULAR GEOFENCE ENGINE",
                                color = CyberTextDim,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(SuccessGreen)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Monitoring Live",
                                    color = SuccessGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Automated Perimeter & Boundary Guard",
                            color = CyberTextBright,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Configure custom circular perimeters (100m to 1000m) around schools, home, or restricted areas. Generates instant arrival & departure telemetry.",
                            color = CyberTextDim,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = CyberBorder, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Safe Zones", color = CyberTextDim, fontSize = 11.sp)
                                Text("$safeZonesCount active", color = SuccessGreen, fontSize = 14.sp, fontFamily = TechMono, fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Text("Restricted Zones", color = CyberTextDim, fontSize = 11.sp)
                                Text("$restrictedZonesCount active", color = if (restrictedZonesCount > 0) AlertRed else CyberTextDim, fontSize = 14.sp, fontFamily = TechMono, fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Text("Total Configured", color = CyberTextDim, fontSize = 11.sp)
                                Text("${zones.size} zones", color = CyberTextBright, fontSize = 14.sp, fontFamily = TechMono, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // ADD ZONE ACTION BUTTON
            item {
                Button(
                    onClick = viewModel::openAddZoneDialog,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = Black)
                ) {
                    Icon(Icons.Outlined.AddCircleOutline, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Create New Geofence Zone", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            // SECTION HEADER
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Configured Perimeters",
                        color = CyberTextBright,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${zones.size} total",
                        color = CyberTextDim,
                        fontSize = 12.sp
                    )
                }
            }

            // GEOFENCE LIST
            items(zones, key = { it.id }) { zone ->
                GeofenceZoneCard(
                    zone = zone,
                    onToggleActive = { viewModel.toggleZone(zone.id) },
                    onDelete = { viewModel.deleteZone(zone.id) }
                )
            }

            // POSITION VERIFICATION SIMULATOR
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
                        Text(
                            text = "Geofence Telemetry Checker",
                            color = CyberTextBright,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Evaluate custom GPS coordinates against configured boundary radii.",
                            color = CyberTextDim,
                            fontSize = 11.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = testLatInput,
                                onValueChange = { testLatInput = it },
                                label = { Text("Latitude", color = CyberTextDim, fontSize = 11.sp) },
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
                                value = testLngInput,
                                onValueChange = { testLngInput = it },
                                label = { Text("Longitude", color = CyberTextDim, fontSize = 11.sp) },
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

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                val lat = testLatInput.toDoubleOrNull() ?: 19.1050
                                val lng = testLngInput.toDoubleOrNull() ?: 72.8350
                                viewModel.checkPosition(lat, lng)
                            },
                            modifier = Modifier.fillMaxWidth().height(40.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = Black)
                        ) {
                            Text("Verify Coordinates", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        testResult?.let { res ->
                            Spacer(modifier = Modifier.height(12.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        when {
                                            res.isInsideRestrictedZone -> AlertRed.copy(alpha = 0.2f)
                                            res.isInsideSafeZone -> SuccessGreen.copy(alpha = 0.2f)
                                            else -> CyberBlack
                                        }
                                    )
                                    .border(
                                        1.dp,
                                        when {
                                            res.isInsideRestrictedZone -> AlertRed
                                            res.isInsideSafeZone -> SuccessGreen
                                            else -> CyberBorder
                                        },
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Text(
                                        text = when {
                                            res.isInsideRestrictedZone -> "ALERT: Child is INSIDE RESTRICTED PERIMETER!"
                                            res.isInsideSafeZone -> "VERIFIED: Child is INSIDE SAFE ZONE (${res.currentZone?.name})"
                                            else -> "OUTSIDE GEOFENCE (Nearest perimeter: ${(res.distanceToNearestMeters).toInt()}m away)"
                                        },
                                        color = when {
                                            res.isInsideRestrictedZone -> AlertRed
                                            res.isInsideSafeZone -> SuccessGreen
                                            else -> CyberTextBright
                                        },
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
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

    if (isAddZoneDialogOpen) {
        AddGeofenceDialog(
            onDismiss = viewModel::closeAddZoneDialog,
            onConfirm = { name, lat, lng, radius, type, desc ->
                viewModel.addGeofence(name, lat, lng, radius, type, desc)
            }
        )
    }
}

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
                        steps = 9,
                        colors = SliderDefaults.colors(
                            thumbColor = White,
                            activeTrackColor = if (selectedType == ZoneType.SAFE_ZONE) SuccessGreen else AlertRed,
                            inactiveTrackColor = CyberBorder
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
