package com.nestcheck.app.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nestcheck.app.navigation.Screen
import com.nestcheck.app.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    LaunchedEffect(Unit) {
        delay(800L)
        val targetRoute = if (com.nestcheck.app.BuildConfig.APP_ROLE == "KID") {
            Screen.KidHome.route
        } else {
            Screen.Login.route
        }
        navController.navigate(targetRoute) {
            popUpTo(Screen.Splash.route) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBlack),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "NestCheck",
            color = CyberTextBright,
            fontSize = 36.sp,
            fontFamily = TechMono,
            fontWeight = FontWeight.Bold
        )
    }
}
