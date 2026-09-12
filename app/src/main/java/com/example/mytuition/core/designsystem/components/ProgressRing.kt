package com.example.mytuition.core.designsystem.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mytuition.core.designsystem.MyTuitionColors
import com.example.mytuition.core.designsystem.MyTuitionTypography

@Composable
fun ProgressRing(
    completed: Int,   // 12
    total: Int,       // 18
    modifier: Modifier = Modifier
) {
    val targetFraction = if (total > 0) (completed.toFloat() / total).coerceIn(0f, 1f) else 0f
    var startAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(completed, total) {
        startAnimation = true
    }

    val animatedProgress by animateFloatAsState(
        targetValue = if (startAnimation) targetFraction else 0f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "progressRingAnim"
    )

    Box(
        modifier = modifier.size(120.dp),
        contentAlignment = Alignment.Center
    ) {
        val trackColor = MyTuitionColors.PrimaryPurpleLight
        val fillColor = MyTuitionColors.PrimaryPurple

        Canvas(modifier = Modifier.fillMaxSize().padding(10.dp)) {
            val strokeWidth = 10.dp.toPx()

            // Draw track
            drawArc(
                color = trackColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Draw progress fill (starts at top: 270 degrees)
            drawArc(
                color = fillColor,
                startAngle = 270f,
                sweepAngle = animatedProgress * 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "$completed / $total",
                style = MyTuitionTypography.TitleLarge.copy(
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MyTuitionColors.TextPrimary
                )
            )
            Text(
                text = "Done",
                style = MyTuitionTypography.LabelMedium.copy(
                    fontSize = 12.sp,
                    color = MyTuitionColors.TextSecondary
                )
            )
        }
    }
}
