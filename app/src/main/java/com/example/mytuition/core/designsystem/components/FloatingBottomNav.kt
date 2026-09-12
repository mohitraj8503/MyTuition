package com.example.mytuition.core.designsystem.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mytuition.core.designsystem.MyTuitionColors
import com.example.mytuition.core.designsystem.MyTuitionShapes
import com.example.mytuition.core.designsystem.MyTuitionTypography

data class BottomNavItem(val label: String, val icon: ImageVector)

@Composable
fun FloatingBottomNav(
    items: List<BottomNavItem>,
    activeIndex: Int,
    onItemSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        val navShape = RoundedCornerShape(32.dp)

        // White clay bar with 2dp border and 16dp shadow
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .shadow(
                    elevation = 16.dp,
                    shape = navShape,
                    ambientColor = Color(0x331A1A1A),
                    spotColor = Color(0x221A1A1A)
                )
                .clip(navShape)
                .background(MyTuitionColors.CardWhite)
                .border(2.dp, Color(0xFFE8E6F0), navShape)
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEachIndexed { index, item ->
                    val isActive = index == activeIndex
                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()

                    val scale by animateFloatAsState(
                        targetValue = if (isPressed) 0.92f else if (isActive) 1.05f else 1f,
                        animationSpec = spring(dampingRatio = 0.50f, stiffness = 300f),
                        label = "navScale"
                    )

                    val iconColor by animateColorAsState(
                        targetValue = if (isActive) MyTuitionColors.PrimaryPurple else MyTuitionColors.TextTertiary,
                        label = "navIconColor"
                    )

                    // Give more space to active item so label stays strictly on 1 line
                    val weight = if (isActive) 1.6f else 0.8f

                    Box(
                        modifier = Modifier
                            .weight(weight)
                            .fillMaxHeight()
                            .scale(scale)
                            .clip(RoundedCornerShape(20.dp))
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null,
                                onClick = { onItemSelect(index) }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        ) {
                            // Active item: puffy purple clay ball (40dp) behind icon
                            Box(
                                modifier = Modifier
                                    .size(if (isActive) 40.dp else 36.dp)
                                    .then(
                                        if (isActive) {
                                            Modifier
                                                .shadow(4.dp, CircleShape, spotColor = MyTuitionColors.PrimaryPurple.copy(alpha = 0.3f))
                                                .clip(CircleShape)
                                                .background(MyTuitionColors.PrimaryPurpleLight)
                                                .border(2.dp, Color(0xFFDCD2FA), CircleShape)
                                        } else Modifier
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label,
                                    tint = iconColor,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            if (isActive) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = item.label,
                                    style = MyTuitionTypography.LabelSmall.copy(
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MyTuitionColors.PrimaryPurple
                                    ),
                                    maxLines = 1,
                                    softWrap = false,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }

            // Bottom thickness gradient
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(10.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.04f))
                        )
                    )
            )
        }
    }
}
