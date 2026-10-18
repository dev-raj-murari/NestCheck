package com.nestcheck.app.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    
    // Parent routes
    object ParentDashboard : Screen("parent_dashboard")
    object ScreenTime : Screen("screen_time")
    object AppControl : Screen("app_control")
    object Location : Screen("location")
    object ContentFilter : Screen("content_filter")
    object Reports : Screen("reports")
    object Credits : Screen("credits")
    object Settings : Screen("settings")
    object ChildProfile : Screen("child_profile")
    object TimeLimits : Screen("time_limits")
    object Homework : Screen("homework")
    object BedtimeSchedule : Screen("bedtime_schedule")
    object Chores : Screen("chores")
    object AgePresets : Screen("age_presets")
    object CategoryQuotas : Screen("category_quotas")
    object TimeRequests : Screen("time_requests")
    
    // Kid routes
    object KidHome : Screen("kid_home")
    object StepCounter : Screen("step_counter")
    object MyCredits : Screen("my_credits")
    object SOS : Screen("sos")
    object KidProfile : Screen("kid_profile")
    object FocusMode : Screen("focus_mode")
    object KidChores : Screen("kid_chores")
    object KidHomework : Screen("kid_homework")
    object RewardShop : Screen("reward_shop")
    object BedtimeLock : Screen("bedtime_lock")
    object DevicePaused : Screen("device_paused")
    object HabitChallenges : Screen("habit_challenges")
    object WaterReminder : Screen("water_reminder")
}
