package com.nestcheck.app.ui.parent.requests

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
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
import com.nestcheck.app.data.model.CreditRequestStatus
import com.nestcheck.app.data.model.RequestStatus
import com.nestcheck.app.ui.parent.schedule.ScheduleViewModel
import com.nestcheck.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TimeRequestsScreen(
    navController: NavController,
    viewModel: ScheduleViewModel = hiltViewModel(),
    parentCreditViewModel: ParentCreditRequestViewModel = hiltViewModel()
) {
    val requests by viewModel.timeRequests.collectAsState()
    val creditRequests by parentCreditViewModel.creditRequests.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    val pendingCreditCount = creditRequests.count { it.status == CreditRequestStatus.PENDING }

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
                text = "Child Requests & Approvals",
                color = CyberTextBright,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // TAB SWITCHER: Time Requests vs Task Credits (2:1)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(CyberCard)
                .border(1.dp, CyberBorder, RoundedCornerShape(8.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Tab 0
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (selectedTab == 0) White else Color.Transparent)
                    .clickable { selectedTab = 0 }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Screen Time (${requests.size})",
                    color = if (selectedTab == 0) Black else CyberTextDim,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Tab 1
            Box(
                modifier = Modifier
                    .weight(1.2f)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (selectedTab == 1) ElectricCyan else Color.Transparent)
                    .clickable { selectedTab = 1 }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Task Credits (2:1)",
                        color = if (selectedTab == 1) CyberBlack else CyberTextDim,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    if (pendingCreditCount > 0) {
                        Surface(
                            color = CoralRed,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "$pendingCreditCount",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (selectedTab == 0) {
            // STANDARD SCREEN TIME REQUESTS
            if (requests.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No extension requests right now", color = CyberTextDim, fontSize = 13.sp)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxSize()) {
                    items(requests, key = { it.id }) { req ->
                        val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                        val timeStr = dateFormat.format(Date(req.timestamp))

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
                                    Text(
                                        text = "+${req.requestedMinutes} Mins Requested",
                                        color = CyberTextBright,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(text = timeStr, color = CyberTextDim, fontSize = 11.sp)
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = "Reason: \"${req.reason}\"", color = CyberTextBright, fontSize = 13.sp)

                                Spacer(modifier = Modifier.height(12.dp))
                                HorizontalDivider(color = CyberBorder, thickness = 0.5.dp)
                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = when (req.status) {
                                            RequestStatus.PENDING -> "Pending Parent Review"
                                            RequestStatus.APPROVED -> "Approved (+${req.requestedMinutes}m added)"
                                            RequestStatus.DECLINED -> "Declined"
                                        },
                                        color = when (req.status) {
                                            RequestStatus.PENDING -> WarningAmber
                                            RequestStatus.APPROVED -> SuccessGreen
                                            RequestStatus.DECLINED -> AlertRed
                                        },
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )

                                    if (req.status == RequestStatus.PENDING) {
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            OutlinedButton(
                                                onClick = { viewModel.declineTimeRequest(req.id) },
                                                shape = RoundedCornerShape(6.dp),
                                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                                colors = ButtonDefaults.outlinedButtonColors(contentColor = AlertRed)
                                            ) {
                                                Icon(Icons.Outlined.Close, contentDescription = null, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Decline", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }

                                            Button(
                                                onClick = { viewModel.approveTimeRequest(req.id) },
                                                shape = RoundedCornerShape(6.dp),
                                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = Black)
                                            ) {
                                                Icon(Icons.Outlined.Check, contentDescription = null, tint = Black, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Approve", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // TASK & CHORE CREDIT REQUESTS (2 CREDITS = 1 MIN RULE)
            if (creditRequests.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No credit requests from child yet", color = CyberTextDim, fontSize = 13.sp)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxSize()) {
                    items(creditRequests, key = { it.id }) { credReq ->
                        ParentCreditRequestCard(
                            request = credReq,
                            onApprove = { id -> parentCreditViewModel.approveCreditRequest(id) },
                            onDecline = { id -> parentCreditViewModel.declineCreditRequest(id) }
                        )
                    }
                }
            }
        }
    }
}
