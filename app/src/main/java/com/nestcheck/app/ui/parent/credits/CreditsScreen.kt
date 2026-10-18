package com.nestcheck.app.ui.parent.credits

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Stars
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nestcheck.app.data.model.CreditTransaction
import com.nestcheck.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CreditsScreen(
    navController: NavController,
    viewModel: CreditsViewModel = hiltViewModel()
) {
    val balance by viewModel.creditBalance.collectAsState()
    val transactions by viewModel.transactions.collectAsState()

    var showCustomDialog by remember { mutableStateOf(false) }
    var customAmount by remember { mutableStateOf("10") }
    var customReason by remember { mutableStateOf("") }

    val equivalentMins = balance.balance * 5

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBlack)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // TOP APP BAR
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back",
                    tint = CyberTextBright
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Credits & Rewards",
                color = CyberTextBright,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // HERO BALANCE CARD
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CyberCard)
                .border(1.dp, CyberBorder, RoundedCornerShape(12.dp))
                .padding(18.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CHILD CREDIT BALANCE",
                        color = CyberTextDim,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "1 pt = 5 mins",
                        color = White,
                        fontSize = 11.sp,
                        fontFamily = TechMono
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "${balance.balance}",
                        color = CyberTextBright,
                        fontSize = 42.sp,
                        fontFamily = TechMono,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "credits ($equivalentMins mins power)",
                        color = CyberTextDim,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = CyberBorder, thickness = 1.dp)
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Lifetime Earned", color = CyberTextDim, fontSize = 11.sp)
                        Text("+${balance.lifetimeEarned} pts", color = SuccessGreen, fontSize = 13.sp, fontFamily = TechMono, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text("Lifetime Spent", color = CyberTextDim, fontSize = 11.sp)
                        Text("-${balance.lifetimeSpent} pts", color = AlertRed, fontSize = 13.sp, fontFamily = TechMono, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text("Net Conversion", color = CyberTextDim, fontSize = 11.sp)
                        Text("+${balance.balance * 5}m", color = CyberTextBright, fontSize = 13.sp, fontFamily = TechMono, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // QUICK AWARD CREDITS SECTION
        Text(
            text = "Award Credits",
            color = CyberTextBright,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(CyberCard)
                    .border(1.dp, CyberBorder, RoundedCornerShape(6.dp))
                    .clickable { viewModel.awardCredits(5, "Completed Quick Chore") },
                contentAlignment = Alignment.Center
            ) {
                Text("+5 Chore", color = White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(CyberCard)
                    .border(1.dp, CyberBorder, RoundedCornerShape(6.dp))
                    .clickable { viewModel.awardCredits(10, "Finished Homework") },
                contentAlignment = Alignment.Center
            ) {
                Text("+10 Homework", color = White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(White)
                    .clickable { showCustomDialog = true },
                contentAlignment = Alignment.Center
            ) {
                Text("+ Custom", color = Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // REWARD TRANSACTION LEDGER
        Text(
            text = "Transaction Ledger",
            color = CyberTextBright,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(transactions, key = { it.id }) { tx ->
                TransactionRow(tx)
            }
        }
    }

    // CUSTOM AWARD DIALOG
    if (showCustomDialog) {
        AlertDialog(
            onDismissRequest = { showCustomDialog = false },
            containerColor = CyberCard,
            title = {
                Text("Award Bonus Credits", color = CyberTextBright, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = customAmount,
                        onValueChange = { customAmount = it },
                        label = { Text("Credit Amount") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = White,
                            unfocusedBorderColor = CyberBorder,
                            focusedTextColor = CyberTextBright,
                            unfocusedTextColor = CyberTextBright
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = customReason,
                        onValueChange = { customReason = it },
                        label = { Text("Reason (e.g. Read 20 mins)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = White,
                            unfocusedBorderColor = CyberBorder,
                            focusedTextColor = CyberTextBright,
                            unfocusedTextColor = CyberTextBright
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = customAmount.toIntOrNull() ?: 5
                        val rsn = customReason.ifBlank { "Parent Bonus" }
                        viewModel.awardCredits(amt, rsn)
                        showCustomDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = Black)
                ) {
                    Text("Award", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomDialog = false }) {
                    Text("Cancel", color = CyberTextDim)
                }
            }
        )
    }
}

@Composable
private fun TransactionRow(tx: CreditTransaction) {
    val isEarned = tx.amount > 0
    val dateFormat = SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault())
    val dateText = dateFormat.format(Date(tx.timestamp))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(CyberCard)
            .border(1.dp, CyberBorder, RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = tx.reason,
                    color = CyberTextBright,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = dateText,
                    color = CyberTextDim,
                    fontSize = 11.sp
                )
            }

            Text(
                text = if (isEarned) "+${tx.amount}" else "${tx.amount}",
                color = if (isEarned) SuccessGreen else AlertRed,
                fontSize = 15.sp,
                fontFamily = TechMono,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
