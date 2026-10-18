package com.nestcheck.app.ui.auth

import android.util.Patterns
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nestcheck.app.navigation.Screen

enum class AuthStep {
    WELCOME,
    LOGIN,
    SIGNUP,
    OTP_VERIFICATION,
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

@Composable
fun FamilyAuthScreen(
    navController: NavController
) {
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

    fun sendOtpAndProceed() {
        val newCode = (1000..9999).random().toString()
        generatedOtp = newCode
        otpDigit1 = ""
        otpDigit2 = ""
        otpDigit3 = ""
        otpDigit4 = ""
        otpError = null
        currentStep = AuthStep.OTP_VERIFICATION
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
                    onGoogleSignInClick = {
                        emailOrUsername = "parent.google@nestcheck.family"
                        password = "GoogleSecurePassword"
                        sendOtpAndProceed()
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
                    onBackClick = { currentStep = AuthStep.WELCOME },
                    onLoginSubmit = {
                        if (isParentRole) {
                            if (emailOrUsername.isBlank() || !emailOrUsername.contains("@")) {
                                authError = "Please enter a valid email address (e.g. parent@family.com)"
                                return@LoginView
                            }
                            if (password.length < 6) {
                                authError = "Password must be at least 6 characters long."
                                return@LoginView
                            }
                        } else {
                            if (emailOrUsername.isBlank()) {
                                authError = "Enter child username or pairing ID."
                                return@LoginView
                            }
                            if (password.length < 4) {
                                authError = "Enter your 4-digit Child PIN."
                                return@LoginView
                            }
                        }
                        sendOtpAndProceed()
                    },
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
                    onBackClick = { currentStep = AuthStep.WELCOME },
                    onSignUpSubmit = {
                        if (emailOrUsername.isBlank() || !emailOrUsername.contains("@")) {
                            authError = "Please enter a valid email address."
                            return@SignUpView
                        }
                        if (password.length < 6) {
                            authError = "Master password must be at least 6 characters."
                            return@SignUpView
                        }
                        sendOtpAndProceed()
                    },
                    onLoginNav = {
                        authError = null
                        currentStep = AuthStep.LOGIN
                    }
                )
            }
            AuthStep.OTP_VERIFICATION -> {
                OtpVerificationView(
                    recipientEmail = emailOrUsername,
                    generatedCodeForDemo = generatedOtp,
                    digit1 = otpDigit1,
                    onDigit1Change = { if (it.length <= 1) { otpDigit1 = it; otpError = null } },
                    digit2 = otpDigit2,
                    onDigit2Change = { if (it.length <= 1) { otpDigit2 = it; otpError = null } },
                    digit3 = otpDigit3,
                    onDigit3Change = { if (it.length <= 1) { otpDigit3 = it; otpError = null } },
                    digit4 = otpDigit4,
                    onDigit4Change = { if (it.length <= 1) { otpDigit4 = it; otpError = null } },
                    errorMessage = otpError,
                    onBackClick = { currentStep = AuthStep.LOGIN },
                    onResendCode = { sendOtpAndProceed() },
                    onVerifyClick = {
                        val entered = "$otpDigit1$otpDigit2$otpDigit3$otpDigit4"
                        if (entered.length < 4) {
                            otpError = "Please enter all 4 digits."
                        } else if (entered != generatedOtp) {
                            otpError = "Invalid verification code! Please enter the exact 4-digit code."
                        } else {
                            otpError = null
                            currentStep = AuthStep.SUCCESS
                        }
                    }
                )
            }
            AuthStep.SUCCESS -> {
                SuccessView(
                    isParent = isParentRole,
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
    }
}

@Composable
fun WelcomeView(
    onLoginClick: () -> Unit,
    onSignUpClick: () -> Unit,
    onGoogleSignInClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Shield with Golden Padlock Emblem
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(200.dp)
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
                        modifier = Modifier.size(90.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Icon(
                        imageVector = Icons.Outlined.Lock,
                        contentDescription = "Padlock",
                        tint = Color(0xFFFFB703),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            Text(
                text = "Hello There!",
                color = AuthTextDark,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Protect your family with Google Family Link style controls, healthy screen time, and school schedule monitoring.",
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

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable(onClick = onGoogleSignInClick)
            ) {
                Text("Or continue with ", color = AuthTextMuted, fontSize = 13.sp)
                Text(
                    "Google Sign-In",
                    color = AuthPrimaryBlue,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
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
    onBackClick: () -> Unit,
    onLoginSubmit: () -> Unit,
    onSignUpNav: () -> Unit
) {
    val scrollState = rememberScrollState()

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

        Spacer(modifier = Modifier.height(32.dp))

        Text("Log in", color = AuthTextDark, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text("Login to manage your family account", color = AuthTextMuted, fontSize = 14.sp)

        Spacer(modifier = Modifier.height(24.dp))

        // Role Selector Pill: Parent vs Kid
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
            label = { Text(if (isParent) "Email address" else "Child Name / Pairing ID") },
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
            visualTransformation = PasswordVisualTransformation(),
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
            Spacer(modifier = Modifier.height(8.dp))
            Text(errorMessage, color = AuthErrorRed, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text("Forgot Password?", color = AuthTextMuted, fontSize = 13.sp)
        }

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onLoginSubmit,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AuthPrimaryBlue, contentColor = Color.White)
        ) {
            Text("Log in", fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
    onBackClick: () -> Unit,
    onSignUpSubmit: () -> Unit,
    onLoginNav: () -> Unit
) {
    val scrollState = rememberScrollState()

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

        Spacer(modifier = Modifier.height(32.dp))

        Text("Sign up", color = AuthTextDark, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text("Create your master parental account", color = AuthTextMuted, fontSize = 14.sp)

        Spacer(modifier = Modifier.height(28.dp))

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Parent Email address") },
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
            label = { Text("Create Master Password") },
            leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null, tint = AuthTextMuted) },
            visualTransformation = PasswordVisualTransformation(),
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
            Spacer(modifier = Modifier.height(8.dp))
            Text(errorMessage, color = AuthErrorRed, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onSignUpSubmit,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AuthPrimaryBlue, contentColor = Color.White)
        ) {
            Text("Send Verification OTP", fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
fun OtpVerificationView(
    recipientEmail: String,
    generatedCodeForDemo: String,
    digit1: String,
    onDigit1Change: (String) -> Unit,
    digit2: String,
    onDigit2Change: (String) -> Unit,
    digit3: String,
    onDigit3Change: (String) -> Unit,
    digit4: String,
    onDigit4Change: (String) -> Unit,
    errorMessage: String?,
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

            Text("Verification", color = AuthTextDark, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                if (recipientEmail.isNotBlank()) "Enter 4-digit code sent to $recipientEmail" else "Enter 4-digit security code",
                color = AuthTextMuted,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Demo Security OTP Hint Banner
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFEFF6FF),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.Security, contentDescription = null, tint = AuthPrimaryBlue, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Security OTP: $generatedCodeForDemo", fontWeight = FontWeight.Bold, color = AuthPrimaryBlue, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Phone with Checkmark Illustration (Shutterstock Reference)
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFEFF6FF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.MarkEmailRead,
                    contentDescription = null,
                    tint = AuthPrimaryBlue,
                    modifier = Modifier.size(68.dp)
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
                Spacer(modifier = Modifier.height(10.dp))
                Text(errorMessage, color = AuthErrorRed, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable(onClick = onResendCode)
            ) {
                Text("Didn't get the code? ", color = AuthTextMuted, fontSize = 13.sp)
                Text("Resend", color = AuthPrimaryBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }

        Button(
            onClick = onVerifyClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AuthPrimaryBlue, contentColor = Color.White)
        ) {
            Text("Verify", fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
fun SuccessView(
    isParent: Boolean,
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
            // Green Checkmark Scalloped Badge (Shutterstock Reference Screen 4)
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

            Text("Success!", color = AuthTextDark, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                if (isParent) "You are now logged into Parent Controls" else "Connected to Household Family Space",
                color = AuthTextMuted,
                fontSize = 15.sp
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
            Text("Continue to App", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}
