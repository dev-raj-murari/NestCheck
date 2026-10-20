package com.nestcheck.app.ui.auth

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nestcheck.app.core.PasswordValidationResult
import com.nestcheck.app.core.PasswordValidator
import com.nestcheck.app.data.remote.EmailService
import com.nestcheck.app.data.model.ParentContactInfo
import com.nestcheck.app.data.model.HouseholdParentConfig
import com.nestcheck.app.data.sync.RealtimeFamilySyncManager
import com.nestcheck.app.navigation.Screen
import com.nestcheck.app.ui.parent.shell.ParentShellViewModel
import kotlinx.coroutines.launch

enum class AuthStep {
    WELCOME,
    LOGIN,
    SIGNUP,
    OTP_VERIFICATION,
    PARENT_SETUP,
    CHILD_SETUP,
    SUCCESS
}

val AuthPrimaryBlue = Color(0xFF0B57D0)
val AuthLightBg = Color(0xFFF8FAFC)
val AuthCardBg = Color(0xFFFFFFFF)
val AuthTextDark = Color(0xFF0F172A)
val AuthTextMuted = Color(0xFF64748B)
val AuthBorderColor = Color(0xFFE2E8F0)
val AuthSuccessGreen = Color(0xFF10B981)
val AuthErrorRed = Color(0xFFEF4444)
val AuthAmber = Color(0xFFF59E0B)

