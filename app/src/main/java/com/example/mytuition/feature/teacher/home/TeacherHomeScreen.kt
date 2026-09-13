package com.example.mytuition.feature.teacher.home

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mytuition.core.designsystem.*
import com.example.mytuition.core.designsystem.components.*
import com.example.mytuition.core.di.AppContainer
import com.example.mytuition.core.domain.model.AnnouncementItem
import com.example.mytuition.core.domain.model.SessionStatus
import com.example.mytuition.core.domain.model.TeacherSessionItem
import com.example.mytuition.core.domain.model.WeekDayItem
import kotlinx.coroutines.launch

@Composable
fun TeacherHomeScreen(
    onNavigateToAttendance: (String) -> Unit,
    onNavigateToHomeworkReview: () -> Unit,
    onNavigateToAnnouncements: () -> Unit,
    onNavigateToStudents: () -> Unit,
    onLogout: () -> Unit = {},
    onNavigateToHomework: () -> Unit = onNavigateToHomeworkReview,
    onNavigateToBatchDetail: (String) -> Unit = onNavigateToAttendance,
    onNavigateToCalendar: () -> Unit = {},
    onNavigateToEarnings: () -> Unit = {},
    viewModel: TeacherHomeViewModel = viewModel(
        factory = TeacherHomeViewModel.provideFactory(AppContainer.teacherRepository)
    )
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    PastelBackground(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            when (val uiState = state) {
                TeacherHomeUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MyTuitionColors.PrimaryPurple)
                    }
                }
                is TeacherHomeUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        ClayCard(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Oops! Something went wrong", style = MyTuitionTypography.TitleSmall)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(uiState.message, style = MyTuitionTypography.BodyMedium, color = MyTuitionColors.TextSecondary)
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(onClick = { viewModel.refresh() }) {
                                    Text("Retry")
                                }
                            }
                        }
                    }
                }
                is TeacherHomeUiState.Success -> {
                    val data = uiState.data
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = MyTuitionSpacing.lg,
                            end = MyTuitionSpacing.lg,
                            top = 16.dp,
                            bottom = 110.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // 1. Header (Logo + Greeting)
                        item {
                            TeacherHeader(teacherName = data.teacherName, institute = data.instituteName)
                        }

                        // 2. Next Teaching Class (Hero NextClassCard)
                        item {
                            if (data.nextSession != null) {
                                TeacherNextClassHero(
                                    session = data.nextSession,
                                    onTakeAttendance = { onNavigateToAttendance(data.nextSession.sessionId) }
                                )
                            }
                        }

                        // 3. Teacher Shortcuts Row (Buttons with live counters)
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                TeacherShortcutRow(
                                    pendingAttendanceCount = data.pendingAttendanceCount,
                                    homeworkStats = data.latestHomeworkStats,
                                    announcementsCount = data.todayAnnouncementsCount,
                                    totalStudentsCount = data.totalStudentsCount,
                                    onAttendanceClick = {
                                        val nextId = data.nextSession?.sessionId ?: "session_tch_today_1"
                                        onNavigateToAttendance(nextId)
                                    },
                                    onHomeworkClick = onNavigateToHomeworkReview,
                                    onAnnounceClick = onNavigateToAnnouncements,
                                    onStudentsClick = onNavigateToStudents
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    TeacherShortcutItem(
                                        title = "Calendar",
                                        badge = "Schedule",
                                        icon = Icons.Rounded.CalendarMonth,
                                        color = MyTuitionColors.PrimaryPurple,
                                        onClick = onNavigateToCalendar,
                                        modifier = Modifier.weight(1f)
                                    )
                                    TeacherShortcutItem(
                                        title = "Earnings",
                                        badge = "Collections",
                                        icon = Icons.Rounded.CurrencyRupee,
                                        color = MyTuitionColors.SuccessGreen,
                                        onClick = onNavigateToEarnings,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }

                        // 4. Week Day Chips Row
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                SectionHeader(
                                    title = "Class Schedule",
                                    actionText = "Aug 2025 ⌵",
                                    onActionClick = { }
                                )
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    contentPadding = PaddingValues(horizontal = 2.dp)
                                ) {
                                    items(data.weekDates, key = { it.date }) { item ->
                                        DateChip(
                                            dayAbbreviation = item.dayAbbr,
                                            dateNumber = item.dayNumber,
                                            isSelected = item.date == uiState.selectedDate,
                                            onClick = { viewModel.selectDate(item.date) }
                                        )
                                    }
                                }
                            }
                        }

                        // 5. Today's Teaching Timeline
                        item {
                            SectionHeader(title = "Today's Timeline (${data.todaySessions.size})")
                        }

                        if (data.todaySessions.isEmpty()) {
                            item {
                                ClayZeroState(
                                    title = "No classes today!",
                                    subtitle = "Enjoy the well-deserved break. 🌟"
                                )
                            }
                        } else {
                            items(data.todaySessions, key = { it.sessionId }) { session ->
                                TeacherTimelineSessionCard(
                                    session = session,
                                    onTakeAttendance = { onNavigateToAttendance(session.sessionId) }
                                )
                            }
                        }

                        // 6. Recent Announcements Preview
                        if (data.recentAnnouncements.isNotEmpty()) {
                            item {
                                SectionHeader(
                                    title = "Notices & Broadcasts",
                                    actionText = "View All →",
                                    onActionClick = onNavigateToAnnouncements
                                )
                            }
                            items(data.recentAnnouncements.take(2), key = { it.id }) { ann ->
                                TeacherAnnouncementCard(announcement = ann)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TeacherHeader(teacherName: String, institute: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            MyTuitionLogo(size = 46.dp)
            Column {
                Text(
                    text = "Today's Classes",
                    style = MyTuitionTypography.TitleExtra.copy(
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MyTuitionColors.TextPrimary
                    )
                )
                Text(
                    text = "Keep teaching, keep inspiring! 🌟",
                    style = MyTuitionTypography.BodyMedium.copy(
                        fontSize = 14.sp,
                        color = MyTuitionColors.TextSecondary
                    )
                )
            }
        }
    }
}

