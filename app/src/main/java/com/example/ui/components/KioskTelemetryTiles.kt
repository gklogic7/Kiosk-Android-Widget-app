package com.example.ui.components

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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.DesktopTileConfig
import com.example.model.KioskSkin
import com.example.model.TelemetrySnapshot
import com.example.model.TileSpan
import com.example.ui.theme.CleanSansFontFamily
import com.example.ui.theme.CrtGridDarkGreen
import com.example.ui.theme.CrtMonitorBlack
import com.example.ui.theme.CrtTraceNeonGreen
import com.example.ui.theme.CrtTraceYellow
import com.example.ui.theme.DotMatrixFontFamily
import com.example.ui.theme.EditorialSerifFontFamily
import com.example.ui.theme.NothingCharcoal
import com.example.ui.theme.NothingCrimsonRed
import com.example.ui.theme.NothingStarkWhite
import com.example.ui.theme.TelemetryMonoFontFamily
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun CpuDialTileContent(
    skin: KioskSkin,
    snapshot: TelemetrySnapshot,
    span: TileSpan,
    contentColor: Color,
    mutedColor: Color,
    accentColor: Color
) {
    if (skin != KioskSkin.NOTHING_DOT_MATRIX) {
        XpCrtMeterContent(
            title = "CPU USAGE HISTORY",
            primaryValue = "${snapshot.cpuLoadPercent}%",
            subValue = "${snapshot.averageFreqMhz} MHz (${snapshot.cpuCoresCount} Cores)",
            history = snapshot.cpuHistory,
            traceColor = CrtTraceNeonGreen,
            showBarMeter = span != TileSpan.CIRCLE_1X1
        )
        return
    }

    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left Analog Dial (Directly inspired by the circular analog clock with thick white hand & red bottom dot in reference image)
        Box(
            modifier = Modifier
                .size(if (span == TileSpan.CIRCLE_1X1) 128.dp else 148.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val r = size.minDimension / 2f
                val center = Offset(size.width / 2f, size.height / 2f)

                // Subtle inner dial circle
                drawCircle(
                    color = Color(0xFF232329),
                    radius = r * 0.95f,
                    center = center
                )

                // Red indicator dot at bottom (6 o'clock position, matching reference image)
                drawCircle(
                    color = NothingCrimsonRed,
                    radius = 5.dp.toPx(),
                    center = Offset(center.x, center.y + r * 0.80f)
                )

                // Secondary subtle max-freq hand
                val freqRatio = (snapshot.averageFreqMhz.toFloat() / snapshot.maxFreqMhz.coerceAtLeast(1)).coerceIn(0.1f, 1f)
                val secAngle = Math.toRadians((-110.0 + freqRatio * 240.0))
                drawLine(
                    color = Color(0xFF686873),
                    start = center,
                    end = Offset(
                        center.x + (r * 0.72f) * cos(secAngle).toFloat(),
                        center.y + (r * 0.72f) * sin(secAngle).toFloat()
                    ),
                    strokeWidth = 4.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Primary Thick White Pill Hand for CPU Load %
                val cpuAngle = Math.toRadians((-145.0 + snapshot.cpuLoadPercent * 2.8))
                drawLine(
                    color = Color.White,
                    start = center,
                    end = Offset(
                        center.x + (r * 0.56f) * cos(cpuAngle).toFloat(),
                        center.y + (r * 0.56f) * sin(cpuAngle).toFloat()
                    ),
                    strokeWidth = 11.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Center pivot
                drawCircle(color = Color(0xFF18181C), radius = 3.5.dp.toPx(), center = center)
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 14.dp)
            ) {
                Text(
                    text = "${snapshot.cpuLoadPercent}%",
                    fontFamily = DotMatrixFontFamily,
                    fontSize = 26.sp,
                    color = contentColor
                )
            }
        }

        if (span != TileSpan.CIRCLE_1X1) {
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CPU MATRIX",
                        fontFamily = TelemetryMonoFontFamily,
                        fontSize = 12.sp,
                        color = mutedColor
                    )
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(accentColor)
                    )
                }

                Text(
                    text = "${snapshot.averageFreqMhz} MHz",
                    fontFamily = DotMatrixFontFamily,
                    fontSize = 32.sp,
                    color = contentColor
                )

                // Dot-matrix sparkline
                DotMatrixSparkline(
                    values = snapshot.cpuHistory.takeLast(14),
                    activeColor = contentColor,
                    peakColor = NothingCrimsonRed,
                    inactiveColor = mutedColor.copy(alpha = 0.25f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                )

                Text(
                    text = "${snapshot.cpuCoresCount} CORES • TAP FOR TASKS",
                    fontFamily = TelemetryMonoFontFamily,
                    fontSize = 11.sp,
                    color = mutedColor,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun WeeklyDotChartTileContent(
    skin: KioskSkin,
    snapshot: TelemetrySnapshot,
    contentColor: Color,
    mutedColor: Color
) {
    val days = snapshot.weeklyLoadDots
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "PEAK CPU THRESHOLD • 7-DAY MATRIX",
                fontFamily = TelemetryMonoFontFamily,
                fontSize = 11.sp,
                color = mutedColor
            )
            Text(
                text = "MAX ${days.maxOfOrNull { it.loadPercent } ?: 82}%",
                fontFamily = TelemetryMonoFontFamily,
                fontSize = 11.sp,
                color = NothingCrimsonRed,
                fontWeight = FontWeight.Bold
            )
        }

        // Top horizontal threshold rule + 3-dot clusters per day (Directly matching top-center white card in reference image!)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 6.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val peakLineY = h * 0.22f

                // Crisp threshold horizontal line across the chart
                drawLine(
                    color = mutedColor.copy(alpha = 0.45f),
                    start = Offset(0f, peakLineY),
                    end = Offset(w, peakLineY),
                    strokeWidth = 1.2.dp.toPx()
                )

                if (days.isNotEmpty()) {
                    val colWidth = w / days.size
                    val dotRadius = 3.8.dp.toPx()
                    val dotSpacing = 9.5.dp.toPx()

                    days.forEachIndexed { idx, item ->
                        val centerX = colWidth * idx + colWidth / 2f
                        val normalized = (item.loadPercent.coerceIn(20, 95) - 20) / 75f
                        val clusterY = if (item.isPeak) {
                            peakLineY
                        } else {
                            h * 0.82f - normalized * (h * 0.55f)
                        }

                        val dotColor = when {
                            item.isPeak -> NothingCrimsonRed
                            item.isToday -> if (skin == KioskSkin.NOTHING_DOT_MATRIX) Color(0xFF111114) else CrtTraceNeonGreen
                            else -> mutedColor.copy(alpha = 0.7f)
                        }

                        // Draw the 3 horizontal dots (•••) for this day
                        for (d in -1..1) {
                            drawCircle(
                                color = dotColor,
                                radius = dotRadius,
                                center = Offset(centerX + d * dotSpacing, clusterY)
                            )
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            days.forEach { day ->
                Text(
                    text = day.dayShort,
                    fontFamily = CleanSansFontFamily,
                    fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 11.sp,
                    color = if (day.isToday) contentColor else mutedColor
                )
            }
        }
    }
}

@Composable
fun EditorialMetricTileContent(
    snapshot: TelemetrySnapshot,
    contentColor: Color,
    mutedColor: Color,
    accentColor: Color
) {
    // Inspired directly by the "35 mm ROADTRIP VIDEO" editorial serif card in the reference image
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "${snapshot.appHeapAllocatedMb.roundToInt()}",
                    fontFamily = EditorialSerifFontFamily,
                    fontSize = 52.sp,
                    color = contentColor,
                    lineHeight = 50.sp
                )
                Box(
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(accentColor)
                )
            }
            Text(
                text = "mb",
                fontFamily = EditorialSerifFontFamily,
                fontSize = 40.sp,
                color = contentColor,
                lineHeight = 40.sp
            )
        }

        Column {
            Text(
                text = "KIOSK HEAP FOOTPRINT",
                fontFamily = DotMatrixFontFamily,
                fontSize = 17.sp,
                color = mutedColor,
                letterSpacing = 1.2.sp
            )
            Text(
                text = "${snapshot.activeThreadCount} THREADS • ULTRA-LOW",
                fontFamily = DotMatrixFontFamily,
                fontSize = 16.sp,
                color = mutedColor,
                letterSpacing = 1.2.sp
            )
        }
    }
}

