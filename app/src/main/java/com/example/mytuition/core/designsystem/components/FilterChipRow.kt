package com.example.mytuition.core.designsystem.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import com.example.mytuition.core.designsystem.MyTuitionAnimations
import com.example.mytuition.core.designsystem.MyTuitionColors
import com.example.mytuition.core.designsystem.MyTuitionShapes
import com.example.mytuition.core.designsystem.MyTuitionTypography
import com.example.mytuition.core.designsystem.darken

@Composable
fun FilterChipRow(
    filters: List<String>,   // ["All", "Pending", "Completed", "Overdue"]
    activeIndex: Int,
    onFilterSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        filters.forEachIndexed { index, title ->
            val isActive = index == activeIndex
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()

            val scale by animateFloatAsState(
                targetValue = if (isPressed) 0.94f else if (isActive) 1.02f else 1f,
                animationSpec = MyTuitionAnimations.bounceSpring,
                label = "filterChipScale"
            )
            val elevation by animateDpAsState(
                targetValue = if (isPressed) 2.dp else if (isActive) 6.dp else 3.dp,
                animationSpec = MyTuitionAnimations.claySpringDp,
                label = "filterChipElevation"
            )

            val bgColor = if (isActive) MyTuitionColors.PrimaryPurple else MyTuitionColors.CardWhite
            val borderColor = if (isActive) MyTuitionColors.PrimaryPurpleDark else MyTuitionColors.CardWhite.darken(0.08f)
            val textColor = if (isActive) MyTuitionColors.TextOnPurple else MyTuitionColors.TextSecondary

            Box(
                modifier = Modifier
                    .scale(scale)
                    .shadow(
                        elevation = elevation,
                        shape = MyTuitionShapes.PillShape,
                        spotColor = if (isActive) MyTuitionColors.PrimaryPurple.copy(alpha = 0.35f) else Color(0x18000000)
                    )
                    .clip(MyTuitionShapes.PillShape)
                    .background(bgColor, MyTuitionShapes.PillShape)
                    .border(2.dp, borderColor, MyTuitionShapes.PillShape)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = { onFilterSelect(index) }
                    )
                    .padding(horizontal = 18.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    style = MyTuitionTypography.LabelLarge.copy(
                        fontSize = 14.sp,
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                        color = textColor
                    )
                )
            }
        }
    }
}
