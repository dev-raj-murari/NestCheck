package com.nestcheck.app.ui.parent.notifications

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
import androidx.compose.material.icons.outlined.BatteryAlert
import androidx.compose.material.icons.outlined.DoneAll
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.DirectionsWalk
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nestcheck.app.data.model.NotificationType
import com.nestcheck.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NotificationHubScreen(
    navController: NavController,
    viewModel: NotificationHubViewModel = hiltViewModel()
) {
    val notifications by viewModel.filteredNotifications.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val dateFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }

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
                    text = "Notification Hub",
                    color = CyberTextBright,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Row {
                IconButton(onClick = viewModel::markAllAsRead) {
                    Icon(
                        imageVector = Icons.Outlined.DoneAll,
                        contentDescription = "Mark All Read",
                        tint = CyberTextBright
                    )
                }
                IconButton(onClick = viewModel::clearAll) {
                    Icon(
                        imageVector = Icons.Outlined.DeleteOutline,
                        contentDescription = "Clear All",
                        tint = CyberTextDim
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // FILTER PILLS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NotificationFilter.values().forEach { filter ->
                val isSelected = filter == selectedFilter
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) White else CyberCard)
                        .border(1.dp, if (isSelected) White else CyberBorder, RoundedCornerShape(8.dp))
                        .clickable { viewModel.setFilter(filter) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = filter.name,
                        color = if (isSelected) Black else CyberTextDim,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // NOTIFICATIONS LIST
        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("No notifications in this filter", color = CyberTextDim, fontSize = 13.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(notifications, key = { it.id }) { notif ->
                    val (icon, tint) = getNotificationIcon(notif.type)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (notif.isRead) CyberCard else CyberCard.copy(alpha = 0.95f))
                            .border(
                                width = 1.dp,
                                color = if (!notif.isRead) tint else CyberBorder,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable { viewModel.markAsRead(notif.id) }
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(tint.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = notif.title,
                                        color = CyberTextBright,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = dateFormat.format(Date(notif.timestamp)),
                                        color = CyberTextDim,
                                        fontSize = 11.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = notif.message,
                                    color = CyberTextDim,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = "Dismiss",
                                tint = CyberTextDim,
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable { viewModel.dismiss(notif.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun getNotificationIcon(type: NotificationType): Pair<ImageVector, Color> {
    return when (type) {
        NotificationType.SOS -> Pair(Icons.Outlined.Warning, AlertRed)
        NotificationType.LOW_BATTERY -> Pair(Icons.Outlined.BatteryAlert, AlertRed)
        NotificationType.NSFW_ALERT -> Pair(Icons.Outlined.Warning, AlertRed)
        NotificationType.DEVICE_TAMPER -> Pair(Icons.Outlined.Warning, WarningAmber)
        NotificationType.GEOFENCE_ENTER -> Pair(Icons.Outlined.LocationOn, SuccessGreen)
        NotificationType.GEOFENCE_EXIT -> Pair(Icons.Outlined.LocationOn, WarningAmber)
        NotificationType.STEP_GOAL_COMPLETE -> Pair(Icons.Outlined.DirectionsWalk, SuccessGreen)
        NotificationType.SCREEN_TIME_EXCEEDED -> Pair(Icons.Outlined.HourglassEmpty, WarningAmber)
        NotificationType.HOMEWORK_COMPLETE -> Pair(Icons.Outlined.DoneAll, SuccessGreen)
        else -> Pair(Icons.Outlined.Info, CyberTextBright)
    }
}