@Composable
fun FamilyAuthScreen(
    navController: NavController,
    syncManager: RealtimeFamilySyncManager = hiltViewModel<ParentShellViewModel>().syncManager
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var currentStep by remember { mutableStateOf(AuthStep.WELCOME) }
    var emailOrUsername by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isParentRole by remember { mutableStateOf(true) }

    // Real dynamic OTP State
    var generatedOtp by remember { mutableStateOf("") }
    var otpDigit1 by remember { mutableStateOf("") }
    var otpDigit2 by remember { mutableStateOf("") }
    var otpDigit3 by remember { mutableStateOf("") }
    var otpDigit4 by remember { mutableStateOf("") }
    var authError by remember { mutableStateOf<String?>(null) }
    var otpError by remember { mutableStateOf<String?>(null) }
    var isSendingOtp by remember { mutableStateOf(false) }

    // Child Setup Form State
    val syncState by syncManager.syncState.collectAsState()

    // Household Parents (Mom, Dad, Guardian) & Child Emergency Contact State
    var dadName by remember { mutableStateOf(syncState.householdParents.father.name.ifBlank { "Rohit Patil" }) }
    var dadRole by remember { mutableStateOf("Father") }
    var dadEmail by remember { mutableStateOf(syncState.householdParents.father.email.ifBlank { "rohit7681010@gmail.com" }) }
    var dadPhone by remember { mutableStateOf(syncState.householdParents.father.phone.ifBlank { "+91 98765 43210" }) }

    var momName by remember { mutableStateOf(syncState.householdParents.mother.name.ifBlank { "Priya Patil" }) }
    var momRole by remember { mutableStateOf("Mother") }
    var momEmail by remember { mutableStateOf(syncState.householdParents.mother.email.ifBlank { "priya.patil@gmail.com" }) }
    var momPhone by remember { mutableStateOf(syncState.householdParents.mother.phone.ifBlank { "+91 98765 43211" }) }

    var guardianName by remember { mutableStateOf(syncState.householdParents.guardian.name) }
    var guardianRole by remember { mutableStateOf("Guardian") }
    var guardianEmail by remember { mutableStateOf(syncState.householdParents.guardian.email) }
    var guardianPhone by remember { mutableStateOf(syncState.householdParents.guardian.phone) }

    var emergencyContactPhone by remember { mutableStateOf(syncState.emergencyContactPhone.ifBlank { "+91 98765 43210" }) }

    var childName by remember { mutableStateOf("Olivia") }
    var childAgeText by remember { mutableStateOf("11") }
    var dailyLimitHours by remember { mutableStateOf("3") }
    var bedtime by remember { mutableStateOf("9:00 PM") }
    var schoolHours by remember { mutableStateOf("8:30 AM - 3:15 PM") }

    // Gmail Config Dialog State (Safe local storage, never on GitHub!)
    var showGmailConfigDialog by remember { mutableStateOf(false) }
    var senderEmailInput by remember { mutableStateOf(EmailService.getSenderEmail(context)) }
    var secretKeyInput by remember { mutableStateOf(EmailService.getSecretKey(context)) }

    fun sendRealOtp(targetEmail: String) {
        val newCode = (1000..9999).random().toString()
        generatedOtp = newCode
        otpDigit1 = ""
        otpDigit2 = ""
        otpDigit3 = ""
        otpDigit4 = ""
        otpError = null
        isSendingOtp = true

        android.util.Log.i("FamilyAuth", "==================================================")
        android.util.Log.i("FamilyAuth", "VERIFICATION OTP CODE FOR $targetEmail: $newCode")
        android.util.Log.i("FamilyAuth", "==================================================")

        scope.launch {
            val result = EmailService.sendOtpEmail(context, targetEmail, newCode)
            isSendingOtp = false
            if (result.isSuccess) {
                Toast.makeText(context, "Verification email sent to $targetEmail!", Toast.LENGTH_SHORT).show()
                currentStep = AuthStep.OTP_VERIFICATION
            } else {
                val err = result.exceptionOrNull()?.message ?: "Failed to send email"
                authError = "Email delivery note: $err"
                // Proceed so user can enter OTP or view helper code
                currentStep = AuthStep.OTP_VERIFICATION
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AuthLightBg)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        when (currentStep) {
            AuthStep.WELCOME -> {
                WelcomeView(
                    onLoginClick = {
                        authError = null
                        currentStep = AuthStep.LOGIN
                    },
                    onSignUpClick = {
                        authError = null
                        currentStep = AuthStep.SIGNUP
                    },
                    onConfigureEmailClick = {
                        showGmailConfigDialog = true
                    }
                )
            }
            AuthStep.LOGIN -> {
                LoginView(
                    email = emailOrUsername,
                    onEmailChange = {
                        emailOrUsername = it
                        authError = null
                    },
                    password = password,
                    onPasswordChange = {
                        password = it
                        authError = null
                    },
                    isParent = isParentRole,
                    onRoleToggle = {
                        isParentRole = it
                        authError = null
                    },
                    errorMessage = authError,
                    isLoading = isSendingOtp,
                    onBackClick = { currentStep = AuthStep.WELCOME },
                    onLoginSubmit = {
                        if (isParentRole) {
                            if (!PasswordValidator.isEmailValid(emailOrUsername)) {
                                authError = "Please enter a valid email address (e.g. parent@gmail.com)"
                                return@LoginView
                            }
                            if (password.length < 8) {
                                authError = "Password must be at least 8 characters long."
                                return@LoginView
                            }
                            sendRealOtp(emailOrUsername)
                        } else {
                            if (emailOrUsername.isBlank()) {
                                authError = "Enter child username or household pairing code."
                                return@LoginView
                            }
                            if (password.length < 4) {
                                authError = "Enter your 4-digit Child PIN."
                                return@LoginView
                            }
                            // Kid login triggers parent approval gate
                            syncManager.requestChildDeviceApproval("Pixel 9 (${emailOrUsername.trim()})")
                            navController.navigate(Screen.KidHome.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        }
                    },
                    onConfigureHouseholdParents = { currentStep = AuthStep.PARENT_SETUP },
                    onSignUpNav = {
                        authError = null
                        currentStep = AuthStep.SIGNUP
                    }
                )
            }
            AuthStep.SIGNUP -> {
                SignUpView(
                    email = emailOrUsername,
                    onEmailChange = {
                        emailOrUsername = it
                        authError = null
                    },
                    password = password,
                    onPasswordChange = {
                        password = it
                        authError = null
                    },
                    errorMessage = authError,
                    isLoading = isSendingOtp,
                    onBackClick = { currentStep = AuthStep.WELCOME },
                    onSignUpSubmit = {
                        if (!PasswordValidator.isEmailValid(emailOrUsername)) {
                            authError = "Please enter a valid email address (e.g. name@gmail.com)."
                            return@SignUpView
                        }
                        val validation = PasswordValidator.validate(password)
                        if (!validation.isValid) {
                            authError = "Password does not meet all security requirements."
                            return@SignUpView
                        }
                        sendRealOtp(emailOrUsername)
                    },
                    onConfigureHouseholdParents = { currentStep = AuthStep.PARENT_SETUP },
                    onLoginNav = {
                        authError = null
                        currentStep = AuthStep.LOGIN
                    }
                )
            }
            AuthStep.OTP_VERIFICATION -> {
                OtpVerificationView(
                    recipientEmail = emailOrUsername,
                    digit1 = otpDigit1,
                    onDigit1Change = { if (it.length <= 1) { otpDigit1 = it; otpError = null } },
                    digit2 = otpDigit2,
                    onDigit2Change = { if (it.length <= 1) { otpDigit2 = it; otpError = null } },
                    digit3 = otpDigit3,
                    onDigit3Change = { if (it.length <= 1) { otpDigit3 = it; otpError = null } },
                    digit4 = otpDigit4,
                    onDigit4Change = { if (it.length <= 1) { otpDigit4 = it; otpError = null } },
                    errorMessage = otpError,
                    isLoading = isSendingOtp,
                    debugOtpCode = generatedOtp,
                    onAutofillCode = { code ->
                        if (code.length == 4) {
                            otpDigit1 = code[0].toString()
                            otpDigit2 = code[1].toString()
                            otpDigit3 = code[2].toString()
                            otpDigit4 = code[3].toString()
                            otpError = null
                        }
                    },
                    onBackClick = { currentStep = AuthStep.LOGIN },
                    onResendCode = { sendRealOtp(emailOrUsername) },
                    onVerifyClick = {
                        val entered = "$otpDigit1$otpDigit2$otpDigit3$otpDigit4"
                        if (entered.length < 4) {
                            otpError = "Please enter all 4 digits."
                        } else if (entered != generatedOtp) {
                            otpError = "Invalid verification code! Please enter the exact 4-digit code sent to your email."
                        } else {
                            otpError = null
                            if (isParentRole) {
                                currentStep = AuthStep.PARENT_SETUP
                            } else {
                                currentStep = AuthStep.SUCCESS
                            }
                        }
                    }
                )
            }
            AuthStep.PARENT_SETUP -> {
                HouseholdParentsSetupView(
                    dadName = dadName,
                    onDadNameChange = { dadName = it },
                    dadEmail = dadEmail,
                    onDadEmailChange = { dadEmail = it },
                    dadPhone = dadPhone,
                    onDadPhoneChange = { dadPhone = it },
                    momName = momName,
                    onMomNameChange = { momName = it },
                    momEmail = momEmail,
                    onMomEmailChange = { momEmail = it },
                    momPhone = momPhone,
                    onMomPhoneChange = { momPhone = it },
                    guardianName = guardianName,
                    onGuardianNameChange = { guardianName = it },
                    guardianEmail = guardianEmail,
                    onGuardianEmailChange = { guardianEmail = it },
                    guardianPhone = guardianPhone,
                    onGuardianPhoneChange = { guardianPhone = it },
                    emergencyContactPhone = emergencyContactPhone,
                    onEmergencyContactPhoneChange = { emergencyContactPhone = it },
                    onBackClick = { currentStep = AuthStep.LOGIN },
                    onSaveAndContinue = {
                        val activeRole = if (dadName.isNotBlank()) "Father" else if (momName.isNotBlank()) "Mother" else "Guardian"
                        val emergencyPhoneToUse = if (emergencyContactPhone.isNotBlank()) emergencyContactPhone.trim() else dadPhone.ifBlank { momPhone.ifBlank { guardianPhone } }
                        syncManager.updateParentContactDetails(
                            father = ParentContactInfo(name = dadName.trim(), role = "Father", email = dadEmail.trim(), phone = dadPhone.trim()),
                            mother = ParentContactInfo(name = momName.trim(), role = "Mother", email = momEmail.trim(), phone = momPhone.trim()),
                            guardian = ParentContactInfo(name = guardianName.trim(), role = "Guardian", email = guardianEmail.trim(), phone = guardianPhone.trim()),
                            emergencyPhone = emergencyPhoneToUse,
                            activeRole = activeRole
                        )
                        currentStep = AuthStep.CHILD_SETUP
                    }
                )
            }
            AuthStep.CHILD_SETUP -> {
                ChildSetupView(
                    childName = childName,
                    onChildNameChange = { childName = it },
                    age = childAgeText,
                    onAgeChange = { childAgeText = it },
                    dailyLimit = dailyLimitHours,
                    onDailyLimitChange = { dailyLimitHours = it },
                    bedtime = bedtime,
                    onBedtimeChange = { bedtime = it },
                    schoolHours = schoolHours,
                    onSchoolHoursChange = { schoolHours = it },
                    onSaveAndContinue = {
                        val ageNum = childAgeText.toIntOrNull() ?: 11
                        val limitMins = (dailyLimitHours.toFloatOrNull() ?: 3f) * 60
                        syncManager.updateChildProfile(
                            name = childName.trim().ifBlank { "Olivia" },
                            age = ageNum,
                            dailyLimitMinutes = limitMins.toInt(),
                            bedtime = bedtime,
                            schoolHours = schoolHours
                        )
                        currentStep = AuthStep.SUCCESS
                    }
                )
            }
            AuthStep.SUCCESS -> {
                SuccessView(
                    isParent = isParentRole,
                    childName = childName,
                    onContinueClick = {
                        if (isParentRole) {
                            navController.navigate(Screen.ParentDashboard.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        } else {
                            navController.navigate(Screen.KidHome.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        }
                    }
                )
            }
        }

        // Secure In-App Gmail Key Configuration Modal (Stored Locally Only!)
        if (showGmailConfigDialog) {
            AlertDialog(
                onDismissRequest = { showGmailConfigDialog = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Key, contentDescription = null, tint = AuthPrimaryBlue)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Gmail API / App Password", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                },
                text = {
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        Text(
                            "Configure your Gmail App Password or API Key to send real OTP verification emails. This is saved securely to private device storage and is NEVER published to GitHub.",
                            color = AuthTextMuted,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        OutlinedTextField(
                            value = senderEmailInput,
                            onValueChange = { senderEmailInput = it },
                            label = { Text("Sender Gmail Address") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = secretKeyInput,
                            onValueChange = { secretKeyInput = it },
                            label = { Text("Gmail App Password / API Key") },
                            placeholder = { Text("16-char app password or key") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            EmailService.saveCredentials(context, senderEmailInput, secretKeyInput)
                            showGmailConfigDialog = false
                            Toast.makeText(context, "Gmail credentials saved locally!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AuthPrimaryBlue)
                    ) {
                        Text("Save Privately", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showGmailConfigDialog = false }) {
                        Text("Cancel", color = AuthTextMuted)
                    }
                }
            )
        }
    }
}

@Composable
fun WelcomeView(
    onLoginClick: () -> Unit,
    onSignUpClick: () -> Unit,
    onConfigureEmailClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Shield with Golden Padlock Emblem
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .clip(RoundedCornerShape(36.dp))
                    .background(AuthPrimaryBlue),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Shield,
                        contentDescription = "Security Shield",
                        tint = Color.White,
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Icon(
                        imageVector = Icons.Outlined.Lock,
                        contentDescription = "Padlock",
                        tint = Color(0xFFFFB703),
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "NestCheck",
                color = AuthTextDark,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Smart parenting, healthy screen time, and real-time family safety.",
                color = AuthTextMuted,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // Action Buttons
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AuthPrimaryBlue, contentColor = Color.White)
            ) {
                Text("Log in", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onSignUpClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, AuthPrimaryBlue),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AuthPrimaryBlue)
            ) {
                Text("Sign up", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable(onClick = onConfigureEmailClick)
            ) {
                Icon(Icons.Outlined.Key, contentDescription = null, tint = AuthTextMuted, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "Configure Gmail Key / App Password",
                    color = AuthPrimaryBlue,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun LoginView(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    isParent: Boolean,
    onRoleToggle: (Boolean) -> Unit,
    errorMessage: String?,
    isLoading: Boolean,
    onBackClick: () -> Unit,
    onLoginSubmit: () -> Unit,
    onSignUpNav: () -> Unit,
    onConfigureHouseholdParents: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(scrollState)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable(onClick = onBackClick)
        ) {
            Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back", tint = AuthTextDark)
            Spacer(modifier = Modifier.width(6.dp))
            Text("Back", color = AuthTextDark, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text("Log in", color = AuthTextDark, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text("Login to manage your family account", color = AuthTextMuted, fontSize = 14.sp)

        Spacer(modifier = Modifier.height(20.dp))

        // Role Selector Pill
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .clip(RoundedCornerShape(23.dp))
                .background(Color(0xFFE2E8F0))
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isParent) Color.White else Color.Transparent)
                    .clickable { onRoleToggle(true) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Parent Account",
                    color = if (isParent) AuthPrimaryBlue else AuthTextMuted,
                    fontWeight = if (isParent) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 13.sp
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (!isParent) Color.White else Color.Transparent)
                    .clickable { onRoleToggle(false) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Child Space",
                    color = if (!isParent) AuthPrimaryBlue else AuthTextMuted,
                    fontWeight = if (!isParent) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(if (isParent) "Email address (e.g. name@gmail.com)" else "Child Name / Pairing Code") },
            leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null, tint = AuthTextMuted) },
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = AuthPrimaryBlue,
                unfocusedBorderColor = AuthBorderColor,
                focusedTextColor = AuthTextDark,
                unfocusedTextColor = AuthTextDark
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(if (isParent) "Password" else "Child 4-Digit PIN") },
            leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null, tint = AuthTextMuted) },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                        contentDescription = null,
                        tint = AuthTextMuted
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = AuthPrimaryBlue,
                unfocusedBorderColor = AuthBorderColor,
                focusedTextColor = AuthTextDark,
                unfocusedTextColor = AuthTextDark
            )
        )

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                color = Color(0xFFFEF2F2),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCA5A5))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.ErrorOutline, contentDescription = null, tint = AuthErrorRed, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(errorMessage, color = AuthErrorRed, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onLoginSubmit,
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AuthPrimaryBlue, contentColor = Color.White)
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                Spacer(modifier = Modifier.width(10.dp))
                Text("Sending Verification Code...", fontSize = 15.sp)
            } else {
                Text(if (isParent) "Send Verification Code" else "Log In to Child Space", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text("Don't have an account? ", color = AuthTextMuted, fontSize = 13.sp)
            Text(
                "SIGN UP",
                color = AuthPrimaryBlue,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable(onClick = onSignUpNav)
            )
        }
    }
}

