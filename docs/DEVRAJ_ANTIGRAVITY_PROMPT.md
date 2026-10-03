# 🤖 Antigravity Autonomous Instruction — Dev Raj

> **Project:** NestCheck — Autonomous Family Telemetry & Parental Control  
> **Role:** Dev Raj · Project Lead · Health Telemetry, Accounts & Sentinel Engineer  
> **Repo:** `https://github.com/dev-raj-murari/NestCheck.git`  
> **Target Branch:** `feature/devraj/<feature-name>` ➔ Merge into `dev` ➔ `main`  

---

## ⚡ Trigger Format

Whenever Dev Raj sends you any prompt in this format:
> **`hey i'm devraj push my <feature> feature on the code and github`**  
*(e.g., `hey i'm devraj push my F15 feature on the code and github` OR `hey i'm devraj push my SOS button feature on the code and github`)*

You MUST execute the full autonomous development loop:
1. **Identify the exact feature** from Dev Raj's 15 features table below.
2. **Implement the feature files** (UI, ViewModel, Repository, and Data Model).
3. **Verify the build**: Run `.\gradlew.bat assembleDebug` and ensure `BUILD SUCCESSFUL`.
4. **Git Commit & Push**:
   ```bash
   git checkout -B feature/devraj/<feature-slug>
   git add -A
   git commit -m "feat(devraj): implement <F-ID> - <feature-name>"
   git push origin feature/devraj/<feature-slug>
   ```
5. Report back with the list of created files, preview instructions, and git commit hash.

---

## 📋 Dev Raj's All 15 Features

| # | Code | Feature Name | Status | Key Target Files |
|---|:---:|---|:---:|---|
| 1 | **F13** | Child Profile Setup | ✅ COMPLETED | `ui/parent/profile/ChildProfileScreen.kt` |
| 2 | **F45** | Parent PIN / Biometrics | Pending | `ui/parent/profile/PinLockDialog.kt` |
| 3 | **F14** | Step Counter + BMI Goal | ✅ COMPLETED | `ui/child/steps/StepCounterScreen.kt` |
| 4 | **F37** | Daily Mood Check-In | Pending | `ui/child/profile/MoodCheckInScreen.kt` |
| 5 | **F15** | SOS Panic Button | ✅ Scaffolded | `ui/child/sos/SOSScreen.kt` |
| 6 | **F44** | Offline Sync Queue | Pending | `data/repository/OfflineSyncManager.kt` |
| 7 | **F16** | Battery & Device Monitor | Pending | `ui/parent/profile/DeviceStatusCard.kt` |
| 8 | **F18** | Notifications Hub | Pending | `ui/parent/reports/NotificationsHubScreen.kt` |
| 9 | **F17** | Activity Reports | Pending | `ui/parent/reports/ReportsScreen.kt` |
| 10 | **F38** | AI Daily Digest | Pending | `ui/parent/reports/AiDailyDigestScreen.kt` |
| 11 | **F19** | Remote Device Lock | ✅ Scaffolded | `ui/parent/dashboard/ParentDashboardScreen.kt` |
| 12 | **F20** | Multi-Child Switcher | Pending | `ui/parent/profile/MultiChildSwitcher.kt` |
| 13 | **F42** | Co-Parent Invite | Pending | `ui/parent/profile/CoParentInviteScreen.kt` |
| 14 | **F39** | AI Parenting Assistant | Pending | `ui/parent/profile/AiAssistantScreen.kt` |
| 15 | **F43** | Multi-Language Support | Pending | `res/values-hi/strings.xml` |

---

## 🎨 UI Style Standards (Must Follow)
- **Palette**: Dark cyber-minimalist (`CyberBlack = #0A0C10`, `CyberCard = #12151B`, `CyberBorder = #2A2E39`, `White`, `AlertRed`).
- **Typography**: Sharp, high-contrast monospace headers (`TechMono`) with simple, human-readable labels.
- **Components**: Rounded corner cards (`8.dp`), thin borders (`1.dp`), high-contrast buttons.
