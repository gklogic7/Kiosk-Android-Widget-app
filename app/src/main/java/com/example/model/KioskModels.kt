package com.example.model

enum class KioskSkin(
    val id: String,
    val title: String,
    val subtitle: String,
    val badgeText: String
) {
    NOTHING_DOT_MATRIX(
        id = "nothing_dot_matrix",
        title = "Nothing OS Dot-Matrix",
        subtitle = "High-contrast monochrome circles, pills, dot-matrix numerals & crimson accents",
        badgeText = "GLYPH OS"
    ),
    WIN_XP_LUNA(
        id = "win_xp_luna",
        title = "Windows XP Luna Blue",
        subtitle = "Royal blue beveled frames, green Start button, classic icons & CRT graphs",
        badgeText = "XP LUNA"
    ),
    WIN_XP_CLASSIC(
        id = "win_xp_classic",
        title = "Windows XP Classic",
        subtitle = "Chiseled 3D Win2000/XP gray bevels, navy headers & retro system tray",
        badgeText = "CLASSIC 3D"
    );

    companion object {
        fun fromId(id: String?): KioskSkin = entries.find { it.id == id } ?: NOTHING_DOT_MATRIX
    }
}

enum class PollingMode(
    val id: String,
    val label: String,
    val intervalMs: Long,
    val overheadNote: String
) {
    REAL_TIME_1S("realtime", "1.0s Real-Time", 1000L, "Instant desktop telemetry (~0.25% CPU)"),
    BALANCED_2S("balanced", "2.0s Balanced", 2000L, "Recommended tablet cadence (~0.12% CPU)"),
    ECO_5S("eco", "5.0s Ultra-Eco", 5000L, "Minimal battery footprint (<0.05% CPU)");

    companion object {
        fun fromId(id: String?): PollingMode = entries.find { it.id == id } ?: BALANCED_2S
    }
}

enum class TileSpan(
    val id: String,
    val label: String,
    val colSpan: Int,
    val heightDp: Int
) {
    CIRCLE_1X1("1x1", "1×1 Compact", 1, 168),
    PILL_2X1("2x1", "2×1 Pill / Wide", 2, 168),
    SQUIRCLE_2X2("2x2", "2×2 Squircle", 2, 236),
    BANNER_4X2("4x2", "4×2 Deck", 4, 236);

    fun next(): TileSpan = when (this) {
        CIRCLE_1X1 -> PILL_2X1
        PILL_2X1 -> SQUIRCLE_2X2
        SQUIRCLE_2X2 -> BANNER_4X2
        BANNER_4X2 -> CIRCLE_1X1
    }

    companion object {
        fun fromId(id: String?): TileSpan = entries.find { it.id == id } ?: SQUIRCLE_2X2
    }
}

enum class TileSurfaceVariant {
    DARK_CHARCOAL,
    STARK_WHITE,
    CRIMSON_RED;

    fun next(): TileSurfaceVariant = when (this) {
        DARK_CHARCOAL -> STARK_WHITE
        STARK_WHITE -> CRIMSON_RED
        CRIMSON_RED -> DARK_CHARCOAL
    }
}

