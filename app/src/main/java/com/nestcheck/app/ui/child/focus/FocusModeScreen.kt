package com.nestcheck.app.ui.child.focus

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Stop
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
fun FocusModeScreen(
    navController: NavController,
    viewModel: FocusModeViewModel = hiltViewModel()
) {
    val session by viewModel.focusSession.collectAsState()
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
                .border(1.dp, if (session.isActive) White else CyberBorder, RoundedCornerShape(16.dp))
                .padding(28.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (session.isActive) "SESSION IN PROGRESS" else "READY TO FOCUS",
                    color = if (session.isActive) SuccessGreen else CyberTextDim,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = if (session.isActive) "${session.targetMinutes}:00" else "${selectedPreset}:00",
                    color = CyberTextBright,
                    fontSize = 56.sp,
                    fontFamily = TechMono,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (session.isActive) "Non-whitelisted apps silenced" else "Select duration & tap start",
                    color = CyberTextDim,
                    fontSize = 12.sp
                )

                if (session.isActive) {
                    Spacer(modifier = Modifier.height(18.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = { viewModel.completeSession() },
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen, contentColor = Black),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Icon(Icons.Outlined.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Complete (+${session.rewardCredits} pts)", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { viewModel.stopSession() },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = AlertRed),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Icon(Icons.Outlined.Stop, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Quit", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        if (!session.isActive) {
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
