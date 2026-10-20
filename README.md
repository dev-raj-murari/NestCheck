# NestCheck - Autonomous Parental Monitoring & Child Protection

![MAD Subject Project](https://img.shields.io/badge/MAD-Subject%20Project-blue)
![Platform](https://img.shields.io/badge/Platform-Android-green)
![Architecture](https://img.shields.io/badge/Architecture-MVVM%20%7C%20Clean-orange)

**NestCheck** is a modern, cyber-minimalist and authentic Google Family Link Android parental telemetry and child wellbeing monitoring platform built with Jetpack Compose, Kotlin Coroutines, and MVVM Clean Architecture.

**Team**: Dev Raj Murari, Rohit ([@Rohit-Patil768](https://github.com/Rohit-Patil768)), Sinan

---

## 📱 Real-Time Dual-App Architecture

NestCheck features dual independent launchers operating in real-time synchronization:
1. **NestCheck Parent (NSP)** - `com.nestcheck.app/.ParentMainActivity`: Google Family Link dashboard, multi-child switcher, household parent account switcher (Father/Mother/Guardian), interactive Leaflet OpenStreetMap safety radar centered at MPSTME Mumbai, and perimeter breach alert hub.
2. **NestCheck Child (NSC)** - `com.nestcheck.app/.KidMainActivity`: Kid dashboard, interactive avatar selection (10 presets), habits/screen-time allowances, MPSTME geofence crossing simulator, and SOS emergency fast-dial dispatcher directly calling parents (replaces 112).

---

## 🛠️ Key Safety Features

- **Real-Time Data Sync**: Bidirectional state synchronization between child telemetry and parent dashboard.
- **Child Avatar Customization**: 10 preset emojis and avatars synced across all interfaces.
- **TopBar Family Controls**: Top-left child selector dropdown and top-right parent switcher dropdown (Father, Mother, Guardian) with parent profile viewer.
- **Flexible Parent Onboarding**: Household configuration supporting Father, Mother, and Guardian (minimum one required) with emergency fast-dial telephone setup.
- **MPSTME Geofence Perimeter Alerting**: Geofence tracking centered at MPSTME NMIMS Mumbai (`19.1075° N, 72.8372° E`) with real-time exit/breach push alerts.
- **Emergency SOS Fast-Dial**: Child SOS broadcast instantly dials the parent's phone number directly (not 112) and triggers high-priority parental notification alarms.
- **Reliable Map Rendering**: Interactive Leaflet / OpenStreetMap CARTO engine with custom avatar marker and geofence perimeter circle.

---

## 🚀 Setup & Build Instructions

1. Clone the repository:
   ```bash
   git clone https://github.com/dev-raj-murari/NestCheck.git
   cd NestCheck
   ```
2. Build the debug APK:
   ```bash
   ./gradlew assembleDebug
   ```
3. Run on connected Android device or emulator:
   ```bash
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   # Launch Parent App (NSP):
   adb shell am start -n com.nestcheck.app/.ParentMainActivity
   # Launch Kid App (NSC):
   adb shell am start -n com.nestcheck.app/.KidMainActivity
   ```

---

## 📄 License
MIT License
