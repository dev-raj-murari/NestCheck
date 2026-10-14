package com.nestcheck.app.ui.parent.content

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
import com.nestcheck.app.data.model.*
import com.nestcheck.app.navigation.Screen
import com.nestcheck.app.ui.sinan.SafetyContentTab
import com.nestcheck.app.ui.sinan.SafetyViewModel
import com.nestcheck.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ContentFilterScreen(
    navController: NavController,
    viewModel: SafetyViewModel = hiltViewModel()
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val scannerConfig by viewModel.scannerConfig.collectAsState()
    val incidents by viewModel.incidents.collectAsState()
    val categoryRules by viewModel.categoryRules.collectAsState()
    val customDomains by viewModel.customDomains.collectAsState()
    val testUrlInput by viewModel.testUrlInput.collectAsState()
    val evaluationResult by viewModel.evaluationResult.collectAsState()

    var showAddDomainDialog by remember { mutableStateOf(false) }

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
                    text = "Content Safety",
                    color = CyberTextBright,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Quick Link to App Quarantine (F9)
            IconButton(onClick = { navController.navigate(Screen.AppQuarantine.route) }) {
                Icon(
                    imageVector = Icons.Outlined.Security,
                    contentDescription = "Quarantined Apps",
                    tint = CyberTextBright
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // TAB SWITCHER: F7 (NSFW Detection) vs F8 (Web Filtering)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (currentTab == SafetyContentTab.NSFW_DETECTION) White else CyberCard)
                    .border(1.dp, if (currentTab == SafetyContentTab.NSFW_DETECTION) White else CyberBorder, RoundedCornerShape(8.dp))
                    .clickable { viewModel.selectTab(SafetyContentTab.NSFW_DETECTION) },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Shield,
                        contentDescription = null,
                        tint = if (currentTab == SafetyContentTab.NSFW_DETECTION) Black else CyberTextDim,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "NSFW Scanner",
                        color = if (currentTab == SafetyContentTab.NSFW_DETECTION) Black else CyberTextDim,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (currentTab == SafetyContentTab.WEB_FILTERING) White else CyberCard)
                    .border(1.dp, if (currentTab == SafetyContentTab.WEB_FILTERING) White else CyberBorder, RoundedCornerShape(8.dp))
                    .clickable { viewModel.selectTab(SafetyContentTab.WEB_FILTERING) },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Public,
                        contentDescription = null,
                        tint = if (currentTab == SafetyContentTab.WEB_FILTERING) Black else CyberTextDim,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Web Filters",
                        color = if (currentTab == SafetyContentTab.WEB_FILTERING) Black else CyberTextDim,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // TAB CONTENT
        when (currentTab) {
            SafetyContentTab.NSFW_DETECTION -> {
                NsfwDetectionContent(
                    config = scannerConfig,
                    incidents = incidents,
                    onSensitivityChange = viewModel::setSensitivity,
                    onToggleBlur = viewModel::toggleRealtimeBlur,
                    onToggleNotification = viewModel::toggleInstantNotification,
                    onDismissIncident = viewModel::dismissIncident,
                    onToggleIncidentBlur = viewModel::toggleIncidentBlur,
                    onTriggerScan = viewModel::triggerSimulatedScan
                )
            }
            SafetyContentTab.WEB_FILTERING -> {
                WebFilteringContent(
                    rules = categoryRules,
                    customDomains = customDomains,
                    testUrlInput = testUrlInput,
                    evaluationResult = evaluationResult,
                    onToggleCategory = viewModel::toggleCategory,
                    onTestUrlChanged = viewModel::onTestUrlChanged,
                    onTestUrl = viewModel::testUrlAccess,
                    onClearEvaluation = viewModel::clearEvaluation,
                    onRemoveDomain = viewModel::removeDomain,
                    onOpenAddDomain = { showAddDomainDialog = true },
                    onNavigateToBlockedLanding = {
                        navController.navigate(Screen.BlockedUrl.route)
                    }
                )
            }
        }
    }

    if (showAddDomainDialog) {
        AddDomainDialog(
            onDismiss = { showAddDomainDialog = false },
            onConfirm = { domain, isBlocked, note ->
                viewModel.addDomain(domain, isBlocked, note)
                showAddDomainDialog = false
            }
        )
    }
}

