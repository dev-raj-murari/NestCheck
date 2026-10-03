package com.nestcheck.app.ui.child.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.nestcheck.app.data.model.ChildProfile
import com.nestcheck.app.data.repository.ProfileRepository
import com.nestcheck.app.navigation.Screen
import com.nestcheck.app.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class KidHomeViewModel @Inject constructor(
    profileRepository: ProfileRepository
) : ViewModel() {
    val childProfile: StateFlow<ChildProfile> = profileRepository.childProfile
}

@Composable
fun KidHomeScreen(
    navController: NavController,
    viewModel: KidHomeViewModel = hiltViewModel()
) {
    val child by viewModel.childProfile.collectAsState()
    val scrollState = rememberScrollState()

    var remainingMins by remember { mutableIntStateOf(15) }
    var choreDone by remember { mutableStateOf(false) }
    var readingDone by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBlack)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 18.dp, vertical = 12.dp)
            .verticalScroll(scrollState)
    ) {
        // TOP BAR
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "NestCheck Kid",
                color = CyberTextBright,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

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

        Spacer(modifier = Modifier.height(14.dp))

        // REMAINING SCREEN TIME HERO CARD
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(CyberCard)
                .border(1.dp, CyberBorder, RoundedCornerShape(8.dp))
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "Screen Time Remaining",
                    color = CyberTextDim,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "$remainingMins",
                        color = CyberTextBright,
                        fontSize = 54.sp,
                        fontFamily = TechMono,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 54.sp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "mins left today",
                        color = CyberTextBright,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = CyberBorder, thickness = 1.dp)
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Device status:", color = CyberTextDim, fontSize = 12.sp)
                    Text("Active", color = SuccessGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 2-COLUMN CARDS: STEPS & CREDITS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Steps
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(CyberCard)
                    .border(1.dp, CyberBorder, RoundedCornerShape(8.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Text("Daily Steps", color = CyberTextDim, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("3,420", color = CyberTextBright, fontSize = 22.sp, fontFamily = TechMono, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("Goal: ${child.dailyStepGoal}", color = CyberTextDim, fontSize = 11.sp)
                }
            }

            // Credits
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(CyberCard)
                    .border(1.dp, CyberBorder, RoundedCornerShape(8.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Text("Credits Earned", color = CyberTextDim, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("150 pts", color = CyberTextBright, fontSize = 22.sp, fontFamily = TechMono, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("+20 min bonus", color = CyberTextDim, fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // TASKS TO EARN TIME
        Text("Tasks & Activities", color = CyberTextDim, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        // Task 1
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(CyberCard)
                .border(1.dp, CyberBorder, RoundedCornerShape(6.dp))
                .clickable {
                    choreDone = !choreDone
                    if (choreDone) remainingMins += 15
                }
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Clean up study room", color = CyberTextBright, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Text(
                    text = if (choreDone) "Done (+15m)" else "Mark Done",
                    color = if (choreDone) SuccessGreen else CyberTextBright,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Task 2
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(CyberCard)
                .border(1.dp, CyberBorder, RoundedCornerShape(6.dp))
                .clickable {
                    readingDone = !readingDone
                    if (readingDone) remainingMins += 20
                }
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Read 15 pages of book", color = CyberTextBright, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Text(
                    text = if (readingDone) "Done (+20m)" else "Mark Done",
                    color = if (readingDone) SuccessGreen else CyberTextBright,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // QUICK TIME CONTROLS
        Text("Quick Controls", color = CyberTextDim, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(White)
                    .clickable { remainingMins += 15 },
                contentAlignment = Alignment.Center
            ) {
                Text("+15 Mins", color = Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(CyberCard)
                    .border(1.dp, CyberBorder, RoundedCornerShape(4.dp))
                    .clickable { remainingMins = (remainingMins - 10).coerceAtLeast(0) },
                contentAlignment = Alignment.Center
            ) {
                Text("-10 Mins", color = CyberTextBright, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(CyberCard)
                    .border(1.dp, CyberBorder, RoundedCornerShape(4.dp))
                    .clickable { remainingMins = 0 },
                contentAlignment = Alignment.Center
            ) {
                Text("Lock Now", color = AlertRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // SOS EMERGENCY BUTTON
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(6.dp))
                .border(1.5.dp, AlertRed, RoundedCornerShape(6.dp))
                .background(AlertRed.copy(alpha = 0.12f))
                .clickable { navController.navigate(Screen.SOS.route) },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Emergency SOS",
                color = AlertRed,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // FOOTER
        Text(
            text = "Profile: ${child.name} • ${child.age} yrs • BMI %.1f".format(child.bmi),
            color = CyberTextDim,
            fontSize = 11.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(10.dp))
    }
}
