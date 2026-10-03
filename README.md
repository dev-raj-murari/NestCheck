# NestCheck - Autonomous Parental Monitoring & Child Protection

![MAD Subject Project](https://img.shields.io/badge/MAD-Subject%20Project-blue)
![Platform](https://img.shields.io/badge/Platform-Android-green)
![Architecture](https://img.shields.io/badge/Architecture-MVVM%20%7C%20Clean-orange)

**NestCheck** is a modern, cyber-minimalist Android parental telemetry and child wellbeing monitoring platform built with Jetpack Compose, Kotlin Coroutines, and MVVM Clean Architecture.

**Team**: Dev Raj Murari, Rohit, Sinan

---

## 📱 App Screenshots

### Onboarding, Auth & Registration
| Login | Role Selection | Parent Register (Gmail OTP) | Child Register (Metrics & BMI) |
| :---: | :---: | :---: | :---: |
| <img src="docs/screenshots/login_screen.png" width="220" /> | <img src="docs/screenshots/role_selection.png" width="220" /> | <img src="docs/screenshots/parent_register_otp.png" width="220" /> | <img src="docs/screenshots/child_register.png" width="220" /> |

### Dashboards & Child Device
| Parent Dashboard | Child Home Screen | Reference Design |
| :---: | :---: | :---: |
| <img src="docs/screenshots/parent_dashboard.png" width="240" /> | <img src="docs/screenshots/kid_home.png" width="240" /> | <img src="docs/architecture/06_kid_ui_reference_phone.jpg" width="240" /> |

---

## 📐 Architecture & System Flows

### 1. System Architecture Overview
<img src="docs/architecture/01_system_architecture_overview.png" width="600" />

### 2. Onboarding & Device Pairing Flow
<img src="docs/architecture/02_onboarding_and_pairing_flow.png" width="600" />

### 3. Parent Dashboard Layout
<img src="docs/architecture/03_parent_dashboard_architecture.png" width="600" />

### 4. Child App Layout & Telemetry
<img src="docs/architecture/04_kid_app_architecture.png" width="600" />

### 5. Cross-Device Real-Time Synchronization
<img src="docs/architecture/05_cross_device_realtime_sync.png" width="600" />

---

## 🛠️ Tech Stack
- **Language**: Kotlin 2.1.20
- **UI Toolkit**: Jetpack Compose (Material3 Dark Cyber-Minimalist Theme)
- **Architecture**: MVVM + Clean Architecture + Repository Pattern
- **Dependency Injection**: Hilt (Dagger)
- **Local Storage**: Room DB + DataStore
- **Cloud & Auth**: Firebase Auth, Firestore, FCM (with local offline fallback)
- **Machine Learning**: TensorFlow Lite for On-Device Content Safety
- **Sensors & Location**: Google Play Services Location & Hardware Step Detector

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
   adb shell am start -n com.nestcheck.app/.MainActivity
   ```

---

## 📄 License
MIT License