@Composable
fun BatteryGlyphTileContent(
    snapshot: TelemetrySnapshot,
    useFahrenheit: Boolean,
    isGlyphPulseEnabled: Boolean,
    onToggleTempUnit: () -> Unit,
    onToggleGlyph: () -> Unit
) {
    val tempVal = if (useFahrenheit) {
        ((snapshot.batteryTempCelsius * 9f / 5f) + 32f).roundToInt()
    } else {
        snapshot.batteryTempCelsius.roundToInt()
    }
    val unitSymbol = if (useFahrenheit) "°F" else "°C"

    // 2×2 Cluster of Circular Badges matching the 34° red circle + °C white circle in the reference image
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Crimson Red Thermal Circle ("34°")
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(NothingCrimsonRed)
                    .clickable { onToggleTempUnit() }
                    .testTag("thermal_unit_circle"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$tempVal°",
                    fontFamily = CleanSansFontFamily,
                    fontWeight = FontWeight.Light,
                    fontSize = 26.sp,
                    color = Color.White
                )
            }

            // Stark White Unit / Glyph Circle ("°C")
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(NothingStarkWhite)
                    .clickable { onToggleGlyph() }
                    .testTag("glyph_pulse_circle"),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(Modifier.size(4.dp).clip(CircleShape).background(Color.Black))
                        Box(Modifier.size(4.dp).clip(CircleShape).background(if (isGlyphPulseEnabled) NothingCrimsonRed else Color.Black))
                    }
                    Text(
                        text = unitSymbol,
                        fontFamily = DotMatrixFontFamily,
                        fontSize = 22.sp,
                        color = Color(0xFF111114)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF111114).copy(alpha = 0.45f))
                .padding(horizontal = 10.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "BAT ${snapshot.batteryPercent}%",
                fontFamily = DotMatrixFontFamily,
                fontSize = 16.sp,
                color = Color.White
            )
            Text(
                text = if (snapshot.isCharging) "CHG" else "${snapshot.batteryVoltageMv}mV",
                fontFamily = TelemetryMonoFontFamily,
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.85f)
            )
        }
    }
}

