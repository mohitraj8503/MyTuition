package com.example.mytuition.feature.teacher.calendar

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mytuition.core.designsystem.*
import com.example.mytuition.core.designsystem.components.*
import com.example.mytuition.core.di.AppContainer
import com.example.mytuition.core.domain.model.SessionStatus
import com.example.mytuition.core.domain.model.TeacherSessionItem
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherCalendarScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val repo = AppContainer.teacherRepository

    var selectedDate by remember {
        mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()))
    }
    var sessions by remember { mutableStateOf<List<TeacherSessionItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    var sessionToReschedule by remember { mutableStateOf<TeacherSessionItem?>(null) }
    var sessionToCancel by remember { mutableStateOf<TeacherSessionItem?>(null) }

    fun loadData() {
        coroutineScope.launch {
            isLoading = true
            val res = repo.getTeacherHomeData(selectedDate)
            sessions = res.getOrNull()?.todaySessions ?: emptyList()
            isLoading = false
        }
    }

    LaunchedEffect(selectedDate) {
        loadData()
    }

    PastelBackground(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Teacher Schedule",
                            style = MyTuitionTypography.TitleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MyTuitionColors.TextPrimary
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = "Back",
                                tint = MyTuitionColors.TextPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Date Selector Header
                ClayCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Selected Date", style = MyTuitionTypography.LabelSmall, color = MyTuitionColors.TextSecondary)
                            Text(selectedDate, style = MyTuitionTypography.TitleSmall.copy(fontWeight = FontWeight.Bold))
                        }
                        Button(
                            onClick = {
                                val cal = Calendar.getInstance()
                                cal.add(Calendar.DAY_OF_YEAR, 1)
                                selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MyTuitionColors.PrimaryPurple),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Next Day")
                        }
                    }
                }

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MyTuitionColors.PrimaryPurple)
                    }
                } else if (sessions.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No sessions scheduled for $selectedDate", color = MyTuitionColors.TextSecondary)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        items(sessions) { s ->
                            SessionCalendarCard(
                                session = s,
                                onReschedule = { sessionToReschedule = s },
                                onCancel = { sessionToCancel = s }
                            )
                        }
                    }
                }
            }
        }

        // Reschedule Bottom Sheet
        if (sessionToReschedule != null) {
            RescheduleBottomSheet(
                session = sessionToReschedule!!,
                onDismiss = { sessionToReschedule = null },
                onConfirm = { newDate, newStart, newEnd ->
                    coroutineScope.launch {
                        val sid = sessionToReschedule!!.sessionId
                        val res = repo.rescheduleSession(sid, newDate, newStart, newEnd)
                        sessionToReschedule = null
                        if (res.isSuccess) {
                            Toast.makeText(context, "Session rescheduled successfully!", Toast.LENGTH_SHORT).show()
                            loadData()
                        } else {
                            Toast.makeText(context, "Failed to reschedule", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            )
        }

        // Cancel Confirm Dialog
        if (sessionToCancel != null) {
            var cancelReason by remember { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = { sessionToCancel = null },
                title = { Text("Cancel Class Session?") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Are you sure you want to cancel ${sessionToCancel!!.subjectName}?")
                        OutlinedTextField(
                            value = cancelReason,
                            onValueChange = { cancelReason = it },
                            label = { Text("Reason (Optional)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            coroutineScope.launch {
                                val sid = sessionToCancel!!.sessionId
                                val res = repo.cancelSession(sid, cancelReason)
                                sessionToCancel = null
                                if (res.isSuccess) {
                                    Toast.makeText(context, "Session cancelled", Toast.LENGTH_SHORT).show()
                                    loadData()
                                } else {
                                    Toast.makeText(context, "Failed to cancel session", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    ) {
                        Text("Confirm Cancel", color = MyTuitionColors.ErrorRed, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { sessionToCancel = null }) {
                        Text("Keep Session")
                    }
                }
            )
        }
    }
}

@Composable
private fun SessionCalendarCard(
    session: TeacherSessionItem,
    onReschedule: () -> Unit,
    onCancel: () -> Unit
) {
    ClayCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    session.timeSlotDisplay,
                    style = MyTuitionTypography.TitleSmall.copy(fontWeight = FontWeight.Bold, color = MyTuitionColors.PrimaryPurple)
                )

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (session.status == SessionStatus.CANCELLED) MyTuitionColors.ErrorRed.copy(alpha = 0.15f) else MyTuitionColors.SuccessGreen.copy(alpha = 0.15f)
                ) {
                    Text(
                        session.status.name,
                        style = MyTuitionTypography.LabelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (session.status == SessionStatus.CANCELLED) MyTuitionColors.ErrorRed else MyTuitionColors.SuccessGreen
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Text(
                session.subjectName,
                style = MyTuitionTypography.TitleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Text(
                "Room: ${session.roomName} • ${session.floorName}",
                style = MyTuitionTypography.BodyMedium,
                color = MyTuitionColors.TextSecondary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onReschedule) {
                    Icon(Icons.Rounded.Schedule, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reschedule")
                }
                TextButton(onClick = onCancel) {
                    Icon(Icons.Rounded.Close, contentDescription = null, modifier = Modifier.size(16.dp), tint = MyTuitionColors.ErrorRed)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Cancel", color = MyTuitionColors.ErrorRed)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RescheduleBottomSheet(
    session: TeacherSessionItem,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit
) {
    var date by remember { mutableStateOf(session.date) }
    var start by remember { mutableStateOf(session.startTime) }
    var end by remember { mutableStateOf(session.endTime) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Reschedule Class", style = MyTuitionTypography.TitleMedium.copy(fontWeight = FontWeight.Bold))

            OutlinedTextField(
                value = date,
                onValueChange = { date = it },
                label = { Text("New Date (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = start,
                    onValueChange = { start = it },
                    label = { Text("Start Time") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = end,
                    onValueChange = { end = it },
                    label = { Text("End Time") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Button(
                onClick = { onConfirm(date, start, end) },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MyTuitionColors.PrimaryPurple)
            ) {
                Text("Confirm Reschedule", fontWeight = FontWeight.Bold)
            }
        }
    }
}
