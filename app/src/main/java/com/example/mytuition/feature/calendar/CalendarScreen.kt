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

@Composable
fun CalendarScreen() {
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
            )
        )
    }

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
                            fontSize = 32.sp,
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
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .shadow(4.dp, CircleShape, spotColor = Color(0x18000000))
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
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .shadow(4.dp, CircleShape, spotColor = Color(0x18000000))
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
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // Horizontal scrolling DateChip row (10dp gap)
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

            Spacer(modifier = Modifier.height(16.dp))

            // Filter chips
            FilterChipRow(
                filters = filterOptions,
                activeIndex = selectedFilterIndex,
                onFilterSelect = { selectedFilterIndex = it },
                modifier = Modifier.padding(horizontal = MyTuitionSpacing.lg)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Schedule timeline list
            if (filteredEvents.isEmpty()) {
                ClayZeroState(
                    title = "No Sessions Today 🗓️",
                    subtitle = "Enjoy your free time or explore upcoming classes!",
                    modifier = Modifier.padding(vertical = 40.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = MyTuitionSpacing.lg,
                        end = MyTuitionSpacing.lg,
                        bottom = 110.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(filteredEvents, key = { it.id }) { event ->
                        CalendarScheduleCard(event = event)
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
            .scale(scale)
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
