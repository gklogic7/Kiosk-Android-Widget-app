package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

val DotMatrixFontFamily = FontFamily(
    Font(R.font.vt323, FontWeight.Normal)
)

val TelemetryMonoFontFamily = FontFamily(
    Font(R.font.share_tech_mono, FontWeight.Normal)
)

val EditorialSerifFontFamily = FontFamily(
    Font(R.font.playfair_display, FontWeight.Normal),
    Font(R.font.playfair_display, FontWeight.Bold)
)

val CleanSansFontFamily = FontFamily(
    Font(R.font.ibm_plex_sans, FontWeight.Normal),
    Font(R.font.ibm_plex_sans, FontWeight.Medium),
    Font(R.font.ibm_plex_sans, FontWeight.SemiBold),
    Font(R.font.ibm_plex_sans, FontWeight.Bold)
)

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = DotMatrixFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 54.sp,
        lineHeight = 56.sp
    ),
    displayMedium = TextStyle(
        fontFamily = EditorialSerifFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 44.sp,
        lineHeight = 48.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = TelemetryMonoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
        lineHeight = 36.sp
    ),
    titleLarge = TextStyle(
        fontFamily = CleanSansFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 26.sp
    ),
    titleMedium = TextStyle(
        fontFamily = CleanSansFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.4.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = CleanSansFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = CleanSansFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp
    ),
    labelLarge = TextStyle(
        fontFamily = TelemetryMonoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 16.sp,
        letterSpacing = 1.1.sp
    ),
    labelSmall = TextStyle(
        fontFamily = TelemetryMonoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.8.sp
    )
)
