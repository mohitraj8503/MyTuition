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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.example.mytuition.core.designsystem.MyTuitionAnimations
import com.example.mytuition.core.designsystem.MyTuitionColors
import com.example.mytuition.core.designsystem.MyTuitionShapes
import com.example.mytuition.core.designsystem.MyTuitionTypography
import com.example.mytuition.core.designsystem.darken

@Composable
fun LessonRow(
    icon: ImageVector,
    iconColor: Color,
    subjectName: String,     // "History"
    duration: String,         // "55 min"
    resourceType: String,     // "Video", "Quiz", "Notes"
    resourceIcon: ImageVector,
    onClick: () -> Unit = {},
    showDivider: Boolean = true,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = MyTuitionAnimations.claySpring,
        label = "lessonRowScale"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Puffy colored clay circle container (44x44.dp)
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .shadow(4.dp, CircleShape, spotColor = iconColor.copy(alpha = 0.25f))
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.18f))
                    .border(1.5.dp, iconColor.darken(0.12f).copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = subjectName,
                    tint = iconColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Middle: Subject name + DifficultyDots + duration
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = subjectName,
                    style = MyTuitionTypography.TitleMedium.copy(
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MyTuitionColors.TextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(3.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    DifficultyDots(count = 3)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = duration,
                        style = MyTuitionTypography.LabelMedium.copy(
                            fontSize = 13.sp,
                            color = MyTuitionColors.TextSecondary
                        )
                    )
                }
            }

            // Right: Resource type clay pill badge
            Box(
                modifier = Modifier
                    .shadow(2.dp, MyTuitionShapes.PillShape, spotColor = Color(0x15000000))
                    .clip(MyTuitionShapes.PillShape)
                    .background(MyTuitionColors.CardWhite)
                    .border(1.5.dp, MyTuitionColors.CardWhite.darken(0.08f), MyTuitionShapes.PillShape)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = resourceIcon,
                        contentDescription = null,
                        tint = MyTuitionColors.PrimaryPurple,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = resourceType,
                        style = MyTuitionTypography.LabelSmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MyTuitionColors.PrimaryPurple
                        )
                    )
                }
            }
        }

        if (showDivider) {
            HorizontalDivider(
                thickness = 1.dp,
                color = MyTuitionColors.DividerColor,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
