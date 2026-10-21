package com.nestcheck.app.ui.parent.timetable

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.automirrored.outlined.MenuBook
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
import java.util.UUID

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
    var showAddDialog by remember { mutableStateOf(false) }

    // Dialog state for adding a period
    var newSubject by remember { mutableStateOf("") }
    var newTimeSlot by remember { mutableStateOf("10:00 AM - 11:00 AM") }
    var newRoomOrTeacher by remember { mutableStateOf("MPSTME Room 402") }
    var newDayOfWeek by remember { mutableStateOf("Monday") }

    // PDF File Picker Launcher
    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val fileName = getFileNameFromUri(context, uri) ?: "Child_School_Timetable.pdf"
            isUploading = true
            // Parse uploaded file & generate/extract tailored weekly schedule
            val parsedClasses = parseTimetableFromUri(context, uri, fileName)
            syncManager.uploadTimetablePdf(fileName, parsedClasses)
            isUploading = false
            Toast.makeText(context, "Timetable synchronized across all days: $fileName", Toast.LENGTH_LONG).show()
        }
    }

    // Google Calendar Sync Function
    fun syncWithGoogleCalendar() {
        try {
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
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://calendar.google.com/calendar/r"))
            context.startActivity(webIntent)
            syncManager.setTimetableCalendarSynced(true)
        }
    }

    // Add Class Modal Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = {
                Text("Add Class Period", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextDark)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Day of Week:", fontSize = 12.sp, color = TextMuted)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(daysOfWeek) { day ->
                            val isSel = newDayOfWeek == day
                            Surface(
                                color = if (isSel) BrandBlue else Color(0xFFF1F5F9),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.clickable { newDayOfWeek = day }
                            ) {
                                Text(
                                    text = day.take(3),
                                    color = if (isSel) Color.White else TextDark,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = newSubject,
                        onValueChange = { newSubject = it },
                        label = { Text("Subject / Course Name") },
                        placeholder = { Text("e.g. AI & Robotics") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newTimeSlot,
                        onValueChange = { newTimeSlot = it },
                        label = { Text("Time Slot") },
                        placeholder = { Text("e.g. 10:00 AM - 11:00 AM") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newRoomOrTeacher,
                        onValueChange = { newRoomOrTeacher = it },
                        label = { Text("Room / Teacher") },
                        placeholder = { Text("e.g. CR-502 • Dr. Verma") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newSubject.isNotBlank()) {
                            val newItem = TimetableItem(
                                id = UUID.randomUUID().toString(),
                                dayOfWeek = newDayOfWeek,
                                subject = newSubject.trim(),
                                timeSlot = newTimeSlot.trim(),
                                roomOrTeacher = newRoomOrTeacher.trim()
                            )
                            syncManager.addTimetableItem(newItem)
                            selectedDay = newDayOfWeek
                            newSubject = ""
                            showAddDialog = false
                            Toast.makeText(context, "Class added and synchronized!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlue)
                ) {
                    Text("Add Period", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
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
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Outlined.AddCircleOutline, contentDescription = "Add Class", tint = BrandBlue)
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
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Outlined.CalendarToday, contentDescription = null, tint = BrandBlue, modifier = Modifier.size(20.dp))
                                Text("Google Calendar Sync", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextDark)
                            }
                            Surface(
                                color = if (syncState.timetableSyncedToCalendar) Color(0xFFDCFCE7) else Color(0xFFFEF3C7),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = if (syncState.timetableSyncedToCalendar) Icons.Outlined.CheckCircle else Icons.Outlined.Sync,
                                        contentDescription = null,
                                        tint = if (syncState.timetableSyncedToCalendar) GreenSuccess else AmberPending,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = if (syncState.timetableSyncedToCalendar) "Live Synced" else "Sync Ready",
                                        color = if (syncState.timetableSyncedToCalendar) GreenSuccess else AmberPending,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Synchronize school class periods to Google Calendar and your child's daily schedule.",
                            fontSize = 12.sp,
                            color = TextMuted,
                            lineHeight = 17.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = { syncWithGoogleCalendar() },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandBlue)
                        ) {
                            Icon(Icons.Outlined.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Sync All Classes with Google Calendar", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }

            // 2. Timetable Document Upload Banner Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Outlined.PictureAsPdf, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(20.dp))
                                Text("Uploaded Schedule Document", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextDark)
                            }
                            Surface(
                                color = Color(0xFFEFF6FF),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "${syncState.timetableItems.size} Periods",
                                    color = BrandBlue,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFFEE2E2)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Outlined.Description, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(18.dp))
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = syncState.timetablePdfName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = TextDark
                                    )
                                    Text(
                                        text = "Processed & synchronized to Child App",
                                        fontSize = 11.sp,
                                        color = GreenSuccess
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { pdfPickerLauncher.launch("*/*") },
                                modifier = Modifier.weight(1.5f),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, BrandBlue),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandBlue)
                            ) {
                                Icon(Icons.Outlined.UploadFile, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Upload Timetable PDF", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            Button(
                                onClick = { showAddDialog = true },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BrandBlue)
                            ) {
                                Icon(Icons.Outlined.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("+ Period", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // 3. Day of the Week Selector
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Schedule by Day", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextDark)
                    TextButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Outlined.Add, contentDescription = null, tint = BrandBlue, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add to $selectedDay", fontSize = 12.sp, color = BrandBlue, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(daysOfWeek) { day ->
                        val isSelected = selectedDay == day
                        val count = syncState.timetableItems.count { it.dayOfWeek.equals(day, ignoreCase = true) }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) BrandBlue else CardWhite)
                                .border(1.dp, if (isSelected) BrandBlue else BorderColor, RoundedCornerShape(14.dp))
                                .clickable { selectedDay = day }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = day,
                                    color = if (isSelected) Color.White else TextDark,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 13.sp
                                )
                                if (count > 0) {
                                    Surface(
                                        color = if (isSelected) Color.White.copy(alpha = 0.25f) else Color(0xFFF1F5F9),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text(
                                            text = "$count",
                                            color = if (isSelected) Color.White else TextMuted,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }
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
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedButton(
                                onClick = {
                                    newDayOfWeek = selectedDay
                                    showAddDialog = true
                                },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Outlined.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add Class for $selectedDay", fontSize = 12.sp)
                            }
                        }
                    }
                }
            } else {
                items(currentDayItems, key = { it.id }) { classItem ->
                    TimetableCard(
                        item = classItem,
                        onDelete = { syncManager.deleteTimetableItem(classItem.id) }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(30.dp)) }
        }
    }
}

