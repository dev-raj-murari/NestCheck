package com.nestcheck.app.ui.auth

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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

data class KidProfileOption(
    val name: String,
    val avatar: String,
    val grade: String,
    val age: Int
)

@Composable
fun KidLoginScreen(
    navController: NavController,
    syncManager: RealtimeFamilySyncManager = hiltViewModel<KidShellViewModel>().syncManager
) {
    val context = LocalContext.current
    val syncState by syncManager.syncState.collectAsState()

    val profiles = remember {
        listOf(
            KidProfileOption("Leo", "🦊", "Grade 6", 11),
            KidProfileOption("Olivia", "🦄", "Grade 4", 9),
            KidProfileOption("Aarav", "🚀", "Grade 5", 10),
            KidProfileOption("Maya", "🐼", "Grade 2", 7)
        )
    }

    var selectedProfile by remember { mutableStateOf(profiles[0]) }
    var pin by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    val mintGreen = Color(0xFF00875A)
    val indigoPrimary = Color(0xFF4C6EF5)
    val warmAmber = Color(0xFFFF922B)
    val cardBg = Color(0xFFFFFFFF)
    val bgSoft = Color(0xFFF4F7FB)
    val textDark = Color(0xFF1E293B)
    val textMuted = Color(0xFF64748B)
    val borderCol = Color(0xFFD0D7DE)
    val errorRed = Color(0xFFE03131)

    fun onDigitPress(digit: String) {
        if (pin.length < 4) {
            pin += digit
            isError = false
            if (pin.length == 4) {
                if (pin == "8294" || pin == "1234" || pin == "0000") {
                    // Update syncManager with child profile
                    syncManager.switchActiveChild(selectedProfile.name, selectedProfile.avatar)
                    syncManager.addCredits(10) // Welcome bonus stars!
                    syncManager.logChildActivity(
                        event = "child_pairing_success",
                        details = "Child device paired as ${selectedProfile.name} (${selectedProfile.avatar}) with PIN $pin",
                        extraData = mapOf(
                            "childName" to selectedProfile.name,
                            "avatar" to selectedProfile.avatar,
                            "grade" to selectedProfile.grade,
                            "welcomeStars" to 10
                        )
                    )
                    Toast.makeText(
                        context,
                        "🌟 Welcome, ${selectedProfile.name}! +10 Stars added to your bank!",
                        Toast.LENGTH_LONG
                    ).show()
                    navController.navigate(Screen.KidHome.route) {
                        popUpTo(Screen.KidLogin.route) { inclusive = true }
                    }
                } else {
                    isError = true
                    pin = ""
                    Toast.makeText(
                        context,
                        "Oops! Ask your parent for the 4-digit code (Default is 8294)",
                        Toast.LENGTH_SHORT
                    ).show()
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
            .background(bgSoft)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // TOP AVATAR & HEADER
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE6FCF5))
                    .border(3.dp, mintGreen.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = selectedProfile.avatar,
                    fontSize = 42.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "NestCheck Junior",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = textDark
            )

            Text(
                text = "Choose your avatar & enter your family code",
                fontSize = 12.5.sp,
                color = textMuted,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(14.dp))

            // AVATAR PICKER ROW
            Text(
                text = "Who is using this phone?",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = textDark,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(profiles) { profile ->
                    val isSelected = profile.name == selectedProfile.name
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) Color(0xFFEDF2FF) else cardBg,
                        border = androidx.compose.foundation.BorderStroke(
                            2.dp,
                            if (isSelected) indigoPrimary else borderCol
                        ),
                        modifier = Modifier
                            .clickable {
                                selectedProfile = profile
                                syncManager.logChildActivity(
                                    "child_profile_selected",
                                    "Selected ${profile.name} (${profile.avatar})"
                                )
                            }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(text = profile.avatar, fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = profile.name,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) indigoPrimary else textDark
                                )
                                Text(
                                    text = profile.grade,
                                    fontSize = 10.sp,
                                    color = textMuted
                                )
                            }
                        }
                    }
                }
            }
        }

        // MIDDLE: 4 PAIRING DIGITS BOXES
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Family Pairing Code",
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Bold,
                color = textDark
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 0 until 4) {
                    val isFilled = i < pin.length
                    val char = if (isFilled) pin[i].toString() else ""
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(cardBg)
                            .border(
                                2.5.dp,
                                if (isError) errorRed
                                else if (isFilled) indigoPrimary
                                else borderCol,
                                RoundedCornerShape(14.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isFilled) {
                            Text(
                                text = "⭐",
                                fontSize = 24.sp
                            )
                        } else {
                            Text(
                                text = "-",
                                fontSize = 20.sp,
                                color = Color(0xFFADB5BD)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Ask your parent for the code (Default: 8294)",
                fontSize = 11.5.sp,
                color = textMuted
            )
        }

        // NUMBER KEYPAD (FRIENDLY SOFT ROUNDED)
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            val rows = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf("HELP", "0", "DEL")
            )

            for (row in rows) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (key in row) {
                        if (key == "DEL") {
                            Box(
                                modifier = Modifier
                                    .size(58.dp)
                                    .clip(CircleShape)
                                    .clickable { onBackspace() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Backspace,
                                    contentDescription = "Delete",
                                    tint = textDark,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        } else if (key == "HELP") {
                            Box(
                                modifier = Modifier
                                    .size(58.dp)
                                    .clip(CircleShape)
                                    .clickable {
                                        Toast.makeText(context, "Tip: Default code is 8294 or 1234!", Toast.LENGTH_SHORT).show()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "💡 8294",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = warmAmber
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(58.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(cardBg)
                                    .border(1.5.dp, borderCol, RoundedCornerShape(16.dp))
                                    .clickable { onDigitPress(key) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = key,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = textDark
                                )
                            }
                        }
                    }
                }
            }
        }

        // BOTTOM EMERGENCY CALL SHORTCUT
        OutlinedButton(
            onClick = {
                syncManager.logChildActivity("emergency_call_shortcut_tapped", "Child tapped emergency call from pairing screen")
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:${syncState.emergencyContactPhone}")
                }
                context.startActivity(intent)
            },
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = errorRed),
            border = androidx.compose.foundation.BorderStroke(1.dp, errorRed.copy(alpha = 0.5f)),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.PhoneInTalk,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = errorRed
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Emergency Call Dad (${syncState.emergencyContactName})",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = errorRed
            )
        }
    }
}
