package com.nestcheck.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nestcheck.app.ui.auth.ParentLoginScreen
import com.nestcheck.app.ui.auth.KidLoginScreen
import com.nestcheck.app.ui.common.LoginScreen
import com.nestcheck.app.ui.auth.FamilyAuthScreen
import com.nestcheck.app.ui.common.SplashScreen
import com.nestcheck.app.ui.parent.dashboard.ParentDashboardScreen
import com.nestcheck.app.ui.parent.shell.ParentShellScreen
import com.nestcheck.app.ui.child.shell.KidShellScreen
import com.nestcheck.app.ui.parent.screentime.ScreenTimeScreen
import com.nestcheck.app.ui.parent.screentime.TimeLimitScreen
import com.nestcheck.app.ui.parent.appcontrol.AppControlScreen
import com.nestcheck.app.ui.parent.credits.CreditsScreen
import com.nestcheck.app.ui.parent.profile.ChildProfileScreen
import com.nestcheck.app.ui.parent.homework.HomeworkScreen
import com.nestcheck.app.ui.parent.schedule.ScheduleScreen
import com.nestcheck.app.ui.parent.chores.ChoresScreen
import com.nestcheck.app.ui.parent.presets.AgePresetsScreen
import com.nestcheck.app.ui.parent.quotas.CategoryQuotasScreen
import com.nestcheck.app.ui.parent.requests.TimeRequestsScreen
import com.nestcheck.app.ui.parent.security.ParentPinSecurityScreen
import com.nestcheck.app.ui.parent.battery.BatteryMonitorScreen
import com.nestcheck.app.ui.parent.notifications.NotificationHubScreen
import com.nestcheck.app.ui.parent.sync.OfflineSyncScreen
import com.nestcheck.app.ui.parent.reports.ReportsScreen
import com.nestcheck.app.ui.parent.ai.AIDigestScreen
import com.nestcheck.app.ui.parent.content.ContentFilterScreen
import com.nestcheck.app.ui.parent.location.LocationScreen
import com.nestcheck.app.ui.sinan.AppQuarantineScreen
import com.nestcheck.app.ui.sinan.BlockedUrlLandingScreen
import com.nestcheck.app.ui.sinan.ChildCheckInScreen
import com.nestcheck.app.ui.parent.lock.RemoteDeviceLockScreen
import com.nestcheck.app.ui.parent.dashboard.MultiChildDashboardScreen
import com.nestcheck.app.ui.parent.ai.AIParentingAssistantScreen
import com.nestcheck.app.ui.parent.family.FamilySharingScreen
import com.nestcheck.app.ui.parent.settings.LanguageSettingsScreen
import com.nestcheck.app.ui.sinan.TrustedContactsScreen
import com.nestcheck.app.ui.parent.location.LiveLocationMapScreen
import com.nestcheck.app.ui.parent.notifications.AppNotificationHistoryScreen
import com.nestcheck.app.ui.sinan.SafeSearchScreen
import com.nestcheck.app.ui.sinan.LowBatteryPingScreen
import com.nestcheck.app.ui.sinan.PermissionAuditorScreen
import com.nestcheck.app.ui.sinan.EmergencyHubScreen
import com.nestcheck.app.ui.child.home.KidHomeScreen
import com.nestcheck.app.ui.child.profile.KidProfileScreen
import com.nestcheck.app.ui.child.credits.MyCreditsScreen
import com.nestcheck.app.ui.child.steps.StepCounterScreen
import com.nestcheck.app.ui.child.mood.MoodCheckInScreen
import com.nestcheck.app.ui.child.sos.SOSScreen
import com.nestcheck.app.ui.child.chores.KidChoresScreen
import com.nestcheck.app.ui.child.homework.KidHomeworkScreen
import com.nestcheck.app.ui.child.focus.FocusModeScreen
import com.nestcheck.app.ui.child.rewards.RewardShopScreen
import com.nestcheck.app.ui.child.lock.BedtimeLockScreen
import com.nestcheck.app.ui.child.lock.DevicePausedScreen
import com.nestcheck.app.ui.child.streaks.HabitStreaksScreen
import com.nestcheck.app.ui.child.health.WaterReminderScreen
import com.nestcheck.app.ui.parent.timetable.TimetableScreen

