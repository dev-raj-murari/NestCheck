package com.nestcheck.app.ui.sinan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nestcheck.app.data.model.ToxicTextIncident
import com.nestcheck.app.data.model.ToxicityCategory
import com.nestcheck.app.data.model.ToxicitySensitivity
import com.nestcheck.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ToxicTextMonitorScreen(
    navController: NavController,
    viewModel: ToxicTextViewModel = hiltViewModel()
) {
    val config by viewModel.config.collectAsState()
    val incidents by viewModel.incidents.collectAsState()
    val testResult by viewModel.simulatedAnalysisResult.collectAsState()

    var testMessageInput by remember { mutableStateOf("Hey loser go away nobody wants you here") }
    var testSenderInput by remember { mutableStateOf("+1 (555) 890-4122") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBlack)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
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
                    text = "Cyberbullying & Toxic Text",
                    color = CyberTextBright,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF00E5FF).copy(alpha = 0.15f))
                    .border(1.dp, Color(0xFF00E5FF), RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text("ON-DEVICE NLP", color = Color(0xFF00E5FF), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // HERO / OVERVIEW CARD
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CyberCard)
                        .border(1.dp, CyberBorder, RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "PRIVACY-PRESERVING MODERATION (F32)",
                                color = CyberTextDim,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = config.sensitivity.displayName,
                                color = Color(0xFF00E5FF),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Zero-Knowledge Bullying & Threat Guard",
                            color = CyberTextBright,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Incoming chats are evaluated locally on-device. Flagged phrases are masked and alerted to parents without intrusive full-text message reading.",
                            color = CyberTextDim,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = CyberBorder, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Analyzed", color = CyberTextDim, fontSize = 11.sp)
                                Text("${config.totalScannedCount} texts", color = CyberTextBright, fontSize = 14.sp, fontFamily = TechMono, fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Text("Flagged", color = CyberTextDim, fontSize = 11.sp)
                                Text("${config.totalFlaggedCount} alerts", color = AlertRed, fontSize = 14.sp, fontFamily = TechMono, fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Text("False Positives", color = CyberTextDim, fontSize = 11.sp)
                                Text("< 1.8%", color = SuccessGreen, fontSize = 14.sp, fontFamily = TechMono, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // SENSITIVITY PICKER
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(CyberCard)
                        .border(1.dp, CyberBorder, RoundedCornerShape(10.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Text("Sensitivity Threshold", color = CyberTextBright, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ToxicitySensitivity.values().forEach { sens ->
                                val isSelected = config.sensitivity == sens
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(34.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSelected) White else CyberBlack)
                                        .border(1.dp, if (isSelected) White else CyberBorder, RoundedCornerShape(6.dp))
                                        .clickable { viewModel.updateSensitivity(sens) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = sens.name,
                                        color = if (isSelected) Black else CyberTextDim,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // INTERACTIVE TEST HARNESS
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(CyberCard)
                        .border(1.dp, CyberBorder, RoundedCornerShape(10.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("SMS Message Analyzer Test", color = CyberTextBright, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("SIMULATOR", color = CyberTextDim, fontSize = 10.sp, fontFamily = TechMono)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = testMessageInput,
                            onValueChange = { testMessageInput = it },
                            label = { Text("Simulated Incoming Text Message", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = CyberTextBright,
                                unfocusedTextColor = CyberTextBright,
                                focusedBorderColor = White,
                                unfocusedBorderColor = CyberBorder
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            PresetChip("Bullying", "Hey loser go away") { testMessageInput = it }
                            PresetChip("Predatory", "Meet me alone in secret") { testMessageInput = it }
                            PresetChip("Friendly", "Let's study math at library") { testMessageInput = it }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = { viewModel.testMessageScan(testSenderInput, testMessageInput) },
                            modifier = Modifier.fillMaxWidth().height(40.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = Black)
                        ) {
                            Text("Run On-Device NLP Scan", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        testResult?.let { res ->
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(AlertRed.copy(alpha = 0.15f))
                                    .border(1.dp, AlertRed, RoundedCornerShape(6.dp))
                                    .padding(10.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "FLAGGED: ${res.flaggedCategory.displayName} (${(res.confidenceScore * 100).toInt()}% confidence)",
                                        color = AlertRed,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text("Masked preview: ${res.obfuscatedSnippet}", color = CyberTextBright, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }

            // INCIDENT FEED HEADER
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Flagged Toxicity Incidents (${incidents.size})",
                        color = CyberTextBright,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (incidents.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(CyberCard)
                            .border(1.dp, CyberBorder, RoundedCornerShape(8.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No toxic text incidents recorded.", color = CyberTextDim, fontSize = 12.sp)
                    }
                }
            } else {
                items(incidents, key = { it.id }) { incident ->
                    ToxicIncidentCard(
                        incident = incident,
                        onDismiss = { viewModel.dismissIncident(incident.id) },
                        onReviewed = { viewModel.markReviewed(incident.id) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun PresetChip(label: String, text: String, onSelect: (String) -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(CyberBlack)
            .border(1.dp, CyberBorder, RoundedCornerShape(4.dp))
            .clickable { onSelect(text) }
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(label, color = CyberTextDim, fontSize = 10.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun ToxicIncidentCard(
    incident: ToxicTextIncident,
    onDismiss: () -> Unit,
    onReviewed: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()) }

    val categoryColor = when (incident.flaggedCategory) {
        ToxicityCategory.PREDATORY -> AlertRed
        ToxicityCategory.THREAT -> AlertRed
        ToxicityCategory.BULLYING -> WarningAmber
        ToxicityCategory.PROFANITY -> Color(0xFF00E5FF)
        ToxicityCategory.HARASSMENT -> AlertRed
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(CyberCard)
            .border(
                1.dp,
                if (incident.isReviewed) CyberBorder else categoryColor.copy(alpha = 0.5f),
                RoundedCornerShape(10.dp)
            )
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(categoryColor.copy(alpha = 0.2f))
                            .border(1.dp, categoryColor, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = incident.flaggedCategory.name,
                            color = categoryColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = incident.senderNumber,
                        color = CyberTextBright,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = TechMono
                    )
                }

                Text(
                    text = dateFormat.format(Date(incident.timestamp)),
                    color = CyberTextDim,
                    fontSize = 10.sp,
                    fontFamily = TechMono
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = incident.obfuscatedSnippet,
                color = CyberTextBright,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Detected in ${incident.sourceApp} • ${(incident.confidenceScore * 100).toInt()}% NLP confidence",
                color = CyberTextDim,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!incident.isReviewed) {
                    Button(
                        onClick = onReviewed,
                        modifier = Modifier.weight(1f).height(34.dp),
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen, contentColor = Black)
                    ) {
                        Text("Mark Reviewed", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.height(34.dp),
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberTextDim)
                ) {
                    Text("Dismiss", fontSize = 11.sp)
                }
            }
        }
    }
}