@Composable
fun ThermalCoreDeckTileContent(
    skin: KioskSkin,
    snapshot: TelemetrySnapshot,
    useFahrenheit: Boolean,
    contentColor: Color,
    mutedColor: Color,
    onToggleTempUnit: () -> Unit
) {
    // Directly inspired by the center "30° H 35° L 26° Toronto Partly cloudy + 6-column forecast" card in the reference image
    val tempDisplay = if (useFahrenheit) {
        ((snapshot.batteryTempCelsius * 9f / 5f) + 32f).roundToInt()
    } else {
        snapshot.batteryTempCelsius.roundToInt()
    }
    val highTemp = tempDisplay + 5
    val lowTemp = (tempDisplay - 4).coerceAtLeast(20)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable { onToggleTempUnit() },
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Header: Dot-Matrix Cloud/Chip Icon + Large 30° + H/L + Core Governor Status
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Dot-matrix thermal glyph icon
                DotMatrixIconCanvas(
                    pattern = DOT_PATTERN_THERMAL_CLOUD,
                    dotColor = contentColor,
                    accentColor = NothingCrimsonRed,
                    modifier = Modifier.size(42.dp)
                )
                Text(
                    text = "$tempDisplay°",
                    fontFamily = CleanSansFontFamily,
                    fontWeight = FontWeight.Light,
                    fontSize = 44.sp,
                    color = contentColor
                )
                Column {
                    Text(
                        text = "H $highTemp°",
                        fontFamily = TelemetryMonoFontFamily,
                        fontSize = 12.sp,
                        color = contentColor
                    )
                    Text(
                        text = "L $lowTemp°",
                        fontFamily = TelemetryMonoFontFamily,
                        fontSize = 12.sp,
                        color = mutedColor
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(NothingCrimsonRed)
                    )
                    Text(
                        text = "SoC Thermal Deck",
                        fontFamily = CleanSansFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = contentColor
                    )
                }
                Text(
                    text = "${snapshot.cpuCoresCount}-Core Scaling • ${snapshot.averageFreqMhz} MHz",
                    fontFamily = CleanSansFontFamily,
                    fontSize = 12.sp,
                    color = mutedColor
                )
            }
        }

        // Bottom 6-Column Per-Core Frequency & Load Forecast Strip (matching WED THU FRI SAT SUN MON columns)
        val displayCores = snapshot.perCoreInfo.take(6)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            displayCores.forEach { core ->
                val isHighCore = core.loadPercent >= 65
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "CORE ${core.coreIndex}",
                        fontFamily = TelemetryMonoFontFamily,
                        fontSize = 10.sp,
                        color = mutedColor
                    )
                    // Mini dot-matrix status symbol
                    Box(
                        modifier = Modifier
                            .size(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val r = 2.4.dp.toPx()
                            val c = if (isHighCore && skin == KioskSkin.NOTHING_DOT_MATRIX) NothingCrimsonRed else contentColor
                            drawCircle(c, r, Offset(size.width * 0.5f, size.height * 0.25f))
                            drawCircle(c, r, Offset(size.width * 0.25f, size.height * 0.55f))
                            drawCircle(c, r, Offset(size.width * 0.5f, size.height * 0.55f))
                            drawCircle(c, r, Offset(size.width * 0.75f, size.height * 0.55f))
                            drawCircle(c, r, Offset(size.width * 0.5f, size.height * 0.82f))
                        }
                    }
                    Text(
                        text = "${core.loadPercent}%",
                        fontFamily = TelemetryMonoFontFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = contentColor
                    )
                    Text(
                        text = "${core.currentFreqMhz / 100 / 10f}G",
                        fontFamily = TelemetryMonoFontFamily,
                        fontSize = 10.sp,
                        color = mutedColor
                    )
                }
            }
        }
    }
}

