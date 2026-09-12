package com.example.mytuition.core.designsystem.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * MyTuition's signature yellow cartoon mascot character.
 * Renders an adorable, expressive character face that scales cleanly to any size.
 */
@Composable
fun MyTuitionLogo(
    size: Dp = 80.dp,
    modifier: Modifier = Modifier,
    showClayCard: Boolean = true // wrap in a white clay circle card
) {
    val cardShape = CircleShape

    if (showClayCard) {
        Box(
            modifier = modifier
                .size(size)
                .shadow(
                    elevation = 12.dp,
                    shape = cardShape,
                    ambientColor = Color(0x331A1A1A),
                    spotColor = Color(0x221A1A1A)
                )
                .clip(cardShape)
                .background(Color.White)
                .border(2.dp, Color(0xFFF0EBFF), cardShape)
                .padding(size * 0.10f),
            contentAlignment = Alignment.Center
        ) {
            YellowMascotCharacter(modifier = Modifier.fillMaxSize())
        }
    } else {
        YellowMascotCharacter(
            modifier = modifier
                .size(size)
                .shadow(4.dp, CircleShape, spotColor = Color(0x30FFA000))
        )
    }
}

@Composable
fun YellowMascotCharacter(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFEE58), // Bright warm yellow center
                        Color(0xFFFFCA28), // Golden mid tone
                        Color(0xFFFFA000)  // Warm clay shadow edge
                    )
                ),
                shape = CircleShape
            )
            .border(
                width = 2.dp,
                color = Color(0xFFFF8F00).copy(alpha = 0.7f),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height

            // 1. Rosy blush cheeks
            val blushRadius = w * 0.10f
            val blushY = h * 0.58f
            drawCircle(
                color = Color(0xFFFF7043).copy(alpha = 0.35f),
                radius = blushRadius,
                center = Offset(w * 0.20f, blushY)
            )
            drawCircle(
                color = Color(0xFFFF7043).copy(alpha = 0.35f),
                radius = blushRadius,
                center = Offset(w * 0.80f, blushY)
            )

            // 2. Playful happy smile arc
            val smilePath = Path().apply {
                moveTo(w * 0.40f, h * 0.65f)
                quadraticTo(
                    w * 0.50f, h * 0.76f,
                    w * 0.60f, h * 0.65f
                )
            }
            drawPath(
                path = smilePath,
                color = Color(0xFF221A44),
                style = Stroke(
                    width = (w * 0.05f).coerceAtLeast(2f),
                    cap = StrokeCap.Round
                )
            )

            // 3. Cute Eyebrows
            val browWidth = w * 0.14f
            val browHeight = h * 0.035f
            val browY = h * 0.28f

            // Left eyebrow (tilted slightly)
            drawRoundRect(
                color = Color(0xFF221A44),
                topLeft = Offset(w * 0.26f, browY),
                size = androidx.compose.ui.geometry.Size(browWidth, browHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(browHeight / 2)
            )

            // Right eyebrow
            drawRoundRect(
                color = Color(0xFF221A44),
                topLeft = Offset(w * 0.60f, browY),
                size = androidx.compose.ui.geometry.Size(browWidth, browHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(browHeight / 2)
            )
        }

        // 4. Two big cartoon eyes with double specular reflections
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BoxWithConstraints(modifier = Modifier.fillMaxWidth(0.68f)) {
                val eyeWidth = maxWidth * 0.42f
                val eyeHeight = eyeWidth * 1.25f

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MascotEye(width = eyeWidth, height = eyeHeight)
                    MascotEye(width = eyeWidth, height = eyeHeight)
                }
            }
        }
    }
}

@Composable
private fun MascotEye(width: Dp, height: Dp) {
    val eyeShape = RoundedCornerShape(height / 2)

    // White eye background with subtle border
    Box(
        modifier = Modifier
            .size(width = width, height = height)
            .shadow(2.dp, eyeShape, spotColor = Color(0x30000000))
            .clip(eyeShape)
            .background(Color.White)
            .border(1.5.dp, Color(0x20000000), eyeShape),
        contentAlignment = Alignment.Center
    ) {
        // Dark pupil looking forward/slightly up
        val pupilSize = width * 0.68f
        Box(
            modifier = Modifier
                .size(pupilSize)
                .clip(CircleShape)
                .background(Color(0xFF221A44)),
            contentAlignment = Alignment.TopEnd
        ) {
            // Big primary specular highlight
            Box(
                modifier = Modifier
                    .padding(top = pupilSize * 0.12f, end = pupilSize * 0.12f)
                    .size(pupilSize * 0.38f)
                    .clip(CircleShape)
                    .background(Color.White)
            )

            // Small secondary specular highlight
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(bottom = pupilSize * 0.15f, start = pupilSize * 0.15f)
                    .size(pupilSize * 0.18f)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.85f))
            )
        }
    }
}
