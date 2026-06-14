package com.example.musik.ui.theme

import com.example.musik.R
import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalTextApi::class)
val SourGummy = FontFamily(
    // ---===---  Regular  ---===---
    Font(resId = R.font.sourgummy_regular, weight = FontWeight.W100, variationSettings = FontVariation.Settings(FontVariation.weight(100))),
    Font(resId = R.font.sourgummy_regular, weight = FontWeight.W200, variationSettings = FontVariation.Settings(FontVariation.weight(200))),
    Font(resId = R.font.sourgummy_regular, weight = FontWeight.W300, variationSettings = FontVariation.Settings(FontVariation.weight(300))),
    Font(resId = R.font.sourgummy_regular, weight = FontWeight.W400, variationSettings = FontVariation.Settings(FontVariation.weight(400))),
    Font(resId = R.font.sourgummy_regular, weight = FontWeight.W500, variationSettings = FontVariation.Settings(FontVariation.weight(500))),
    Font(resId = R.font.sourgummy_regular, weight = FontWeight.W600, variationSettings = FontVariation.Settings(FontVariation.weight(600))),
    Font(resId = R.font.sourgummy_regular, weight = FontWeight.W700, variationSettings = FontVariation.Settings(FontVariation.weight(700))),
    Font(resId = R.font.sourgummy_regular, weight = FontWeight.W800, variationSettings = FontVariation.Settings(FontVariation.weight(800))),
    Font(resId = R.font.sourgummy_regular, weight = FontWeight.W900, variationSettings = FontVariation.Settings(FontVariation.weight(900))),

    // ---===---  Italics  ---===---
    Font(resId = R.font.sourgummy_italics, weight = FontWeight.W100, variationSettings = FontVariation.Settings(FontVariation.weight(100))),
    Font(resId = R.font.sourgummy_italics, weight = FontWeight.W200, variationSettings = FontVariation.Settings(FontVariation.weight(200))),
    Font(resId = R.font.sourgummy_italics, weight = FontWeight.W300, variationSettings = FontVariation.Settings(FontVariation.weight(300))),
    Font(resId = R.font.sourgummy_italics, weight = FontWeight.W400, variationSettings = FontVariation.Settings(FontVariation.weight(400))),
    Font(resId = R.font.sourgummy_italics, weight = FontWeight.W500, variationSettings = FontVariation.Settings(FontVariation.weight(500))),
    Font(resId = R.font.sourgummy_italics, weight = FontWeight.W600, variationSettings = FontVariation.Settings(FontVariation.weight(600))),
    Font(resId = R.font.sourgummy_italics, weight = FontWeight.W700, variationSettings = FontVariation.Settings(FontVariation.weight(700))),
    Font(resId = R.font.sourgummy_italics, weight = FontWeight.W800, variationSettings = FontVariation.Settings(FontVariation.weight(800))),
    Font(resId = R.font.sourgummy_italics, weight = FontWeight.W900, variationSettings = FontVariation.Settings(FontVariation.weight(900))),
)

// Set of Material typography styles to start with
val Typography = Typography(
    titleLarge = TextStyle( // App watermark
        fontFamily = SourGummy,
        fontWeight = FontWeight.W900,
        fontSize = 30.sp
    ),
    titleMedium = TextStyle( // Tabs
        fontFamily = SourGummy,
        fontSize = 15.sp
    ),
    bodyLarge = TextStyle( // Song title
        fontFamily = SourGummy,
        fontWeight = FontWeight.W400,
        fontSize = 15.sp
    ),
    bodyMedium = TextStyle( // Song artist
        fontFamily = SourGummy,
        fontWeight = FontWeight.W300,
        fontSize = 13.sp
    ),
    bodySmall = TextStyle( // Song duration
        fontFamily = SourGummy,
        fontWeight = FontWeight.W300,
        fontSize = 13.sp
    ),

    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)