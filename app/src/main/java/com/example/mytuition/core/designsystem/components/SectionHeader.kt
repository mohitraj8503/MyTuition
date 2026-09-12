package com.example.mytuition.core.designsystem.components

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mytuition.core.designsystem.MyTuitionColors
import com.example.mytuition.core.designsystem.MyTuitionTypography
import com.example.mytuition.core.designsystem.darken

@Composable
fun SectionHeader(
    title: String,                     // "Class Schedule"
    trailingText: String? = null,      // "Aug 2025 ⌵"
    onTrailingClick: () -> Unit = {},
    actionText: String? = null,
    onActionClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val effectiveTrailing = actionText ?: trailingText
    val effectiveClick = if (actionText != null) onActionClick else onTrailingClick

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MyTuitionTypography.TitleLarge.copy(
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = MyTuitionColors.TextPrimary
            )
        )

        if (effectiveTrailing != null) {
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val scale by animateFloatAsState(
                targetValue = if (isPressed) 0.95f else 1f,
                animationSpec = spring(dampingRatio = 0.55f, stiffness = 380f),
                label = "trailingScale"
            )

            Box(
                modifier = Modifier
                    .scale(scale)
                    .shadow(
                        elevation = if (isPressed) 2.dp else 4.dp,
                        shape = RoundedCornerShape(16.dp),
                        ambientColor = Color(0x221A1A1A),
                        spotColor = Color(0x181A1A1A)
                    )
                    .clip(RoundedCornerShape(16.dp))
                    .background(MyTuitionColors.CardWhite)
                    .border(
                        width = 1.5.dp,
                        color = MyTuitionColors.CardWhite.darken(0.08f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = effectiveClick
                    )
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = effectiveTrailing,
                    style = MyTuitionTypography.LabelMedium.copy(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MyTuitionColors.PrimaryPurple
                    )
                )
            }
        }
    }
}
