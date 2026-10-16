package com.nestcheck.app.ui.parent.battery

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.BatteryChargingFull
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Power
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nestcheck.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BatteryMonitorScreen(
    navController: NavController,
    viewModel: BatteryMonitorViewModel = hiltViewModel()
) {
    val telemetry by viewModel.batteryState.collectAsState()
    val scrollState = rememberScrollState()

    val isLowBattery = telemetry.percentage <= telemetry.lowBatteryAlertThreshold
    val batteryColor = when {
        telemetry.percentage <= 15 -> AlertRed
        telemetry.percentage <= 30 -> WarningAmber
        else -> SuccessGreen
    }

    val hoursRemaining = telemetry.estimatedRemainingMinutes / 60
    val minsRemaining = telemetry.estimatedRemainingMinutes % 60
    val timeFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBlack)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // TOP BAR
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
                    text = "Battery & Power Telemetry",
                    color = CyberTextBright,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (telemetry.isCharging) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(SuccessGreen.copy(alpha = 0.15f))
                        .border(1.dp, SuccessGreen, RoundedCornerShape(20.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("CHARGING", color = SuccessGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // BIG BATTERY LEVEL DIAL / DISPLAY
        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .background(CyberCard)
                .border(3.dp, batteryColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (telemetry.isCharging) {
                    Icon(
                        imageVector = Icons.Outlined.Bolt,
                        contentDescription = "Charging",
                        tint = SuccessGreen,
                        modifier = Modifier.size(32.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.BatteryChargingFull,
                        contentDescription = "Battery",
                        tint = batteryColor,
                        modifier = Modifier.size(30.dp)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "${telemetry.percentage}%",
                    color = CyberTextBright,
                    fontSize = 42.sp,
                    fontFamily = TechMono,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (telemetry.isCharging) telemetry.chargingSource else "$hoursRemaining hr $minsRemaining min left",
                    color = CyberTextDim,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // WARNING CARD IF LOW
        if (isLowBattery) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(AlertRed.copy(alpha = 0.15f))
                    .border(1.dp, AlertRed, RoundedCornerShape(10.dp))
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Warning, contentDescription = null, tint = AlertRed)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "CRITICAL BATTERY THRESHOLD",
                            color = AlertRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Child device is at ${telemetry.percentage}%. Automatic GPS fix dispatched.",
                            color = CyberTextDim,
                            fontSize = 11.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // TELEMETRY VITALS GRID
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Temperature
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(CyberCard)
                    .border(1.dp, CyberBorder, RoundedCornerShape(10.dp))
                    .padding(12.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Thermostat, contentDescription = null, tint = CyberTextDim, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("TEMP", color = CyberTextDim, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("${telemetry.temperatureCelsius}°C", color = CyberTextBright, fontSize = 15.sp, fontFamily = TechMono, fontWeight = FontWeight.Bold)
                    Text("Normal", color = SuccessGreen, fontSize = 10.sp)
                }
            }

            // Voltage
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(CyberCard)
                    .border(1.dp, CyberBorder, RoundedCornerShape(10.dp))
                    .padding(12.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Power, contentDescription = null, tint = CyberTextDim, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("VOLTAGE", color = CyberTextDim, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("${telemetry.voltageVolts} V", color = CyberTextBright, fontSize = 15.sp, fontFamily = TechMono, fontWeight = FontWeight.Bold)
                    Text(telemetry.healthStatus, color = SuccessGreen, fontSize = 10.sp)
                }
            }

            // Last Updated
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(CyberCard)
                    .border(1.dp, CyberBorder, RoundedCornerShape(10.dp))
                    .padding(12.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("LAST PING", color = CyberTextDim, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(timeFormat.format(Date(telemetry.lastUpdatedTimestamp)), color = White, fontSize = 14.sp, fontFamily = TechMono, fontWeight = FontWeight.Bold)
                    Text("Live Radio", color = CyberTextDim, fontSize = 10.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // POWER CONTROLS CARD
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CyberCard)
                .border(1.dp, CyberBorder, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Column {
                Text("POWER PREFERENCES", color = CyberTextDim, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Remote Power Saver", color = CyberTextBright, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Text("Dims screen & throttles non-essential background tasks", color = CyberTextDim, fontSize = 11.sp)
                    }
                    Switch(
                        checked = telemetry.powerSavingEnabled,
                        onCheckedChange = viewModel::togglePowerSaving,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = White,
                            checkedTrackColor = CyberBorder,
                            uncheckedThumbColor = CyberTextDim,
                            uncheckedTrackColor = CyberBlack
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Low Battery Alert Threshold: ${telemetry.lowBatteryAlertThreshold}%", color = CyberTextBright, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Slider(
                    value = telemetry.lowBatteryAlertThreshold.toFloat(),
                    onValueChange = { viewModel.setLowBatteryThreshold(it.toInt()) },
                    valueRange = 5f..30f,
                    steps = 4,
                    colors = SliderDefaults.colors(
                        thumbColor = White,
                        activeTrackColor = WarningAmber,
                        inactiveTrackColor = CyberBorder
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // SIMULATION TEST ACTIONS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = viewModel::simulateBatteryDrain,
                modifier = Modifier.weight(1f).height(44.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberTextBright)
            ) {
                Text("-5% Drain Ping", fontSize = 12.sp)
            }
            Button(
                onClick = viewModel::toggleCharging,
                modifier = Modifier.weight(1f).height(44.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = Black)
            ) {
                Text(if (telemetry.isCharging) "Unplug Charger" else "Plug Charger", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
