package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DesktopTileConfig
import com.example.model.KioskSkin
import com.example.model.TileSpan
import com.example.model.TileSurfaceVariant
import com.example.ui.theme.CleanSansFontFamily
import com.example.ui.theme.NothingCharcoal
import com.example.ui.theme.NothingCrimsonRed
import com.example.ui.theme.NothingStarkWhite
import com.example.ui.theme.TelemetryMonoFontFamily
import com.example.ui.theme.XpClassicDarkShadow
import com.example.ui.theme.XpClassicFace
import com.example.ui.theme.XpClassicHighlight
import com.example.ui.theme.XpClassicLightShadow
import com.example.ui.theme.XpClassicTitleEnd
import com.example.ui.theme.XpClassicTitleStart
import com.example.ui.theme.XpLunaBorderBlue
import com.example.ui.theme.XpLunaCloseRed
import com.example.ui.theme.XpLunaTitleEnd
import com.example.ui.theme.XpLunaTitleStart
import com.example.ui.theme.XpLunaWindowFace

enum class RetroPixelIconType {
    CPU_CHIP,
    RAM_STICK,
    MY_COMPUTER,
    NETWORK_MONITORS,
    BATTERY_BOLT,
    HARD_DRIVE,
    CLOCK_TRAY,
    TASK_MANAGER
}

@Composable
fun RetroPixelIcon(
    type: RetroPixelIconType,
    modifier: Modifier = Modifier.size(18.dp)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        when (type) {
            RetroPixelIconType.CPU_CHIP -> {
                // Classic green/gold microchip icon
                drawRect(Color(0xFF006B3C), topLeft = Offset(w * 0.18f, h * 0.18f), size = Size(w * 0.64f, h * 0.64f))
                drawRect(Color(0xFFD4AF37), topLeft = Offset(w * 0.32f, h * 0.32f), size = Size(w * 0.36f, h * 0.36f))
                val pinColor = Color(0xFFE5E5E5)
                for (i in 0..2) {
                    val pos = w * (0.26f + i * 0.2f)
                    drawRect(pinColor, topLeft = Offset(pos, 0f), size = Size(w * 0.08f, h * 0.18f))
                    drawRect(pinColor, topLeft = Offset(pos, h * 0.82f), size = Size(w * 0.08f, h * 0.18f))
                    drawRect(pinColor, topLeft = Offset(0f, pos), size = Size(w * 0.18f, h * 0.08f))
                    drawRect(pinColor, topLeft = Offset(w * 0.82f, pos), size = Size(w * 0.18f, h * 0.08f))
                }
            }
            RetroPixelIconType.RAM_STICK -> {
                drawRect(Color(0xFF1A7F37), topLeft = Offset(w * 0.08f, h * 0.25f), size = Size(w * 0.84f, h * 0.45f))
                for (i in 0..3) {
                    drawRect(
                        Color(0xFF222222),
                        topLeft = Offset(w * (0.14f + i * 0.19f), h * 0.32f),
                        size = Size(w * 0.13f, h * 0.28f)
                    )
                }
                drawRect(Color(0xFFFFD700), topLeft = Offset(w * 0.12f, h * 0.70f), size = Size(w * 0.76f, h * 0.12f))
            }
            RetroPixelIconType.MY_COMPUTER -> {
                // CRT Monitor + Beige tower base
                drawRect(Color(0xFF808080), topLeft = Offset(w * 0.12f, h * 0.08f), size = Size(w * 0.76f, h * 0.62f))
                drawRect(Color(0xFF008080), topLeft = Offset(w * 0.2f, h * 0.16f), size = Size(w * 0.6f, h * 0.46f))
                drawRect(Color(0xFFD4D0C8), topLeft = Offset(w * 0.35f, h * 0.70f), size = Size(w * 0.3f, h * 0.12f))
                drawRect(Color(0xFFC0C0C0), topLeft = Offset(w * 0.18f, h * 0.82f), size = Size(w * 0.64f, h * 0.12f))
            }
            RetroPixelIconType.NETWORK_MONITORS -> {
                // Classic XP blinking dual network monitors
                drawRect(Color(0xFF003399), topLeft = Offset(w * 0.06f, h * 0.12f), size = Size(w * 0.52f, h * 0.44f))
                drawRect(Color(0xFF00FF66), topLeft = Offset(w * 0.12f, h * 0.18f), size = Size(w * 0.40f, h * 0.32f))
                drawRect(Color(0xFF003399), topLeft = Offset(w * 0.42f, h * 0.42f), size = Size(w * 0.52f, h * 0.44f))
                drawRect(Color(0xFF33CCFF), topLeft = Offset(w * 0.48f, h * 0.48f), size = Size(w * 0.40f, h * 0.32f))
            }
            RetroPixelIconType.BATTERY_BOLT -> {
                drawRect(Color(0xFF333333), topLeft = Offset(w * 0.35f, h * 0.06f), size = Size(w * 0.3f, h * 0.1f))
                drawRect(Color(0xFF2B5B84), topLeft = Offset(w * 0.22f, h * 0.16f), size = Size(w * 0.56f, h * 0.76f))
                drawRect(Color(0xFFFFD700), topLeft = Offset(w * 0.36f, h * 0.30f), size = Size(w * 0.28f, h * 0.48f))
            }
            RetroPixelIconType.HARD_DRIVE -> {
                drawRect(Color(0xFFB8B4AC), topLeft = Offset(w * 0.1f, h * 0.28f), size = Size(w * 0.8f, h * 0.46f))
                drawRect(Color(0xFF404040), topLeft = Offset(w * 0.16f, h * 0.56f), size = Size(w * 0.68f, h * 0.1f))
                drawCircle(Color(0xFF00FF00), radius = w * 0.07f, center = Offset(w * 0.76f, h * 0.42f))
            }
            RetroPixelIconType.CLOCK_TRAY -> {
                drawCircle(Color(0xFFF5F5DC), radius = w * 0.44f, center = Offset(w / 2f, h / 2f))
                drawLine(Color(0xFF0A246A), Offset(w / 2f, h / 2f), Offset(w / 2f, h * 0.2f), strokeWidth = 2.5f)
                drawLine(Color(0xFFD71921), Offset(w / 2f, h / 2f), Offset(w * 0.76f, h / 2f), strokeWidth = 2.5f)
            }
            RetroPixelIconType.TASK_MANAGER -> {
                drawRect(Color(0xFF000000), topLeft = Offset(w * 0.1f, h * 0.1f), size = Size(w * 0.8f, h * 0.8f))
                drawRect(Color(0xFF00FF00), topLeft = Offset(w * 0.22f, h * 0.52f), size = Size(w * 0.24f, h * 0.28f))
                drawRect(Color(0xFF00FF00), topLeft = Offset(w * 0.54f, h * 0.26f), size = Size(w * 0.24f, h * 0.54f))
            }
        }
    }
}