@Composable
fun RamMonitorTileContent(
    skin: KioskSkin,
    snapshot: TelemetrySnapshot,
    contentColor: Color,
    mutedColor: Color,
    accentColor: Color
) {
    if (skin != KioskSkin.NOTHING_DOT_MATRIX) {
        XpCrtMeterContent(
            title = "PF / MEMORY USAGE",
            primaryValue = "${snapshot.ramUsedMb} MB",
            subValue = "${snapshot.ramUsagePercent}% of ${snapshot.ramTotalMb} MB",
            history = snapshot.ramHistory,
            traceColor = CrtTraceYellow,
            showBarMeter = true
        )
        return
    }

    val usedGb = snapshot.ramUsedMb / 1024f
    val totalGb = snapshot.ramTotalMb / 1024f

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "MEMORY MATRIX",
                fontFamily = TelemetryMonoFontFamily,
                fontSize = 12.sp,
                color = mutedColor
            )
            Text(
                text = String.format(Locale.US, "%.1f / %.1f GB", usedGb, totalGb),
                fontFamily = TelemetryMonoFontFamily,
                fontSize = 12.sp,
                color = accentColor
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "${snapshot.ramUsagePercent}%",
                fontFamily = DotMatrixFontFamily,
                fontSize = 52.sp,
                color = contentColor,
                lineHeight = 50.sp
            )
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "AVAIL ${snapshot.ramAvailableMb} MB",
                    fontFamily = TelemetryMonoFontFamily,
                    fontSize = 11.sp,
                    color = contentColor
                )
                Text(
                    text = "NATIVE ${String.format(Locale.US, "%.1f", snapshot.nativeHeapAllocatedMb)} MB",
                    fontFamily = TelemetryMonoFontFamily,
                    fontSize = 11.sp,
                    color = mutedColor
                )
            }
        }

        // Dot-Matrix Segmented Memory Grid
        DotMatrixSparkline(
            values = snapshot.ramHistory.takeLast(16),
            activeColor = contentColor,
            peakColor = NothingCrimsonRed,
            inactiveColor = mutedColor.copy(alpha = 0.25f),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        )

        Text(
            text = "TAP TO INSPECT PROCESSES & TRIM HEAP",
            fontFamily = TelemetryMonoFontFamily,
            fontSize = 10.sp,
            color = mutedColor
        )
    }
}

