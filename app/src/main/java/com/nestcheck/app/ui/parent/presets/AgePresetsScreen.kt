package com.nestcheck.app.ui.parent.presets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
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
import com.nestcheck.app.data.model.AgeGroupPreset
import com.nestcheck.app.ui.theme.*

@Composable
fun AgePresetsScreen(
    navController: NavController,
    viewModel: AgePresetsViewModel = hiltViewModel()
) {
    var appliedPreset by remember { mutableStateOf<AgeGroupPreset?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBlack)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
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
                text = "Age-Based Presets",
                color = CyberTextBright,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Select a preset to automatically configure daily screen time, bedtime curfew, and app category rules.",
            color = CyberTextDim,
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(18.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            AgeGroupPreset.values().forEach { preset ->
                val isSelected = appliedPreset == preset

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(CyberCard)
                        .border(1.dp, if (isSelected) White else CyberBorder, RoundedCornerShape(10.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = preset.label, color = CyberTextBright, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Text(text = "Ages: ${preset.ageRange}", color = CyberTextDim, fontSize = 12.sp)
                            }

                            Button(
                                onClick = {
                                    viewModel.applyPreset(preset)
                                    appliedPreset = preset
                                },
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) SuccessGreen else White,
                                    contentColor = Black
                                )
                            ) {
                                if (isSelected) {
                                    Icon(Icons.Outlined.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Applied", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                } else {
                                    Text("Apply Preset", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = CyberBorder, thickness = 0.5.dp)
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Daily Limit", color = CyberTextDim, fontSize = 11.sp)
                                Text("${preset.dailyLimitMinutes / 60}h ${preset.dailyLimitMinutes % 60}m", color = CyberTextBright, fontSize = 13.sp, fontFamily = TechMono, fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Text("Bedtime", color = CyberTextDim, fontSize = 11.sp)
                                Text(preset.bedtime, color = CyberTextBright, fontSize = 13.sp, fontFamily = TechMono, fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Text("Gaming Cap", color = CyberTextDim, fontSize = 11.sp)
                                Text("${preset.gamingCapMinutes}m", color = CyberTextBright, fontSize = 13.sp, fontFamily = TechMono, fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Text("Social Media", color = CyberTextDim, fontSize = 11.sp)
                                Text(if (preset.socialAllowed) "Allowed" else "Blocked", color = if (preset.socialAllowed) SuccessGreen else AlertRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
