package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = BentoPrimary,
    onPrimary = BentoOnPrimary,
    primaryContainer = BentoPrimaryContainer,
    onPrimaryContainer = BentoOnPrimaryContainer,
    secondary = BentoStreakContainer,
    onSecondary = BentoOnStreak,
    secondaryContainer = BentoStreakContainer,
    onSecondaryContainer = BentoOnStreak,
    tertiary = BentoBadgeContainer,
    onTertiary = BentoOnBadge,
    tertiaryContainer = BentoBadgeContainer,
    onTertiaryContainer = BentoOnBadge,
    background = BentoBackground,
    onBackground = BentoTextPrimary,
    surface = BentoSurface,
    onSurface = BentoTextPrimary,
    surfaceVariant = BentoSurfaceVariant,
    onSurfaceVariant = BentoTextSecondary,
    outline = BentoBorder,
    outlineVariant = BentoBorderSubtle,
    error = CoralError,
    onError = BentoOnPrimary
)

private val DarkColorScheme = darkColorScheme(
    primary = BentoPrimaryContainer,
    onPrimary = BentoOnPrimaryContainer,
    primaryContainer = BentoPrimary,
    onPrimaryContainer = BentoOnPrimary,
    secondary = BentoStreakContainer,
    onSecondary = BentoOnStreak,
    secondaryContainer = Color(0xFF041E49),
    onSecondaryContainer = BentoStreakContainer,
    tertiary = BentoBadgeContainer,
    onTertiary = BentoOnBadge,
    tertiaryContainer = Color(0xFF31111D),
    onTertiaryContainer = BentoBadgeContainer,
    background = DarkBg,
    onBackground = DarkText,
    surface = DarkSurface,
    onSurface = DarkText,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextMuted,
    outline = DarkBorder,
    outlineVariant = Color(0xFF332D37),
    error = CoralError,
    onError = Color.White
)

val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false, // Keep cheerful high-contrast Bento Light palette by default so text and inputs never wash out
    dynamicColor: Boolean = false, // Keep cheerful custom branding
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = AppShapes,
        content = content
    )
}
