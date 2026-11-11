package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DashboardCustomize
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.DesktopTileConfig
import com.example.model.KioskSkin
import com.example.model.TileType
import com.example.ui.components.BatteryGlyphTileContent
import com.example.ui.components.ClockGmtTileContent
import com.example.ui.components.CpuDialTileContent
import com.example.ui.components.EditorialMetricTileContent
import com.example.ui.components.HomeWidgetConfigStudioModal
import com.example.ui.components.InspirationBoardTileContent
import com.example.ui.components.KioskBottomTaskbar
import com.example.ui.components.KioskStartMenuPopup
import com.example.ui.components.NetworkStreamTileContent
import com.example.ui.components.ProcessAndMemoryInspectorModal
import com.example.ui.components.QuickTogglePillsTileContent
import com.example.ui.components.RamMonitorTileContent
import com.example.ui.components.RetroPixelIconType
import com.example.ui.components.SkinAdaptiveTileContainer
import com.example.ui.components.StorageCompassTileContent
import com.example.ui.components.ThermalCoreDeckTileContent
import com.example.ui.components.WeeklyDotChartTileContent
import com.example.ui.components.xpClassic3DBevel
import com.example.ui.theme.CleanSansFontFamily
import com.example.ui.theme.DotMatrixFontFamily
import com.example.ui.theme.KioskTheme
import com.example.ui.theme.NothingCrimsonRed
import com.example.ui.theme.NothingPitchBlack
import com.example.ui.theme.NothingStarkWhite
import com.example.ui.theme.TelemetryMonoFontFamily
import com.example.ui.theme.XpClassicFace
import com.example.ui.theme.XpClassicTealDesktop
import com.example.ui.theme.XpLunaMeadowBottom
import com.example.ui.theme.XpLunaSkyTop
import com.example.ui.theme.XpLunaTitleStart
import com.example.viewmodel.KioskUiState
import com.example.viewmodel.KioskViewModel
import java.util.Locale
import kotlinx.coroutines.delay

