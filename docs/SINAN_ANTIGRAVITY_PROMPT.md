# 🤖 Antigravity Autonomous Instruction — Sinan

> **Project:** NestCheck — Autonomous Family Telemetry & Parental Control  
> **Role:** Sinan · Content Safety, AI Vision & Geospatial Engineer  
> **Repo:** `https://github.com/dev-raj-murari/NestCheck.git`  
> **Target Branch:** `feature/sinan/<feature-name>` ➔ Merge into `dev`  

---

## ⚡ Trigger Format

Whenever Sinan sends you any prompt in this format:
> **`hey i'm sinan push my <feature> feature on the code and github`**  
*(e.g., `hey i'm sinan push my F7 feature on the code and github` OR `hey i'm sinan push my NSFW detection feature on the code and github`)*

You MUST execute the full autonomous development loop:
1. **Identify the exact feature** from the 15 features table below.
2. **Implement the feature files** (UI, ViewModel, Repository, and Data Model).
3. **Verify the build**: Run `.\gradlew.bat assembleDebug` and ensure `BUILD SUCCESSFUL`.
4. **Git Commit & Push**:
   ```bash
   git checkout -B feature/sinan/<feature-slug>
   git add -A
   git commit -m "feat(sinan): implement <F-ID> - <feature-name>"
   git push origin feature/sinan/<feature-slug>
   ```
5. Report back with the list of created files, preview instructions, and git commit hash.

---

## 📋 Sinan's All 15 Features

| # | Code | Feature Name | Description | Key Target Files |
|---|:---:|---|---|---|
| 1 | **F7** | NSFW Detection | On-device TensorFlow Lite image scanner with parent alert feed | `ui/parent/content/NsfwDetector.kt` |
| 2 | **F8** | Web Category Filtering | Block adult, gambling, social, violence websites by category | `ui/parent/content/ContentFilterScreen.kt` |
| 3 | **F33** | Safe Search Lock | Force strict Google, Bing & YouTube SafeSearch on kid browser | `core/SafeSearchManager.kt` |
| 4 | **F10** | Geofencing | Set virtual fences around School (MPSTME) & Home (DN Nagar) | `ui/parent/location/GeofenceManager.kt` |
| 5 | **F29** | Reached School/Home Alert | Push notifications when child enters or leaves school or home | `data/model/GeofenceEvent.kt` |
| 6 | **F11** | Live Location Map | Real-time Google Map with child pin, accuracy circle & address | `ui/parent/location/LocationScreen.kt` |
| 7 | **F36** | "I'm Here" Check-In | Kid taps one button on Kid home to share arrival timestamp | `ui/child/home/CheckInDialog.kt` |
| 8 | **F12** | Location History | Timeline of locations visited by kid throughout the day | `ui/parent/location/LocationHistoryScreen.kt` |
| 9 | **F34** | Low-Battery Last Ping | Auto-broadcasts last known GPS fix when battery drops below 15% | `core/LowBatteryBeacon.kt` |
| 10 | **F9** | App Install Approval | Kid requests new app install ➔ Parent gets 1-tap approve/deny | `ui/parent/appcontrol/AppApprovalScreen.kt` |
| 11 | **F35** | App Permission Audit | Scans kid device for apps with dangerous permissions (Mic/Camera) | `ui/parent/appcontrol/PermissionAuditScreen.kt` |
| 12 | **F30** | Fast Travel Alert | Flags if child speed exceeds 60 km/h (riding in moving car/train) | `core/SpeedMonitor.kt` |
| 13 | **F31** | Trusted Contacts | Whitelist of contacts child is permitted to call or message | `ui/parent/profile/TrustedContactsScreen.kt` |
| 14 | **F32** | Bad-Word / Bullying Alert | Scans incoming notifications for harassment or explicit language | `core/BullyingDetector.kt` |
| 15 | **F41** | Emergency Contacts | 1-tap dial list for parents, guardians and local police/ambulance | `ui/child/sos/EmergencyContactsScreen.kt` |

---

## 🎨 UI Style Standards (Must Follow)
- **Palette**: Dark cyber-minimalist (`CyberBlack = #0A0C10`, `CyberCard = #12151B`, `CyberBorder = #2A2E39`, `White`, `AlertRed`).
- **Typography**: Sharp, high-contrast monospace headers (`TechMono`) with simple, human-readable labels.
- **Components**: Rounded corner cards (`8.dp`), thin borders (`1.dp`), high-contrast buttons.
