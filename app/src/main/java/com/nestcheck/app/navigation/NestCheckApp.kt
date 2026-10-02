package com.nestcheck.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nestcheck.app.ui.child.credits.MyCreditsScreen
import com.nestcheck.app.ui.child.home.KidHomeScreen
import com.nestcheck.app.ui.child.profile.KidProfileScreen
import com.nestcheck.app.ui.child.sos.SOSScreen
import com.nestcheck.app.ui.child.steps.StepCounterScreen
import com.nestcheck.app.ui.common.LoginScreen
import com.nestcheck.app.ui.common.RoleSelectionScreen
import com.nestcheck.app.ui.common.SplashScreen
import com.nestcheck.app.ui.parent.appcontrol.AppControlScreen
import com.nestcheck.app.ui.parent.content.ContentFilterScreen
import com.nestcheck.app.ui.parent.credits.CreditsScreen
import com.nestcheck.app.ui.parent.dashboard.ParentDashboardScreen
import com.nestcheck.app.ui.parent.location.LocationScreen
import com.nestcheck.app.ui.parent.profile.ChildProfileScreen
import com.nestcheck.app.ui.parent.reports.ReportsScreen
import com.nestcheck.app.ui.parent.screentime.ScreenTimeScreen

@Composable
fun NestCheckApp() {
    val navController = rememberNavController()
    
    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        // Common
        composable(Screen.Splash.route) { SplashScreen(navController) }
        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(Screen.RoleSelection.route) { RoleSelectionScreen(navController) }
        
        // Parent
        composable(Screen.ParentDashboard.route) { ParentDashboardScreen(navController) }
        composable(Screen.ScreenTime.route) { ScreenTimeScreen(navController) }
        composable(Screen.AppControl.route) { AppControlScreen(navController) }
        composable(Screen.Location.route) { LocationScreen(navController) }
        composable(Screen.ContentFilter.route) { ContentFilterScreen(navController) }
        composable(Screen.Reports.route) { ReportsScreen(navController) }
        composable(Screen.Credits.route) { CreditsScreen(navController) }
        composable(Screen.ChildProfile.route) { ChildProfileScreen(navController) }
        
        // Kid
        composable(Screen.KidHome.route) { KidHomeScreen(navController) }
        composable(Screen.StepCounter.route) { StepCounterScreen(navController) }
        composable(Screen.MyCredits.route) { MyCreditsScreen(navController) }
        composable(Screen.SOS.route) { SOSScreen(navController) }
        composable(Screen.KidProfile.route) { KidProfileScreen(navController) }
    }
}
