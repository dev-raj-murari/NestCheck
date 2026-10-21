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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HourglassTop
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
import com.nestcheck.app.data.model.TaskApprovalStatus

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
    val amberPending = Color(0xFFD97706)
    val redReject = Color(0xFFDC2626)

    val pendingCount = tasks.count { 
        it.approvalStatus != TaskApprovalStatus.APPROVED_AND_CREDITED && it.approvalStatus != TaskApprovalStatus.APPROVED 
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
                        text = "Tasks & Quests",
                        color = textDark,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Surface(
                        color = Color(0xFFE8F0FE),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "$pendingCount active",
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
                val isApproved = task.approvalStatus == TaskApprovalStatus.APPROVED_AND_CREDITED || task.approvalStatus == TaskApprovalStatus.APPROVED
                val isSubmitted = task.approvalStatus == TaskApprovalStatus.SUBMITTED_PENDING_APPROVAL || task.approvalStatus == TaskApprovalStatus.PENDING_PARENT_APPROVAL
                val isRejected = task.approvalStatus == TaskApprovalStatus.REJECTED

                Surface(
                    color = when {
                        isApproved -> Color(0xFFF8FAFC)
                        isSubmitted -> Color(0xFFFFFBEB)
                        isRejected -> Color(0xFFFEF2F2)
                        else -> Color.White
                    },
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        when {
                            isSubmitted -> Color(0xFFFDE68A)
                            isRejected -> Color(0xFFFECACA)
                            isApproved -> Color(0xFFBBF7D0)
                            else -> Color(0xFFE2E8F0)
                        }
                    ),
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
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isApproved -> greenSuccess
                                            isSubmitted -> amberPending
                                            isRejected -> redReject
                                            else -> Color.Transparent
                                        }
                                    )
                                    .border(
                                        width = 1.5.dp,
                                        color = when {
                                            isApproved -> greenSuccess
                                            isSubmitted -> amberPending
                                            isRejected -> redReject
                                            else -> Color(0xFFCBD5E1)
                                        },
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                when {
                                    isApproved -> {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Verified",
                                            tint = Color.White,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                    isSubmitted -> {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Submitted",
                                            tint = Color.White,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                    isRejected -> {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Rejected",
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }

                            Column {
                                Text(
                                    text = task.title,
                                    color = if (isApproved) textMuted else textDark,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    style = if (isApproved) androidx.compose.ui.text.TextStyle(textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough) else androidx.compose.ui.text.TextStyle()
                                )
                                val subtitleText = when {
                                    isApproved -> "Verified by Parent ✓"
                                    isSubmitted -> "Submitted — Waiting for Parent"
                                    isRejected -> "Needs Revision: ${task.rejectionReason ?: "Changes requested"} • Tap to resubmit"
                                    else -> "${task.category.label} • Due ${task.dueDate}"
                                }
                                val subtitleColor = when {
                                    isApproved -> greenSuccess
                                    isSubmitted -> amberPending
                                    isRejected -> redReject
                                    else -> textMuted
                                }
                                Text(
                                    text = subtitleText,
                                    color = subtitleColor,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSubmitted || isRejected) FontWeight.SemiBold else FontWeight.Normal
                                )
                            }
                        }

                        // Status badge on the right
                        when {
                            isApproved -> {
                                Surface(
                                    color = Color(0xFFE6F4EA),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "+${task.creditsReward} pts CREDITED",
                                        color = greenSuccess,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                            isSubmitted -> {
                                Surface(
                                    color = Color(0xFFFEF3C7),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "WAITING REVIEW",
                                        color = amberPending,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                            isRejected -> {
                                Surface(
                                    color = Color(0xFFFEE2E2),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "REVISE",
                                        color = redReject,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                            else -> {
                                Surface(
                                    color = Color(0xFFE8F0FE),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "+${task.creditsReward} pts (+${task.screenTimeBonusMinutes}m)",
                                        color = primaryBlue,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
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
