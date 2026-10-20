package com.nestcheck.app.ui.parent.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.BatteryChargingFull
import androidx.compose.material.icons.outlined.Devices
import androidx.compose.material.icons.automirrored.outlined.DirectionsWalk
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material.icons.outlined.Timer
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
import com.nestcheck.app.navigation.Screen

private val CyberBg = Color(0xFFF8FAFC)
private val CyberCardBg = Color(0xFFFFFFFF)
private val CyberBorderColor = Color(0xFFE2E8F0)
private val NeonCyan = Color(0xFF0B57D0)
private val SafeGreen = Color(0xFF10B981)
private val AlertRed = Color(0xFFEF4444)
private val Amber = Color(0xFFF59E0B)
private val TextWhite = Color(0xFF0F172A)
private val TextMuted = Color(0xFF64748B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiChildDashboardScreen(
    navController: NavController,
    viewModel: MultiChildViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedChild = uiState.children.find { it.id == uiState.selectedChildId }
        ?: uiState.children.firstOrNull()

    Scaffold(
        containerColor = CyberBg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Household Children",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextWhite
                        )
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::openAddChildDialog) {
                        Icon(
                            imageVector = Icons.Outlined.Add,
                            contentDescription = "Add Child",
                            tint = NeonCyan
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CyberBg)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Household Overview Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CyberCardBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorderColor)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "My Family",
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "${uiState.children.size} Children Monitored • All Systems Online",
                            color = SafeGreen,
                            fontSize = 12.sp
                        )
                    }
                    Button(
                        onClick = viewModel::openAddChildDialog,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("+ Add Child", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Horizontal Selector for Children
            Text(
                text = "SELECT CHILD PROFILE",
                color = TextMuted,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                uiState.children.forEach { child ->
                    val isSelected = child.id == uiState.selectedChildId
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.selectChild(child.id) },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = if (isSelected) CyberCardBg else CyberBg),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) NeonCyan else CyberBorderColor
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color(child.avatarColorHex).copy(alpha = 0.2f))
                                    .border(1.dp, Color(child.avatarColorHex), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = child.name.take(1),
                                    color = Color(child.avatarColorHex),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = child.name.substringBefore(" "),
                                color = if (isSelected) TextWhite else TextMuted,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )

                            Text(
                                text = "Age ${child.age}",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // Detailed Selected Child Card
            selectedChild?.let { child ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CyberCardBg),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorderColor)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = child.name,
                                    color = TextWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                                Text(
                                    text = "${child.grade} • ${child.deviceName}",
                                    color = TextMuted,
                                    fontSize = 12.sp
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (child.isOnline) SafeGreen.copy(alpha = 0.15f) else Amber.copy(alpha = 0.15f))
                                    .border(1.dp, if (child.isOnline) SafeGreen else Amber, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (child.isOnline) "ONLINE" else "STANDBY",
                                    color = if (child.isOnline) SafeGreen else Amber,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Screen Time Progress
                        Text(
                            text = "Today's Screen Time: ${child.screenTimeMinutes}m / ${child.screenTimeLimitMinutes}m",
                            color = TextWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        val progress = (child.screenTimeMinutes.toFloat() / child.screenTimeLimitMinutes.toFloat()).coerceIn(0f, 1f)
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = if (progress > 0.85f) AlertRed else NeonCyan,
                            trackColor = CyberBg
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Metric Pills Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Battery
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CyberBg)
                                    .border(1.dp, CyberBorderColor, RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Outlined.BatteryChargingFull, contentDescription = null, tint = SafeGreen, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "${child.batteryPct}%", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            // Steps
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CyberBg)
                                    .border(1.dp, CyberBorderColor, RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.AutoMirrored.Outlined.DirectionsWalk, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "${child.stepsToday}", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            // Pairing Code
                            Box(
                                modifier = Modifier
                                    .weight(1.2f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CyberBg)
                                    .border(1.dp, CyberBorderColor, RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Outlined.QrCode, contentDescription = null, tint = Amber, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = child.pairingCode, color = Amber, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Location Info
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Outlined.LocationOn, contentDescription = null, tint = SafeGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = child.locationSummary, color = TextMuted, fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { navController.navigate(Screen.RemoteLock.route) },
                                modifier = Modifier.weight(1f).height(40.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AlertRed, contentColor = TextWhite)
                            ) {
                                Icon(imageVector = Icons.Outlined.Lock, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Remote Lock", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { navController.navigate(Screen.ChildProfile.route) },
                                modifier = Modifier.weight(1f).height(40.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CyberBg, contentColor = NeonCyan)
                            ) {
                                Icon(imageVector = Icons.Outlined.Person, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Profile Rules", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Toast Message Notification
            uiState.toastMessage?.let { msg ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = CyberCardBg),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SafeGreen)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = msg, color = SafeGreen, fontSize = 12.sp, modifier = Modifier.weight(1f))
                        Text(
                            text = "OK",
                            color = TextWhite,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { viewModel.clearToast() }
                                .padding(4.dp)
                        )
                    }
                }
            }
        }

        // Add Child Dialog
        if (uiState.showAddChildDialog) {
            AlertDialog(
                onDismissRequest = viewModel::closeAddChildDialog,
                containerColor = CyberCardBg,
                title = {
                    Text(
                        text = "Add Child to Household",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = uiState.newChildName,
                            onValueChange = viewModel::setNewChildName,
                            label = { Text("Child Full Name", color = TextMuted) },
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(color = TextWhite),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = CyberBorderColor
                            )
                        )
                        OutlinedTextField(
                            value = uiState.newChildAge,
                            onValueChange = viewModel::setNewChildAge,
                            label = { Text("Age", color = TextMuted) },
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(color = TextWhite),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = CyberBorderColor
                            )
                        )
                        OutlinedTextField(
                            value = uiState.newChildGrade,
                            onValueChange = viewModel::setNewChildGrade,
                            label = { Text("Grade / School Class", color = TextMuted) },
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(color = TextWhite),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = CyberBorderColor
                            )
                        )
                        OutlinedTextField(
                            value = uiState.newChildDevice,
                            onValueChange = viewModel::setNewChildDevice,
                            label = { Text("Device Model (e.g. Pixel Tablet)", color = TextMuted) },
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(color = TextWhite),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = CyberBorderColor
                            )
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = viewModel::submitNewChild,
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black)
                    ) {
                        Text("Add & Generate Code", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = viewModel::closeAddChildDialog) {
                        Text("Cancel", color = TextMuted)
                    }
                }
            )
        }
    }
}
