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
import androidx.compose.material.icons.rounded.MoreHoriz
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
import com.example.mytuition.core.designsystem.MyTuitionAnimations
import com.example.mytuition.core.designsystem.MyTuitionColors
import com.example.mytuition.core.designsystem.MyTuitionTypography
import com.example.mytuition.core.designsystem.darken

@Composable
fun TimelineSessionCard(
    time: String,            // "8:00"
    subjectName: String,     // "Maths"
    subTopic: String,        // "Chapter 4"
    iconColor: Color,        // SubjectMath green, etc.
    icon: ImageVector,
    onMenuClick: () -> Unit,
    onClick: () -> Unit = {},
    showDot: Boolean = true,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = MyTuitionAnimations.claySpring,
        label = "timelineCardScale"
    )
    val elevation by animateDpAsState(
        targetValue = if (isPressed) 4.dp else 8.dp,
        animationSpec = MyTuitionAnimations.claySpringDp,
        label = "timelineCardElevation"
    )

    val cardShape = RoundedCornerShape(26.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Time label in small white clay pill (13sp LabelMedium)
        Box(
            modifier = Modifier
                .width(58.dp)
                .height(34.dp)
                .shadow(
                    elevation = 3.dp,
                    shape = RoundedCornerShape(12.dp),
                    ambientColor = Color(0x201A1A1A),
                    spotColor = Color(0x151A1A1A)
                )
                .clip(RoundedCornerShape(12.dp))
                .background(MyTuitionColors.CardWhite)
                .border(
                    width = 1.5.dp,
                    color = MyTuitionColors.CardWhite.darken(0.08f),
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = time,
                style = MyTuitionTypography.LabelMedium.copy(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MyTuitionColors.TextPrimary
                )
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Center lane for the 12dp purple dot sitting on the continuous 4dp line
        Box(
            modifier = Modifier.width(16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (showDot) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .shadow(2.dp, CircleShape)
                        .background(MyTuitionColors.PrimaryPurple, CircleShape)
                        .border(2.dp, MyTuitionColors.CardWhite, CircleShape)
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        // White Clay Card
        Box(
            modifier = Modifier
                .weight(1f)
                .scale(scale)
                .shadow(
                    elevation = elevation,
                    shape = cardShape,
                    ambientColor = Color(0x261A1A1A),
                    spotColor = Color(0x1E1A1A1A)
                )
                .clip(cardShape)
                .background(MyTuitionColors.CardWhite)
                .border(
                    width = 2.dp,
                    color = MyTuitionColors.CardWhite.darken(0.08f),
                    shape = cardShape
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                )
        ) {
            // Inner bottom shadow for 3D thickness
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

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Puffy 48dp circle filled with pastel subject color + 2dp darker border + 6dp shadow
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .shadow(
                                elevation = 6.dp,
                                shape = CircleShape,
                                ambientColor = iconColor.copy(alpha = 0.25f),
                                spotColor = iconColor.copy(alpha = 0.35f)
                            )
                            .clip(CircleShape)
                            .background(iconColor.copy(alpha = 0.18f))
                            .border(
                                width = 2.dp,
                                color = iconColor.darken(0.12f).copy(alpha = 0.5f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = subjectName,
                            tint = iconColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = subjectName,
                            style = MyTuitionTypography.TitleMedium.copy(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MyTuitionColors.TextPrimary
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = subTopic,
                            style = MyTuitionTypography.BodyMedium.copy(
                                fontSize = 15.sp,
                                color = MyTuitionColors.TextSecondary
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Three dots menu button
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(onClick = onMenuClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.MoreHoriz,
                        contentDescription = "Options",
                        tint = MyTuitionColors.TextTertiary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}