@Composable
fun TeacherNextClassHero(
    session: TeacherSessionItem,
    onTakeAttendance: () -> Unit
) {
    ClayCard(
        modifier = Modifier.fillMaxWidth(),
        cardColor = Color(0xFFFAF7FF),
        borderColor = Color(0xFFEDE4FF)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Live status pill badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (session.status == SessionStatus.ONGOING) Color(0xFFFFEBEE) else Color(0xFFEDE7FF))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (session.status == SessionStatus.ONGOING) "🔴 LIVE NOW" else "NEXT UP • ${session.startTime}",
                        style = MyTuitionTypography.LabelSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = if (session.status == SessionStatus.ONGOING) Color(0xFFD32F2F) else MyTuitionColors.PrimaryPurple,
                            fontSize = 11.sp
                        )
                    )
                }

                // Room pill
                Text(
                    text = session.roomName,
                    style = MyTuitionTypography.LabelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MyTuitionColors.TextSecondary
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = session.subjectName,
                style = MyTuitionTypography.TitleSmall.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MyTuitionColors.TextPrimary
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${session.batchName} • ${session.studentCount} Students Enrolled",
                style = MyTuitionTypography.BodyMedium.copy(
                    fontSize = 14.sp,
                    color = MyTuitionColors.TextSecondary
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Time & Location details
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Schedule,
                    contentDescription = null,
                    tint = MyTuitionColors.PrimaryPurple,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = session.timeSlotDisplay,
                    style = MyTuitionTypography.LabelSmall.copy(
                        color = MyTuitionColors.TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    imageVector = Icons.Rounded.LocationOn,
                    contentDescription = null,
                    tint = MyTuitionColors.PrimaryPurple,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = session.floorName.ifEmpty { "Staircase B" },
                    style = MyTuitionTypography.LabelSmall.copy(
                        color = MyTuitionColors.TextSecondary
                    ),
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Primary action pill: TAKE ATTENDANCE
            PillButton(
                text = if (session.attendanceTaken) "View Attendance ✓" else "TAKE ATTENDANCE →",
                onClick = onTakeAttendance,
                modifier = Modifier.fillMaxWidth(),
                variant = if (session.attendanceTaken) PillButtonVariant.Secondary else PillButtonVariant.Primary
            )
        }
    }
}

