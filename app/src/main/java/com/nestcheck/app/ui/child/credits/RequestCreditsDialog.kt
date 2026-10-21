package com.nestcheck.app.ui.child.credits

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nestcheck.app.data.model.CreditReasonCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestCreditsDialog(
    onDismiss: () -> Unit,
    viewModel: CreditRequestViewModel = hiltViewModel()
) {
    val taskTitle by viewModel.taskTitle
    val creditsRequested by viewModel.creditsRequested
    val selectedCategory by viewModel.selectedCategory
    val justification by viewModel.justification
    val isSubmittedSuccess by viewModel.isSubmittedSuccess

    val cardBg = Color(0xFFFFFFFF)
    val borderStroke = Color(0xFFE2E8F0)
    val textDark = Color(0xFF1E293B)
    val textMuted = Color(0xFF64748B)
    val primaryBlue = Color(0xFF0B57D0)
    val greenSuccess = Color(0xFF10B981)

    ModalBottomSheet(
        onDismissRequest = {
            viewModel.resetForm()
            onDismiss()
        },
        containerColor = cardBg,
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color(0xFFCBD5E1)) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Stars,
                        contentDescription = null,
                        tint = primaryBlue,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = "Request Extra Task Credits",
                        color = textDark,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(onClick = {
                    viewModel.resetForm()
                    onDismiss()
                }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = textMuted
                    )
                }
            }

            if (isSubmittedSuccess) {
                // Success Message
                Surface(
                    color = Color(0xFFE6F4EA),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA7F3D0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = greenSuccess,
                            modifier = Modifier.size(24.dp)
                        )
                        Column {
                            Text(
                                text = "Request Sent to Parent!",
                                color = textDark,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Your parent will receive a notification to approve your credits.",
                                color = textMuted,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Button(
                    onClick = {
                        viewModel.resetForm()
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryBlue)
                ) {
                    Text("Done", fontWeight = FontWeight.Bold)
                }
            } else {
                // Form Fields
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Category",
                        color = textMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(CreditReasonCategory.values()) { category ->
                            val isSelected = selectedCategory == category
                            Surface(
                                color = if (isSelected) Color(0xFFE8F0FE) else Color(0xFFF8FAFC),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) primaryBlue else borderStroke
                                ),
                                modifier = Modifier.clickable { viewModel.onCategoryChange(category) }
                            ) {
                                Text(
                                    text = category.title,
                                    color = if (isSelected) primaryBlue else textMuted,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = taskTitle,
                        onValueChange = { viewModel.onTitleChange(it) },
                        label = { Text("What did you accomplish?") },
                        placeholder = { Text("e.g. Cleaned my study table & folded laundry") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryBlue,
                            unfocusedBorderColor = borderStroke,
                            focusedTextColor = textDark,
                            unfocusedTextColor = textDark
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Credits Requested: $creditsRequested pts (+${creditsRequested / 2}m screen time)",
                            color = primaryBlue,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Slider(
                        value = creditsRequested.toFloat(),
                        onValueChange = { viewModel.onCreditsChange(it.toInt()) },
                        valueRange = 10f..100f,

                        colors = SliderDefaults.colors(
                            thumbColor = primaryBlue,
                            activeTrackColor = primaryBlue,
                            inactiveTrackColor = Color(0xFFE2E8F0)
                        )
                    )

                    OutlinedTextField(
                        value = justification,
                        onValueChange = { viewModel.onJustificationChange(it) },
                        label = { Text("Parent Note (Optional)") },
                        placeholder = { Text("Add extra notes for Mom & Dad") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 2,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryBlue,
                            unfocusedBorderColor = borderStroke,
                            focusedTextColor = textDark,
                            unfocusedTextColor = textDark
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Button(
                        onClick = { viewModel.submitRequest() },
                        enabled = taskTitle.isNotBlank(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryBlue)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Send Request to Parent", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}
