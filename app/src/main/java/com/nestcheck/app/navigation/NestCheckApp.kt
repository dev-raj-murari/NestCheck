package com.nestcheck.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nestcheck.app.ui.common.LoginScreen
import com.nestcheck.app.ui.common.SplashScreen
import com.nestcheck.app.ui.parent.screentime.ScreenTimeScreen
import com.nestcheck.app.ui.parent.screentime.TimeLimitScreen
import com.nestcheck.app.ui.parent.appcontrol.AppControlScreen
import com.nestcheck.app.ui.parent.credits.CreditsScreen
import com.nestcheck.app.ui.child.credits.MyCreditsScreen

import com.nestcheck.app.ui.parent.security.ParentPinSecurityScreen
import com.nestcheck.app.ui.child.steps.StepCounterScreen
import com.nestcheck.app.ui.child.mood.MoodCheckInScreen
import com.nestcheck.app.ui.child.sos.SOSScreen

@Composable
fun NestCheckApp() {
    val navController = rememberNavController()
    
    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) { SplashScreen(navController) }
        composable(Screen.Login.route) { LoginScreen(navController) }
        
        // Rohit's Feature Routes (F1, F2, F3, F4)
        composable(Screen.ScreenTime.route) { ScreenTimeScreen(navController) }
        composable(Screen.TimeLimits.route) { TimeLimitScreen(navController) }
        composable(Screen.AppControl.route) { AppControlScreen(navController) }
        composable(Screen.Credits.route) { CreditsScreen(navController) }
        composable(Screen.MyCredits.route) { MyCreditsScreen(navController) }

        // Dev Raj's Feature Routes (F45, F14, F37, F15)
        composable(Screen.ParentSecurity.route) { ParentPinSecurityScreen(navController) }
        composable(Screen.StepCounter.route) { StepCounterScreen(navController) }
        composable(Screen.MoodCheckIn.route) { MoodCheckInScreen(navController) }
        composable(Screen.SOS.route) { SOSScreen(navController) }
    }
}
