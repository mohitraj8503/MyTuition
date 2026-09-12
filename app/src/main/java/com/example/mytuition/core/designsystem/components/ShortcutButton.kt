package com.example.mytuition.core.designsystem.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mytuition.core.designsystem.MyTuitionColors
import com.example.mytuition.core.domain.model.FeeStatus
import kotlinx.coroutines.delay

@Composable
fun ShortcutButton(
    icon: ImageVector,
    iconBackgroundColor: Color,
    iconBorderColor: Color,
    label: String,
    statusText: String,
    statusColor: Color,
    statusBackgroundColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 400f),
        label = "shortcutScale"
    )

    val elevation by animateDpAsState(
        targetValue = if (isPressed) 2.dp else 6.dp,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 400f),
        label = "shortcutElevation"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        // Icon clay circle (52dp, thick 2dp border, soft drop shadow)
        Box(
            modifier = Modifier
                .scale(scale)
                .size(52.dp)
                .shadow(
                    elevation = elevation,
                    shape = CircleShape,
                    ambientColor = Color(0x221A1A1A),
                    spotColor = iconBackgroundColor.copy(alpha = 0.35f)
                )
                .clip(CircleShape)
                .background(iconBackgroundColor)
                .border(2.dp, iconBorderColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(7.dp))

        // Label
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = MyTuitionColors.TextPrimary
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Status pill
        Surface(
            shape = RoundedCornerShape(999.dp),
            color = statusBackgroundColor
        ) {
            Text(
                text = statusText,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = statusColor,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
        }
    }
}

@Composable
fun ShortcutRow(
    homeworkPending: Int,
    attendancePercent: Int,
    feeStatus: FeeStatus,
    feeAmount: Double?,
    notesCount: Int,
    onHomeworkClick: () -> Unit,
    onAttendanceClick: () -> Unit,
    onFeesClick: () -> Unit,
    onNotesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(50)
        isVisible = true
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        // 1. Homework Button (Coral)
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(tween(300, delayMillis = 0)) +
                    slideInVertically(tween(300, delayMillis = 0), initialOffsetY = { it / 4 }) +
                    scaleIn(tween(300, delayMillis = 0), initialScale = 0.8f),
            modifier = Modifier.weight(1f)
        ) {
            ShortcutButton(
                icon = Icons.Filled.Assignment,
                iconBackgroundColor = Color(0xFFFF9F40),
                iconBorderColor = Color(0xFFE8862D),
                label = "Homework",
                statusText = if (homeworkPending > 0) "$homeworkPending left" else "All done! 🎉",
                statusColor = if (homeworkPending > 0) Color(0xFFFF3B30) else Color(0xFF34C759),
                statusBackgroundColor = if (homeworkPending > 0) Color(0xFFFF3B30).copy(alpha = 0.15f) else Color(0xFF34C759).copy(alpha = 0.15f),
                onClick = onHomeworkClick
            )
        }

        // 2. Attendance Button (Green)
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(tween(300, delayMillis = 80)) +
                    slideInVertically(tween(300, delayMillis = 80), initialOffsetY = { it / 4 }) +
                    scaleIn(tween(300, delayMillis = 80), initialScale = 0.8f),
            modifier = Modifier.weight(1f)
        ) {
            ShortcutButton(
                icon = Icons.Filled.CheckCircle,
                iconBackgroundColor = Color(0xFF34C759),
                iconBorderColor = Color(0xFF2DA34C),
                label = "Attendance",
                statusText = "$attendancePercent%",
                statusColor = if (attendancePercent >= 75) Color(0xFF34C759) else Color(0xFFFFB340),
                statusBackgroundColor = if (attendancePercent >= 75) Color(0xFF34C759).copy(alpha = 0.15f) else Color(0xFFFFB340).copy(alpha = 0.15f),
                onClick = onAttendanceClick
            )
        }

        // 3. Fees Button (Purple)
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(tween(300, delayMillis = 160)) +
                    slideInVertically(tween(300, delayMillis = 160), initialOffsetY = { it / 4 }) +
                    scaleIn(tween(300, delayMillis = 160), initialScale = 0.8f),
            modifier = Modifier.weight(1f)
        ) {
            val amountInt = feeAmount?.toInt() ?: 2500
            val (statusText, statusColor) = when (feeStatus) {
                FeeStatus.PAID -> Pair("Paid ✓", Color(0xFF34C759))
                FeeStatus.PENDING -> Pair("₹$amountInt due", Color(0xFFFFB340))
                FeeStatus.OVERDUE -> Pair("₹$amountInt OVERDUE", Color(0xFFFF3B30))
            }

            ShortcutButton(
                icon = Icons.Filled.Payments,
                iconBackgroundColor = Color(0xFF6C48FF),
                iconBorderColor = Color(0xFF5538CC),
                label = "Fees",
                statusText = statusText,
                statusColor = statusColor,
                statusBackgroundColor = statusColor.copy(alpha = 0.15f),
                onClick = onFeesClick
            )
        }

        // 4. Notes Button (Blue)
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(tween(300, delayMillis = 240)) +
                    slideInVertically(tween(300, delayMillis = 240), initialOffsetY = { it / 4 }) +
                    scaleIn(tween(300, delayMillis = 240), initialScale = 0.8f),
            modifier = Modifier.weight(1f)
        ) {
            ShortcutButton(
                icon = Icons.AutoMirrored.Filled.MenuBook,
                iconBackgroundColor = Color(0xFF007AFF),
                iconBorderColor = Color(0xFF0066D6),
                label = "Notes",
                statusText = "$notesCount files",
                statusColor = Color(0xFF007AFF),
                statusBackgroundColor = Color(0xFF007AFF).copy(alpha = 0.15f),
                onClick = onNotesClick
            )
        }
    }
}
