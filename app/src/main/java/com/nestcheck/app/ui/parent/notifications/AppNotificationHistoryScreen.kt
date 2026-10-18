package com.nestcheck.app.ui.parent.notifications

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.*
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
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class AppNotificationRecord(
    val id: String,
    val appName: String,
    val packageName: String,
    val title: String,
    val message: String,
    val timestamp: Long,
    val category: String,
    val iconColor: Color,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNotificationHistoryScreen(
    navController: NavController
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val initialNotifications = remember {
        mutableStateListOf(
            AppNotificationRecord(
                id = "n1",
                appName = "WhatsApp",
                packageName = "com.whatsapp",
                title = "Study Group (Class 8B)",
                message = "Dev: Did anyone finish the Chapter 4 algebra worksheet?",
                timestamp = System.currentTimeMillis() - 12 * 60 * 1000,
                category = "Messaging",
                iconColor = Color(0xFF25D366),
                icon = Icons.Outlined.Chat
            ),
            AppNotificationRecord(
                id = "n2",
                appName = "YouTube",
                packageName = "com.google.android.youtube",
                title = "CrashCourse Science",
                message = "New upload: How Photosynthesis Actually Works in Plants",
                timestamp = System.currentTimeMillis() - 45 * 60 * 1000,
                category = "Entertainment",
                iconColor = Color(0xFFFF0000),
                icon = Icons.Outlined.PlayCircle
            ),
            AppNotificationRecord(
                id = "n3",
                appName = "Roblox",
                packageName = "com.roblox.client",
                title = "Roblox Alert",
                message = "Friend request accepted from Alex_Builds99",
                timestamp = System.currentTimeMillis() - 95 * 60 * 1000,
                category = "Gaming",
                iconColor = Color(0xFF00A2FF),
                icon = Icons.Outlined.SportsEsports
            ),
            AppNotificationRecord(
                id = "n4",
                appName = "Instagram",
                packageName = "com.instagram.android",
                title = "Direct Message",
                message = "Liam sent a reel: Top 10 football dribbles 2026",
                timestamp = System.currentTimeMillis() - 140 * 60 * 1000,
                category = "Social",
                iconColor = Color(0xFFE1306C),
                icon = Icons.Outlined.CameraAlt
            ),
            AppNotificationRecord(
                id = "n5",
                appName = "Google Classroom",
                packageName = "com.google.android.apps.classroom",
                title = "Mrs. Henderson assigned homework",
                message = "History Essay: Causes of Industrial Revolution due Friday 5 PM",
                timestamp = System.currentTimeMillis() - 210 * 60 * 1000,
                category = "Education",
                iconColor = Color(0xFF0F9D58),
                icon = Icons.Outlined.School
            ),
            AppNotificationRecord(
                id = "n6",
                appName = "Discord",
                packageName = "com.discord",
                title = "#gaming-lounge",
                message = "Voice channel started: Join Minecraft Realm now",
                timestamp = System.currentTimeMillis() - 320 * 60 * 1000,
                category = "Social",
                iconColor = Color(0xFF5865F2),
                icon = Icons.Outlined.Forum
            ),
            AppNotificationRecord(
                id = "n7",
                appName = "Messages",
                packageName = "com.google.android.apps.messaging",
                title = "Dad",
                message = "Remember to pack your soccer cleats before school tomorrow!",
                timestamp = System.currentTimeMillis() - 400 * 60 * 1000,
                category = "Messaging",
                iconColor = Color(0xFF1A73E8),
                icon = Icons.Outlined.Sms
            )
        )
    }

    val categories = listOf("All", "Messaging", "Social", "Gaming", "Education", "Entertainment")

    val filteredList = initialNotifications.filter { item ->
        val matchesCategory = selectedCategory == "All" || item.category.equals(selectedCategory, ignoreCase = true)
        val matchesSearch = searchQuery.isBlank() ||
                item.appName.contains(searchQuery, ignoreCase = true) ||
                item.title.contains(searchQuery, ignoreCase = true) ||
                item.message.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    val timeFormat = remember { SimpleDateFormat("h:mm a", Locale.getDefault()) }

    Scaffold(
        containerColor = Color(0xFFF8FAFC),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("App Notification History", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                        Text("Track apps sending notifications to child", fontSize = 12.sp, color = Color(0xFF64748B))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back", tint = Color(0xFF0F172A))
                    }
                },
                actions = {
                    IconButton(onClick = { initialNotifications.clear() }) {
                        Icon(Icons.Outlined.DeleteSweep, contentDescription = "Clear History", tint = Color(0xFFEF4444))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                placeholder = { Text("Search by app name or keyword...") },
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, tint = Color(0xFF64748B)) },
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color(0xFF0B57D0),
                    unfocusedBorderColor = Color(0xFFE2E8F0)
                )
            )

            // Category Filter Pills
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    val isSelected = selectedCategory == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat, fontSize = 13.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFD3E3FD),
                            selectedLabelColor = Color(0xFF0B57D0),
                            containerColor = Color.White,
                            labelColor = Color(0xFF64748B)
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) Color(0xFF0B57D0) else Color(0xFFE2E8F0)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Notifications List
            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Outlined.NotificationsNone,
                            contentDescription = null,
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No notifications found", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A), fontSize = 16.sp)
                        Text("Captured notifications from child apps will appear here", color = Color(0xFF64748B), fontSize = 13.sp)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredList, key = { it.id }) { notif ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(notif.iconColor.copy(alpha = 0.15f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = notif.icon,
                                                contentDescription = notif.appName,
                                                tint = notif.iconColor,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(notif.appName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0F172A))
                                            Text(notif.category, fontSize = 11.sp, color = Color(0xFF64748B))
                                        }
                                    }
                                    Text(
                                        text = timeFormat.format(Date(notif.timestamp)),
                                        fontSize = 12.sp,
                                        color = Color(0xFF94A3B8),
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = notif.title,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF1E293B)
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = notif.message,
                                    fontSize = 13.sp,
                                    color = Color(0xFF475569),
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
