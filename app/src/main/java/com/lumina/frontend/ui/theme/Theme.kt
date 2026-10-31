package com.lumina.frontend.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = LuminaDarkPrimary,
    onPrimary = LuminaOnPrimary,
    primaryContainer = LuminaDarkSurfaceVariant,
    onPrimaryContainer = LuminaDarkPrimary,
    secondary = LuminaDarkSecondary,
    onSecondary = LuminaOnSecondary,
    background = LuminaDarkBackground,
    onBackground = LuminaSurface,
    surface = LuminaDarkSurface,
    onSurface = LuminaSurface,
    surfaceVariant = LuminaDarkSurfaceVariant,
    onSurfaceVariant = LuminaOutline,
    outline = LuminaOutlineVariant,
    error = LuminaError,
    onError = LuminaOnError
)

private val LightColorScheme = lightColorScheme(
    primary = LuminaPrimary,
    onPrimary = LuminaOnPrimary,
    primaryContainer = LuminaPrimaryContainer,
    onPrimaryContainer = LuminaOnPrimaryContainer,
    secondary = LuminaSecondary,
    onSecondary = LuminaOnSecondary,
    secondaryContainer = LuminaSecondaryContainer,
    onSecondaryContainer = LuminaOnSecondaryContainer,
    tertiary = LuminaTertiary,
    onTertiary = LuminaOnTertiary,
    tertiaryContainer = LuminaTertiaryContainer,
    onTertiaryContainer = LuminaOnTertiaryContainer,
    background = LuminaBackground,
    onBackground = LuminaOnBackground,
    surface = LuminaSurface,
    onSurface = LuminaOnSurface,
    surfaceVariant = LuminaSurfaceVariant,
    onSurfaceVariant = LuminaOnSurfaceVariant,
    outline = LuminaOutline,
    outlineVariant = LuminaOutlineVariant,
    error = LuminaError,
    onError = LuminaOnError
)

@Composable
fun LuminaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
