package com.nestcheck.app.ui.child.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nestcheck.app.data.model.UpcomingExam

@Composable
fun UpcomingExamsCard(
    exams: List<UpcomingExam>,
    modifier: Modifier = Modifier
) {
    val cardBg = Color(0xFF121824)
    val cyanAccent = Color(0xFF00E5FF)
    val amberWarning = Color(0xFFFFAB00)
    val redCritical = Color(0xFFFF5252)
    val borderStroke = Color(0xFF1E293B)
    val textMuted = Color(0xFF94A3B8)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(cardBg)
            .border(1.dp, borderStroke, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = amberWarning,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Upcoming School Exams",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    color = amberWarning.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "${exams.size} Scheduled",
                        color = amberWarning,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Exams List
            exams.forEach { exam ->
                val badgeColor = if (exam.daysRemaining <= 4) redCritical else amberWarning
                Surface(
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    color = cyanAccent.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = exam.subject,
                                        color = cyanAccent,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Text(
                                    text = exam.examTitle,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Surface(
                                color = badgeColor.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "${exam.daysRemaining} days left",
                                    color = badgeColor,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        // Date & Target Grade
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    tint = textMuted,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = exam.examDate,
                                    color = textMuted,
                                    fontSize = 11.sp
                                )
                            }

                            Text(
                                text = "Target: ${exam.targetGrade}",
                                color = Color(0xFF00E676),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Syllabus Topics Chips
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(exam.syllabusTopics) { topic ->
                                Surface(
                                    color = Color(0xFF0F172A),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "• $topic",
                                        color = textMuted,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
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
