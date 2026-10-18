package com.nestcheck.app.ui.auth

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

val AuthPrimaryBlue = Color(0xFF2563EB)
val AuthLightBg = Color(0xFFF8FAFC)
val AuthCardBg = Color(0xFFFFFFFF)
val AuthTextDark = Color(0xFF0F172A)
val AuthTextMuted = Color(0xFF64748B)
val AuthBorderColor = Color(0xFFE2E8F0)
val AuthSuccessGreen = Color(0xFF10B981)

@Composable
fun FamilyAuthScreen(
    navController: NavController
) {
    var currentStep by remember { mutableStateOf(AuthStep.WELCOME) }
    var emailOrUsername by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var otpDigit1 by remember { mutableStateOf("4") }
    var otpDigit2 by remember { mutableStateOf("8") }
    var otpDigit3 by remember { mutableStateOf("2") }
    var otpDigit4 by remember { mutableStateOf("9") }
    var isParentRole by remember { mutableStateOf(true) }

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
                    onLoginClick = { currentStep = AuthStep.LOGIN },
                    onSignUpClick = { currentStep = AuthStep.SIGNUP },
                    onGoogleSignInClick = { currentStep = AuthStep.OTP_VERIFICATION }
                )
            }
            AuthStep.LOGIN -> {
                LoginView(
                    email = emailOrUsername,
                    onEmailChange = { emailOrUsername = it },
                    password = password,
                    onPasswordChange = { password = it },
                    isParent = isParentRole,
                    onRoleToggle = { isParentRole = it },
                    onBackClick = { currentStep = AuthStep.WELCOME },
                    onLoginSubmit = { currentStep = AuthStep.OTP_VERIFICATION },
                    onSignUpNav = { currentStep = AuthStep.SIGNUP }
                )
            }
            AuthStep.SIGNUP -> {
                SignUpView(
                    email = emailOrUsername,
                    onEmailChange = { emailOrUsername = it },
                    password = password,
                    onPasswordChange = { password = it },
                    onBackClick = { currentStep = AuthStep.WELCOME },
                    onSignUpSubmit = { currentStep = AuthStep.OTP_VERIFICATION },
                    onLoginNav = { currentStep = AuthStep.LOGIN }
                )
            }
            AuthStep.OTP_VERIFICATION -> {
                OtpVerificationView(
                    digit1 = otpDigit1,
                    onDigit1Change = { if (it.length <= 1) otpDigit1 = it },
                    digit2 = otpDigit2,
                    onDigit2Change = { if (it.length <= 1) otpDigit2 = it },
                    digit3 = otpDigit3,
                    onDigit3Change = { if (it.length <= 1) otpDigit3 = it },
                    digit4 = otpDigit4,
                    onDigit4Change = { if (it.length <= 1) otpDigit4 = it },
                    onBackClick = { currentStep = AuthStep.LOGIN },
                    onVerifyClick = { currentStep = AuthStep.SUCCESS }
                )
            }
            AuthStep.SUCCESS -> {
                SuccessView(
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

        // Shield Illustration Box (Like Shutterstock Screen 1)
        Box(
            modifier = Modifier
                .size(240.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(AuthPrimaryBlue),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Outlined.Shield,
                    contentDescription = "Shield",
                    tint = Color.White,
                    modifier = Modifier.size(110.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Icon(
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = "Lock",
                    tint = Color(0xFFFBBF24),
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Hello There!",
                color = AuthTextDark,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Protect your family with Google Family Link style controls, healthy screen time, and school schedule monitoring.",
                color = AuthTextMuted,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Log In Primary Pill Button
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

            Spacer(modifier = Modifier.height(14.dp))

            // Sign Up Outline Button
            OutlinedButton(
                onClick = onSignUpClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AuthPrimaryBlue),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, AuthPrimaryBlue)
            ) {
                Text("Sign up", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Continue with Google Social Media
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable(onClick = onGoogleSignInClick)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Or continue with ", color = AuthTextMuted, fontSize = 13.sp)
                Text("Google Sign-In", color = AuthPrimaryBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
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

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Text("Forgot Password?", color = AuthTextMuted, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
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
    onBackClick: () -> Unit,
    onSignUpSubmit: () -> Unit,
    onLoginNav: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
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

        Text("Create Account", color = AuthTextDark, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text("Join NestCheck to supervise and protect your family", color = AuthTextMuted, fontSize = 14.sp)

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Parent Full Name") },
            leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null, tint = AuthTextMuted) },
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
            value = email,
            onValueChange = onEmailChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Email address") },
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
    digit1: String,
    onDigit1Change: (String) -> Unit,
    digit2: String,
    onDigit2Change: (String) -> Unit,
    digit3: String,
    onDigit3Change: (String) -> Unit,
    digit4: String,
    onDigit4Change: (String) -> Unit,
    onBackClick: () -> Unit,
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
            Text("Enter 4-digit code sent to your email", color = AuthTextMuted, fontSize = 14.sp)

            Spacer(modifier = Modifier.height(36.dp))

            // Phone with Checkmark Illustration (Like Shutterstock Screen 3)
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFEFF6FF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.MarkEmailRead,
                    contentDescription = null,
                    tint = AuthPrimaryBlue,
                    modifier = Modifier.size(72.dp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

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

            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
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
            textStyle = LocalTextStyle.current.copy(
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = AuthTextDark
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            ),
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun SuccessView(
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
            // Green Checkmark Badge (Like Shutterstock Screen 4)
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
            Text("You are now authenticated & connected", color = AuthTextMuted, fontSize = 15.sp)
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
