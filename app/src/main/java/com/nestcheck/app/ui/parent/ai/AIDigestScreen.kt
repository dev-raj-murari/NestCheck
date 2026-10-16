package com.nestcheck.app.ui.parent.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.DirectionsWalk
import androidx.compose.material.icons.outlined.HourglassBottom
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nestcheck.app.ui.theme.*

@Composable
fun AIDigestScreen(
    navController: NavController,
    viewModel: AIDigestViewModel = hiltViewModel()
) {
    val digest by viewModel.digest.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBlack)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // TOP APP BAR
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back",
                        tint = CyberTextBright
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "AI Daily Digest",
                    color = CyberTextBright,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(White.copy(alpha = 0.15f))
                    .border(1.dp, White, RoundedCornerShape(20.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.AutoAwesome, contentDescription = null, tint = White, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("ON-DEVICE AI", color = White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // AI SUMMARY HERO CARD
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CyberCard)
                .border(1.dp, CyberBorder, RoundedCornerShape(12.dp))
                .padding(18.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(digest.generatedDate, color = CyberTextDim, fontSize = 11.sp)
                    Text(
                        text = "Score: ${digest.wellbeingScore} / 100",
                        color = SuccessGreen,
                        fontSize = 12.sp,
                        fontFamily = TechMono,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = digest.executiveSummary,
                    color = CyberTextBright,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3 DOMAIN SYNTHESIS CARDS
        // 1. Screen Time
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(CyberCard)
                .border(1.dp, CyberBorder, RoundedCornerShape(10.dp))
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Icon(Icons.Outlined.HourglassBottom, contentDescription = null, tint = White, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Screen Time Analysis", color = CyberTextBright, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(digest.screenTimeAnalysis, color = CyberTextDim, fontSize = 12.sp, lineHeight = 16.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 2. Physical Activity
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(CyberCard)
                .border(1.dp, CyberBorder, RoundedCornerShape(10.dp))
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Icon(Icons.Outlined.DirectionsWalk, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Physical Wellbeing", color = CyberTextBright, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(digest.physicalActivityAnalysis, color = CyberTextDim, fontSize = 12.sp, lineHeight = 16.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 3. Safety & Shield
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(CyberCard)
                .border(1.dp, CyberBorder, RoundedCornerShape(10.dp))
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Icon(Icons.Outlined.Shield, contentDescription = null, tint = White, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Digital Protection Status", color = CyberTextBright, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(digest.safetyIncidentAnalysis, color = CyberTextDim, fontSize = 12.sp, lineHeight = 16.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // AI RECOMMENDATIONS
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CyberCard)
                .border(1.dp, CyberBorder, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Lightbulb, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("SMART PARENTING RECOMMENDATIONS", color = CyberTextDim, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                digest.aiRecommendations.forEach { rec ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(Icons.Outlined.Check, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(rec, color = CyberTextBright, fontSize = 12.sp, lineHeight = 16.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // REGENERATE ACTION BUTTON
        Button(
            onClick = viewModel::refreshDigest,
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = Black)
        ) {
            Icon(Icons.Outlined.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Regenerate AI Daily Synthesis", fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
