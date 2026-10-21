# NestCheck - Autonomous Parental Monitoring & Child Protection

**NestCheck** is a next-generation, cyber-minimalist parental telemetry and child wellbeing platform engineered for Android. Built with **Jetpack Compose**, **Kotlin Coroutines / Flow**, **Hilt DI**, and a dual-layer **Firebase Realtime Database & Cloud Firestore** backend, NestCheck delivers instantaneous telemetry synchronization, interactive safety radar, weekly timetable management, in-app emergency alert popups, and a balanced task-and-reward economy.

---

## 👥 Authors & Team
- **Dev Raj Murari** 
- **Rohit Patil** 
- **Sinan**

---

## 📱 Dual-App Architecture

NestCheck packages two synchronized applications within a unified codebase via distinct Android launcher entry points:

```
               ┌────────────────────────────────────────────────────────┐
               │         Firebase Cloud & Realtime Data Fabric          │
               │    (RTDB Low-Latency Pulse + Cloud Firestore Ledger)   │
               └───────────────┬────────────────────────┬───────────────┘
                               │                        │
                               ▼                        ▼
        ┌──────────────────────────────┐        ┌──────────────────────────────┐
        │     NestCheck Parent (NSP)   │        │      NestCheck Kid (NSC)     │
        │ com.nestcheck.app/.ParentMain│        │ com.nestcheck.app/.KidMain   │
        ├──────────────────────────────┤        ├──────────────────────────────┤
        │ • Real-time Safety Radar     │        │ • Live Screen-Time Clock     │
        │ • In-App Alert Popups        │        │ • Instant Check-In Dispatch  │
        │ • Weekly Timetable (Mon-Sat) │        │ • Weekly Calendar View       │
        │ • Remote Device Lock         │        │ • Task & Rewards Economy     │
        │ • Multi-Child Profile Hub    │        │ • Geofence Crossing Sim      │
        │ • Task Review & Rewards      │        │ • 1-Touch Parent Fast-Dial   │
        └──────────────────────────────┘        └──────────────────────────────┘
```

1. **NestCheck Parent (NSP)** (`com.nestcheck.app/.ParentMainActivity`): Comprehensive parental dashboard featuring multi-child switching, real-time safety radar centered at MPSTME Mumbai, weekly timetable sync, in-app modal alerts for breaches and check-ins, and one-tap device locking.
2. **NestCheck Kid (NSC)** (`com.nestcheck.app/.KidMainActivity`): Child dashboard featuring daily time allowances, 2:1 rewards conversion chips, timetable schedule viewer, check-in dispatcher with custom notes, safety simulator, and direct parent SOS fast-dialing.

---

## 🌟 Key Features

### 🚨 1. Real-Time In-App Alert & Modal Engine
- **Geofence Breach Modal**: When a child exits their designated safety perimeter, an instant modal `AlertDialog` appears on the parent's screen showing the child's identity, violated zone, live coordinates, and immediate action buttons (**Track on Map** and **Acknowledge & Dismiss**).
- **Safe Check-In Modal**: When a child initiates a safe arrival check-in, an interactive modal appears displaying the child's custom note, destination label, and timestamp, complete with an **Acknowledge Arrival** action.
- **Dual-Channel Alerting**: Alerts fire both as Android system notifications in the status drawer and as blocking interactive in-app dialogs.

### 🗺️ 2. Live Safety Radar & Geofencing
- **Interactive OpenStreetMap / CARTO Leaflet Engine**: High-performance, battery-efficient map view centered at MPSTME NMIMS Mumbai (`19.1075° N, 72.8372° E`).
- **Dynamic Safety Perimeter**: Visualizes active safety zones with real-time radius adjustments (e.g., 500m campus zone).
- **Crossing Simulation**: Built-in test sandbox allowing parents and children to simulate boundary crossings and test latency.

### 📅 3. Weekly Timetable & Academic Synchronization
- **Monday through Saturday Schedule**: Live academic scheduling covering 6 days a week with customizable time slots, course names, and classroom/faculty details.
- **PDF Upload & Parsing**: Parents can upload timetable documents (such as NMIMS B.Tech IT semester schedules) which instantly parse and synchronize to both parent and kid dashboards.
- **Calendar Integration**: Children see their daily upcoming classes directly from the home screen calendar widget.

### 💎 4. Balanced Rewards & Screen-Time Economy
- **2:1 Credit-to-Time Conversion**: Children redeem earned credits for screen time at an intuitive 2:1 ratio:
  - **10 credits** $\rightarrow$ **+5 minutes** screen time
  - **20 credits** $\rightarrow$ **+10 minutes** screen time
  - **40 credits** $\rightarrow$ **+20 minutes** screen time
- **Task Request & Review Flow**: Children can request tasks or submit completed chores; parents review and grant reward credits in real time.
- **Unified Light/Dark Theme Consistency**: Redesigned cards matching the clean Cyber-Minimalist styling.

### 🔒 5. Remote Device Lock & Bedtime Schedules
- **Instant Kill Switch**: Parents can toggle a remote device lock from the home screen, immediately locking the child's device.
- **Persistent Countdown**: Synchronized remaining screen-time counter with automatic lockout when zero is reached.

### 👨‍👩‍👧‍👦 6. Multi-Child & Household Account Isolation
- **Independent Child Profiles**: Dedicated profiles for Leo (11, Grade 6, 🦊), Olivia (9, Grade 4, 🦄), and Aarav (10, Grade 5, 🚀) with dedicated avatar emojis, isolated telemetry, and independent rule sets.
- **Household Role Switcher**: Seamless switching between Father, Mother, and Guardian accounts.

