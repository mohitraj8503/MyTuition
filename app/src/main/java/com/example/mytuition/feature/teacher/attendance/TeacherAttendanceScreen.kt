package com.example.mytuition.feature.teacher.attendance

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mytuition.core.designsystem.MyTuitionColors
import com.example.mytuition.core.designsystem.MyTuitionSpacing
import com.example.mytuition.core.designsystem.MyTuitionTypography
import com.example.mytuition.core.designsystem.PastelBackground
import com.example.mytuition.core.designsystem.components.*
import com.example.mytuition.core.di.AppContainer
import com.example.mytuition.core.domain.model.AttendanceStatus
import com.example.mytuition.core.domain.model.StudentRosterItem
import kotlinx.coroutines.launch

@Composable
fun TeacherAttendanceScreen(
    sessionId: String,
    onBackClick: () -> Unit,
    viewModel: TeacherAttendanceViewModel = viewModel(
        factory = TeacherAttendanceViewModel.provideFactory(sessionId, AppContainer.teacherRepository)
    )
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    PastelBackground(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {

        when (val uiState = state) {
            AttendanceUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MyTuitionColors.PrimaryPurple)
                }
            }
            is AttendanceUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                    ClayCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Failed to load roster", style = MyTuitionTypography.TitleSmall)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(uiState.message, style = MyTuitionTypography.BodyMedium, color = MyTuitionColors.TextSecondary)
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { viewModel.loadStudents() }) { Text("Retry") }
                        }
                    }
                }
            }
            is AttendanceUiState.Ready -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Header with back button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBackClick) {
                            Icon(imageVector = Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                        }
                        Text(
                            text = "Take Attendance",
                            style = MyTuitionTypography.TitleMedium.copy(
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = MyTuitionColors.TextPrimary
                            )
                        )
                    }

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = MyTuitionSpacing.lg, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // 1. Session Info Card (lavender ClayCard)
                        item {
                            ClayCard(
                                modifier = Modifier.fillMaxWidth(),
                                cardColor = Color(0xFFF3EFFF),
                                borderColor = Color(0xFFE2D6FF)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Class 10-A • Mathematics",
                                        style = MyTuitionTypography.TitleSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Today • 4:00 PM - 5:30 PM • Room 4B",
                                        style = MyTuitionTypography.BodySmall.copy(color = MyTuitionColors.TextSecondary)
                                    )
                                }
                            }
                        }

                        // 2. Mark All Present Bar
                        item {
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = Color(0xFFE8F5E9),
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFC8E6C9)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.markAllPresent() }
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.DoneAll,
                                        contentDescription = null,
                                        tint = Color(0xFF2E7D32),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Mark All Present (${uiState.students.size} Students)",
                                        style = MyTuitionTypography.LabelLarge.copy(
                                            color = Color(0xFF2E7D32),
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }

                        // 3. Roll-call students
                        items(uiState.students, key = { it.studentId }) { student ->
                            val currentStatus = uiState.attendanceMap[student.studentId] ?: AttendanceStatus.UNMARKED
                            StudentAttendanceCard(
                                student = student,
                                status = currentStatus,
                                onStatusSelected = { s -> viewModel.setStatus(student.studentId, s) }
                            )
                        }

                        // 4. Absent parent notification switch
                        item {
                            ClayCard(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Notify parents of absent students",
                                            style = MyTuitionTypography.BodyMedium.copy(fontWeight = FontWeight.SemiBold)
                                        )
                                        Text(
                                            text = "Sends instant WhatsApp/SMS alert to parents",
                                            style = MyTuitionTypography.BodySmall.copy(color = MyTuitionColors.TextTertiary, fontSize = 12.sp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Switch(
                                        checked = uiState.notifyParents,
                                        onCheckedChange = { viewModel.toggleNotifyParents(it) },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = Color.White,
                                            checkedTrackColor = MyTuitionColors.PrimaryPurple
                                        )
                                    )
                                }
                            }
                        }

                        // Bottom spacer
                        item {
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }

                    // 5. Fixed Save Bar at Bottom
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.White,
                        shadowElevation = 12.dp
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)) {
                            PillButton(
                                text = if (uiState.isSaving) "Saving..." else if (uiState.allMarked) "Save Attendance (${uiState.presentCount} Present, ${uiState.absentCount} Absent) ✓" else "Mark all students to save (${uiState.students.count { uiState.attendanceMap[it.studentId] != AttendanceStatus.UNMARKED }}/${uiState.students.size})",
                                onClick = {
                                    viewModel.saveAttendance {
                                        Toast.makeText(context, "Attendance saved successfully! ✓", Toast.LENGTH_SHORT).show()
                                        onBackClick()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = uiState.allMarked && !uiState.isSaving,
                                variant = if (uiState.allMarked) PillButtonVariant.Primary else PillButtonVariant.Secondary
                            )
                        }
                    }
                }
            }
        }
    }
}
}

@Composable
fun StudentAttendanceCard(
    student: StudentRosterItem,
    status: AttendanceStatus,
    onStatusSelected: (AttendanceStatus) -> Unit
) {
    ClayCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(14.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Student Avatar + Info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MyTuitionColors.PrimaryPurple.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = student.name.take(1),
                        style = MyTuitionTypography.TitleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MyTuitionColors.PrimaryPurple
                        )
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = student.name,
                        style = MyTuitionTypography.BodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MyTuitionColors.TextPrimary
                        )
                    )
                    Text(
                        text = "Roll No. ${student.rollNumber} • ${student.classGrade}-${student.section}",
                        style = MyTuitionTypography.BodySmall.copy(
                            color = MyTuitionColors.TextTertiary,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            // 3-Way Segmented Toggle (P / A / L)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                AttendanceSegmentPill(
                    label = "P",
                    isSelected = status == AttendanceStatus.PRESENT,
                    color = Color(0xFF2E7D32),
                    onClick = { onStatusSelected(AttendanceStatus.PRESENT) }
                )
                AttendanceSegmentPill(
                    label = "A",
                    isSelected = status == AttendanceStatus.ABSENT,
                    color = Color(0xFFD32F2F),
                    onClick = { onStatusSelected(AttendanceStatus.ABSENT) }
                )
                AttendanceSegmentPill(
                    label = "L",
                    isSelected = status == AttendanceStatus.LATE,
                    color = Color(0xFFF57C00),
                    onClick = { onStatusSelected(AttendanceStatus.LATE) }
                )
            }
        }
    }
}

@Composable
fun AttendanceSegmentPill(
    label: String,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    val scaleAnim = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()

    val bg by animateColorAsState(
        targetValue = if (isSelected) color else color.copy(alpha = 0.08f),
        label = "segmentBg"
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else color,
        label = "segmentTextColor"
    )

    Box(
        modifier = Modifier
            .size(36.dp)
            .graphicsLayer {
                scaleX = scaleAnim.value
                scaleY = scaleAnim.value
            }
            .clip(CircleShape)
            .background(bg)
            .border(1.dp, color.copy(alpha = if (isSelected) 1f else 0.25f), CircleShape)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        scope.launch { scaleAnim.animateTo(0.88f, spring(dampingRatio = 0.55f, stiffness = 400f)) }
                        tryAwaitRelease()
                        scope.launch { scaleAnim.animateTo(1f, spring(dampingRatio = 0.55f, stiffness = 400f)) }
                    },
                    onTap = { onClick() }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MyTuitionTypography.LabelLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                color = textColor,
                fontSize = 14.sp
            )
        )
    }
}