@Composable
fun ClockGmtTileContent(
    snapshot: TelemetrySnapshot,
    contentColor: Color,
    mutedColor: Color
) {
    // Inspired directly by the stark-white "TUESDAY GMT +1" and "22:25" dot-matrix cards in the reference image
    val now = Date(snapshot.timestampMs)
    val dayName = SimpleDateFormat("EEEE", Locale.US).format(now).uppercase(Locale.US)
    val hhmm = SimpleDateFormat("HH:mm", Locale.US).format(now)
    val tzOffsetHours = TimeZone.getDefault().getOffset(snapshot.timestampMs) / (1000 * 3600)
    val gmtString = if (tzOffsetHours >= 0) "GMT +$tzOffsetHours" else "GMT $tzOffsetHours"

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = dayName,
                fontFamily = DotMatrixFontFamily,
                fontSize = 22.sp,
                color = contentColor,
                letterSpacing = 1.5.sp
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(NothingCharcoal)
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = hhmm,
                    fontFamily = DotMatrixFontFamily,
                    fontSize = 18.sp,
                    color = Color.White
                )
            }
        }

        Column {
            Text(
                text = "UPTIME ${snapshot.uptimeFormatted}",
                fontFamily = TelemetryMonoFontFamily,
                fontSize = 12.sp,
                color = mutedColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = gmtString,
                fontFamily = DotMatrixFontFamily,
                fontSize = 46.sp,
                color = contentColor,
                lineHeight = 46.sp
            )
        }
    }
}

@Composable
fun QuickTogglePillsTileContent(
    isBluetoothOn: Boolean,
    isMobileDataOn: Boolean,
    isAutoRotateOn: Boolean,
    isBedtimeEcoOn: Boolean,
    isRecordingOn: Boolean,
    onToggleBluetooth: () -> Unit,
    onToggleMobileData: () -> Unit,
    onToggleAutoRotate: () -> Unit,
    onToggleBedtimeEco: () -> Unit,
    onToggleRecording: () -> Unit
) {
    // Directly models the pill toggles ("Bluetooth", "Mobile data", "Bedtime mode") & circular red/dark toggles ("Auto-rotate", "Screen recorder") in the reference image
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Pill 1: Bluetooth
            NothingQuickPill(
                icon = Icons.Default.Bluetooth,
                label = "Bluetooth",
                isActive = isBluetoothOn,
                activeBg = NothingCharcoal,
                activeFg = Color.White,
                onClick = onToggleBluetooth,
                modifier = Modifier.weight(1.3f).fillMaxHeight(),
                testTag = "pill_bluetooth"
            )
            // Circle 1: Auto-rotate
            NothingQuickCircle(
                icon = Icons.Default.ScreenRotation,
                label = "Auto-rotate",
                bgColor = if (isAutoRotateOn) NothingCrimsonRed else Color(0xFF25252B),
                fgColor = Color.White,
                onClick = onToggleAutoRotate,
                modifier = Modifier.weight(0.8f).fillMaxHeight(),
                testTag = "circle_autorotate"
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth().weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Pill 2: Mobile / Network Data (Stark white pill when active, matching reference image!)
            NothingQuickPill(
                icon = Icons.Default.CellTower,
                label = if (isBedtimeEcoOn) "Bedtime eco" else "Mobile data",
                isActive = isMobileDataOn,
                activeBg = NothingStarkWhite,
                activeFg = Color(0xFF111114),
                onClick = onToggleMobileData,
                modifier = Modifier.weight(1.3f).fillMaxHeight(),
                testTag = "pill_network"
            )
            // Circle 2: Telemetry Recorder / Bedtime Eco
            NothingQuickCircle(
                icon = if (isRecordingOn) Icons.Default.FiberManualRecord else Icons.Default.Bedtime,
                label = if (isRecordingOn) "Recording" else "Eco mode",
                bgColor = if (isRecordingOn) NothingCrimsonRed else if (isBedtimeEcoOn) NothingStarkWhite else Color(0xFF25252B),
                fgColor = if (!isRecordingOn && isBedtimeEcoOn) Color.Black else Color.White,
                onClick = {
                    onToggleBedtimeEco()
                    onToggleRecording()
                },
                modifier = Modifier.weight(0.8f).fillMaxHeight(),
                testTag = "circle_eco_recorder"
            )
        }
    }
}