@Composable
fun NestCheckApp(startDestination: String = Screen.Splash.route) {
    val navController = rememberNavController()
    
    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Splash.route) { SplashScreen(navController) }
        composable(Screen.Login.route) { FamilyAuthScreen(navController) }
        composable(Screen.ParentLogin.route) { ParentLoginScreen(navController) }
        composable(Screen.KidLogin.route) { KidLoginScreen(navController) }
        composable(Screen.ParentDashboard.route) { ParentShellScreen(navController) }
        
        // Rohit's Feature Routes (F1-F6, F21-F28, F40)
        composable(Screen.ScreenTime.route) { ScreenTimeScreen(navController) }
        composable(Screen.TimeLimits.route) { TimeLimitScreen(navController) }
        composable(Screen.AppControl.route) { AppControlScreen(navController) }
        composable(Screen.Credits.route) { CreditsScreen(navController) }
        composable(Screen.Homework.route) { HomeworkScreen(navController) }
        composable(Screen.BedtimeSchedule.route) { ScheduleScreen(navController) }
        composable(Screen.Chores.route) { ChoresScreen(navController) }
        composable(Screen.AgePresets.route) { AgePresetsScreen(navController) }
        composable(Screen.CategoryQuotas.route) { CategoryQuotasScreen(navController) }
        composable(Screen.TimeRequests.route) { TimeRequestsScreen(navController) }
        composable(Screen.FocusMode.route) { FocusModeScreen(navController) }
        composable(Screen.KidChores.route) { KidChoresScreen(navController) }
        composable(Screen.KidHomework.route) { KidHomeworkScreen(navController) }
        composable(Screen.RewardShop.route) { RewardShopScreen(navController) }
        composable(Screen.BedtimeLock.route) { BedtimeLockScreen(navController) }
        composable(Screen.DevicePaused.route) { DevicePausedScreen(navController) }
        composable(Screen.HabitChallenges.route) { HabitStreaksScreen(navController) }
        composable(Screen.WaterReminder.route) { WaterReminderScreen(navController) }

        // Dev Raj's Feature Routes (F13, F45, F14, F37, F15, F16, F18, F44, F17, F38, F19, F20, F39, F42, F43)
        composable(Screen.ChildProfile.route) { ChildProfileScreen(navController) }
        composable(Screen.ParentSecurity.route) { ParentPinSecurityScreen(navController) }
        composable(Screen.StepCounter.route) { StepCounterScreen(navController) }
        composable(Screen.MoodCheckIn.route) { MoodCheckInScreen(navController) }
        composable(Screen.SOS.route) { SOSScreen(navController) }
        composable(Screen.BatteryMonitor.route) { BatteryMonitorScreen(navController) }
        composable(Screen.NotificationsHub.route) { NotificationHubScreen(navController) }
        composable(Screen.OfflineSync.route) { OfflineSyncScreen(navController) }
        composable(Screen.Reports.route) { ReportsScreen(navController) }
        composable(Screen.AIDigest.route) { AIDigestScreen(navController) }
        composable(Screen.RemoteLock.route) { RemoteDeviceLockScreen(navController) }
        composable(Screen.MultiChild.route) { MultiChildDashboardScreen(navController) }
        composable(Screen.AIAssistant.route) { AIParentingAssistantScreen(navController) }
        composable(Screen.FamilySharing.route) { FamilySharingScreen(navController) }
        composable(Screen.LanguageSettings.route) { LanguageSettingsScreen(navController) }
        composable(Screen.Timetable.route) { TimetableScreen(navController) }

        // Kid Core Routes
        composable(Screen.KidHome.route) { KidShellScreen(navController) }
        composable(Screen.KidProfile.route) { KidProfileScreen(navController) }
        composable(Screen.MyCredits.route) { MyCreditsScreen(navController) }

        // Sinan's Feature Routes (F7, F8, F9, F10, F36)
        composable(Screen.ContentFilter.route) { ContentFilterScreen(navController) }
        composable(Screen.Location.route) { LocationScreen(navController) }
        composable(Screen.AppQuarantine.route) { AppQuarantineScreen(navController) }
        composable(Screen.BlockedUrl.route) { BlockedUrlLandingScreen(navController) }
        composable(Screen.ChildCheckIn.route) { ChildCheckInScreen(navController) }
        composable(Screen.TrustedContacts.route) { TrustedContactsScreen(navController) }
        composable(Screen.NotificationHistory.route) { AppNotificationHistoryScreen(navController) }
        composable(Screen.LiveMap.route) { LiveLocationMapScreen(navController) }
        composable(Screen.SafeSearch.route) { SafeSearchScreen(navController) }
        composable(Screen.LowBatteryPing.route) { LowBatteryPingScreen(navController) }
        composable(Screen.PermissionAuditor.route) { PermissionAuditorScreen(navController) }
        composable(Screen.EmergencyHub.route) { EmergencyHubScreen(navController) }
    }
}
