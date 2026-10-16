package com.nestcheck.app.ui.parent.sync

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CloudDone
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.CloudSync
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material.icons.outlined.WifiOff
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
import com.nestcheck.app.data.repository.SyncItemStatus
import com.nestcheck.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OfflineSyncScreen(
    navController: NavController,
    viewModel: OfflineSyncViewModel = hiltViewModel()
) {
    val engineState by viewModel.engineState.collectAsState()
    val queueItems by viewModel.queueItems.collectAsState()
    val timeFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }

    val pendingCount = queueItems.count { it.status == SyncItemStatus.PENDING }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBlack)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp)
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
                    text = "Offline Sync Queue",
                    color = CyberTextBright,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (engineState.isOnline) SuccessGreen.copy(alpha = 0.15f) else AlertRed.copy(alpha = 0.15f))
                    .border(1.dp, if (engineState.isOnline) SuccessGreen else AlertRed, RoundedCornerShape(20.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (engineState.isOnline) "ONLINE" else "OFFLINE",
                    color = if (engineState.isOnline) SuccessGreen else AlertRed,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // SYNC ENGINE STATUS CARD
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
                    Column {
                        Text(
                            text = if (engineState.isSyncing) "SYNCHRONIZING TELEMETRY..." else "$pendingCount EVENTS IN LOCAL QUEUE",
                            color = if (pendingCount > 0) WarningAmber else SuccessGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Last Synced: ${timeFormat.format(Date(engineState.lastSyncTimestamp))}",
                            color = CyberTextDim,
                            fontSize = 11.sp
                        )
                    }

                    if (engineState.isSyncing) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp, color = White)
                    } else {
                        IconButton(onClick = viewModel::triggerManualSync, enabled = engineState.isOnline) {
                            Icon(Icons.Outlined.Refresh, contentDescription = "Sync Now", tint = if (engineState.isOnline) White else CyberTextDim)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action buttons: toggle network simulation & add mock event
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.toggleNetwork(!engineState.isOnline) },
                        modifier = Modifier.weight(1f).height(38.dp),
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberTextBright)
                    ) {
                        Icon(if (engineState.isOnline) Icons.Outlined.WifiOff else Icons.Outlined.Wifi, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (engineState.isOnline) "Cut Network" else "Connect Net", fontSize = 11.sp)
                    }

                    Button(
                        onClick = viewModel::addMockEvent,
                        modifier = Modifier.weight(1f).height(38.dp),
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = Black)
                    ) {
                        Text("+ Queue Event", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // QUEUE ITEMS LIST
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("LOCAL QUEUE LEDGER", color = CyberTextDim, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(
                text = "Clear Synced",
                color = White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { viewModel.clearSynced() }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (queueItems.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("All local telemetry is synchronized with cloud.", color = CyberTextDim, fontSize = 13.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(queueItems, key = { it.id }) { item ->
                    val isPending = item.status == SyncItemStatus.PENDING
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(CyberCard)
                            .border(1.dp, if (isPending) WarningAmber.copy(alpha = 0.5f) else CyberBorder, RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.eventType,
                                    color = CyberTextBright,
                                    fontSize = 13.sp,
                                    fontFamily = TechMono,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = item.payloadPreview,
                                    color = CyberTextDim,
                                    fontSize = 11.sp
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (isPending) WarningAmber.copy(alpha = 0.15f) else SuccessGreen.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = item.status.name,
                                    color = if (isPending) WarningAmber else SuccessGreen,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
