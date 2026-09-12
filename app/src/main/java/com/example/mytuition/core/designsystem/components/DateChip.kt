package com.example.mytuition.core.designsystem.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.example.mytuition.core.designsystem.MyTuitionAnimations
import com.example.mytuition.core.designsystem.MyTuitionColors
import com.example.mytuition.core.designsystem.MyTuitionShapes
import com.example.mytuition.core.designsystem.MyTuitionTypography

@Composable
fun DateChip(
    dayAbbreviation: String = "",  // "Mon"
    dateNumber: String,            // "17"
    isActive: Boolean = false,
    dayOfWeek: String = dayAbbreviation,
    isSelected: Boolean = isActive,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val effectiveDay = if (dayOfWeek.isNotEmpty()) dayOfWeek else dayAbbreviation
    val effectiveActive = isSelected || isActive

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Bounce scale: 1.08 on active, 0.95 on press
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else if (effectiveActive) 1.08f else 1f,
        animationSpec = spring(dampingRatio = 0.50f, stiffness = 300f),
        label = "dateChipScale"
    )

    val currentElevation by animateDpAsState(
        targetValue = if (effectiveActive) 8.dp else 4.dp,
        label = "dateChipElevation"
    )

    val bgColor by animateColorAsState(
        targetValue = if (effectiveActive) MyTuitionColors.PrimaryPurple else MyTuitionColors.CardWhite,
        label = "dateChipBg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (effectiveActive) MyTuitionColors.PrimaryPurpleDark else Color(0xFFE8E6F0),
        label = "dateChipBorder"
    )
    val dayTextColor by animateColorAsState(
        targetValue = if (effectiveActive) Color.White.copy(alpha = 0.9f) else MyTuitionColors.TextSecondary,
        label = "dateChipDayColor"
    )
    val numTextColor by animateColorAsState(
        targetValue = if (effectiveActive) Color.White else MyTuitionColors.TextPrimary,
        label = "dateChipNumColor"
    )

    val chipShape = RoundedCornerShape(20.dp)

    Box(
        modifier = modifier
            .scale(scale)
            .size(width = 64.dp, height = 72.dp)
            .shadow(
                elevation = currentElevation,
                shape = chipShape,
                ambientColor = Color(0x331A1A1A),
                spotColor = if (effectiveActive) MyTuitionColors.PrimaryPurple.copy(alpha = 0.35f) else Color(0x181A1A1A)
            )
            .clip(chipShape)
            .background(bgColor, chipShape)
            .border(2.dp, borderColor, chipShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = effectiveDay,
                style = MyTuitionTypography.LabelMedium.copy(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = dayTextColor
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = dateNumber,
                style = MyTuitionTypography.TitleLarge.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = numTextColor
                )
            )
        }

        // Inner shadow on inactive white chip
        if (!effectiveActive) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.04f))
                        )
                    )
            )
        }
    }
}
