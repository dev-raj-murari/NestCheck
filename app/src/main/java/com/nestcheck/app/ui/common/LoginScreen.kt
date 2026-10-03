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
    val scrollState = rememberScrollState()

    var isRegistering by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("devraj@nestcheck.app") }
    var password by remember { mutableStateOf("password123") }
    var name by remember { mutableStateOf("Dev Raj") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBlack)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 20.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Simple App Header
        Text(
            text = "NestCheck",
            color = CyberTextBright,
            fontSize = 32.sp,
            fontFamily = TechMono,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = if (isRegistering) "Create a parent account" else "Login with email & password",
            color = CyberTextDim,
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 4.dp, bottom = 32.dp)
        )

        // Simple Clean Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(CyberCard)
                .border(1.dp, CyberBorder, RoundedCornerShape(8.dp))
                .padding(20.dp)
        ) {
            Column {
                if (isRegistering) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
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
                    Spacer(modifier = Modifier.height(14.dp))
                }

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
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
                    value = password,
                    onValueChange = { password = it },
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

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        navController.navigate(Screen.RoleSelection.route)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = Black)
                ) {
                    Text(
                        text = if (isRegistering) "Register" else "Login",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = if (isRegistering) "Already have an account? Login" else "Don't have an account? Register",
            color = CyberTextDim,
            fontSize = 14.sp,
            modifier = Modifier
                .clickable { isRegistering = !isRegistering }
                .padding(8.dp)
        )
    }
}
