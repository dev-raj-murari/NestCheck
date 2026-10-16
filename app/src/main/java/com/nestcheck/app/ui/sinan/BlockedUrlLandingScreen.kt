package com.nestcheck.app.ui.sinan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nestcheck.app.ui.theme.*

@Composable
fun BlockedUrlLandingScreen(
    navController: NavController,
    blockedDomain: String = "restricted-portal.net",
    categoryReason: String = "Adult & Unapproved Web Category"
) {
    var requestSent by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBlack)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // TOP BACK BUTTON
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back",
                    tint = CyberTextBright
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // SHIELD ICON WITH PULSE CIRCLE
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(AlertRed.copy(alpha = 0.15f))
                .border(2.dp, AlertRed, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Shield,
                contentDescription = "Blocked",
                tint = AlertRed,
                modifier = Modifier.size(52.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "ACCESS RESTRICTED",
            color = AlertRed,
            fontSize = 20.sp,
            fontFamily = TechMono,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "This website is locked by NestCheck Parental Guard to protect your digital wellbeing.",
            color = CyberTextDim,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // RESTRICTION METADATA CARD
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CyberCard)
                .border(1.dp, CyberBorder, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Attempted Domain", color = CyberTextDim, fontSize = 12.sp)
                    Text(blockedDomain, color = CyberTextBright, fontSize = 12.sp, fontFamily = TechMono, fontWeight = FontWeight.Bold)
                }

                HorizontalDivider(color = CyberBorder, thickness = 1.dp)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Policy Category", color = CyberTextDim, fontSize = 12.sp)
                    Text(categoryReason, color = AlertRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                HorizontalDivider(color = CyberBorder, thickness = 1.dp)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Enforcement Mode", color = CyberTextDim, fontSize = 12.sp)
                    Text("Hardware Level DNS Filter", color = CyberTextBright, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        if (requestSent) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(SuccessGreen.copy(alpha = 0.2f))
                    .border(1.dp, SuccessGreen, RoundedCornerShape(10.dp))
                    .padding(14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Request sent to parent device. Please await approval.",
                    color = SuccessGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            Button(
                onClick = { requestSent = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = Black)
            ) {
                Icon(Icons.Outlined.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Request Access from Parent", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        TextButton(
            onClick = { navController.popBackStack() },
            colors = ButtonDefaults.textButtonColors(contentColor = CyberTextDim)
        ) {
            Text("Return to Safety", fontSize = 13.sp)
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}
