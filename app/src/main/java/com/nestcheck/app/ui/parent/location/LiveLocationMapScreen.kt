package com.nestcheck.app.ui.parent.location

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nestcheck.app.data.sync.RealtimeFamilySyncManager
import com.nestcheck.app.ui.parent.shell.ParentShellViewModel

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun LiveLocationMapScreen(
    navController: NavController,
    syncManager: RealtimeFamilySyncManager = hiltViewModel<ParentShellViewModel>().syncManager
) {
    val syncState by syncManager.syncState.collectAsState()
    val context = LocalContext.current

    var showGeofence by remember { mutableStateOf(true) }
    var webViewRef by remember { mutableStateOf<WebView?>(null) }

    val lat = syncState.kidLatitude
    val lon = syncState.kidLongitude
    val childName = syncState.activeChildName
    val childAvatar = if (syncState.activeChildAvatar.isNotBlank()) syncState.activeChildAvatar else "🦊"
    val isGeofenceBreached = syncState.geofenceBreachActive

    val mapHtml = remember(lat, lon, childName, childAvatar, showGeofence, isGeofenceBreached) {
        MapTemplateHelper.buildMpstmeMapHtml(
            lat = lat,
            lon = lon,
            childName = childName,
            childAvatar = childAvatar,
            address = syncState.locationAddress,
            showGeofence = showGeofence,
            isGeofenceBreached = isGeofenceBreached
        )
    }

    val breadcrumbs = remember(lat, lon) {
        listOf(
            Triple("MPSTME Main Academic Block", "Arrived 8:45 AM • Campus Safe Zone", lat + 0.001 to lon + 0.001),
            Triple("Mukesh Patel Library & Labs", "Arrived 11:30 AM • Connected to NMIMS-WiFi", lat - 0.001 to lon + 0.002),
            Triple("Current: MPSTME NMIMS Campus", "Live GPS Tracking • Safe Geofence Active", lat to lon)
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        MpstmeCampusMapView(
            childName = childName,
            childAvatar = childAvatar,
            lat = lat,
            lon = lon,
            locationAddress = syncState.locationAddress,
            showGeofence = showGeofence,
            isGeofenceBreached = isGeofenceBreached
        )
        // Floating Top Header with Back Navigation and Status
        Surface(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(20.dp),
            color = Color.White.copy(alpha = 0.96f),
            shadowElevation = 6.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back", tint = Color(0xFF0F172A))
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${syncState.activeChildName}'s Live Map",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFF0F172A)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF10B981))
                            )
                        }
                        Text(
                            text = syncState.locationAddress,
                            fontSize = 12.sp,
                            color = Color(0xFF64748B),
                            maxLines = 1
                        )
                    }
                }

                IconButton(onClick = {
                    webViewRef?.evaluateJavascript("recenterMap();", null)
                }) {
                    Icon(Icons.Outlined.MyLocation, contentDescription = "Recenter", tint = Color(0xFF0B57D0))
                }
            }
        }

        // Floating Quick Action FABs on Right Side
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            FloatingActionButton(
                onClick = { showGeofence = !showGeofence },
                containerColor = Color.White,
                contentColor = if (showGeofence) Color(0xFF0B57D0) else Color(0xFF64748B),
                modifier = Modifier.size(46.dp)
            ) {
                Icon(Icons.Outlined.Radar, contentDescription = "Toggle Geofence", modifier = Modifier.size(22.dp))
            }

            FloatingActionButton(
                onClick = {
                    val gmmIntentUri = Uri.parse("google.navigation:q=${syncState.kidLatitude},${syncState.kidLongitude}")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    if (mapIntent.resolveActivity(context.packageManager) != null) {
                        context.startActivity(mapIntent)
                    } else {
                        val browserUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=${syncState.kidLatitude},${syncState.kidLongitude}")
                        context.startActivity(Intent(Intent.ACTION_VIEW, browserUri))
                    }
                },
                containerColor = Color(0xFF0B57D0),
                contentColor = Color.White,
                modifier = Modifier.size(46.dp)
            ) {
                Icon(Icons.Outlined.Directions, contentDescription = "Navigate to Child", modifier = Modifier.size(22.dp))
            }
        }

        // Bottom Expandable Child Location Sheet
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFD3E3FD)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(childAvatar, fontSize = 22.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(syncState.activeChildName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0F172A))
                            Text(
                                if (isGeofenceBreached) "${syncState.kidBattery}% ⚡ • 🚨 Boundary Breached" else "${syncState.kidBattery}% ⚡ • Inside Safe Zone",
                                fontSize = 13.sp,
                                color = if (isGeofenceBreached) Color(0xFFEF4444) else Color(0xFF10B981),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Button(
                        onClick = {
                            val gmmIntentUri = Uri.parse("google.navigation:q=${syncState.kidLatitude},${syncState.kidLongitude}")
                            context.startActivity(Intent(Intent.ACTION_VIEW, gmmIntentUri))
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD3E3FD), contentColor = Color(0xFF0B57D0)),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Outlined.Navigation, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Navigate", fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color(0xFFF1F5F9))
                Spacer(modifier = Modifier.height(12.dp))

                Text("Today's Location Timeline", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                Spacer(modifier = Modifier.height(8.dp))

                breadcrumbs.forEach { (place, time, _) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (place.contains("Current")) Color(0xFF10B981) else Color(0xFF94A3B8))
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(place, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Color(0xFF1E293B))
                            Text(time, fontSize = 11.sp, color = Color(0xFF64748B))
                        }
                    }
                }
            }
        }
    }
}
