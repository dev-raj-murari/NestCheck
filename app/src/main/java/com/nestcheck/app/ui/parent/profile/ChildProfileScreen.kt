package com.nestcheck.app.ui.parent.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
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
import com.nestcheck.app.ui.theme.*
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
            .background(CyberBlack)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .verticalScroll(scrollState)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back", tint = CyberTextBright)
                }
                Text(
                    text = "CHILD_PROFILE // BIO_CONFIG",
                    color = CyberTextBright,
                    style = Typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .border(1.dp, CyberTextBright, RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "NODE: ARMED",
                    color = CyberTextBright,
                    style = Typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(CyberCard)
                .border(1.dp, CyberBorder, RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            Column {
                Text("PHYSICAL_ATTRIBUTES", color = CyberTextDim, style = Typography.labelSmall, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("CHILD BIOMETRIC & WELLNESS CALIBRATION", color = CyberTextBright, style = Typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("CHILD_NAME", style = Typography.labelSmall) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = CyberTextBright,
                unfocusedTextColor = CyberTextBright,
                focusedBorderColor = CyberTextBright,
                unfocusedBorderColor = CyberBorder,
                focusedLabelColor = CyberTextBright,
                unfocusedLabelColor = CyberTextDim
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = age,
                onValueChange = { age = it },
                label = { Text("AGE (YRS)", style = Typography.labelSmall) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = CyberTextBright,
                    unfocusedTextColor = CyberTextBright,
                    focusedBorderColor = CyberTextBright,
                    unfocusedBorderColor = CyberBorder,
                    focusedLabelColor = CyberTextBright,
                    unfocusedLabelColor = CyberTextDim
                )
            )
            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1.2f)) {
                Text("GENDER", style = Typography.labelSmall, color = CyberTextDim, letterSpacing = 0.5.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                    listOf("Boy", "Girl").forEach { g ->
                        val selected = gender == g
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (selected) White else CyberCard)
                                .border(1.dp, if (selected) White else CyberBorder, RoundedCornerShape(4.dp))
                                .clickable { gender = g },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = g.uppercase(),
                                color = if (selected) Black else CyberTextBright,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                letterSpacing = 0.5.sp
                            )
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
                label = { Text("WEIGHT (KG)", style = Typography.labelSmall) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = CyberTextBright,
                    unfocusedTextColor = CyberTextBright,
                    focusedBorderColor = CyberTextBright,
                    unfocusedBorderColor = CyberBorder,
                    focusedLabelColor = CyberTextBright,
                    unfocusedLabelColor = CyberTextDim
                )
            )
            Spacer(modifier = Modifier.width(12.dp))
            OutlinedTextField(
                value = height,
                onValueChange = { height = it },
                label = { Text("HEIGHT (CM)", style = Typography.labelSmall) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = CyberTextBright,
                    unfocusedTextColor = CyberTextBright,
                    focusedBorderColor = CyberTextBright,
                    unfocusedBorderColor = CyberBorder,
                    focusedLabelColor = CyberTextBright,
                    unfocusedLabelColor = CyberTextDim
                )
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // LIVE BMI & DAILY TARGET CARD
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
                    Text("CALCULATED_BMI", color = CyberTextDim, style = Typography.labelSmall, letterSpacing = 1.sp)
                    val category = when {
                        calculatedBmi < 15.0f -> "Underweight"
                        calculatedBmi <= 19.5f -> "Healthy Weight"
                        calculatedBmi <= 24.0f -> "Overweight"
                        else -> "Obese"
                    }
                    Text(
                        text = "%.1f (%s)".format(calculatedBmi, category),
                        color = CyberTextBright,
                        style = Typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("KINETIC_STEP_TARGET", color = CyberTextDim, style = Typography.labelSmall, letterSpacing = 1.sp)
                    Text(
                        text = "$calculatedStepGoal STEPS / DAY",
                        color = CyberTextBright,
                        style = Typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(White)
                .clickable {
                    viewModel.saveProfile(name, a, gender, w, h)
                    navController.popBackStack()
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "COMMIT_CHANGES // SAVE_PROFILE >>",
                color = Black,
                style = Typography.titleSmall,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
        }
    }
}