@Composable
fun SignUpView(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    errorMessage: String?,
    isLoading: Boolean,
    onBackClick: () -> Unit,
    onSignUpSubmit: () -> Unit,
    onLoginNav: () -> Unit,
    onConfigureHouseholdParents: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    var passwordVisible by remember { mutableStateOf(false) }
    val passwordValidation = remember(password) { PasswordValidator.validate(password) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(scrollState)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable(onClick = onBackClick)
        ) {
            Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back", tint = AuthTextDark)
            Spacer(modifier = Modifier.width(6.dp))
            Text("Back", color = AuthTextDark, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Create Account", color = AuthTextDark, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text("Create your master parental account", color = AuthTextMuted, fontSize = 14.sp)

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Parent Email address (e.g. name@gmail.com)") },
            leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null, tint = AuthTextMuted) },
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = AuthPrimaryBlue,
                unfocusedBorderColor = AuthBorderColor,
                focusedTextColor = AuthTextDark,
                unfocusedTextColor = AuthTextDark
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Master Password") },
            leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null, tint = AuthTextMuted) },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                        contentDescription = null,
                        tint = AuthTextMuted
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = AuthPrimaryBlue,
                unfocusedBorderColor = AuthBorderColor,
                focusedTextColor = AuthTextDark,
                unfocusedTextColor = AuthTextDark
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Live Password Rules Checklist & Strength Meter
        PasswordRulesChecklist(validation = passwordValidation)

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                color = Color(0xFFFEF2F2),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCA5A5))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.ErrorOutline, contentDescription = null, tint = AuthErrorRed, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(errorMessage, color = AuthErrorRed, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onSignUpSubmit,
            enabled = !isLoading && passwordValidation.isValid,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AuthPrimaryBlue,
                contentColor = Color.White,
                disabledContainerColor = Color(0xFFCBD5E1)
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                Spacer(modifier = Modifier.width(10.dp))
                Text("Sending Verification OTP...", fontSize = 15.sp)
            } else {
                Text("Send Verification OTP", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text("Already have an account? ", color = AuthTextMuted, fontSize = 13.sp)
            Text(
                "LOG IN",
                color = AuthPrimaryBlue,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable(onClick = onLoginNav)
            )
        }
    }
}

