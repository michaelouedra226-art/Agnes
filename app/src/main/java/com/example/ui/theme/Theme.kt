package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AtelierAccent,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF2E245E),
    onPrimaryContainer = Color(0xFFD4CAFF),
    secondary = AtelierAccent,
    onSecondary = Color.White,
    background = AtelierBgDark,
    onBackground = AtelierTextPrimary,
    surface = AtelierSurfaceDark,
    onSurface = AtelierTextPrimary,
    surfaceVariant = AtelierSurfaceVariantDark,
    onSurfaceVariant = AtelierTextSecondary,
    outline = AtelierBorderDark,
    error = AtelierError,
    onError = Color.Black
)

private val LightColorScheme = lightColorScheme(
    primary = AtelierAccent,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEADBFF),
    onPrimaryContainer = Color(0xFF22005D),
    secondary = AtelierAccent,
    onSecondary = Color.White,
    background = AtelierBgLight,
    onBackground = AtelierTextPrimaryLight,
    surface = AtelierSurfaceLight,
    onSurface = AtelierTextPrimaryLight,
    surfaceVariant = AtelierSurfaceVariantLight,
    onSurfaceVariant = AtelierTextSecondaryLight,
    outline = AtelierBorderLight,
    error = AtelierError,
    onError = Color.White
)

@Composable
fun AtelierTheme(
    darkTheme: Boolean = true, // Force dark luxury theme by default as per Cahier des Charges
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