@Composable
fun KioskDesktopScreen(
    viewModel: KioskViewModel
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // Auto-dismiss transient status toast after 3.2 seconds
    LaunchedEffect(state.statusBannerMessage) {
        if (state.statusBannerMessage != null) {
            delay(3200L)
            viewModel.dismissStatusBanner()
        }
    }

    KioskTheme(skin = state.skin) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .testTag("kiosk_desktop_root")
        ) {
            val isExpandedTablet = maxWidth >= 840.dp
            val isMediumTablet = maxWidth >= 560.dp
            val gridColumns = when {
                isExpandedTablet -> 6
                isMediumTablet -> 4
                else -> 2
            }

            // 1. Skin-Specific Desktop Wallpaper / Atmospheric Canvas
            DesktopBackdropCanvas(skin = state.skin)

            // 2. Main Desktop Column (Top Status Bar + Adaptive Draggable/Resizable Bento Grid + Bottom Taskbar)
            Column(modifier = Modifier.fillMaxSize()) {
                KioskTopDesktopBar(
                    state = state,
                    onSelectSkin = viewModel::selectSkin,
                    onToggleEditGrid = viewModel::toggleEditGridMode,
                    onOpenWidgetConfig = viewModel::openWidgetConfig,
                    onOpenProcessInspector = viewModel::openProcessInspector
                )

                val displayedTiles = if (state.isEditGridMode) {
                    state.tiles
                } else {
                    state.tiles.filter { it.isVisible }
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(gridColumns),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .testTag("desktop_widget_grid"),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 8.dp,
                        bottom = 16.dp
                    ),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = displayedTiles,
                        key = { it.id },
                        span = { tile ->
                            val spanCols = tile.span.colSpan.coerceIn(1, gridColumns)
                            GridItemSpan(spanCols)
                        }
                    ) { tile ->
                        DesktopTileRenderer(
                            tile = tile,
                            state = state,
                            onMoveLeft = { viewModel.moveTileOrder(tile.id, -1) },
                            onMoveRight = { viewModel.moveTileOrder(tile.id, +1) },
                            onCycleSpan = { viewModel.cycleTileSpan(tile.id) },
                            onCycleVariant = { viewModel.cycleTileSurfaceVariant(tile.id) },
                            onToggleVisibility = { viewModel.toggleTileVisibility(tile.id) },
                            onOpenProcessInspector = viewModel::openProcessInspector,
                            onOpenWidgetConfig = viewModel::openWidgetConfig,
                            onToggleTempUnit = viewModel::toggleTemperatureUnit,
                            onToggleGlyphPulse = viewModel::toggleGlyphPulse,
                            onToggleBluetooth = { viewModel.toggleBluetoothQuickPill(false) },
                            onToggleMobileData = { viewModel.toggleMobileDataQuickPill(false) },
                            onToggleAutoRotate = viewModel::toggleAutoRotateQuickPill,
                            onToggleBedtimeEco = viewModel::toggleBedtimeEcoMode,
                            onToggleRecording = viewModel::toggleTelemetryRecording
                        )
                    }
                }

                // Bottom Desktop Taskbar
                KioskBottomTaskbar(
                    skin = state.skin,
                    snapshot = state.snapshot,
                    isStartMenuOpen = state.isStartMenuOpen,
                    isEditGridMode = state.isEditGridMode,
                    isProcessInspectorOpen = state.isProcessInspectorOpen,
                    isWidgetConfigOpen = state.isWidgetConfigOpen,
                    onToggleStartMenu = viewModel::toggleStartMenu,
                    onToggleEditGrid = viewModel::toggleEditGridMode,
                    onOpenProcessInspector = viewModel::openProcessInspector,
                    onOpenWidgetConfig = viewModel::openWidgetConfig,
                    onCycleSkin = viewModel::cycleSkin
                )
            }

            // 3. Transient Status Banner
            AnimatedVisibility(
                visible = state.statusBannerMessage != null,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 76.dp)
            ) {
                state.statusBannerMessage?.let { msg ->
                    Surface(
                        color = if (state.skin == KioskSkin.NOTHING_DOT_MATRIX) NothingCrimsonRed else XpLunaTitleStart,
                        shape = RoundedCornerShape(20.dp),
                        shadowElevation = 6.dp,
                        modifier = Modifier.clickable { viewModel.dismissStatusBanner() }
                    ) {
                        Text(
                            text = msg,
                            fontFamily = TelemetryMonoFontFamily,
                            fontSize = 12.sp,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            // 4. Start Menu Popup
            if (state.isStartMenuOpen) {
                KioskStartMenuPopup(
                    skin = state.skin,
                    pollingMode = state.pollingMode,
                    isEditGridMode = state.isEditGridMode,
                    onSelectSkin = viewModel::selectSkin,
                    onSelectPollingMode = viewModel::setPollingMode,
                    onOpenProcessInspector = viewModel::openProcessInspector,
                    onOpenWidgetConfig = viewModel::openWidgetConfig,
                    onToggleEditGrid = viewModel::toggleEditGridMode,
                    onResetGridLayout = viewModel::resetGridLayout,
                    onTriggerGcTrim = viewModel::triggerMemoryTrimAndGc,
                    onOpenSystemDisplay = viewModel::openSystemDisplaySettings,
                    onDismiss = viewModel::closeStartMenu
                )
            }

            // 5. Floating Process & Memory Breakdown Inspector Modal
            if (state.isProcessInspectorOpen) {
                ProcessAndMemoryInspectorModal(
                    skin = state.skin,
                    snapshot = state.snapshot,
                    onTriggerGcTrim = viewModel::triggerMemoryTrimAndGc,
                    onDismiss = viewModel::closeProcessInspector
                )
            }

            // 6. Standalone Home Screen Widget Configurator & Preview Studio Modal
            if (state.isWidgetConfigOpen) {
                HomeWidgetConfigStudioModal(
                    skin = state.skin,
                    snapshot = state.snapshot,
                    tiles = state.tiles,
                    pinnedWidgetCount = state.pinnedWidgetCount,
                    onSelectSkin = viewModel::selectSkin,
                    onPinCompactWidget = { viewModel.requestPinHomeWidget(isWideDeck = false) },
                    onPinDeckWidget = { viewModel.requestPinHomeWidget(isWideDeck = true) },
                    onSyncWidgetsNow = viewModel::forceRefreshTelemetry,
                    onToggleTileVisibility = viewModel::toggleTileVisibility,
                    onDismiss = viewModel::closeWidgetConfig
                )
            }
        }
    }
}

@Composable
private fun DesktopBackdropCanvas(skin: KioskSkin) {
    when (skin) {
        KioskSkin.NOTHING_DOT_MATRIX -> {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .background(NothingPitchBlack)
            ) {
                // Subtle dot-matrix grid atmosphere
                val step = 28.dp.toPx()
                val dotColor = Color(0xFF17171C)
                var x = step
                while (x < size.width) {
                    var y = step
                    while (y < size.height) {
                        drawCircle(dotColor, radius = 1.4.dp.toPx(), center = Offset(x, y))
                        y += step
                    }
                    x += step
                }
            }
        }

        KioskSkin.WIN_XP_LUNA -> {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Bliss-inspired sky and rolling green hills backdrop
                drawRect(
                    brush = Brush.verticalGradient(
                        0.0f to XpLunaSkyTop,
                        0.62f to Color(0xFF75B8FA),
                        0.68f to Color(0xFF56B044),
                        1.0f to XpLunaMeadowBottom
                    )
                )
            }
        }

        KioskSkin.WIN_XP_CLASSIC -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(XpClassicTealDesktop)
            )
        }
    }
}