fun Modifier.xpClassic3DBevel(
    sunken: Boolean = false,
    strokeWidthDp: Dp = 2.dp
): Modifier = this.drawBehind {
    val stroke = strokeWidthDp.toPx()
    val topLeftColor = if (sunken) XpClassicDarkShadow else XpClassicHighlight
    val bottomRightColor = if (sunken) XpClassicHighlight else XpClassicDarkShadow
    val innerTopLeft = if (sunken) XpClassicLightShadow else XpClassicFace
    val innerBottomRight = if (sunken) XpClassicFace else XpClassicLightShadow

    // Top & Left outer
    drawRect(topLeftColor, topLeft = Offset.Zero, size = Size(size.width, stroke))
    drawRect(topLeftColor, topLeft = Offset.Zero, size = Size(stroke, size.height))
    // Bottom & Right outer
    drawRect(bottomRightColor, topLeft = Offset(0f, size.height - stroke), size = Size(size.width, stroke))
    drawRect(bottomRightColor, topLeft = Offset(size.width - stroke, 0f), size = Size(stroke, size.height))

    // Inner bevel
    drawRect(innerTopLeft, topLeft = Offset(stroke, stroke), size = Size(size.width - stroke * 2, stroke * 0.6f))
    drawRect(innerBottomRight, topLeft = Offset(stroke, size.height - stroke * 1.6f), size = Size(size.width - stroke * 2, stroke * 0.6f))
}

