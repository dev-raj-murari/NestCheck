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
    object ParentSecurity : Screen("parent_security")
    
    // Kid routes
    object KidHome : Screen("kid_home")
    object StepCounter : Screen("step_counter")
    object MyCredits : Screen("my_credits")
    object SOS : Screen("sos")
    object KidProfile : Screen("kid_profile")
    object MoodCheckIn : Screen("mood_checkin")
}
