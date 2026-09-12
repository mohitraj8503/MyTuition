package com.example.mytuition.core.designsystem

import androidx.compose.animation.core.spring
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// === COLOR DARKEN HELPER (for Clay Borders) ===
fun Color.darken(factor: Float = 0.08f): Color {
    return Color(
        red = (red * (1f - factor)).coerceIn(0f, 1f),
        green = (green * (1f - factor)).coerceIn(0f, 1f),
        blue = (blue * (1f - factor)).coerceIn(0f, 1f),
        alpha = alpha
    )
}

// === 1.1 COLOR PALETTE — FRESH CLAY CANDY PALETTE ===
object MyTuitionColors {
    // === PRIMARY BRAND ===
    val PrimaryPurple       = Color(0xFF6C48FF)  // Main purple CTA, active pills, accents
    val PrimaryPurpleDark   = Color(0xFF5538CC)  // 2dp border on primary buttons
    val PrimaryPurpleLight  = Color(0xFFEDE7FF)  // Active nav pill, subtle accents
    val PrimaryPurpleGhost  = Color(0xFFF5F2FF)

    // === BACKGROUNDS ===
    val ScreenBackground    = Color(0xFFF4F6FA)  // Deeper off-white so white clay cards pop
    val CardWhite           = Color(0xFFFFFFFF)  // Base white clay card
    val CardMint            = Color(0xFFE3F8EC)  // Mint clay card
    val CardPeach           = Color(0xFFFFE9DB)  // Peach clay card
    val CardLavender        = Color(0xFFEFE9FF)  // Lavender clay card (featured class card)
    val CardSky             = Color(0xFFE3F0FF)  // Sky clay card
    val CardLemon           = Color(0xFFFFF6D9)  // Lemon clay card
    val CardLightPink       = Color(0xFFFFEBF5)  // Light pink variant
    val CardLightBlue       = CardSky
    val CardLightYellow     = CardLemon
    val CardLightPurple     = CardLavender
    val CardLightMint       = CardMint

    val SurfaceVariant      = Color(0xFFEAE8F2)  // Clay input fields, inactive chips
    val DividerColor        = Color(0xFFE6E3EE)  // Thin dividers

    // === TEXT (WARM INDIGO PALETTE - FRIENDLY FOR KIDS & PARENTS) ===
    val TextPrimary         = Color(0xFF221A44)  // Warm dark indigo
    val TextSecondary       = Color(0xFF6E6A8F)  // Soft indigo-grey
    val TextTertiary        = Color(0xFF9B97B8)  // Caption / hint
    val TextOnPurple        = Color(0xFFFFFFFF)

    // === PASTEL BLOBS (30% opacity for vibrant freshness) ===
    val BlobMint            = Color(0xFFB8F2D8)
    val BlobPeach           = Color(0xFFFFDDC7)
    val BlobLightBlue       = Color(0xFFC7E0FF)
    val BlobLavender        = Color(0xFFD2B0FF)
    val BlobPaleYellow      = Color(0xFFFDF3C0)

    // === SUBJECT COLORS ===
    val SubjectMath         = Color(0xFF34C759)  // Green
    val SubjectChemistry    = Color(0xFFFF9500)  // Orange
    val SubjectEnglish      = Color(0xFF007AFF)  // Blue
    val SubjectPhysics      = Color(0xFF6C48FF)  // Purple
    val SubjectHistory      = Color(0xFFFFB340)  // Amber
    val SubjectGeometry     = Color(0xFF9C27B0)  // Magenta
    val SubjectBiology      = Color(0xFF00BFA5)  // Teal
    val SubjectBio          = SubjectBiology

    // === STATUS COLORS ===
    val StatusGreen         = Color(0xFF34C759)
    val StatusAmber         = Color(0xFFFFB340)
    val StatusOrange        = Color(0xFFFF9500)
    val StatusRed           = Color(0xFFFF3B30)
    val StatusGreenDot      = Color(0xFF34C759)
    val OnlineGreen         = StatusGreen

    // === DIFFICULTY / RATING DOTS ===
    val DifficultyDot       = Color(0xFFFFB340)
}

// === 1.2 NEW TYPOGRAPHY SCALE — BIG, BOLD, KID-FRIENDLY (NO TEXT < 12sp) ===
val Poppins = FontFamily.SansSerif
val Caveat = FontFamily.Cursive

