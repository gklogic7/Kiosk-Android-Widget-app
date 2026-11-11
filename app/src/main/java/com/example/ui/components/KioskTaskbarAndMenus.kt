package com.example.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DashboardCustomize
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DesktopTileConfig
import com.example.model.KioskSkin
import com.example.model.PollingMode
import com.example.model.TelemetrySnapshot
import com.example.ui.theme.CleanSansFontFamily
import com.example.ui.theme.CrtMonitorBlack
import com.example.ui.theme.CrtTraceNeonGreen
import com.example.ui.theme.CrtTraceYellow
import com.example.ui.theme.DotMatrixFontFamily
import com.example.ui.theme.NothingCharcoal
import com.example.ui.theme.NothingCrimsonRed
import com.example.ui.theme.NothingStarkWhite
import com.example.ui.theme.TelemetryMonoFontFamily
import com.example.ui.theme.XpClassicDarkShadow
import com.example.ui.theme.XpClassicFace
import com.example.ui.theme.XpClassicTitleEnd
import com.example.ui.theme.XpClassicTitleStart
import com.example.ui.theme.XpLunaBorderBlue
import com.example.ui.theme.XpLunaStartGreenBottom
import com.example.ui.theme.XpLunaStartGreenTop
import com.example.ui.theme.XpLunaTaskbarBottom
import com.example.ui.theme.XpLunaTaskbarTop
import com.example.ui.theme.XpLunaTitleEnd
import com.example.ui.theme.XpLunaTitleStart
import com.example.ui.theme.XpLunaTrayBlue
import com.example.ui.theme.XpLunaWindowFace
import com.example.widget.renderWidgetBitmap
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun KioskBottomTaskbar(
    skin: KioskSkin,
    snapshot: TelemetrySnapshot,
    isStartMenuOpen: Boolean,
    isEditGridMode: Boolean,
    isProcessInspectorOpen: Boolean,
    isWidgetConfigOpen: Boolean,
    onToggleStartMenu: () -> Unit,
    onToggleEditGrid: () -> Unit,
    onOpenProcessInspector: () -> Unit,
    onOpenWidgetConfig: () -> Unit,
    onCycleSkin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val timeFormatted = remember(snapshot.timestampMs / 10000L) {
        SimpleDateFormat("HH:mm", Locale.US).format(Date(snapshot.timestampMs))
    }

    when (skin) {
        KioskSkin.NOTHING_DOT_MATRIX -> {
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(Color(0xFF141418))
                        .border(1.dp, Color(0xFF2C2C34), RoundedCornerShape(28.dp))
                        .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Left: Kiosk Dot-Matrix Start Button + Taskbar Tabs
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Start / Kiosk Command Pill
                        Row(
                            modifier = Modifier
                                .height(40.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isStartMenuOpen) NothingCrimsonRed else NothingStarkWhite)
                                .clickable { onToggleStartMenu() }
                                .padding(horizontal = 14.dp)
                                .testTag("taskbar_start_button"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (isStartMenuOpen) Color.White else NothingCrimsonRed)
                            )
                            Text(
                                text = "KIOSK",
                                fontFamily = DotMatrixFontFamily,
                                fontSize = 19.sp,
                                color = if (isStartMenuOpen) Color.White else Color.Black
                            )
                        }

                        NothingTaskbarTab(
                            label = "TASKS",
                            isActive = isProcessInspectorOpen,
                            onClick = onOpenProcessInspector,
                            testTag = "taskbar_tasks_button"
                        )
                        NothingTaskbarTab(
                            label = "WIDGETS",
                            isActive = isWidgetConfigOpen,
                            onClick = onOpenWidgetConfig,
                            testTag = "taskbar_widgets_button"
                        )
                        NothingTaskbarTab(
                            label = if (isEditGridMode) "LOCK GRID" else "GRID",
                            isActive = isEditGridMode,
                            onClick = onToggleEditGrid,
                            testTag = "taskbar_grid_button"
                        )
                        NothingTaskbarTab(
                            label = "XP SKIN",
                            isActive = false,
                            onClick = onCycleSkin,
                            testTag = "taskbar_skin_button"
                        )
                    }

                    // Right: Live Telemetry Tray & Clock
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF1F1F26))
                            .clickable { onOpenProcessInspector() }
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = "CPU ${snapshot.cpuLoadPercent}%",
                            fontFamily = TelemetryMonoFontFamily,
                            fontSize = 12.sp,
                            color = if (snapshot.cpuLoadPercent > 75) NothingCrimsonRed else Color.White
                        )
                        Text(
                            text = "RAM ${snapshot.ramUsagePercent}%",
                            fontFamily = TelemetryMonoFontFamily,
                            fontSize = 12.sp,
                            color = Color(0xFFB4B4BE)
                        )
                        Text(
                            text = timeFormatted,
                            fontFamily = DotMatrixFontFamily,
                            fontSize = 20.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }

        KioskSkin.WIN_XP_LUNA, KioskSkin.WIN_XP_CLASSIC -> {
            val isLuna = skin == KioskSkin.WIN_XP_LUNA
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .height(48.dp)
                    .then(
                        if (isLuna) {
                            Modifier.background(
                                Brush.verticalGradient(listOf(XpLunaTaskbarTop, XpLunaTaskbarBottom))
                            )
                        } else {
                            Modifier
                                .background(XpClassicFace)
                                .xpClassic3DBevel(sunken = false, strokeWidthDp = 2.dp)
                        }
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f).padding(end = 6.dp)
                ) {
                    // Authentic XP Green Start Button or Classic 3D Start Button
                    Row(
                        modifier = Modifier
                            .fillMaxHeight()
                            .then(
                                if (isLuna) {
                                    Modifier
                                        .clip(RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp))
                                        .background(
                                            Brush.verticalGradient(
                                                if (isStartMenuOpen) {
                                                    listOf(XpLunaStartGreenBottom, XpLunaStartGreenTop)
                                                } else {
                                                    listOf(XpLunaStartGreenTop, XpLunaStartGreenBottom)
                                                }
                                            )
                                        )
                                } else {
                                    Modifier
                                        .padding(4.dp)
                                        .background(XpClassicFace)
                                        .xpClassic3DBevel(sunken = isStartMenuOpen, strokeWidthDp = 2.dp)
                                }
                            )
                            .clickable { onToggleStartMenu() }
                            .padding(horizontal = 14.dp)
                            .testTag("taskbar_start_button"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        WindowsFourColorFlagCanvas(modifier = Modifier.size(18.dp))
                        Text(
                            text = if (isLuna) "start" else "Start",
                            color = if (isLuna) Color.White else Color.Black,
                            fontFamily = CleanSansFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontStyle = if (isLuna) FontStyle.Italic else FontStyle.Normal,
                            fontSize = 16.sp
                        )
                    }

                    // XP Taskbar Window Tabs
                    XpTaskbarWindowTab(
                        title = "Task Manager",
                        iconType = RetroPixelIconType.TASK_MANAGER,
                        isLuna = isLuna,
                        isActive = isProcessInspectorOpen,
                        onClick = onOpenProcessInspector,
                        testTag = "taskbar_tasks_button"
                    )
                    XpTaskbarWindowTab(
                        title = "Home Widgets",
                        iconType = RetroPixelIconType.MY_COMPUTER,
                        isLuna = isLuna,
                        isActive = isWidgetConfigOpen,
                        onClick = onOpenWidgetConfig,
                        testTag = "taskbar_widgets_button"
                    )
                    XpTaskbarWindowTab(
                        title = if (isEditGridMode) "Lock Grid" else "Customize Grid",
                        iconType = RetroPixelIconType.CPU_CHIP,
                        isLuna = isLuna,
                        isActive = isEditGridMode,
                        onClick = onToggleEditGrid,
                        testTag = "taskbar_grid_button"
                    )
                    XpTaskbarWindowTab(
                        title = "Theme: ${skin.badgeText}",
                        iconType = RetroPixelIconType.RAM_STICK,
                        isLuna = isLuna,
                        isActive = false,
                        onClick = onCycleSkin,
                        testTag = "taskbar_skin_button"
                    )
                }

                // Right XP System Tray Well
                Row(
                    modifier = Modifier
                        .fillMaxHeight()
                        .then(
                            if (isLuna) {
                                Modifier
                                    .background(XpLunaTrayBlue)
                                    .border(1.dp, Color(0xFF1952B8))
                            } else {
                                Modifier
                                    .padding(4.dp)
                                    .background(XpClassicFace)
                                    .xpClassic3DBevel(sunken = true, strokeWidthDp = 1.5.dp)
                            }
                        )
                        .clickable { onOpenProcessInspector() }
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RetroPixelIcon(type = RetroPixelIconType.TASK_MANAGER, modifier = Modifier.size(16.dp))
                    RetroPixelIcon(type = RetroPixelIconType.NETWORK_MONITORS, modifier = Modifier.size(16.dp))
                    RetroPixelIcon(type = RetroPixelIconType.BATTERY_BOLT, modifier = Modifier.size(16.dp))
                    Text(
                        text = "${snapshot.cpuLoadPercent}%",
                        fontFamily = TelemetryMonoFontFamily,
                        fontSize = 12.sp,
                        color = if (isLuna) Color.White else Color.Black
                    )
                    Text(
                        text = timeFormatted,
                        fontFamily = CleanSansFontFamily,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isLuna) Color.White else Color.Black
                    )
                }
            }
        }
    }
}

