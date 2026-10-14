package com.nestcheck.app.ui.child.steps

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.DirectionsWalk
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Timer
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
fun StepCounterScreen(
    navController: NavController,
    viewModel: StepCounterViewModel = hiltViewModel()
) {
    val stepGoal by viewModel.stepGoal.collectAsState()
    val weeklySteps by viewModel.weeklySteps.collectAsState()
    val scrollState = rememberScrollState()

    var goalSliderValue by remember { mutableFloatStateOf(stepGoal.targetGoal.toFloat()) }
    val progress = (stepGoal.currentSteps.toFloat() / stepGoal.targetGoal.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
    val percentage = (progress * 100).toInt()

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
                    text = "Activity & Step Goal",
                    color = CyberTextBright,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(SuccessGreen.copy(alpha = 0.15f))
                    .border(1.dp, SuccessGreen, RoundedCornerShape(20.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "HARDWARE SENSOR ON",
                    color = SuccessGreen,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // CIRCULAR GOAL RING
        Box(
            modifier = Modifier
                .size(220.dp)
                .clip(CircleShape)
                .background(CyberCard)
                .border(2.dp, CyberBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.size(200.dp),
                color = SuccessGreen,
                strokeWidth = 12.dp,
                trackColor = CyberBlack
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.DirectionsWalk,
                    contentDescription = "Steps",
                    tint = SuccessGreen,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "${stepGoal.currentSteps}",
                    color = CyberTextBright,
                    fontSize = 34.sp,
                    fontFamily = TechMono,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Goal: ${stepGoal.targetGoal}",
                    color = CyberTextDim,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$percentage% COMPLETED",
                    color = SuccessGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // 3 METRIC TILES: Distance, Calories, Active Time
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Distance
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(CyberCard)
                    .border(1.dp, CyberBorder, RoundedCornerShape(10.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("DISTANCE", color = CyberTextDim, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("${stepGoal.distanceKm} km", color = CyberTextBright, fontSize = 16.sp, fontFamily = TechMono, fontWeight = FontWeight.Bold)
                }
            }

            // Calories
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(CyberCard)
                    .border(1.dp, CyberBorder, RoundedCornerShape(10.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("CALORIES", color = CyberTextDim, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("${stepGoal.caloriesBurnt} kcal", color = WarningAmber, fontSize = 16.sp, fontFamily = TechMono, fontWeight = FontWeight.Bold)
                }
            }

            // Active Time
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(CyberCard)
                    .border(1.dp, CyberBorder, RoundedCornerShape(10.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("ACTIVE", color = CyberTextDim, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("${stepGoal.activeMinutes} mins", color = White, fontSize = 16.sp, fontFamily = TechMono, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 7-DAY BAR CHART
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CyberCard)
                .border(1.dp, CyberBorder, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Column {
                Text("WEEKLY STEP HISTORY", color = CyberTextDim, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    weeklySteps.forEach { day ->
                        val barHeightFrac = (day.steps.toFloat() / 10000f).coerceIn(0.1f, 1f)
                        val reachedGoal = day.steps >= day.targetGoal

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${day.steps / 1000}k",
                                color = if (reachedGoal) SuccessGreen else CyberTextDim,
                                fontSize = 10.sp,
                                fontFamily = TechMono
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .width(18.dp)
                                    .fillMaxHeight(barHeightFrac)
                                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                    .background(if (reachedGoal) SuccessGreen else CyberBorder)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(day.dayName, color = CyberTextDim, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // GOAL ADJUSTMENT CARD
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CyberCard)
                .border(1.dp, CyberBorder, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Daily Target Goal", color = CyberTextBright, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("${goalSliderValue.toInt()} steps", color = SuccessGreen, fontSize = 14.sp, fontFamily = TechMono, fontWeight = FontWeight.Bold)
                }

                Slider(
                    value = goalSliderValue,
                    onValueChange = { goalSliderValue = it },
                    valueRange = 3000f..15000f,
                    steps = 11,
                    colors = SliderDefaults.colors(
                        thumbColor = White,
                        activeTrackColor = SuccessGreen,
                        inactiveTrackColor = CyberBorder
                    )
                )

                Button(
                    onClick = { viewModel.updateGoal(goalSliderValue.toInt()) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = Black)
                ) {
                    Text("Set Target Goal", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // SIMULATE WALK BUTTON
        OutlinedButton(
            onClick = { viewModel.addWalkSteps(500) },
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberTextBright)
        ) {
            Icon(Icons.AutoMirrored.Outlined.DirectionsWalk, contentDescription = "Walk", modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Simulate +500 Steps", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
