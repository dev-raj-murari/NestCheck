package com.nestcheck.app.ui.parent.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.nestcheck.app.core.BMICalculator
import com.nestcheck.app.data.model.ChildAvatars
import com.nestcheck.app.data.model.ChildProfile
import com.nestcheck.app.data.repository.ProfileRepository
import com.nestcheck.app.data.sync.RealtimeFamilySyncManager
import com.nestcheck.app.ui.parent.shell.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ChildProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    val syncManager: RealtimeFamilySyncManager
) : ViewModel() {
    val childProfile: StateFlow<ChildProfile> = profileRepository.childProfile

    fun saveProfile(
        name: String,
        age: Int,
        gender: String,
        weight: Float,
        height: Float,
        avatar: String
    ) {
        profileRepository.updateChildProfile(name, age, gender, weight, height, avatarUrl = avatar)
        syncManager.updateChildProfile(
            name = name,
            age = age,
            dailyLimitMinutes = syncManager.syncState.value.dailyLimitMinutes,
            bedtime = syncManager.syncState.value.bedtime,
            schoolHours = syncManager.syncState.value.schoolHours,
            avatar = avatar
        )
    }

    fun selectAvatar(avatar: String) {
        profileRepository.updateChildAvatar(avatar)
        syncManager.updateChildAvatar(avatar)
    }
}

@Composable
fun ChildProfileScreen(
    navController: NavController,
    viewModel: ChildProfileViewModel = hiltViewModel()
) {
    val child by viewModel.childProfile.collectAsState()

    var name by remember(child) { mutableStateOf(child.name) }
    var age by remember(child) { mutableStateOf(child.age.toString()) }
    var gender by remember(child) { mutableStateOf(child.gender) }
    var weight by remember(child) { mutableStateOf(child.weight.toString()) }
    var height by remember(child) { mutableStateOf(child.height.toString()) }
    var selectedAvatar by remember(child) { mutableStateOf(if (child.avatarUrl.isNotBlank()) child.avatarUrl else ChildAvatars.DEFAULT_AVATAR) }

    val w = weight.toFloatOrNull() ?: 30f
    val h = height.toFloatOrNull() ?: 130f
    val a = age.toIntOrNull() ?: 10
    val calculatedBmi = BMICalculator.calculateBMI(w, h)
    val calculatedStepGoal = BMICalculator.calculateStepGoal(a, calculatedBmi)

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GflBgLight)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .verticalScroll(scrollState)
    ) {
        // TOP APP BAR
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back", tint = GflTextDark)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Child Profile",
                    color = GflTextDark,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = GflBlueContainer
            ) {
                Text(
                    text = "Active Child",
                    color = GflBlue,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // AVATAR & HEADER CARD
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = GflCardWhite),
            border = androidx.compose.foundation.BorderStroke(1.dp, GflBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(GflBlueContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = selectedAvatar,
                        fontSize = 32.sp
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = name.ifBlank { "Child Name" },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = GflTextDark
                    )
                    Text(
                        text = "Biometric & Avatar Profile",
                        fontSize = 13.sp,
                        color = GflTextMuted
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // POINTER 2: CHOOSE CHILD AVATAR CARD
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = GflCardWhite),
            border = androidx.compose.foundation.BorderStroke(1.dp, GflBorder)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Face, contentDescription = null, tint = GflBlue, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Choose Child Avatar",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = GflTextDark
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Pick an avatar icon for this child's profile and dashboard",
                    fontSize = 12.sp,
                    color = GflTextMuted
                )
                Spacer(modifier = Modifier.height(14.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(ChildAvatars.PRESETS) { preset ->
                        val isSelected = selectedAvatar == preset.emoji
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) GflBlueContainer else GflBgLight)
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) GflBlue else GflBorder,
                                    shape = CircleShape
                                )
                                .clickable { selectedAvatar = preset.emoji },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = preset.emoji, fontSize = 26.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // BASIC DETAILS CARD
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = GflCardWhite),
            border = androidx.compose.foundation.BorderStroke(1.dp, GflBorder)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Child Details",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = GflTextDark
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Child's Display Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it },
                    label = { Text("Age (Years)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Gender", color = GflTextMuted, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf("Boy", "Girl").forEach { g ->
                        val selected = gender.equals(g, ignoreCase = true)
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (selected) GflBlue else GflBgLight,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (selected) GflBlue else GflBorder
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .clickable { gender = g }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = g,
                                    color = if (selected) Color.White else GflTextDark,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // BIOMETRIC METRICS CARD
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = GflCardWhite),
            border = androidx.compose.foundation.BorderStroke(1.dp, GflBorder)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Physical Health & Growth",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = GflTextDark
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = weight,
                        onValueChange = { weight = it },
                        label = { Text("Weight (kg)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = height,
                        onValueChange = { height = it },
                        label = { Text("Height (cm)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = GflBorder)
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Calculated Body Mass Index", color = GflTextMuted, fontSize = 12.sp)
                        Text(
                            text = String.format("%.1f BMI", calculatedBmi),
                            color = GflTextDark,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = GflBlueContainer
                    ) {
                        Text(
                            text = BMICalculator.getBMICategory(calculatedBmi),
                            color = GflBlue,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = GflBorder)
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Recommended Daily Step Goal", color = GflTextMuted, fontSize = 12.sp)
                        Text(
                            text = "$calculatedStepGoal steps / day",
                            color = GflBlue,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Icon(Icons.Outlined.DirectionsWalk, contentDescription = null, tint = GflBlue, modifier = Modifier.size(24.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // SAVE BUTTON
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GflBlue, contentColor = Color.White),
            onClick = {
                viewModel.saveProfile(name, a, gender, w, h, selectedAvatar)
                navController.popBackStack()
            }
        ) {
            Text(
                text = "Save Child Profile",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
