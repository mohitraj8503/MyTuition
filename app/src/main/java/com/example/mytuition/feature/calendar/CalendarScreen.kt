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
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.mytuition.core.designsystem.components.OfflineBanner
import com.example.mytuition.core.designsystem.darken
import com.example.mytuition.core.di.AppContainer
import com.example.mytuition.core.domain.model.HomeData
import com.example.mytuition.core.domain.model.TimelineSessionItem
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class CalendarDay(val dayName: String, val dayNumber: Int, val fullDate: String, val hasClasses: Boolean)

enum class CalendarViewMode {
    WEEK,
    MONTH
}

@Composable
fun CalendarScreen() {
    val homeRepo = AppContainer.homeRepository
    val coroutineScope = rememberCoroutineScope()

    var viewMode by remember { mutableStateOf(CalendarViewMode.MONTH) }
    var selectedFilterIndex by remember { mutableIntStateOf(0) }

    // Dynamic month calendar state
    val calendarInstance = remember { Calendar.getInstance() }
    var displayedCalendar by remember { mutableStateOf(Calendar.getInstance().apply { set(Calendar.DAY_OF_MONTH, 1) }) }

    val todayCalendar = remember { Calendar.getInstance() }
    var selectedDateStr by remember {
        mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()))
    }

    var homeData by remember { mutableStateOf<HomeData?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Month display string e.g. "September 2026"
    val monthTitle = remember(displayedCalendar) {
        SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(displayedCalendar.time)
    }

    // Days in current selected month
    val daysInMonth = remember(displayedCalendar) {
        displayedCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    // Offset for start of month (Sunday=0, Monday=1, ..., Saturday=6)
    val startDayOffset = remember(displayedCalendar) {
        val c = displayedCalendar.clone() as Calendar
        c.set(Calendar.DAY_OF_MONTH, 1)
        c.get(Calendar.DAY_OF_WEEK) - 1
    }

    // Fetch data whenever selectedDateStr changes
    fun loadSchedule(targetDate: String) {
        coroutineScope.launch {
            isLoading = true
            val result = homeRepo.getHomeData(targetDate)
            homeData = result.getOrNull()
            isLoading = false
        }
    }

    LaunchedEffect(selectedDateStr) {
        loadSchedule(selectedDateStr)
    }

    // Extract days with events from weekDates or today's session
    val weekItems = homeData?.weekDates ?: emptyList()
    val eventDays = remember(weekItems, displayedCalendar) {
        val set = mutableSetOf<Int>()
        val calMonth = displayedCalendar.get(Calendar.MONTH)
        val calYear = displayedCalendar.get(Calendar.YEAR)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        weekItems.filter { it.hasClasses }.forEach { item ->
            try {
                val d = sdf.parse(item.date)
                if (d != null) {
                    val c = Calendar.getInstance().apply { time = d }
                    if (c.get(Calendar.MONTH) == calMonth && c.get(Calendar.YEAR) == calYear) {
                        set.add(c.get(Calendar.DAY_OF_MONTH))
                    }
                }
            } catch (_: Exception) {}
        }

        // Also add the selected date if timeline has sessions
        if ((homeData?.timeline?.isNotEmpty()) == true) {
            try {
                val d = sdf.parse(selectedDateStr)
                if (d != null) {
                    val c = Calendar.getInstance().apply { time = d }
                    if (c.get(Calendar.MONTH) == calMonth && c.get(Calendar.YEAR) == calYear) {
                        set.add(c.get(Calendar.DAY_OF_MONTH))
                    }
                }
            } catch (_: Exception) {}
        }
        set
    }

    // Selected day number in current month
    val selectedDayNumber = remember(selectedDateStr, displayedCalendar) {
        try {
            val d = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(selectedDateStr)
            if (d != null) {
                val c = Calendar.getInstance().apply { time = d }
                if (c.get(Calendar.MONTH) == displayedCalendar.get(Calendar.MONTH) &&
                    c.get(Calendar.YEAR) == displayedCalendar.get(Calendar.YEAR)) {
                    c.get(Calendar.DAY_OF_MONTH)
                } else {
                    -1
                }
            } else -1
        } catch (_: Exception) { -1 }
    }

    // Dynamic 7-day week strip derived from selectedDateStr
    val weekDays = remember(weekItems, selectedDateStr) {
        if (weekItems.isNotEmpty()) {
            weekItems.map {
                CalendarDay(
                    dayName = it.dayAbbr,
                    dayNumber = it.dayNumber.toIntOrNull() ?: 1,
                    fullDate = it.date,
                    hasClasses = it.hasClasses
                )
            }
        } else {
            // Fallback week derived dynamically from current week
            val daysList = mutableListOf<CalendarDay>()
            val c = Calendar.getInstance()
            try {
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(selectedDateStr)?.let {
                    c.time = it
                }
            } catch (_: Exception) {}
            val curDow = c.get(Calendar.DAY_OF_WEEK)
            val diffToMon = if (curDow == Calendar.SUNDAY) -6 else Calendar.MONDAY - curDow
            c.add(Calendar.DAY_OF_MONTH, diffToMon)
            val sdfFull = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val sdfDay = SimpleDateFormat("EEE", Locale.getDefault())
            for (i in 0 until 7) {
                daysList.add(
                    CalendarDay(
                        dayName = sdfDay.format(c.time),
                        dayNumber = c.get(Calendar.DAY_OF_MONTH),
                        fullDate = sdfFull.format(c.time),
                        hasClasses = false
                    )
                )
                c.add(Calendar.DAY_OF_MONTH, 1)
            }
            daysList
        }
    }

    // Sessions for the day
    val rawSessions = homeData?.timeline ?: emptyList()
    val filterOptions = listOf("All Sessions", "Classes Only", "Exams Only")

    val filteredSessions = rawSessions.filter { session ->
        val isExam = session.topic.contains("Test", ignoreCase = true) ||
                     session.topic.contains("Exam", ignoreCase = true) ||
                     session.subjectName.contains("Exam", ignoreCase = true)
        when (selectedFilterIndex) {
            1 -> !isExam
            2 -> isExam
            else -> true
        }
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
                        text = monthTitle,
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
                                val nextCal = displayedCalendar.clone() as Calendar
                                nextCal.add(Calendar.MONTH, -1)
                                displayedCalendar = nextCal
                                val targetDay = if (selectedDayNumber > 0) selectedDayNumber.coerceAtMost(nextCal.getActualMaximum(Calendar.DAY_OF_MONTH)) else 1
                                val dStr = String.format(Locale.getDefault(), "%04d-%02d-%02d", nextCal.get(Calendar.YEAR), nextCal.get(Calendar.MONTH) + 1, targetDay)
                                selectedDateStr = dStr
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
                                val nextCal = displayedCalendar.clone() as Calendar
                                nextCal.add(Calendar.MONTH, 1)
                                displayedCalendar = nextCal
                                val targetDay = if (selectedDayNumber > 0) selectedDayNumber.coerceAtMost(nextCal.getActualMaximum(Calendar.DAY_OF_MONTH)) else 1
                                val dStr = String.format(Locale.getDefault(), "%04d-%02d-%02d", nextCal.get(Calendar.YEAR), nextCal.get(Calendar.MONTH) + 1, targetDay)
                                selectedDateStr = dStr
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

            // Offline banner if cached data
            if (homeData?.isFromCache == true) {
                Box(modifier = Modifier.padding(horizontal = MyTuitionSpacing.lg)) {
                    OfflineBanner()
                }
            }

            // 2-Way View Mode Selector: "Month View" & "Week View"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MyTuitionSpacing.lg, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
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
                FullMonthCalendarGrid(
                    selectedDayNumber = selectedDayNumber,
                    daysInMonth = daysInMonth,
                    startDayOffset = startDayOffset,
                    eventDays = eventDays,
                    onDayClick = { dayNum ->
                        val dStr = String.format(Locale.getDefault(), "%04d-%02d-%02d", displayedCalendar.get(Calendar.YEAR), displayedCalendar.get(Calendar.MONTH) + 1, dayNum)
                        selectedDateStr = dStr
                    },
                    modifier = Modifier.padding(horizontal = MyTuitionSpacing.lg)
                )
            } else {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = MyTuitionSpacing.lg),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(weekDays) { day ->
                        val isSelected = day.fullDate == selectedDateStr
                        DateChip(
                            dayAbbreviation = day.dayName,
                            dateNumber = day.dayNumber.toString(),
                            isActive = isSelected,
                            onClick = { selectedDateStr = day.fullDate }
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
            val formattedDateLabel = remember(selectedDateStr) {
                try {
                    val d = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(selectedDateStr)
                    if (d != null) SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault()).format(d) else selectedDateStr
                } catch (_: Exception) { selectedDateStr }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MyTuitionSpacing.lg),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sessions for $formattedDateLabel",
                    style = MyTuitionTypography.TitleMedium.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MyTuitionColors.TextPrimary
                    )
                )
                Text(
                    text = "${filteredSessions.size} scheduled",
                    style = MyTuitionTypography.LabelSmall.copy(
                        fontSize = 12.sp,
                        color = MyTuitionColors.TextSecondary
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Schedule timeline list
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MyTuitionColors.PrimaryPurple,
                        modifier = Modifier.size(36.dp)
                    )
                }
            } else if (filteredSessions.isEmpty()) {
                ClayZeroState(
                    title = "No Sessions on $formattedDateLabel 🗓️",
                    subtitle = "Tap any date with an indicator to view scheduled classes and tests.",
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
                    items(filteredSessions, key = { it.sessionId }) { session ->
                        CalendarScheduleSessionCard(session = session)
                    }
                }
            }
        }
    }
}

