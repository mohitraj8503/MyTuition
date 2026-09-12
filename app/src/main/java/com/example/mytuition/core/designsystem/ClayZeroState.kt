package com.example.mytuition.core.designsystem

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ClayZeroState(
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Rounded.CheckCircle,
    title: String = "All Caught Up!",
    subtitle: String = "You have no pending items here. Enjoy your free time or explore new topics!",
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
    badgeColor: Color = Color(0xFFD4FF26),
    badgeSecondaryColor: Color = Color(0xFFA2D600)
) {
    // Gentle floating breathing animation for tactile clay feel
    val infiniteTransition = rememberInfiniteTransition(label = "zeroStateFloat")
    val floatAnim by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatAnim"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Clay Container with 3D Main Sphere & Floating Accent Bubbles
        Box(
            modifier = Modifier
                .size(140.dp)
                .offset(y = floatAnim.dp),
            contentAlignment = Alignment.Center
        ) {
            // Little decorative clay bubble top-left
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.TopStart)
                    .offset(x = 6.dp, y = 6.dp)
                    .shadow(8.dp, CircleShape, spotColor = Color(0xFF8A62FF).copy(alpha = 0.4f))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFFB092FF), Color(0xFF7B52FF))
                        ),
                        CircleShape
                    )
                    .border(1.dp, Color.White.copy(alpha = 0.8f), CircleShape)
            )

            // Little decorative clay bubble bottom-right
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = (-4).dp, y = (-8).dp)
                    .shadow(8.dp, CircleShape, spotColor = Color(0xFFFF8080).copy(alpha = 0.35f))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFFFFB0B0), Color(0xFFFF6B6B))
                        ),
                        CircleShape
                    )
                    .border(1.dp, Color.White.copy(alpha = 0.7f), CircleShape)
            )

            // Main 3D tactile sphere
            Box(
                modifier = Modifier
                    .size(104.dp)
                    .shadow(
                        elevation = 20.dp,
                        shape = CircleShape,
                        spotColor = badgeSecondaryColor.copy(alpha = 0.55f),
                        ambientColor = Color.Black.copy(alpha = 0.1f)
                    )
                    .background(
                        Brush.verticalGradient(
                            listOf(badgeColor, badgeSecondaryColor)
                        ),
                        CircleShape
                    )
                    .border(
                        width = 2.dp,
                        brush = Brush.verticalGradient(
                            listOf(Color.White.copy(alpha = 0.85f), Color.White.copy(alpha = 0.15f))
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Shiny highlight glare top-left of sphere
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .align(Alignment.TopStart)
                        .offset(x = 18.dp, y = 14.dp)
                        .background(
                            Brush.radialGradient(
                                listOf(Color.White.copy(alpha = 0.75f), Color.Transparent)
                            ),
                            CircleShape
                        )
                )

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF13131A),
                    modifier = Modifier.size(52.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Title
        Text(
            text = title,
            style = MyTuitionTypography.HeadlineMedium.copy(fontSize = 24.sp, lineHeight = 30.sp),
            color = MyTuitionColors.TextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Subtitle
        Text(
            text = subtitle,
            style = MyTuitionTypography.BodyMedium.copy(fontSize = 14.sp, lineHeight = 21.sp),
            color = MyTuitionColors.TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        // Optional Tactile Clay Action Button
        if (actionLabel != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(26.dp))

            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val buttonScale by animateFloatAsState(
                targetValue = if (isPressed) 0.94f else 1f,
                animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
                label = "zeroStateButtonScale"
            )

            Box(
                modifier = Modifier
                    .scale(buttonScale)
                    .shadow(
                        elevation = 14.dp,
                        shape = RoundedCornerShape(26.dp),
                        spotColor = MyTuitionColors.PrimaryPurple.copy(alpha = 0.35f),
                        ambientColor = Color.Black.copy(alpha = 0.08f)
                    )
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(MyTuitionColors.PrimaryPurple, Color(0xFF6536F5))
                        ),
                        shape = RoundedCornerShape(26.dp)
                    )
                    .border(
                        width = 1.5.dp,
                        brush = Brush.verticalGradient(
                            listOf(Color.White.copy(alpha = 0.7f), Color.White.copy(alpha = 0.15f))
                        ),
                        shape = RoundedCornerShape(26.dp)
                    )
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onActionClick
                    )
                    .padding(horizontal = 26.dp, vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = actionLabel,
                    style = MyTuitionTypography.TitleMedium.copy(fontSize = 14.sp),
                    color = Color.White
                )
            }
        }
    }
}
