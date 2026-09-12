package com.example.mytuition.core.designsystem

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

val LightColorScheme = lightColorScheme(
    primary = MyTuitionColors.PrimaryPurple,
    onPrimary = MyTuitionColors.TextOnPurple,
    primaryContainer = MyTuitionColors.PrimaryPurpleLight,
    onPrimaryContainer = MyTuitionColors.PrimaryPurpleDark,
    secondary = MyTuitionColors.PrimaryPurpleDark,
    onSecondary = MyTuitionColors.TextOnPurple,
    secondaryContainer = MyTuitionColors.PrimaryPurpleGhost,
    onSecondaryContainer = MyTuitionColors.PrimaryPurple,
    background = MyTuitionColors.ScreenBackground,
    onBackground = MyTuitionColors.TextPrimary,
    surface = MyTuitionColors.CardWhite,
    onSurface = MyTuitionColors.TextPrimary,
    surfaceVariant = MyTuitionColors.SurfaceVariant,
    onSurfaceVariant = MyTuitionColors.TextSecondary,
    outline = MyTuitionColors.DividerColor,
    error = MyTuitionColors.StatusRed
)

@Composable
fun MyTuitionTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}
