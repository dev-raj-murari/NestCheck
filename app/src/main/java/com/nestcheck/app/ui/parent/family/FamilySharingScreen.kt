package com.nestcheck.app.ui.parent.family

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Share
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
import androidx.navigation.NavController
import com.nestcheck.app.data.model.CoParentRole
import com.nestcheck.app.data.model.InviteStatus

private val CyberBg = Color(0xFFF8FAFC)
private val CyberCardBg = Color(0xFFFFFFFF)
private val CyberBorderColor = Color(0xFFE2E8F0)
private val NeonCyan = Color(0xFF0B57D0)
private val SafeGreen = Color(0xFF10B981)
private val AlertRed = Color(0xFFEF4444)
private val Amber = Color(0xFFF59E0B)
private val TextWhite = Color(0xFF0F172A)
private val TextMuted = Color(0xFF64748B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FamilySharingScreen(
    navController: NavController,
    viewModel: FamilySharingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = CyberBg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Co-Parents & Household",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextWhite
                        )
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::openInviteDialog) {
                        Icon(
                            imageVector = Icons.Outlined.PersonAdd,
                            contentDescription = "Invite Parent",
                            tint = NeonCyan
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CyberBg)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Invite Code Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CyberCardBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorderColor)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Household Join Code",
                                color = TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "Share this 6-digit code with spouse or guardian",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                        IconButton(onClick = viewModel::generateNewCode) {
                            Icon(imageVector = Icons.Outlined.Refresh, contentDescription = "Regenerate", tint = NeonCyan)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(CyberBg)
                            .border(1.dp, NeonCyan, RoundedCornerShape(8.dp))
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = uiState.activeInvite?.code ?: "NC-8294",
                            color = NeonCyan,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 24.sp,
                            letterSpacing = 4.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = uiState.activeInvite?.expiresAtFormatted ?: "Valid for 48 hours",
                            color = Amber,
                            fontSize = 11.sp
                        )
                        Button(
                            onClick = viewModel::openInviteDialog,
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Icon(imageVector = Icons.Outlined.Mail, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Invite by Email", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Role Privileges Guide Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CyberCardBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorderColor)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "ROLE-BASED PERMISSION TIERS",
                        color = TextMuted,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• Co-Parent (Admin): Full rights to adjust screen limits, bedtime, remote lock, and app approvals.",
                        color = TextWhite,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• Guardian / Babysitter: Supervisory rights to view live GPS, trigger temporary pause, and receive SOS alerts.",
                        color = TextWhite,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }

            // Household Members List
            Text(
                text = "ACTIVE HOUSEHOLD MEMBERS (${uiState.members.size})",
                color = TextMuted,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )

            uiState.members.forEach { member ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = CyberCardBg),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorderColor)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (member.role == CoParentRole.ADMIN) NeonCyan.copy(alpha = 0.2f) else SafeGreen.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (member.role == CoParentRole.ADMIN) Icons.Outlined.AdminPanelSettings else Icons.Outlined.Security,
                                contentDescription = null,
                                tint = if (member.role == CoParentRole.ADMIN) NeonCyan else SafeGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = member.name,
                                    color = TextWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (member.status == InviteStatus.ACTIVE) SafeGreen.copy(alpha = 0.15f) else Amber.copy(alpha = 0.15f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = member.role.displayName.substringBefore(" "),
                                        color = if (member.status == InviteStatus.ACTIVE) SafeGreen else Amber,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Text(text = member.email, color = TextMuted, fontSize = 12.sp)
                            Text(text = member.lastActive, color = TextMuted, fontSize = 11.sp)
                        }

                        if (!member.lastActive.contains("Primary Owner")) {
                            IconButton(onClick = { viewModel.removeMember(member.id) }) {
                                Icon(
                                    imageVector = Icons.Outlined.DeleteOutline,
                                    contentDescription = "Revoke",
                                    tint = AlertRed
                                )
                            }
                        }
                    }
                }
            }

            // Toast Message
            uiState.toastMessage?.let { msg ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = CyberCardBg),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = msg, color = NeonCyan, fontSize = 12.sp, modifier = Modifier.weight(1f))
                        Text(
                            text = "OK",
                            color = TextWhite,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { viewModel.clearToast() }
                                .padding(4.dp)
                        )
                    }
                }
            }
        }

        // Invite Dialog
        if (uiState.showInviteDialog) {
            AlertDialog(
                onDismissRequest = viewModel::closeInviteDialog,
                containerColor = CyberCardBg,
                title = {
                    Text(
                        text = "Invite Co-Parent or Guardian",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = uiState.inviteeName,
                            onValueChange = viewModel::setInviteeName,
                            label = { Text("Full Name", color = TextMuted) },
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(color = TextWhite),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = CyberBorderColor
                            )
                        )
                        OutlinedTextField(
                            value = uiState.inviteeEmail,
                            onValueChange = viewModel::setInviteeEmail,
                            label = { Text("Email Address", color = TextMuted) },
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(color = TextWhite),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = CyberBorderColor
                            )
                        )

                        Text(text = "Assign Access Role:", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                        CoParentRole.values().forEach { role ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.setSelectedRole(role) }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = uiState.selectedRole == role,
                                    onClick = { viewModel.setSelectedRole(role) },
                                    colors = RadioButtonDefaults.colors(selectedColor = NeonCyan, unselectedColor = TextMuted)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(text = role.displayName, color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    Text(text = role.description, color = TextMuted, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = viewModel::sendEmailInvite,
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black)
                    ) {
                        Text("Send Invitation", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = viewModel::closeInviteDialog) {
                        Text("Cancel", color = TextMuted)
                    }
                }
            )
        }
    }
}
