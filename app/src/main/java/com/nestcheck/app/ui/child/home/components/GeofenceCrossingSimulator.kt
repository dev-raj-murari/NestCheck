package com.nestcheck.app.ui.child.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FmdGood
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nestcheck.app.data.model.GeofenceBreachSimResult

@Composable
fun GeofenceCrossingSimulator(
    breachResult: GeofenceBreachSimResult? = null,
    onSimulateBreach: () -> Unit,
    onDismissBreach: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val cardBg = Color(0xFFFFFFFF)
    val borderStroke = Color(0xFFE2E8F0)
    val textDark = Color(0xFF1E293B)
    val textMuted = Color(0xFF64748B)
    val primaryBlue = Color(0xFF0B57D0)
    val redCritical = Color(0xFFEF4444)
    val greenSuccess = Color(0xFF10B981)

    var testDispatchedTime by remember { mutableStateOf<String?>(null) }

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
                            imageVector = Icons.Default.FmdGood,
                            contentDescription = null,
                            tint = primaryBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = "Geofence Safety Check",
                        color = textDark,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    color = Color(0xFFE8F0FE),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "Safety Perimeter",
                        color = primaryBlue,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Text(
                text = "Simulate crossing the MPSTME NMIMS Mumbai safe boundary. The resulting alert is delivered directly to the Parent App.",
                color = textMuted,
                fontSize = 12.sp
            )

            // Simulation Action Button
            Button(
                onClick = {
                    onSimulateBreach()
                    val now = java.text.SimpleDateFormat("hh:mm:ss a", java.util.Locale.getDefault()).format(java.util.Date())
                    testDispatchedTime = now
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF0F4F9))
            ) {
                Icon(
                    imageVector = Icons.Default.NotificationsActive,
                    contentDescription = null,
                    tint = redCritical,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Test School Perimeter Crossing",
                    color = textDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            // Brief confirmation indicator (NOT a duplicate breach result card!)
            testDispatchedTime?.let { time ->
                Surface(
                    color = Color(0xFFE6F4EA),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "✓ Simulated crossing alert sent to Parent Console at $time",
                            color = greenSuccess,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
