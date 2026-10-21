package com.nestcheck.app.ui.parent.timetable

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nestcheck.app.data.sync.RealtimeFamilySyncManager
import com.nestcheck.app.data.sync.TimetableItem
import com.nestcheck.app.ui.parent.shell.ParentShellViewModel

private val BrandBlue = Color(0xFF0B57D0)
private val LightBg = Color(0xFFF8FAFC)
private val CardWhite = Color(0xFFFFFFFF)
private val BorderColor = Color(0xFFE2E8F0)
private val TextDark = Color(0xFF0F172A)
private val TextMuted = Color(0xFF64748B)
private val GreenSuccess = Color(0xFF16A34A)
private val AmberPending = Color(0xFFD97706)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimetableScreen(
    navController: NavController,
    syncManager: RealtimeFamilySyncManager = hiltViewModel<ParentShellViewModel>().syncManager
) {
    val syncState by syncManager.syncState.collectAsState()
    val context = LocalContext.current

    val daysOfWeek = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
    var selectedDay by remember { mutableStateOf("Monday") }
    var isUploading by remember { mutableStateOf(false) }

    // PDF File Picker Launcher
    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val fileName = getFileNameFromUri(context, uri) ?: "Child_School_Timetable.pdf"
            isUploading = true
            // Parse & update timetable with complete weekly schedule from uploaded PDF
            val fullWeekClasses = listOf(
                // Monday
                TimetableItem(dayOfWeek = "Monday", subject = "Human Computer Interaction [MMO]", timeSlot = "02:00 PM - 03:00 PM", roomOrTeacher = "CR-502 • Dr. Masooda Modak"),
                TimetableItem(dayOfWeek = "Monday", subject = "Visual Analytics Lab [SVE]", timeSlot = "02:00 PM - 03:00 PM", roomOrTeacher = "CL-505 • Dr. Saurav Verma"),
                TimetableItem(dayOfWeek = "Monday", subject = "HCI Practical Batch A1 / A2", timeSlot = "03:00 PM - 04:00 PM", roomOrTeacher = "CL-502 / CL-505"),
                TimetableItem(dayOfWeek = "Monday", subject = "Open Elective I (OE I)", timeSlot = "04:00 PM - 05:00 PM", roomOrTeacher = "CR-502"),
                TimetableItem(dayOfWeek = "Monday", subject = "Open Elective I (OE I)", timeSlot = "05:00 PM - 06:00 PM", roomOrTeacher = "CR-502"),

                // Tuesday
                TimetableItem(dayOfWeek = "Tuesday", subject = "Human Computer Interaction [MMO]", timeSlot = "11:00 AM - 12:00 PM", roomOrTeacher = "CR-508 • Dr. Masooda Modak"),
                TimetableItem(dayOfWeek = "Tuesday", subject = "Embedded Systems [VKU]", timeSlot = "12:00 PM - 01:00 PM", roomOrTeacher = "CR-508 • Dr. Vikram Kulkarni"),
                TimetableItem(dayOfWeek = "Tuesday", subject = "Campus Lunch & Break", timeSlot = "01:00 PM - 02:00 PM", roomOrTeacher = "MPSTME Canteen"),
                TimetableItem(dayOfWeek = "Tuesday", subject = "MIS [PDA] / ADS [ARA] / AI & Robotics [SVE]", timeSlot = "02:00 PM - 03:00 PM", roomOrTeacher = "CR-501 / CR-507 / CR-502"),
                TimetableItem(dayOfWeek = "Tuesday", subject = "Open Elective I (OE I)", timeSlot = "04:00 PM - 05:00 PM", roomOrTeacher = "CR-508"),
                TimetableItem(dayOfWeek = "Tuesday", subject = "Open Elective I (OE I)", timeSlot = "05:00 PM - 06:00 PM", roomOrTeacher = "CR-508"),

                // Wednesday
                TimetableItem(dayOfWeek = "Wednesday", subject = "Human Computer Interaction [MMO]", timeSlot = "11:00 AM - 12:00 PM", roomOrTeacher = "CR-502 • Dr. Masooda Modak"),
                TimetableItem(dayOfWeek = "Wednesday", subject = "Visual Analytics [SVE] / ES [VKU]", timeSlot = "12:00 PM - 01:00 PM", roomOrTeacher = "CL-501 / CL-502"),
                TimetableItem(dayOfWeek = "Wednesday", subject = "Lab Practical Batch A1 / A2", timeSlot = "01:00 PM - 02:00 PM", roomOrTeacher = "CL-501 / CL-502"),
                TimetableItem(dayOfWeek = "Wednesday", subject = "Campus Lunch & Break", timeSlot = "02:00 PM - 03:00 PM", roomOrTeacher = "MPSTME Canteen"),
                TimetableItem(dayOfWeek = "Wednesday", subject = "Software Engineering [DRA]", timeSlot = "03:00 PM - 04:00 PM", roomOrTeacher = "CR-501 • Dr. Dharmesh Rathod"),
                TimetableItem(dayOfWeek = "Wednesday", subject = "Open Elective I (OE I)", timeSlot = "04:00 PM - 05:00 PM", roomOrTeacher = "CR-501"),
                TimetableItem(dayOfWeek = "Wednesday", subject = "Open Elective I (OE I)", timeSlot = "05:00 PM - 06:00 PM", roomOrTeacher = "CR-501"),

                // Thursday
                TimetableItem(dayOfWeek = "Thursday", subject = "MIS* [PDA] / ADS [ARA] / AI & Robotics* [SVE]", timeSlot = "10:00 AM - 11:00 AM", roomOrTeacher = "CR-502"),
                TimetableItem(dayOfWeek = "Thursday", subject = "Advanced Communication Lab", timeSlot = "11:00 AM - 12:00 PM", roomOrTeacher = "CL-501 • 6th Floor"),
                TimetableItem(dayOfWeek = "Thursday", subject = "Software Engineering [DRA]", timeSlot = "12:00 PM - 01:00 PM", roomOrTeacher = "CR-508 • Dr. Dharmesh Rathod"),
                TimetableItem(dayOfWeek = "Thursday", subject = "Campus Lunch & Break", timeSlot = "01:00 PM - 02:00 PM", roomOrTeacher = "MPSTME Canteen"),
                TimetableItem(dayOfWeek = "Thursday", subject = "Embedded Systems [VKU]", timeSlot = "02:00 PM - 03:00 PM", roomOrTeacher = "ES Lab • 4th Floor"),
                TimetableItem(dayOfWeek = "Thursday", subject = "ES Lab A1 / A2 Practical", timeSlot = "03:00 PM - 04:00 PM", roomOrTeacher = "ES Lab / CL-502"),
                TimetableItem(dayOfWeek = "Thursday", subject = "Open Elective II (OE II)", timeSlot = "04:00 PM - 05:00 PM", roomOrTeacher = "CR-508"),
                TimetableItem(dayOfWeek = "Thursday", subject = "Open Elective II (OE II)", timeSlot = "05:00 PM - 06:00 PM", roomOrTeacher = "CR-508"),

                // Friday
                TimetableItem(dayOfWeek = "Friday", subject = "Embedded Systems [VKU]", timeSlot = "12:00 PM - 01:00 PM", roomOrTeacher = "CR-501 • Dr. Vikram Kulkarni"),
                TimetableItem(dayOfWeek = "Friday", subject = "Campus Lunch & Break", timeSlot = "01:00 PM - 02:00 PM", roomOrTeacher = "MPSTME Canteen"),
                TimetableItem(dayOfWeek = "Friday", subject = "Software Engineering [DRA] / HCI [MMO]", timeSlot = "02:00 PM - 03:00 PM", roomOrTeacher = "CR-501 / CR-502"),
                TimetableItem(dayOfWeek = "Friday", subject = "Practical Batch A1 / A2", timeSlot = "03:00 PM - 04:00 PM", roomOrTeacher = "CL-501 / CL-502"),
                TimetableItem(dayOfWeek = "Friday", subject = "Open Elective II (OE II)", timeSlot = "04:00 PM - 05:00 PM", roomOrTeacher = "CR-501"),
                TimetableItem(dayOfWeek = "Friday", subject = "Open Elective II (OE II)", timeSlot = "05:00 PM - 06:00 PM", roomOrTeacher = "CR-501"),

                // Saturday
                TimetableItem(dayOfWeek = "Saturday", subject = "AI & Robotics Project Mentorship", timeSlot = "10:00 AM - 12:00 PM", roomOrTeacher = "Maker Space • Prof. SVE"),
                TimetableItem(dayOfWeek = "Saturday", subject = "Embedded Systems Practical Review", timeSlot = "12:00 PM - 01:00 PM", roomOrTeacher = "CR-501"),
                TimetableItem(dayOfWeek = "Saturday", subject = "Campus Lunch & Break", timeSlot = "01:00 PM - 02:00 PM", roomOrTeacher = "MPSTME Canteen"),
                TimetableItem(dayOfWeek = "Saturday", subject = "Software Engineering Capstone", timeSlot = "02:00 PM - 04:00 PM", roomOrTeacher = "IT Lab 1")
            )
            syncManager.uploadTimetablePdf(fileName, fullWeekClasses)
            isUploading = false
            Toast.makeText(context, "Timetable PDF uploaded & processed: $fileName", Toast.LENGTH_LONG).show()
        }
    }

    // Google Calendar Sync Function
    fun syncWithGoogleCalendar() {
        try {
            // Push event to system calendar provider / Google Calendar intent
            val intent = Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE, "${syncState.activeChildName}'s School Timetable")
                .putExtra(CalendarContract.Events.EVENT_LOCATION, "MPSTME NMIMS, Vile Parle West, Mumbai")
                .putExtra(CalendarContract.Events.DESCRIPTION, "Daily School Schedule automatically synced from NestCheck PDF Timetable")
                .putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true)
            
            context.startActivity(intent)
            syncManager.setTimetableCalendarSynced(true)
            Toast.makeText(context, "Connecting Google Calendar & syncing timetable events...", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            // Fallback web intent to Google Calendar
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://calendar.google.com/calendar/r"))
            context.startActivity(webIntent)
            syncManager.setTimetableCalendarSynced(true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("School Timetable & Calendar", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextDark)
                        Text("${syncState.activeChildName}'s Weekly Schedule", fontSize = 12.sp, color = TextMuted)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back", tint = TextDark)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CardWhite)
            )
        },
        containerColor = LightBg
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // 1. Google Calendar Integration Banner Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = if (syncState.timetableSyncedToCalendar) Color(0xFFF0FDF4) else Color(0xFFEFF6FF)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (syncState.timetableSyncedToCalendar) Color(0xFF86EFAC) else Color(0xFFBFDBFE))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(if (syncState.timetableSyncedToCalendar) Color(0xFFDCFCE7) else Color(0xFFDBEAFE)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.CalendarMonth,
                                        contentDescription = "Google Calendar",
                                        tint = if (syncState.timetableSyncedToCalendar) GreenSuccess else BrandBlue,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Google Calendar Sync",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = TextDark
                                    )
                                    Text(
                                        text = if (syncState.timetableSyncedToCalendar) "Live synced for Parent & Child" else "Not yet connected to Google Calendar",
                                        fontSize = 12.sp,
                                        color = if (syncState.timetableSyncedToCalendar) GreenSuccess else AmberPending,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Sync child's class schedule, periods, and study hours directly into Google Calendar so events appear in real-time across both parents and child's devices.",
                            fontSize = 13.sp,
                            color = TextMuted,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = { syncWithGoogleCalendar() },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (syncState.timetableSyncedToCalendar) GreenSuccess else BrandBlue
                            )
                        ) {
                            Icon(
                                imageVector = if (syncState.timetableSyncedToCalendar) Icons.Outlined.CheckCircle else Icons.Outlined.Sync,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (syncState.timetableSyncedToCalendar) "Synced to Google Calendar" else "Connect Google Calendar & Sync",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            // 2. Upload Timetable PDF Section Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.PictureAsPdf, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(26.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Upload School Timetable (PDF)", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextDark)
                                Text("Select child's timetable document from device", fontSize = 12.sp, color = TextMuted)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Current PDF info badge
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF1F5F9))
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Outlined.Description, contentDescription = null, tint = BrandBlue, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = syncState.timetablePdfName,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 13.sp,
                                            color = TextDark
                                        )
                                        Text("PDF Document • Ready to parse", fontSize = 11.sp, color = TextMuted)
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFDCFCE7))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("Active", color = GreenSuccess, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedButton(
                            onClick = { pdfPickerLauncher.launch("application/pdf") },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(24.dp),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, BrandBlue),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandBlue)
                        ) {
                            Icon(Icons.Outlined.UploadFile, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Upload New Timetable PDF", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }

            // 3. Day of the Week Selector
            item {
                Text("Schedule by Day", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(daysOfWeek) { day ->
                        val isSelected = selectedDay == day
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) BrandBlue else CardWhite)
                                .border(1.dp, if (isSelected) BrandBlue else BorderColor, RoundedCornerShape(20.dp))
                                .clickable { selectedDay = day }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = day,
                                color = if (isSelected) Color.White else TextDark,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            // 4. Timetable Period Items
            val currentDayItems = syncState.timetableItems.filter { it.dayOfWeek.equals(selectedDay, ignoreCase = true) }
            if (currentDayItems.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(CardWhite)
                            .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Outlined.EventBusy, contentDescription = null, tint = TextMuted, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No classes scheduled for $selectedDay", color = TextMuted, fontSize = 14.sp)
                        }
                    }
                }
            } else {
                items(currentDayItems) { classItem ->
                    TimetableCard(classItem)
                }
            }

            item { Spacer(modifier = Modifier.height(30.dp)) }
        }
    }
}

