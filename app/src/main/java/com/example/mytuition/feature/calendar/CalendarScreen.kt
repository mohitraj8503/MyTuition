package com.example.mytuition.feature.calendar

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mytuition.core.designsystem.MyTuitionColors
import com.example.mytuition.core.designsystem.MyTuitionSpacing
import com.example.mytuition.core.designsystem.MyTuitionTypography

data class CalendarDay(val dayName: String, val dayNumber: Int)

data class CalendarEvent(
    val id: String,
    val title: String,
    val subtitle: String,
    val time: String,
    val dayNumber: Int,
    val color: Color,
    val textColor: Color = MyTuitionColors.DeepNavyText
)

@Composable
fun CalendarScreen() {
    var selectedDayNumber by remember { mutableIntStateOf(12) }

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
                title = "Mathematics Chapter Test",
                subtitle = "Quadratic Equations & Polynomials · Room 3",
                time = "10:00 AM – 11:30 AM",
                dayNumber = 12,
                color = MyTuitionColors.PremiumLime,
                textColor = MyTuitionColors.DeepNavyText
            ),
            CalendarEvent(
                id = "e2",
                title = "Physics Doubt Clearing Session",
                subtitle = "Current Electricity & Ohm's Law",
                time = "05:00 PM – 06:00 PM",
                dayNumber = 12,
                color = MyTuitionColors.PremiumPurple,
                textColor = Color.White
            ),
            CalendarEvent(
                id = "e3",
                title = "Science Exhibition Prep",
                subtitle = "Working models and lab reports submission",
                time = "09:00 AM – 12:00 PM",
                dayNumber = 13,
                color = MyTuitionColors.PremiumBlue,
                textColor = MyTuitionColors.DeepNavyText
            ),
            CalendarEvent(
                id = "e4",
                title = "Parent-Teacher Meeting",
                subtitle = "Monthly academic progress review",
                time = "04:00 PM – 05:30 PM",
                dayNumber = 14,
                color = MyTuitionColors.PremiumCoral,
                textColor = Color.White
            )
        )
    }

    val filteredEvents = events.filter { it.dayNumber == selectedDayNumber }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MyTuitionColors.WarmIvory)
    ) {
        Column(modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 36.dp, bottom = 12.dp)) {
            Text(
                text = "September",
                style = MyTuitionTypography.Display,
                color = MyTuitionColors.DeepNavyText
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Tap a date to inspect your schedule.",
                style = MyTuitionTypography.Body,
                color = MyTuitionColors.DeepNavyText.copy(alpha = 0.5f)
            )
        }

        // Horizontal week selector
        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(days) { day ->
                val isSelected = day.dayNumber == selectedDayNumber
                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.05f else 1f,
                    animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f)
                )
                val bgColor by animateColorAsState(
                    targetValue = if (isSelected) MyTuitionColors.DeepNavyText else Color.White
                )
                val contentColor by animateColorAsState(
                    targetValue = if (isSelected) MyTuitionColors.PremiumLime else MyTuitionColors.DeepNavyText
                )

                Box(
                    modifier = Modifier
                        .scale(scale)
                        .width(52.dp)
                        .height(76.dp)
                        .shadow(
                            elevation = if (isSelected) 10.dp else 2.dp,
                            shape = RoundedCornerShape(18.dp),
                            spotColor = Color.Black.copy(alpha = if (isSelected) 0.2f else 0.05f)
                        )
                        .background(bgColor, RoundedCornerShape(18.dp))
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { selectedDayNumber = day.dayNumber }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = day.dayName,
                            style = MyTuitionTypography.Caption,
                            color = contentColor.copy(alpha = if (isSelected) 0.8f else 0.5f)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${day.dayNumber}",
                            style = MyTuitionTypography.LargeTitle.copy(fontSize = 18.sp),
                            color = contentColor
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = MyTuitionSpacing.BottomNavPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            if (filteredEvents.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp)
                            .background(Color.White.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                            .padding(28.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "No Scheduled Sessions",
                                style = MyTuitionTypography.SectionTitle,
                                color = MyTuitionColors.DeepNavyText
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Take time to review notes or work ahead!",
                                style = MyTuitionTypography.Body,
                                color = MyTuitionColors.DeepNavyText.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            } else {
                items(filteredEvents, key = { it.id }) { event ->
                    CalendarEventCard(event = event)
                }
            }
        }
    }
}

@Composable
fun CalendarEventCard(event: CalendarEvent) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                8.dp,
                RoundedCornerShape(24.dp),
                spotColor = event.color.copy(alpha = 0.3f),
                ambientColor = event.color.copy(alpha = 0.1f)
            )
            .background(event.color, RoundedCornerShape(24.dp))
            .padding(22.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sep ${event.dayNumber}",
                    style = MyTuitionTypography.Caption,
                    color = event.textColor.copy(alpha = 0.7f)
                )
                Text(
                    text = event.time,
                    style = MyTuitionTypography.Caption,
                    color = event.textColor.copy(alpha = 0.85f)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = event.title,
                style = MyTuitionTypography.LargeTitle.copy(fontSize = 18.sp),
                color = event.textColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = event.subtitle,
                style = MyTuitionTypography.Body.copy(fontSize = 13.sp),
                color = event.textColor.copy(alpha = 0.8f)
            )
        }
    }
}
