package com.kwiby.musik.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import com.kwiby.musik.ui.theme.colour_schemes.AppleDarkColourScheme
import com.kwiby.musik.ui.theme.colour_schemes.AppleLightColourScheme
import com.kwiby.musik.ui.theme.colour_schemes.FlamingoDarkColourScheme
import com.kwiby.musik.ui.theme.colour_schemes.FlamingoLightColourScheme
import com.kwiby.musik.ui.theme.colour_schemes.GrapeDarkColourScheme
import com.kwiby.musik.ui.theme.colour_schemes.GrapeLightColourScheme
import com.kwiby.musik.ui.theme.colour_schemes.IceDarkColourScheme
import com.kwiby.musik.ui.theme.colour_schemes.IceLightColourScheme
import com.kwiby.musik.ui.theme.colour_schemes.LemonDarkColourScheme
import com.kwiby.musik.ui.theme.colour_schemes.LemonLightColourScheme
import com.kwiby.musik.ui.theme.colour_schemes.LimeDarkColourScheme
import com.kwiby.musik.ui.theme.colour_schemes.LimeLightColourScheme
import com.kwiby.musik.ui.theme.colour_schemes.NightDarkColourScheme
import com.kwiby.musik.ui.theme.colour_schemes.NightLightColourScheme
import com.kwiby.musik.ui.theme.colour_schemes.SunsetDarkColourScheme
import com.kwiby.musik.ui.theme.colour_schemes.SunsetLightColourScheme

enum class ThemeMode {
    DARK,
    LIGHT;

    companion object {
        val DEFAULT = DARK
        fun fromString(value: String?): ThemeMode = entries.find { it.name == value } ?: DEFAULT
    }
}

enum class ThemeStyle {
    NIGHT,
    ICE,
    APPLE,
    LIME,
    SUNSET,
    LEMON,
    FLAMINGO,
    GRAPE;

    companion object {
        val DEFAULT = NIGHT
        fun fromString(value: String?): ThemeStyle = entries.find { it.name == value } ?: DEFAULT
    }
}

data class AppTheme(
    val mode: ThemeMode,
    val style: ThemeStyle
) {
    val name: String get() = "${style.name}_${mode.name}"

    companion object {
        val DEFAULT = AppTheme(ThemeMode.DEFAULT, ThemeStyle.DEFAULT)
    }
}
fun AppTheme.colorScheme(): ColorScheme = when (mode) {
    ThemeMode.DARK -> when (style) {
        ThemeStyle.NIGHT -> NightDarkColourScheme
        ThemeStyle.ICE -> IceDarkColourScheme
        ThemeStyle.APPLE -> AppleDarkColourScheme
        ThemeStyle.LIME -> LimeDarkColourScheme
        ThemeStyle.SUNSET -> SunsetDarkColourScheme
        ThemeStyle.LEMON -> LemonDarkColourScheme
        ThemeStyle.FLAMINGO -> FlamingoDarkColourScheme
        ThemeStyle.GRAPE -> GrapeDarkColourScheme
    }

    ThemeMode.LIGHT -> when (style) {
        ThemeStyle.NIGHT -> NightLightColourScheme
        ThemeStyle.ICE -> IceLightColourScheme
        ThemeStyle.APPLE -> AppleLightColourScheme
        ThemeStyle.LIME -> LimeLightColourScheme
        ThemeStyle.SUNSET -> SunsetLightColourScheme
        ThemeStyle.LEMON -> LemonLightColourScheme
        ThemeStyle.FLAMINGO -> FlamingoLightColourScheme
        ThemeStyle.GRAPE -> GrapeLightColourScheme
    }
}

val LocalAppTheme = staticCompositionLocalOf { AppTheme.DEFAULT }

@Composable
fun MusikTheme(
    appTheme: AppTheme = AppTheme.DEFAULT,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalAppTheme provides appTheme) {
        MaterialTheme(
            colorScheme = appTheme.colorScheme(),
            typography = Typography,
            content = content
        )
    }
}