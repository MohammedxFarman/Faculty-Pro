package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val GlassCardShape = RoundedCornerShape(20.dp)

@Composable
fun glassBorder(isDark: Boolean = isSystemInDarkTheme()): BorderStroke {
    return BorderStroke(1.dp, if (isDark) Color.White.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.55f))
}

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryIndigo,
    secondary = SecondaryTeal,
    tertiary = AccentEmerald,
    background = SlateBackgroundDark,
    surface = SlateSurfaceDark.copy(alpha = 0.68f),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = DarkTextPrimary,
    onSurface = DarkTextPrimary,
    error = DestructiveRose
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryNavy,
    secondary = SecondaryTeal,
    tertiary = AccentEmerald,
    background = SlateBackgroundLight,
    surface = SlateSurfaceLight.copy(alpha = 0.72f),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = LightTextPrimary,
    onSurface = LightTextPrimary,
    error = DestructiveRose
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
