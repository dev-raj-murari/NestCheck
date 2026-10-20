package com.nestcheck.app.ui.child.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nestcheck.app.data.model.ChildProfile
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChildDashboardHeader(
    profile: ChildProfile,
    isCurfewLocked: Boolean,
    onSosClick: () -> Unit,
    modifier: Modifier = Modifier,
    onParentPinClick: (() -> Unit)? = null
) {
    val cardBg = Color(0xFFFFFFFF)
    val borderStroke = Color(0xFFE2E8F0)
    val textDark = Color(0xFF1E293B)
    val textMuted = Color(0xFF64748B)
    val primaryBlue = Color(0xFF0B57D0)
    val greenActive = Color(0xFF10B981)
    val alertRed = Color(0xFFEF4444)

    val timeString = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
    val dateString = SimpleDateFormat("EEE, MMM d", Locale.getDefault()).format(Date())

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(cardBg)
            .border(1.dp, borderStroke, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Top Row: Avatar + Greeting + SOS + Pin Lock (Strictly NO switch role)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Avatar circle with initial in soft blue
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE8F0FE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = profile.name.firstOrNull()?.toString() ?: "S",
                            color = primaryBlue,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Column {
                        Text(
                            text = "Hey, ${profile.name}! 👋",
                            color = textDark,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$dateString • $timeString",
                            color = textMuted,
                            fontSize = 12.sp
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // SOS Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFEE2E2))
                            .clickable { onSosClick() }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "SOS",
                                tint = alertRed,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "SOS",
                                color = alertRed,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Optional Parent Exit PIN Lock (Safe gate for child app, NO switch role)
                    onParentPinClick?.let { onClick ->
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF1F5F9))
                                .clickable { onClick() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Lock,
                                contentDescription = "Parent Gate",
                                tint = textMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Bottom Strip: Status & Daily Goal
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Age ${profile.age} • Daily Goal: ${profile.dailyStepGoal} steps",
                    color = textMuted,
                    fontSize = 12.sp
                )

                val statusBg = if (isCurfewLocked) Color(0xFFFEE2E2) else Color(0xFFE6F4EA)
                val statusColor = if (isCurfewLocked) alertRed else greenActive
                val statusText = if (isCurfewLocked) "TIME EXPIRED" else "SAFE MODE • ONLINE"

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(statusBg)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(statusColor)
                        )
                        Text(
                            text = statusText,
                            color = statusColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
