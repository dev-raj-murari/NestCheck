package com.nestcheck.app.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.SupervisorAccount
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nestcheck.app.navigation.Screen
import com.nestcheck.app.ui.theme.*

@Composable
fun RoleSelectionScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBlack)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Select Role",
            style = Typography.displayLarge,
            color = CyberTextBright,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Who is using this device?",
            color = CyberTextDim,
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 4.dp, bottom = 32.dp)
        )

        // Parent Role Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(CyberCard)
                .border(1.dp, CyberBorder, RoundedCornerShape(8.dp))
                .clickable {
                    navController.navigate(Screen.ParentDashboard.route)
                }
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.SupervisorAccount,
                    contentDescription = "Parent",
                    tint = CyberTextBright,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "I'm a Parent",
                        color = CyberTextBright,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Access Parent Dashboard, manage profiles, rules and safety.",
                        color = CyberTextDim,
                        fontSize = 13.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Kid Role Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(CyberCard)
                .border(1.dp, CyberBorder, RoundedCornerShape(8.dp))
                .clickable {
                    navController.navigate(Screen.KidHome.route)
                }
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Face,
                    contentDescription = "Kid",
                    tint = CyberTextBright,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "I'm a Child",
                        color = CyberTextBright,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Track steps, check screen time remaining and use SOS emergency.",
                        color = CyberTextDim,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
