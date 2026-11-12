package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

/**
 * Supported Theme modes for NexusPulse.
 */
enum class ThemeMode(val displayName: String) {
    DARK("Dark"),
    LIGHT("Light"),
    SYSTEM("System Default")
}

/**
 * Modern Executive Dark Color Scheme based on deep slate obsidian surfaces
 * with vibrant electric cyan, violet, and hyper-blue highlights.
 */
val ModernExecutiveDarkColorScheme = darkColorScheme(
    primary = RawDarkCyanAccent,
    onPrimary = RawDarkObsidianBg,
    primaryContainer = RawDarkSlateSurface,
    onPrimaryContainer = RawDarkCyanAccent,
    secondary = RawDarkVioletAccent,
    onSecondary = Color.White,
    secondaryContainer = RawDarkCardSurface,
    onSecondaryContainer = RawDarkVioletAccent,
    tertiary = RawDarkHyperBlue,
    onTertiary = Color.White,
    tertiaryContainer = RawDarkElevatedSurface,
    onTertiaryContainer = RawDarkCyanAccent,
    background = RawDarkObsidianBg,
    onBackground = RawDarkTextPrimary,
    surface = RawDarkSlateSurface,
    onSurface = RawDarkTextPrimary,
    surfaceVariant = RawDarkCardSurface,
    onSurfaceVariant = RawDarkTextSecondary,
    surfaceTint = RawDarkCyanAccent,
    inverseSurface = RawDarkTextPrimary,
    inverseOnSurface = RawDarkObsidianBg,
    outline = RawDarkBorderStroke,
    outlineVariant = RawDarkElevatedSurface,
    error = StatusRose,
    onError = Color.White,
    errorContainer = Color(0x33EF4444),
    onErrorContainer = StatusRose
)

/**
 * Modern Executive Light Color Scheme featuring clean luminous surfaces,
 * crisp obsidian charcoal typography, and rich ocean cyan and royal violet accents.
 */
val ModernExecutiveLightColorScheme = lightColorScheme(
    primary = RawLightCyanAccent,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F2FE),
    onPrimaryContainer = Color(0xFF0369A1),
    secondary = RawLightVioletAccent,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEDE9FE),
    onSecondaryContainer = Color(0xFF5B21B6),
    tertiary = RawLightHyperBlue,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFDBEAFE),
    onTertiaryContainer = Color(0xFF1D4ED8),
    background = RawLightObsidianBg,
    onBackground = RawLightTextPrimary,
    surface = RawLightSlateSurface,
    onSurface = RawLightTextPrimary,
    surfaceVariant = RawLightCardSurface,
    onSurfaceVariant = RawLightTextSecondary,
    surfaceTint = RawLightCyanAccent,
    inverseSurface = RawDarkObsidianBg,
    inverseOnSurface = Color.White,
    outline = RawLightBorderStroke,
    outlineVariant = RawLightElevatedSurface,
    error = StatusRoseLight,
    onError = Color.White,
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = StatusRoseLight
)

/**
 * CompositionLocal providing access to custom vibrant accents and social platform colors.
 */
val LocalExecutiveAccents = staticCompositionLocalOf { DarkExecutiveAccents }

/**
 * Extension property providing direct access to custom executive accents from MaterialTheme.
 */
val MaterialTheme.executiveAccents: ExecutiveAccents
    @Composable
    @ReadOnlyComposable
    get() = LocalExecutiveAccents.current

/**
 * Main theme for NexusPulse.
 *
 * Supports both Executive Dark and Executive Light themes, with dynamic color support
 * on Android 12+ (API 31+).
 *
 * @param darkTheme Controls whether Dark or Light mode is active.
 * @param dynamicColor When true on Android 12+, integrates system dynamic wallpaper tones.
 * @param content The composable content hierarchy to theme.
 */
@Composable
fun NexusPulseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme: ColorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) {
                dynamicDarkColorScheme(context).copy(
                    background = RawDarkObsidianBg,
                    surface = RawDarkSlateSurface,
                    surfaceVariant = RawDarkCardSurface,
                    outline = RawDarkBorderStroke,
                    outlineVariant = RawDarkElevatedSurface,
                    primary = RawDarkCyanAccent,
                    onPrimary = RawDarkObsidianBg,
                    secondary = RawDarkVioletAccent,
                    onSecondary = Color.White,
                    tertiary = RawDarkHyperBlue,
                    onTertiary = Color.White,
                    error = StatusRose,
                    onError = Color.White
                )
            } else {
                dynamicLightColorScheme(context).copy(
                    background = RawLightObsidianBg,
                    surface = RawLightSlateSurface,
                    surfaceVariant = RawLightCardSurface,
                    outline = RawLightBorderStroke,
                    outlineVariant = RawLightElevatedSurface,
                    primary = RawLightCyanAccent,
                    onPrimary = Color.White,
                    secondary = RawLightVioletAccent,
                    onSecondary = Color.White,
                    tertiary = RawLightHyperBlue,
                    onTertiary = Color.White,
                    error = StatusRoseLight,
                    onError = Color.White
                )
            }
        }
        darkTheme -> ModernExecutiveDarkColorScheme
        else -> ModernExecutiveLightColorScheme
    }

    val customAccents = if (darkTheme) DarkExecutiveAccents else LightExecutiveAccents

    CompositionLocalProvider(LocalExecutiveAccents provides customAccents) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

/**
 * Compatibility alias matching template defaults.
 */
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    NexusPulseTheme(
        darkTheme = darkTheme,
        dynamicColor = dynamicColor,
        content = content
    )
}
