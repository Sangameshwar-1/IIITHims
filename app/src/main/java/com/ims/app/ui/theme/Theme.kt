package com.ims.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * IMS dark color scheme using the custom palette.
 * The app uses a premium dark theme exclusively.
 */
private val ImsDarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = PrimaryLight,
    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = Secondary,
    tertiary = Tertiary,
    tertiaryContainer = TertiaryContainer,
    onTertiaryContainer = Tertiary,
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceVariant,
    error = Error,
    onError = OnPrimary,
    errorContainer = ErrorContainer,
    outline = Outline,
    outlineVariant = Border
)

/**
 * IMS app theme applying dark color scheme, custom typography,
 * and edge-to-edge status bar configuration.
 */
@Composable
fun ImsTheme(content: @Composable () -> Unit) {
    val colorScheme = ImsDarkColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = ImsTypography,
        content = content
    )
}