// ============================================================================
// F7: ON-DEVICE NSFW DETECTION COMPOSABLES
// ============================================================================

@Composable
private fun NsfwDetectionContent(
    config: SafetyScannerConfig,
    incidents: List<NsfwIncident>,
    onSensitivityChange: (NsfwSensitivityLevel) -> Unit,
    onToggleBlur: (Boolean) -> Unit,
    onToggleNotification: (Boolean) -> Unit,
    onDismissIncident: (String) -> Unit,
    onToggleIncidentBlur: (String) -> Unit,
    onTriggerScan: () -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // SCANNER STATUS HERO
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(SuccessGreen)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ON-DEVICE AI SCANNER ACTIVE",
                                color = SuccessGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "TensorFlow Lite",
                            color = CyberTextDim,
                            fontSize = 11.sp,
                            fontFamily = TechMono
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Automated Real-Time Visual Moderation",
                        color = CyberTextBright,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Scans received photos and downloads locally on device. Images triggering the safety threshold are automatically blurred before display.",
                        color = CyberTextDim,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = CyberBorder, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Media Analyzed", color = CyberTextDim, fontSize = 11.sp)
                            Text("${config.totalScannedCount} files", color = CyberTextBright, fontSize = 14.sp, fontFamily = TechMono, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text("Threats Blocked", color = CyberTextDim, fontSize = 11.sp)
                            Text("${config.totalBlockedCount} blocked", color = AlertRed, fontSize = 14.sp, fontFamily = TechMono, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text("Auto-Blur", color = CyberTextDim, fontSize = 11.sp)
                            Text(if (config.realtimeBlurEnabled) "Enabled" else "Off", color = if (config.realtimeBlurEnabled) SuccessGreen else CyberTextDim, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // SENSITIVITY & CONTROLS CARD
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
                    Text(
                        text = "Detection Sensitivity",
                        color = CyberTextBright,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        NsfwSensitivityLevel.values().forEach { level ->
                            val isSelected = config.sensitivityLevel == level
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(34.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) White else CyberBlack)
                                    .border(1.dp, if (isSelected) White else CyberBorder, RoundedCornerShape(6.dp))
                                    .clickable { onSensitivityChange(level) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = level.displayName.substringBefore(" "),
                                    color = if (isSelected) Black else CyberTextDim,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Instant Image Blurring", color = CyberTextBright, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("Applies real-time Gaussian filter to flagged frames", color = CyberTextDim, fontSize = 11.sp)
                        }
                        Switch(
                            checked = config.realtimeBlurEnabled,
                            onCheckedChange = onToggleBlur,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Black,
                                checkedTrackColor = White,
                                uncheckedThumbColor = CyberTextDim,
                                uncheckedTrackColor = CyberBlack
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Parent Panic Notification", color = CyberTextBright, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("Dispatches high-priority incident alert to parent device", color = CyberTextDim, fontSize = 11.sp)
                        }
                        Switch(
                            checked = config.notifyParentInstant,
                            onCheckedChange = onToggleNotification,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Black,
                                checkedTrackColor = White,
                                uncheckedThumbColor = CyberTextDim,
                                uncheckedTrackColor = CyberBlack
                            )
                        )
                    }
                }
            }
        }

        // SIMULATE SCAN ACTION BUTTON
        item {
            Button(
                onClick = onTriggerScan,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = Black)
            ) {
                Icon(Icons.Outlined.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Simulate Incoming Media Scan", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }

        // INCIDENT LOG SECTION HEADER
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Flagged Incidents (${incidents.size})",
                    color = CyberTextBright,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Parent Audit Feed",
                    color = CyberTextDim,
                    fontSize = 12.sp
                )
            }
        }

        // INCIDENT LIST
        if (incidents.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(CyberCard)
                        .border(1.dp, CyberBorder, RoundedCornerShape(10.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No safety incidents detected. Media is clean.", color = CyberTextDim, fontSize = 13.sp)
                }
            }
        } else {
            items(incidents, key = { it.id }) { incident ->
                IncidentReportCard(
                    incident = incident,
                    onToggleBlur = { onToggleIncidentBlur(incident.id) },
                    onDismiss = { onDismissIncident(incident.id) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun IncidentReportCard(
    incident: NsfwIncident,
    onToggleBlur: () -> Unit,
    onDismiss: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(CyberCard)
            .border(
                1.dp,
                if (incident.severity == NsfwSeverity.HIGH) AlertRed.copy(alpha = 0.6f) else CyberBorder,
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
                                when (incident.severity) {
                                    NsfwSeverity.HIGH -> AlertRed
                                    NsfwSeverity.MEDIUM -> WarningAmber
                                    NsfwSeverity.LOW -> CyberBorderBright
                                }
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = incident.severity.label.uppercase(),
                            color = if (incident.severity == NsfwSeverity.MEDIUM) Black else White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = incident.sourceApp,
                        color = CyberTextBright,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = dateFormat.format(Date(incident.timestamp)),
                    color = CyberTextDim,
                    fontSize = 11.sp,
                    fontFamily = TechMono
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = incident.mediaName,
                color = CyberTextBright,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = incident.flagReason,
                color = CyberTextDim,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // BLURRED MEDIA PREVIEW CONTAINER
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (incident.isBlurred) CyberBlack else Color(0xFF241517))
                    .border(1.dp, CyberBorder, RoundedCornerShape(6.dp))
                    .clickable { onToggleBlur() },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (incident.isBlurred) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                        contentDescription = null,
                        tint = if (incident.isBlurred) WarningAmber else AlertRed,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (incident.isBlurred) "IMAGE PROTECTED & BLURRED (Tap to unmask)" else "UNMASKED PREVIEW (Tap to re-blur)",
                        color = if (incident.isBlurred) WarningAmber else AlertRed,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "AI Score: ${(incident.confidenceScore * 100).toInt()}% match",
                    color = CyberTextDim,
                    fontSize = 11.sp,
                    fontFamily = TechMono
                )

                TextButton(
                    onClick = onDismiss,
                    colors = ButtonDefaults.textButtonColors(contentColor = CyberTextDim)
                ) {
                    Text("Dismiss Report", fontSize = 12.sp)
                }
            }
        }
    }
}

