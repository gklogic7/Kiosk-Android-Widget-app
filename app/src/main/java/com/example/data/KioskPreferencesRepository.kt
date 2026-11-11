package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.DesktopTileConfig
import com.example.model.KioskSkin
import com.example.model.PollingMode
import com.example.model.TileSpan
import com.example.model.TileSurfaceVariant
import com.example.model.TileType

class KioskPreferencesRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getSkin(): KioskSkin {
        val raw = prefs.getString(KEY_SKIN, KioskSkin.NOTHING_DOT_MATRIX.id)
        return KioskSkin.fromId(raw)
    }

    fun setSkin(skin: KioskSkin) {
        prefs.edit().putString(KEY_SKIN, skin.id).apply()
    }

    fun getPollingMode(): PollingMode {
        val raw = prefs.getString(KEY_POLLING_MODE, PollingMode.BALANCED_2S.id)
        return PollingMode.fromId(raw)
    }

    fun setPollingMode(mode: PollingMode) {
        prefs.edit().putString(KEY_POLLING_MODE, mode.id).apply()
    }

    fun isUseFahrenheit(): Boolean = prefs.getBoolean(KEY_USE_FAHRENHEIT, false)

    fun setUseFahrenheit(useFahrenheit: Boolean) {
        prefs.edit().putBoolean(KEY_USE_FAHRENHEIT, useFahrenheit).apply()
    }

    fun isGlyphPulseEnabled(): Boolean = prefs.getBoolean(KEY_GLYPH_PULSE, true)

    fun setGlyphPulseEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_GLYPH_PULSE, enabled).apply()
    }

    fun isBedtimeEcoActive(): Boolean = prefs.getBoolean(KEY_BEDTIME_ECO, false)

    fun setBedtimeEcoActive(active: Boolean) {
        prefs.edit().putBoolean(KEY_BEDTIME_ECO, active).apply()
    }

    fun isTelemetryRecording(): Boolean = prefs.getBoolean(KEY_TELEMETRY_RECORDING, false)

    fun setTelemetryRecording(recording: Boolean) {
        prefs.edit().putBoolean(KEY_TELEMETRY_RECORDING, recording).apply()
    }

    fun loadTiles(): List<DesktopTileConfig> {
        val raw = prefs.getString(KEY_TILES_SPEC, null)
        if (raw.isNullOrBlank()) {
            return defaultTiles()
        }
        return try {
            val parsed = raw.split(";").mapNotNull { entry ->
                val parts = entry.split("|")
                if (parts.size < 5) return@mapNotNull null
                val type = TileType.entries.find { it.id == parts[0] } ?: return@mapNotNull null
                val span = TileSpan.fromId(parts[1])
                val variant = runCatching { TileSurfaceVariant.valueOf(parts[2]) }
                    .getOrDefault(type.defaultVariant)
                val visible = parts[3].toBooleanStrictOrNull() ?: true
                val order = parts[4].toIntOrNull() ?: 0
                DesktopTileConfig(
                    id = type.id,
                    type = type,
                    span = span,
                    surfaceVariant = variant,
                    isVisible = visible,
                    order = order
                )
            }
            if (parsed.isEmpty()) defaultTiles() else {
                val missing = TileType.entries.filter { t -> parsed.none { it.type == t } }
                    .mapIndexed { idx, t ->
                        DesktopTileConfig(
                            id = t.id,
                            type = t,
                            span = t.defaultSpan,
                            surfaceVariant = t.defaultVariant,
                            isVisible = true,
                            order = parsed.size + idx
                        )
                    }
                (parsed + missing).sortedBy { it.order }.mapIndexed { index, item ->
                    item.copy(order = index)
                }
            }
        } catch (_: Exception) {
            defaultTiles()
        }
    }

    fun saveTiles(tiles: List<DesktopTileConfig>) {
        val serialized = tiles.mapIndexed { idx, tile ->
            "${tile.type.id}|${tile.span.id}|${tile.surfaceVariant.name}|${tile.isVisible}|$idx"
        }.joinToString(";")
        prefs.edit().putString(KEY_TILES_SPEC, serialized).apply()
    }

    fun resetTiles(): List<DesktopTileConfig> {
        prefs.edit().remove(KEY_TILES_SPEC).apply()
        return defaultTiles()
    }

    companion object {
        private const val PREFS_NAME = "kiosk_desktop_prefs"
        private const val KEY_SKIN = "active_skin"
        private const val KEY_POLLING_MODE = "polling_mode"
        private const val KEY_USE_FAHRENHEIT = "use_fahrenheit"
        private const val KEY_GLYPH_PULSE = "glyph_pulse_enabled"
        private const val KEY_BEDTIME_ECO = "bedtime_eco_active"
        private const val KEY_TELEMETRY_RECORDING = "telemetry_recording"
        private const val KEY_TILES_SPEC = "tiles_layout_v1"

        fun defaultTiles(): List<DesktopTileConfig> = listOf(
            DesktopTileConfig(
                id = TileType.CPU_DIAL.id,
                type = TileType.CPU_DIAL,
                span = TileSpan.SQUIRCLE_2X2,
                surfaceVariant = TileSurfaceVariant.DARK_CHARCOAL,
                isVisible = true,
                order = 0
            ),
            DesktopTileConfig(
                id = TileType.WEEKLY_DOT_CHART.id,
                type = TileType.WEEKLY_DOT_CHART,
                span = TileSpan.PILL_2X1,
                surfaceVariant = TileSurfaceVariant.STARK_WHITE,
                isVisible = true,
                order = 1
            ),
            DesktopTileConfig(
                id = TileType.EDITORIAL_METRIC.id,
                type = TileType.EDITORIAL_METRIC,
                span = TileSpan.SQUIRCLE_2X2,
                surfaceVariant = TileSurfaceVariant.DARK_CHARCOAL,
                isVisible = true,
                order = 2
            ),
            DesktopTileConfig(
                id = TileType.BATTERY_GLYPH.id,
                type = TileType.BATTERY_GLYPH,
                span = TileSpan.CIRCLE_1X1,
                surfaceVariant = TileSurfaceVariant.CRIMSON_RED,
                isVisible = true,
                order = 3
            ),
            DesktopTileConfig(
                id = TileType.INSPIRATION_BOARD.id,
                type = TileType.INSPIRATION_BOARD,
                span = TileSpan.CIRCLE_1X1,
                surfaceVariant = TileSurfaceVariant.STARK_WHITE,
                isVisible = true,
                order = 4
            ),
            DesktopTileConfig(
                id = TileType.THERMAL_CORE_DECK.id,
                type = TileType.THERMAL_CORE_DECK,
                span = TileSpan.BANNER_4X2,
                surfaceVariant = TileSurfaceVariant.DARK_CHARCOAL,
                isVisible = true,
                order = 5
            ),
            DesktopTileConfig(
                id = TileType.RAM_MONITOR.id,
                type = TileType.RAM_MONITOR,
                span = TileSpan.SQUIRCLE_2X2,
                surfaceVariant = TileSurfaceVariant.DARK_CHARCOAL,
                isVisible = true,
                order = 6
            ),
            DesktopTileConfig(
                id = TileType.CLOCK_GMT_TILE.id,
                type = TileType.CLOCK_GMT_TILE,
                span = TileSpan.SQUIRCLE_2X2,
                surfaceVariant = TileSurfaceVariant.STARK_WHITE,
                isVisible = true,
                order = 7
            ),
            DesktopTileConfig(
                id = TileType.QUICK_TOGGLE_PILLS.id,
                type = TileType.QUICK_TOGGLE_PILLS,
                span = TileSpan.SQUIRCLE_2X2,
                surfaceVariant = TileSurfaceVariant.DARK_CHARCOAL,
                isVisible = true,
                order = 8
            ),
            DesktopTileConfig(
                id = TileType.STORAGE_COMPASS.id,
                type = TileType.STORAGE_COMPASS,
                span = TileSpan.SQUIRCLE_2X2,
                surfaceVariant = TileSurfaceVariant.DARK_CHARCOAL,
                isVisible = true,
                order = 9
            ),
            DesktopTileConfig(
                id = TileType.NETWORK_STREAM.id,
                type = TileType.NETWORK_STREAM,
                span = TileSpan.SQUIRCLE_2X2,
                surfaceVariant = TileSurfaceVariant.DARK_CHARCOAL,
                isVisible = true,
                order = 10
            )
        )
    }
}
