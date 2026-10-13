package com.nestcheck.app.ui.parent.screentime

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.nestcheck.app.data.model.DaySchedule
import com.nestcheck.app.ui.theme.*

@Composable
fun TimeLimitScreen(
    navController: NavController,
    viewModel: ScreenTimeViewModel = hiltViewModel()
) {
    val screenTimeData by viewModel.screenTimeData.collectAsState()
    val scrollState = rememberScrollState()

    var masterDailyLimit by remember { mutableFloatStateOf(screenTimeData.dailyLimit.toFloat()) }
    var saveConfirmed by remember { mutableStateOf(false) }

    // Per-day limits state
    var sundayLimit by remember { mutableFloatStateOf(120f) }
    var mondayLimit by remember { mutableFloatStateOf(90f) }
    var tuesdayLimit by remember { mutableFloatStateOf(90f) }
    var wednesdayLimit by remember { mutableFloatStateOf(90f) }
    var thursdayLimit by remember { mutableFloatStateOf(90f) }
    var fridayLimit by remember { mutableFloatStateOf(120f) }
    var saturdayLimit by remember { mutableFloatStateOf(150f) }

    val masterHours = (masterDailyLimit.toInt() / 60)
    val masterMins = (masterDailyLimit.toInt() % 60)
    val remainingMins = (masterDailyLimit.toInt() - screenTimeData.totalMinutes).coerceAtLeast(0)
    val isWarning = remainingMins in 1..14

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
                text = "Time Limits",
                color = CyberTextBright,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // MASTER DAILY LIMIT CARD
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CyberCard)
                .border(1.dp, CyberBorder, RoundedCornerShape(12.dp))
                .padding(18.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "MASTER DAILY LIMIT",
                        color = CyberTextDim,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isWarning) "WARNING: <15M LEFT" else "$remainingMins mins remaining",
                        color = if (isWarning) WarningAmber else SuccessGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "${masterHours}h ${masterMins}m",
                        color = CyberTextBright,
                        fontSize = 36.sp,
                        fontFamily = TechMono,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "allowed per day",
                        color = CyberTextDim,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Slider(
                    value = masterDailyLimit,
                    onValueChange = { masterDailyLimit = it },
                    valueRange = 30f..360f,
                    steps = 10,
                    colors = SliderDefaults.colors(
                        thumbColor = White,
                        activeTrackColor = White,
                        inactiveTrackColor = CyberBorder
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("30 mins", color = CyberTextDim, fontSize = 11.sp)
                    Text("6 hours", color = CyberTextDim, fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // PER-DAY SCHEDULE CONFIGURATION
        Text(
            text = "Day-of-Week Schedule",
            color = CyberTextBright,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(10.dp))

        val daysConfig = listOf(
            Triple("Sunday", sundayLimit) { v: Float -> sundayLimit = v },
            Triple("Monday", mondayLimit) { v: Float -> mondayLimit = v },
            Triple("Tuesday", tuesdayLimit) { v: Float -> tuesdayLimit = v },
            Triple("Wednesday", wednesdayLimit) { v: Float -> wednesdayLimit = v },
            Triple("Thursday", thursdayLimit) { v: Float -> thursdayLimit = v },
            Triple("Friday", fridayLimit) { v: Float -> fridayLimit = v },
            Triple("Saturday", saturdayLimit) { v: Float -> saturdayLimit = v }
        )

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            daysConfig.forEach { (dayName, value, onValueChange) ->
                val hours = value.toInt() / 60
                val mins = value.toInt() % 60
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberCard)
                        .border(1.dp, CyberBorder, RoundedCornerShape(8.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = dayName,
                                color = CyberTextBright,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${hours}h ${mins}m",
                                color = White,
                                fontSize = 13.sp,
                                fontFamily = TechMono,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Slider(
                            value = value,
                            onValueChange = onValueChange,
                            valueRange = 30f..300f,
                            steps = 8,
                            colors = SliderDefaults.colors(
                                thumbColor = White,
                                activeTrackColor = White,
                                inactiveTrackColor = CyberBorder
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // SAVE BUTTON
        Button(
            onClick = {
                viewModel.updateDailyLimit(masterDailyLimit.toInt())
                saveConfirmed = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = Black)
        ) {
            if (saveConfirmed) {
                Icon(Icons.Outlined.Check, contentDescription = "Saved", tint = Black)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Limits Saved", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            } else {
                Text("Save Time Limits", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
