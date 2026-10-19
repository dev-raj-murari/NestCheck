package com.nestcheck.app.ui.child.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nestcheck.app.data.model.CalendarEventItem

@Composable
fun ChildCalendarCard(
    events: List<CalendarEventItem>,
    modifier: Modifier = Modifier
) {
    val cardBg = Color(0xFF121824)
    val cyanAccent = Color(0xFF00E5FF)
    val borderStroke = Color(0xFF1E293B)
    val textMuted = Color(0xFF94A3B8)

    var selectedDay by remember { mutableIntStateOf(20) }

    val daysOfWeek = listOf(
        Pair("Mon", 20),
        Pair("Tue", 21),
        Pair("Wed", 22),
        Pair("Thu", 23),
        Pair("Fri", 24),
        Pair("Sat", 25),
        Pair("Sun", 26)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(cardBg)
            .border(1.dp, borderStroke, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            // Header
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
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = cyanAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Weekly Schedule & Exams Calendar",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "October 2026",
                    color = textMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Days of Week Strip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                daysOfWeek.forEach { (dayName, dayNum) ->
                    val isSelected = selectedDay == dayNum
                    val hasExam = dayNum == 24 // Friday Math exam

                    Surface(
                        color = if (isSelected) cyanAccent else Color(0xFF1E293B),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 2.dp)
                            .clickable { selectedDay = dayNum }
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = dayName,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.Black else textMuted
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "$dayNum",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isSelected) Color.Black else Color.White
                            )
                            if (hasExam) {
                                Box(
                                    modifier = Modifier
                                        .padding(top = 2.dp)
                                        .size(4.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(if (isSelected) Color.Black else Color(0xFFFF5252))
                                )
                            }
                        }
                    }
                }
            }

            // Events List for Week
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                events.forEach { ev ->
                    Surface(
                        color = Color(0xFF1E293B),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    color = Color(ev.eventType.badgeColorHex).copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = ev.eventType.label,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(ev.eventType.badgeColorHex),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Text(
                                    text = ev.title,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = textMuted,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = ev.timeRange,
                                    fontSize = 11.sp,
                                    color = textMuted
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
