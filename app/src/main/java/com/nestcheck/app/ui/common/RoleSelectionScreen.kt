package com.nestcheck.app.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.SupervisorAccount
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nestcheck.app.navigation.Screen
import com.nestcheck.app.ui.theme.Black
import com.nestcheck.app.ui.theme.GrayLight
import com.nestcheck.app.ui.theme.Typography
import com.nestcheck.app.ui.theme.White

@Composable
fun RoleSelectionScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome to NestCheck",
            style = Typography.displayLarge,
            color = Black
        )
        Text(
            text = "Select your device mode to get started",
            style = Typography.bodyMedium,
            color = Black,
            modifier = Modifier.padding(top = 4.dp, bottom = 40.dp)
        )

        // Parent Role Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(White)
                .border(2.dp, Black, RoundedCornerShape(12.dp))
                .clickable {
                    navController.navigate(Screen.ParentDashboard.route) {
                        popUpTo(Screen.RoleSelection.route) { inclusive = true }
                    }
                }
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.SupervisorAccount,
                    contentDescription = "Parent Mode",
                    tint = Black,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = "Parent App (v1)", style = Typography.headlineMedium, color = Black)
                    Text(text = "Monitor, set rules & view reports", style = Typography.bodyMedium, color = Black)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Kid Role Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(GrayLight)
                .border(2.dp, Black, RoundedCornerShape(12.dp))
                .clickable {
                    navController.navigate(Screen.KidHome.route) {
                        popUpTo(Screen.RoleSelection.route) { inclusive = true }
                    }
                }
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Face,
                    contentDescription = "Kid Mode",
                    tint = Black,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = "Kid App (v2)", style = Typography.headlineMedium, color = Black)
                    Text(text = "Track steps, credits & SOS emergency", style = Typography.bodyMedium, color = Black)
                }
            }
        }
    }
}
