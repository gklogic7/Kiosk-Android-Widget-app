package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.model.KioskSkin

private val NothingColorScheme = darkColorScheme(
    primary = NothingCrimsonRed,
    onPrimary = Color.White,
    secondary = NothingStarkWhite,
    onSecondary = NothingPitchBlack,
    tertiary = NothingMutedGray,
    background = NothingPitchBlack,
    onBackground = NothingStarkWhite,
    surface = NothingCharcoal,
    onSurface = NothingStarkWhite,
    surfaceVariant = NothingCharcoalElevated,
    onSurfaceVariant = NothingMutedGray
)

private val XpLunaColorScheme = lightColorScheme(
    primary = XpLunaTitleStart,
    onPrimary = Color.White,
    secondary = XpLunaStartGreenTop,
    onSecondary = Color.White,
    tertiary = XpLunaTrayBlue,
    background = XpLunaSkyTop,
    onBackground = Color(0xFF111111),
    surface = XpLunaWindowFace,
    onSurface = Color(0xFF111111),
    surfaceVariant = Color(0xFFDCD8C0),
    onSurfaceVariant = Color(0xFF42413C)
)

private val XpClassicColorScheme = lightColorScheme(
    primary = XpClassicTitleStart,
    onPrimary = Color.White,
    secondary = XpClassicTealDesktop,
    onSecondary = Color.White,
    tertiary = XpClassicDarkShadow,
    background = XpClassicTealDesktop,
    onBackground = Color.Black,
    surface = XpClassicFace,
    onSurface = Color.Black,
    surfaceVariant = Color(0xFFC0BCB4),
    onSurfaceVariant = Color(0xFF333333)
)

@Composable
fun KioskTheme(
    skin: KioskSkin = KioskSkin.NOTHING_DOT_MATRIX,
    content: @Composable () -> Unit
) {
    val colorScheme = when (skin) {
        KioskSkin.NOTHING_DOT_MATRIX -> NothingColorScheme
        KioskSkin.WIN_XP_LUNA -> XpLunaColorScheme
        KioskSkin.WIN_XP_CLASSIC -> XpClassicColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    KioskTheme(skin = KioskSkin.NOTHING_DOT_MATRIX, content = content)
}
