package com.nestcheck.app.ui.sinan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nestcheck.app.data.model.CommunicationType
import com.nestcheck.app.data.model.ContactRelationship
import com.nestcheck.app.data.model.TrustedContact
import com.nestcheck.app.data.model.UnknownCommunicationAlert
import com.nestcheck.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TrustedContactsScreen(
    navController: NavController,
    viewModel: TrustedContactsViewModel = hiltViewModel()
) {
    val contacts by viewModel.contacts.collectAsState()
    val unknownAlerts by viewModel.unknownAlerts.collectAsState()
    val isAddDialogOpen by viewModel.isAddContactDialogOpen.collectAsState()

    var approveTargetAlert by remember { mutableStateOf<UnknownCommunicationAlert?>(null) }

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
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Trusted Contacts",
                    color = CyberTextBright,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(onClick = viewModel::openAddContactDialog) {
                Icon(
                    imageVector = Icons.Outlined.PersonAdd,
                    contentDescription = "Add Contact",
                    tint = Color(0xFF0B57D0)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // HERO / OVERVIEW CARD
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CyberCard)
                        .border(1.dp, CyberBorder, RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "COMMUNICATION FIREWALL (F31)",
                                color = CyberTextDim,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(SuccessGreen.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "WHITELIST STRICT",
                                    color = SuccessGreen,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Approved Caller & SMS Whitelist",
                            color = CyberTextBright,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Child can only exchange calls and text messages with contacts verified in this directory. Unknown callers are instantly blocked and flagged.",
                            color = CyberTextDim,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = CyberBorder, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Approved", color = CyberTextDim, fontSize = 11.sp)
                                Text("${contacts.size} contacts", color = CyberTextBright, fontSize = 14.sp, fontFamily = TechMono, fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Text("Calls Allowed", color = CyberTextDim, fontSize = 11.sp)
                                Text("${contacts.count { it.isCallAllowed }} active", color = SuccessGreen, fontSize = 14.sp, fontFamily = TechMono, fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Text("SMS Allowed", color = CyberTextDim, fontSize = 11.sp)
                                Text("${contacts.count { it.isSmsAllowed }} active", color = Color(0xFF0B57D0), fontSize = 14.sp, fontFamily = TechMono, fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Text("Interceptions", color = CyberTextDim, fontSize = 11.sp)
                                Text("${unknownAlerts.size} blocked", color = AlertRed, fontSize = 14.sp, fontFamily = TechMono, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // SIMULATION HARNESS ACTIONS
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = viewModel::simulateUnknownCall,
                        modifier = Modifier.weight(1f).height(40.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AlertRed, contentColor = White)
                    ) {
                        Icon(Icons.Outlined.PhoneMissed, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Simulate Call", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = viewModel::simulateUnknownSms,
                        modifier = Modifier.weight(1f).height(40.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCard, contentColor = CyberTextBright),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
                    ) {
                        Icon(Icons.Outlined.SmsFailed, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Simulate SMS", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // UNKNOWN INTERCEPTION FEED
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Unknown Communication Interceptions (${unknownAlerts.size})",
                        color = CyberTextBright,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (unknownAlerts.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(CyberCard)
                            .border(1.dp, CyberBorder, RoundedCornerShape(8.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No unauthorized communications intercepted.", color = CyberTextDim, fontSize = 12.sp)
                    }
                }
            } else {
                items(unknownAlerts, key = { it.id }) { alert ->
                    UnknownAlertCard(
                        alert = alert,
                        onApprove = { approveTargetAlert = alert },
                        onDismiss = { viewModel.dismissAlert(alert.id) }
                    )
                }
            }

            // APPROVED DIRECTORY SECTION HEADER
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Whitelisted Directory (${contacts.size})",
                        color = CyberTextBright,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = viewModel::openAddContactDialog) {
                        Text("+ Add Contact", color = White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            items(contacts, key = { it.id }) { contact ->
                TrustedContactCard(
                    contact = contact,
                    onToggleCall = { viewModel.toggleCallPermission(contact.id) },
                    onToggleSms = { viewModel.toggleSmsPermission(contact.id) },
                    onDelete = { viewModel.deleteContact(contact.id) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    if (isAddDialogOpen) {
        AddContactDialog(
            onDismiss = viewModel::closeAddContactDialog,
            onConfirm = { name, phone, email, rel, call, sms ->
                viewModel.addContact(name, phone, email, rel, call, sms)
            }
        )
    }

    approveTargetAlert?.let { alert ->
        ApproveUnknownDialog(
            alert = alert,
            onDismiss = { approveTargetAlert = null },
            onConfirm = { name, rel ->
                viewModel.approveAndWhitelist(alert.id, name, rel)
                approveTargetAlert = null
            }
        )
    }
}

@Composable
private fun TrustedContactCard(
    contact: TrustedContact,
    onToggleCall: () -> Unit,
    onToggleSms: () -> Unit,
    onDelete: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(CyberCard)
            .border(1.dp, CyberBorder, RoundedCornerShape(10.dp))
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0B57D0).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = null,
                            tint = Color(0xFF0B57D0),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(contact.name, color = CyberTextBright, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(White.copy(alpha = 0.1f))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(contact.relationship.displayName, color = CyberTextDim, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(contact.phoneNumber, color = CyberTextDim, fontSize = 11.sp, fontFamily = TechMono)
                        }
                    }
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Outlined.DeleteOutline,
                        contentDescription = "Delete",
                        tint = CyberTextDim,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = CyberBorder, thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))

            // PERMISSION TOGGLES ROW
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Call, contentDescription = null, tint = if (contact.isCallAllowed) SuccessGreen else CyberTextDim, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Calls", color = CyberTextBright, fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Switch(
                        checked = contact.isCallAllowed,
                        onCheckedChange = { onToggleCall() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Black,
                            checkedTrackColor = SuccessGreen,
                            uncheckedThumbColor = CyberTextDim,
                            uncheckedTrackColor = CyberBlack
                        ),
                        modifier = Modifier.height(24.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Chat, contentDescription = null, tint = if (contact.isSmsAllowed) Color(0xFF0B57D0) else CyberTextDim, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("SMS", color = CyberTextBright, fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Switch(
                        checked = contact.isSmsAllowed,
                        onCheckedChange = { onToggleSms() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Black,
                            checkedTrackColor = Color(0xFF0B57D0),
                            uncheckedThumbColor = CyberTextDim,
                            uncheckedTrackColor = CyberBlack
                        ),
                        modifier = Modifier.height(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun UnknownAlertCard(
    alert: UnknownCommunicationAlert,
    onApprove: () -> Unit,
    onDismiss: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(CyberCard)
            .border(1.dp, AlertRed.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(AlertRed)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (alert.communicationType == CommunicationType.CALL) "CALL BLOCKED" else "SMS BLOCKED",
                            color = White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(alert.senderAddress, color = CyberTextBright, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = TechMono)
                }

                Text(
                    text = dateFormat.format(Date(alert.timestamp)),
                    color = CyberTextDim,
                    fontSize = 10.sp,
                    fontFamily = TechMono
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(alert.snippet, color = CyberTextDim, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onApprove,
                    modifier = Modifier.weight(1f).height(36.dp),
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen, contentColor = Black)
                ) {
                    Text("Whitelist & Approve", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.height(36.dp),
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberTextDim)
                ) {
                    Text("Dismiss", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun AddContactDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, ContactRelationship, Boolean, Boolean) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var relationship by remember { mutableStateOf(ContactRelationship.FRIEND) }
    var callAllowed by remember { mutableStateOf(true) }
    var smsAllowed by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Approved Contact", color = CyberTextBright, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name (e.g. Aunt Clara)") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email (Optional)") },
                    singleLine = true
                )

                Text("Relationship:", color = CyberTextDim, fontSize = 11.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(ContactRelationship.MOM, ContactRelationship.DAD, ContactRelationship.TUTOR, ContactRelationship.FRIEND).forEach { rel ->
                        FilterChip(
                            selected = relationship == rel,
                            onClick = { relationship = rel },
                            label = { Text(rel.displayName, fontSize = 10.sp) }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Allow Voice Calls", color = CyberTextBright, fontSize = 12.sp)
                    Switch(checked = callAllowed, onCheckedChange = { callAllowed = it })
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Allow SMS Texts", color = CyberTextBright, fontSize = 12.sp)
                    Switch(checked = smsAllowed, onCheckedChange = { smsAllowed = it })
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, phone, email, relationship, callAllowed, smsAllowed) },
                colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = Black)
            ) {
                Text("Add to Whitelist")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = CyberTextDim) }
        },
        containerColor = CyberCard,
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
private fun ApproveUnknownDialog(
    alert: UnknownCommunicationAlert,
    onDismiss: () -> Unit,
    onConfirm: (String, ContactRelationship) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var relationship by remember { mutableStateOf(ContactRelationship.FRIEND) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Whitelist Intercepted Caller", color = CyberTextBright, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Sender: ${alert.senderAddress}", color = Color(0xFF0B57D0), fontSize = 13.sp, fontFamily = TechMono)
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Contact Name") },
                    singleLine = true
                )
                Text("Relationship:", color = CyberTextDim, fontSize = 11.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(ContactRelationship.FRIEND, ContactRelationship.TUTOR, ContactRelationship.OTHER).forEach { rel ->
                        FilterChip(
                            selected = relationship == rel,
                            onClick = { relationship = rel },
                            label = { Text(rel.displayName, fontSize = 10.sp) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, relationship) },
                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen, contentColor = Black)
            ) {
                Text("Approve & Whitelist")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = CyberTextDim) }
        },
        containerColor = CyberCard,
        shape = RoundedCornerShape(12.dp)
    )
}