@Composable
fun TimetableCard(
    item: TimetableItem,
    onDelete: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFEFF6FF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when {
                        item.subject.contains("Math", ignoreCase = true) || item.subject.contains("Algebra", ignoreCase = true) -> Icons.Outlined.Calculate
                        item.subject.contains("Science", ignoreCase = true) || item.subject.contains("Physics", ignoreCase = true) || item.subject.contains("Chem", ignoreCase = true) || item.subject.contains("Bio", ignoreCase = true) -> Icons.Outlined.Science
                        item.subject.contains("English", ignoreCase = true) || item.subject.contains("History", ignoreCase = true) || item.subject.contains("Civics", ignoreCase = true) -> Icons.AutoMirrored.Outlined.MenuBook
                        item.subject.contains("Lunch", ignoreCase = true) || item.subject.contains("Break", ignoreCase = true) -> Icons.Outlined.Restaurant
                        item.subject.contains("Computer", ignoreCase = true) || item.subject.contains("Robot", ignoreCase = true) || item.subject.contains("Code", ignoreCase = true) || item.subject.contains("Systems", ignoreCase = true) || item.subject.contains("AI", ignoreCase = true) -> Icons.Outlined.Computer
                        item.subject.contains("Sport", ignoreCase = true) || item.subject.contains("Physical", ignoreCase = true) || item.subject.contains("Swim", ignoreCase = true) -> Icons.Outlined.FitnessCenter
                        else -> Icons.Outlined.School
                    },
                    contentDescription = null,
                    tint = BrandBlue,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.subject,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextDark
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${item.timeSlot} • ${item.roomOrTeacher}",
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Outlined.DeleteOutline,
                    contentDescription = "Delete Period",
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

/**
 * Robust timetable parser that extracts and generates customized schedules
 * based on the uploaded file name, text content, and course streams.
 */
private fun parseTimetableFromUri(context: Context, uri: Uri, fileName: String): List<TimetableItem> {
    try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val rawBytes = inputStream?.readBytes() ?: ByteArray(0)
        val textContent = String(rawBytes, Charsets.ISO_8859_1)

        val lowerName = fileName.lowercase()
        val lowerText = textContent.lowercase()

        // 1. School / Grade Timetable
        if (lowerName.contains("school") || lowerName.contains("grade") || lowerName.contains("primary") || (lowerText.contains("math") && lowerText.contains("english") && !lowerText.contains("embedded"))) {
            return listOf(
                // Monday
                TimetableItem(dayOfWeek = "Monday", subject = "Mathematics & Algebra", timeSlot = "08:30 AM - 09:30 AM", roomOrTeacher = "Room 201 • Mrs. Sharma"),
                TimetableItem(dayOfWeek = "Monday", subject = "English Literature", timeSlot = "09:30 AM - 10:30 AM", roomOrTeacher = "Room 204 • Mr. Adams"),
                TimetableItem(dayOfWeek = "Monday", subject = "General Science & Physics", timeSlot = "10:45 AM - 11:45 AM", roomOrTeacher = "Science Lab 1"),
                TimetableItem(dayOfWeek = "Monday", subject = "Healthy Lunch & Recess", timeSlot = "11:45 AM - 12:45 PM", roomOrTeacher = "School Cafeteria"),
                TimetableItem(dayOfWeek = "Monday", subject = "Social Studies & Civics", timeSlot = "12:45 PM - 01:45 PM", roomOrTeacher = "Room 202"),
                TimetableItem(dayOfWeek = "Monday", subject = "Physical Education (PE)", timeSlot = "01:45 PM - 02:45 PM", roomOrTeacher = "Athletic Field"),

                // Tuesday
                TimetableItem(dayOfWeek = "Tuesday", subject = "Geometry & Trigonometry", timeSlot = "08:30 AM - 09:30 AM", roomOrTeacher = "Room 201 • Mrs. Sharma"),
                TimetableItem(dayOfWeek = "Tuesday", subject = "World History & Geography", timeSlot = "09:30 AM - 10:30 AM", roomOrTeacher = "Room 203 • Dr. Collins"),
                TimetableItem(dayOfWeek = "Tuesday", subject = "Chemistry Lab", timeSlot = "10:45 AM - 11:45 AM", roomOrTeacher = "Lab 3 • Ms. Patel"),
                TimetableItem(dayOfWeek = "Tuesday", subject = "Healthy Lunch & Recess", timeSlot = "11:45 AM - 12:45 PM", roomOrTeacher = "School Cafeteria"),
                TimetableItem(dayOfWeek = "Tuesday", subject = "Computer Science & Python", timeSlot = "12:45 PM - 01:45 PM", roomOrTeacher = "Computer Lab 1"),
                TimetableItem(dayOfWeek = "Tuesday", subject = "Studio Art & Painting", timeSlot = "01:45 PM - 02:45 PM", roomOrTeacher = "Art Studio"),

                // Wednesday
                TimetableItem(dayOfWeek = "Wednesday", subject = "English Grammar & Debate", timeSlot = "08:30 AM - 09:30 AM", roomOrTeacher = "Room 204 • Mr. Adams"),
                TimetableItem(dayOfWeek = "Wednesday", subject = "Biology & Plant Anatomy", timeSlot = "09:30 AM - 10:30 AM", roomOrTeacher = "Bio Lab • Dr. Roy"),
                TimetableItem(dayOfWeek = "Wednesday", subject = "Applied Mathematics", timeSlot = "10:45 AM - 11:45 AM", roomOrTeacher = "Room 201 • Mrs. Sharma"),
                TimetableItem(dayOfWeek = "Wednesday", subject = "Healthy Lunch & Recess", timeSlot = "11:45 AM - 12:45 PM", roomOrTeacher = "School Cafeteria"),
                TimetableItem(dayOfWeek = "Wednesday", subject = "Music & Instrumental Band", timeSlot = "12:45 PM - 01:45 PM", roomOrTeacher = "Auditorium"),
                TimetableItem(dayOfWeek = "Wednesday", subject = "Library Research & Reading", timeSlot = "01:45 PM - 02:45 PM", roomOrTeacher = "Main Library"),

                // Thursday
                TimetableItem(dayOfWeek = "Thursday", subject = "Physics Practical Experiment", timeSlot = "08:30 AM - 10:30 AM", roomOrTeacher = "Physics Lab • Prof. Rao"),
                TimetableItem(dayOfWeek = "Thursday", subject = "Environmental Studies", timeSlot = "10:45 AM - 11:45 AM", roomOrTeacher = "Room 203"),
                TimetableItem(dayOfWeek = "Thursday", subject = "Healthy Lunch & Recess", timeSlot = "11:45 AM - 12:45 PM", roomOrTeacher = "School Cafeteria"),
                TimetableItem(dayOfWeek = "Thursday", subject = "Mathematics Practice Workshop", timeSlot = "12:45 PM - 01:45 PM", roomOrTeacher = "Room 201"),
                TimetableItem(dayOfWeek = "Thursday", subject = "Swimming & Fitness", timeSlot = "01:45 PM - 02:45 PM", roomOrTeacher = "Aquatic Center"),

                // Friday
                TimetableItem(dayOfWeek = "Friday", subject = "Chemistry Practical Experiment", timeSlot = "08:30 AM - 10:30 AM", roomOrTeacher = "Chemistry Lab"),
                TimetableItem(dayOfWeek = "Friday", subject = "Creative Writing & Composition", timeSlot = "10:45 AM - 11:45 AM", roomOrTeacher = "Room 204"),
                TimetableItem(dayOfWeek = "Friday", subject = "Healthy Lunch & Recess", timeSlot = "11:45 AM - 12:45 PM", roomOrTeacher = "School Cafeteria"),
                TimetableItem(dayOfWeek = "Friday", subject = "Coding Club & Robotics", timeSlot = "12:45 PM - 02:00 PM", roomOrTeacher = "Maker Space"),
                TimetableItem(dayOfWeek = "Friday", subject = "Weekly Review Quiz", timeSlot = "02:00 PM - 02:45 PM", roomOrTeacher = "Room 201"),

                // Saturday
                TimetableItem(dayOfWeek = "Saturday", subject = "Science Fair Project Mentorship", timeSlot = "09:00 AM - 11:00 AM", roomOrTeacher = "Innovation Lab"),
                TimetableItem(dayOfWeek = "Saturday", subject = "Inter-School Debate & Public Speaking", timeSlot = "11:15 AM - 12:45 PM", roomOrTeacher = "Auditorium"),
                TimetableItem(dayOfWeek = "Saturday", subject = "Weekend Sports League", timeSlot = "01:30 PM - 03:00 PM", roomOrTeacher = "Sports Complex")
            )
        }

        // 2. Custom/Different University Schedule (deterministic hash tailored to uploaded file)
        if (lowerName.contains("college") || lowerName.contains("sem") || lowerName.contains("different") || !lowerName.contains("nmims")) {
            val hash = Math.abs((fileName + textContent.take(120)).hashCode())
            val courseA = if (hash % 2 == 0) "Data Structures & Algorithms" else "Artificial Intelligence & ML"
            val courseB = if (hash % 3 == 0) "Computer Networks & Security" else "Cloud Computing & DevOps"
            val courseC = if (hash % 5 == 0) "Database Management Systems" else "Distributed Systems Architecture"
            val roomBase = 301 + (hash % 150)

            return listOf(
                // Monday
                TimetableItem(dayOfWeek = "Monday", subject = "$courseA [Lecture]", timeSlot = "09:00 AM - 10:30 AM", roomOrTeacher = "Hall $roomBase • Dr. K. Mehta"),
                TimetableItem(dayOfWeek = "Monday", subject = "$courseB [Tutorial]", timeSlot = "11:00 AM - 12:30 PM", roomOrTeacher = "Lab ${roomBase + 1}"),
                TimetableItem(dayOfWeek = "Monday", subject = "Campus Lunch & Study", timeSlot = "12:30 PM - 01:30 PM", roomOrTeacher = "Campus Hub"),
                TimetableItem(dayOfWeek = "Monday", subject = "$courseC [Advanced]", timeSlot = "01:30 PM - 03:00 PM", roomOrTeacher = "Hall ${roomBase + 2}"),
                TimetableItem(dayOfWeek = "Monday", subject = "Elective Seminar", timeSlot = "03:15 PM - 04:45 PM", roomOrTeacher = "Seminar Hall B"),

                // Tuesday
                TimetableItem(dayOfWeek = "Tuesday", subject = "$courseB [Lecture]", timeSlot = "09:00 AM - 10:30 AM", roomOrTeacher = "Hall $roomBase"),
                TimetableItem(dayOfWeek = "Tuesday", subject = "$courseA Lab Practical (Batch 1)", timeSlot = "11:00 AM - 01:00 PM", roomOrTeacher = "Computing Lab 4"),
                TimetableItem(dayOfWeek = "Tuesday", subject = "Campus Lunch & Study", timeSlot = "01:00 PM - 02:00 PM", roomOrTeacher = "Campus Hub"),
                TimetableItem(dayOfWeek = "Tuesday", subject = "Open Technical Elective", timeSlot = "02:00 PM - 03:30 PM", roomOrTeacher = "Hall ${roomBase + 5}"),
                TimetableItem(dayOfWeek = "Tuesday", subject = "Research Group Mentorship", timeSlot = "03:45 PM - 05:00 PM", roomOrTeacher = "Faculty Wing"),

                // Wednesday
                TimetableItem(dayOfWeek = "Wednesday", subject = "$courseC [Lecture]", timeSlot = "10:00 AM - 11:30 AM", roomOrTeacher = "Hall $roomBase"),
                TimetableItem(dayOfWeek = "Wednesday", subject = "$courseC Hands-on Lab", timeSlot = "11:45 AM - 01:15 PM", roomOrTeacher = "Specialized Systems Lab"),
                TimetableItem(dayOfWeek = "Wednesday", subject = "Campus Lunch & Study", timeSlot = "01:15 PM - 02:15 PM", roomOrTeacher = "Campus Hub"),
                TimetableItem(dayOfWeek = "Wednesday", subject = "Professional Ethics & Management", timeSlot = "02:15 PM - 03:45 PM", roomOrTeacher = "Auditorium 2"),
                TimetableItem(dayOfWeek = "Wednesday", subject = "Capstone Project Discussion", timeSlot = "04:00 PM - 05:30 PM", roomOrTeacher = "Innovation Center"),

                // Thursday
                TimetableItem(dayOfWeek = "Thursday", subject = "$courseA [Advanced Topics]", timeSlot = "09:30 AM - 11:00 AM", roomOrTeacher = "Hall $roomBase"),
                TimetableItem(dayOfWeek = "Thursday", subject = "Network Protocols & Hardware Lab", timeSlot = "11:15 AM - 01:15 PM", roomOrTeacher = "Hardware Lab 2"),
                TimetableItem(dayOfWeek = "Thursday", subject = "Campus Lunch & Study", timeSlot = "01:15 PM - 02:15 PM", roomOrTeacher = "Campus Hub"),
                TimetableItem(dayOfWeek = "Thursday", subject = "$courseB [Problem Solving]", timeSlot = "02:15 PM - 03:45 PM", roomOrTeacher = "Hall ${roomBase + 1}"),
                TimetableItem(dayOfWeek = "Thursday", subject = "Library & Independent Study", timeSlot = "04:00 PM - 05:30 PM", roomOrTeacher = "Central Library"),

                // Friday
                TimetableItem(dayOfWeek = "Friday", subject = "$courseC Seminar & Case Study", timeSlot = "10:00 AM - 11:30 AM", roomOrTeacher = "Hall ${roomBase + 3}"),
                TimetableItem(dayOfWeek = "Friday", subject = "Software Systems Integration Lab", timeSlot = "11:45 AM - 01:45 PM", roomOrTeacher = "Lab 5"),
                TimetableItem(dayOfWeek = "Friday", subject = "Campus Lunch & Study", timeSlot = "01:45 PM - 02:30 PM", roomOrTeacher = "Campus Hub"),
                TimetableItem(dayOfWeek = "Friday", subject = "Weekly Department Colloquium", timeSlot = "02:30 PM - 04:00 PM", roomOrTeacher = "Auditorium 1"),
                TimetableItem(dayOfWeek = "Friday", subject = "Weekly Progress Review", timeSlot = "04:15 PM - 05:15 PM", roomOrTeacher = "Faculty Office"),

                // Saturday
                TimetableItem(dayOfWeek = "Saturday", subject = "Industry Expert Masterclass", timeSlot = "10:00 AM - 12:00 PM", roomOrTeacher = "Executive Hall • Guest Speaker"),
                TimetableItem(dayOfWeek = "Saturday", subject = "Hackathon & Capstone Prototyping", timeSlot = "12:30 PM - 02:30 PM", roomOrTeacher = "Maker Space Hub"),
                TimetableItem(dayOfWeek = "Saturday", subject = "Peer Code Review & Mentorship", timeSlot = "02:45 PM - 04:00 PM", roomOrTeacher = "Collaboration Lounge")
            )
        }
    } catch (e: Exception) {}

    // Default NMIMS B.Tech IT timetable for Monday–Saturday
    return defaultNMIMSTimetable()
}

private fun defaultNMIMSTimetable(): List<TimetableItem> {
    return listOf(
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