@Composable
private fun NothingTaskbarTab(
    label: String,
    isActive: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Box(
        modifier = Modifier
            .height(38.dp)
            .clip(RoundedCornerShape(19.dp))
            .background(if (isActive) NothingCrimsonRed else Color(0xFF22222A))
            .clickable { onClick() }
            .padding(horizontal = 12.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontFamily = TelemetryMonoFontFamily,
            fontSize = 12.sp,
            color = Color.White,
            maxLines = 1
        )
    }
}

@Composable
private fun XpTaskbarWindowTab(
    title: String,
    iconType: RetroPixelIconType,
    isLuna: Boolean,
    isActive: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val bgColor = when {
        isLuna && isActive -> Color(0xFF1943A6)
        isLuna -> Color(0xFF397DF6)
        isActive -> Color(0xFFC4C0B8)
        else -> XpClassicFace
    }
    Row(
        modifier = Modifier
            .height(36.dp)
            .clip(if (isLuna) RoundedCornerShape(4.dp) else RoundedCornerShape(0.dp))
            .background(bgColor)
            .then(
                if (isLuna) {
                    Modifier.border(1.dp, Color(0xFF14388C), RoundedCornerShape(4.dp))
                } else {
                    Modifier.xpClassic3DBevel(sunken = isActive, strokeWidthDp = 1.5.dp)
                }
            )
            .clickable { onClick() }
            .padding(horizontal = 10.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        RetroPixelIcon(type = iconType, modifier = Modifier.size(15.dp))
        Text(
            text = title,
            color = if (isLuna) Color.White else Color.Black,
            fontFamily = CleanSansFontFamily,
            fontSize = 12.sp,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun WindowsFourColorFlagCanvas(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val halfW = size.width * 0.44f
        val halfH = size.height * 0.44f
        val gap = size.width * 0.08f
        drawRect(Color(0xFFFF3B2F), topLeft = Offset(0f, 0f), size = Size(halfW, halfH))
        drawRect(Color(0xFF34C759), topLeft = Offset(halfW + gap, 0f), size = Size(halfW, halfH))
        drawRect(Color(0xFF007AFF), topLeft = Offset(0f, halfH + gap), size = Size(halfW, halfH))
        drawRect(Color(0xFFFFCC00), topLeft = Offset(halfW + gap, halfH + gap), size = Size(halfW, halfH))
    }
}

@Composable
fun KioskStartMenuPopup(
    skin: KioskSkin,
    pollingMode: PollingMode,
    isEditGridMode: Boolean,
    onSelectSkin: (KioskSkin) -> Unit,
    onSelectPollingMode: (PollingMode) -> Unit,
    onOpenProcessInspector: () -> Unit,
    onOpenWidgetConfig: () -> Unit,
    onToggleEditGrid: () -> Unit,
    onResetGridLayout: () -> Unit,
    onTriggerGcTrim: () -> Unit,
    onOpenSystemDisplay: () -> Unit,
    onDismiss: () -> Unit
) {
    BackHandler { onDismiss() }

    val isNothing = skin == KioskSkin.NOTHING_DOT_MATRIX
    val isLuna = skin == KioskSkin.WIN_XP_LUNA

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.35f))
            .clickable { onDismiss() }
            .padding(start = 16.dp, bottom = 68.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        Column(
            modifier = Modifier
                .widthIn(min = 340.dp, max = 520.dp)
                .clip(
                    if (isNothing) RoundedCornerShape(28.dp)
                    else if (isLuna) RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
                    else RoundedCornerShape(0.dp)
                )
                .background(
                    when (skin) {
                        KioskSkin.NOTHING_DOT_MATRIX -> Color(0xFF151519)
                        KioskSkin.WIN_XP_LUNA -> XpLunaWindowFace
                        KioskSkin.WIN_XP_CLASSIC -> XpClassicFace
                    }
                )
                .then(
                    when (skin) {
                        KioskSkin.NOTHING_DOT_MATRIX -> Modifier.border(1.5.dp, Color(0xFF32323A), RoundedCornerShape(28.dp))
                        KioskSkin.WIN_XP_LUNA -> Modifier.border(2.5.dp, XpLunaBorderBlue, RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                        KioskSkin.WIN_XP_CLASSIC -> Modifier.xpClassic3DBevel(sunken = false, strokeWidthDp = 2.dp)
                    }
                )
                .clickable(enabled = false) {}
                .testTag("start_menu_popup")
        ) {
            // Top User / Workstation Header Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = when (skin) {
                            KioskSkin.NOTHING_DOT_MATRIX -> Brush.horizontalGradient(listOf(Color(0xFF1F1F26), Color(0xFF151519)))
                            KioskSkin.WIN_XP_LUNA -> Brush.verticalGradient(listOf(XpLunaTitleEnd, XpLunaTitleStart))
                            KioskSkin.WIN_XP_CLASSIC -> Brush.horizontalGradient(listOf(XpClassicTitleStart, XpClassicTitleEnd))
                        }
                    )
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(if (isNothing) CircleShape else RoundedCornerShape(6.dp))
                            .background(if (isNothing) NothingCrimsonRed else Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        RetroPixelIcon(type = RetroPixelIconType.MY_COMPUTER, modifier = Modifier.size(24.dp))
                    }
                    Column {
                        Text(
                            text = "Kiosk Tablet Workstation",
                            fontFamily = if (isNothing) DotMatrixFontFamily else CleanSansFontFamily,
                            fontSize = if (isNothing) 22.sp else 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Low-Overhead System Telemetry & Desktop",
                            fontFamily = TelemetryMonoFontFamily,
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // Two-Column Body (Left: Actions, Right: Skins & Polling Governor)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Left Column: Primary Desktop Tools
                Column(
                    modifier = Modifier.weight(1.05f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "DESKTOP UTILITIES",
                        fontFamily = TelemetryMonoFontFamily,
                        fontSize = 11.sp,
                        color = if (isNothing) Color(0xFF8E8E95) else Color(0xFF4A4944)
                    )

                    StartMenuActionRow(
                        icon = Icons.Default.Memory,
                        title = "Process & RAM Inspector",
                        subtitle = "Heap, cores & running tasks",
                        isNothing = isNothing,
                        onClick = onOpenProcessInspector,
                        testTag = "menu_open_inspector"
                    )
                    StartMenuActionRow(
                        icon = Icons.Default.Widgets,
                        title = "Home Screen Widgets",
                        subtitle = "Pin & sync standalone widgets",
                        isNothing = isNothing,
                        onClick = onOpenWidgetConfig,
                        testTag = "menu_open_widgets"
                    )
                    StartMenuActionRow(
                        icon = Icons.Default.DashboardCustomize,
                        title = if (isEditGridMode) "Lock Widget Grid" else "Customize Widget Grid",
                        subtitle = "Resize spans & reorder tiles",
                        isNothing = isNothing,
                        onClick = onToggleEditGrid,
                        testTag = "menu_toggle_grid"
                    )
                    StartMenuActionRow(
                        icon = Icons.Default.CleaningServices,
                        title = "Trim Memory & Run GC",
                        subtitle = "Reclaim unused JVM heap",
                        isNothing = isNothing,
                        onClick = {
                            onTriggerGcTrim()
                            onDismiss()
                        },
                        testTag = "menu_trim_memory"
                    )
                    StartMenuActionRow(
                        icon = Icons.Default.RestartAlt,
                        title = "Reset Desktop Layout",
                        subtitle = "Restore default bento grid",
                        isNothing = isNothing,
                        onClick = {
                            onResetGridLayout()
                            onDismiss()
                        },
                        testTag = "menu_reset_grid"
                    )
                }

                // Right Column: Theme Skins & Polling Cadence
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "VISUAL SKIN ENGINE",
                        fontFamily = TelemetryMonoFontFamily,
                        fontSize = 11.sp,
                        color = if (isNothing) Color(0xFF8E8E95) else Color(0xFF4A4944)
                    )

                    KioskSkin.entries.forEach { skinOption ->
                        val selected = skinOption == skin
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    when {
                                        selected && isNothing -> NothingCrimsonRed
                                        selected -> XpLunaTitleStart
                                        isNothing -> Color(0xFF232329)
                                        else -> Color.White.copy(alpha = 0.7f)
                                    }
                                )
                                .clickable { onSelectSkin(skinOption) }
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                                .testTag("skin_option_${skinOption.id}"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = skinOption.title,
                                    fontFamily = CleanSansFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp,
                                    color = if (selected || isNothing) Color.White else Color.Black
                                )
                                Text(
                                    text = skinOption.badgeText,
                                    fontFamily = TelemetryMonoFontFamily,
                                    fontSize = 10.sp,
                                    color = if (selected || isNothing) Color.White.copy(alpha = 0.8f) else Color.DarkGray
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "POLLING CADENCE",
                        fontFamily = TelemetryMonoFontFamily,
                        fontSize = 11.sp,
                        color = if (isNothing) Color(0xFF8E8E95) else Color(0xFF4A4944)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PollingMode.entries.forEach { mode ->
                            val isSelected = mode == pollingMode
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        when {
                                            isSelected && isNothing -> NothingStarkWhite
                                            isSelected -> XpLunaStartGreenTop
                                            isNothing -> Color(0xFF232329)
                                            else -> Color.White
                                        }
                                    )
                                    .clickable { onSelectPollingMode(mode) }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${mode.intervalMs / 1000}s",
                                    fontFamily = TelemetryMonoFontFamily,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when {
                                        isSelected && isNothing -> Color.Black
                                        isSelected -> Color.White
                                        isNothing -> Color.White
                                        else -> Color.Black
                                    }
                                )
                            }
                        }
                    }

                    StartMenuActionRow(
                        icon = Icons.Default.Speed,
                        title = "Tablet Display Settings",
                        subtitle = "System brightness & scale",
                        isNothing = isNothing,
                        onClick = {
                            onOpenSystemDisplay()
                            onDismiss()
                        },
                        testTag = "menu_display_settings"
                    )
                }
            }
        }
    }
}

