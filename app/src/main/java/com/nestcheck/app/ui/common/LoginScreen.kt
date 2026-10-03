package com.nestcheck.app.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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

    LaunchedEffect(uiState.navigateToRole) {
        if (uiState.navigateToRole) {
            viewModel.onNavigated()
            navController.navigate(Screen.RoleSelection.route)
        }
    }

    LaunchedEffect(uiState.navigateToParentDashboard) {
        if (uiState.navigateToParentDashboard) {
            viewModel.onNavigated()
            navController.navigate(Screen.ParentDashboard.route)
        }
    }

    LaunchedEffect(uiState.navigateToKidDashboard) {
        if (uiState.navigateToKidDashboard) {
            viewModel.onNavigated()
            navController.navigate(Screen.KidHome.route)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBlack)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // HEADER
        Text(
            text = "NestCheck",
            color = CyberTextBright,
            fontSize = 30.sp,
            fontFamily = TechMono,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = if (uiState.isRegistering) "Create new account" else "Login to continue",
            color = CyberTextDim,
            fontSize = 13.sp,
            modifier = Modifier.padding(top = 2.dp, bottom = 20.dp)
        )

        // IF REGISTERING: TOGGLE BETWEEN PARENT AND STUDENT
        if (uiState.isRegistering) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(CyberCard)
                    .border(1.dp, CyberBorder, RoundedCornerShape(8.dp))
                    .padding(4.dp)
            ) {
                // Register as Parent
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (uiState.registerRole == RegisterRole.PARENT) White else Color.Transparent)
                        .clickable { viewModel.setRegisterRole(RegisterRole.PARENT) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Register as Parent",
                        color = if (uiState.registerRole == RegisterRole.PARENT) Black else CyberTextDim,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                // Register as Student
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (uiState.registerRole == RegisterRole.STUDENT) White else Color.Transparent)
                        .clickable { viewModel.setRegisterRole(RegisterRole.STUDENT) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Register as Student",
                        color = if (uiState.registerRole == RegisterRole.STUDENT) Black else CyberTextDim,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // MAIN FORM CARD
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(CyberCard)
                .border(1.dp, CyberBorder, RoundedCornerShape(8.dp))
                .padding(18.dp)
        ) {
            Column {
                if (!uiState.isRegistering) {
                    // === LOGIN FORM ===
                    OutlinedTextField(
                        value = uiState.email,
                        onValueChange = viewModel::onEmailChange,
                        label = { Text("Email Address") },
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
                        value = uiState.pass,
                        onValueChange = viewModel::onPasswordChange,
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
                        onClick = viewModel::login,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = Black)
                    ) {
                        Text("Login", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }

                } else if (uiState.registerRole == RegisterRole.PARENT) {
                    // === REGISTER AS PARENT (WITH GMAIL OTP) ===
                    Text("Parent Details", color = CyberTextBright, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = uiState.name,
                        onValueChange = viewModel::onNameChange,
                        label = { Text("Parent Full Name") },
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
                        value = uiState.phone,
                        onValueChange = viewModel::onPhoneChange,
                        label = { Text("Phone Number") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
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
                        value = uiState.email,
                        onValueChange = viewModel::onEmailChange,
                        label = { Text("Gmail Address") },
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
                        value = uiState.pass,
                        onValueChange = viewModel::onPasswordChange,
                        label = { Text("Create Password") },
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

                    Spacer(modifier = Modifier.height(16.dp))

                    // GMAIL OTP VERIFICATION BOX
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(CyberBlack)
                            .border(1.dp, if (uiState.isOtpVerified) SuccessGreen else CyberBorder, RoundedCornerShape(8.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Gmail Verification", color = CyberTextBright, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                if (uiState.isOtpVerified) {
                                    Text("✓ Verified", color = SuccessGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            if (!uiState.isOtpSent && !uiState.isOtpVerified) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = viewModel::sendGmailOtp,
                                    modifier = Modifier.fillMaxWidth().height(42.dp),
                                    shape = RoundedCornerShape(4.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = CyberBorder, contentColor = White)
                                ) {
                                    Text("Send OTP to Gmail", fontSize = 13.sp)
                                }
                            } else if (!uiState.isOtpVerified) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Code sent to ${uiState.email} (Demo code: ${uiState.generatedOtp})",
                                    color = SuccessGreen,
                                    fontSize = 11.sp
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                    OutlinedTextField(
                                        value = uiState.enteredOtp,
                                        onValueChange = viewModel::onEnteredOtpChange,
                                        label = { Text("6-Digit OTP") },
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
                                    Button(
                                        onClick = viewModel::verifyGmailOtp,
                                        modifier = Modifier.height(52.dp),
                                        shape = RoundedCornerShape(4.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = Black)
                                    ) {
                                        Text("Verify", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }

                                uiState.otpError?.let { err ->
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(err, color = AlertRed, fontSize = 11.sp)
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
                        Text("Register Parent Account", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                } else {
                    // === REGISTER AS STUDENT / CHILD ===
                    Text("Student / Child Info", color = CyberTextBright, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text("Physical metrics and daily health goals", color = CyberTextDim, fontSize = 11.sp)

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = uiState.childName,
                        onValueChange = viewModel::onChildNameChange,
                        label = { Text("Student Name") },
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
                            value = uiState.childAge,
                            onValueChange = viewModel::onChildAgeChange,
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
                        Spacer(modifier = Modifier.width(10.dp))
                        OutlinedTextField(
                            value = uiState.childDob,
                            onValueChange = viewModel::onChildDobChange,
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
                    Text("Gender", color = CyberTextDim, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        listOf("Boy", "Girl").forEach { g ->
                            val selected = uiState.childGender == g
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (selected) White else CyberBlack)
                                    .border(1.dp, if (selected) White else CyberBorder, RoundedCornerShape(4.dp))
                                    .clickable { viewModel.onChildGenderChange(g) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = g,
                                    color = if (selected) Black else CyberTextBright,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = uiState.childWeight,
                            onValueChange = viewModel::onChildWeightChange,
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
                        Spacer(modifier = Modifier.width(10.dp))
                        OutlinedTextField(
                            value = uiState.childHeight,
                            onValueChange = viewModel::onChildHeightChange,
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
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = uiState.childGoal,
                        onValueChange = viewModel::onChildGoalChange,
                        label = { Text("Daily Step Goal") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = CyberTextBright,
                            unfocusedTextColor = CyberTextBright,
                            focusedBorderColor = CyberTextBright,
                            unfocusedBorderColor = CyberBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Real-time BMI Display Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(CyberBlack)
                            .border(1.dp, CyberBorder, RoundedCornerShape(6.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Calculated BMI", color = CyberTextDim, fontSize = 12.sp)
                            val category = when {
                                uiState.calculatedBmi < 15.0f -> "Underweight"
                                uiState.calculatedBmi <= 19.5f -> "Healthy Weight"
                                uiState.calculatedBmi <= 24.0f -> "Overweight"
                                else -> "Obese"
                            }
                            Text(
                                text = "%.1f (%s)".format(uiState.calculatedBmi, category),
                                color = CyberTextBright,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = viewModel::registerStudent,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = Black)
                    ) {
                        Text("Register Student Device", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // TOGGLE LOGIN / REGISTER
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
