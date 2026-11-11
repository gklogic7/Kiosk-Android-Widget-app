package com.example.viewmodel

import android.app.Application
import android.content.Intent
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.KioskPreferencesRepository
import com.example.data.LowOverheadTelemetryEngine
import com.example.model.DesktopTileConfig
import com.example.model.KioskSkin
import com.example.model.PollingMode
import com.example.model.TelemetrySnapshot
import com.example.widget.KioskWidgetUpdater
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class KioskUiState(
    val skin: KioskSkin = KioskSkin.NOTHING_DOT_MATRIX,
    val pollingMode: PollingMode = PollingMode.BALANCED_2S,
    val snapshot: TelemetrySnapshot = TelemetrySnapshot(),
    val tiles: List<DesktopTileConfig> = KioskPreferencesRepository.defaultTiles(),
    val isEditGridMode: Boolean = false,
    val isStartMenuOpen: Boolean = false,
    val isProcessInspectorOpen: Boolean = false,
    val isWidgetConfigOpen: Boolean = false,
    val useFahrenheit: Boolean = false,
    val isGlyphPulseEnabled: Boolean = true,
    val isBedtimeEcoActive: Boolean = false,
    val isTelemetryRecording: Boolean = false,
    val isAutoRotateSimulated: Boolean = true,
    val isBluetoothSimulatedOn: Boolean = true,
    val isMobileDataSimulatedOn: Boolean = true,
    val recordedSamplesCount: Int = 0,
    val pinnedWidgetCount: Int = 0,
    val statusBannerMessage: String? = null
)

class KioskViewModel(application: Application) : AndroidViewModel(application) {

    private val appContext = application.applicationContext
    private val prefs = KioskPreferencesRepository(appContext)
    private val telemetryEngine = LowOverheadTelemetryEngine.getInstance(appContext)

    private val _uiState = MutableStateFlow(
        KioskUiState(
            skin = prefs.getSkin(),
            pollingMode = prefs.getPollingMode(),
            tiles = prefs.loadTiles(),
            useFahrenheit = prefs.isUseFahrenheit(),
            isGlyphPulseEnabled = prefs.isGlyphPulseEnabled(),
            isBedtimeEcoActive = prefs.isBedtimeEcoActive(),
            isTelemetryRecording = prefs.isTelemetryRecording(),
            pinnedWidgetCount = KioskWidgetUpdater.getPinnedWidgetCount(appContext)
        )
    )
    val uiState: StateFlow<KioskUiState> = _uiState.asStateFlow()

    private var tickerJob: Job? = null
    private var widgetSyncTickCounter = 0

    init {
        startTelemetryTicker()
    }

    private fun startTelemetryTicker() {
        tickerJob?.cancel()
        tickerJob = viewModelScope.launch {
            while (isActive) {
                val snap = telemetryEngine.sampleSnapshot()
                val currentState = _uiState.value
                val newRecorded = if (currentState.isTelemetryRecording) {
                    currentState.recordedSamplesCount + 1
                } else {
                    currentState.recordedSamplesCount
                }

                _uiState.update { state ->
                    state.copy(
                        snapshot = snap,
                        recordedSamplesCount = newRecorded,
                        pinnedWidgetCount = KioskWidgetUpdater.getPinnedWidgetCount(appContext)
                    )
                }

                // Periodically keep standalone Home Screen widgets synced
                widgetSyncTickCounter++
                if (widgetSyncTickCounter % 3 == 0) {
                    KioskWidgetUpdater.updateAllWidgets(appContext, snap)
                }

                val baseDelay = _uiState.value.pollingMode.intervalMs
                val actualDelay = if (_uiState.value.isBedtimeEcoActive) {
                    (baseDelay * 2L).coerceAtMost(6000L)
                } else {
                    baseDelay
                }
                delay(actualDelay)
            }
        }
    }

    fun selectSkin(skin: KioskSkin) {
        prefs.setSkin(skin)
        _uiState.update {
            it.copy(
                skin = skin,
                statusBannerMessage = "Switched skin to ${skin.title}"
            )
        }
        KioskWidgetUpdater.updateAllWidgets(appContext, _uiState.value.snapshot)
    }

    fun cycleSkin() {
        val next = when (_uiState.value.skin) {
            KioskSkin.NOTHING_DOT_MATRIX -> KioskSkin.WIN_XP_LUNA
            KioskSkin.WIN_XP_LUNA -> KioskSkin.WIN_XP_CLASSIC
            KioskSkin.WIN_XP_CLASSIC -> KioskSkin.NOTHING_DOT_MATRIX
        }
        selectSkin(next)
    }

