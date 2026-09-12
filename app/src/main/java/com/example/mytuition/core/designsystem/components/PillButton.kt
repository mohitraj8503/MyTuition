package com.example.mytuition.core.designsystem.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mytuition.core.designsystem.MyTuitionAnimations
import com.example.mytuition.core.designsystem.MyTuitionColors
import com.example.mytuition.core.designsystem.MyTuitionShapes
import com.example.mytuition.core.designsystem.MyTuitionTypography

enum class PillButtonVariant { Primary, Secondary, Outline }

@Composable
fun PillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    variant: PillButtonVariant = PillButtonVariant.Primary
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.96f else 1f,
        animationSpec = MyTuitionAnimations.claySpring,
        label = "pillButtonScale"
    )

    val currentElevation by animateDpAsState(
        targetValue = if (isPressed && enabled) 3.dp else 10.dp,
        animationSpec = androidx.compose.animation.core.spring(dampingRatio = 0.55f, stiffness = 380f),
        label = "pillElevation"
    )

    val bgColor = when (variant) {
        PillButtonVariant.Primary -> MyTuitionColors.PrimaryPurple
        PillButtonVariant.Secondary -> MyTuitionColors.CardWhite
        PillButtonVariant.Outline -> Color.Transparent
    }

    val borderColor = when (variant) {
        PillButtonVariant.Primary -> MyTuitionColors.PrimaryPurpleDark
        PillButtonVariant.Secondary -> Color(0xFFE8E6F0)
        PillButtonVariant.Outline -> MyTuitionColors.PrimaryPurple
    }

    val contentColor = when (variant) {
        PillButtonVariant.Primary -> MyTuitionColors.TextOnPurple
        PillButtonVariant.Secondary -> MyTuitionColors.TextPrimary
        PillButtonVariant.Outline -> MyTuitionColors.PrimaryPurple
    }

    Box(
        modifier = modifier
            .defaultMinSize(minHeight = 56.dp)
            .scale(scale)
            .alpha(if (enabled) 1f else 0.45f)
            .shadow(
                elevation = if (enabled) currentElevation else 0.dp,
                shape = MyTuitionShapes.ButtonPillShape,
                ambientColor = Color(0x331A1A1A),
                spotColor = if (variant == PillButtonVariant.Primary)
                    MyTuitionColors.PrimaryPurple.copy(alpha = 0.35f)
                else
                    Color(0x221A1A1A)
            )
            .clip(MyTuitionShapes.ButtonPillShape)
            .background(bgColor, MyTuitionShapes.ButtonPillShape)
            .border(2.dp, borderColor, MyTuitionShapes.ButtonPillShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onClick()
                }
            )
            .padding(horizontal = 24.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                style = MyTuitionTypography.LabelLarge.copy(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
            )
            if (icon != null) {
                Spacer(modifier = Modifier.width(10.dp))
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
