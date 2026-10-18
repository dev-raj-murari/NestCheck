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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nestcheck.app.data.model.EmergencyCategory
import com.nestcheck.app.data.model.EmergencyDispatchRecord
import com.nestcheck.app.data.model.FastDialEmergencyContact
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyHubScreen(
    navController: NavController,
    viewModel: EmergencyHubViewModel = hiltViewModel()
) {
    val contacts by viewModel.contacts.collectAsState()
    val dispatchLogs by viewModel.dispatchLogs.collectAsState()
    val config by viewModel.config.collectAsState()
    val countdownContact by viewModel.countdownContact
    val secondsLeft by viewModel.countdownSeconds
    val lastDispatched by viewModel.lastDispatched

    val bgColor = Color(0xFFF8FAFC)
    val cardColor = Color(0xFFFFFFFF)
    val cyanAccent = Color(0xFF0B57D0)
    val redEmergency = Color(0xFFEF4444)
    val greenSuccess = Color(0xFF10B981)
    val amberWarning = Color(0xFFF59E0B)
    val textMuted = Color(0xFF64748B)
    val borderStroke = Color(0xFFE2E8F0)
    val textDark = Color(0xFF0F172A)

    val caregivers = contacts.filter { it.category == EmergencyCategory.FAMILY }
    val nationalServices = contacts.filter { it.category != EmergencyCategory.FAMILY }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Emergency Hub",
                            fontWeight = FontWeight.Bold,
                            color = textDark,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "1-Tap Fast-Dial & SOS Dispatcher",
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
                        color = redEmergency.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Text(
                            text = "F41 ACTIVE",
                            color = redEmergency,
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Emergency SOS Master Broadcast Button Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = cardColor),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(redEmergency.copy(alpha = 0.4f)))
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(76.dp)
                                    .clip(CircleShape)
                                    .background(redEmergency)
                                    .clickable { viewModel.broadcastSosAll() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Sos,
                                    contentDescription = "SOS",
                                    tint = Color.White,
                                    modifier = Modifier.size(46.dp)
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "TAP FOR IMMEDIATE EMERGENCY SOS",
                                    fontWeight = FontWeight.Black,
                                    color = textDark,
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "Broadcasts critical distress alert + high-power GPS fix to Mom, Dad & 112 simultaneously",
                                    color = textMuted,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // Caregivers & Primary Guardians Fast-Dial
                item {
                    Text(
                        text = "Primary Caregivers & Guardians",
                        fontWeight = FontWeight.Bold,
                        color = textDark,
                        fontSize = 14.sp
                    )
                }

                items(caregivers) { contact ->
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
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(cyanAccent.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = cyanAccent,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Column {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = contact.name,
                                            fontWeight = FontWeight.Bold,
                                            color = textDark,
                                            fontSize = 14.sp
                                        )
                                        if (contact.isPrimary) {
                                            Surface(
                                                color = cyanAccent.copy(alpha = 0.2f),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = "PRIMARY",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = cyanAccent,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }
                                    Text(
                                        text = "${contact.roleTitle} • ${contact.phoneNumber}",
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.SansSerif,
                                        color = textMuted
                                    )
                                }
                            }

                            Button(
                                onClick = { viewModel.initiateCall(contact) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = greenSuccess),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Call",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }

                // National 24x7 Services Section
                item {
                    Text(
                        text = "National Emergency Helplines (Toll-Free 24x7)",
                        fontWeight = FontWeight.Bold,
                        color = textDark,
                        fontSize = 14.sp
                    )
                }

                items(nationalServices) { contact ->
                    val badgeColor = when (contact.category) {
                        EmergencyCategory.NATIONAL_EMERGENCY -> redEmergency
                        EmergencyCategory.MEDICAL -> greenSuccess
                        EmergencyCategory.SAFETY_PATROL -> amberWarning
                        EmergencyCategory.FAMILY -> cyanAccent
                    }

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
                                    Text(
                                        text = contact.name,
                                        fontWeight = FontWeight.Bold,
                                        color = textDark,
                                        fontSize = 14.sp
                                    )
                                    Surface(
                                        color = badgeColor.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = contact.category.displayName,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = badgeColor,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "${contact.roleTitle} • Dial: ${contact.phoneNumber}",
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.SansSerif,
                                    color = textMuted
                                )
                            }

                            Button(
                                onClick = { viewModel.initiateCall(contact) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = badgeColor),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = contact.phoneNumber,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }

                // Safety Options & Anti-Accidental Dial Controls
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
                                text = "Emergency Dial Protection Settings",
                                fontWeight = FontWeight.Bold,
                                color = textDark,
                                fontSize = 14.sp
                            )

                            // Countdown toggle
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "3-Second Accidental Dial Safeguard",
                                        color = textDark,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "Shows a cancelable timer before initiating emergency call",
                                        color = textMuted,
                                        fontSize = 11.sp
                                    )
                                }
                                Switch(
                                    checked = config.isCountdownEnabled,
                                    onCheckedChange = { viewModel.toggleCountdown(it) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = cyanAccent,
                                        uncheckedThumbColor = textMuted,
                                        uncheckedTrackColor = Color(0xFFCBD5E1)
                                    )
                                )
                            }

                            HorizontalDivider(color = borderStroke)

                            // Auto GPS SMS toggle
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Auto-Send GPS Coordinates with Call",
                                        color = textDark,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "Transmits live map link to parent phones during emergency",
                                        color = textMuted,
                                        fontSize = 11.sp
                                    )
                                }
                                Switch(
                                    checked = config.autoSendLocationSmsOnCall,
                                    onCheckedChange = { viewModel.toggleAutoLocation(it) },
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

                // Recent Dispatch Log
                item {
                    Text(
                        text = "Emergency Dispatch Audit Trail",
                        fontWeight = FontWeight.Bold,
                        color = textDark,
                        fontSize = 14.sp
                    )
                }

                items(dispatchLogs) { log: EmergencyDispatchRecord ->
                    val timeStr = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date(log.timestamp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = cardColor),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(borderStroke))
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = greenSuccess,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = log.contactName,
                                        fontWeight = FontWeight.Bold,
                                        color = textDark,
                                        fontSize = 13.sp
                                    )
                                }
                                Text(
                                    text = timeStr,
                                    fontSize = 11.sp,
                                    color = textMuted
                                )
                            }
                            Text(
                                text = log.status,
                                fontSize = 11.sp,
                                color = cyanAccent
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }

            // Countdown Overlay Dialog
            countdownContact?.let { contact ->
                Surface(
                    color = Color.Black.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(redEmergency))
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    text = "CALLING EMERGENCY CONTACT",
                                    fontWeight = FontWeight.Black,
                                    color = redEmergency,
                                    fontSize = 13.sp
                                )

                                Text(
                                    text = contact.name,
                                    fontWeight = FontWeight.Bold,
                                    color = textDark,
                                    fontSize = 18.sp
                                )

                                Text(
                                    text = "$secondsLeft",
                                    fontWeight = FontWeight.Black,
                                    color = redEmergency,
                                    fontSize = 54.sp
                                )

                                Text(
                                    text = "Dispatching in $secondsLeft seconds...\nTap Cancel if pressed by mistake.",
                                    color = textMuted,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { viewModel.cancelCountdown() },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = textDark),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1))
                                    ) {
                                        Text(text = "CANCEL", fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = {
                                            viewModel.cancelCountdown()
                                            viewModel.executeDial(contact)
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = greenSuccess)
                                    ) {
                                        Text(text = "CALL NOW", fontWeight = FontWeight.Bold, color = Color.Black)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
