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
import com.nestcheck.app.data.model.SafeSearchEngineRule
import com.nestcheck.app.data.model.SafeSearchQueryLog
import com.nestcheck.app.data.model.SearchEngineType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SafeSearchScreen(
    navController: NavController,
    viewModel: SafeSearchViewModel = hiltViewModel()
) {
    val config by viewModel.config.collectAsState()
    val logs by viewModel.logs.collectAsState()
    val query by viewModel.searchQuery
    val selectedEngine by viewModel.selectedEngine
    val lastResult by viewModel.lastResult

    val bgColor = Color(0xFF0A0D14)
    val cardColor = Color(0xFF121824)
    val cyanAccent = Color(0xFF00E5FF)
    val greenSuccess = Color(0xFF00E676)
    val textMuted = Color(0xFF94A3B8)
    val borderStroke = Color(0xFF1E293B)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "SafeSearch Lock",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "System-Wide Strict Content Filtering",
                            fontSize = 12.sp,
                            color = textMuted
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
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
                            text = "F33 ACTIVE",
                            color = cyanAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = bgColor
                )
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
            // Status Hero Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(borderStroke))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(greenSuccess.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = greenSuccess,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "System-Wide Enforcement Active",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "DNS & HTTP rewriting guarantees unfiltered adult content is blocked",
                                    fontSize = 12.sp,
                                    color = textMuted
                                )
                            }
                        }

                        HorizontalDivider(color = borderStroke)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Filtered Queries",
                                    fontSize = 11.sp,
                                    color = textMuted
                                )
                                Text(
                                    text = "${config.totalQueriesFiltered}",
                                    fontWeight = FontWeight.Bold,
                                    color = cyanAccent,
                                    fontSize = 18.sp
                                )
                            }
                            Column {
                                Text(
                                    text = "Tamper Lock",
                                    fontSize = 11.sp,
                                    color = textMuted
                                )
                                Text(
                                    text = if (config.isTamperLockEnabled) "ENFORCED" else "UNLOCKED",
                                    fontWeight = FontWeight.Bold,
                                    color = if (config.isTamperLockEnabled) greenSuccess else Color.Red,
                                    fontSize = 14.sp
                                )
                            }
                            Column {
                                Text(
                                    text = "Incognito Mode",
                                    fontSize = 11.sp,
                                    color = textMuted
                                )
                                Text(
                                    text = if (config.blockIncognitoBrowsing) "BLOCKED" else "ALLOWED",
                                    fontWeight = FontWeight.Bold,
                                    color = if (config.blockIncognitoBrowsing) greenSuccess else Color.Yellow,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }

            // Master Protection Controls
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(borderStroke))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "Hardware & System Controls",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 14.sp
                        )

                        // Tamper Lock Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Tamper-Proof Lock",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Prevents turning off SafeSearch without Parent PIN",
                                    color = textMuted,
                                    fontSize = 11.sp
                                )
                            }
                            Switch(
                                checked = config.isTamperLockEnabled,
                                onCheckedChange = { viewModel.toggleTamperLock(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = cyanAccent,
                                    uncheckedThumbColor = textMuted,
                                    uncheckedTrackColor = Color(0xFF1E293B)
                                )
                            )
                        }

                        HorizontalDivider(color = borderStroke)

                        // Incognito Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Block Incognito / Private Browsing",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Forces all web lookups through inspected child profile",
                                    color = textMuted,
                                    fontSize = 11.sp
                                )
                            }
                            Switch(
                                checked = config.blockIncognitoBrowsing,
                                onCheckedChange = { viewModel.toggleBlockIncognito(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = cyanAccent,
                                    uncheckedThumbColor = textMuted,
                                    uncheckedTrackColor = Color(0xFF1E293B)
                                )
                            )
                        }

                        HorizontalDivider(color = borderStroke)

                        // YouTube Strict Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Strict YouTube Restricted Mode",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Filters mature videos and comments on YouTube",
                                    color = textMuted,
                                    fontSize = 11.sp
                                )
                            }
                            Switch(
                                checked = config.enforceRestrictedYouTubeMode,
                                onCheckedChange = { viewModel.toggleYouTubeRestricted(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = cyanAccent,
                                    uncheckedThumbColor = textMuted,
                                    uncheckedTrackColor = Color(0xFF1E293B)
                                )
                            )
                        }
                    }
                }
            }

            // Enforced Search Engines List
            item {
                Text(
                    text = "Enforced Search Engines",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 14.sp
                )
            }

            items(config.engines) { rule: SafeSearchEngineRule ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(borderStroke))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = rule.engine.displayName,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                                Surface(
                                    color = Color(0xFF1E293B),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "${rule.requestsIntercepted} filtered",
                                        fontSize = 10.sp,
                                        color = cyanAccent,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Rule: ${rule.engine.safeParam}=${rule.engine.safeValue} on ${rule.engine.hostDomain}",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = textMuted
                            )
                        }

                        Switch(
                            checked = rule.isEnforced,
                            onCheckedChange = { viewModel.toggleEngine(rule.engine, it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = cyanAccent,
                                uncheckedThumbColor = textMuted,
                                uncheckedTrackColor = Color(0xFF1E293B)
                            )
                        )
                    }
                }
            }

            // Live URL Rewriter & Test Harness
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(borderStroke))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = cyanAccent,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Live SafeSearch URL Rewriter Simulator",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }

                        Text(
                            text = "Test how incoming browser queries are intercepted and rewritten in real-time.",
                            fontSize = 12.sp,
                            color = textMuted
                        )

                        // Quick Presets
                        Text(
                            text = "Quick Presets:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = textMuted
                        )
                        val presets = listOf(
                            "photosynthesis cycle diagram",
                            "minecraft redstone tutorial",
                            "french revolution history notes",
                            "funny animal video clips"
                        )
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(presets) { preset ->
                                Surface(
                                    color = Color(0xFF1E293B),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier.clickable {
                                        viewModel.simulateSearch(preset)
                                    }
                                ) {
                                    Text(
                                        text = preset,
                                        fontSize = 11.sp,
                                        color = cyanAccent,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        // Search engine selector chips
                        Text(
                            text = "Select Engine:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = textMuted
                        )
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(SearchEngineType.values()) { eng ->
                                val isSelected = eng == selectedEngine
                                Surface(
                                    color = if (isSelected) cyanAccent.copy(alpha = 0.2f) else Color(0xFF1E293B),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .clickable { viewModel.onSelectEngine(eng) }
                                        .border(
                                            width = 1.dp,
                                            color = if (isSelected) cyanAccent else Color.Transparent,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                ) {
                                    Text(
                                        text = eng.displayName,
                                        fontSize = 11.sp,
                                        color = if (isSelected) cyanAccent else Color.White,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        // Input field
                        OutlinedTextField(
                            value = query,
                            onValueChange = { viewModel.onQueryChange(it) },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Enter search query...", color = textMuted) },
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = cyanAccent,
                                unfocusedBorderColor = borderStroke,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = cyanAccent
                            )
                        )

                        Button(
                            onClick = { viewModel.simulateSearch() },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = cyanAccent)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Simulate & Intercept URL",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Simulation Result Box
                        lastResult?.let { res ->
                            Surface(
                                color = Color(0xFF0F172A),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, cyanAccent.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "INTERCEPTION RESULT",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = greenSuccess
                                        )
                                        Text(
                                            text = res.engine.displayName,
                                            fontSize = 11.sp,
                                            color = textMuted
                                        )
                                    }

                                    Text(
                                        text = "Original URL:",
                                        fontSize = 11.sp,
                                        color = textMuted
                                    )
                                    Text(
                                        text = res.originalUrl,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = Color.White.copy(alpha = 0.8f),
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Spacer(modifier = Modifier.height(2.dp))

                                    Text(
                                        text = "Rewritten & Locked URL:",
                                        fontSize = 11.sp,
                                        color = cyanAccent,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = res.rewrittenUrl,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = cyanAccent,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Recent Interception Audit Log
            item {
                Text(
                    text = "Recent Interception Audit Trail",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 14.sp
                )
            }

            items(logs) { log ->
                val timeStr = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(log.timestamp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(borderStroke))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    color = cyanAccent.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = log.engine.displayName,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = cyanAccent,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Text(
                                    text = "\"${log.query}\"",
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White,
                                    fontSize = 13.sp
                                )
                            }
                            Text(
                                text = timeStr,
                                fontSize = 11.sp,
                                color = textMuted
                            )
                        }

                        Text(
                            text = log.rewrittenUrl,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = textMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
