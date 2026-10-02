package com.nestcheck.app.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nestcheck.app.navigation.Screen
import com.nestcheck.app.ui.theme.Black
import com.nestcheck.app.ui.theme.Typography
import com.nestcheck.app.ui.theme.White
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    LaunchedEffect(Unit) {
        delay(1500L) // 1.5 second splash display
        navController.navigate(Screen.Login.route) {
            popUpTo(Screen.Splash.route) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(White),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "NestCheck",
                style = Typography.displayLarge,
                color = Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Keeping families safer online",
                style = Typography.bodyMedium,
                color = Black
            )
        }
    }
}