@Composable
private fun KioskTopDesktopBar(
    state: KioskUiState,
    onSelectSkin: (KioskSkin) -> Unit,
    onToggleEditGrid: () -> Unit,
    onOpenWidgetConfig: () -> Unit,
    onOpenProcessInspector: () -> Unit
) {
    val isNothing = state.skin == KioskSkin.NOTHING_DOT_MATRIX

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left Brand & Lightweight Resource Badge
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.clickable { onOpenProcessInspector() }
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(NothingCrimsonRed)
            )
            Text(
                text = "KIOSK",
                fontFamily = DotMatrixFontFamily,
                fontSize = 26.sp,
                color = Color.White
            )
            Surface(
                color = if (isNothing) Color(0xFF1C1C22) else Color.Black.copy(alpha = 0.45f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = String.format(
                        Locale.US,
                        "HEAP %.1fMB • %s",
                        state.snapshot.appHeapAllocatedMb,
                        state.pollingMode.label.uppercase(Locale.US)
                    ),
                    fontFamily = TelemetryMonoFontFamily,
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }

        // Right Instant Skin Switcher Pills + Edit Grid + Home Widgets Button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            KioskSkin.entries.forEach { skinOption ->
                val isSelected = state.skin == skinOption
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            when {
                                isSelected && isNothing -> NothingStarkWhite
                                isSelected -> NothingCrimsonRed
                                isNothing -> Color(0xFF1C1C22)
                                else -> Color.Black.copy(alpha = 0.45f)
                            }
                        )
                        .clickable { onSelectSkin(skinOption) }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("top_skin_${skinOption.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = skinOption.badgeText,
                        fontFamily = TelemetryMonoFontFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected && isNothing) Color.Black else Color.White
                    )
                }
            }

            // Customize / Lock Grid Toggle
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (state.isEditGridMode) NothingCrimsonRed else Color(0xFF1C1C22))
                    .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                    .clickable { onToggleEditGrid() }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("top_edit_grid_button"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = if (state.isEditGridMode) Icons.Default.Lock else Icons.Default.DashboardCustomize,
                        contentDescription = "Edit Grid",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = if (state.isEditGridMode) "DONE" else "EDIT",
                        fontFamily = TelemetryMonoFontFamily,
                        fontSize = 11.sp,
                        color = Color.White
                    )
                }
            }

            // Home Widgets Studio Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (state.skin == KioskSkin.WIN_XP_CLASSIC) XpClassicFace
                        else Color(0xFF1C1C22)
                    )
                    .then(
                        if (state.skin == KioskSkin.WIN_XP_CLASSIC) {
                            Modifier.xpClassic3DBevel(sunken = false, strokeWidthDp = 1.5.dp)
                        } else {
                            Modifier.border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                        }
                    )
                    .clickable { onOpenWidgetConfig() }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("top_home_widgets_button"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Widgets,
                        contentDescription = "Home Screen Widgets",
                        tint = if (state.skin == KioskSkin.WIN_XP_CLASSIC) Color.Black else NothingCrimsonRed,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "WIDGETS",
                        fontFamily = TelemetryMonoFontFamily,
                        fontSize = 11.sp,
                        color = if (state.skin == KioskSkin.WIN_XP_CLASSIC) Color.Black else Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun DesktopTileRenderer(
    tile: DesktopTileConfig,
    state: KioskUiState,
    onMoveLeft: () -> Unit,
    onMoveRight: () -> Unit,
    onCycleSpan: () -> Unit,
    onCycleVariant: () -> Unit,
    onToggleVisibility: () -> Unit,
    onOpenProcessInspector: () -> Unit,
    onOpenWidgetConfig: () -> Unit,
    onToggleTempUnit: () -> Unit,
    onToggleGlyphPulse: () -> Unit,
    onToggleBluetooth: () -> Unit,
    onToggleMobileData: () -> Unit,
    onToggleAutoRotate: () -> Unit,
    onToggleBedtimeEco: () -> Unit,
    onToggleRecording: () -> Unit
) {
    val retroIcon = when (tile.type) {
        TileType.CPU_DIAL -> RetroPixelIconType.CPU_CHIP
        TileType.WEEKLY_DOT_CHART -> RetroPixelIconType.TASK_MANAGER
        TileType.RAM_MONITOR -> RetroPixelIconType.RAM_STICK
        TileType.EDITORIAL_METRIC -> RetroPixelIconType.MY_COMPUTER
        TileType.THERMAL_CORE_DECK -> RetroPixelIconType.CPU_CHIP
        TileType.QUICK_TOGGLE_PILLS -> RetroPixelIconType.MY_COMPUTER
        TileType.CLOCK_GMT_TILE -> RetroPixelIconType.CLOCK_TRAY
        TileType.STORAGE_COMPASS -> RetroPixelIconType.HARD_DRIVE
        TileType.BATTERY_GLYPH -> RetroPixelIconType.BATTERY_BOLT
        TileType.NETWORK_STREAM -> RetroPixelIconType.NETWORK_MONITORS
        TileType.INSPIRATION_BOARD -> RetroPixelIconType.MY_COMPUTER
    }

    val tileClickAction: (() -> Unit)? = when (tile.type) {
        TileType.CPU_DIAL, TileType.RAM_MONITOR, TileType.EDITORIAL_METRIC -> onOpenProcessInspector
        TileType.INSPIRATION_BOARD, TileType.WEEKLY_DOT_CHART -> onOpenWidgetConfig
        TileType.CLOCK_GMT_TILE, TileType.STORAGE_COMPASS, TileType.NETWORK_STREAM -> onOpenProcessInspector
        TileType.THERMAL_CORE_DECK, TileType.BATTERY_GLYPH, TileType.QUICK_TOGGLE_PILLS -> null
    }

    SkinAdaptiveTileContainer(
        skin = state.skin,
        tileConfig = tile,
        windowTitle = tile.type.title,
        retroIcon = retroIcon,
        isEditMode = state.isEditGridMode,
        onMoveLeft = onMoveLeft,
        onMoveRight = onMoveRight,
        onCycleSpan = onCycleSpan,
        onCycleVariant = onCycleVariant,
        onToggleVisibility = onToggleVisibility,
        onClickTile = tileClickAction
    ) { _, contentColor, mutedColor, accentColor ->
        when (tile.type) {
            TileType.CPU_DIAL -> CpuDialTileContent(
                skin = state.skin,
                snapshot = state.snapshot,
                span = tile.span,
                contentColor = contentColor,
                mutedColor = mutedColor,
                accentColor = accentColor
            )
            TileType.WEEKLY_DOT_CHART -> WeeklyDotChartTileContent(
                skin = state.skin,
                snapshot = state.snapshot,
                contentColor = contentColor,
                mutedColor = mutedColor
            )
            TileType.EDITORIAL_METRIC -> EditorialMetricTileContent(
                snapshot = state.snapshot,
                contentColor = contentColor,
                mutedColor = mutedColor,
                accentColor = accentColor
            )
            TileType.BATTERY_GLYPH -> BatteryGlyphTileContent(
                snapshot = state.snapshot,
                useFahrenheit = state.useFahrenheit,
                isGlyphPulseEnabled = state.isGlyphPulseEnabled,
                onToggleTempUnit = onToggleTempUnit,
                onToggleGlyph = onToggleGlyphPulse
            )
            TileType.THERMAL_CORE_DECK -> ThermalCoreDeckTileContent(
                skin = state.skin,
                snapshot = state.snapshot,
                useFahrenheit = state.useFahrenheit,
                contentColor = contentColor,
                mutedColor = mutedColor,
                onToggleTempUnit = onToggleTempUnit
            )
            TileType.RAM_MONITOR -> RamMonitorTileContent(
                skin = state.skin,
                snapshot = state.snapshot,
                contentColor = contentColor,
                mutedColor = mutedColor,
                accentColor = accentColor
            )
            TileType.CLOCK_GMT_TILE -> ClockGmtTileContent(
                snapshot = state.snapshot,
                contentColor = contentColor,
                mutedColor = mutedColor
            )
            TileType.QUICK_TOGGLE_PILLS -> QuickTogglePillsTileContent(
                isBluetoothOn = state.isBluetoothSimulatedOn,
                isMobileDataOn = state.isMobileDataSimulatedOn,
                isAutoRotateOn = state.isAutoRotateSimulated,
                isBedtimeEcoOn = state.isBedtimeEcoActive,
                isRecordingOn = state.isTelemetryRecording,
                onToggleBluetooth = onToggleBluetooth,
                onToggleMobileData = onToggleMobileData,
                onToggleAutoRotate = onToggleAutoRotate,
                onToggleBedtimeEco = onToggleBedtimeEco,
                onToggleRecording = onToggleRecording
            )
            TileType.STORAGE_COMPASS -> StorageCompassTileContent(
                snapshot = state.snapshot,
                contentColor = contentColor,
                mutedColor = mutedColor
            )
            TileType.NETWORK_STREAM -> NetworkStreamTileContent(
                snapshot = state.snapshot,
                contentColor = contentColor,
                mutedColor = mutedColor,
                accentColor = accentColor
            )
            TileType.INSPIRATION_BOARD -> InspirationBoardTileContent(
                contentColor = contentColor
            )
        }
    }
}
