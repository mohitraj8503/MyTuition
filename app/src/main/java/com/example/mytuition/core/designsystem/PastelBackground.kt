package com.example.mytuition.core.designsystem

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun PastelBackground(
    modifier: Modifier = Modifier,
    blobColors: List<Color> = listOf(
        MyTuitionColors.BlobMint,
        MyTuitionColors.BlobPeach,
        MyTuitionColors.BlobLightBlue
    ),
    blobPositions: List<Offset> = listOf(
        Offset(0.1f, 0.05f),  // top-left
        Offset(0.85f, 0.15f), // top-right
        Offset(0.5f, 0.8f)    // bottom-center
    ),
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MyTuitionColors.ScreenBackground)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            for (i in blobColors.indices) {
                val color = blobColors[i]
                val normalizedPos = if (i < blobPositions.size) blobPositions[i] else Offset(0.5f, 0.5f)
                val center = Offset(normalizedPos.x * width, normalizedPos.y * height)
                val radius = (width.coerceAtLeast(height) * 0.42f)

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            color.copy(alpha = 0.25f),
                            color.copy(alpha = 0.12f),
                            Color.Transparent
                        ),
                        center = center,
                        radius = radius
                    ),
                    radius = radius,
                    center = center
                )
            }
        }

        content()
    }
}
