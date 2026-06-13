package com.example.musik.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import com.example.musik.R
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val SourGummy = FontFamily(
    Font(R.font.sourgummy_regular),
    Font(R.font.sourgummy_italics)
)

// Set of Material typography styles to start with
val Typography = Typography(
    titleLarge = TextStyle( // App watermark
        fontFamily = SourGummy,
        fontWeight = FontWeight.W900,
        fontSize = 30.sp
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