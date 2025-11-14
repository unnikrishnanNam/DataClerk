package com.unnikrishnan.dataclerk.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = AccentPrimary,
    onPrimary = BackgroundDark,
    primaryContainer = SurfaceElevated1,
    onPrimaryContainer = TextPrimary,
    
    secondary = AccentSecondary,
    onSecondary = BackgroundDark,
    secondaryContainer = SurfaceElevated1,
    onSecondaryContainer = TextPrimary,
    
    tertiary = AccentInfo,
    onTertiary = BackgroundDark,
    tertiaryContainer = SurfaceElevated1,
    onTertiaryContainer = TextPrimary,
    
    background = BackgroundDark,
    onBackground = TextPrimary,
    
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondary,
    
    surfaceTint = AccentPrimary,
    
    inverseSurface = TextPrimary,
    inverseOnSurface = BackgroundDark,
    
    error = AccentError,
    onError = BackgroundDark,
    errorContainer = SurfaceElevated1,
    onErrorContainer = AccentError,
    
    outline = BorderColor,
    outlineVariant = DividerColor,
    
    scrim = OverlayDark
)

private val LightColorScheme = lightColorScheme(
    primary = AccentPrimary,
    secondary = AccentSecondary,
    tertiary = AccentInfo
)

@Composable
fun DataClerkTheme(
    darkTheme: Boolean = true, // Always use dark theme
    // Dynamic color is disabled for consistent branding
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
