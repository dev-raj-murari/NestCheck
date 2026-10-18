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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nestcheck.app.ui.parent.shell.*

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
            .background(GflBgLight)
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
                    tint = GflTextDark
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Time Limits",
                color = GflTextDark,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // MASTER DAILY LIMIT CARD
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = GflCardWhite),
            border = androidx.compose.foundation.BorderStroke(1.dp, GflBorder)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "MASTER DAILY LIMIT",
                        color = GflTextMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isWarning) "Warning: <15m left" else "$remainingMins mins remaining",
                        color = if (isWarning) Color(0xFFD97706) else GflGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "${masterHours}h ${masterMins}m",
                        color = GflBlue,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "allowed per day",
                        color = GflTextMuted,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Slider(
                    value = masterDailyLimit,
                    onValueChange = { masterDailyLimit = it },
                    valueRange = 30f..360f,
                    colors = SliderDefaults.colors(
                        thumbColor = GflBlue,
                        activeTrackColor = GflBlue,
                        inactiveTrackColor = Color(0xFFD3E3FD)
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("30 mins", color = GflTextMuted, fontSize = 12.sp)
                    Text("6 hours", color = GflTextMuted, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // PER-DAY SCHEDULE CONFIGURATION
        Text(
            text = "Day-of-Week Schedule",
            color = GflTextDark,
            fontSize = 16.sp,
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
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = GflCardWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GflBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = dayName,
                                color = GflTextDark,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${hours}h ${mins}m",
                                color = GflBlue,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Slider(
                            value = value,
                            onValueChange = onValueChange,
                            valueRange = 30f..300f,
                            colors = SliderDefaults.colors(
                                thumbColor = GflBlue,
                                activeTrackColor = GflBlue,
                                inactiveTrackColor = Color(0xFFD3E3FD)
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
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GflBlue, contentColor = Color.White)
        ) {
            if (saveConfirmed) {
                Icon(Icons.Outlined.Check, contentDescription = "Saved", tint = Color.White)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Limits Saved", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            } else {
                Text("Save Time Limits", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