@Composable
fun TeacherShortcutRow(
    pendingAttendanceCount: Int,
    homeworkStats: String,
    announcementsCount: Int,
    totalStudentsCount: Int,
    onAttendanceClick: () -> Unit,
    onHomeworkClick: () -> Unit,
    onAnnounceClick: () -> Unit,
    onStudentsClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        TeacherShortcutItem(
            title = "Attendance",
            badge = "$pendingAttendanceCount pending",
            icon = Icons.Rounded.CheckCircle,
            color = Color(0xFF4CAF50),
            onClick = onAttendanceClick,
            modifier = Modifier.weight(1f)
        )
        TeacherShortcutItem(
            title = "Homework",
            badge = homeworkStats,
            icon = Icons.Rounded.Assignment,
            color = Color(0xFFFF7043),
            onClick = onHomeworkClick,
            modifier = Modifier.weight(1f)
        )
        TeacherShortcutItem(
            title = "Announce",
            badge = "$announcementsCount today",
            icon = Icons.Rounded.Campaign,
            color = MyTuitionColors.PrimaryPurple,
            onClick = onAnnounceClick,
            modifier = Modifier.weight(1f)
        )
        TeacherShortcutItem(
            title = "Students",
            badge = "$totalStudentsCount total",
            icon = Icons.Rounded.Groups,
            color = Color(0xFF29B6F6),
            onClick = onStudentsClick,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun TeacherShortcutItem(
    title: String,
    badge: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scaleAnim = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()

    ClayCard(
        modifier = modifier
            .graphicsLayer {
                scaleX = scaleAnim.value
                scaleY = scaleAnim.value
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        scope.launch { scaleAnim.animateTo(0.95f, spring(dampingRatio = 0.55f, stiffness = 400f)) }
                        tryAwaitRelease()
                        scope.launch { scaleAnim.animateTo(1f, spring(dampingRatio = 0.55f, stiffness = 400f)) }
                    },
                    onTap = { onClick() }
                )
            }
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = title, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                style = MyTuitionTypography.LabelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = MyTuitionColors.TextPrimary
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = badge,
                style = MyTuitionTypography.LabelSmall.copy(
                    fontSize = 9.sp,
                    color = MyTuitionColors.TextTertiary
                ),
                maxLines = 1
            )
        }
    }
}

@Composable
fun TeacherTimelineSessionCard(
    session: TeacherSessionItem,
    onTakeAttendance: () -> Unit
) {
    ClayCard(
        modifier = Modifier.fillMaxWidth(),
        cardColor = Color.White
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left Time Column
            Column(
                modifier = Modifier.width(68.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = session.startTime,
                    style = MyTuitionTypography.TitleSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        color = MyTuitionColors.TextPrimary
                    )
                )
                Text(
                    text = session.endTime,
                    style = MyTuitionTypography.LabelSmall.copy(
                        fontSize = 11.sp,
                        color = MyTuitionColors.TextTertiary
                    )
                )
            }

            // Divider vertical
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(36.dp)
                    .background(Color(0xFFECEBF0))
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Middle info column
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = session.subjectName,
                    style = MyTuitionTypography.BodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MyTuitionColors.TextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${session.batchName} • ${session.roomName}",
                    style = MyTuitionTypography.LabelSmall.copy(
                        color = MyTuitionColors.TextSecondary,
                        fontSize = 12.sp
                    )
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Right Action: Take attendance pill button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (session.attendanceTaken) Color(0xFFE8F5E9) else MyTuitionColors.PrimaryPurple)
                    .clickable { onTakeAttendance() }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (session.attendanceTaken) "Done ✓" else "Mark 📋",
                    style = MyTuitionTypography.LabelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (session.attendanceTaken) Color(0xFF2E7D32) else Color.White,
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}

@Composable
fun TeacherAnnouncementCard(announcement: AnnouncementItem) {
    ClayCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = announcement.title,
                    style = MyTuitionTypography.TitleSmall.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = announcement.date,
                    style = MyTuitionTypography.LabelSmall.copy(color = MyTuitionColors.TextTertiary)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = announcement.message,
                style = MyTuitionTypography.BodySmall.copy(color = MyTuitionColors.TextSecondary),
                maxLines = 2
            )
        }
    }
}
