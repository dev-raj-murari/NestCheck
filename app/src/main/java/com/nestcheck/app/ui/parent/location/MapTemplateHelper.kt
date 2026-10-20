package com.nestcheck.app.ui.parent.location

object MapTemplateHelper {

    fun buildMpstmeMapHtml(
        lat: Double = 19.1075,
        lon: Double = 72.8372,
        childName: String = "Leo",
        childAvatar: String = "\uD83E\uDD8A",
        address: String = "MPSTME NMIMS, Vile Parle West, Mumbai, Maharashtra",
        showGeofence: Boolean = true,
        isGeofenceBreached: Boolean = false
    ): String {
        val safeColor = if (isGeofenceBreached) "#EF4444" else "#0B57D0"
        val safeFill = if (isGeofenceBreached) "rgba(239,68,68,0.22)" else "rgba(11,87,208,0.18)"
        val statusText = if (isGeofenceBreached) "GEOFENCE BREACH DETECTED" else "INSIDE SAFE GEOFENCE (350m)"
        val statusBadgeColor = if (isGeofenceBreached) "#EF4444" else "#10B981"

        return """
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; user-select: none; }
        html, body { width: 100%; height: 100%; overflow: hidden; font-family: -apple-system, Roboto, BlinkMacSystemFont, "Segoe UI", sans-serif; background: #E8ECEF; }
        #map-container { position: relative; width: 100%; height: 100%; overflow: hidden; background: #F0F4F8; }
        
        /* Map Canvas & Vector Layers */
        .grid-layer {
            position: absolute; width: 200%; height: 200%; left: -50%; top: -50%;
            background-color: #F1F4F9;
            background-image:
                linear-gradient(#E2E8F0 1.5px, transparent 1.5px),
                linear-gradient(90deg, #E2E8F0 1.5px, transparent 1.5px);
            background-size: 60px 60px;
            transition: transform 0.2s ease-out;
        }

        /* Roads */
        .road-major-1 {
            position: absolute; left: 0; right: 0; top: 46%; height: 32px;
            background: #FFFFFF; border-top: 2.5px solid #CBD5E1; border-bottom: 2.5px solid #CBD5E1;
            transform: rotate(-7deg); display: flex; align-items: center; justify-content: center;
        }
        .road-major-2 {
            position: absolute; top: 0; bottom: 0; left: 48%; width: 28px;
            background: #FFFFFF; border-left: 2.5px solid #CBD5E1; border-right: 2.5px solid #CBD5E1;
            transform: rotate(4deg); display: flex; align-items: center; justify-content: center;
        }
        .road-label {
            font-size: 11px; font-weight: 700; color: #64748B; letter-spacing: 0.5px; text-transform: uppercase;
        }

        /* Campus Grounds */
        .campus-block {
            position: absolute; left: 34%; top: 38%; width: 34%; height: 26%;
            background: #DCFCE7; border: 2px dashed #86EFAC; border-radius: 16px;
            box-shadow: 0 4px 12px rgba(16, 185, 129, 0.08);
            display: flex; flex-direction: column; align-items: center; justify-content: center; text-align: center;
            padding: 8px;
        }
        .campus-title { font-size: 12px; font-weight: 800; color: #166534; }
        .campus-sub { font-size: 9px; font-weight: 600; color: #15803D; margin-top: 2px; }

        /* Surrounding Landmarks */
        .landmark-nmims {
            position: absolute; left: 12%; top: 22%; padding: 6px 10px;
            background: #FFFFFF; border: 1.5px solid #E2E8F0; border-radius: 8px;
            font-size: 10px; font-weight: 700; color: #334155; box-shadow: 0 2px 6px rgba(0,0,0,0.06);
        }
        .landmark-mithibai {
            position: absolute; right: 12%; top: 24%; padding: 6px 10px;
            background: #FFFFFF; border: 1.5px solid #E2E8F0; border-radius: 8px;
            font-size: 10px; font-weight: 700; color: #334155; box-shadow: 0 2px 6px rgba(0,0,0,0.06);
        }
        .landmark-cooper {
            position: absolute; left: 15%; bottom: 20%; padding: 6px 10px;
            background: #FFFFFF; border: 1.5px solid #E2E8F0; border-radius: 8px;
            font-size: 10px; font-weight: 700; color: #DC2626; box-shadow: 0 2px 6px rgba(0,0,0,0.06);
        }
        .landmark-juhu {
            position: absolute; right: 14%; bottom: 22%; padding: 6px 10px;
            background: #FFFFFF; border: 1.5px solid #E2E8F0; border-radius: 8px;
            font-size: 10px; font-weight: 700; color: #2563EB; box-shadow: 0 2px 6px rgba(0,0,0,0.06);
        }

        /* Geofence Circle */
        .geofence-circle {
            position: absolute; left: 50%; top: 50%;
            width: 280px; height: 280px; margin-left: -140px; margin-top: -140px;
            border-radius: 50%;
            border: 2.5px dashed $safeColor;
            background: $safeFill;
            display: ${if (showGeofence) "block" else "none"};
            pointer-events: none;
            box-shadow: 0 0 24px rgba(11,87,208,0.12);
        }

        /* Radar Ring Animation */
        .radar-pulse {
            position: absolute; left: 50%; top: 50%;
            width: 60px; height: 60px; margin-left: -30px; margin-top: -30px;
            border-radius: 50%;
            border: 2px solid $safeColor;
            animation: pulse-ring 2.4s cubic-bezier(0.215, 0.61, 0.355, 1) infinite;
            pointer-events: none;
        }
        @keyframes pulse-ring {
            0% { transform: scale(0.6); opacity: 0.9; }
            70% { transform: scale(2.6); opacity: 0.0; }
            100% { transform: scale(2.8); opacity: 0.0; }
        }

        /* Child Marker */
        .child-marker {
            position: absolute; left: 50%; top: 50%;
            width: 52px; height: 52px; margin-left: -26px; margin-top: -26px;
            border-radius: 50%;
            background: #FFFFFF;
            border: 3.5px solid $safeColor;
            box-shadow: 0 6px 18px rgba(11,87,208,0.35);
            display: flex; align-items: center; justify-content: center;
            font-size: 26px; z-index: 10;
            cursor: pointer;
            transition: transform 0.15s ease;
        }
        .child-marker:active { transform: scale(0.92); }

        /* Floating Info Badge */
        .floating-badge {
            position: absolute; left: 50%; top: calc(50% - 64px);
            transform: translateX(-50%);
            background: #1E293B; color: #FFFFFF;
            padding: 5px 12px; border-radius: 20px;
            font-size: 11px; font-weight: 700;
            white-space: nowrap; box-shadow: 0 4px 12px rgba(0,0,0,0.25);
            z-index: 12; display: flex; align-items: center; gap: 6px;
        }
        .status-dot {
            width: 8px; height: 8px; border-radius: 50%; background: $statusBadgeColor;
        }

        /* GPS Telemetry Card */
        .telemetry-card {
            position: absolute; top: 12px; left: 12px;
            background: rgba(255,255,255,0.95); backdrop-filter: blur(8px);
            border: 1px solid #E2E8F0; border-radius: 12px;
            padding: 8px 12px; font-size: 11px; color: #334155;
            box-shadow: 0 2px 8px rgba(0,0,0,0.06); z-index: 15;
            line-height: 1.4;
        }
        .telemetry-bold { font-weight: 700; color: #0F172A; }

        /* Controls */
        .map-ctrl-box {
            position: absolute; right: 14px; top: 12px;
            display: flex; flex-direction: column; gap: 8px; z-index: 15;
        }
        .ctrl-btn {
            width: 36px; height: 36px; border-radius: 10px;
            background: #FFFFFF; border: 1.5px solid #E2E8F0;
            display: flex; align-items: center; justify-content: center;
            font-size: 18px; font-weight: bold; color: #334155;
            box-shadow: 0 2px 6px rgba(0,0,0,0.08); cursor: pointer;
        }
    </style>
</head>
<body>
    <div id="map-container">
        <!-- Background Vector Grid -->
        <div id="grid-layer" class="grid-layer"></div>

        <!-- Major Streets of Vile Parle West -->
        <div class="road-major-1">
            <span class="road-label">Swami Vivekanand (S.V.) Road &bull; Vile Parle</span>
        </div>
        <div class="road-major-2">
            <span class="road-label" style="transform: rotate(90deg);">V.L. Mehta Road</span>
        </div>

        <!-- Landmarks -->
        <div class="landmark-nmims">&bull; NMIMS Deemed University</div>
        <div class="landmark-mithibai">&bull; Mithibai College Campus</div>
        <div class="landmark-cooper">&bull; R.N. Cooper Hospital</div>
        <div class="landmark-juhu">&bull; Juhu Circle Junction</div>

        <!-- MPSTME Campus Safe Zone -->
        <div class="campus-block">
            <div class="campus-title">MPSTME NMIMS CAMPUS</div>
            <div class="campus-sub">Mukesh Patel School of Tech &bull; Safe Zone</div>
        </div>

        <!-- Safe Geofence Boundary -->
        <div class="geofence-circle"></div>

        <!-- Radar Pulse -->
        <div class="radar-pulse"></div>

        <!-- Floating Child Pin & Status -->
        <div class="floating-badge">
            <span class="status-dot"></span>
            <span>$childName &bull; $statusText</span>
        </div>
        <div class="child-marker" onclick="recenter()">
            $childAvatar
        </div>

        <!-- Telemetry Details -->
        <div class="telemetry-card">
            <div><span class="telemetry-bold">Target:</span> $childName ($childAvatar)</div>
            <div><span class="telemetry-bold">Coords:</span> ${String.format("%.4f", lat)}° N, ${String.format("%.4f", lon)}° E</div>
            <div><span class="telemetry-bold">Zone:</span> MPSTME Mumbai (350m Safe Area)</div>
        </div>

        <!-- Zoom Controls -->
        <div class="map-ctrl-box">
            <div class="ctrl-btn" onclick="zoomIn()">+</div>
            <div class="ctrl-btn" onclick="zoomOut()">&minus;</div>
            <div class="ctrl-btn" onclick="recenter()" title="Recenter">&#x2316;</div>
        </div>
    </div>

    <script>
        var scale = 1.0;
        function zoomIn() {
            scale = Math.min(scale + 0.15, 1.6);
            document.getElementById('grid-layer').style.transform = 'scale(' + scale + ')';
        }
        function zoomOut() {
            scale = Math.max(scale - 0.15, 0.7);
            document.getElementById('grid-layer').style.transform = 'scale(' + scale + ')';
        }
        function recenter() {
            scale = 1.0;
            document.getElementById('grid-layer').style.transform = 'scale(1.0)';
        }
    </script>
</body>
</html>
        """.trimIndent()
    }
}
