package com.nestcheck.app.ui.parent.screentime

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Tune
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
import com.nestcheck.app.data.model.AppCategory
import com.nestcheck.app.data.model.AppControlStatus
import com.nestcheck.app.navigation.Screen
import com.nestcheck.app.ui.theme.*

@Composable
fun ScreenTimeScreen(
    navController: NavController,
    viewModel: ScreenTimeViewModel = hiltViewModel()
) {
    val screenTimeData by viewModel.screenTimeData.collectAsState()
    val appUsageList by viewModel.appUsageList.collectAsState()
    val weeklyHistory by viewModel.weeklyHistory.collectAsState()
    val scrollState = rememberScrollState()

    var selectedRange by remember { mutableStateOf(TimeRangeFilter.TODAY) }

    val usedHours = screenTimeData.totalMinutes / 60
    val usedMins = screenTimeData.totalMinutes % 60
    val limitHours = screenTimeData.dailyLimit / 60
    val limitMins = screenTimeData.dailyLimit % 60
    val progress = (screenTimeData.totalMinutes.toFloat() / screenTimeData.dailyLimit.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
    val isOverLimit = screenTimeData.totalMinutes >= screenTimeData.dailyLimit

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
                Text(
                    text = "Screen Time",
                    color = CyberTextBright,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(onClick = { navController.navigate(Screen.TimeLimits.route) }) {
                Icon(
                    imageVector = Icons.Outlined.Tune,
                    contentDescription = "Edit Limits",
                    tint = CyberTextBright
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // TIME RANGE SELECTOR PILLS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TimeRangeFilter.values().forEach { filter ->
                val isSelected = filter == selectedRange
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) White else CyberCard)
                        .border(1.dp, if (isSelected) White else CyberBorder, RoundedCornerShape(6.dp))
                        .clickable { selectedRange = filter },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (filter) {
                            TimeRangeFilter.TODAY -> "Today"
                            TimeRangeFilter.WEEK -> "7 Days"
                            TimeRangeFilter.MONTH -> "30 Days"
                        },
                        color = if (isSelected) Black else CyberTextDim,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // HERO USAGE CARD
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
                        text = "TOTAL SCREEN TIME",
                        color = CyberTextDim,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isOverLimit) "LIMIT EXCEEDED" else "${screenTimeData.remainingMinutes}m remaining",
                        color = if (isOverLimit) AlertRed else SuccessGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "${usedHours}h ${usedMins}m",
                        color = CyberTextBright,
                        fontSize = 38.sp,
                        fontFamily = TechMono,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "/ ${limitHours}h ${limitMins}m limit",
                        color = CyberTextDim,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // PROGRESS BAR
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (isOverLimit) AlertRed else White,
                    trackColor = CyberBorder
                )

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = CyberBorder, thickness = 1.dp)
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Usage Ratio", color = CyberTextDim, fontSize = 11.sp)
                        Text("${(progress * 100).toInt()}%", color = CyberTextBright, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text("Daily Cap", color = CyberTextDim, fontSize = 11.sp)
                        Text("${screenTimeData.dailyLimit} mins", color = CyberTextBright, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text("Status", color = CyberTextDim, fontSize = 11.sp)
                        Text(if (isOverLimit) "Locked" else "Active", color = if (isOverLimit) AlertRed else SuccessGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // CATEGORY BREAKDOWN VISUALIZATION
        Text(
            text = "Category Breakdown",
            color = CyberTextBright,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CyberCard)
                .border(1.dp, CyberBorder, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                CategoryBreakdownRow(
                    title = "Gaming",
                    minutes = 45,
                    total = screenTimeData.totalMinutes,
                    barColor = AlertRed
                )
                CategoryBreakdownRow(
                    title = "Entertainment",
                    minutes = 30,
                    total = screenTimeData.totalMinutes,
                    barColor = WarningAmber
                )
                CategoryBreakdownRow(
                    title = "Education",
                    minutes = 20,
                    total = screenTimeData.totalMinutes,
                    barColor = SuccessGreen
                )
                CategoryBreakdownRow(
                    title = "Social",
                    minutes = 10,
                    total = screenTimeData.totalMinutes,
                    barColor = AccentBlue
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 7-DAY USAGE CHART (F1 / F2 Analytics)
        Text(
            text = "7-Day Usage Activity",
            color = CyberTextBright,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(10.dp))

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
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    weeklyHistory.forEach { point ->
                        val barHeightFactor = (point.minutes / 180f).coerceIn(0.1f, 1f)
                        val isToday = point.day == "Sun"
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "${point.minutes}m",
                                color = if (isToday) White else CyberTextDim,
                                fontSize = 9.sp,
                                fontFamily = TechMono
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .width(16.dp)
                                    .height((90 * barHeightFactor).dp)
                                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                    .background(if (isToday) White else CyberBorderBright)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = point.day,
                                color = if (isToday) White else CyberTextDim,
                                fontSize = 11.sp,
                                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // PER-APP USAGE LIST
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Per-App Usage",
                color = CyberTextBright,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${appUsageList.size} apps",
                color = CyberTextDim,
                fontSize = 12.sp
            )
        }
        Spacer(modifier = Modifier.height(10.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            appUsageList.forEach { app ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberCard)
                        .border(1.dp, CyberBorder, RoundedCornerShape(8.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = app.appName,
                                color = CyberTextBright,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(CyberBlack)
                                        .border(0.5.dp, CyberBorderBright, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = app.category.displayName,
                                        color = CyberTextDim,
                                        fontSize = 10.sp
                                    )
                                }
                                if (app.status == AppControlStatus.BLOCKED) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Blocked",
                                        color = AlertRed,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${app.minutesUsed} mins",
                                color = CyberTextBright,
                                fontSize = 14.sp,
                                fontFamily = TechMono,
                                fontWeight = FontWeight.Bold
                            )
                            if (app.dailyLimitMinutes > 0) {
                                Text(
                                    text = "Limit: ${app.dailyLimitMinutes}m",
                                    color = CyberTextDim,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // CONFIGURE LIMITS BUTTON
        Button(
            onClick = { navController.navigate(Screen.TimeLimits.route) },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = Black)
        ) {
            Text(
                text = "Configure Daily Limits",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun CategoryBreakdownRow(
    title: String,
    minutes: Int,
    total: Int,
    barColor: Color
) {
    val fraction = if (total > 0) (minutes.toFloat() / total.toFloat()).coerceIn(0f, 1f) else 0f
    val percentage = (fraction * 100).toInt()

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = title, color = CyberTextBright, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Text(text = "$minutes mins ($percentage%)", color = CyberTextDim, fontSize = 11.sp, fontFamily = TechMono)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { fraction },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = barColor,
            trackColor = CyberBorder
        )
    }
}
