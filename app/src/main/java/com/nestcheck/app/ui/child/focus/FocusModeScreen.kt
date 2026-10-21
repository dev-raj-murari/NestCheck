package com.nestcheck.app.ui.child.focus

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.FastForward
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nestcheck.app.ui.theme.*

@Composable
fun FocusModeScreen(
    navController: NavController,
    viewModel: FocusModeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var selectedPreset by remember { mutableIntStateOf(25) }

    val presets = listOf(
        Triple(25, "Pomodoro Study", 5),
        Triple(45, "Deep Work", 10),
        Triple(60, "Exam Preparation", 15)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBlack)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
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
                text = "Focus Mode",
                color = CyberTextBright,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // HERO TIMER DISPLAY
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CyberCard)
                .border(
                    1.dp,
                    if (uiState.isCompleted) SuccessGreen
                    else if (uiState.isPaused) WarningAmber
                    else if (uiState.isActive) White
                    else CyberBorder,
                    RoundedCornerShape(16.dp)
                )
                .padding(28.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = when {
                        uiState.isCompleted && !uiState.isClaimed -> "SESSION FINISHED • READY TO CLAIM!"
                        uiState.isClaimed -> "REWARD CLAIMED! GREAT JOB!"
                        uiState.isActive && uiState.isPaused -> "SESSION PAUSED"
                        uiState.isActive -> "SESSION IN PROGRESS (LIVE COUNTDOWN)"
                        else -> "READY TO FOCUS"
                    },
                    color = when {
                        uiState.isCompleted -> SuccessGreen
                        uiState.isPaused -> WarningAmber
                        uiState.isActive -> CyberBlue
                        else -> CyberTextDim
                    },
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(14.dp))

                // LIVE COUNTDOWN TIMER DISPLAY
                Text(
                    text = if (uiState.isActive) uiState.formattedTime else String.format("%02d:00", selectedPreset),
                    color = CyberTextBright,
                    fontSize = 54.sp,
                    fontFamily = TechMono,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = when {
                        uiState.isCompleted -> "All apps unlocked. Tap below to claim your credits!"
                        uiState.isActive && uiState.isPaused -> "Countdown paused at ${uiState.formattedTime}. Tap Resume to continue."
                        uiState.isActive -> "Distraction-free environment. Non-whitelisted apps silenced."
                        else -> "Select study duration & tap start"
                    },
                    color = CyberTextDim,
                    fontSize = 12.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                if (uiState.isActive) {
                    Spacer(modifier = Modifier.height(20.dp))

                    if (!uiState.isCompleted) {
                        // CONTROLS DURING RUNNING / PAUSED SESSION
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (!uiState.isPaused) {
                                // PAUSE BUTTON
                                Button(
                                    onClick = { viewModel.pauseSession() },
                                    colors = ButtonDefaults.buttonColors(containerColor = WarningAmber, contentColor = Black),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Outlined.Pause, contentDescription = "Pause", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Pause", fontWeight = FontWeight.Bold)
                                }
                            } else {
                                // RESUME BUTTON
                                Button(
                                    onClick = { viewModel.resumeSession() },
                                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen, contentColor = Black),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Outlined.PlayArrow, contentDescription = "Resume", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Resume", fontWeight = FontWeight.Bold)
                                }
                            }

                            // QUIT / CANCEL BUTTON
                            OutlinedButton(
                                onClick = { viewModel.stopSession() },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = AlertRed),
                                border = androidx.compose.foundation.BorderStroke(1.dp, AlertRed.copy(alpha = 0.6f)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Outlined.Stop, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Quit", fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Fast-forward demo button to test zero-timer claim
                        TextButton(
                            onClick = { viewModel.fastForwardToFinish() },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Outlined.FastForward, contentDescription = null, tint = CyberTextDim, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Fast-Forward Timer to 00:00 (Demo Test)", color = CyberTextDim, fontSize = 11.sp)
                        }

                        // Disabled claim credits indicator
                        Text(
                            text = "🔒 Credits (+${uiState.rewardCredits} pts) locked until timer reaches 00:00",
                            color = CyberTextDim,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    } else if (!uiState.isClaimed) {
                        // COMPLETED SESSION: CLAIM CREDITS BUTTON (ENABLED ONLY AT 00:00)
                        Button(
                            onClick = {
                                val success = viewModel.claimCredits()
                                if (success) {
                                    Toast.makeText(context, "🌟 +${uiState.rewardCredits} Credits Claimed Successfully!", Toast.LENGTH_LONG).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen, contentColor = Black),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Icon(Icons.Outlined.CheckCircle, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Claim Reward (+${uiState.rewardCredits} Credits)", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                        }
                    } else {
                        Text(
                            text = "✓ Reward of +${uiState.rewardCredits} Credits has been added to your bank!",
                            color = SuccessGreen,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        if (!uiState.isActive) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Choose Session Duration",
                color = CyberTextBright,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                presets.forEach { (mins, label, credits) ->
                    val isSelected = selectedPreset == mins
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) CyberCard else CyberBlack)
                            .border(1.dp, if (isSelected) White else CyberBorder, RoundedCornerShape(8.dp))
                            .clickable { selectedPreset = mins }
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "$mins Minutes", color = CyberTextBright, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                Text(text = label, color = CyberTextDim, fontSize = 12.sp)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(CyberBlack)
                                    .border(0.5.dp, CyberBorderBright, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(text = "+$credits credits", color = White, fontSize = 11.sp, fontFamily = TechMono)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = {
                    val p = presets.find { it.first == selectedPreset } ?: presets[0]
                    viewModel.startSession(p.first, p.second, p.third)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = Black)
            ) {
                Icon(Icons.Outlined.PlayArrow, contentDescription = null, tint = Black)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Start Focus Session", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
