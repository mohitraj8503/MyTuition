package com.example.mytuition.core.designsystem.components

import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.material.icons.rounded.AttachFile
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mytuition.core.designsystem.MyTuitionAnimations
import com.example.mytuition.core.designsystem.MyTuitionColors
import com.example.mytuition.core.designsystem.MyTuitionTypography
import com.example.mytuition.core.designsystem.darken

@Composable
fun HomeworkItemCard(
    subjectTag: String,      // "Maths"
    subjectColor: Color,
    title: String,           // "Trigonometry Worksheet"
    dueText: String,         // "Due in 2 days"
    isOverdue: Boolean,
    isCompleted: Boolean,
    hasAttachment: Boolean,
    onToggleComplete: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = MyTuitionAnimations.claySpring,
        label = "hwCardScale"
    )
    val elevation by animateDpAsState(
        targetValue = if (isPressed) 3.dp else 8.dp,
        animationSpec = MyTuitionAnimations.claySpringDp,
        label = "hwCardElevation"
    )

    val checkScale by animateFloatAsState(
        targetValue = if (isCompleted) 1f else 0f,
        animationSpec = MyTuitionAnimations.bounceSpring,
        label = "checkScale"
    )

    val cardShape = RoundedCornerShape(28.dp)

    Box(
        modifier = modifier
            .scale(scale)
            .fillMaxWidth()
            .shadow(
                elevation = elevation,
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
                onClick = onClick
            )
            .alpha(if (isCompleted) 0.65f else 1f)
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

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Top Row: Subject pill + Due text with clock
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .shadow(2.dp, RoundedCornerShape(12.dp), spotColor = subjectColor.copy(alpha = 0.2f))
                        .clip(RoundedCornerShape(12.dp))
                        .background(subjectColor.copy(alpha = 0.18f))
                        .border(1.5.dp, subjectColor.darken(0.1f).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = subjectTag,
                        style = MyTuitionTypography.LabelSmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = subjectColor
                        )
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Schedule,
                        contentDescription = null,
                        tint = if (isOverdue) MyTuitionColors.StatusRed else MyTuitionColors.TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = dueText,
                        style = MyTuitionTypography.LabelMedium.copy(
                            fontSize = 13.sp,
                            fontWeight = if (isOverdue) FontWeight.Bold else FontWeight.Medium,
                            color = if (isOverdue) MyTuitionColors.StatusRed else MyTuitionColors.TextSecondary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Middle: Title + subject info
            Text(
                text = title,
                style = MyTuitionTypography.TitleMedium.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MyTuitionColors.TextPrimary,
                    textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Bottom Row: Attachment icon + Circular checkbox
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (hasAttachment) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.AttachFile,
                            contentDescription = "Attachment",
                            tint = MyTuitionColors.TextTertiary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Attachment",
                            style = MyTuitionTypography.LabelSmall.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MyTuitionColors.TextTertiary
                            )
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                // Circular Clay Checkbox (32.dp for kid fingers)
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .shadow(
                            elevation = 3.dp,
                            shape = CircleShape,
                            spotColor = if (isCompleted) MyTuitionColors.PrimaryPurple.copy(alpha = 0.3f) else Color(0x15000000)
                        )
                        .clip(CircleShape)
                        .background(
                            if (isCompleted) MyTuitionColors.PrimaryPurple else MyTuitionColors.CardWhite
                        )
                        .border(
                            width = 2.dp,
                            color = if (isCompleted) MyTuitionColors.PrimaryPurpleDark else MyTuitionColors.CardWhite.darken(0.12f),
                            shape = CircleShape
                        )
                        .clickable(onClick = onToggleComplete),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = "Completed",
                            tint = MyTuitionColors.TextOnPurple,
                            modifier = Modifier
                                .size(20.dp)
                                .scale(checkScale)
                        )
                    }
                }
            }
        }
    }
}
