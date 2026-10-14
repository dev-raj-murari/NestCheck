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
    }
}