@Composable
private fun NothingQuickPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isActive: Boolean,
    activeBg: Color,
    activeFg: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String
) {
    val bg = if (isActive) activeBg else Color(0xFF222228)
    val fg = if (isActive) activeFg else Color(0xFF8E8E95)
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(bg)
            .border(1.dp, Color(0xFF33333C), RoundedCornerShape(50))
            .clickable { onClick() }
            .padding(horizontal = 14.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = fg,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = label,
            fontFamily = CleanSansFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
            color = fg,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun NothingQuickCircle(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    bgColor: Color,
    fgColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(bgColor)
            .clickable { onClick() }
            .padding(6.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = fgColor,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontFamily = CleanSansFontFamily,
            fontSize = 9.sp,
            color = fgColor.copy(alpha = 0.9f),
            maxLines = 1
        )
    }
}

@Composable
fun StorageCompassTileContent(
    snapshot: TelemetrySnapshot,
    contentColor: Color,
    mutedColor: Color
) {
    // Inspired directly by the circular N E S W dot-matrix arrow compass card in the reference image
    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier.size(134.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val r = size.minDimension / 2f
                val center = Offset(size.width / 2f, size.height / 2f)
                drawCircle(color = Color(0xFF1F1F24), radius = r * 0.95f, center = center)

                // Draw dotted arrow pointing upward-right based on storage usage
                val dotR = 3.5.dp.toPx()
                val step = 10.dp.toPx()
                for (i in -3..3) {
                    drawCircle(Color.White, dotR, Offset(center.x, center.y + i * step))
                }
                drawCircle(Color.White, dotR, Offset(center.x - step, center.y - step * 2))
                drawCircle(Color.White, dotR, Offset(center.x + step, center.y - step * 2))
                drawCircle(Color.White, dotR, Offset(center.x - step * 2, center.y - step))
                drawCircle(Color.White, dotR, Offset(center.x + step * 2, center.y - step))
            }

            // Cardinal storage labels (N = Red, W/E/S = Muted)
            Text(
                text = "N",
                color = NothingCrimsonRed,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.TopEnd).padding(top = 14.dp, end = 24.dp)
            )
            Text(
                text = "W",
                color = mutedColor,
                fontSize = 10.sp,
                modifier = Modifier.align(Alignment.TopStart).padding(top = 26.dp, start = 16.dp)
            )
            Text(
                text = "E",
                color = contentColor,
                fontSize = 10.sp,
                modifier = Modifier.align(Alignment.BottomEnd).padding(bottom = 26.dp, end = 16.dp)
            )
            Text(
                text = "S",
                color = mutedColor,
                fontSize = 10.sp,
                modifier = Modifier.align(Alignment.BottomStart).padding(bottom = 14.dp, start = 24.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "DISK VECTOR",
                fontFamily = TelemetryMonoFontFamily,
                fontSize = 12.sp,
                color = mutedColor
            )
            Text(
                text = "${snapshot.storageUsagePercent}%",
                fontFamily = DotMatrixFontFamily,
                fontSize = 42.sp,
                color = contentColor
            )
            Text(
                text = String.format(
                    Locale.US,
                    "%.1f GB FREE\n%.1f / %.0f GB",
                    snapshot.storageFreeGb,
                    snapshot.storageUsedGb,
                    snapshot.storageTotalGb
                ),
                fontFamily = TelemetryMonoFontFamily,
                fontSize = 11.sp,
                color = mutedColor,
                lineHeight = 15.sp
            )
        }
    }
}

