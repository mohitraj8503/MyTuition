package com.example.mytuition.feature.calendar

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mytuition.core.designsystem.ClayZeroState
import com.example.mytuition.core.designsystem.MyTuitionAnimations
import com.example.mytuition.core.designsystem.MyTuitionColors
import com.example.mytuition.core.designsystem.MyTuitionShapes
import com.example.mytuition.core.designsystem.MyTuitionSpacing
import com.example.mytuition.core.designsystem.MyTuitionTypography
import com.example.mytuition.core.designsystem.PastelBackground
import com.example.mytuition.core.designsystem.components.ClayCard
import com.example.mytuition.core.designsystem.components.DateChip
import com.example.mytuition.core.designsystem.components.FilterChipRow
import com.example.mytuition.core.designsystem.darken

data class CalendarDay(val dayName: String, val dayNumber: Int)

data class CalendarEvent(
    val id: String,
    val title: String,
    val teacher: String,
    val time: String,
    val location: String,
    val dayNumber: Int,
    val type: String, // "Class", "Exam"
    val subjectColor: Color
)

enum class CalendarViewMode {
    WEEK,
    MONTH
}

@Composable
fun CalendarScreen() {
    var viewMode by remember { mutableStateOf(CalendarViewMode.MONTH) }
    var selectedDayNumber by remember { mutableIntStateOf(12) }
    var selectedFilterIndex by remember { mutableIntStateOf(0) }
    var currentMonthIndex by remember { mutableIntStateOf(0) }
    val months = listOf("November 2024", "December 2024")

    val days = remember {
        listOf(
            CalendarDay("Mon", 8),
            CalendarDay("Tue", 9),
            CalendarDay("Wed", 10),
            CalendarDay("Thu", 11),
            CalendarDay("Fri", 12),
            CalendarDay("Sat", 13),
            CalendarDay("Sun", 14)
        )
    }

    val events = remember {
        listOf(
            CalendarEvent(
                id = "e1",
                title = "Physics — Electromagnetic Induction",
                teacher = "Dr. Aalvina Fatehi",
                time = "10:00 AM - 11:30 AM",
                location = "Room 302",
                dayNumber = 12,
                type = "Class",
                subjectColor = MyTuitionColors.SubjectPhysics
            ),
            CalendarEvent(
                id = "e2",
                title = "Mathematics Chapter Test",
                teacher = "Prof. R. Sharma",
                time = "01:00 PM - 02:30 PM",
                location = "Exam Hall A",
                dayNumber = 12,
                type = "Exam",
                subjectColor = MyTuitionColors.SubjectMath
            ),
            CalendarEvent(
                id = "e3",
                title = "Chemistry Doubt Session",
                teacher = "Dr. S. K. Verma",
                time = "04:30 PM - 05:30 PM",
                location = "Google Meet",
                dayNumber = 12,
                type = "Class",
                subjectColor = MyTuitionColors.SubjectChemistry
            ),
            CalendarEvent(
                id = "e4",
                title = "Biology Lab Experiment",
                teacher = "Dr. Priya Patel",
                time = "11:00 AM - 12:30 PM",
                location = "Bio Lab 1",
                dayNumber = 13,
                type = "Class",
                subjectColor = MyTuitionColors.SubjectBio
            ),
            CalendarEvent(
                id = "e5",
                title = "History — World War II",
                teacher = "Prof. A. Mukherjee",
                time = "02:00 PM - 03:30 PM",
                location = "Room 105",
                dayNumber = 14,
                type = "Class",
                subjectColor = MyTuitionColors.SubjectHistory
            ),
            CalendarEvent(
                id = "e6",
                title = "Creative Sketching & Design",
                teacher = "Dr. Aalvina Fatehi",
                time = "05:00 PM - 06:30 PM",
                location = "Room 4B",
                dayNumber = 17,
                type = "Class",
                subjectColor = Color(0xFF8E24AA)
            ),
            CalendarEvent(
                id = "e7",
                title = "English Grammar & Writing",
                teacher = "Mrs. Sarah Jenkins",
                time = "03:00 PM - 04:30 PM",
                location = "Room 201",
                dayNumber = 20,
                type = "Class",
                subjectColor = Color(0xFF2E7D32)
            ),
            CalendarEvent(
                id = "e8",
                title = "Science Monthly Assessment",
                teacher = "Dr. Aalvina Fatehi",
                time = "10:00 AM - 12:00 PM",
                location = "Hall B",
                dayNumber = 25,
                type = "Exam",
                subjectColor = MyTuitionColors.SubjectPhysics
            )
        )
    }

    val eventDays = remember(events) { events.map { it.dayNumber }.toSet() }

    val filterOptions = listOf("All Sessions", "Classes Only", "Exams Only")

    val filteredEvents = events.filter { event ->
        val matchesDay = event.dayNumber == selectedDayNumber
        val matchesType = when (selectedFilterIndex) {
            1 -> event.type == "Class"
            2 -> event.type == "Exam"
            else -> true
        }
        matchesDay && matchesType
    }

    PastelBackground(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MyTuitionSpacing.lg, vertical = MyTuitionSpacing.md),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Calendar",
                        style = MyTuitionTypography.HeadlineLarge.copy(
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            color = MyTuitionColors.TextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = months[currentMonthIndex],
                        style = MyTuitionTypography.BodyMedium.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MyTuitionColors.PrimaryPurple
                        )
                    )
                }

                // Month switcher arrows
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .shadow(3.dp, CircleShape, spotColor = Color(0x18000000))
                            .clip(CircleShape)
                            .background(MyTuitionColors.CardWhite)
                            .border(1.5.dp, MyTuitionColors.CardWhite.darken(0.08f), CircleShape)
                            .clickable {
                                if (currentMonthIndex > 0) currentMonthIndex--
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                            contentDescription = "Previous Month",
                            tint = MyTuitionColors.TextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .shadow(3.dp, CircleShape, spotColor = Color(0x18000000))
                            .clip(CircleShape)
                            .background(MyTuitionColors.CardWhite)
                            .border(1.5.dp, MyTuitionColors.CardWhite.darken(0.08f), CircleShape)
                            .clickable {
                                if (currentMonthIndex < months.size - 1) currentMonthIndex++
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                            contentDescription = "Next Month",
                            tint = MyTuitionColors.TextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // 2-Way View Mode Selector: "Month View" & "Week View"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MyTuitionSpacing.lg, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Month View Tab
                val isMonth = viewMode == CalendarViewMode.MONTH
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .shadow(if (isMonth) 3.dp else 1.dp, RoundedCornerShape(20.dp), spotColor = Color(0x15000000))
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isMonth) MyTuitionColors.PrimaryPurple else MyTuitionColors.CardWhite)
                        .border(
                            1.5.dp,
                            if (isMonth) MyTuitionColors.PrimaryPurple.darken(0.1f) else MyTuitionColors.CardWhite.darken(0.08f),
                            RoundedCornerShape(20.dp)
                        )
                        .clickable { viewMode = CalendarViewMode.MONTH },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "📅 Month Grid",
                        style = MyTuitionTypography.LabelMedium.copy(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isMonth) Color.White else MyTuitionColors.TextPrimary
                        )
                    )
                }

                // Week View Tab
                val isWeek = viewMode == CalendarViewMode.WEEK
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .shadow(if (isWeek) 3.dp else 1.dp, RoundedCornerShape(20.dp), spotColor = Color(0x15000000))
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isWeek) MyTuitionColors.PrimaryPurple else MyTuitionColors.CardWhite)
                        .border(
                            1.5.dp,
                            if (isWeek) MyTuitionColors.PrimaryPurple.darken(0.1f) else MyTuitionColors.CardWhite.darken(0.08f),
                            RoundedCornerShape(20.dp)
                        )
                        .clickable { viewMode = CalendarViewMode.WEEK },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🗓️ Week View",
                        style = MyTuitionTypography.LabelMedium.copy(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isWeek) Color.White else MyTuitionColors.TextPrimary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Calendar Display according to 2-Way mode
            if (viewMode == CalendarViewMode.MONTH) {
                // Real Monthly Calendar Grid (Like a real physical/system calendar)
                FullMonthCalendarGrid(
                    selectedDayNumber = selectedDayNumber,
                    eventDays = eventDays,
                    onDayClick = { selectedDayNumber = it },
                    modifier = Modifier.padding(horizontal = MyTuitionSpacing.lg)
                )
            } else {
                // Horizontal scrolling Week strip view
                LazyRow(
                    contentPadding = PaddingValues(horizontal = MyTuitionSpacing.lg),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(days) { day ->
                        DateChip(
                            dayAbbreviation = day.dayName,
                            dateNumber = day.dayNumber.toString(),
                            isActive = day.dayNumber == selectedDayNumber,
                            onClick = { selectedDayNumber = day.dayNumber }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Filter chips
            FilterChipRow(
                filters = filterOptions,
                activeIndex = selectedFilterIndex,
                onFilterSelect = { selectedFilterIndex = it },
                modifier = Modifier.padding(horizontal = MyTuitionSpacing.lg)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Day label
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MyTuitionSpacing.lg),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sessions for Nov $selectedDayNumber",
                    style = MyTuitionTypography.TitleMedium.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MyTuitionColors.TextPrimary
                    )
                )
                Text(
                    text = "${filteredEvents.size} scheduled",
                    style = MyTuitionTypography.LabelSmall.copy(
                        fontSize = 12.sp,
                        color = MyTuitionColors.TextSecondary
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Schedule timeline list
            if (filteredEvents.isEmpty()) {
                ClayZeroState(
                    title = "No Sessions on Nov $selectedDayNumber 🗓️",
                    subtitle = "Tap any date with a dot to view scheduled classes and tests.",
                    modifier = Modifier.padding(vertical = 24.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = MyTuitionSpacing.lg,
                        end = MyTuitionSpacing.lg,
                        bottom = 110.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredEvents, key = { it.id }) { event ->
                        CalendarScheduleCard(event = event)
                    }
                }
            }
        }
    }
}

/**
 * Real Monthly Calendar Grid
 * Shows standard Sun, Mon, Tue, Wed, Thu, Fri, Sat columns
 * Days 1-30 formatted into 7-column rows with active day highlight & event indicators
 */
@Composable
private fun FullMonthCalendarGrid(
    selectedDayNumber: Int,
    eventDays: Set<Int>,
    onDayClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val dayOfWeekLabels = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    // November 2024 starts on Friday (5 blank cells before 1st)
    // 30 days total
    val startDayOffset = 5 // Sun=0, Mon=1, Tue=2, Wed=3, Thu=4, Fri=5
    val daysInMonth = 30

    ClayCard(
        modifier = modifier.fillMaxWidth(),
        cornerRadius = 24.dp,
        elevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Weekday Headers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                dayOfWeekLabels.forEach { label ->
                    Text(
                        text = label,
                        modifier = Modifier.weight(1f),
                        style = MyTuitionTypography.LabelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (label == "Sun") MyTuitionColors.StatusRed else MyTuitionColors.TextSecondary
                        ),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Calendar day cells in rows
            val totalCells = startDayOffset + daysInMonth
            val rowCount = (totalCells + 6) / 7

            for (rowIndex in 0 until rowCount) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    for (colIndex in 0 until 7) {
                        val cellIndex = rowIndex * 7 + colIndex
                        val dayNum = cellIndex - startDayOffset + 1

                        if (dayNum in 1..daysInMonth) {
                            val isSelected = dayNum == selectedDayNumber
                            val hasEvents = eventDays.contains(dayNum)

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(2.dp)
                                    .then(
                                        if (isSelected) {
                                            Modifier
                                                .shadow(3.dp, CircleShape, spotColor = MyTuitionColors.PrimaryPurple.copy(alpha = 0.35f))
                                                .clip(CircleShape)
                                                .background(MyTuitionColors.PrimaryPurple)
                                        } else if (hasEvents) {
                                            Modifier
                                                .clip(CircleShape)
                                                .background(MyTuitionColors.PrimaryPurpleLight.copy(alpha = 0.4f))
                                        } else {
                                            Modifier.clip(CircleShape)
                                        }
                                    )
                                    .clickable { onDayClick(dayNum) },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = dayNum.toString(),
                                        style = MyTuitionTypography.BodyMedium.copy(
                                            fontSize = 13.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) Color.White else MyTuitionColors.TextPrimary
                                        )
                                    )
                                    if (hasEvents) {
                                        Box(
                                            modifier = Modifier
                                                .size(4.dp)
                                                .clip(CircleShape)
                                                .background(if (isSelected) Color.White else MyTuitionColors.PrimaryPurple)
                                        )
                                    }
                                }
                            }
                        } else {
                            // Blank cell for offset
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarScheduleCard(
    event: CalendarEvent,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = MyTuitionAnimations.claySpring,
        label = "calCardScale"
    )

    val cardShape = RoundedCornerShape(28.dp)

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .fillMaxWidth()
            .shadow(
                elevation = if (isPressed) 3.dp else 8.dp,
                shape = cardShape,
                ambientColor = Color(0x221A1A1A),
                spotColor = Color(0x181A1A1A)
            )
            .clip(cardShape)
            .background(MyTuitionColors.CardWhite)
            .border(2.dp, MyTuitionColors.CardWhite.darken(0.08f), cardShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {}
            )
    ) {
        // Inner bottom shadow
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.04f))
                    )
                )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left color stripe (5dp wide, rounded, colored by subject)
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .height(64.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(event.subjectColor)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Time
                Text(
                    text = event.time,
                    style = MyTuitionTypography.LabelMedium.copy(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MyTuitionColors.PrimaryPurple
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Title
                Text(
                    text = event.title,
                    style = MyTuitionTypography.TitleMedium.copy(
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MyTuitionColors.TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Teacher
                Text(
                    text = event.teacher,
                    style = MyTuitionTypography.BodyMedium.copy(
                        fontSize = 14.sp,
                        color = MyTuitionColors.TextSecondary
                    )
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Room / Link badge
            Box(
                modifier = Modifier
                    .shadow(2.dp, MyTuitionShapes.PillShape, spotColor = Color(0x12000000))
                    .clip(MyTuitionShapes.PillShape)
                    .background(MyTuitionColors.PrimaryPurpleLight)
                    .border(1.5.dp, MyTuitionColors.PrimaryPurpleLight.darken(0.08f), MyTuitionShapes.PillShape)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (event.location.contains("Meet", ignoreCase = true)) {
                        Icon(
                            imageVector = Icons.Rounded.Videocam,
                            contentDescription = null,
                            tint = MyTuitionColors.PrimaryPurple,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                    }
                    Text(
                        text = event.location,
                        style = MyTuitionTypography.LabelSmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MyTuitionColors.PrimaryPurple
                        )
                    )
                }
            }
        }
    }
}
