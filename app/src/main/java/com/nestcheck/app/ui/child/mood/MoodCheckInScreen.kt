package com.nestcheck.app.ui.child.mood

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Mood
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
import com.nestcheck.app.data.model.MoodType
import com.nestcheck.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MoodCheckInScreen(
    navController: NavController,
    viewModel: MoodViewModel = hiltViewModel()
) {
    val moodHistory by viewModel.moodHistory.collectAsState()
    val selectedMood by viewModel.selectedMood.collectAsState()
    val noteText by viewModel.noteText.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()
    val scrollState = rememberScrollState()

    val dateFormat = remember { SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBlack)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
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
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Daily Mood Check-In",
                    color = CyberTextBright,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Icon(
                imageVector = Icons.Outlined.Mood,
                contentDescription = "Mood",
                tint = White
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // MOOD SELECTOR CARDS
        Text(
            text = "HOW ARE YOU FEELING TODAY?",
            color = CyberTextDim,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MoodType.values().forEach { mood ->
                val isSelected = mood == selectedMood
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) White else CyberCard)
                        .border(1.dp, if (isSelected) White else CyberBorder, RoundedCornerShape(12.dp))
                        .clickable { viewModel.selectMood(mood) }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = mood.emoji, fontSize = 26.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = mood.label,
                            color = if (isSelected) Black else CyberTextDim,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // NOTE INPUT CARD
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CyberCard)
                .border(1.dp, CyberBorder, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "Add a quick note (Optional)",
                    color = CyberTextBright,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = noteText,
                    onValueChange = viewModel::updateNote,
                    placeholder = { Text("What made you feel this way today?", color = CyberTextDim, fontSize = 13.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = CyberTextBright,
                        unfocusedTextColor = CyberTextBright,
                        focusedBorderColor = White,
                        unfocusedBorderColor = CyberBorder
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = viewModel::saveMoodEntry,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = Black)
                ) {
                    if (saveSuccess) {
                        Icon(Icons.Outlined.Check, contentDescription = "Saved", tint = Black)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Mood Recorded!", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    } else {
                        Text("Log Mood Check-In", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // MOOD HISTORY TIMELINE
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CyberCard)
                .border(1.dp, CyberBorder, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "RECENT MOOD LOGS",
                    color = CyberTextDim,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (moodHistory.isEmpty()) {
                    Text("No mood entries logged yet.", color = CyberTextDim, fontSize = 13.sp)
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        moodHistory.forEach { entry ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CyberBlack)
                                    .border(1.dp, CyberBorder, RoundedCornerShape(8.dp))
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = entry.mood.emoji, fontSize = 24.sp)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = entry.mood.label,
                                                color = CyberTextBright,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = dateFormat.format(Date(entry.timestamp)),
                                                color = CyberTextDim,
                                                fontSize = 11.sp
                                            )
                                        }
                                        if (entry.note.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = entry.note,
                                                color = CyberTextDim,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
