package com.nestcheck.app.ui.child.sos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nestcheck.app.ui.theme.AlertRed
import com.nestcheck.app.ui.theme.Typography

@Composable
fun SOSScreen(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(AlertRed, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("SOS", style = Typography.displayLarge, color = Color.White)
        }
    }
}
