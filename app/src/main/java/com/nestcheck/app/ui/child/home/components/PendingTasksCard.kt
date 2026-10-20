package com.nestcheck.app.ui.child.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nestcheck.app.data.model.PendingTaskItem

@Composable
fun PendingTasksCard(
    tasks: List<PendingTaskItem>,
    onToggleTask: (String) -> Unit,
    onRequestCreditsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardBg = Color(0xFFFFFFFF)
    val borderStroke = Color(0xFFE2E8F0)
    val textDark = Color(0xFF1E293B)
    val textMuted = Color(0xFF64748B)
    val primaryBlue = Color(0xFF0B57D0)
    val greenSuccess = Color(0xFF10B981)

    val pendingCount = tasks.count { !it.isCompleted }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(cardBg)
            .border(1.dp, borderStroke, RoundedCornerShape(20.dp))
            .padding(18.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            // Header Row
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
                            .background(Color(0xFFE6F4EA)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.TaskAlt,
                            contentDescription = null,
                            tint = greenSuccess,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = "Pending Tasks & Quests",
                        color = textDark,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Surface(
                        color = Color(0xFFE8F0FE),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "$pendingCount left",
                            color = primaryBlue,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Text(
                    text = "Tap to Complete",
                    color = textMuted,
                    fontSize = 11.sp
                )
            }

            // Task Items List
            tasks.forEach { task ->
                Surface(
                    color = if (task.isCompleted) Color(0xFFF8FAFC) else Color.White,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onToggleTask(task.id) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(if (task.isCompleted) greenSuccess else Color.Transparent)
                                    .border(
                                        width = 1.5.dp,
                                        color = if (task.isCompleted) greenSuccess else Color(0xFFCBD5E1),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (task.isCompleted) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Done",
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }

                            Column {
                                Text(
                                    text = task.title,
                                    color = if (task.isCompleted) textMuted else textDark,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    style = if (task.isCompleted) androidx.compose.ui.text.TextStyle(textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough) else androidx.compose.ui.text.TextStyle()
                                )
                                Text(
                                    text = "${task.category.label} • Due ${task.dueDate}",
                                    color = textMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Surface(
                            color = if (task.isCompleted) Color(0xFFE6F4EA) else Color(0xFFE8F0FE),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (task.isCompleted) "+${task.creditsReward} pts EARNED" else "+${task.creditsReward} pts (+${task.screenTimeBonusMinutes}m)",
                                color = if (task.isCompleted) greenSuccess else primaryBlue,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = borderStroke)

            // Request Credits from Parent Action
            OutlinedButton(
                onClick = onRequestCreditsClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = primaryBlue),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE))
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = primaryBlue,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Request Extra Credits for a Task",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryBlue
                )
            }
        }
    }
}
