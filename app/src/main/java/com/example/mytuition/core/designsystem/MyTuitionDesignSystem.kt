package com.example.mytuition.core.designsystem

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object MyTuitionColors {
    val WarmIvory = Color(0xFFFBF9F6)
    val PremiumPurple = Color(0xFF7B52FF)
    val PremiumLime = Color(0xFFD4FF26)
    val PremiumCoral = Color(0xFFFF8080)
    val PremiumBlue = Color(0xFF90D0FF)
    val PremiumLavender = Color(0xFFD2B0FF)
    val DeepNavyText = Color(0xFF13131A)
    val WhiteSurface = Color(0xFFFFFFFF)
    val TranslucentWhite = Color.White.copy(alpha = 0.6f)
    val SubtleBorder = DeepNavyText.copy(alpha = 0.05f)
}

object MyTuitionTypography {
    val Display = TextStyle(
        fontWeight = FontWeight.Black,
        fontSize = 32.sp,
        letterSpacing = (-1).sp
    )
    val LargeTitle = TextStyle(
        fontWeight = FontWeight.ExtraBold,
        fontSize = 20.sp,
        letterSpacing = (-0.5).sp
    )
    val SectionTitle = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
    )
    val Body = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    )
    val Metadata = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp
    )
    val Caption = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 10.sp,
        letterSpacing = 0.5.sp
    )
}

object MyTuitionShapes {
    val Small = RoundedCornerShape(12.dp)
    val Medium = RoundedCornerShape(16.dp)
    val Large = RoundedCornerShape(24.dp)
    val ExtraLarge = RoundedCornerShape(32.dp)
    val Capsule = RoundedCornerShape(50)
}

object MyTuitionSpacing {
    val ExtraSmall = 4.dp
    val Small = 8.dp
    val Medium = 12.dp
    val Large = 16.dp
    val ExtraLarge = 24.dp
    val Huge = 32.dp
    val BottomNavPadding = 140.dp
}

object MyTuitionElevation {
    val None = 0.dp
    val Soft = 4.dp
    val Floating = 16.dp
    val HighFloating = 24.dp
}

object MyTuitionMotion {
    val StandardSpring = spring<Float>(dampingRatio = 0.7f, stiffness = 400f)
    val BouncySpring = spring<Float>(dampingRatio = 0.6f, stiffness = 300f)
}
