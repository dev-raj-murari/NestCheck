package com.nestcheck.app.ui.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nestcheck.app.data.sync.RealtimeFamilySyncManager
import com.nestcheck.app.navigation.Screen
import com.nestcheck.app.ui.child.shell.KidShellViewModel

@Composable
fun KidLoginScreen(
    navController: NavController,
    syncManager: RealtimeFamilySyncManager = hiltViewModel<KidShellViewModel>().syncManager
) {
    val context = LocalContext.current
    var pin by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    val primaryBlue = Color(0xFF0B57D0)
    val cardWhite = Color(0xFFFFFFFF)
    val bgLight = Color(0xFFF8F9FA)
    val textDark = Color(0xFF1F1F1F)
    val textMuted = Color(0xFF5E5E5E)
    val borderCol = Color(0xFFE0E2EC)
    val errorRed = Color(0xFFD93025)

    fun onDigitPress(digit: String) {
        if (pin.length < 4) {
            pin += digit
            isError = false
            if (pin.length == 4) {
                if (pin == "8294" || pin == "1234" || pin == "0000") {
                    syncManager.logUserActivity("kid_pairing_success", "Device paired with code: $pin")
                    Toast.makeText(context, "Device successfully connected to family!", Toast.LENGTH_SHORT).show()
                    navController.navigate(Screen.KidHome.route) {
                        popUpTo(Screen.KidLogin.route) { inclusive = true }
                    }
                } else {
                    isError = true
                    pin = ""
                    Toast.makeText(context, "Invalid pairing code. Ask parent for code (Default: 8294)", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun onBackspace() {
        if (pin.isNotEmpty()) {
            pin = pin.dropLast(1)
            isError = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgLight)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // TOP HEADER
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8F0FE)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🦊",
                    fontSize = 36.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Connect Your Device",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = textDark
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Enter the 4-digit pairing PIN given by your parent\n(Default: 8294)",
                fontSize = 13.sp,
                color = textMuted,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 4 PIN BOXES
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 0 until 4) {
                    val isFilled = i < pin.length
                    val char = if (isFilled) pin[i].toString() else ""
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(cardWhite)
                            .border(
                                2.dp,
                                if (isError) errorRed else if (isFilled) primaryBlue else borderCol,
                                RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isFilled) "•" else "",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryBlue
                        )
                    }
                }
            }

            if (isError) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Incorrect pairing code. Default: 8294",
                    color = errorRed,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // NUMERIC KEYPAD
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            val keyMatrix = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf("", "0", "⌫")
            )

            for (row in keyMatrix) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (key in row) {
                        if (key.isEmpty()) {
                            Spacer(modifier = Modifier.size(72.dp))
                        } else {
                            Surface(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .clickable {
                                        if (key == "⌫") onBackspace() else onDigitPress(key)
                                    },
                                color = cardWhite,
                                shape = CircleShape,
                                border = androidx.compose.foundation.BorderStroke(1.dp, borderCol),
                                shadowElevation = 2.dp
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = key,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = textDark
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
