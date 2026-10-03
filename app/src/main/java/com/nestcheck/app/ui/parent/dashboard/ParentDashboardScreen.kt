package com.nestcheck.app.ui.parent.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.nestcheck.app.data.model.ChildProfile
import com.nestcheck.app.data.model.ParentUser
import com.nestcheck.app.data.repository.ProfileRepository
import com.nestcheck.app.navigation.Screen
import com.nestcheck.app.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ParentDashboardViewModel @Inject constructor(
    profileRepository: ProfileRepository
) : ViewModel() {
    val parentProfile: StateFlow<ParentUser> = profileRepository.parentProfile
    val childProfile: StateFlow<ChildProfile> = profileRepository.childProfile
}

data class DashboardCard(
    val title: String,
    val subtitle: String,
    val featureTag: String,
    val icon: ImageVector,
    val route: String
)

@Composable
fun ParentDashboardScreen(
    navController: NavController,
    viewModel: ParentDashboardViewModel = hiltViewModel()
) {
    val child by viewModel.childProfile.collectAsState()

    // 8 sections directly matching flowchart #2
    val dashboardCards = listOf(
        DashboardCard("Child profiles", "Age, weight, height, auto BMI", "F13, F20", Icons.Outlined.Person, Screen.ChildProfile.route),
        DashboardCard("Screen time", "Daily caps, countdown, limits", "F1, F2", Icons.Outlined.Timer, Screen.ScreenTime.route),
        DashboardCard("App control", "Block, allow, install approvals", "F3, F9", Icons.Outlined.Block, Screen.AppControl.route),
        DashboardCard("Content safety", "Web filters, NSFW detection", "F7, F8", Icons.Outlined.Shield, Screen.ContentFilter.route),
        DashboardCard("Location", "Live map, school/home geofence", "F10-F12", Icons.Outlined.LocationOn, Screen.Location.route),
        DashboardCard("Credits & routines", "Award credits, homework check", "F4-F6", Icons.Outlined.Stars, Screen.Credits.route),
        DashboardCard("Device & safety", "SOS alerts, battery, remote lock", "F15, F16", Icons.Outlined.WarningAmber, Screen.SOS.route),
        DashboardCard("Reports & alerts", "Weekly summary, notification feed", "F17, F18", Icons.Outlined.Assessment, Screen.Reports.route)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBlack)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // TOP HEADER
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.navigate(Screen.RoleSelection.route) }) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back", tint = CyberTextBright)
                }
                Text(
                    text = "Parent Dashboard",
                    color = CyberTextBright,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .border(1.dp, CyberBorder, RoundedCornerShape(4.dp))
                    .clickable { navController.navigate(Screen.RoleSelection.route) }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Switch Role",
                    color = CyberTextDim,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // CHILD SUMMARY HERO CARD
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(CyberCard)
                .border(1.dp, CyberBorder, RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "CURRENT CHILD",
                            color = CyberTextDim,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${child.name} • ${child.age} yrs (${child.gender})",
                            color = CyberTextBright,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(White)
                            .clickable { navController.navigate(Screen.ChildProfile.route) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Edit Profile",
                            color = Black,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = CyberBorder, thickness = 1.dp)
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Weight", color = CyberTextDim, fontSize = 11.sp)
                        Text("${child.weight} kg", color = CyberTextBright, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text("Height", color = CyberTextDim, fontSize = 11.sp)
                        Text("${child.height} cm", color = CyberTextBright, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text("BMI", color = CyberTextDim, fontSize = 11.sp)
                        Text("%.1f".format(child.bmi), color = CyberTextBright, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text("Step Goal", color = CyberTextDim, fontSize = 11.sp)
                        Text("${child.dailyStepGoal}", color = CyberTextBright, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2-COLUMN GRID (8 SECTIONS FROM FLOWCHART)
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(dashboardCards) { card ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(115.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(CyberCard)
                        .border(1.dp, CyberBorder, RoundedCornerShape(6.dp))
                        .clickable { navController.navigate(card.route) }
                        .padding(12.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(card.featureTag, color = CyberTextDim, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Icon(card.icon, contentDescription = card.title, tint = CyberTextBright, modifier = Modifier.size(18.dp))
                        }

                        Column {
                            Text(card.title, color = CyberTextBright, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                            Text(card.subtitle, color = CyberTextDim, fontSize = 10.sp, maxLines = 2)
                        }
                    }
                }
            }
        }
    }
}
