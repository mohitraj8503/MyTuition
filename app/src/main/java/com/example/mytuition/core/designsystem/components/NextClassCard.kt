package com.example.mytuition.core.designsystem.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Directions
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.NearMe
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.NotificationsNone
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mytuition.core.designsystem.MyTuitionAnimations
import com.example.mytuition.core.designsystem.MyTuitionColors
import com.example.mytuition.core.designsystem.MyTuitionShapes
import com.example.mytuition.core.designsystem.MyTuitionTypography

enum class NextClassStatus {
    UPCOMING,
    RESCHEDULED,
    CANCELLED
}

data class NextClassInfo(
    val id: String = "class_sketching",
    val subjectName: String = "Creative Sketching",
    val teacherName: String = "Dr. Aalvina Fatehi",
    val timeText: String = "Today • 5:00 PM - 6:30 PM",
    val countdownText: String = "Starts in 45 min",
    val room: String = "Room 4B",
    val floor: String = "2nd Floor, Arts Block",
    val directionsNote: String = "Opposite Physics Lab • Next to Staircase B",
    val status: NextClassStatus = NextClassStatus.UPCOMING,
    val statusNote: String = ""
)

/**
 * Clean, Spacious, Photo-Free Claymorphic "Next Class" Card for Offline Classes.
 * - Displays subject name, teacher, exact time, and room/venue prominently across full width.
 * - Same-day countdown text ("Starts in 45 min") without any online "Live" streaming badge.
 * - 15-min-before "Set Reminder" toggle & "Add to Calendar" icon with clear visual feedback.
 * - Dedicated full-width Venue card with "View Room" / "Get Directions" trigger.
 * - Completely uncluttered and beautifully legible.
 */
