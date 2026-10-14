package com.nestcheck.app.ui.child.sos

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Warning
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
import com.nestcheck.app.ui.theme.*

@Composable
fun SOSScreen(
    navController: NavController,
    viewModel: SOSViewModel = hiltViewModel()
) {
    val sosState by viewModel.sosState.collectAsState()
    val countdown by viewModel.countdownSeconds.collectAsState()
    val isHolding by viewModel.isHolding.collectAsState()
    val scrollState = rememberScrollState()

    // Pulse animation for active emergency state
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

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
                    text = "Emergency SOS",
                    color = CyberTextBright,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (sosState.isEmergencyActive) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(AlertRed)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("LIVE ALERT", color = White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // STATUS BANNER
        if (sosState.isEmergencyActive) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(AlertRed.copy(alpha = 0.2f))
                    .border(1.dp, AlertRed, RoundedCornerShape(10.dp))
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Warning, contentDescription = "Active Alert", tint = AlertRed)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "EMERGENCY BROADCAST ACTIVE",
                            color = AlertRed,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "GPS telemetry & alert dispatched to parents",
                            color = CyberTextDim,
                            fontSize = 11.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // BIG CIRCULAR SOS BUTTON
        Box(
            modifier = Modifier
                .size(240.dp)
                .scale(if (sosState.isEmergencyActive) pulseScale else 1f)
                .clip(CircleShape)
                .background(if (sosState.isEmergencyActive) AlertRed else CyberCard)
                .border(
                    width = 4.dp,
                    color = if (sosState.isEmergencyActive) White else AlertRed,
                    shape = CircleShape
                )
                .clickable {
                    if (sosState.isEmergencyActive) {
                        viewModel.cancelActiveEmergency()
                    } else {
                        viewModel.triggerImmediateSOS()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (sosState.isEmergencyActive) "CANCEL" else "SOS",
                    color = White,
                    fontSize = if (sosState.isEmergencyActive) 28.sp else 46.sp,
                    fontFamily = TechMono,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (sosState.isEmergencyActive) "Tap to Resolve" else "TAP FOR IMMEDIATE ALERT",
                    color = if (sosState.isEmergencyActive) White.copy(alpha = 0.8f) else CyberTextDim,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // HOLD-TO-ACTIVATE SECTION
        if (!sosState.isEmergencyActive) {
            Button(
                onClick = viewModel::onStartHold,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AlertRed, contentColor = White)
            ) {
                Text(
                    text = if (isHolding) "ACTIVATING IN $countdown..." else "Press for 3s Countdown SOS",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
            if (isHolding) {
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = viewModel::onCancelHold) {
                    Text("Cancel Countdown", color = CyberTextDim, fontSize = 12.sp)
                }
            }
        } else {
            Button(
                onClick = viewModel::cancelActiveEmergency,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CyberCard, contentColor = CyberTextBright),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
            ) {
                Text("Deactivate SOS Broadcast", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // TELEMETRY ATTACHED CARD
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
                    text = "ATTACHED RESCUE TELEMETRY",
                    color = CyberTextDim,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.LocationOn, contentDescription = "Location", tint = AlertRed, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("GPS Coordinates", color = CyberTextBright, fontSize = 13.sp)
                    }
                    Text(
                        text = "${sosState.latitude}° N, ${sosState.longitude}° E",
                        color = White,
                        fontSize = 12.sp,
                        fontFamily = TechMono,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Device Battery", color = CyberTextBright, fontSize = 13.sp)
                    Text(
                        text = "${sosState.batteryPercent}% (High)",
                        color = SuccessGreen,
                        fontSize = 12.sp,
                        fontFamily = TechMono,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text("Emergency Contacts Notified:", color = CyberTextDim, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                sosState.notifiedContacts.forEach { contact ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.Call, contentDescription = "Call", tint = SuccessGreen, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(contact, color = CyberTextBright, fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