/**
 * Dynamic Monthly Calendar Grid with Real Date Math
 */
@Composable
private fun FullMonthCalendarGrid(
    selectedDayNumber: Int,
    daysInMonth: Int,
    startDayOffset: Int,
    eventDays: Set<Int>,
    onDayClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val dayOfWeekLabels = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

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
private fun CalendarScheduleSessionCard(
    session: TimelineSessionItem,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = MyTuitionAnimations.claySpring,
        label = "calCardScale"
    )

    val cardShape = RoundedCornerShape(24.dp)
    val stripeColor = try {
        Color(android.graphics.Color.parseColor(session.subjectIconColorHex))
    } catch (_: Exception) {
        MyTuitionColors.PrimaryPurple
    }

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .fillMaxWidth()
            .shadow(
                elevation = if (isPressed) 3.dp else 6.dp,
                shape = cardShape,
                ambientColor = Color(0x181A1A1A),
                spotColor = Color(0x121A1A1A)
            )
            .clip(cardShape)
            .background(MyTuitionColors.CardWhite)
            .border(2.dp, MyTuitionColors.CardWhite.darken(0.08f), cardShape)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left color stripe
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .height(60.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(stripeColor)
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                val timeLabel = if (session.startTimeDisplay.isNotBlank() && session.endTimeDisplay.isNotBlank()) {
                    "${session.startTimeDisplay} - ${session.endTimeDisplay}"
                } else if (session.time.isNotBlank()) {
                    session.time
                } else {
                    "Scheduled Session"
                }

                Text(
                    text = timeLabel,
                    style = MyTuitionTypography.LabelMedium.copy(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MyTuitionColors.PrimaryPurple
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = session.subjectName.ifBlank { "Class Session" },
                    style = MyTuitionTypography.TitleMedium.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MyTuitionColors.TextPrimary
                    )
                )

                if (session.topic.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = session.topic,
                        style = MyTuitionTypography.BodySmall.copy(
                            fontSize = 13.sp,
                            color = MyTuitionColors.TextSecondary
                        )
                    )
                }

                if (session.teacherName.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Teacher: ${session.teacherName}",
                        style = MyTuitionTypography.BodySmall.copy(
                            fontSize = 12.sp,
                            color = MyTuitionColors.TextTertiary
                        )
                    )
                }
            }

            if (session.roomName.isNotBlank()) {
                Spacer(modifier = Modifier.width(10.dp))
                Box(
                    modifier = Modifier
                        .shadow(2.dp, MyTuitionShapes.PillShape, spotColor = Color(0x12000000))
                        .clip(MyTuitionShapes.PillShape)
                        .background(MyTuitionColors.PrimaryPurpleLight)
                        .border(1.5.dp, MyTuitionColors.PrimaryPurpleLight.darken(0.08f), MyTuitionShapes.PillShape)
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = session.roomName,
                        style = MyTuitionTypography.LabelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MyTuitionColors.PrimaryPurple
                        )
                    )
                }
            }
        }
    }
}
