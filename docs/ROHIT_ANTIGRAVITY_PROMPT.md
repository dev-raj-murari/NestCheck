# 🤖 Antigravity Autonomous Instruction — Rohit

> **Project:** NestCheck — Autonomous Family Telemetry & Parental Control  
> **Role:** Rohit · Screen Time, Incentives & Control Systems Engineer  
> **Repo:** `https://github.com/dev-raj-murari/NestCheck.git`  
> **Target Branch:** `feature/rohit/<feature-name>` ➔ Merge into `dev`  

---

## ⚡ Trigger Format

Whenever Rohit sends you any prompt in this format:
> **`hey i'm rohit push my <feature> feature on the code and github`**  
*(e.g., `hey i'm rohit push my F1 feature on the code and github` OR `hey i'm rohit push my Screen time dashboard feature on the code and github`)*

You MUST execute the full autonomous development loop:
1. **Identify the exact feature** from the 15 features table below.
2. **Implement the feature files** (UI, ViewModel, Repository, and Data Model).
3. **Verify the build**: Run `.\gradlew.bat assembleDebug` and ensure `BUILD SUCCESSFUL`.
4. **Git Commit & Push**:
   ```bash
   git checkout -B feature/rohit/<feature-slug>
   git add -A
   git commit -m "feat(rohit): implement <F-ID> - <feature-name>"
   git push origin feature/rohit/<feature-slug>
   ```
5. Report back with the list of created files, preview instructions, and git commit hash.

---

## 📋 Rohit's All 15 Features

| # | Code | Feature Name | Description | Key Target Files |
|---|:---:|---|---|---|
| 1 | **F1** | Screen Time Dashboard | Per-app usage, bar charts, daily/weekly/monthly breakdown | `ui/parent/screentime/ScreenTimeScreen.kt` |
| 2 | **F2** | Daily Time Limits | Per-day-of-week limits (Sun–Sat) with countdown on Kid app | `ui/parent/screentime/DailyLimitsScreen.kt` |
| 3 | **F25** | Age Presets | Auto-recommend daily limits based on kid's age (e.g., 6–10y: 1.5h) | `core/AgePresets.kt` |
| 4 | **F3** | App Management | List installed apps, toggle block/allow, set per-app caps | `ui/parent/appcontrol/AppControlScreen.kt` |
| 5 | **F27** | Category Limits | Limit entire groups (Games 1h, Social Media 30m, Study Unlimited) | `ui/parent/appcontrol/CategoryLimitsScreen.kt` |
| 6 | **F4** | Credit System | Parent awards credits (1 credit = 5 min bonus time), ledger | `ui/parent/credits/CreditsScreen.kt` |
| 7 | **F23** | Reward Shop | Kid can spend earned credits on rewards (+30m games, 1h movie) | `ui/child/credits/RewardShopScreen.kt` |
| 8 | **F5** | Homework Check-off | Parent marks school homework done ➔ kid automatically gets +6 credits | `ui/parent/credits/HomeworkCheckScreen.kt` |
| 9 | **F6** | Bedtime Schedule | Auto-lock during sleep hours (e.g., 9:30 PM to 6:30 AM) | `ui/parent/screentime/BedtimeScheduleScreen.kt` |
| 10 | **F24** | One-Tap Pause | Instant pause button to halt all screen time immediately | `ui/parent/screentime/OneTapPauseScreen.kt` |
| 11 | **F26** | Ask for More Time | Kid sends "+15 min request" ➔ Parent gets notification to approve/deny | `ui/child/home/TimeRequestDialog.kt` |
| 12 | **F21** | Focus / Study Mode | Lock all apps except whitelisted study/calculator tools during study hours | `ui/parent/appcontrol/FocusModeScreen.kt` |
| 13 | **F22** | Chore Chart | Daily checklist of household chores earning bonus credits | `ui/parent/credits/ChoreChartScreen.kt` |
| 14 | **F28** | Weekly Challenge | Goal for the week (e.g. read 50 pages) with bonus reward | `ui/parent/credits/WeeklyChallengeScreen.kt` |
| 15 | **F40** | Water & Sleep Reminders | Timed hydration prompts & bedtime wind-down notifications for kid | `ui/child/profile/WellnessReminders.kt` |

---

## 🎨 UI Style Standards (Must Follow)
- **Palette**: Dark cyber-minimalist (`CyberBlack = #0A0C10`, `CyberCard = #12151B`, `CyberBorder = #2A2E39`, `White`, `AlertRed`).
- **Typography**: Sharp, high-contrast monospace headers (`TechMono`) with simple, human-readable labels.
- **Components**: Rounded corner cards (`8.dp`), thin borders (`1.dp`), high-contrast buttons.