@Composable
fun PasswordRulesChecklist(validation: PasswordValidationResult) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF8FAFC),
        border = androidx.compose.foundation.BorderStroke(1.dp, AuthBorderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Password Security Checklist", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = AuthTextDark)
                val strengthText = when {
                    validation.strengthScore >= 1f -> "Strong"
                    validation.strengthScore >= 0.6f -> "Medium"
                    else -> "Weak"
                }
                val strengthColor = when {
                    validation.strengthScore >= 1f -> AuthSuccessGreen
                    validation.strengthScore >= 0.6f -> AuthAmber
                    else -> AuthErrorRed
                }
                Text(strengthText, color = strengthColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { validation.strengthScore },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = when {
                    validation.strengthScore >= 1f -> AuthSuccessGreen
                    validation.strengthScore >= 0.6f -> AuthAmber
                    else -> AuthErrorRed
                },
                trackColor = Color(0xFFE2E8F0)
            )

            Spacer(modifier = Modifier.height(10.dp))

            PasswordRequirementRow(label = "At least 8 characters long", met = validation.hasMinLength)
            PasswordRequirementRow(label = "At least 1 uppercase letter (A-Z)", met = validation.hasUppercase)
            PasswordRequirementRow(label = "At least 1 lowercase letter (a-z)", met = validation.hasLowercase)
            PasswordRequirementRow(label = "At least 1 number (0-9)", met = validation.hasDigit)
            PasswordRequirementRow(label = "At least 1 special symbol (!@#$...)", met = validation.hasSpecialChar)
        }
    }
}

