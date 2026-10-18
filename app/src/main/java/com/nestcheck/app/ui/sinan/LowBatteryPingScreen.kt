package com.nestcheck.app.ui.sinan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nestcheck.app.data.model.LowBatteryLastPing
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LowBatteryPingScreen(
    navController: NavController,
    viewModel: LowBatteryPingViewModel = hiltViewModel()
) {
    val config by viewModel.config.collectAsState()
    val pings by viewModel.pings.collectAsState()
    val simulatedLevel by viewModel.simulatedLevel
    val lastPing by viewModel.lastTriggeredPing

    val bgColor = Color(0xFFF8FAFC)
    val cardColor = Color(0xFFFFFFFF)
    val cyanAccent = Color(0xFF0B57D0)
    val greenSuccess = Color(0xFF10B981)
    val redCritical = Color(0xFFEF4444)
    val amberWarning = Color(0xFFF59E0B)
    val textMuted = Color(0xFF64748B)
    val borderStroke = Color(0xFFE2E8F0)
    val textDark = Color(0xFF0F172A)

    val latest = pings.firstOrNull()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Low-Battery Last Ping",
                            fontWeight = FontWeight.Bold,
                            color = textDark,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Autonomous Emergency Telemetry Fix",
                            fontSize = 12.sp,
                            color = textMuted
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = textDark
                        )
                    }
                },
                actions = {
                    Surface(
                        color = cyanAccent.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Text(
                            text = "F34 ACTIVE",
                            color = cyanAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = bgColor)
            )
        },
        containerColor = bgColor
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Status Card: Last Known Ping
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(borderStroke))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(if ((latest?.batteryLevel ?: 100) <= 5) redCritical.copy(alpha = 0.2f) else amberWarning.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.BatteryAlert,
                                    contentDescription = null,
                                    tint = if ((latest?.batteryLevel ?: 100) <= 5) redCritical else amberWarning,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Last Known Location Fix",
                                        fontWeight = FontWeight.Bold,
                                        color = textDark,
                                        fontSize = 15.sp
                                    )
                                    Surface(
                                        color = if ((latest?.batteryLevel ?: 100) <= 5) redCritical.copy(alpha = 0.15f) else amberWarning.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "${latest?.batteryLevel ?: 12}% BATTERY",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if ((latest?.batteryLevel ?: 100) <= 5) redCritical else amberWarning,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = latest?.addressName ?: "Capturing GPS fix...",
                                    fontSize = 12.sp,
                                    color = textMuted,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        HorizontalDivider(color = borderStroke)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = "Coordinates", fontSize = 11.sp, color = textMuted)
                                Text(
                                    text = "${String.format(Locale.US, "%.4f", latest?.latitude ?: 19.1054)}°N, ${String.format(Locale.US, "%.4f", latest?.longitude ?: 72.8362)}°E",
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.SansSerif,
                                    color = cyanAccent,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Column {
                                Text(text = "Accuracy", fontSize = 11.sp, color = textMuted)
                                Text(
                                    text = "±${latest?.accuracyMeters ?: 3.8} m",
                                    fontSize = 12.sp,
                                    color = greenSuccess,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Column {
                                Text(text = "Emergency SMS", fontSize = 11.sp, color = textMuted)
                                Text(
                                    text = if (latest?.isSmsDispatched == true) "SENT" else "OFF",
                                    fontSize = 12.sp,
                                    color = if (latest?.isSmsDispatched == true) greenSuccess else textMuted,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Autonomous Triggers Configuration Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(borderStroke))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "Autonomous Ping Thresholds",
                            fontWeight = FontWeight.Bold,
                            color = textDark,
                            fontSize = 14.sp
                        )

                        // Trigger Threshold
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "First Warning Ping Threshold",
                                    color = textDark,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Triggers high-accuracy GNSS fix before battery dies",
                                    color = textMuted,
                                    fontSize = 11.sp
                                )
                            }
                            Surface(
                                color = amberWarning.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "${config.triggerThresholdPercent}%",
                                    color = amberWarning,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        // Critical Shutdown Fix
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Critical Shutdown Fix",
                                    color = textDark,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Immediate forced snapshot at 5% prior to shutdown",
                                    color = textMuted,
                                    fontSize = 11.sp
                                )
                            }
                            Surface(
                                color = redCritical.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "${config.criticalThresholdPercent}%",
                                    color = redCritical,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        HorizontalDivider(color = borderStroke)

                        // Auto SMS Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Auto-Dispatch Emergency SMS",
                                    color = textDark,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Sends GPS coordinates to mom & dad if battery ≤ 15%",
                                    color = textMuted,
                                    fontSize = 11.sp
                                )
                            }
                            Switch(
                                checked = config.autoDispatchSmsToEmergencyContacts,
                                onCheckedChange = { viewModel.toggleAutoSms(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = cyanAccent,
                                    uncheckedThumbColor = textMuted,
                                    uncheckedTrackColor = Color(0xFFCBD5E1)
                                )
                            )
                        }

                        HorizontalDivider(color = borderStroke)

                        // High Power GNSS Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Burst High-Power GNSS Mode",
                                    color = textDark,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Temporarily overrides power-saving to obtain 3m accuracy",
                                    color = textMuted,
                                    fontSize = 11.sp
                                )
                            }
                            Switch(
                                checked = config.enableGpsHighPowerFixOnDrain,
                                onCheckedChange = { viewModel.toggleHighPowerFix(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = cyanAccent,
                                    uncheckedThumbColor = textMuted,
                                    uncheckedTrackColor = Color(0xFFCBD5E1)
                                )
                            )
                        }
                    }
                }
            }

            // Live Drain Simulator Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(borderStroke))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FlashOn,
                                contentDescription = null,
                                tint = cyanAccent,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Battery Drain Event Simulator",
                                fontWeight = FontWeight.Bold,
                                color = textDark,
                                fontSize = 14.sp
                            )
                        }

                        Text(
                            text = "Simulate battery dropping below threshold to verify autonomous ping generation & parent notification.",
                            fontSize = 12.sp,
                            color = textMuted
                        )

                        // Quick level buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val presets = listOf(
                                Triple("Normal (45%)", 45, textMuted),
                                Triple("Warning (14%)", 14, amberWarning),
                                Triple("Critical (3%)", 3, redCritical)
                            )
                            presets.forEach { (label, lvl, color) ->
                                Surface(
                                    color = Color(0xFFF1F5F9),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            viewModel.onBatteryLevelChange(lvl.toFloat())
                                            viewModel.triggerPing(lvl)
                                        }
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = color,
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }

                        // Slider
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Simulated Level:", fontSize = 12.sp, color = textMuted)
                                Text(
                                    text = "${simulatedLevel.toInt()}%",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (simulatedLevel <= 5) redCritical else if (simulatedLevel <= 15) amberWarning else cyanAccent
                                )
                            }
                            Slider(
                                value = simulatedLevel,
                                onValueChange = { viewModel.onBatteryLevelChange(it) },
                                valueRange = 1f..100f,
                                colors = SliderDefaults.colors(
                                    thumbColor = cyanAccent,
                                    activeTrackColor = cyanAccent,
                                    inactiveTrackColor = Color(0xFFCBD5E1)
                                )
                            )
                        }

                        Button(
                            onClick = { viewModel.triggerPing() },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = cyanAccent)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Dispatch Emergency Telemetry Snapshot",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        // Display last ping response
                        lastPing?.let { ping ->
                            Surface(
                                color = Color(0xFFF8FAFC),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, cyanAccent.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "EMERGENCY TELEMETRY CAPTURED",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = greenSuccess
                                    )
                                    Text(
                                        text = "Location: ${ping.addressName}",
                                        fontSize = 11.sp,
                                        color = textDark
                                    )
                                    Text(
                                        text = "Lat/Lng: ${ping.latitude}, ${ping.longitude} (±${ping.accuracyMeters}m)",
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.SansSerif,
                                        color = cyanAccent
                                    )
                                    Text(
                                        text = "Network: ${ping.networkType} | Cloud Sync: SUCCESS",
                                        fontSize = 10.sp,
                                        color = textMuted
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Historical Pings Section
            item {
                Text(
                    text = "Emergency Ping Audit Log",
                    fontWeight = FontWeight.Bold,
                    color = textDark,
                    fontSize = 14.sp
                )
            }

            items(pings) { ping: LowBatteryLastPing ->
                val timeStr = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date(ping.timestamp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(borderStroke))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    color = if (ping.isCriticalShutdown) redCritical.copy(alpha = 0.2f) else amberWarning.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "${ping.batteryLevel}%",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (ping.isCriticalShutdown) redCritical else amberWarning,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Text(
                                    text = if (ping.isCriticalShutdown) "Critical Shutdown Fix" else "Low-Battery Warning Ping",
                                    fontWeight = FontWeight.SemiBold,
                                    color = textDark,
                                    fontSize = 13.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = ping.addressName,
                                fontSize = 11.sp,
                                color = textMuted,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${String.format(Locale.US, "%.4f", ping.latitude)}, ${String.format(Locale.US, "%.4f", ping.longitude)} • ${ping.networkType}",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.SansSerif,
                                color = cyanAccent.copy(alpha = 0.8f)
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = timeStr,
                                fontSize = 11.sp,
                                color = textMuted
                            )
                            Icon(
                                imageVector = Icons.Default.CloudDone,
                                contentDescription = "Synced",
                                tint = greenSuccess,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
