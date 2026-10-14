package com.nestcheck.app.ui.parent.security

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
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Pin
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
import com.nestcheck.app.data.model.LockTimeoutOption
import com.nestcheck.app.ui.theme.*

@Composable
fun ParentPinSecurityScreen(
    navController: NavController,
    viewModel: ParentSecurityViewModel = hiltViewModel()
) {
    val securityConfig by viewModel.securityConfig.collectAsState()
    val enteredPin by viewModel.enteredPin.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()
    val scrollState = rememberScrollState()

    var showChangePinDialog by remember { mutableStateOf(false) }
    var currentPinInput by remember { mutableStateOf("") }
    var newPinInput by remember { mutableStateOf("") }

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
                    text = "Parent PIN & Security",
                    color = CyberTextBright,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(onClick = viewModel::lockNow) {
                Icon(
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = "Lock Now",
                    tint = AlertRed
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // STATUS BADGE
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(if (securityConfig.isAppLocked) AlertRed.copy(alpha = 0.15f) else SuccessGreen.copy(alpha = 0.15f))
                .border(1.dp, if (securityConfig.isAppLocked) AlertRed else SuccessGreen, RoundedCornerShape(20.dp))
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Text(
                text = if (securityConfig.isAppLocked) "LOCKED • PIN REQUIRED" else "UNLOCKED • ACCESS GRANTED",
                color = if (securityConfig.isAppLocked) AlertRed else SuccessGreen,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 4 PIN DOTS
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(4) { index ->
                val isFilled = index < enteredPin.length
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(if (isFilled) White else CyberCard)
                        .border(2.dp, if (isFilled) White else CyberBorder, CircleShape)
                )
            }
        }

        if (statusMessage != null) {
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = statusMessage ?: "",
                color = if (statusMessage?.contains("Incorrect") == true || statusMessage?.contains("Failed") == true) AlertRed else SuccessGreen,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // NUMERIC KEYPAD (1-9, Clear, 0, Backspace)
        val keypadButtons = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf("C", "0", "⌫")
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.width(280.dp)
        ) {
            keypadButtons.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    row.forEach { key ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(CyberCard)
                                .border(1.dp, CyberBorder, RoundedCornerShape(12.dp))
                                .clickable {
                                    when (key) {
                                        "C" -> viewModel.onKeypadClear()
                                        "⌫" -> viewModel.onKeypadDelete()
                                        else -> viewModel.onKeypadDigit(key)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = key,
                                color = CyberTextBright,
                                fontSize = 20.sp,
                                fontFamily = TechMono,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // BIOMETRIC QUICK UNLOCK
        Button(
            onClick = { viewModel.simulateBiometricUnlock() },
            modifier = Modifier
                .width(280.dp)
                .height(46.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CyberCard, contentColor = CyberTextBright),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
        ) {
            Icon(Icons.Outlined.Fingerprint, contentDescription = "Biometrics", tint = CyberTextBright, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Unlock with Fingerprint", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(30.dp))

        // SECURITY SETTINGS CARD
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
                    text = "SECURITY CONTROLS",
                    color = CyberTextDim,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Biometrics Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Biometric Authentication", color = CyberTextBright, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Text("Use Face Unlock / Fingerprint", color = CyberTextDim, fontSize = 12.sp)
                    }
                    Switch(
                        checked = securityConfig.biometricEnabled,
                        onCheckedChange = viewModel::toggleBiometrics,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = White,
                            checkedTrackColor = CyberBorder,
                            uncheckedThumbColor = CyberTextDim,
                            uncheckedTrackColor = CyberBlack
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Change PIN Action
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showChangePinDialog = true }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Change Master PIN", color = CyberTextBright, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Text("Current PIN: **** (Default: 1234)", color = CyberTextDim, fontSize = 12.sp)
                    }
                    Icon(Icons.Outlined.Pin, contentDescription = "Change PIN", tint = White)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Auto-Lock Timeout Options
                Text("Auto-Lock Timeout", color = CyberTextBright, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    LockTimeoutOption.values().forEach { option ->
                        val isSelected = securityConfig.autoLockTimeoutMinutes == option.minutes
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) White else CyberBlack)
                                .border(1.dp, if (isSelected) White else CyberBorder, RoundedCornerShape(6.dp))
                                .clickable { viewModel.setLockTimeout(option) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = option.label,
                                color = if (isSelected) Black else CyberTextDim,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Change PIN Dialog
    if (showChangePinDialog) {
        AlertDialog(
            onDismissRequest = { showChangePinDialog = false },
            title = { Text("Update Parent PIN", color = CyberTextBright, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = currentPinInput,
                        onValueChange = { if (it.length <= 4) currentPinInput = it },
                        label = { Text("Current PIN") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = newPinInput,
                        onValueChange = { if (it.length <= 4) newPinInput = it },
                        label = { Text("New 4-Digit PIN") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val success = viewModel.updatePin(currentPinInput, newPinInput)
                        if (success) {
                            showChangePinDialog = false
                            currentPinInput = ""
                            newPinInput = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = Black)
                ) {
                    Text("Save PIN")
                }
            },
            dismissButton = {
                TextButton(onClick = { showChangePinDialog = false }) {
                    Text("Cancel", color = CyberTextDim)
                }
            },
            containerColor = CyberCard
        )
    }
}
