package com.nestcheck.app.ui.parent.chores

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nestcheck.app.data.model.ChoreStatus
import com.nestcheck.app.data.model.ChoreTask
import com.nestcheck.app.ui.theme.*

@Composable
fun ChoresScreen(
    navController: NavController,
    viewModel: ChoresViewModel = hiltViewModel()
) {
    val chores by viewModel.chores.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var choreTitle by remember { mutableStateOf("") }
    var choreCredits by remember { mutableStateOf("10") }

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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back",
                        tint = CyberTextBright
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Chore Chart",
                    color = CyberTextBright,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(onClick = { showAddDialog = true }) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = "Add Chore",
                    tint = CyberTextBright
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(chores, key = { it.id }) { chore ->
                ChoreItemCard(
                    chore = chore,
                    onApprove = { viewModel.approveChore(chore.id) }
                )
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = CyberCard,
            title = { Text("Assign New Chore", color = CyberTextBright, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = choreTitle,
                        onValueChange = { choreTitle = it },
                        label = { Text("Chore Description (e.g. Clean Room)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = White,
                            unfocusedBorderColor = CyberBorder,
                            focusedTextColor = CyberTextBright,
                            unfocusedTextColor = CyberTextBright
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = choreCredits,
                        onValueChange = { choreCredits = it },
                        label = { Text("Credit Reward") },
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
                        if (choreTitle.isNotBlank()) {
                            val credits = choreCredits.toIntOrNull() ?: 10
                            viewModel.addChore(choreTitle, credits)
                            choreTitle = ""
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = Black)
                ) {
                    Text("Add Chore", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel", color = CyberTextDim)
                }
            }
        )
    }
}

@Composable
private fun ChoreItemCard(
    chore: ChoreTask,
    onApprove: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(CyberCard)
            .border(1.dp, CyberBorder, RoundedCornerShape(8.dp))
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = chore.title,
                        color = CyberTextBright,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Due: ${chore.deadline}",
                        color = CyberTextDim,
                        fontSize = 11.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(CyberBlack)
                        .border(0.5.dp, CyberBorderBright, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "+${chore.rewardCredits} pts",
                        color = White,
                        fontSize = 11.sp,
                        fontFamily = TechMono,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (chore.submissionNote.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Note: ${chore.submissionNote}",
                    color = CyberTextBright,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = CyberBorder, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = chore.status.label,
                    color = when (chore.status) {
                        ChoreStatus.APPROVED -> SuccessGreen
                        ChoreStatus.SUBMITTED -> WarningAmber
                        ChoreStatus.PENDING -> CyberTextDim
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                if (chore.status == ChoreStatus.SUBMITTED) {
                    Button(
                        onClick = onApprove,
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = Black)
                    ) {
                        Text("Approve (+${chore.rewardCredits})", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