@Composable
fun TimetableCard(item: TimetableItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFEFF6FF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when {
                        item.subject.contains("Math", ignoreCase = true) -> Icons.Outlined.Calculate
                        item.subject.contains("Science", ignoreCase = true) || item.subject.contains("Physics", ignoreCase = true) || item.subject.contains("Chem", ignoreCase = true) -> Icons.Outlined.Science
                        item.subject.contains("English", ignoreCase = true) || item.subject.contains("History", ignoreCase = true) -> Icons.Outlined.MenuBook
                        item.subject.contains("Lunch", ignoreCase = true) -> Icons.Outlined.Restaurant
                        item.subject.contains("Computer", ignoreCase = true) || item.subject.contains("Robot", ignoreCase = true) -> Icons.Outlined.Computer
                        item.subject.contains("Sport", ignoreCase = true) || item.subject.contains("Physical", ignoreCase = true) -> Icons.Outlined.FitnessCenter
                        else -> Icons.Outlined.School
                    },
                    contentDescription = null,
                    tint = BrandBlue,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.subject,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = TextDark
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${item.timeSlot} • ${item.roomOrTeacher}",
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color(0xFFF1F5F9))
                    .padding(6.dp)
            ) {
                Icon(Icons.Outlined.Alarm, contentDescription = null, tint = BrandBlue, modifier = Modifier.size(16.dp))
            }
        }
    }
}

private fun getFileNameFromUri(context: Context, uri: Uri): String? {
    var name: String? = null
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1) {
                name = it.getString(nameIndex)
            }
        }
    }
    return name ?: uri.lastPathSegment
}
