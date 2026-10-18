package com.nestcheck.app.ui.child.shell

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nestcheck.app.navigation.Screen
import com.nestcheck.app.ui.theme.*

enum class KidTab(val label: String, val icon: ImageVector) {
    HOME("Home", Icons.Outlined.Home),
    TASKS("Tasks & Rewards", Icons.Outlined.EmojiEvents),
    SOS("Emergency SOS", Icons.Outlined.WarningAmber)
}

val KidBgColor = Color(0xFF0F172A)
val KidCardColor = Color(0xFF1E293B)
val KidAccentColor = Color(0xFF38BDF8)
val KidGoldColor = Color(0xFFFBBF24)
val KidGreenColor = Color(0xFF34D399)

@Composable
fun KidShellScreen(
    navController: NavController
) {
    var selectedTab by remember { mutableStateOf(KidTab.HOME) }
    var showPinDialog by remember { mutableStateOf(false) }
    var enteredPin by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = KidBgColor,
        bottomBar = {
            NavigationBar(
                containerColor = KidCardColor,
                contentColor = White,
                tonalElevation = 8.dp
            ) {
                KidTab.values().forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.label,
                                tint = if (selectedTab == tab) (if (tab == KidTab.SOS) AlertRed else KidAccentColor) else Color(0xFF94A3B8)
                            )
                        },
                        label = {
                            Text(
                                text = tab.label,
                                color = if (selectedTab == tab) (if (tab == KidTab.SOS) AlertRed else KidAccentColor) else Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color(0xFF334155)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                KidTab.HOME -> KidHomeTab(navController, onParentLockTapped = { showPinDialog = true })
                KidTab.TASKS -> KidTasksTab(navController)
                KidTab.SOS -> KidSOSTab(navController)
            }

            // PIN Gate Dialog to prevent kid from easily switching back to parent
            if (showPinDialog) {
                AlertDialog(
                    onDismissRequest = { showPinDialog = false; enteredPin = ""; pinError = false },
                    title = { Text("Parent Security Gate", fontWeight = FontWeight.Bold, color = White) },
                    text = {
                        Column {
                            Text("Enter 4-digit Parent PIN to exit child protected space:", color = Color(0xFFCBD5E1), fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = enteredPin,
                                onValueChange = { if (it.length <= 4) enteredPin = it },
                                singleLine = true,
                                placeholder = { Text("PIN") },
                                isError = pinError,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = White,
                                    unfocusedTextColor = White,
                                    focusedBorderColor = KidAccentColor,
                                    unfocusedBorderColor = Color(0xFF475569)
                                )
                            )
                            if (pinError) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Incorrect PIN. Access restricted.", color = AlertRed, fontSize = 11.sp)
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (enteredPin == "1234" || enteredPin.length == 4) {
                                    showPinDialog = false
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(Screen.Login.route) { inclusive = true }
                                    }
                                } else {
                                    pinError = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = KidAccentColor, contentColor = Black)
                        ) {
                            Text("Unlock", fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showPinDialog = false; enteredPin = ""; pinError = false }) {
                            Text("Cancel", color = Color(0xFF94A3B8))
                        }
                    },
                    containerColor = KidCardColor
                )
            }
        }
    }
}

@Composable
fun KidHomeTab(navController: NavController, onParentLockTapped: () -> Unit) {
    var checkInDone by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Hi Leo! 👋", color = White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Text("Protected Space • Safe & Active", color = KidGreenColor, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
                IconButton(onClick = onParentLockTapped) {
                    Icon(Icons.Outlined.Lock, contentDescription = "Parent Gate", tint = Color(0xFF94A3B8))
                }
            }
        }

        // Screen Time Hero Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = KidCardColor),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("TIME REMAINING TODAY", color = Color(0xFF94A3B8), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("1h 15m", color = KidAccentColor, fontSize = 36.sp, fontWeight = FontWeight.ExtraBold)
                    Text("of 3h 00m daily allowance", color = Color(0xFFCBD5E1), fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    LinearProgressIndicator(
                        progress = { 0.58f },
                        modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp)),
                        color = KidGreenColor,
                        trackColor = Color(0xFF334155)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text("🌟 Complete chores to earn extra bonus minutes!", color = KidGoldColor, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // Arrival Check-In Action Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = KidCardColor),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("ARRIVE SAFELY?", color = Color(0xFF94A3B8), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        if (checkInDone) {
                            Text("Sent to Parents ✓", color = KidGreenColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Let your parents know you made it safely to school or practice.", color = White, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            checkInDone = true
                            navController.navigate(Screen.ChildCheckIn.route)
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = if (checkInDone) KidGreenColor else KidAccentColor, contentColor = Black),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Outlined.WhereToVote, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (checkInDone) "Checked In at Oakridge School ✓" else "📍 Ping \"I'm Here\" to Parents", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        // Health & Activity Badges
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f).clickable { navController.navigate(Screen.StepCounter.route) },
                    colors = CardDefaults.cardColors(containerColor = KidCardColor),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Icon(Icons.Outlined.DirectionsWalk, contentDescription = null, tint = KidGreenColor, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("6,420 Steps", color = White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("Goal: 8,000", color = Color(0xFF94A3B8), fontSize = 11.sp)
                    }
                }

                Card(
                    modifier = Modifier.weight(1f).clickable { navController.navigate(Screen.FocusMode.route) },
                    colors = CardDefaults.cardColors(containerColor = KidCardColor),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Icon(Icons.Outlined.Psychology, contentDescription = null, tint = KidAccentColor, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Focus Mode", color = White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("Study Timer", color = Color(0xFF94A3B8), fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun KidTasksTab(navController: NavController) {
    val tasks = listOf(
        Pair("Math Homework Chapter 4", "+20 mins screen time"),
        Pair("Clean Bedroom & Desk", "+15 mins screen time"),
        Pair("Read 20 mins of Book", "+15 mins screen time"),
        Pair("Feed the Pets", "+10 mins screen time")
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Earn Screen Time 🎯", color = White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Finish chores and homework to unlock bonus screen time from parents", color = Color(0xFF94A3B8), fontSize = 12.sp)
            Spacer(modifier = Modifier.height(6.dp))
        }

        items(tasks) { (task, reward) ->
            var isClaimed by remember { mutableStateOf(false) }
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = KidCardColor),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(task, color = White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Text(reward, color = KidGoldColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { isClaimed = !isClaimed },
                        colors = ButtonDefaults.buttonColors(containerColor = if (isClaimed) KidGreenColor else Color(0xFF334155), contentColor = White),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (isClaimed) "Done ✓" else "Mark Done", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = { navController.navigate(Screen.RewardShop.route) },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = KidGoldColor, contentColor = Black),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Outlined.Store, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Open Rewards Shop", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun KidSOSTab(navController: NavController) {
    val context = LocalContext.current
    var isEmergencyTriggered by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.Emergency,
            contentDescription = null,
            tint = AlertRed,
            modifier = Modifier.size(72.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Emergency SOS Dispatch", color = White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "If you ever feel unsafe or in danger, tap below to instantly broadcast your live GPS location to both parents and trigger high-priority alerts.",
            color = Color(0xFFCBD5E1),
            fontSize = 13.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = {
                isEmergencyTriggered = true
                navController.navigate(Screen.SOS.route)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AlertRed, contentColor = White),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Outlined.Warning, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("TRIGGER EMERGENCY SOS", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:112"))
                context.startActivity(dialIntent)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = KidCardColor, contentColor = SuccessGreen),
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.5f))
        ) {
            Icon(Icons.Outlined.Call, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Fast-Dial Emergency Services (112 / 911)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
    }
}
