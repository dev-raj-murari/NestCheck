package com.nestcheck.app.ui.parent.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.nestcheck.app.data.model.ChildProfile
import com.nestcheck.app.data.repository.ProfileRepository
import com.nestcheck.app.ui.parent.shell.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ChildProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {
    val childProfile: StateFlow<ChildProfile> = profileRepository.childProfile

    fun saveProfile(
        name: String,
        age: Int,
        gender: String,
        weight: Float,
        height: Float
    ) {
        profileRepository.updateChildProfile(name, age, gender, weight, height)
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
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(GflBlueContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = name.take(1).ifBlank { "O" },
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = GflBlue
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
                        text = "Biometric & Wellness Profile",
                        fontSize = 13.sp,
                        color = GflTextMuted
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // PERSONAL DETAILS FORM CARD
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = GflCardWhite),
            border = androidx.compose.foundation.BorderStroke(1.dp, GflBorder)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Personal Information", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = GflTextDark)
                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Child Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = age,
                        onValueChange = { age = it },
                        label = { Text("Age (Years)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1.2f)) {
                        Text("Gender", fontSize = 12.sp, color = GflTextMuted, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            listOf("Boy", "Girl").forEach { g ->
                                val selected = gender == g
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .clickable { gender = g },
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (selected) GflBlueContainer else GflBgLight,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (selected) GflBlue else GflBorder
                                    )
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = g,
                                            color = if (selected) GflBlue else GflTextDark,
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = weight,
                        onValueChange = { weight = it },
                        label = { Text("Weight (kg)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
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
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // LIVE BMI & DAILY TARGET CARD
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = GflCardWhite),
            border = androidx.compose.foundation.BorderStroke(1.dp, GflBorder)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Health & Target Metrics", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = GflTextDark)
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Calculated BMI", color = GflTextMuted, fontSize = 12.sp)
                        val category = when {
                            calculatedBmi < 15.0f -> "Underweight"
                            calculatedBmi <= 19.5f -> "Healthy Weight"
                            calculatedBmi <= 24.0f -> "Overweight"
                            else -> "Obese"
                        }
                        Text(
                            text = "%.1f (%s)".format(calculatedBmi, category),
                            color = GflTextDark,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFECFDF5)
                    ) {
                        Text(
                            text = "Normal Range",
                            color = GflGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
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
                viewModel.saveProfile(name, a, gender, w, h)
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
