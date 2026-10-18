package com.nestcheck.app.ui.child.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nestcheck.app.ui.theme.*

@Composable
fun ScreenTimeWidget(
    remainingMinutes: Int,
    dailyLimitMinutes: Int = 120,
    modifier: Modifier = Modifier
) {
    val isWarning = remainingMinutes in 1..14
    val isLocked = remainingMinutes <= 0

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(CyberCard)
            .border(
                1.dp,
                if (isLocked) AlertRed else if (isWarning) WarningAmber else CyberBorder,
                RoundedCornerShape(8.dp)
            )
            .padding(20.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Screen Time Remaining",
                    color = CyberTextDim,
                    fontSize = 12.sp
                )

                if (isWarning) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(WarningAmber.copy(alpha = 0.2f))
                            .border(1.dp, WarningAmber, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Warning: <15m",
                            color = WarningAmber,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else if (isLocked) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(AlertRed.copy(alpha = 0.2f))
                            .border(1.dp, AlertRed, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Time Exhausted",
                            color = AlertRed,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "$remainingMinutes",
                    color = if (isLocked) AlertRed else CyberTextBright,
                    fontSize = 54.sp,
                    fontFamily = TechMono,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 54.sp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = if (isLocked) "minutes left - locked" else "mins left today",
                    color = if (isLocked) AlertRed else CyberTextBright,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = CyberBorder, thickness = 1.dp)
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Daily Quota: $dailyLimitMinutes mins", color = CyberTextDim, fontSize = 12.sp)
                Text(
                    text = if (isLocked) "Locked" else "Active",
                    color = if (isLocked) AlertRed else SuccessGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
