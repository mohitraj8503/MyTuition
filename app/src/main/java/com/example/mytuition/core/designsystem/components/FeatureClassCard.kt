package com.example.mytuition.core.designsystem.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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

@Composable
fun FeatureClassCard(
    status: String = "Live in 1 hour",
    statusColor: Color = MyTuitionColors.StatusGreen,
    title: String = "Creative Sketching",
    subtitle: String = "Batch OOV 021",
    progressLabel: String = "Your Progress",
    progressPercent: Int = 60,
    buttonLabel: String = "Join Class",
    illustration: @Composable () -> Unit,
    onClick: () -> Unit,
    onJoinClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = MyTuitionAnimations.claySpring,
        label = "featureCardScale"
    )

    var animatedTarget by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(progressPercent) {
        animatedTarget = (progressPercent / 100f).coerceIn(0f, 1f)
    }

    val animatedProgress by animateFloatAsState(
        targetValue = animatedTarget,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "progressBarAnim"
    )

    val cardShape = RoundedCornerShape(32.dp)

    Box(
        modifier = modifier
            .scale(scale)
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
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
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        // Right-aligned illustration slot (approx 40% width, 140dp)
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
                .size(140.dp),
            contentAlignment = Alignment.Center
        ) {
            illustration()
        }

        // Left Content Column with generous 24dp internal padding
        Column(
            modifier = Modifier
                .fillMaxWidth(0.66f)
                .padding(24.dp)
        ) {
            // "Live in 1 hour": green dot 10dp + LabelLarge on white clay chip
            Box(
                modifier = Modifier
                    .shadow(2.dp, CircleShape, spotColor = Color(0x10000000))
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(1.dp, Color(0xFFE5E0F2), CircleShape)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(statusColor, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = status,
                        style = MyTuitionTypography.LabelLarge.copy(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = statusColor
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Title "Creative Sketching" (TitleExtra 24sp ExtraBold)
            Text(
                text = title,
                style = MyTuitionTypography.TitleExtra.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MyTuitionColors.TextPrimary
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Subtitle "Batch OOV 021" (BodyMedium 15sp)
            Text(
                text = subtitle,
                style = MyTuitionTypography.BodyMedium.copy(
                    fontSize = 15.sp,
                    color = MyTuitionColors.TextSecondary
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Progress row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = progressLabel,
                    style = MyTuitionTypography.LabelMedium.copy(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MyTuitionColors.TextSecondary
                    )
                )
                Text(
                    text = "$progressPercent%",
                    style = MyTuitionTypography.LabelLarge.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MyTuitionColors.PrimaryPurple
                    )
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Progress bar (10dp tall, track #DCD4F8, fill purple, rounded 5dp caps)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color(0xFFDCD4F8))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(5.dp))
                        .background(MyTuitionColors.PrimaryPurple)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // "Join Class" button: 56dp tall purple clay pill
            val joinInteraction = remember { MutableInteractionSource() }
            val joinPressed by joinInteraction.collectIsPressedAsState()
            val joinScale by animateFloatAsState(
                targetValue = if (joinPressed) 0.96f else 1f,
                animationSpec = MyTuitionAnimations.claySpring,
                label = "joinScale"
            )

            Box(
                modifier = Modifier
                    .scale(joinScale)
                    .height(56.dp)
                    .shadow(
                        elevation = if (joinPressed) 3.dp else 8.dp,
                        shape = MyTuitionShapes.ButtonPillShape,
                        spotColor = MyTuitionColors.PrimaryPurple.copy(alpha = 0.4f)
                    )
                    .clip(MyTuitionShapes.ButtonPillShape)
                    .background(MyTuitionColors.PrimaryPurple)
                    .border(2.dp, MyTuitionColors.PrimaryPurpleDark, MyTuitionShapes.ButtonPillShape)
                    .clickable(
                        interactionSource = joinInteraction,
                        indication = null,
                        onClick = onJoinClick
                    )
                    .padding(horizontal = 22.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = buttonLabel,
                        style = MyTuitionTypography.LabelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 15.sp
                        )
                    )
                }
            }
        }

        // Inner bottom shadow for 3D depth
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(16.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.04f))
                    )
                )
        )
    }
}