### 🆘 7. Emergency SOS Fast-Dial
- Direct one-touch SOS dialing connecting directly to the configured parent's mobile phone number (bypassing generic emergency dispatchers for immediate family response).

---

## 🔥 Firebase Dual-Layer Architecture & Logging

NestCheck utilizes both **Firebase Realtime Database (RTDB)** and **Cloud Firestore** for high throughput, sub-second latency, and compliance-grade audit logging.

```
Firebase Root
├── Realtime Database (Live Telemetry & Signals)
│   ├── /children/location                    --> { lat, lng, accuracy, speed, timestamp }
│   ├── /children/device_locked               --> boolean (true / false)
│   ├── /children/remaining_minutes           --> integer (screen-time balance)
│   ├── /children/credits/balance             --> integer (reward credits)
│   ├── /children/geofence_alerts/active      --> { id, childId, zoneName, lat, lng, acknowledged }
│   ├── /children/checkins/latest             --> { checkInId, locationName, note, timestamp }
│   ├── /children/tasks/{taskId}              --> { title, rewardCredits, status, isPendingReview }
│   └── /children/timetable                   --> { pdfName, syncedToCalendar, items: [...] }
│
└── Cloud Firestore (Immutable Audit Trails & History)
    └── families/{familyId}
        ├── geofence_events/{eventId}         --> Historical breach events with GPS trail
        ├── checkin_events/{checkInId}        --> Historical arrival check-ins with notes
        ├── timetable/current                 --> Persistent master academic schedule
        ├── rewards_ledger/{transactionId}    --> Ledger of points earned vs minutes redeemed
        └── audit_logs/{logId}                --> Administrative actions (locks, schedule edits)
```

### What You Can Inspect in the Firebase Console
1. **Realtime Database $\rightarrow$ Data Tab**: Live JSON tree showing real-time updates (flashing orange as location, check-ins, or lock status changes occur).
2. **Cloud Firestore $\rightarrow$ Data Tab**: Searchable, queryable collections of all historical check-ins, geofence violations, and rewards ledger entries.
3. **Authentication $\rightarrow$ Users Tab**: Managed family accounts, parent credentials, and device UIDs.
4. **Google Cloud Logs Explorer**: Function execution logs, network payloads, and rule evaluation traces.

---

## 🏗️ Technology Stack

| Layer | Technologies |
|---|---|
| **Language** | Kotlin 2.0.21 |
| **UI Framework** | Jetpack Compose (1.7.5), Material Design 3 |
| **Architecture** | MVVM (Model-View-ViewModel) + Clean Architecture |
| **Dependency Injection** | Dagger Hilt 2.51.1 |
| **Asynchronous** | Kotlin Coroutines & StateFlow |
| **Local Storage** | Encrypted SharedPreferences / Jetpack DataStore / Room |
| **Mapping & Radar** | Leaflet JS, CARTO Tiles, Android WebKit |
| **Backend & Cloud** | Firebase RTDB, Cloud Firestore, Firebase Authentication, Cloud Messaging |
| **Build System** | Gradle 8.11.1, Android Gradle Plugin 8.7.2, JDK 21+ |

---

## 🚀 Getting Started & Installation

### Prerequisites
- Android Studio Ladybug (2024.2.1+) or newer
- Android SDK 35 (Android 15) with Build Tools 35.0.0
- JDK 21 or JDK 24/25
- Physical Android device or emulator running Android 8.0+ (API 26+)

### 1. Clone Repository
```bash
git clone https://github.com/dev-raj-murari/NestCheck.git
cd NestCheck
```

### 2. Build the Debug APK
```bash
# Windows
gradlew.bat assembleDebug

# macOS / Linux
./gradlew assembleDebug
```

### 3. Install via ADB
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### 4. Launch Either Application
```bash
# Launch NestCheck Parent (NSP):
adb shell am start -n com.nestcheck.app/.ParentMainActivity

# Launch NestCheck Kid (NSC):
adb shell am start -n com.nestcheck.app/.KidMainActivity
```

### 5. Grant Permissions (Optional via ADB)
```bash
adb shell pm grant com.nestcheck.app android.permission.POST_NOTIFICATIONS
adb shell pm grant com.nestcheck.app android.permission.ACCESS_FINE_LOCATION
adb shell pm grant com.nestcheck.app android.permission.ACCESS_COARSE_LOCATION
```

---

## 📁 Project Directory Layout

```
NestCheck/
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml             # Declares dual launcher activities
│       ├── java/com/nestcheck/app/
│       │   ├── data/
│       │   │   ├── local/                  # SharedPreferences & local caches
│       │   │   ├── model/                  # Data classes (Timetable, Geofence, CheckIn)
│       │   │   └── sync/                   # RealtimeFamilySyncManager (Firebase integration)
│       │   ├── di/                         # Hilt dependency injection modules
│       │   ├── ui/
│       │   │   ├── components/             # Reusable Material 3 cards, bars, buttons
│       │   │   ├── kid/                    # NSC screens (Home, Tasks, Calendar, Settings)
│       │   │   ├── parent/                 # NSP screens (Dashboard, Radar, Timetable, Shell)
│       │   │   └── theme/                  # Cyber-minimalist color palettes & typography
│       │   ├── KidMainActivity.kt          # Child entry-point activity
│       │   ├── ParentMainActivity.kt       # Parent entry-point activity
│       │   └── NestCheckApplication.kt     # Application class with Hilt Android App
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

---

## 📄 License
This project is licensed under the [MIT License](LICENSE).