@Composable
fun NetworkStreamTileContent(
    snapshot: TelemetrySnapshot,
    contentColor: Color,
    mutedColor: Color,
    accentColor: Color
) {
    // Inspired by the bottom-center dark media/note card ("Blood On Me - Sampha") in the reference image
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            // Left rounded square with dot-matrix stream/pulse glyph
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF282830)),
                contentAlignment = Alignment.Center
            ) {
                DotMatrixIconCanvas(
                    pattern = DOT_PATTERN_STREAM_NOTE,
                    dotColor = Color.White,
                    accentColor = NothingCrimsonRed,
                    modifier = Modifier.size(36.dp)
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = String.format(Locale.US, "↓ %.1f KB/s", snapshot.networkRxKbps),
                    fontFamily = DotMatrixFontFamily,
                    fontSize = 24.sp,
                    color = contentColor
                )
                Text(
                    text = String.format(Locale.US, "↑ %.1f KB/s", snapshot.networkTxKbps),
                    fontFamily = TelemetryMonoFontFamily,
                    fontSize = 12.sp,
                    color = mutedColor
                )
            }
        }

        Column {
            Text(
                text = "Packet Stream Telemetry",
                fontFamily = CleanSansFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = contentColor
            )
            Text(
                text = String.format(
                    Locale.US,
                    "Session RX %.1f MB • TX %.1f MB",
                    snapshot.sessionRxMb,
                    snapshot.sessionTxMb
                ),
                fontFamily = CleanSansFontFamily,
                fontSize = 12.sp,
                color = mutedColor
            )
            Spacer(modifier = Modifier.height(10.dp))
            // Minimalist bottom progress bar matching the media card in reference image
            val progress = ((snapshot.networkRxKbps + snapshot.networkTxKbps) / 500f).coerceIn(0.18f, 0.92f)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(mutedColor.copy(alpha = 0.25f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .background(accentColor)
                )
            }
        }
    }
}

@Composable
fun InspirationBoardTileContent(
    contentColor: Color
) {
    // Displays the user's attached reference inspiration image inside a clean rounded frame + widget badge
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.img_kiosk_reference),
            contentDescription = "Kiosk visual design reference and widget launcher",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(20.dp))
        )
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.72f))
                .padding(horizontal = 8.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Widgets,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "HOME WIDGETS",
                fontFamily = TelemetryMonoFontFamily,
                fontSize = 10.sp,
                color = Color.White
            )
        }
    }
}

