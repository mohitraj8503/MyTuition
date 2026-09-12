package com.example.mytuition.core.designsystem

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

/**
 * High-performance PastelBackground:
 * Renders pastel gradient blobs ONCE into a cached bitmap instead of redrawing
 * expensive RadialGradient Canvas operations on every scroll / animation frame.
 * Retains 100% of the visual fidelity while eliminating 60/90/120Hz frame drops.
 */
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
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }.toInt().coerceAtLeast(1)
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }.toInt().coerceAtLeast(1)

    val backgroundImage = remember(blobColors, blobPositions, screenWidthPx, screenHeightPx) {
        val bitmap = Bitmap.createBitmap(screenWidthPx, screenHeightPx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Fill background color
        val bgPaint = Paint().apply { color = MyTuitionColors.ScreenBackground.toArgb() }
        canvas.drawRect(0f, 0f, screenWidthPx.toFloat(), screenHeightPx.toFloat(), bgPaint)

        // Pre-bake each pastel blob
        for (i in blobColors.indices) {
            val color = blobColors[i]
            val normalizedPos = if (i < blobPositions.size) blobPositions[i] else Offset(0.5f, 0.5f)
            val cx = normalizedPos.x * screenWidthPx
            val cy = normalizedPos.y * screenHeightPx
            val radius = screenWidthPx.coerceAtLeast(screenHeightPx) * 0.42f

            val shader = RadialGradient(
                cx, cy, radius,
                intArrayOf(
                    color.copy(alpha = 0.25f).toArgb(),
                    color.copy(alpha = 0.12f).toArgb(),
                    android.graphics.Color.TRANSPARENT
                ),
                floatArrayOf(0f, 0.5f, 1f),
                Shader.TileMode.CLAMP
            )
            val blobPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { this.shader = shader }
            canvas.drawCircle(cx, cy, radius, blobPaint)
        }

        bitmap.asImageBitmap()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MyTuitionColors.ScreenBackground)
    ) {
        Image(
            bitmap = backgroundImage,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        content()
    }
}
