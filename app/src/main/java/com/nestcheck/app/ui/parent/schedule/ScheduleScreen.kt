package com.nestcheck.app.ui.parent.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PlayArrow
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

@Composable
fun ScheduleScreen(
    navController: NavController,
    viewModel: ScheduleViewModel = hiltViewModel()
) {
    val bedtimeConfig by viewModel.bedtimeConfig.collectAsState()
    val pauseState by viewModel.pauseState.collectAsState()
    val scrollState = rememberScrollState()

    var weekdayStart by remember(bedtimeConfig.weekdayBedtimeStart) { mutableStateOf(bedtimeConfig.weekdayBedtimeStart) }
    var weekdayEnd by remember(bedtimeConfig.weekdayBedtimeEnd) { mutableStateOf(bedtimeConfig.weekdayBedtimeEnd) }
    var weekendStart by remember(bedtimeConfig.weekendBedtimeStart) { mutableStateOf(bedtimeConfig.weekendBedtimeStart) }
    var weekendEnd by remember(bedtimeConfig.weekendBedtimeEnd) { mutableStateOf(bedtimeConfig.weekendBedtimeEnd) }
    var isCurfewEnforced by remember(bedtimeConfig.isCurfewEnforced) { mutableStateOf(bedtimeConfig.isCurfewEnforced) }
    var saveFeedback by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBlack)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .verticalScroll(scrollState)
    ) {
        // TOP APP BAR
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back",
                    tint = CyberTextBright
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Bedtime & Curfew",
                color = CyberTextBright,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // F24: ONE-TAP DEVICE PAUSE HERO CARD
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CyberCard)
                .border(1.dp, if (pauseState.isPaused) AlertRed else CyberBorder, RoundedCornerShape(12.dp))
                .padding(18.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("ONE-TAP DEVICE PAUSE", color = CyberTextDim, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (pauseState.isPaused) "DEVICE IS PAUSED" else "Device Active",
                            color = if (pauseState.isPaused) AlertRed else SuccessGreen,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = {
                            if (pauseState.isPaused) viewModel.resumeDevice() else viewModel.pauseDevice("Dinnertime Lock")
                        },
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (pauseState.isPaused) SuccessGreen else AlertRed,
                            contentColor = Black
                        )
                    ) {
                        Text(
                            text = if (pauseState.isPaused) "Resume" else "Pause Now",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // F6: BEDTIME CURFEW CARD
        Text(
            text = "Curfew Schedules",
            color = CyberTextBright,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(10.dp))

        // WEEKDAY SCHEDULE
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(CyberCard)
                .border(1.dp, CyberBorder, RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            Column {
                Text("Weekdays (Sunday - Thursday)", color = CyberTextBright, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = weekdayStart,
                        onValueChange = { weekdayStart = it },
                        label = { Text("Bedtime (e.g. 21:00)") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = White,
                            unfocusedBorderColor = CyberBorder,
                            focusedTextColor = CyberTextBright,
                            unfocusedTextColor = CyberTextBright
                        )
                    )
                    OutlinedTextField(
                        value = weekdayEnd,
                        onValueChange = { weekdayEnd = it },
                        label = { Text("Wakeup (e.g. 06:30)") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = White,
                            unfocusedBorderColor = CyberBorder,
                            focusedTextColor = CyberTextBright,
                            unfocusedTextColor = CyberTextBright
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // WEEKEND SCHEDULE
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(CyberCard)
                .border(1.dp, CyberBorder, RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            Column {
                Text("Weekends (Friday & Saturday)", color = CyberTextBright, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = weekendStart,
                        onValueChange = { weekendStart = it },
                        label = { Text("Bedtime (e.g. 22:00)") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = White,
                            unfocusedBorderColor = CyberBorder,
                            focusedTextColor = CyberTextBright,
                            unfocusedTextColor = CyberTextBright
                        )
                    )
                    OutlinedTextField(
                        value = weekendEnd,
                        onValueChange = { weekendEnd = it },
                        label = { Text("Wakeup (e.g. 07:30)") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = White,
                            unfocusedBorderColor = CyberBorder,
                            focusedTextColor = CyberTextBright,
                            unfocusedTextColor = CyberTextBright
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // ENFORCEMENT TOGGLE
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(CyberCard)
                .border(1.dp, CyberBorder, RoundedCornerShape(8.dp))
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Enforce Lockout Mode", color = CyberTextBright, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text("Locks device at bedtime with emergency dialer whitelist", color = CyberTextDim, fontSize = 11.sp)
            }
            Switch(
                checked = isCurfewEnforced,
                onCheckedChange = { isCurfewEnforced = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Black,
                    checkedTrackColor = White,
                    uncheckedTrackColor = CyberBorder
                )
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // EMERGENCY WHITELIST
        Text(
            text = "Emergency Whitelist (Active During Lockout)",
            color = CyberTextBright,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            bedtimeConfig.emergencyWhitelist.forEach { item ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(CyberCard)
                        .border(1.dp, CyberBorder, RoundedCornerShape(4.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(text = item, color = CyberTextBright, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // SAVE BUTTON
        Button(
            onClick = {
                viewModel.updateSchedule(weekdayStart, weekdayEnd, weekendStart, weekendEnd, isCurfewEnforced)
                saveFeedback = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = Black)
        ) {
            if (saveFeedback) {
                Icon(Icons.Outlined.Check, contentDescription = null, tint = Black)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Schedule Saved", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            } else {
                Text("Save Bedtime Schedule", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