@Composable
private fun XpCrtMeterContent(
    title: String,
    primaryValue: String,
    subValue: String,
    history: List<Int>,
    traceColor: Color,
    showBarMeter: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CrtMonitorBlack)
            .border(1.5.dp, Color(0xFF606060))
            .padding(8.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontFamily = TelemetryMonoFontFamily,
                fontSize = 11.sp,
                color = CrtTraceNeonGreen
            )
            Text(
                text = primaryValue,
                fontFamily = DotMatrixFontFamily,
                fontSize = 22.sp,
                color = traceColor
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (showBarMeter) {
                // Classic XP Task Manager vertical LED bar gauge
                val latest = history.lastOrNull() ?: 35
                Canvas(
                    modifier = Modifier
                        .width(34.dp)
                        .fillMaxHeight()
                ) {
                    val segs = 10
                    val active = ((latest * segs) / 100).coerceIn(1, segs)
                    val segH = size.height / segs
                    for (i in 0 until segs) {
                        val inv = segs - 1 - i
                        drawRect(
                            color = if (inv < active) traceColor else CrtGridDarkGreen.copy(alpha = 0.5f),
                            topLeft = Offset(2f, i * segH + 1.5f),
                            size = Size(size.width - 4f, segH - 3f)
                        )
                    }
                }
            }

            // Classic XP CRT Grid & Waveform
            Canvas(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                val stepGrid = 14.dp.toPx()
                var gx = 0f
                while (gx <= size.width) {
                    drawLine(CrtGridDarkGreen, Offset(gx, 0f), Offset(gx, size.height), strokeWidth = 1f)
                    gx += stepGrid
                }
                var gy = 0f
                while (gy <= size.height) {
                    drawLine(CrtGridDarkGreen, Offset(0f, gy), Offset(size.width, gy), strokeWidth = 1f)
                    gy += stepGrid
                }

                if (history.size > 1) {
                    val path = Path()
                    val stepX = size.width / (history.size - 1).coerceAtLeast(1)
                    history.forEachIndexed { index, v ->
                        val px = index * stepX
                        val py = size.height - (v.coerceIn(0, 100) / 100f) * size.height
                        if (index == 0) path.moveTo(px, py) else path.lineTo(px, py)
                    }
                    drawPath(path = path, color = traceColor, style = Stroke(width = 2.2.dp.toPx()))
                }
            }
        }

        Text(
            text = subValue,
            fontFamily = TelemetryMonoFontFamily,
            fontSize = 10.sp,
            color = CrtTraceNeonGreen
        )
    }
}

@Composable
fun DotMatrixSparkline(
    values: List<Int>,
    activeColor: Color,
    peakColor: Color,
    inactiveColor: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val cols = values.size.coerceAtLeast(1)
        val rows = 5
        val colW = size.width / cols
        val rowH = size.height / rows
        val radius = (minOf(colW, rowH) * 0.34f).coerceAtLeast(2f)

        for (c in 0 until cols) {
            val v = values[c].coerceIn(0, 100)
            val activeRows = ((v * rows) / 100).coerceIn(1, rows)
            for (r in 0 until rows) {
                val invertedRow = rows - 1 - r
                val isLit = invertedRow < activeRows
                val dotColor = when {
                    isLit && invertedRow >= rows - 1 -> peakColor
                    isLit -> activeColor
                    else -> inactiveColor
                }
                drawCircle(
                    color = dotColor,
                    radius = radius,
                    center = Offset(c * colW + colW / 2f, r * rowH + rowH / 2f)
                )
            }
        }
    }
}

private val DOT_PATTERN_THERMAL_CLOUD = arrayOf(
    "00111000",
    "01111100",
    "11111111",
    "11111111",
    "01111110"
)

private val DOT_PATTERN_STREAM_NOTE = arrayOf(
    "00111110",
    "00100010",
    "00111110",
    "01100110",
    "01100110"
)

@Composable
private fun DotMatrixIconCanvas(
    pattern: Array<String>,
    dotColor: Color,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val rows = pattern.size
        val cols = pattern.firstOrNull()?.length ?: 1
        val cellW = size.width / cols
        val cellH = size.height / rows
        val r = minOf(cellW, cellH) * 0.38f

        for (y in 0 until rows) {
            val rowStr = pattern[y]
            for (x in 0 until rowStr.length) {
                if (rowStr[x] == '1') {
                    drawCircle(
                        color = if (x == cols - 1 && y == 0) accentColor else dotColor,
                        radius = r,
                        center = Offset(x * cellW + cellW / 2f, y * cellH + cellH / 2f)
                    )
                }
            }
        }
    }
}