    fun setPollingMode(mode: PollingMode) {
        prefs.setPollingMode(mode)
        _uiState.update {
            it.copy(
                pollingMode = mode,
                statusBannerMessage = "Sampling set to ${mode.label}"
            )
        }
        startTelemetryTicker()
    }

    fun toggleEditGridMode() {
        _uiState.update {
            val next = !it.isEditGridMode
            it.copy(
                isEditGridMode = next,
                isStartMenuOpen = false,
                statusBannerMessage = if (next) {
                    "Grid Unlocked: Drag/reorder, resize spans (1×1 to 4×2), or invert card colors"
                } else {
                    "Desktop Grid Locked"
                }
            )
        }
    }

    fun cycleTileSpan(tileId: String) {
        val updated = _uiState.value.tiles.map { tile ->
            if (tile.id == tileId) tile.copy(span = tile.span.next()) else tile
        }
        prefs.saveTiles(updated)
        _uiState.update { it.copy(tiles = updated) }
    }

    fun cycleTileSurfaceVariant(tileId: String) {
        val updated = _uiState.value.tiles.map { tile ->
            if (tile.id == tileId) tile.copy(surfaceVariant = tile.surfaceVariant.next()) else tile
        }
        prefs.saveTiles(updated)
        _uiState.update { it.copy(tiles = updated) }
    }

    fun moveTileOrder(tileId: String, delta: Int) {
        val current = _uiState.value.tiles.toMutableList()
        val index = current.indexOfFirst { it.id == tileId }
        if (index == -1) return
        val targetIndex = (index + delta).coerceIn(0, current.lastIndex)
        if (targetIndex == index) return
        val item = current.removeAt(index)
        current.add(targetIndex, item)
        val reindexed = current.mapIndexed { idx, tile -> tile.copy(order = idx) }
        prefs.saveTiles(reindexed)
        _uiState.update { it.copy(tiles = reindexed) }
    }

    fun swapTiles(fromId: String, toId: String) {
        if (fromId == toId) return
        val current = _uiState.value.tiles.toMutableList()
        val fromIdx = current.indexOfFirst { it.id == fromId }
        val toIdx = current.indexOfFirst { it.id == toId }
        if (fromIdx == -1 || toIdx == -1) return
        val temp = current[fromIdx]
        current[fromIdx] = current[toIdx]
        current[toIdx] = temp
        val reindexed = current.mapIndexed { idx, tile -> tile.copy(order = idx) }
        prefs.saveTiles(reindexed)
        _uiState.update { it.copy(tiles = reindexed) }
    }

    fun toggleTileVisibility(tileId: String) {
        val current = _uiState.value.tiles
        val visibleCount = current.count { it.isVisible }
        val updated = current.map { tile ->
            if (tile.id == tileId) {
                if (tile.isVisible && visibleCount <= 1) tile else tile.copy(isVisible = !tile.isVisible)
            } else tile
        }
        prefs.saveTiles(updated)
        _uiState.update { it.copy(tiles = updated) }
    }

    fun resetGridLayout() {
        val defaults = prefs.resetTiles()
        _uiState.update {
            it.copy(
                tiles = defaults,
                statusBannerMessage = "Desktop widget grid restored to default layout"
            )
        }
    }

    fun toggleStartMenu() {
        _uiState.update { it.copy(isStartMenuOpen = !it.isStartMenuOpen) }
    }

    fun closeStartMenu() {
        _uiState.update { it.copy(isStartMenuOpen = false) }
    }

    fun openProcessInspector() {
        _uiState.update {
            it.copy(
                isProcessInspectorOpen = true,
                isStartMenuOpen = false
            )
        }
    }

    fun closeProcessInspector() {
        _uiState.update { it.copy(isProcessInspectorOpen = false) }
    }

    fun openWidgetConfig() {
        _uiState.update {
            it.copy(
                isWidgetConfigOpen = true,
                isStartMenuOpen = false,
                pinnedWidgetCount = KioskWidgetUpdater.getPinnedWidgetCount(appContext)
            )
        }
    }

    fun closeWidgetConfig() {
        _uiState.update { it.copy(isWidgetConfigOpen = false) }
    }

    fun triggerMemoryTrimAndGc() {
        System.gc()
        Runtime.getRuntime().gc()
        val snap = telemetryEngine.sampleSnapshot()
        _uiState.update {
            it.copy(
                snapshot = snap,
                statusBannerMessage = "Heap GC & memory trim completed (${String.format("%.1f", snap.appHeapAllocatedMb)} MB active)"
            )
        }
        KioskWidgetUpdater.updateAllWidgets(appContext, snap)
    }