@Composable
fun PasswordRequirementRow(label: String, met: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (met) Icons.Outlined.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (met) AuthSuccessGreen else Color(0xFF94A3B8),
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            fontSize = 11.5.sp,
            color = if (met) AuthTextDark else AuthTextMuted,
            fontWeight = if (met) FontWeight.Medium else FontWeight.Normal
        )
    }
}

@Composable
fun OtpVerificationView(
    recipientEmail: String,
    digit1: String,
    onDigit1Change: (String) -> Unit,
    digit2: String,
    onDigit2Change: (String) -> Unit,
    digit3: String,
    onDigit3Change: (String) -> Unit,
    digit4: String,
    onDigit4Change: (String) -> Unit,
    errorMessage: String?,
    isLoading: Boolean,
    debugOtpCode: String? = null,
    onAutofillCode: ((String) -> Unit)? = null,
    onBackClick: () -> Unit,
    onResendCode: () -> Unit,
    onVerifyClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back", tint = AuthTextDark)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Verify Your Email", color = AuthTextDark, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (recipientEmail.isNotBlank()) "We sent a 4-digit code to\n$recipientEmail" else "Enter 4-digit verification code",
                color = AuthTextMuted,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Phone with Checkmark Illustration
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFFEFF6FF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.MarkEmailRead,
                    contentDescription = null,
                    tint = AuthPrimaryBlue,
                    modifier = Modifier.size(64.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 4 OTP Boxes
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OtpBox(digit1, onDigit1Change)
                OtpBox(digit2, onDigit2Change)
                OtpBox(digit3, onDigit3Change)
                OtpBox(digit4, onDigit4Change)
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(errorMessage, color = AuthErrorRed, fontSize = 13.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
            }

            if (!debugOtpCode.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF1F5F9),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Info, contentDescription = null, tint = AuthPrimaryBlue, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Testing Code: ", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AuthTextDark)
                            Text(debugOtpCode, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = AuthPrimaryBlue)
                        }
                        if (onAutofillCode != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tap here to autofill",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AuthPrimaryBlue,
                                modifier = Modifier.clickable { onAutofillCode(debugOtpCode) }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable(enabled = !isLoading, onClick = onResendCode)
            ) {
                Text("Didn't get the email? ", color = AuthTextMuted, fontSize = 13.sp)
                Text("Resend Code", color = AuthPrimaryBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }

        Button(
            onClick = onVerifyClick,
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AuthPrimaryBlue, contentColor = Color.White)
        ) {
            Text("Verify & Continue", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun OtpBox(value: String, onValueChange: (String) -> Unit) {
    Box(
        modifier = Modifier
            .size(60.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.5.dp, if (value.isNotEmpty()) AuthPrimaryBlue else AuthBorderColor, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedTextColor = AuthTextDark,
                unfocusedTextColor = AuthTextDark
            ),
            textStyle = LocalTextStyle.current.copy(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        )
    }
}

@Composable
fun ChildSetupView(
    childName: String,
    onChildNameChange: (String) -> Unit,
    age: String,
    onAgeChange: (String) -> Unit,
    dailyLimit: String,
    onDailyLimitChange: (String) -> Unit,
    bedtime: String,
    onBedtimeChange: (String) -> Unit,
    schoolHours: String,
    onSchoolHoursChange: (String) -> Unit,
    onSaveAndContinue: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Spacer(modifier = Modifier.height(16.dp))

            Text("Child Profile Setup", color = AuthTextDark, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                "Configure safety rules and screen time limits for your child.",
                color = AuthTextMuted,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = childName,
                onValueChange = onChildNameChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Child's First Name") },
                leadingIcon = { Icon(Icons.Outlined.Face, contentDescription = null, tint = AuthTextMuted) },
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = age,
                onValueChange = onAgeChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Age (Years)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingIcon = { Icon(Icons.Outlined.Cake, contentDescription = null, tint = AuthTextMuted) },
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = dailyLimit,
                onValueChange = onDailyLimitChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Daily Screen Time Allowance (Hours)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingIcon = { Icon(Icons.Outlined.HourglassEmpty, contentDescription = null, tint = AuthTextMuted) },
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = bedtime,
                onValueChange = onBedtimeChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Bedtime Schedule") },
                leadingIcon = { Icon(Icons.Outlined.Bedtime, contentDescription = null, tint = AuthTextMuted) },
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = schoolHours,
                onValueChange = onSchoolHoursChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("School Downtime Hours") },
                leadingIcon = { Icon(Icons.Outlined.School, contentDescription = null, tint = AuthTextMuted) },
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onSaveAndContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AuthPrimaryBlue, contentColor = Color.White)
        ) {
            Text("Complete Setup & Enter Dashboard", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SuccessView(
    isParent: Boolean,
    childName: String,
    onContinueClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(AuthSuccessGreen.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(AuthSuccessGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Check,
                        contentDescription = "Success",
                        tint = Color.White,
                        modifier = Modifier.size(60.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            Text("Setup Complete!", color = AuthTextDark, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                if (isParent) "Parent Controls active for $childName" else "Connected to Household Family Space",
                color = AuthTextMuted,
                fontSize = 15.sp,
                textAlign = TextAlign.Center
            )
        }

        Button(
            onClick = onContinueClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AuthPrimaryBlue, contentColor = Color.White)
        ) {
            Text("Launch NestCheck", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}



@Composable
fun HouseholdParentsSetupView(
    dadName: String,
    onDadNameChange: (String) -> Unit,
    dadEmail: String,
    onDadEmailChange: (String) -> Unit,
    dadPhone: String,
    onDadPhoneChange: (String) -> Unit,
    momName: String,
    onMomNameChange: (String) -> Unit,
    momEmail: String,
    onMomEmailChange: (String) -> Unit,
    momPhone: String,
    onMomPhoneChange: (String) -> Unit,
    guardianName: String,
    onGuardianNameChange: (String) -> Unit,
    guardianEmail: String,
    onGuardianEmailChange: (String) -> Unit,
    guardianPhone: String,
    onGuardianPhoneChange: (String) -> Unit,
    emergencyContactPhone: String,
    onEmergencyContactPhoneChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onSaveAndContinue: () -> Unit
) {
    val scrollState = rememberScrollState()
    var validationError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(scrollState)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable(onClick = onBackClick)
        ) {
            Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back", tint = AuthTextDark)
            Spacer(modifier = Modifier.width(6.dp))
            Text("Back", color = AuthTextDark, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text("Household Parents & Safety", color = AuthTextDark, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            "Add parents info (Dad, Mom, Guardian). Minimum 1 parent required. Provide child's emergency contact for direct SOS fast-dialing.",
            color = AuthTextMuted,
            fontSize = 13.sp,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Father (Dad) Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AuthCardBg),
            border = androidx.compose.foundation.BorderStroke(1.dp, AuthBorderColor)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("\uD83D\uDC68", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Father / Dad Information", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = AuthTextDark)
                    Spacer(modifier = Modifier.weight(1f))
                    Surface(
                        color = AuthPrimaryBlue.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Father", color = AuthPrimaryBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = dadName,
                    onValueChange = { onDadNameChange(it); validationError = null },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Your Name (Father)") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = dadEmail,
                    onValueChange = { onDadEmailChange(it); validationError = null },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Father's Email") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = dadPhone,
                    onValueChange = { onDadPhoneChange(it); validationError = null },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Father's Phone Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mother (Mom) Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AuthCardBg),
            border = androidx.compose.foundation.BorderStroke(1.dp, AuthBorderColor)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("\uD83D\uDC69", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Mother / Mom Information", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = AuthTextDark)
                    Spacer(modifier = Modifier.weight(1f))
                    Surface(
                        color = Color(0xFFFCE7F3),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Mother", color = Color(0xFFBE185D), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = momName,
                    onValueChange = { onMomNameChange(it); validationError = null },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Your Name (Mother)") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = momEmail,
                    onValueChange = { onMomEmailChange(it); validationError = null },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Mother's Email") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = momPhone,
                    onValueChange = { onMomPhoneChange(it); validationError = null },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Mother's Phone Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Guardian Card (Optional)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AuthCardBg),
            border = androidx.compose.foundation.BorderStroke(1.dp, AuthBorderColor)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("\uD83D\uDEE1\uFE0F", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Guardian Information (Optional)", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = AuthTextDark)
                    Spacer(modifier = Modifier.weight(1f))
                    Surface(
                        color = Color(0xFFFEF3C7),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Guardian", color = Color(0xFFB45309), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = guardianName,
                    onValueChange = { onGuardianNameChange(it); validationError = null },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Guardian Name") },
                    placeholder = { Text("e.g. Uncle / Aunt / Co-Guardian") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = guardianEmail,
                    onValueChange = { onGuardianEmailChange(it); validationError = null },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Guardian's Email") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = guardianPhone,
                    onValueChange = { onGuardianPhoneChange(it); validationError = null },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Guardian's Phone Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Child's Emergency Fast-Dial Contact Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFCA5A5))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Emergency, contentDescription = null, tint = AuthErrorRed, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Child's Emergency Fast-Dial SOS", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = AuthErrorRed)
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    "When the child taps the SOS button on their app, this phone number will be dialed directly (calls parent directly, NOT 112), and the parents app receives an instant emergency alert.",
                    fontSize = 12.sp,
                    color = AuthTextMuted,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = emergencyContactPhone,
                    onValueChange = { onEmergencyContactPhoneChange(it); validationError = null },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Emergency Phone Number") },
                    placeholder = { Text("+91 98765 43210") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AuthErrorRed,
                        unfocusedBorderColor = Color(0xFFFCA5A5)
                    )
                )
            }
        }

        if (validationError != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(validationError!!, color = AuthErrorRed, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val hasDad = dadName.isNotBlank() && (dadPhone.isNotBlank() || dadEmail.isNotBlank())
                val hasMom = momName.isNotBlank() && (momPhone.isNotBlank() || momEmail.isNotBlank())
                val hasGuardian = guardianName.isNotBlank() && (guardianPhone.isNotBlank() || guardianEmail.isNotBlank())

                if (!hasDad && !hasMom && !hasGuardian) {
                    validationError = "Please fill in details for at least ONE parent (Father, Mother, or Guardian)."
                    return@Button
                }
                validationError = null
                onSaveAndContinue()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AuthPrimaryBlue, contentColor = Color.White)
        ) {
            Text("Save Household & Continue", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