@Composable
fun SkinAdaptiveTileContainer(
    skin: KioskSkin,
    tileConfig: DesktopTileConfig,
    windowTitle: String,
    retroIcon: RetroPixelIconType,
    isEditMode: Boolean,
    onMoveLeft: () -> Unit,
    onMoveRight: () -> Unit,
    onCycleSpan: () -> Unit,
    onCycleVariant: () -> Unit,
    onToggleVisibility: () -> Unit,
    modifier: Modifier = Modifier,
    onClickTile: (() -> Unit)? = null,
    content: @Composable (bgColor: Color, contentColor: Color, mutedColor: Color, accentColor: Color) -> Unit
) {
    val heightDp = tileConfig.span.heightDp.dp

    when (skin) {
        KioskSkin.NOTHING_DOT_MATRIX -> {
            val isPureCircle = tileConfig.span == TileSpan.CIRCLE_1X1
            val shape = when {
                isPureCircle -> CircleShape
                tileConfig.span == TileSpan.PILL_2X1 -> RoundedCornerShape(34.dp)
                else -> RoundedCornerShape(30.dp)
            }

            val (bgColor, contentColor, mutedColor, accentColor) = when (tileConfig.surfaceVariant) {
                TileSurfaceVariant.DARK_CHARCOAL -> listOf(
                    NothingCharcoal,
                    NothingStarkWhite,
                    Color(0xFF8E8E95),
                    NothingCrimsonRed
                )
                TileSurfaceVariant.STARK_WHITE -> listOf(
                    NothingStarkWhite,
                    Color(0xFF111114),
                    Color(0xFF686870),
                    NothingCrimsonRed
                )
                TileSurfaceVariant.CRIMSON_RED -> listOf(
                    NothingCrimsonRed,
                    Color.White,
                    Color(0xFFFFCDD2),
                    Color.White
                )
            }

            Box(
                modifier = modifier
                    .height(heightDp)
                    .clip(shape)
                    .background(bgColor)
                    .border(
                        width = if (isEditMode) 2.dp else 1.dp,
                        color = if (isEditMode) NothingCrimsonRed else Color(0xFF28282E),
                        shape = shape
                    )
                    .then(
                        if (onClickTile != null && !isEditMode) {
                            Modifier.clickable { onClickTile() }
                        } else Modifier
                    )
                    .testTag("tile_${tileConfig.id}")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(if (isPureCircle) 14.dp else 18.dp)
                ) {
                    content(bgColor, contentColor, mutedColor, accentColor)
                }

                if (isEditMode) {
                    TileEditControlsOverlay(
                        tileConfig = tileConfig,
                        onMoveLeft = onMoveLeft,
                        onMoveRight = onMoveRight,
                        onCycleSpan = onCycleSpan,
                        onCycleVariant = onCycleVariant,
                        onToggleVisibility = onToggleVisibility
                    )
                }
            }
        }

        KioskSkin.WIN_XP_LUNA, KioskSkin.WIN_XP_CLASSIC -> {
            val isLuna = skin == KioskSkin.WIN_XP_LUNA
            val windowFace = if (isLuna) XpLunaWindowFace else XpClassicFace
            val shape = if (isLuna) RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomStart = 4.dp, bottomEnd = 4.dp)
            else RoundedCornerShape(0.dp)

            Box(
                modifier = modifier
                    .height(heightDp)
                    .clip(shape)
                    .background(windowFace)
                    .then(
                        if (isLuna) {
                            Modifier.border(2.5.dp, XpLunaBorderBlue, shape)
                        } else {
                            Modifier.xpClassic3DBevel(sunken = false, strokeWidthDp = 2.dp)
                        }
                    )
                    .then(
                        if (onClickTile != null && !isEditMode) {
                            Modifier.clickable { onClickTile() }
                        } else Modifier
                    )
                    .testTag("tile_${tileConfig.id}")
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Retro XP Window Title Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(28.dp)
                            .background(
                                brush = if (isLuna) {
                                    Brush.verticalGradient(listOf(XpLunaTitleEnd, XpLunaTitleStart))
                                } else {
                                    Brush.horizontalGradient(listOf(XpClassicTitleStart, XpClassicTitleEnd))
                                }
                            )
                            .padding(horizontal = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RetroPixelIcon(type = retroIcon, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = windowTitle,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = CleanSansFontFamily,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        // Minimize / Maximize / Close decorative window buttons
                        XpWindowCaptionButton(label = "_", isLuna = isLuna, isClose = false, onClick = onCycleSpan)
                        Spacer(modifier = Modifier.width(2.dp))
                        XpWindowCaptionButton(label = "□", isLuna = isLuna, isClose = false, onClick = onCycleVariant)
                        Spacer(modifier = Modifier.width(2.dp))
                        XpWindowCaptionButton(
                            label = "×",
                            isLuna = isLuna,
                            isClose = true,
                            onClick = { onClickTile?.invoke() }
                        )
                    }

                    // Window Body
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp)
                    ) {
                        content(
                            windowFace,
                            Color(0xFF111111),
                            Color(0xFF4A4944),
                            if (isLuna) XpLunaTitleStart else XpClassicTitleStart
                        )
                    }
                }

                if (isEditMode) {
                    TileEditControlsOverlay(
                        tileConfig = tileConfig,
                        onMoveLeft = onMoveLeft,
                        onMoveRight = onMoveRight,
                        onCycleSpan = onCycleSpan,
                        onCycleVariant = onCycleVariant,
                        onToggleVisibility = onToggleVisibility
                    )
                }
            }
        }
    }
}