    fun forceRefreshTelemetry() {
        val snap = telemetryEngine.sampleSnapshot()
        KioskWidgetUpdater.updateAllWidgets(appContext, snap)
        _uiState.update {
            it.copy(
                snapshot = snap,
                pinnedWidgetCount = KioskWidgetUpdater.getPinnedWidgetCount(appContext),
                statusBannerMessage = "Telemetry & standalone Home Screen widgets synced"
            )
        }
    }

    fun requestPinHomeWidget(isWideDeck: Boolean) {
        val supported = KioskWidgetUpdater.requestPinWidget(appContext, isWideDeck)
        val label = if (isWideDeck) "Tablet Telemetry Deck (4×2)" else "Compact CPU/RAM Gauge (3×2)"
        _uiState.update {
            it.copy(
                statusBannerMessage = if (supported) {
                    "Pin request sent for $label widget"
                } else {
                    "Synced $label — long-press your Home Screen to place widget"
                }
            )
        }
        KioskWidgetUpdater.updateAllWidgets(appContext, _uiState.value.snapshot)
    }

    fun toggleTemperatureUnit() {
        val next = !_uiState.value.useFahrenheit
        prefs.setUseFahrenheit(next)
        _uiState.update {
            it.copy(
                useFahrenheit = next,
                statusBannerMessage = if (next) "Thermal unit: Fahrenheit (°F)" else "Thermal unit: Celsius (°C)"
            )
        }
    }

    fun toggleGlyphPulse() {
        val next = !_uiState.value.isGlyphPulseEnabled
        prefs.setGlyphPulseEnabled(next)
        _uiState.update {
            it.copy(
                isGlyphPulseEnabled = next,
                statusBannerMessage = if (next) "Glyph Matrix LEDs Active" else "Glyph Matrix Static Mode"
            )
        }
    }

    fun toggleBedtimeEcoMode() {
        val next = !_uiState.value.isBedtimeEcoActive
        prefs.setBedtimeEcoActive(next)
        _uiState.update {
            it.copy(
                isBedtimeEcoActive = next,
                statusBannerMessage = if (next) "Bedtime Eco Governor Enabled (Low-frequency polling)" else "Bedtime Eco Governor Off"
            )
        }
    }

    fun toggleTelemetryRecording() {
        val next = !_uiState.value.isTelemetryRecording
        prefs.setTelemetryRecording(next)
        _uiState.update {
            it.copy(
                isTelemetryRecording = next,
                recordedSamplesCount = if (next) 1 else it.recordedSamplesCount,
                statusBannerMessage = if (next) "Telemetry Log Recording Started" else "Telemetry Log Saved (${it.recordedSamplesCount} samples)"
            )
        }
    }

    fun toggleAutoRotateQuickPill() {
        val next = !_uiState.value.isAutoRotateSimulated
        _uiState.update {
            it.copy(
                isAutoRotateSimulated = next,
                statusBannerMessage = if (next) "Auto-rotate Lock: Unlocked (Tablet Landscape/Portrait)" else "Orientation Locked"
            )
        }
    }

    fun toggleBluetoothQuickPill(openSystemSettings: Boolean = false) {
        if (openSystemSettings) {
            launchSystemSettingSafely(Settings.ACTION_BLUETOOTH_SETTINGS)
        } else {
            val next = !_uiState.value.isBluetoothSimulatedOn
            _uiState.update {
                it.copy(
                    isBluetoothSimulatedOn = next,
                    statusBannerMessage = if (next) "Bluetooth Radio Telemetry Active" else "Bluetooth Radio Muted"
                )
            }
        }
    }

    fun toggleMobileDataQuickPill(openSystemSettings: Boolean = false) {
        if (openSystemSettings) {
            launchSystemSettingSafely(Settings.ACTION_WIRELESS_SETTINGS)
        } else {
            val next = !_uiState.value.isMobileDataSimulatedOn
            _uiState.update {
                it.copy(
                    isMobileDataSimulatedOn = next,
                    statusBannerMessage = if (next) "Network Stream Counter Active" else "Network Stream Paused"
                )
            }
        }
    }

    fun openSystemDisplaySettings() {
        launchSystemSettingSafely(Settings.ACTION_DISPLAY_SETTINGS)
    }

    fun dismissStatusBanner() {
        _uiState.update { it.copy(statusBannerMessage = null) }
    }

    private fun launchSystemSettingSafely(action: String) {
        runCatching {
            val intent = Intent(action).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            appContext.startActivity(intent)
        }.onFailure {
            _uiState.update {
                it.copy(statusBannerMessage = "System panel shortcut triggered ($action)")
            }
        }
    }
}