// ============================================================================
// F8: WEB CATEGORY CONTENT FILTERING COMPOSABLES
// ============================================================================

@Composable
private fun WebFilteringContent(
    rules: List<WebCategoryRule>,
    customDomains: List<CustomDomainRule>,
    testUrlInput: String,
    evaluationResult: WebAccessLog?,
    onToggleCategory: (WebCategory) -> Unit,
    onTestUrlChanged: (String) -> Unit,
    onTestUrl: () -> Unit,
    onClearEvaluation: () -> Unit,
    onRemoveDomain: (String) -> Unit,
    onOpenAddDomain: () -> Unit,
    onNavigateToBlockedLanding: () -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // CATEGORY MATRIX HEADER
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
                            text = "CATEGORY DOMAIN BLOCKER",
                            color = CyberTextDim,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Strict SafeDNS",
                            color = SuccessGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Automated Browser & Web Protection",
                        color = CyberTextBright,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Blocks network connections to websites matching parent-prohibited categories. Enforces SafeSearch parameters across engines.",
                        color = CyberTextDim,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // CATEGORY TOGGLES
        item {
            Text(
                text = "Protected Web Categories",
                color = CyberTextBright,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        items(rules, key = { it.category.name }) { rule ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(CyberCard)
                    .border(1.dp, if (rule.isBlocked) AlertRed.copy(alpha = 0.4f) else CyberBorder, RoundedCornerShape(10.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = rule.category.displayName,
                                color = CyberTextBright,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (rule.blockCount > 0) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(AlertRed.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 1.dp)
                                ) {
                                    Text("${rule.blockCount} hits", color = AlertRed, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Text(
                            text = rule.category.description,
                            color = CyberTextDim,
                            fontSize = 11.sp
                        )
                    }

                    Switch(
                        checked = rule.isBlocked,
                        onCheckedChange = { onToggleCategory(rule.category) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Black,
                            checkedTrackColor = AlertRed,
                            uncheckedThumbColor = CyberTextDim,
                            uncheckedTrackColor = CyberBlack
                        )
                    )
                }
            }
        }

        // CUSTOM WHITELIST / BLACKLIST SECTION
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Custom Whitelist & Blacklist",
                    color = CyberTextBright,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                TextButton(onClick = onOpenAddDomain) {
                    Icon(Icons.Outlined.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Domain", color = White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        items(customDomains, key = { it.id }) { domainRule ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(CyberCard)
                    .border(1.dp, CyberBorder, RoundedCornerShape(8.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (domainRule.isBlocked) AlertRed else SuccessGreen)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (domainRule.isBlocked) "BLOCKED" else "APPROVED",
                                    color = if (domainRule.isBlocked) White else Black,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = domainRule.domain,
                                color = CyberTextBright,
                                fontSize = 13.sp,
                                fontFamily = TechMono,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        if (domainRule.note.isNotBlank()) {
                            Text(text = domainRule.note, color = CyberTextDim, fontSize = 11.sp)
                        }
                    }

                    IconButton(onClick = { onRemoveDomain(domainRule.id) }) {
                        Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = CyberTextDim, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }

        // INTERACTIVE URL TESTER & SIMULATION
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
                    Text(
                        text = "Real-Time URL Simulator",
                        color = CyberTextBright,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Simulate a child web browsing attempt to verify category rules.",
                        color = CyberTextDim,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = testUrlInput,
                        onValueChange = onTestUrlChanged,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("e.g., https://bad-casino.com or wiki.org", color = CyberTextDim, fontSize = 12.sp) },
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = CyberBlack,
                            unfocusedContainerColor = CyberBlack,
                            focusedBorderColor = White,
                            unfocusedBorderColor = CyberBorder,
                            focusedTextColor = CyberTextBright,
                            unfocusedTextColor = CyberTextBright
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onTestUrl,
                            modifier = Modifier.weight(1f).height(40.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = Black)
                        ) {
                            Text("Evaluate URL", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = onNavigateToBlockedLanding,
                            modifier = Modifier.weight(1f).height(40.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AlertRed),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = AlertRed)
                        ) {
                            Text("Preview Child Block Screen", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }

                    if (evaluationResult != null) {
                        val result = evaluationResult
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (result.wasBlocked) AlertRed.copy(alpha = 0.15f) else SuccessGreen.copy(alpha = 0.15f))
                                .border(1.dp, if (result.wasBlocked) AlertRed else SuccessGreen, RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (result.wasBlocked) "BLOCKED ACCESS" else "ACCESS ALLOWED",
                                        color = if (result.wasBlocked) AlertRed else SuccessGreen,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    IconButton(onClick = onClearEvaluation, modifier = Modifier.size(20.dp)) {
                                        Icon(Icons.Outlined.Close, contentDescription = "Close", tint = CyberTextDim)
                                    }
                                }
                                Text("Domain: ${result.domain}", color = CyberTextBright, fontSize = 12.sp, fontFamily = TechMono)
                                Text("Reason: ${result.blockedReason}", color = CyberTextDim, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun AddDomainDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Boolean, String) -> Unit
) {
    var domainInput by remember { mutableStateOf("") }
    var isBlocked by remember { mutableStateOf(true) }
    var noteInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CyberCard,
        title = {
            Text("Add Custom Web Domain", color = CyberTextBright, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = domainInput,
                    onValueChange = { domainInput = it },
                    label = { Text("Domain (e.g. game.xyz)", color = CyberTextDim) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = CyberTextBright,
                        unfocusedTextColor = CyberTextBright,
                        focusedBorderColor = White,
                        unfocusedBorderColor = CyberBorder
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isBlocked) AlertRed else CyberBlack)
                            .border(1.dp, if (isBlocked) AlertRed else CyberBorder, RoundedCornerShape(6.dp))
                            .clickable { isBlocked = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Blacklist", color = White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (!isBlocked) SuccessGreen else CyberBlack)
                            .border(1.dp, if (!isBlocked) SuccessGreen else CyberBorder, RoundedCornerShape(6.dp))
                            .clickable { isBlocked = false },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Whitelist", color = if (!isBlocked) Black else White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                OutlinedTextField(
                    value = noteInput,
                    onValueChange = { noteInput = it },
                    label = { Text("Note / Purpose", color = CyberTextDim) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = CyberTextBright,
                        unfocusedTextColor = CyberTextBright,
                        focusedBorderColor = White,
                        unfocusedBorderColor = CyberBorder
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (domainInput.isNotBlank()) {
                        onConfirm(domainInput, isBlocked, noteInput)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = Black)
            ) {
                Text("Add Rule", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = CyberTextDim)
            }
        }
    )
}