@Composable
private fun XpWindowCaptionButton(
    label: String,
    isLuna: Boolean,
    isClose: Boolean,
    onClick: () -> Unit
) {
    val bg = when {
        isLuna && isClose -> XpLunaCloseRed
        isLuna -> Color(0xFF2868E8)
        else -> XpClassicFace
    }
    val fg = if (isLuna) Color.White else Color.Black
    Box(
        modifier = Modifier
            .size(18.dp)
            .clip(if (isLuna) RoundedCornerShape(3.dp) else RoundedCornerShape(0.dp))
            .background(bg)
            .border(1.dp, if (isLuna) Color.White.copy(alpha = 0.6f) else XpClassicDarkShadow)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = fg,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 10.sp
        )
    }
}

@Composable
private fun TileEditControlsOverlay(
    tileConfig: DesktopTileConfig,
    onMoveLeft: () -> Unit,
    onMoveRight: () -> Unit,
    onCycleSpan: () -> Unit,
    onCycleVariant: () -> Unit,
    onToggleVisibility: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.68f))
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = NothingCrimsonRed,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = tileConfig.span.label,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontFamily = TelemetryMonoFontFamily,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                IconButtonPill(
                    icon = if (tileConfig.isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    label = if (tileConfig.isVisible) "ON" else "HIDDEN",
                    onClick = onToggleVisibility,
                    testTag = "toggle_vis_${tileConfig.id}"
                )
            }

            Text(
                text = tileConfig.type.title.uppercase(),
                color = Color.White,
                fontSize = 12.sp,
                fontFamily = TelemetryMonoFontFamily,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButtonPill(
                    icon = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    label = "",
                    onClick = onMoveLeft,
                    testTag = "move_left_${tileConfig.id}"
                )
                IconButtonPill(
                    icon = Icons.Default.AspectRatio,
                    label = "SIZE",
                    onClick = onCycleSpan,
                    testTag = "resize_${tileConfig.id}"
                )
                IconButtonPill(
                    icon = Icons.Default.Palette,
                    label = "TONE",
                    onClick = onCycleVariant,
                    testTag = "tone_${tileConfig.id}"
                )
                IconButtonPill(
                    icon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    label = "",
                    onClick = onMoveRight,
                    testTag = "move_right_${tileConfig.id}"
                )
            }
        }
    }
}

@Composable
private fun IconButtonPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF27272D))
            .border(1.dp, Color(0xFF52525B), RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label.ifEmpty { "Control" },
            tint = Color.White,
            modifier = Modifier.size(16.dp)
        )
        if (label.isNotEmpty()) {
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = label,
                color = Color.White,
                fontSize = 10.sp,
                fontFamily = TelemetryMonoFontFamily
            )
        }
    }
}
