package com.nestcheck.app.ui.child.credits

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nestcheck.app.data.model.CreditRequest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CreditRequestHistoryCard(
    requests: List<CreditRequest>,
    modifier: Modifier = Modifier
) {
    val cardBg = Color(0xFF121824)
    val cyanAccent = Color(0xFF00E5FF)
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
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = cyanAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Credit Request Audit History",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "${requests.size} Requests",
                    color = textMuted,
                    fontSize = 11.sp
                )
            }

            if (requests.isEmpty()) {
                Text(
                    text = "No credit requests submitted yet.",
                    color = textMuted,
                    fontSize = 12.sp
                )
            } else {
                requests.forEach { req ->
                    val statusColor = Color(req.status.badgeColorHex)
                    val timeStr = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date(req.requestTimestamp))

                    Surface(
                        color = Color(0xFF1E293B),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = req.taskTitle,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )

                                Surface(
                                    color = statusColor.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = req.status.label,
                                        color = statusColor,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${req.creditsRequested} pts requested (${req.screenTimeMinutesEquivalent}m)",
                                    color = cyanAccent,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Text(
                                    text = timeStr,
                                    color = textMuted,
                                    fontSize = 10.sp
                                )
                            }

                            if (req.responseNote.isNotBlank()) {
                                Surface(
                                    color = Color(0xFF0F172A),
                                    shape = RoundedCornerShape(4.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Parent: \"${req.responseNote}\"",
                                        color = textMuted,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(8.dp)
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
