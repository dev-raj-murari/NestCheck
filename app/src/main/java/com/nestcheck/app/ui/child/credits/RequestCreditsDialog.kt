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
import androidx.compose.ui.text.font.FontFamily
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

    val cardBg = Color(0xFF121824)
    val cyanAccent = Color(0xFF00E5FF)
    val greenSuccess = Color(0xFF00E676)
    val borderStroke = Color(0xFF1E293B)
    val textMuted = Color(0xFF94A3B8)

    ModalBottomSheet(
        onDismissRequest = {
            viewModel.resetForm()
            onDismiss()
        },
        containerColor = cardBg,
        dragHandle = { BottomSheetDefaults.DragHandle(color = borderStroke) }
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
                        tint = cyanAccent,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = "Request Extra Task Credits",
                        color = Color.White,
                        fontSize = 16.sp,
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
                    color = greenSuccess.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, greenSuccess.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = greenSuccess,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = "Credit Request Sent to Parents!",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Once approved, $creditsRequested credits (${creditsRequested / 2} mins screen time) will be instantly added to your wallet.",
                            color = textMuted,
                            fontSize = 12.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Button(
                            onClick = {
                                viewModel.resetForm()
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = greenSuccess),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(text = "Done", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                // Form Fields
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Task or Accomplishment:",
                        color = textMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    OutlinedTextField(
                        value = taskTitle,
                        onValueChange = { viewModel.onTitleChange(it) },
                        placeholder = { Text("e.g., Cleaned bicycle & organized bedroom", color = textMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = cyanAccent,
                            unfocusedBorderColor = borderStroke,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = cyanAccent
                        )
                    )

                    // Conversion Calculator & Slider
                    Surface(
                        color = Color(0xFF1E293B),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Points Requested:",
                                    color = textMuted,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "$creditsRequested credits = ${creditsRequested / 2} mins",
                                    color = cyanAccent,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Slider(
                                value = creditsRequested.toFloat(),
                                onValueChange = { viewModel.onCreditsChange(it.toInt()) },
                                valueRange = 10f..120f,
                                steps = 10,
                                colors = SliderDefaults.colors(
                                    thumbColor = cyanAccent,
                                    activeTrackColor = cyanAccent,
                                    inactiveTrackColor = Color(0xFF0F172A)
                                )
                            )
                        }
                    }

                    // Category Selector
                    Text(
                        text = "Reason Category:",
                        color = textMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(CreditReasonCategory.values()) { category ->
                            val isSelected = category == selectedCategory
                            Surface(
                                color = if (isSelected) cyanAccent.copy(alpha = 0.2f) else Color(0xFF1E293B),
                                shape = RoundedCornerShape(6.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    width = 1.dp,
                                    color = if (isSelected) cyanAccent else Color.Transparent
                                ),
                                modifier = Modifier.clickable { viewModel.onCategoryChange(category) }
                            ) {
                                Text(
                                    text = category.title,
                                    color = if (isSelected) cyanAccent else textMuted,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    // Justification
                    Text(
                        text = "Why do you deserve these extra points? (Optional):",
                        color = textMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    OutlinedTextField(
                        value = justification,
                        onValueChange = { viewModel.onJustificationChange(it) },
                        placeholder = { Text("Details for mom & dad to review...", color = textMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2,
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = cyanAccent,
                            unfocusedBorderColor = borderStroke,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = cyanAccent
                        )
                    )

                    // Submit Button
                    Button(
                        onClick = { viewModel.submitRequest() },
                        enabled = taskTitle.isNotBlank(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = cyanAccent)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Submit Request to Parents",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
