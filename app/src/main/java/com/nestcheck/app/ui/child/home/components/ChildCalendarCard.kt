package com.nestcheck.app.ui.child.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.School
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
import com.nestcheck.app.data.sync.TimetableItem

@Composable
fun ChildCalendarCard(
    timetableItems: List<TimetableItem> = emptyList(),
    isCalendarSynced: Boolean = false,
    timetablePdfName: String = "",
    events: List<CalendarEventItem> = emptyList(),
    modifier: Modifier = Modifier
) {
    val cardBg = Color(0xFFFFFFFF)
    val borderStroke = Color(0xFFE2E8F0)
    val textDark = Color(0xFF1E293B)
    val textMuted = Color(0xFF64748B)
    val primaryBlue = Color(0xFF0B57D0)

    val daysOfWeek = listOf(
        Pair("Mon", 20),
        Pair("Tue", 21),
        Pair("Wed", 22),
        Pair("Thu", 23),
        Pair("Fri", 24),
        Pair("Sat", 25),
        Pair("Sun", 26)
    )

    var selectedDayNum by remember { mutableIntStateOf(20) }
    val currentDayPair = daysOfWeek.find { it.second == selectedDayNum } ?: daysOfWeek.first()
    val currentDayPrefix = currentDayPair.first

    // Filter strictly by the selected day of the week
    val classesForSelectedDay = timetableItems.filter {
        it.dayOfWeek.startsWith(currentDayPrefix, ignoreCase = true)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(cardBg)
            .border(1.dp, borderStroke, RoundedCornerShape(20.dp))
            .padding(18.dp)
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
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE8F0FE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = primaryBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Weekly School Schedule",
                            color = textDark,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isCalendarSynced) "Google Calendar Synced" else if (timetablePdfName.isNotBlank()) "Synced from $timetablePdfName" else "Parent Timetable Sync",
                            color = textMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                if (isCalendarSynced) {
                    Surface(
                        color = Color(0xFFE8F0FE),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CheckCircle,
                                contentDescription = null,
                                tint = primaryBlue,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "Synced",
                                color = primaryBlue,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Days of Week Strip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                daysOfWeek.forEach { (dayName, dayNum) ->
                    val isSelected = selectedDayNum == dayNum
                    val isWeekend = dayName == "Sat" || dayName == "Sun"

                    Surface(
                        color = if (isSelected) primaryBlue else Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(12.dp),
                        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 2.dp)
                            .clickable { selectedDayNum = dayNum }
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = dayName,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White.copy(alpha = 0.85f) else textMuted
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "$dayNum",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isSelected) Color.White else textDark
                            )
                            if (dayNum == 24) { // Friday review & quiz
                                Box(
                                    modifier = Modifier
                                        .padding(top = 4.dp)
                                        .size(4.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) Color.White else Color(0xFFD97706))
                                )
                            }
                        }
                    }
                }
            }

            // Events/Classes for the selected day only
            if (currentDayPrefix == "Sat" || currentDayPrefix == "Sun") {
                // Weekend State
                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Weekend • No Classes Scheduled",
                            color = textDark,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Free recreation & family time",
                            color = textMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            } else if (classesForSelectedDay.isEmpty()) {
                // Empty weekday state (no fake data)
                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "No Classes Scheduled for $currentDayPrefix",
                            color = textDark,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Classes will appear when parent uploads timetable or syncs Google Calendar.",
                            color = textMuted,
                            fontSize = 11.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                // Render real classes uploaded by parent
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    classesForSelectedDay.forEach { item ->
                        Surface(
                            color = Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFFE8F0FE)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.School,
                                            contentDescription = null,
                                            tint = primaryBlue,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }

                                    Column {
                                        Text(
                                            text = item.subject,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = textDark
                                        )
                                        Text(
                                            text = item.roomOrTeacher,
                                            fontSize = 11.sp,
                                            color = textMuted
                                        )
                                    }
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
                                        text = item.timeSlot,
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
}
