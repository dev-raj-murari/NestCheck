package com.nestcheck.app.ui.sinan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nestcheck.app.data.model.AuditedApp
import com.nestcheck.app.data.model.PermissionRiskLevel
import com.nestcheck.app.data.model.SensitivePermission

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionAuditorScreen(
    navController: NavController,
    viewModel: PermissionAuditorViewModel = hiltViewModel()
) {
    val apps by viewModel.apps.collectAsState()
    val summary by viewModel.summary.collectAsState()
    val selectedFilter by viewModel.selectedFilter
    val isScanning by viewModel.isScanning

    val bgColor = Color(0xFFF8FAFC)
    val cardColor = Color(0xFFFFFFFF)
    val cyanAccent = Color(0xFF0B57D0)
    val greenSuccess = Color(0xFF10B981)
    val redCritical = Color(0xFFEF4444)
    val amberWarning = Color(0xFFF59E0B)
    val textMuted = Color(0xFF64748B)
    val borderStroke = Color(0xFFE2E8F0)
    val textDark = Color(0xFF0F172A)

    val filteredApps = apps.filter { app ->
        selectedFilter == null || app.overallRisk == selectedFilter
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "App Permission Auditor",
                            fontWeight = FontWeight.Bold,
                            color = textDark,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Sensitive Hardware & Privacy Scanner",
                            fontSize = 12.sp,
                            color = textMuted
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = textDark
                        )
                    }
                },
                actions = {
                    Surface(
                        color = cyanAccent.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Text(
                            text = "F35 ACTIVE",
                            color = cyanAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = bgColor)
            )
        },
        containerColor = bgColor
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Metrics Row (3 Stat Cards)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Critical
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.setFilter(PermissionRiskLevel.CRITICAL) },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = cardColor),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(if (selectedFilter == PermissionRiskLevel.CRITICAL) redCritical else borderStroke))
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(text = "CRITICAL", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = redCritical)
                            Text(text = "${summary.criticalRiskAppsCount}", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = textDark)
                            Text(text = "Excessive access", fontSize = 10.sp, color = textMuted)
                        }
                    }

                    // Elevated
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.setFilter(PermissionRiskLevel.ELEVATED) },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = cardColor),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(if (selectedFilter == PermissionRiskLevel.ELEVATED) amberWarning else borderStroke))
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(text = "ELEVATED", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = amberWarning)
                            Text(text = "${summary.elevatedRiskAppsCount}", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = textDark)
                            Text(text = "Monitor closely", fontSize = 10.sp, color = textMuted)
                        }
                    }

                    // Safe
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.setFilter(PermissionRiskLevel.SAFE) },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = cardColor),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(if (selectedFilter == PermissionRiskLevel.SAFE) greenSuccess else borderStroke))
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(text = "CLEAN", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = greenSuccess)
                            Text(text = "${summary.safeAppsCount}", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = textDark)
                            Text(text = "Healthy apps", fontSize = 10.sp, color = textMuted)
                        }
                    }
                }
            }

            // Scan Action Button
            item {
                Button(
                    onClick = { viewModel.runScan() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = cyanAccent),
                    enabled = !isScanning
                ) {
                    if (isScanning) {
                        CircularProgressIndicator(
                            color = Color.Black,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Auditing 42 Installed Packages...",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Run Comprehensive Hardware & Permission Audit",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Filter Chips Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val filters = listOf(
                        Triple("All (42)", null, textMuted),
                        Triple("Critical", PermissionRiskLevel.CRITICAL, redCritical),
                        Triple("Elevated", PermissionRiskLevel.ELEVATED, amberWarning),
                        Triple("Safe", PermissionRiskLevel.SAFE, greenSuccess)
                    )

                    filters.forEach { (label, risk, color) ->
                        val isSelected = selectedFilter == risk
                        Surface(
                            color = if (isSelected) color.copy(alpha = 0.15f) else Color(0xFFF1F5F9),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .clickable { viewModel.setFilter(risk) }
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) color else Color.Transparent,
                                    shape = RoundedCornerShape(8.dp)
                                )
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) color else textMuted,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // Audited App Cards List
            items(filteredApps) { app: AuditedApp ->
                val riskColor = when (app.overallRisk) {
                    PermissionRiskLevel.CRITICAL -> redCritical
                    PermissionRiskLevel.ELEVATED -> amberWarning
                    PermissionRiskLevel.SAFE -> greenSuccess
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(borderStroke))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFD3E3FD)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = app.appName.take(1),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = cyanAccent
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = app.appName,
                                        fontWeight = FontWeight.Bold,
                                        color = textDark,
                                        fontSize = 14.sp
                                    )
                                    if (app.isQuarantined) {
                                        Surface(
                                            color = redCritical.copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = "QUARANTINED",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = redCritical,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = "${app.category} • ${app.packageName}",
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.SansSerif,
                                    color = textMuted,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Surface(
                                color = riskColor.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = app.overallRisk.displayName,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = riskColor,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        // Granted permissions list
                        if (app.permissionsGranted.isNotEmpty()) {
                            Text(
                                text = "Granted Sensitive Permissions (${app.permissionsGranted.size}):",
                                fontSize = 11.sp,
                                color = textMuted,
                                fontWeight = FontWeight.SemiBold
                            )
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(app.permissionsGranted) { perm: SensitivePermission ->
                                    val permColor = when (perm.riskLevel) {
                                        PermissionRiskLevel.CRITICAL -> redCritical
                                        PermissionRiskLevel.ELEVATED -> amberWarning
                                        PermissionRiskLevel.SAFE -> cyanAccent
                                    }
                                    Surface(
                                        color = permColor.copy(alpha = 0.12f),
                                        shape = RoundedCornerShape(6.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, permColor.copy(alpha = 0.3f))
                                    ) {
                                        Text(
                                            text = perm.title,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = permColor,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        } else {
                            Text(
                                text = "No dangerous hardware permissions granted.",
                                fontSize = 11.sp,
                                color = greenSuccess
                            )
                        }

                        HorizontalDivider(color = borderStroke)

                        // Action buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (!app.isQuarantined && app.overallRisk != PermissionRiskLevel.SAFE) {
                                OutlinedButton(
                                    onClick = { viewModel.quarantine(app.packageName) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = redCritical),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, redCritical.copy(alpha = 0.5f))
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Block,
                                        contentDescription = null,
                                        tint = redCritical,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Quarantine App",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = redCritical
                                    )
                                }
                            }

                            OutlinedButton(
                                onClick = { viewModel.trust(app.packageName) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = greenSuccess),
                                border = androidx.compose.foundation.BorderStroke(1.dp, greenSuccess.copy(alpha = 0.5f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = greenSuccess,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (app.isQuarantined) "Restore & Trust" else "Mark Safe",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = greenSuccess
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