enum class TileType(
    val id: String,
    val title: String,
    val defaultSpan: TileSpan,
    val defaultVariant: TileSurfaceVariant
) {
    CPU_DIAL("cpu_dial", "CPU Core & Analog Dial", TileSpan.SQUIRCLE_2X2, TileSurfaceVariant.DARK_CHARCOAL),
    WEEKLY_DOT_CHART("weekly_dots", "Weekly Load Dot-Matrix", TileSpan.PILL_2X1, TileSurfaceVariant.STARK_WHITE),
    RAM_MONITOR("ram_monitor", "Memory & Heap Matrix", TileSpan.SQUIRCLE_2X2, TileSurfaceVariant.DARK_CHARCOAL),
    EDITORIAL_METRIC("editorial_metric", "Process & Thread Density", TileSpan.SQUIRCLE_2X2, TileSurfaceVariant.DARK_CHARCOAL),
    THERMAL_CORE_DECK("thermal_deck", "Thermal & Per-Core Deck", TileSpan.BANNER_4X2, TileSurfaceVariant.DARK_CHARCOAL),
    QUICK_TOGGLE_PILLS("quick_toggles", "Quick System Pills & Circles", TileSpan.SQUIRCLE_2X2, TileSurfaceVariant.DARK_CHARCOAL),
    CLOCK_GMT_TILE("clock_gmt", "Uptime & GMT Dot Clock", TileSpan.SQUIRCLE_2X2, TileSurfaceVariant.STARK_WHITE),
    STORAGE_COMPASS("storage_compass", "Storage & Disk Vector", TileSpan.SQUIRCLE_2X2, TileSurfaceVariant.DARK_CHARCOAL),
    BATTERY_GLYPH("battery_glyph", "Battery & Thermal Glyph", TileSpan.CIRCLE_1X1, TileSurfaceVariant.CRIMSON_RED),
    NETWORK_STREAM("network_stream", "Network RX/TX Throughput", TileSpan.SQUIRCLE_2X2, TileSurfaceVariant.DARK_CHARCOAL),
    INSPIRATION_BOARD("inspiration_board", "Design Reference Deck", TileSpan.CIRCLE_1X1, TileSurfaceVariant.STARK_WHITE);
}

data class DesktopTileConfig(
    val id: String,
    val type: TileType,
    val span: TileSpan = type.defaultSpan,
    val surfaceVariant: TileSurfaceVariant = type.defaultVariant,
    val isVisible: Boolean = true,
    val order: Int = 0
)

data class CpuCoreInfo(
    val coreIndex: Int,
    val currentFreqMhz: Int,
    val maxFreqMhz: Int,
    val minFreqMhz: Int,
    val loadPercent: Int
)

data class ProcessDetailItem(
    val pid: Int,
    val processName: String,
    val packageName: String,
    val memoryMb: Float,
    val threadsCount: Int,
    val importanceLabel: String,
    val cpuSharePercent: Float
)

data class DayLoadMetric(
    val dayShort: String,
    val loadPercent: Int,
    val isPeak: Boolean,
    val isToday: Boolean
)

data class TelemetrySnapshot(
    val timestampMs: Long = System.currentTimeMillis(),
    val cpuLoadPercent: Int = 18,
    val cpuCoresCount: Int = Runtime.getRuntime().availableProcessors().coerceAtLeast(4),
    val perCoreInfo: List<CpuCoreInfo> = emptyList(),
    val averageFreqMhz: Int = 1840,
    val maxFreqMhz: Int = 2850,
    val ramUsedMb: Long = 3420L,
    val ramTotalMb: Long = 8192L,
    val ramAvailableMb: Long = 4772L,
    val ramThresholdMb: Long = 512L,
    val ramUsagePercent: Int = 42,
    val appHeapAllocatedMb: Float = 18.4f,
    val appHeapMaxMb: Float = 256f,
    val nativeHeapAllocatedMb: Float = 11.2f,
    val storageUsedGb: Float = 42.6f,
    val storageTotalGb: Float = 128.0f,
    val storageFreeGb: Float = 85.4f,
    val storageUsagePercent: Int = 33,
    val batteryPercent: Int = 86,
    val batteryTempCelsius: Float = 31.4f,
    val batteryVoltageMv: Int = 4120,
    val isCharging: Boolean = false,
    val networkRxKbps: Float = 124.5f,
    val networkTxKbps: Float = 38.2f,
    val sessionRxMb: Float = 48.2f,
    val sessionTxMb: Float = 12.6f,
    val uptimeFormatted: String = "04:22:18",
    val activeThreadCount: Int = 24,
    val cpuHistory: List<Int> = List(36) { 15 },
    val ramHistory: List<Int> = List(36) { 42 },
    val weeklyLoadDots: List<DayLoadMetric> = emptyList(),
    val processList: List<ProcessDetailItem> = emptyList()
)
