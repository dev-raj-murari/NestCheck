package com.nestcheck.app.ui.parent.location

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MpstmeCampusMapView(
    childName: String,
    childAvatar: String,
    lat: Double = 19.1075,
    lon: Double = 72.8372,
    locationAddress: String = "MPSTME NMIMS, Vile Parle West, Mumbai, Maharashtra",
    showGeofence: Boolean = true,
    isGeofenceBreached: Boolean = false,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Interactive Pan & Zoom State
    var zoomScale by remember { mutableFloatStateOf(1.0f) }
    var panOffset by remember { mutableStateOf(Offset.Zero) }

    // Pulsing radar animation
    val infiniteTransition = rememberInfiniteTransition(label = "RadarTransition")
    val radarRadius by infiniteTransition.animateFloat(
        initialValue = 35f,
        targetValue = 135f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RadarRadius"
    )
    val radarAlpha by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RadarAlpha"
    )

    val safeThemeColor = if (isGeofenceBreached) Color(0xFFEF4444) else Color(0xFF0B57D0)
    val safeFillColor = if (isGeofenceBreached) Color(0x33EF4444) else Color(0x220B57D0)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF1F5F9))
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    zoomScale = (zoomScale * zoom).coerceIn(0.6f, 2.5f)
                    panOffset += pan
                }
            }
    ) {
        // High-Precision Vector Canvas for MPSTME Campus & Vile Parle West
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f + panOffset.x, size.height * 0.42f + panOffset.y)

            // 1. Street Grid & Background Tiles
            val gridSize = 70f * zoomScale
            var x = -gridSize * 4
            while (x < size.width + gridSize * 4) {
                drawLine(
                    color = Color(0xFFE2E8F0),
                    start = Offset(x, 0f),
                    end = Offset(x, size.height),
                    strokeWidth = 1.5f
                )
                x += gridSize
            }
            var y = -gridSize * 4
            while (y < size.height + gridSize * 4) {
                drawLine(
                    color = Color(0xFFE2E8F0),
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1.5f
                )
                y += gridSize
            }

            // 2. Major Roads (Swami Vivekanand Road & V.L. Mehta Road)
            // SV Road (Diagonal Road)
            val svRoadWidth = 44f * zoomScale
            drawLine(
                color = Color.White,
                start = Offset(-100f, center.y - 120f * zoomScale),
                end = Offset(size.width + 100f, center.y + 100f * zoomScale),
                strokeWidth = svRoadWidth
            )
            drawLine(
                color = Color(0xFFCBD5E1),
                start = Offset(-100f, center.y - 120f * zoomScale - svRoadWidth / 2f),
                end = Offset(size.width + 100f, center.y + 100f * zoomScale - svRoadWidth / 2f),
                strokeWidth = 2f
            )
            drawLine(
                color = Color(0xFFCBD5E1),
                start = Offset(-100f, center.y - 120f * zoomScale + svRoadWidth / 2f),
                end = Offset(size.width + 100f, center.y + 100f * zoomScale + svRoadWidth / 2f),
                strokeWidth = 2f
            )

            // V.L. Mehta Road (Cross road)
            val vlRoadWidth = 36f * zoomScale
            drawLine(
                color = Color.White,
                start = Offset(center.x - 70f * zoomScale, -100f),
                end = Offset(center.x - 20f * zoomScale, size.height + 100f),
                strokeWidth = vlRoadWidth
            )

            // 3. MPSTME NMIMS Campus Zone Polygon / Rounded Rect
            val campusW = 260f * zoomScale
            val campusH = 170f * zoomScale
            val campusTopLeft = Offset(center.x - campusW / 2f, center.y - campusH / 2f)

            // Campus Green Ground
            drawRoundRect(
                color = Color(0xFFDCFCE7),
                topLeft = campusTopLeft,
                size = Size(campusW, campusH),
                cornerRadius = CornerRadius(24f * zoomScale, 24f * zoomScale)
            )
            drawRoundRect(
                color = Color(0xFF86EFAC),
                topLeft = campusTopLeft,
                size = Size(campusW, campusH),
                cornerRadius = CornerRadius(24f * zoomScale, 24f * zoomScale),
                style = Stroke(
                    width = 2.5f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f), 0f)
                )
            )

            // 4. Safe Geofence Perimeter Circle (350m radius)
            if (showGeofence) {
                val geofenceRadiusPx = 220f * zoomScale
                drawCircle(
                    color = safeFillColor,
                    center = center,
                    radius = geofenceRadiusPx
                )
                drawCircle(
                    color = safeThemeColor,
                    center = center,
                    radius = geofenceRadiusPx,
                    style = Stroke(
                        width = 3f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 14f), 0f)
                    )
                )
            }

            // 5. Radar Pulse Rings
            drawCircle(
                color = safeThemeColor.copy(alpha = radarAlpha),
                center = center,
                radius = radarRadius * zoomScale,
                style = Stroke(width = 2.5f)
            )
        }

        // Surrounding Landmarks Pills (Overlayed)
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 24.dp, top = 90.dp)
                .background(Color.White, RoundedCornerShape(8.dp))
                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text("📍 NMIMS University", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155))
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 24.dp, top = 90.dp)
                .background(Color.White, RoundedCornerShape(8.dp))
                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text("🏫 Mithibai College", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155))
        }

        // Center Campus Label & Child Avatar Marker
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(x = (panOffset.x / 2.7).dp, y = ((panOffset.y - 180) / 2.7).dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Status Badge Above Pin
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF0F172A),
                    shadowElevation = 6.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isGeofenceBreached) Color(0xFFEF4444) else Color(0xFF10B981))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isGeofenceBreached) "🚨 GEOFENCE BREACH: $childName" else "Safe: $childName @ MPSTME",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Avatar Pin
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 8.dp,
                    border = androidx.compose.foundation.BorderStroke(3.5.dp, safeThemeColor),
                    modifier = Modifier.size(54.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = if (childAvatar.isNotBlank()) childAvatar else "🦊",
                            fontSize = 28.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Campus Badge
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFDCFCE7),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF86EFAC))
                ) {
                    Text(
                        text = "MPSTME NMIMS CAMPUS (Safe Zone)",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF166534),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }

        // Live Telemetry Info Box (Top Left below header)
        Surface(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 16.dp, top = 135.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color.White.copy(alpha = 0.94f),
            shadowElevation = 4.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    "GPS: ${String.format("%.4f", lat)}° N, ${String.format("%.4f", lon)}° E",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Text(
                    "Vile Parle West, Mumbai (±4m)",
                    fontSize = 10.sp,
                    color = Color(0xFF64748B)
                )
            }
        }

        // Map Control Buttons (Top Right)
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 16.dp, top = 135.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                modifier = Modifier
                    .size(38.dp)
                    .clickable { zoomScale = (zoomScale + 0.2f).coerceAtMost(2.5f) },
                shape = RoundedCornerShape(10.dp),
                color = Color.White,
                shadowElevation = 4.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.Add, contentDescription = "Zoom In", tint = Color(0xFF0F172A), modifier = Modifier.size(20.dp))
                }
            }

            Surface(
                modifier = Modifier
                    .size(38.dp)
                    .clickable { zoomScale = (zoomScale - 0.2f).coerceAtLeast(0.6f) },
                shape = RoundedCornerShape(10.dp),
                color = Color.White,
                shadowElevation = 4.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.Remove, contentDescription = "Zoom Out", tint = Color(0xFF0F172A), modifier = Modifier.size(20.dp))
                }
            }

            Surface(
                modifier = Modifier
                    .size(38.dp)
                    .clickable {
                        zoomScale = 1.0f
                        panOffset = Offset.Zero
                    },
                shape = RoundedCornerShape(10.dp),
                color = Color.White,
                shadowElevation = 4.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.MyLocation, contentDescription = "Recenter", tint = Color(0xFF0B57D0), modifier = Modifier.size(20.dp))
                }
            }

            Surface(
                modifier = Modifier
                    .size(38.dp)
                    .clickable {
                        val gmmIntentUri = Uri.parse("geo:$lat,$lon?q=MPSTME+NMIMS+Mumbai")
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        mapIntent.setPackage("com.google.android.apps.maps")
                        if (mapIntent.resolveActivity(context.packageManager) != null) {
                            context.startActivity(mapIntent)
                        } else {
                            val webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=$lat,$lon")
                            context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
                        }
                    },
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF0B57D0),
                shadowElevation = 4.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.Map, contentDescription = "Google Maps", tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}
