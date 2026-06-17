package com.example.musik.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class AppTheme {
    NIGHT
}

// "darkColorScheme"/"lightColorScheme" give you default colours, and can be overridden like below.
private val NightColorScheme = darkColorScheme(
    primary = Color(0xFF000000), // Default
    secondary = Color(0xFF282834), // Containers
    background = Color(0xFF1D1D26), // Background
    onPrimary = Color(0xFFC6C6E7), // Title text
    onSecondary = Color(0xFFFFFFFF), // General text and buttons
    onTertiary = Color(0xFF9E9E9E), // Duration text
    onSurfaceVariant = Color(0xFF999999), // Artist text
    surface = Color(0xFF353549), // Version text
    onSurface = Color(0xFF15151C) // Shadows

    /* Other default colours to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun MusikTheme(
    appTheme: AppTheme = AppTheme.NIGHT,
    content: @Composable () -> Unit
) {
    val colorScheme = when (appTheme) {
        AppTheme.NIGHT -> NightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}