@Composable
private fun StartMenuActionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    isNothing: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (isNothing) Color(0xFF22222A) else Color.White.copy(alpha = 0.75f))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 8.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = if (isNothing) NothingCrimsonRed else XpLunaTitleStart,
            modifier = Modifier.size(20.dp)
        )
        Column {
            Text(
                text = title,
                fontFamily = CleanSansFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                color = if (isNothing) Color.White else Color.Black
            )
            Text(
                text = subtitle,
                fontFamily = CleanSansFontFamily,
                fontSize = 10.sp,
                color = if (isNothing) Color(0xFF9A9AA2) else Color(0xFF555550)
            )
        }
    }
}

@Composable
fun ProcessAndMemoryInspectorModal(
    skin: KioskSkin,
    snapshot: TelemetrySnapshot,
    onTriggerGcTrim: () -> Unit,
    onDismiss: () -> Unit
) {
    BackHandler { onDismiss() }

    val isNothing = skin == KioskSkin.NOTHING_DOT_MATRIX
    val bgColor = when (skin) {
        KioskSkin.NOTHING_DOT_MATRIX -> Color(0xFF141418)
        KioskSkin.WIN_XP_LUNA -> XpLunaWindowFace
        KioskSkin.WIN_XP_CLASSIC -> XpClassicFace
    }
    val textColor = if (isNothing) Color.White else Color.Black
    val mutedColor = if (isNothing) Color(0xFF9A9AA2) else Color(0xFF4A4944)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable { onDismiss() }
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 760.dp)
                .fillMaxWidth()
                .heightIn(max = 580.dp)
                .clip(if (isNothing) RoundedCornerShape(28.dp) else RoundedCornerShape(6.dp))
                .background(bgColor)
                .border(
                    width = 2.dp,
                    color = if (isNothing) Color(0xFF32323A) else XpLunaBorderBlue,
                    shape = if (isNothing) RoundedCornerShape(28.dp) else RoundedCornerShape(6.dp)
                )
                .clickable(enabled = false) {}
                .testTag("process_inspector_modal")
        ) {
            // Modal Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (isNothing) Color(0xFF1E1E24) else XpLunaTitleStart
                    )
                    .padding(horizontal = 18.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    RetroPixelIcon(type = RetroPixelIconType.TASK_MANAGER, modifier = Modifier.size(20.dp))
                    Text(
                        text = "Kiosk Task Manager — Process & Memory Breakdown",
                        fontFamily = CleanSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(NothingCrimsonRed)
                            .clickable { onTriggerGcTrim() }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag("btn_trim_heap"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CleaningServices,
                            contentDescription = "Trim Heap",
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = "TRIM HEAP / GC",
                            fontFamily = TelemetryMonoFontFamily,
                            fontSize = 11.sp,
                            color = Color.White
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.18f))
                            .clickable { onDismiss() }
                            .testTag("close_inspector_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Summary Telemetry Cards Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                InspectorStatBox(
                    label = "SYSTEM RAM USED",
                    value = "${snapshot.ramUsedMb} / ${snapshot.ramTotalMb} MB",
                    sub = "${snapshot.ramUsagePercent}% Allocated • ${snapshot.ramAvailableMb} MB Free",
                    isNothing = isNothing,
                    modifier = Modifier.weight(1f)
                )
                InspectorStatBox(
                    label = "KIOSK FOOTPRINT",
                    value = String.format(Locale.US, "%.1f MB JVM + %.1f MB Native", snapshot.appHeapAllocatedMb, snapshot.nativeHeapAllocatedMb),
                    sub = "${snapshot.activeThreadCount} Threads • Ultra-Lightweight",
                    isNothing = isNothing,
                    modifier = Modifier.weight(1f)
                )
                InspectorStatBox(
                    label = "CPU CORES (${snapshot.cpuCoresCount})",
                    value = "${snapshot.cpuLoadPercent}% @ ${snapshot.averageFreqMhz} MHz",
                    sub = "Max Clock ${snapshot.maxFreqMhz} MHz",
                    isNothing = isNothing,
                    modifier = Modifier.weight(1f)
                )
            }

            // Per-Core Frequency Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                snapshot.perCoreInfo.take(8).forEach { core ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isNothing) Color(0xFF202028) else CrtMonitorBlack)
                            .padding(6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "C${core.coreIndex}",
                            fontFamily = TelemetryMonoFontFamily,
                            fontSize = 10.sp,
                            color = if (isNothing) mutedColor else CrtTraceNeonGreen
                        )
                        Text(
                            text = "${core.currentFreqMhz}",
                            fontFamily = DotMatrixFontFamily,
                            fontSize = 16.sp,
                            color = if (isNothing) Color.White else CrtTraceYellow
                        )
                        Text(
                            text = "${core.loadPercent}%",
                            fontFamily = TelemetryMonoFontFamily,
                            fontSize = 10.sp,
                            color = NothingCrimsonRed
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = mutedColor.copy(alpha = 0.25f))

            // Process Table Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("PROCESS / SUBSYSTEM", fontFamily = TelemetryMonoFontFamily, fontSize = 11.sp, color = mutedColor, modifier = Modifier.weight(2f))
                Text("PID / THREADS", fontFamily = TelemetryMonoFontFamily, fontSize = 11.sp, color = mutedColor, modifier = Modifier.weight(1f))
                Text("CPU SHARE", fontFamily = TelemetryMonoFontFamily, fontSize = 11.sp, color = mutedColor, modifier = Modifier.weight(0.8f))
                Text("MEMORY", fontFamily = TelemetryMonoFontFamily, fontSize = 11.sp, color = mutedColor, modifier = Modifier.weight(0.9f))
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 14.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(snapshot.processList, key = { it.pid }) { proc ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isNothing) Color(0xFF1E1E25) else Color.White.copy(alpha = 0.8f))
                            .padding(horizontal = 12.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(2f)) {
                            Text(
                                text = proc.processName,
                                fontFamily = CleanSansFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = textColor
                            )
                            Text(
                                text = "${proc.packageName} • ${proc.importanceLabel}",
                                fontFamily = TelemetryMonoFontFamily,
                                fontSize = 10.sp,
                                color = mutedColor
                            )
                        }
                        Text(
                            text = "PID ${proc.pid} (${proc.threadsCount}T)",
                            fontFamily = TelemetryMonoFontFamily,
                            fontSize = 11.sp,
                            color = textColor,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = String.format(Locale.US, "%.1f%%", proc.cpuSharePercent),
                            fontFamily = TelemetryMonoFontFamily,
                            fontSize = 12.sp,
                            color = NothingCrimsonRed,
                            modifier = Modifier.weight(0.8f)
                        )
                        Text(
                            text = String.format(Locale.US, "%.1f MB", proc.memoryMb),
                            fontFamily = DotMatrixFontFamily,
                            fontSize = 18.sp,
                            color = textColor,
                            modifier = Modifier.weight(0.9f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InspectorStatBox(
    label: String,
    value: String,
    sub: String,
    isNothing: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (isNothing) Color(0xFF202027) else Color.White.copy(alpha = 0.85f))
            .padding(12.dp)
    ) {
        Text(
            text = label,
            fontFamily = TelemetryMonoFontFamily,
            fontSize = 10.sp,
            color = if (isNothing) Color(0xFF9A9AA2) else Color(0xFF555550)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontFamily = CleanSansFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = if (isNothing) Color.White else Color.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = sub,
            fontFamily = TelemetryMonoFontFamily,
            fontSize = 10.sp,
            color = NothingCrimsonRed,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun HomeWidgetConfigStudioModal(
    skin: KioskSkin,
    snapshot: TelemetrySnapshot,
    tiles: List<DesktopTileConfig>,
    pinnedWidgetCount: Int,
    onSelectSkin: (KioskSkin) -> Unit,
    onPinCompactWidget: () -> Unit,
    onPinDeckWidget: () -> Unit,
    onSyncWidgetsNow: () -> Unit,
    onToggleTileVisibility: (String) -> Unit,
    onDismiss: () -> Unit
) {
    BackHandler { onDismiss() }

    val isNothing = skin == KioskSkin.NOTHING_DOT_MATRIX
    val bgColor = if (isNothing) Color(0xFF141418) else XpLunaWindowFace
    val textColor = if (isNothing) Color.White else Color.Black
    val mutedColor = if (isNothing) Color(0xFF9A9AA2) else Color(0xFF4A4944)

    val compactPreviewBitmap = remember(skin, snapshot.cpuLoadPercent, snapshot.ramUsagePercent) {
        renderWidgetBitmap(skin, snapshot, isWideDeck = false).asImageBitmap()
    }
    val deckPreviewBitmap = remember(skin, snapshot.cpuLoadPercent, snapshot.ramUsagePercent) {
        renderWidgetBitmap(skin, snapshot, isWideDeck = true).asImageBitmap()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.65f))
            .clickable { onDismiss() }
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 820.dp)
                .fillMaxWidth()
                .heightIn(max = 600.dp)
                .clip(if (isNothing) RoundedCornerShape(28.dp) else RoundedCornerShape(8.dp))
                .background(bgColor)
                .border(
                    width = 2.dp,
                    color = if (isNothing) Color(0xFF32323A) else XpLunaBorderBlue,
                    shape = if (isNothing) RoundedCornerShape(28.dp) else RoundedCornerShape(8.dp)
                )
                .clickable(enabled = false) {}
                .testTag("widget_config_modal")
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isNothing) Color(0xFF1E1E24) else XpLunaTitleStart)
                    .padding(horizontal = 18.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Widgets,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Standalone Home Screen Widgets & Tile Configurator ($pinnedWidgetCount Active)",
                        fontFamily = CleanSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(NothingCrimsonRed)
                            .clickable { onSyncWidgetsNow() }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag("btn_sync_widgets_now"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Sync",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "SYNC HOME WIDGETS",
                            fontFamily = TelemetryMonoFontFamily,
                            fontSize = 11.sp,
                            color = Color.White
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.18f))
                            .clickable { onDismiss() }
                            .testTag("close_widget_config_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    // Skin Switcher Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        KioskSkin.entries.forEach { option ->
                            val selected = option == skin
                            Surface(
                                color = when {
                                    selected && isNothing -> NothingCrimsonRed
                                    selected -> XpLunaTitleStart
                                    isNothing -> Color(0xFF22222A)
                                    else -> Color.White
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onSelectSkin(option) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Palette,
                                        contentDescription = null,
                                        tint = if (selected || isNothing) Color.White else Color.Black,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = option.title,
                                        fontFamily = CleanSansFontFamily,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp,
                                        color = if (selected || isNothing) Color.White else Color.Black,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    // Live Home Screen Widget Previews + Pin Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Left: Compact 3×2 Widget Preview
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(18.dp))
                                .background(if (isNothing) Color(0xFF1F1F26) else Color.White)
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "COMPACT CPU & RAM WIDGET (3×2)",
                                fontFamily = TelemetryMonoFontFamily,
                                fontSize = 11.sp,
                                color = mutedColor
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Image(
                                bitmap = compactPreviewBitmap,
                                contentDescription = "Compact Widget Preview",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(126.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(if (isNothing) NothingStarkWhite else XpLunaStartGreenTop)
                                    .clickable { onPinCompactWidget() }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                                    .testTag("btn_pin_compact_widget"),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PushPin,
                                    contentDescription = null,
                                    tint = if (isNothing) Color.Black else Color.White,
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = "Pin 3×2 Widget to Home",
                                    fontFamily = CleanSansFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp,
                                    color = if (isNothing) Color.Black else Color.White
                                )
                            }
                        }

                        // Right: Wide 4×2 Telemetry Deck Preview
                        Column(
                            modifier = Modifier
                                .weight(1.25f)
                                .clip(RoundedCornerShape(18.dp))
                                .background(if (isNothing) Color(0xFF1F1F26) else Color.White)
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "WIDE TABLET TELEMETRY DECK WIDGET (4×2)",
                                fontFamily = TelemetryMonoFontFamily,
                                fontSize = 11.sp,
                                color = mutedColor
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Image(
                                bitmap = deckPreviewBitmap,
                                contentDescription = "Telemetry Deck Widget Preview",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(126.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(NothingCrimsonRed)
                                    .clickable { onPinDeckWidget() }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                                    .testTag("btn_pin_deck_widget"),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PushPin,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = "Pin 4×2 Deck to Home",
                                    fontFamily = CleanSansFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "DESKTOP GRID TILES VISIBILITY",
                        fontFamily = TelemetryMonoFontFamily,
                        fontSize = 11.sp,
                        color = mutedColor
                    )
                }

                items(tiles, key = { it.id }) { tile ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isNothing) Color(0xFF1E1E25) else Color.White.copy(alpha = 0.85f))
                            .clickable { onToggleTileVisibility(tile.id) }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = tile.type.title,
                                fontFamily = CleanSansFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = textColor
                            )
                            Text(
                                text = "Span: ${tile.span.label} • Tone: ${tile.surfaceVariant.name}",
                                fontFamily = TelemetryMonoFontFamily,
                                fontSize = 11.sp,
                                color = mutedColor
                            )
                        }
                        Surface(
                            color = if (tile.isVisible) NothingCrimsonRed else mutedColor.copy(alpha = 0.35f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = if (tile.isVisible) "VISIBLE" else "HIDDEN",
                                fontFamily = TelemetryMonoFontFamily,
                                fontSize = 11.sp,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
