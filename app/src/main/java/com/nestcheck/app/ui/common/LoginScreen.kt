package com.nestcheck.app.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nestcheck.app.navigation.Screen
import com.nestcheck.app.ui.theme.*

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBlack)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 20.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Clean Header - No comments or subtitles underneath
        Text(
            text = "NestCheck",
            color = CyberTextBright,
            fontSize = 32.sp,
            fontFamily = TechMono,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        // If authenticated: Show clean session card (no dashboard)
        if (uiState.isLoggedIn) {
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
                        text = if (uiState.loggedInUserType == UserType.PARENT) "Parent Account" else "Kid Account",
                        color = CyberTextBright,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (uiState.loggedInUserType == UserType.PARENT) {
                        Text(text = "Name: ${uiState.parentName}", color = CyberTextBright, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "Role in House: ${uiState.parentRoleInHouse}", color = CyberTextBright, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "Email: ${uiState.parentEmail}", color = CyberTextBright, fontSize = 14.sp)

                        if (uiState.addedKids.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(text = "Children (${uiState.addedKids.size}):", color = CyberTextBright, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            uiState.addedKids.forEach { kid ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(CyberBlack)
                                        .border(1.dp, CyberBorder, RoundedCornerShape(6.dp))
                                        .padding(10.dp)
                                ) {
                                    Column {
                                        Text(text = "${kid.name} • ${kid.age} yrs • ${kid.gender}", color = CyberTextBright, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                        if (kid.dob.isNotBlank()) {
                                            Text(text = "DOB: ${kid.dob}", color = CyberTextDim, fontSize = 12.sp)
                                        }
                                        if (kid.height.isNotBlank() || kid.weight.isNotBlank()) {
                                            Text(text = "Height: ${kid.height} cm • Weight: ${kid.weight} kg", color = CyberTextDim, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        Text(text = "Kid Name: ${uiState.kidLoginName}", color = CyberTextBright, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "Status: Connected", color = SuccessGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { navController.navigate(Screen.MyCredits.route) },
                            modifier = Modifier.fillMaxWidth().height(42.dp),
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CyberBlack, contentColor = CyberTextBright)
                        ) {
                            Text("My Credits & Rewards", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { navController.navigate(Screen.StepCounter.route) },
                                modifier = Modifier.weight(1f).height(42.dp),
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CyberBlack, contentColor = SuccessGreen)
                            ) {
                                Text("Step Counter", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Button(
                                onClick = { navController.navigate(Screen.MoodCheckIn.route) },
                                modifier = Modifier.weight(1f).height(42.dp),
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CyberBlack, contentColor = CyberTextBright)
                            ) {
                                Text("Mood Check-In", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { navController.navigate(Screen.FocusMode.route) },
                                modifier = Modifier.weight(1f).height(42.dp),
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CyberBlack, contentColor = CyberTextBright)
                            ) {
                                Text("Focus Mode", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Button(
                                onClick = { navController.navigate(Screen.HabitChallenges.route) },
                                modifier = Modifier.weight(1f).height(42.dp),
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CyberBlack, contentColor = CyberTextBright)
                            ) {
                                Text("Streaks", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { navController.navigate(Screen.KidChores.route) },
                                modifier = Modifier.weight(1f).height(42.dp),
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CyberBlack, contentColor = CyberTextBright)
                            ) {
                                Text("My Chores", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Button(
                                onClick = { navController.navigate(Screen.KidHomework.route) },
                                modifier = Modifier.weight(1f).height(42.dp),
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CyberBlack, contentColor = CyberTextBright)
                            ) {
                                Text("My Homework", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { navController.navigate(Screen.WaterReminder.route) },
                                modifier = Modifier.weight(1f).height(42.dp),
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CyberBlack, contentColor = CyberTextBright)
                            ) {
                                Text("Hydration & Sleep", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Button(
                                onClick = { navController.navigate(Screen.RewardShop.route) },
                                modifier = Modifier.weight(1f).height(42.dp),
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CyberBlack, contentColor = WarningAmber)
                            ) {
                                Text("Reward Shop", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = { navController.navigate(Screen.ChildCheckIn.route) },
                            modifier = Modifier.fillMaxWidth().height(42.dp),
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CyberBlack, contentColor = SuccessGreen)
                        ) {
                            Text("📍 \"I'm Here\" Arrival Check-In (F36)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = { navController.navigate(Screen.SOS.route) },
                            modifier = Modifier.fillMaxWidth().height(42.dp),
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AlertRed, contentColor = White)
                        ) {
                            Text("Emergency SOS", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (uiState.userType == UserType.PARENT) {
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(text = "Active Modules:", color = CyberTextDim, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { navController.navigate(Screen.ScreenTime.route) },
                                modifier = Modifier.weight(1f).height(40.dp),
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CyberBlack, contentColor = CyberTextBright)
                            ) {
                                Text("Screen Time", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Button(
                                onClick = { navController.navigate(Screen.TimeLimits.route) },
                                modifier = Modifier.weight(1f).height(40.dp),
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CyberBlack, contentColor = CyberTextBright)
                            ) {
                                Text("Time Limits", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { navController.navigate(Screen.AppControl.route) },
                                modifier = Modifier.weight(1f).height(40.dp),
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CyberBlack, contentColor = CyberTextBright)
                            ) {
                                Text("App Control", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Button(
                                onClick = { navController.navigate(Screen.Credits.route) },
                                modifier = Modifier.weight(1f).height(40.dp),
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CyberBlack, contentColor = CyberTextBright)
                            ) {
                                Text("Credits Ledger", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = { navController.navigate(Screen.ParentSecurity.route) },
                            modifier = Modifier.fillMaxWidth().height(40.dp),
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CyberBlack, contentColor = White)
                        ) {
                            Text("Parent PIN & Biometrics", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { navController.navigate(Screen.BatteryMonitor.route) },
                                modifier = Modifier.weight(1f).height(40.dp),
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CyberBlack, contentColor = SuccessGreen)
                            ) {
                                Text("Battery Telemetry", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Button(
                                onClick = { navController.navigate(Screen.NotificationsHub.route) },
                                modifier = Modifier.weight(1f).height(40.dp),
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CyberBlack, contentColor = CyberTextBright)
                            ) {
                                Text("Notification Hub", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { navController.navigate(Screen.Reports.route) },
                                modifier = Modifier.weight(1f).height(40.dp),
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CyberBlack, contentColor = CyberTextBright)
                            ) {
                                Text("Weekly Reports", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Button(
                                onClick = { navController.navigate(Screen.OfflineSync.route) },
                                modifier = Modifier.weight(1f).height(40.dp),
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CyberBlack, contentColor = WarningAmber)
                            ) {
                                Text("Offline Sync", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = { navController.navigate(Screen.AIDigest.route) },
                            modifier = Modifier.fillMaxWidth().height(40.dp),
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = Black)
                        ) {
                            Text("✨ AI Daily Digest Synthesis", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { navController.navigate(Screen.Location.route) },
                                modifier = Modifier.weight(1f).height(40.dp),
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CyberBlack, contentColor = AccentBlue)
                            ) {
                                Text("📍 Location & Safety", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Button(
                                onClick = { navController.navigate(Screen.ContentFilter.route) },
                                modifier = Modifier.weight(1f).height(40.dp),
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CyberBlack, contentColor = CyberTextBright)
                            ) {
                                Text("Web & NSFW Filter", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = { navController.navigate(Screen.AppQuarantine.route) },
                            modifier = Modifier.fillMaxWidth().height(40.dp),
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CyberBlack, contentColor = AlertRed)
                        ) {
                            Text("🛡️ App Quarantine Guard (F9)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { navController.navigate(Screen.RemoteLock.route) },
                                modifier = Modifier.weight(1f).height(40.dp),
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CyberBlack, contentColor = AlertRed)
                            ) {
                                Text("🔒 Remote Lock (F19)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Button(
                                onClick = { navController.navigate(Screen.MultiChild.route) },
                                modifier = Modifier.weight(1f).height(40.dp),
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CyberBlack, contentColor = AccentBlue)
                            ) {
                                Text("👨‍👧‍👦 Multi-Child (F20)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { navController.navigate(Screen.AIAssistant.route) },
                                modifier = Modifier.weight(1f).height(40.dp),
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CyberBlack, contentColor = CyberTextBright)
                            ) {
                                Text("💡 AI Advisor (F39)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Button(
                                onClick = { navController.navigate(Screen.FamilySharing.route) },
                                modifier = Modifier.weight(1f).height(40.dp),
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CyberBlack, contentColor = CyberTextBright)
                            ) {
                                Text("🤝 Co-Parents (F42)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = { navController.navigate(Screen.LanguageSettings.route) },
                            modifier = Modifier.fillMaxWidth().height(40.dp),
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CyberBlack, contentColor = WarningAmber)
                        ) {
                            Text("🌐 Multi-Language & Region (F43)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = viewModel::logout,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = Black)
                    ) {
                        Text("Log Out", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
            return@Column
        }

        // Two User Tabs: Parent vs Kid
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(CyberCard)
                .border(1.dp, CyberBorder, RoundedCornerShape(8.dp))
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (uiState.userType == UserType.PARENT) White else Color.Transparent)
                    .clickable { viewModel.setUserType(UserType.PARENT) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Parent",
                    color = if (uiState.userType == UserType.PARENT) Black else CyberTextDim,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (uiState.userType == UserType.KID) White else Color.Transparent)
                    .clickable { viewModel.setUserType(UserType.KID) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Kid",
                    color = if (uiState.userType == UserType.KID) Black else CyberTextDim,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Main Auth Form
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(CyberCard)
                .border(1.dp, CyberBorder, RoundedCornerShape(8.dp))
                .padding(18.dp)
        ) {
            Column {
                if (uiState.userType == UserType.PARENT) {
                    if (!uiState.isRegistering) {
                        // === PARENT LOGIN ===
                        Text("Parent Login", color = CyberTextBright, fontSize = 16.sp, fontWeight = FontWeight.Bold)

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = uiState.parentEmail,
                            onValueChange = viewModel::onParentEmailChange,
                            label = { Text("Email") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = CyberTextBright,
                                unfocusedTextColor = CyberTextBright,
                                focusedBorderColor = CyberTextBright,
                                unfocusedBorderColor = CyberBorder
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = uiState.parentPassword,
                            onValueChange = viewModel::onParentPasswordChange,
                            label = { Text("Password") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = CyberTextBright,
                                unfocusedTextColor = CyberTextBright,
                                focusedBorderColor = CyberTextBright,
                                unfocusedBorderColor = CyberBorder
                            )
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = viewModel::loginParent,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = Black)
                        ) {
                            Text("Login as Parent", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                    } else {
                        // === PARENT REGISTER WITH KID ADDING ===
                        Text("Parent Information", color = CyberTextBright, fontSize = 16.sp, fontWeight = FontWeight.Bold)

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = uiState.parentName,
                            onValueChange = viewModel::onParentNameChange,
                            label = { Text("Full Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = CyberTextBright,
                                unfocusedTextColor = CyberTextBright,
                                focusedBorderColor = CyberTextBright,
                                unfocusedBorderColor = CyberBorder
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = uiState.parentEmail,
                            onValueChange = viewModel::onParentEmailChange,
                            label = { Text("Email") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = CyberTextBright,
                                unfocusedTextColor = CyberTextBright,
                                focusedBorderColor = CyberTextBright,
                                unfocusedBorderColor = CyberBorder
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Role in House
                        Text("Role in the house", color = CyberTextDim, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            listOf("Father", "Mother", "Guardian").forEach { role ->
                                val selected = uiState.parentRoleInHouse == role
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(40.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (selected) White else CyberBlack)
                                        .border(1.dp, if (selected) White else CyberBorder, RoundedCornerShape(4.dp))
                                        .clickable { viewModel.onParentRoleChange(role) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = role,
                                        color = if (selected) Black else CyberTextBright,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = uiState.parentPassword,
                            onValueChange = viewModel::onParentPasswordChange,
                            label = { Text("Password") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = CyberTextBright,
                                unfocusedTextColor = CyberTextBright,
                                focusedBorderColor = CyberTextBright,
                                unfocusedBorderColor = CyberBorder
                            )
                        )

                        Spacer(modifier = Modifier.height(20.dp))
                        HorizontalDivider(color = CyberBorder, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(16.dp))

                        // SECTION: ADD KIDS
                        Text("Add Child", color = CyberTextBright, fontSize = 16.sp, fontWeight = FontWeight.Bold)

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = uiState.kidName,
                            onValueChange = viewModel::onKidNameChange,
                            label = { Text("Child Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = CyberTextBright,
                                unfocusedTextColor = CyberTextBright,
                                focusedBorderColor = CyberTextBright,
                                unfocusedBorderColor = CyberBorder
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = uiState.kidAge,
                                onValueChange = viewModel::onKidAgeChange,
                                label = { Text("Age") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = CyberTextBright,
                                    unfocusedTextColor = CyberTextBright,
                                    focusedBorderColor = CyberTextBright,
                                    unfocusedBorderColor = CyberBorder
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedTextField(
                                value = uiState.kidDob,
                                onValueChange = viewModel::onKidDobChange,
                                label = { Text("DOB (YYYY-MM-DD)") },
                                singleLine = true,
                                modifier = Modifier.weight(1.5f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = CyberTextBright,
                                    unfocusedTextColor = CyberTextBright,
                                    focusedBorderColor = CyberTextBright,
                                    unfocusedBorderColor = CyberBorder
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Gender Selector
                        Text("Gender", color = CyberTextDim, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            listOf("Boy", "Girl").forEach { g ->
                                val selected = uiState.kidGender == g
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(40.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (selected) White else CyberBlack)
                                        .border(1.dp, if (selected) White else CyberBorder, RoundedCornerShape(4.dp))
                                        .clickable { viewModel.onKidGenderChange(g) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = g,
                                        color = if (selected) Black else CyberTextBright,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = uiState.kidHeight,
                                onValueChange = viewModel::onKidHeightChange,
                                label = { Text("Height (cm)") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = CyberTextBright,
                                    unfocusedTextColor = CyberTextBright,
                                    focusedBorderColor = CyberTextBright,
                                    unfocusedBorderColor = CyberBorder
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedTextField(
                                value = uiState.kidWeight,
                                onValueChange = viewModel::onKidWeightChange,
                                label = { Text("Weight (kg)") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = CyberTextBright,
                                    unfocusedTextColor = CyberTextBright,
                                    focusedBorderColor = CyberTextBright,
                                    unfocusedBorderColor = CyberBorder
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedButton(
                            onClick = viewModel::addKidToList,
                            modifier = Modifier.fillMaxWidth().height(42.dp),
                            shape = RoundedCornerShape(4.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberTextBright)
                        ) {
                            Text("+ Add Child to List", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }

                        // Display Added Kids List
                        if (uiState.addedKids.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Added Children:", color = CyberTextDim, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            uiState.addedKids.forEach { kid ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(CyberBlack)
                                        .border(1.dp, CyberBorder, RoundedCornerShape(4.dp))
                                        .padding(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "${kid.name} (${kid.age}y, ${kid.gender})",
                                                color = CyberTextBright,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Text(
                                                text = "${kid.height}cm • ${kid.weight}kg • ${kid.dob}",
                                                color = CyberTextDim,
                                                fontSize = 11.sp
                                            )
                                        }
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Remove",
                                            tint = AlertRed,
                                            modifier = Modifier
                                                .size(18.dp)
                                                .clickable { viewModel.removeKid(kid.id) }
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = viewModel::registerParent,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = Black)
                        ) {
                            Text("Register Parent", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }

                } else {
                    // === KID USER ===
                    if (!uiState.isRegistering) {
                        // === KID LOGIN ===
                        Text("Kid Login", color = CyberTextBright, fontSize = 16.sp, fontWeight = FontWeight.Bold)

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = uiState.kidLoginName,
                            onValueChange = viewModel::onKidLoginNameChange,
                            label = { Text("Child Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = CyberTextBright,
                                unfocusedTextColor = CyberTextBright,
                                focusedBorderColor = CyberTextBright,
                                unfocusedBorderColor = CyberBorder
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = uiState.kidLoginPass,
                            onValueChange = viewModel::onKidLoginPassChange,
                            label = { Text("PIN / Password") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = CyberTextBright,
                                unfocusedTextColor = CyberTextBright,
                                focusedBorderColor = CyberTextBright,
                                unfocusedBorderColor = CyberBorder
                            )
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = viewModel::loginKid,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = Black)
                        ) {
                            Text("Login as Kid", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                    } else {
                        // === KID REGISTER ===
                        Text("Kid Registration", color = CyberTextBright, fontSize = 16.sp, fontWeight = FontWeight.Bold)

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = uiState.kidLoginName,
                            onValueChange = viewModel::onKidLoginNameChange,
                            label = { Text("Child Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = CyberTextBright,
                                unfocusedTextColor = CyberTextBright,
                                focusedBorderColor = CyberTextBright,
                                unfocusedBorderColor = CyberBorder
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = uiState.kidAge,
                                onValueChange = viewModel::onKidAgeChange,
                                label = { Text("Age") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = CyberTextBright,
                                    unfocusedTextColor = CyberTextBright,
                                    focusedBorderColor = CyberTextBright,
                                    unfocusedBorderColor = CyberBorder
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedTextField(
                                value = uiState.kidDob,
                                onValueChange = viewModel::onKidDobChange,
                                label = { Text("DOB (YYYY-MM-DD)") },
                                singleLine = true,
                                modifier = Modifier.weight(1.5f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = CyberTextBright,
                                    unfocusedTextColor = CyberTextBright,
                                    focusedBorderColor = CyberTextBright,
                                    unfocusedBorderColor = CyberBorder
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = uiState.kidHeight,
                                onValueChange = viewModel::onKidHeightChange,
                                label = { Text("Height (cm)") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = CyberTextBright,
                                    unfocusedTextColor = CyberTextBright,
                                    focusedBorderColor = CyberTextBright,
                                    unfocusedBorderColor = CyberBorder
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedTextField(
                                value = uiState.kidWeight,
                                onValueChange = viewModel::onKidWeightChange,
                                label = { Text("Weight (kg)") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = CyberTextBright,
                                    unfocusedTextColor = CyberTextBright,
                                    focusedBorderColor = CyberTextBright,
                                    unfocusedBorderColor = CyberBorder
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = uiState.parentEmail,
                            onValueChange = viewModel::onParentEmailChange,
                            label = { Text("Parent's Email") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = CyberTextBright,
                                unfocusedTextColor = CyberTextBright,
                                focusedBorderColor = CyberTextBright,
                                unfocusedBorderColor = CyberBorder
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = uiState.kidLoginPass,
                            onValueChange = viewModel::onKidLoginPassChange,
                            label = { Text("Create PIN / Password") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = CyberTextBright,
                                unfocusedTextColor = CyberTextBright,
                                focusedBorderColor = CyberTextBright,
                                unfocusedBorderColor = CyberBorder
                            )
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = viewModel::loginKid,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = Black)
                        ) {
                            Text("Register Kid", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Toggle Login / Register
        Text(
            text = if (uiState.isRegistering) "Already have an account? Login" else "Don't have an account? Register",
            color = CyberTextDim,
            fontSize = 13.sp,
            modifier = Modifier
                .clickable { viewModel.setRegistering(!uiState.isRegistering) }
                .padding(8.dp)
        )
    }
}