@Composable
fun NextClassCard(
    info: NextClassInfo,
    onClick: () -> Unit,
    onViewRoomClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val cardInteraction = remember { MutableInteractionSource() }
    val isCardPressed by cardInteraction.collectIsPressedAsState()

    val cardScale by animateFloatAsState(
        targetValue = if (isCardPressed) 0.98f else 1f,
        animationSpec = MyTuitionAnimations.claySpring,
        label = "nextCardScale"
    )

    var isReminderActive by remember { mutableStateOf(false) }
    var isCalendarAdded by remember { mutableStateOf(false) }

    val cardShape = RoundedCornerShape(32.dp)

    Box(
        modifier = modifier
            .scale(cardScale)
            .fillMaxWidth()
            .shadow(
                elevation = if (isCardPressed) 4.dp else 12.dp,
                shape = cardShape,
                ambientColor = Color(0x331A1A1A),
                spotColor = Color(0x221A1A1A)
            )
            .clip(cardShape)
            .background(MyTuitionColors.CardLavender)
            .border(
                width = 2.dp,
                color = Color(0xFFD9CFF5),
                shape = cardShape
            )
            .clickable(
                interactionSource = cardInteraction,
                indication = null,
                onClick = onClick
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp)
        ) {
            // Top Row: Countdown Badge (Left) + Quick Actions (Right)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Countdown / Status Chip (No Live streaming badge)
                val (badgeBg, badgeBorder, badgeText, badgeColor) = when (info.status) {
                    NextClassStatus.UPCOMING -> Quadruple(
                        Color.White,
                        Color(0xFFE2DCF5),
                        info.countdownText,
                        MyTuitionColors.PrimaryPurple
                    )
                    NextClassStatus.RESCHEDULED -> Quadruple(
                        Color(0xFFFFF3E0),
                        Color(0xFFFFCC80),
                        if (info.statusNote.isNotEmpty()) info.statusNote else "Rescheduled to 5:30 PM",
                        MyTuitionColors.StatusOrange
                    )
                    NextClassStatus.CANCELLED -> Quadruple(
                        Color(0xFFFFEBEE),
                        Color(0xFFFFCDD2),
                        "Class Cancelled",
                        MyTuitionColors.StatusRed
                    )
                }

                Box(
                    modifier = Modifier
                        .shadow(2.dp, CircleShape, spotColor = Color(0x10000000))
                        .clip(CircleShape)
                        .background(badgeBg)
                        .border(1.dp, badgeBorder, CircleShape)
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.Schedule,
                            contentDescription = null,
                            tint = badgeColor,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = badgeText,
                            style = MyTuitionTypography.LabelLarge.copy(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = badgeColor
                            )
                        )
                    }
                }

                // Quick Action Buttons: Reminder (15m) + Calendar
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Set Reminder Toggle
                    val reminderInteraction = remember { MutableInteractionSource() }
                    val reminderPressed by reminderInteraction.collectIsPressedAsState()
                    val reminderScale by animateFloatAsState(
                        targetValue = if (reminderPressed) 0.92f else 1f,
                        animationSpec = MyTuitionAnimations.claySpring,
                        label = "reminderScale"
                    )

                    Box(
                        modifier = Modifier
                            .scale(reminderScale)
                            .shadow(
                                elevation = if (isReminderActive) 4.dp else 2.dp,
                                shape = CircleShape,
                                spotColor = if (isReminderActive) MyTuitionColors.PrimaryPurple.copy(alpha = 0.4f) else Color(0x10000000)
                            )
                            .clip(CircleShape)
                            .background(if (isReminderActive) MyTuitionColors.PrimaryPurple else Color.White)
                            .border(
                                width = 1.dp,
                                color = if (isReminderActive) MyTuitionColors.PrimaryPurpleDark else Color(0xFFE2DCF5),
                                shape = CircleShape
                            )
                            .clickable(
                                interactionSource = reminderInteraction,
                                indication = null,
                                onClick = { isReminderActive = !isReminderActive }
                            )
                            .padding(horizontal = 10.dp, vertical = 7.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isReminderActive) Icons.Rounded.NotificationsActive else Icons.Rounded.NotificationsNone,
                                contentDescription = "Reminder 15 min before",
                                tint = if (isReminderActive) Color.White else MyTuitionColors.PrimaryPurple,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "15m",
                                style = MyTuitionTypography.LabelSmall.copy(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isReminderActive) Color.White else MyTuitionColors.TextPrimary
                                )
                            )
                        }
                    }

                    // Add to Calendar Button
                    val calInteraction = remember { MutableInteractionSource() }
                    val calPressed by calInteraction.collectIsPressedAsState()
                    val calScale by animateFloatAsState(
                        targetValue = if (calPressed) 0.92f else 1f,
                        animationSpec = MyTuitionAnimations.claySpring,
                        label = "calScale"
                    )

                    Box(
                        modifier = Modifier
                            .scale(calScale)
                            .size(36.dp)
                            .shadow(
                                elevation = if (isCalendarAdded) 4.dp else 2.dp,
                                shape = CircleShape,
                                spotColor = Color(0x10000000)
                            )
                            .clip(CircleShape)
                            .background(if (isCalendarAdded) Color(0xFFE8F5E9) else Color.White)
                            .border(
                                width = 1.dp,
                                color = if (isCalendarAdded) Color(0xFFA5D6A7) else Color(0xFFE2DCF5),
                                shape = CircleShape
                            )
                            .clickable(
                                interactionSource = calInteraction,
                                indication = null,
                                onClick = { isCalendarAdded = !isCalendarAdded }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isCalendarAdded) Icons.Rounded.Check else Icons.Rounded.CalendarToday,
                            contentDescription = "Add to Calendar",
                            tint = if (isCalendarAdded) MyTuitionColors.StatusGreen else MyTuitionColors.PrimaryPurple,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Subject Name (TitleExtra 26sp ExtraBold)
            Text(
                text = info.subjectName,
                style = MyTuitionTypography.TitleExtra.copy(
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MyTuitionColors.TextPrimary
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Teacher Name ("with Dr. Aalvina Fatehi")
            Text(
                text = "with ${info.teacherName}",
                style = MyTuitionTypography.BodyMedium.copy(
                    fontSize = 15.sp,
                    color = MyTuitionColors.TextSecondary
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Time: "Today • 5:00 PM - 6:30 PM"
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "🕒 ${info.timeText}",
                    style = MyTuitionTypography.LabelMedium.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MyTuitionColors.PrimaryPurpleDark
                    )
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Full-Width Offline Venue & Room Card (Key physical info)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(3.dp, RoundedCornerShape(22.dp), spotColor = Color(0x15000000))
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color.White)
                    .border(1.5.dp, Color(0xFFE4DEF5), RoundedCornerShape(22.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(MyTuitionColors.CardLavender, CircleShape)
                                .border(1.dp, Color(0xFFD9CFF5), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.LocationOn,
                                contentDescription = "Venue",
                                tint = MyTuitionColors.PrimaryPurple,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = info.room,
                                style = MyTuitionTypography.TitleMedium.copy(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MyTuitionColors.TextPrimary
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${info.floor} • Near Staircase B",
                                style = MyTuitionTypography.BodySmall.copy(
                                    fontSize = 12.5.sp,
                                    color = MyTuitionColors.TextSecondary
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // "View Room" / "Get Directions" Secondary Action Button
                    val dirInteraction = remember { MutableInteractionSource() }
                    val dirPressed by dirInteraction.collectIsPressedAsState()
                    val dirScale by animateFloatAsState(
                        targetValue = if (dirPressed) 0.94f else 1f,
                        animationSpec = MyTuitionAnimations.claySpring,
                        label = "dirScale"
                    )

                    Box(
                        modifier = Modifier
                            .scale(dirScale)
                            .shadow(2.dp, CircleShape, spotColor = Color(0x15000000))
                            .clip(CircleShape)
                            .background(MyTuitionColors.CardLavender)
                            .border(1.2.dp, Color(0xFFD9CFF5), CircleShape)
                            .clickable(
                                interactionSource = dirInteraction,
                                indication = null,
                                onClick = onViewRoomClick
                            )
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.Directions,
                                contentDescription = "Get Directions",
                                tint = MyTuitionColors.PrimaryPurple,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "View Room",
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

        // Inner bottom shadow for 3D depth
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(14.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.04f))
                    )
                )
        )
    }
}

/**
 * Empty state card when all classes for the day are finished.
 */
@Composable
fun NoMoreClassesCard(
    modifier: Modifier = Modifier,
    message: String = "You're all caught up for today! Review your homework or take a well-deserved rest."
) {
    val cardShape = RoundedCornerShape(28.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(6.dp, cardShape, spotColor = Color(0x15000000))
            .clip(cardShape)
            .background(Color(0xFFF6F3FF))
            .border(2.dp, Color(0xFFE8E2FA), cardShape)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "🎉",
                fontSize = 44.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "No more classes today 🎉",
                style = MyTuitionTypography.HeadlineSmall.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MyTuitionColors.TextPrimary
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = message,
                style = MyTuitionTypography.BodyMedium.copy(
                    fontSize = 14.sp,
                    color = MyTuitionColors.TextSecondary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            )
        }
    }
}

/**
 * Modal Bottom Sheet providing Room Details & Campus Navigation guide for offline classrooms.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomDirectionsBottomSheet(
    info: NextClassInfo,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Campus Navigation",
                        style = MyTuitionTypography.LabelLarge.copy(
                            fontSize = 13.sp,
                            color = MyTuitionColors.PrimaryPurple,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = info.room,
                        style = MyTuitionTypography.HeadlineMedium.copy(
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MyTuitionColors.TextPrimary
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(MyTuitionColors.CardLavender, CircleShape)
                        .border(1.5.dp, Color(0xFFD9CFF5), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.NearMe,
                        contentDescription = null,
                        tint = MyTuitionColors.PrimaryPurple,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Location details card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(3.dp, RoundedCornerShape(20.dp), spotColor = Color(0x10000000))
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFF9F7FD))
                    .border(1.5.dp, Color(0xFFEDE8F7), RoundedCornerShape(20.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    DirectionStep(
                        icon = "📍",
                        title = "Building & Floor",
                        description = info.floor
                    )
                    DirectionStep(
                        icon = "🧭",
                        title = "Landmark & Proximity",
                        description = info.directionsNote
                    )
                    DirectionStep(
                        icon = "🏫",
                        title = "Subject & Faculty",
                        description = "${info.subjectName} • ${info.teacherName}"
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Action: "Got it" Button
            val interactionSource = remember { MutableInteractionSource() }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .shadow(6.dp, MyTuitionShapes.ButtonPillShape, spotColor = MyTuitionColors.PrimaryPurple.copy(alpha = 0.35f))
                    .clip(MyTuitionShapes.ButtonPillShape)
                    .background(MyTuitionColors.PrimaryPurple)
                    .border(2.dp, MyTuitionColors.PrimaryPurpleDark, MyTuitionShapes.ButtonPillShape)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onDismiss
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Got It, Thanks! 👍",
                    style = MyTuitionTypography.LabelLarge.copy(
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Composable
private fun DirectionStep(
    icon: String,
    title: String,
    description: String
) {
    Row(verticalAlignment = Alignment.Top) {
        Text(text = icon, fontSize = 20.sp)
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = title,
                style = MyTuitionTypography.LabelLarge.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MyTuitionColors.TextPrimary
                )
            )
            Text(
                text = description,
                style = MyTuitionTypography.BodyMedium.copy(
                    fontSize = 13.sp,
                    color = MyTuitionColors.TextSecondary
                )
            )
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
