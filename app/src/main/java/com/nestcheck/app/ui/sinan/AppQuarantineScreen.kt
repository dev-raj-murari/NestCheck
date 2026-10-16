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
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Warning
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
import com.nestcheck.app.data.model.QuarantineStatus
import com.nestcheck.app.data.model.QuarantinedApp
import com.nestcheck.app.data.model.RiskRating
import com.nestcheck.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AppQuarantineScreen(
    navController: NavController,
    viewModel: AppQuarantineViewModel = hiltViewModel()
) {
    val apps by viewModel.quarantinedApps.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()

    val pendingCount = apps.count { it.status == QuarantineStatus.PENDING_APPROVAL }
    val highRiskCount = apps.count { it.riskRating == RiskRating.HIGH_RISK }

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
                    text = "App Quarantine Guard",
                    color = CyberTextBright,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(onClick = viewModel::simulateIncomingApp) {
                Icon(
                    imageVector = Icons.Outlined.Download,
                    contentDescription = "Simulate New Install",
                    tint = CyberTextBright
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // HERO OVERVIEW CARD
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
                        text = "NEW INSTALL INTERCEPTOR",
                        color = CyberTextDim,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Play Store Guard Active",
                        color = SuccessGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Automated Zero-Trust App Quarantine",
                    color = CyberTextBright,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Apps downloaded by the child are held in a sandboxed, restricted state until a parent audits their permissions and authorizes launch.",
                    color = CyberTextDim,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = CyberBorder, thickness = 1.dp)
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Total Quarantined", color = CyberTextDim, fontSize = 11.sp)
                        Text("${apps.size} packages", color = CyberTextBright, fontSize = 14.sp, fontFamily = TechMono, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text("Pending Review", color = CyberTextDim, fontSize = 11.sp)
                        Text("$pendingCount apps", color = if (pendingCount > 0) WarningAmber else SuccessGreen, fontSize = 14.sp, fontFamily = TechMono, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text("High Risk Flagged", color = CyberTextDim, fontSize = 11.sp)
                        Text("$highRiskCount flagged", color = if (highRiskCount > 0) AlertRed else CyberTextDim, fontSize = 14.sp, fontFamily = TechMono, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // FILTER PILLS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            QuarantineFilter.values().forEach { filter ->
                val isSelected = filter == selectedFilter
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(34.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) White else CyberCard)
                        .border(1.dp, if (isSelected) White else CyberBorder, RoundedCornerShape(6.dp))
                        .clickable { viewModel.selectFilter(filter) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (filter) {
                            QuarantineFilter.ALL -> "All"
                            QuarantineFilter.PENDING -> "Pending"
                            QuarantineFilter.APPROVED -> "Approved"
                            QuarantineFilter.BLOCKED -> "Blocked"
                        },
                        color = if (isSelected) Black else CyberTextDim,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // APP LIST
        if (apps.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("No applications matching selected filter.", color = CyberTextDim, fontSize = 13.sp)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(apps, key = { it.id }) { app ->
                    QuarantineAppCard(
                        app = app,
                        onApprove = { viewModel.approveApp(app.id) },
                        onBlock = { viewModel.blockApp(app.id) }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun QuarantineAppCard(
    app: QuarantinedApp,
    onApprove: () -> Unit,
    onBlock: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(CyberCard)
            .border(
                1.dp,
                when (app.status) {
                    QuarantineStatus.PENDING_APPROVAL -> WarningAmber.copy(alpha = 0.5f)
                    QuarantineStatus.BLOCKED -> AlertRed.copy(alpha = 0.5f)
                    QuarantineStatus.APPROVED -> CyberBorder
                },
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
                            .background(
                                when (app.status) {
                                    QuarantineStatus.PENDING_APPROVAL -> WarningAmber
                                    QuarantineStatus.APPROVED -> SuccessGreen
                                    QuarantineStatus.BLOCKED -> AlertRed
                                }
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = app.status.displayName.uppercase(),
                            color = if (app.status == QuarantineStatus.PENDING_APPROVAL) Black else White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(CyberBlack)
                            .border(0.5.dp, CyberBorderBright, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = app.contentRating,
                            color = CyberTextBright,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(
                    text = dateFormat.format(Date(app.installTimestamp)),
                    color = CyberTextDim,
                    fontSize = 11.sp,
                    fontFamily = TechMono
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = app.appName,
                color = CyberTextBright,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${app.packageName} • v${app.versionName} • ${app.developer}",
                color = CyberTextDim,
                fontSize = 11.sp,
                fontFamily = TechMono
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Requested Android Permissions (${app.permissions.size}):",
                color = CyberTextDim,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(6.dp))

            // PERMISSIONS TAGS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                app.permissions.take(3).forEach { perm ->
                    val cleanPerm = perm.substringAfterLast(".")
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(CyberBlack)
                            .border(1.dp, CyberBorder, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(text = cleanPerm, color = CyberTextDim, fontSize = 10.sp, fontFamily = TechMono)
                    }
                }
                if (app.permissions.size > 3) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(CyberBlack)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(text = "+${app.permissions.size - 3} more", color = CyberTextDim, fontSize = 10.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ACTION BUTTONS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (app.status != QuarantineStatus.APPROVED) {
                    Button(
                        onClick = onApprove,
                        modifier = Modifier.weight(1f).height(38.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen, contentColor = Black)
                    ) {
                        Icon(Icons.Outlined.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Approve & Release", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                if (app.status != QuarantineStatus.BLOCKED) {
                    OutlinedButton(
                        onClick = onBlock,
                        modifier = Modifier.weight(1f).height(38.dp),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AlertRed),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AlertRed)
                    ) {
                        Icon(Icons.Outlined.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Block Package", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
