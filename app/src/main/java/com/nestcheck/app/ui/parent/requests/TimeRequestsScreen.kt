package com.nestcheck.app.ui.parent.requests

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material.icons.outlined.Schedule
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
import com.nestcheck.app.data.model.CreditRequest
import com.nestcheck.app.data.model.CreditRequestStatus
import com.nestcheck.app.data.model.RequestStatus
import com.nestcheck.app.data.model.TimeExtensionRequest
import com.nestcheck.app.ui.parent.schedule.ScheduleViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val BrandBlue = Color(0xFF0B57D0)
private val LightBg = Color(0xFFF8FAFC)
private val CardWhite = Color(0xFFFFFFFF)
private val BorderColor = Color(0xFFE2E8F0)
private val TextDark = Color(0xFF0F172A)
private val TextMuted = Color(0xFF64748B)

private sealed interface MergedRequest {
    val id: String
    val timestamp: Long
    val isPending: Boolean

    data class Time(val req: TimeExtensionRequest) : MergedRequest {
        override val id: String get() = req.id
        override val timestamp: Long get() = req.timestamp
        override val isPending: Boolean get() = req.status == RequestStatus.PENDING
    }

    data class Credit(val req: CreditRequest) : MergedRequest {
        override val id: String get() = req.id
        override val timestamp: Long get() = req.requestTimestamp
        override val isPending: Boolean get() = req.status == CreditRequestStatus.PENDING
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeRequestsScreen(
    navController: NavController,
    viewModel: ScheduleViewModel = hiltViewModel(),
    parentCreditViewModel: ParentCreditRequestViewModel = hiltViewModel()
) {
    val timeRequests by viewModel.timeRequests.collectAsState()
    val creditRequests by parentCreditViewModel.creditRequests.collectAsState()

    // 0 = All Requests, 1 = Pending Review, 2 = Resolved
    var filterIndex by remember { mutableIntStateOf(0) }

    val mergedList: List<MergedRequest> = remember(timeRequests, creditRequests) {
        val list = mutableListOf<MergedRequest>()
        timeRequests.forEach { list.add(MergedRequest.Time(it)) }
        creditRequests.forEach { list.add(MergedRequest.Credit(it)) }
        // Sort with pending first, then by timestamp descending
        list.sortedWith(
            compareByDescending<MergedRequest> { it.isPending }
                .thenByDescending { it.timestamp }
        )
    }

    val pendingCount = mergedList.count { it.isPending }
    val filteredList = when (filterIndex) {
        1 -> mergedList.filter { it.isPending }
        2 -> mergedList.filter { !it.isPending }
        else -> mergedList
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Child Requests & Approvals",
                            color = TextDark,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (pendingCount > 0) "$pendingCount pending request${if (pendingCount > 1) "s" else ""} require review" else "All requests reviewed",
                            color = if (pendingCount > 0) Color(0xFFD97706) else TextMuted,
                            fontSize = 12.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Back",
                            tint = TextDark
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CardWhite)
            )
        },
        containerColor = LightBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // MERGED UNIFIED FILTER BAR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE2E8F0).copy(alpha = 0.5f))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf(
                    "All (${mergedList.size})" to 0,
                    "Pending ($pendingCount)" to 1,
                    "Resolved" to 2
                ).forEach { (label, idx) ->
                    val isSelected = filterIndex == idx
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) CardWhite else Color.Transparent)
                            .clickable { filterIndex = idx }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) TextDark else TextMuted,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.HourglassEmpty,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (filterIndex == 1) "No pending requests from child" else "No requests found",
                            color = TextMuted,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredList, key = { it.id }) { item ->
                        when (item) {
                            is MergedRequest.Credit -> {
                                ParentCreditRequestCard(
                                    request = item.req,
                                    onApprove = { id -> parentCreditViewModel.approveCreditRequest(id) },
                                    onDecline = { id -> parentCreditViewModel.declineCreditRequest(id) }
                                )
                            }
                            is MergedRequest.Time -> {
                                DirectTimeRequestCard(
                                    request = item.req,
                                    onApprove = { id -> viewModel.approveTimeRequest(id) },
                                    onDecline = { id -> viewModel.declineTimeRequest(id) }
                                )
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(30.dp)) }
                }
            }
        }
    }
}

@Composable
private fun DirectTimeRequestCard(
    request: TimeExtensionRequest,
    onApprove: (String) -> Unit,
    onDecline: (String) -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM d, hh:mm a", Locale.getDefault())
    val timeStr = dateFormat.format(Date(request.timestamp))

    val (badgeBg, badgeText, badgeLabel) = when (request.status) {
        RequestStatus.PENDING -> Triple(Color(0xFFFEF3C7), Color(0xFFB45309), "Pending Review")
        RequestStatus.APPROVED -> Triple(Color(0xFFDCFCE7), Color(0xFF15803D), "Approved (+${request.requestedMinutes}m)")
        RequestStatus.DECLINED -> Triple(Color(0xFFFEE2E2), Color(0xFFB91C1C), "Declined")
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardWhite)
            .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Header: Child Info & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE8F0FE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "K",
                            color = BrandBlue,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    Column {
                        Text(
                            text = "Screen Time Extension",
                            color = TextDark,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(text = timeStr, color = TextMuted, fontSize = 11.sp)
                    }
                }

                Surface(
                    color = badgeBg,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = badgeLabel,
                        color = badgeText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            // Reason
            Surface(
                color = Color(0xFFF8FAFC),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, BorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Schedule,
                        contentDescription = null,
                        tint = BrandBlue,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "+${request.requestedMinutes} Minutes Requested: \"${request.reason}\"",
                        color = TextDark,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Action Buttons
            if (request.status == RequestStatus.PENDING) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { onDecline(request.id) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                        border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                        contentPadding = PaddingValues(vertical = 10.dp)
                    ) {
                        Icon(Icons.Outlined.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Decline", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { onApprove(request.id) },
                        modifier = Modifier.weight(1.4f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandBlue, contentColor = Color.White),
                        contentPadding = PaddingValues(vertical = 10.dp)
                    ) {
                        Icon(Icons.Outlined.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Approve (+${request.requestedMinutes}m)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
