package com.example.mytuition.core.designsystem.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.mytuition.core.designsystem.Caveat
import com.example.mytuition.core.designsystem.MyTuitionColors
import com.example.mytuition.core.designsystem.Poppins

@Composable
fun OnboardingSlide(
    illustrationUrl: String,
    headlinePrefix: String,
    headlineHighlight: String,
    subHeadline: String,
    primaryButtonText: String,           // "Continue" or "Get Started"
    currentPage: Int,                    // 0, 1, 2
    totalPages: Int = 3,
    onPrimaryClick: () -> Unit,
    onSecondaryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // TOP: Illustration + Organic Blobs + Overlapping Handwritten Text (in the SAME Box)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            contentAlignment = Alignment.Center
        ) {
            // FIX 2: Pastel blobs BEHIND circular photo — organic with radial gradients
            Canvas(modifier = Modifier.fillMaxSize()) {
                fun drawSoftBlob(color: Color, center: Offset, radius: Float) {
                    val brush = Brush.radialGradient(
                        colors = listOf(color.copy(alpha = 0.35f), color.copy(alpha = 0.0f)),
                        center = center,
                        radius = radius
                    )
                    drawCircle(brush = brush, center = center, radius = radius)
                }

                drawSoftBlob(
                    color = MyTuitionColors.BlobMint,
                    center = Offset(size.width * 0.35f, size.height * 0.45f),
                    radius = 160.dp.toPx()
                )
                drawSoftBlob(
                    color = MyTuitionColors.BlobPeach,
                    center = Offset(size.width * 0.72f, size.height * 0.30f),
                    radius = 120.dp.toPx()
                )
                drawSoftBlob(
                    color = MyTuitionColors.BlobLightBlue,
                    center = Offset(size.width * 0.28f, size.height * 0.72f),
                    radius = 110.dp.toPx()
                )
            }

            // FIX 3: Circular photo (NO chat bubble, NO overlays)
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .shadow(14.dp, CircleShape, spotColor = MyTuitionColors.PrimaryPurple.copy(alpha = 0.15f))
                    .clip(CircleShape)
                    .border(2.dp, Color.White, CircleShape)
                    .background(MyTuitionColors.PrimaryPurpleLight)
            ) {
                AsyncImage(
                    model = illustrationUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            // FIX 1: Handwritten text — TOP-RIGHT, OVERLAPPING the circle's upper-right edge
            Text(
                text = "Better\nLearning\nBrighter\nTomorrow",
                fontFamily = Caveat,
                fontSize = 22.sp,
                fontWeight = FontWeight.Normal,
                color = MyTuitionColors.TextPrimary,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 12.dp, top = 20.dp)
                    .rotate(-5f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // MIDDLE: Headline and Sub-headline
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // FIX 4: Headline with buildAnnotatedString
            val headline = buildAnnotatedString {
                append(headlinePrefix)
                withStyle(SpanStyle(color = MyTuitionColors.PrimaryPurple)) {
                    append(headlineHighlight)
                }
            }

            Text(
                text = headline,
                fontFamily = Poppins,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 36.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = subHeadline,
                fontFamily = Poppins,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = MyTuitionColors.TextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // BOTTOM: Action buttons & Pagination dots
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // FIX 6: Button layout
            // Left circular back button (hidden on slide 0) + Right pill button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val showBackButton = currentPage > 0

                if (showBackButton) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .shadow(2.dp, CircleShape, spotColor = Color.Black.copy(alpha = 0.05f))
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(1.dp, MyTuitionColors.PrimaryPurple, CircleShape)
                            .clickable(onClick = onSecondaryClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = MyTuitionColors.PrimaryPurple,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Right: PillButton fills remaining width
                Box(modifier = Modifier.weight(1f)) {
                    PillButton(
                        text = primaryButtonText,
                        onClick = onPrimaryClick,
                        icon = Icons.AutoMirrored.Rounded.ArrowForward,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // FIX 5: Pagination Dots (active: 24x8.dp rounded bar, inactive: 8x8.dp circle)
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 0 until totalPages) {
                    val isActive = i == currentPage
                    val dotWidth by animateDpAsState(
                        targetValue = if (isActive) 24.dp else 8.dp,
                        animationSpec = spring(stiffness = 300f),
                        label = "dotWidth"
                    )

                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .width(dotWidth)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (isActive) MyTuitionColors.PrimaryPurple else Color(0xFFD0D0D0)
                            )
                    )
                }
            }
        }
    }
}