object MyTuitionTypography {
    val HeadlineXLarge = TextStyle(
        fontFamily = Poppins,
        fontSize = 36.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = (-0.5).sp,
        lineHeight = 44.sp,
        color = MyTuitionColors.TextPrimary
    )
    val HeadlineLarge = TextStyle(
        fontFamily = Poppins,
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = (-0.5).sp,
        lineHeight = 38.sp,
        color = MyTuitionColors.TextPrimary
    )
    val HeadlineMedium = TextStyle(
        fontFamily = Poppins,
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = (-0.5).sp,
        lineHeight = 34.sp,
        color = MyTuitionColors.TextPrimary
    )
    val HeadlineSmall = TextStyle(
        fontFamily = Poppins,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = (-0.4).sp,
        lineHeight = 30.sp,
        color = MyTuitionColors.TextPrimary
    )
    val TitleExtra = TextStyle(
        fontFamily = Poppins,
        fontSize = 24.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = (-0.4).sp,
        lineHeight = 30.sp,
        color = MyTuitionColors.TextPrimary
    )
    val TitleLarge = TextStyle(
        fontFamily = Poppins,
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = (-0.3).sp,
        lineHeight = 26.sp,
        color = MyTuitionColors.TextPrimary
    )
    val TitleMedium = TextStyle(
        fontFamily = Poppins,
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = (-0.2).sp,
        lineHeight = 24.sp,
        color = MyTuitionColors.TextPrimary
    )
    val TitleSmall = TextStyle(
        fontFamily = Poppins,
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = (-0.1).sp,
        lineHeight = 22.sp,
        color = MyTuitionColors.TextPrimary
    )
    val BodyLarge = TextStyle(
        fontFamily = Poppins,
        fontSize = 17.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 23.sp,
        color = MyTuitionColors.TextPrimary
    )
    val BodyMedium = TextStyle(
        fontFamily = Poppins,
        fontSize = 15.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 20.sp,
        color = MyTuitionColors.TextSecondary
    )
    val LabelLarge = TextStyle(
        fontFamily = Poppins,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.2.sp,
        color = MyTuitionColors.TextPrimary
    )
    val LabelMedium = TextStyle(
        fontFamily = Poppins,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.2.sp,
        color = MyTuitionColors.TextSecondary
    )
    val LabelSmall = TextStyle(
        fontFamily = Poppins,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.2.sp,
        color = MyTuitionColors.TextTertiary
    )
    val BodySmall = LabelSmall
    val Handwritten = TextStyle(
        fontFamily = Caveat,
        fontSize = 26.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 30.sp,
        color = MyTuitionColors.TextPrimary
    )
}

// === 1.3 SHAPES & CORNER RADIUS TOKENS ===
object MyTuitionShapes {
    val CardRadiusLarge    = 32.dp  // Featured class card, onboarding hero card, profile card
    val CardRadiusMedium   = 28.dp  // Subject cards, timeline cards, lesson cards, homework cards
    val CardRadiusSmall    = 20.dp  // Small cards, pills, info chips
    val ButtonRadiusLarge  = 28.dp  // Primary CTA buttons (pill shape)
    val ButtonRadiusSmall  = 20.dp  // Small buttons, secondary buttons
    val NavRadius          = 32.dp  // Floating bottom nav bar
    val InputRadius        = 18.dp  // Text input fields
    val PillRadius         = 999.dp // Fully rounded pills, chips
    val IconRadius         = 16.dp  // Puffy icon containers
    val DateChipRadius     = 20.dp  // Date picker items

    val CardLargeShape     = RoundedCornerShape(CardRadiusLarge)
    val CardMediumShape    = RoundedCornerShape(CardRadiusMedium)
    val CardSmallShape     = RoundedCornerShape(CardRadiusSmall)
    val ButtonPillShape    = RoundedCornerShape(ButtonRadiusLarge)
    val NavShape           = RoundedCornerShape(NavRadius)
    val InputShape         = RoundedCornerShape(InputRadius)
    val PillShape          = RoundedCornerShape(PillRadius)
    val IconShape          = RoundedCornerShape(IconRadius)
    val DateChipShape      = RoundedCornerShape(DateChipRadius)
}

// === 1.4 SPACING SYSTEM ===
object MyTuitionSpacing {
    val spacing_xs    = 4.dp
    val spacing_sm    = 8.dp
    val spacing_md    = 12.dp
    val spacing_lg    = 16.dp
    val spacing_xl    = 20.dp
    val spacing_xxl   = 24.dp
    val spacing_xxxl  = 32.dp

    val xs = spacing_xs
    val sm = spacing_sm
    val md = spacing_md
    val lg = spacing_lg
    val xl = spacing_xl
    val xxl = spacing_xxl
    val xxxl = spacing_xxxl

    val screenPadding = 20.dp
    val bottomNavHeight = 72.dp
    val bottomContentPadding = 110.dp
}

// === 1.5 ANIMATION SPECS (BOUNCY, SQUISHY CLAY) ===
object MyTuitionAnimations {
    val claySpring = spring<Float>(
        dampingRatio = 0.55f,
        stiffness = 380f
    )
    val claySpringDp = spring<androidx.compose.ui.unit.Dp>(
        dampingRatio = 0.55f,
        stiffness = 380f
    )
    val bounceSpring = spring<Float>(
        dampingRatio = 0.50f,
        stiffness = 300f
    )
    val bounceSpringDp = spring<androidx.compose.ui.unit.Dp>(
        dampingRatio = 0.50f,
        stiffness = 300f
    )
}
