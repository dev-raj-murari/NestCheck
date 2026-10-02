package com.nestcheck.app.ui.parent.screentime

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.nestcheck.app.ui.theme.Typography

@Composable
fun ScreenTimeScreen(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Screen Time Management - Coming Soon", style = Typography.titleMedium)
    }
}
