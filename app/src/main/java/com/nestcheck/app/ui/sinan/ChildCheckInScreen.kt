package com.nestcheck.app.ui.sinan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nestcheck.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChildCheckInScreen(
    navController: NavController,
    viewModel: ChildCheckInViewModel = hiltViewModel()
) {
    val liveLoc by viewModel.liveLocation.collectAsState()
    val lastSent by viewModel.lastSentCheckIn.collectAsState()
    val isSubmitting by viewModel.isSubmitting.collectAsState()
    val scrollState = rememberScrollState()

    var selectedPreset by remember { mutableStateOf("Arrived at School") }
    var noteInput by remember { mutableStateOf("I made it safely!") }

    val presets = listOf(
        "Arrived at School" to Icons.Outlined.School,
        "Arrived Home" to Icons.Outlined.Home,
        "At the Library" to Icons.Outlined.MenuBook,
        "Sports Practice" to Icons.Outlined.SportsSoccer
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
                    text = "Safety Check-In",
                    color = CyberTextBright,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(SuccessGreen.copy(alpha = 0.2f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "GPS READY",
                    color = SuccessGreen,
                    fontSize = 10.sp,
                    fontFamily = TechMono,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // BIG HERO ONE-TAP BUTTON (F36)
        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .background(if (lastSent != null) SuccessGreen else Color(0xFFD3E3FD))
                .border(
                    width = 4.dp,
                    color = if (lastSent != null) SuccessGreen else Color(0xFF0B57D0),
                    shape = CircleShape
                )
                .clickable {
                    viewModel.sendCheckIn(selectedPreset, noteInput)
                },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = if (lastSent != null) Icons.Outlined.CheckCircle else Icons.Outlined.NearMe,
                    contentDescription = null,
                    tint = if (lastSent != null) Color.White else Color(0xFF0B57D0),
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isSubmitting) "SENDING..." else if (lastSent != null) "CHECKED IN!" else "I'M HERE!",
                    color = if (lastSent != null) Color.White else Color(0xFF0B57D0),
                    fontSize = 22.sp,
                    fontFamily = TechMono,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (lastSent != null) "Dispatched to Parent" else "TAP FOR 1-TAP DISPATCH",
                    color = if (lastSent != null) Color.White.copy(alpha = 0.9f) else Color(0xFF0842A0),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // CONFIRMATION BANNER
        if (lastSent != null) {
            val sent = lastSent
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(SuccessGreen.copy(alpha = 0.15f))
                    .border(1.dp, SuccessGreen, RoundedCornerShape(10.dp))
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Verified, contentDescription = null, tint = SuccessGreen)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "ARRIVAL CONFIRMED",
                            color = SuccessGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${sent?.locationLabel} • ${sent?.customNote}",
                            color = CyberTextBright,
                            fontSize = 12.sp
                        )
                    }
                    IconButton(onClick = viewModel::dismissConfirmation, modifier = Modifier.size(20.dp)) {
                        Icon(Icons.Outlined.Close, contentDescription = "Close", tint = CyberTextDim)
                    }
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
        }

        // QUICK PRESET SELECTION
        Text(
            text = "Select Quick Location Tag",
            color = CyberTextDim,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            presets.take(2).forEach { (preset, icon) ->
                val isSelected = selectedPreset == preset
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) White else CyberCard)
                        .border(1.dp, if (isSelected) White else CyberBorder, RoundedCornerShape(8.dp))
                        .clickable { selectedPreset = preset }
                        .padding(vertical = 12.dp, horizontal = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isSelected) Black else CyberTextBright,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = preset.substringAfter(" "),
                            color = if (isSelected) Black else CyberTextBright,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            presets.takeLast(2).forEach { (preset, icon) ->
                val isSelected = selectedPreset == preset
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) White else CyberCard)
                        .border(1.dp, if (isSelected) White else CyberBorder, RoundedCornerShape(8.dp))
                        .clickable { selectedPreset = preset }
                        .padding(vertical = 12.dp, horizontal = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isSelected) Black else CyberTextBright,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = preset.substringAfter(" "),
                            color = if (isSelected) Black else CyberTextBright,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // CUSTOM NOTE INPUT
        OutlinedTextField(
            value = noteInput,
            onValueChange = { noteInput = it },
            label = { Text("Message to Parents", color = CyberTextDim, fontSize = 12.sp) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = CyberCard,
                unfocusedContainerColor = CyberCard,
                focusedBorderColor = White,
                unfocusedBorderColor = CyberBorder,
                focusedTextColor = CyberTextBright,
                unfocusedTextColor = CyberTextBright
            ),
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ATTACHED TELEMETRY CARD
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
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Current Coordinates", color = CyberTextDim, fontSize = 11.sp)
                    Text("${liveLoc.latitude}° N, ${liveLoc.longitude}° E", color = CyberTextBright, fontSize = 11.sp, fontFamily = TechMono, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Detected Address", color = CyberTextDim, fontSize = 11.sp)
                    Text("MPSTME Campus", color = CyberTextBright, fontSize = 11.sp)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Battery Attached", color = CyberTextDim, fontSize = 11.sp)
                    Text("${liveLoc.batteryPercent}% (Normal)", color = SuccessGreen, fontSize = 11.sp, fontFamily = TechMono, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
