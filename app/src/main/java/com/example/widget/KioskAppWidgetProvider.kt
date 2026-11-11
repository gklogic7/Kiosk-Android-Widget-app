package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.os.Build
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import com.example.data.KioskPreferencesRepository
import com.example.data.LowOverheadTelemetryEngine
import com.example.model.KioskSkin
import com.example.model.TelemetrySnapshot
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

open class BaseKioskWidgetProvider(private val isWideDeck: Boolean) : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val prefs = KioskPreferencesRepository(context)
        val skin = prefs.getSkin()
        val snapshot = LowOverheadTelemetryEngine.getInstance(context).sampleSnapshot()
        for (widgetId in appWidgetIds) {
            updateSingleWidget(context, appWidgetManager, widgetId, skin, snapshot, isWideDeck)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when (intent.action) {
            ACTION_REFRESH_WIDGET -> {
                KioskWidgetUpdater.updateAllWidgets(context)
            }
            ACTION_CYCLE_SKIN -> {
                val prefs = KioskPreferencesRepository(context)
                val nextSkin = when (prefs.getSkin()) {
                    KioskSkin.NOTHING_DOT_MATRIX -> KioskSkin.WIN_XP_LUNA
                    KioskSkin.WIN_XP_LUNA -> KioskSkin.WIN_XP_CLASSIC
                    KioskSkin.WIN_XP_CLASSIC -> KioskSkin.NOTHING_DOT_MATRIX
                }
                prefs.setSkin(nextSkin)
                KioskWidgetUpdater.updateAllWidgets(context)
            }
        }
    }

    companion object {
        const val ACTION_REFRESH_WIDGET = "com.example.kiosk.ACTION_REFRESH_WIDGET"
        const val ACTION_CYCLE_SKIN = "com.example.kiosk.ACTION_CYCLE_SKIN"
    }
}

class KioskCompactWidgetProvider : BaseKioskWidgetProvider(isWideDeck = false)

class KioskDeckWidgetProvider : BaseKioskWidgetProvider(isWideDeck = true)

object KioskWidgetUpdater {

    fun updateAllWidgets(context: Context, snapshotOverride: TelemetrySnapshot? = null) {
        val appContext = context.applicationContext
        val manager = AppWidgetManager.getInstance(appContext)
        val prefs = KioskPreferencesRepository(appContext)
        val skin = prefs.getSkin()
        val snapshot = snapshotOverride
            ?: LowOverheadTelemetryEngine.getInstance(appContext).sampleSnapshot()

        val compactIds = manager.getAppWidgetIds(
            ComponentName(appContext, KioskCompactWidgetProvider::class.java)
        )
        for (id in compactIds) {
            updateSingleWidget(appContext, manager, id, skin, snapshot, isWideDeck = false)
        }

        val deckIds = manager.getAppWidgetIds(
            ComponentName(appContext, KioskDeckWidgetProvider::class.java)
        )
        for (id in deckIds) {
            updateSingleWidget(appContext, manager, id, skin, snapshot, isWideDeck = true)
        }
    }

    fun getPinnedWidgetCount(context: Context): Int {
        val manager = AppWidgetManager.getInstance(context.applicationContext)
        val compactCount = manager.getAppWidgetIds(
            ComponentName(context, KioskCompactWidgetProvider::class.java)
        ).size
        val deckCount = manager.getAppWidgetIds(
            ComponentName(context, KioskDeckWidgetProvider::class.java)
        ).size
        return compactCount + deckCount
    }

    fun requestPinWidget(context: Context, isWideDeck: Boolean): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return false
        val manager = AppWidgetManager.getInstance(context)
        if (!manager.isRequestPinAppWidgetSupported) return false
        val provider = ComponentName(
            context,
            if (isWideDeck) KioskDeckWidgetProvider::class.java else KioskCompactWidgetProvider::class.java
        )
        return runCatching {
            manager.requestPinAppWidget(provider, null, null)
        }.getOrDefault(false)
    }
}

private fun updateSingleWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int,
    skin: KioskSkin,
    snapshot: TelemetrySnapshot,
    isWideDeck: Boolean
) {
    val views = RemoteViews(context.packageName, R.layout.widget_kiosk_canvas)
    val bitmap = renderWidgetBitmap(skin, snapshot, isWideDeck)
    views.setImageViewBitmap(R.id.widget_canvas_image, bitmap)

    val openIntent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
    val openPending = PendingIntent.getActivity(
        context,
        appWidgetId,
        openIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    views.setOnClickPendingIntent(R.id.widget_zone_open, openPending)

    val providerClass = if (isWideDeck) {
        KioskDeckWidgetProvider::class.java
    } else {
        KioskCompactWidgetProvider::class.java
    }

    val skinIntent = Intent(context, providerClass).apply {
        action = BaseKioskWidgetProvider.ACTION_CYCLE_SKIN
    }
    val skinPending = PendingIntent.getBroadcast(
        context,
        appWidgetId + 1000,
        skinIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    views.setOnClickPendingIntent(R.id.widget_zone_skin, skinPending)

    val refreshIntent = Intent(context, providerClass).apply {
        action = BaseKioskWidgetProvider.ACTION_REFRESH_WIDGET
    }
    val refreshPending = PendingIntent.getBroadcast(
        context,
        appWidgetId + 2000,
        refreshIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    views.setOnClickPendingIntent(R.id.widget_zone_refresh, refreshPending)

    appWidgetManager.updateAppWidget(appWidgetId, views)
}

fun renderWidgetBitmap(
    skin: KioskSkin,
    snapshot: TelemetrySnapshot,
    isWideDeck: Boolean
): Bitmap {
    val width = if (isWideDeck) 840 else 560
    val height = 340
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    when (skin) {
        KioskSkin.NOTHING_DOT_MATRIX -> drawNothingWidgetCanvas(canvas, width, height, snapshot, isWideDeck)
        KioskSkin.WIN_XP_LUNA -> drawXpWidgetCanvas(canvas, width, height, snapshot, isWideDeck, isLuna = true)
        KioskSkin.WIN_XP_CLASSIC -> drawXpWidgetCanvas(canvas, width, height, snapshot, isWideDeck, isLuna = false)
    }

    return bitmap
}

private fun drawNothingWidgetCanvas(
    canvas: Canvas,
    width: Int,
    height: Int,
    snapshot: TelemetrySnapshot,
    isWideDeck: Boolean
) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    // Outer pitch-black capsule container
    paint.color = Color.parseColor("#08080A")
    canvas.drawRoundRect(RectF(0f, 0f, width.toFloat(), height.toFloat()), 48f, 48f, paint)

    // Left circular CPU dial (like the analog dial in the reference image)
    val circleCenterX = 165f
    val circleCenterY = height / 2f
    val circleRadius = 142f
    paint.color = Color.parseColor("#18181C")
    canvas.drawCircle(circleCenterX, circleCenterY, circleRadius, paint)

    // Dial tick dots
    for (i in 0 until 12) {
        val angleRad = Math.toRadians((i * 30 - 90).toDouble())
        val tx = circleCenterX + (circleRadius - 20f) * cos(angleRad).toFloat()
        val ty = circleCenterY + (circleRadius - 20f) * sin(angleRad).toFloat()
        paint.color = if (i == 6) Color.parseColor("#D71921") else Color.parseColor("#3E3E44")
        canvas.drawCircle(tx, ty, if (i == 6) 8f else 4f, paint)
    }

    // CPU needle + value
    val cpuAngleRad = Math.toRadians((-135.0 + (snapshot.cpuLoadPercent * 2.7)))
    val nx = circleCenterX + (circleRadius - 42f) * cos(cpuAngleRad).toFloat()
    val ny = circleCenterY + (circleRadius - 42f) * sin(cpuAngleRad).toFloat()
    paint.color = Color.WHITE
    paint.strokeWidth = 14f
    paint.strokeCap = Paint.Cap.ROUND
    canvas.drawLine(circleCenterX, circleCenterY, nx, ny, paint)
    paint.color = Color.parseColor("#D71921")
    canvas.drawCircle(circleCenterX, circleCenterY, 12f, paint)

    paint.typeface = Typeface.MONOSPACE
    paint.textSize = 40f
    paint.textAlign = Paint.Align.CENTER
    paint.color = Color.WHITE
    canvas.drawText("CPU ${snapshot.cpuLoadPercent}%", circleCenterX, circleCenterY + 62f, paint)
    paint.textSize = 20f
    paint.color = Color.parseColor("#9A9AA2")
    canvas.drawText("${snapshot.averageFreqMhz} MHz", circleCenterX, circleCenterY + 90f, paint)

    // Center Stark-White RAM Squircle Card
    val ramLeft = 328f
    val ramWidth = if (isWideDeck) 280f else (width - ramLeft - 18f)
    val ramRect = RectF(ramLeft, 20f, ramLeft + ramWidth, height - 20f)
    paint.color = Color.parseColor("#F3F3F6")
    canvas.drawRoundRect(ramRect, 40f, 40f, paint)

    paint.textAlign = Paint.Align.LEFT
    paint.color = Color.parseColor("#121214")
    paint.textSize = 22f
    canvas.drawText("RAM MATRIX", ramLeft + 24f, 64f, paint)

    paint.textSize = 62f
    paint.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
    canvas.drawText("${snapshot.ramUsagePercent}%", ramLeft + 24f, 140f, paint)

    paint.textSize = 22f
    paint.typeface = Typeface.MONOSPACE
    paint.color = Color.parseColor("#52525B")
    val usedGb = snapshot.ramUsedMb / 1024f
    val totalGb = snapshot.ramTotalMb / 1024f
    canvas.drawText(
        String.format(Locale.US, "%.1f / %.1f GB", usedGb, totalGb),
        ramLeft + 24f,
        178f,
        paint
    )

    // Dot-matrix bar inside RAM card
    val dotsCols = 10
    val filledCols = ((snapshot.ramUsagePercent * dotsCols) / 100).coerceIn(1, dotsCols)
    val dotStartX = ramLeft + 28f
    val dotStartY = 216f
    val colSpacing = (ramWidth - 56f) / dotsCols
    for (c in 0 until dotsCols) {
        for (r in 0 until 3) {
            paint.color = when {
                c < filledCols && c >= dotsCols - 2 -> Color.parseColor("#D71921")
                c < filledCols -> Color.parseColor("#121214")
                else -> Color.parseColor("#D4D4D8")
            }
            canvas.drawCircle(dotStartX + c * colSpacing, dotStartY + r * 20f, 6.5f, paint)
        }
    }

    paint.textSize = 18f
    paint.color = Color.parseColor("#71717A")
    canvas.drawText("HEAP ${String.format(Locale.US, "%.1fMB", snapshot.appHeapAllocatedMb)}", ramLeft + 24f, height - 38f, paint)

    if (isWideDeck) {
        val rightLeft = ramLeft + ramWidth + 16f
        val rightRight = width - 18f

        // Top-right Crimson Red Battery/Thermal Pill
        val topPill = RectF(rightLeft, 20f, rightRight, 162f)
        paint.color = Color.parseColor("#D71921")
        canvas.drawRoundRect(topPill, 38f, 38f, paint)

        paint.color = Color.WHITE
        paint.textSize = 20f
        canvas.drawText("SKIN • TAP", rightLeft + 22f, 58f, paint)
        paint.textSize = 44f
        paint.typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL)
        canvas.drawText("${snapshot.batteryTempCelsius.toInt()}°C", rightLeft + 22f, 112f, paint)
        paint.typeface = Typeface.MONOSPACE
        paint.textSize = 20f
        canvas.drawText("BAT ${snapshot.batteryPercent}%", rightLeft + 22f, 142f, paint)

        // Bottom-right Charcoal Refresh / Net Pill
        val botPill = RectF(rightLeft, 178f, rightRight, height - 20f)
        paint.color = Color.parseColor("#18181C")
        canvas.drawRoundRect(botPill, 38f, 38f, paint)

        paint.color = Color.WHITE
        paint.textSize = 20f
        canvas.drawText("SYNC • TAP", rightLeft + 22f, 218f, paint)
        paint.textSize = 30f
        canvas.drawText(
            String.format(Locale.US, "↓%.0f KB/s", snapshot.networkRxKbps),
            rightLeft + 22f,
            262f,
            paint
        )
        paint.color = Color.parseColor("#9A9AA2")
        paint.textSize = 18f
        canvas.drawText("DSK ${snapshot.storageUsagePercent}% USED", rightLeft + 22f, 296f, paint)
    }
}

private fun drawXpWidgetCanvas(
    canvas: Canvas,
    width: Int,
    height: Int,
    snapshot: TelemetrySnapshot,
    isWideDeck: Boolean,
    isLuna: Boolean
) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val windowBg = if (isLuna) Color.parseColor("#ECE9D8") else Color.parseColor("#D4D0C8")
    val borderColor = if (isLuna) Color.parseColor("#0054E3") else Color.parseColor("#404040")

    // Window frame
    paint.color = windowBg
    canvas.drawRoundRect(
        RectF(0f, 0f, width.toFloat(), height.toFloat()),
        if (isLuna) 16f else 0f,
        if (isLuna) 16f else 0f,
        paint
    )
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = 6f
    paint.color = borderColor
    canvas.drawRoundRect(
        RectF(3f, 3f, width - 3f, height - 3f),
        if (isLuna) 16f else 0f,
        if (isLuna) 16f else 0f,
        paint
    )
    paint.style = Paint.Style.FILL

    // Title bar gradient
    val titleRect = RectF(6f, 6f, width - 6f, 56f)
    paint.shader = if (isLuna) {
        LinearGradient(0f, 6f, 0f, 56f, Color.parseColor("#2A7FFF"), Color.parseColor("#0046D5"), Shader.TileMode.CLAMP)
    } else {
        LinearGradient(6f, 0f, width - 6f, 0f, Color.parseColor("#0A246A"), Color.parseColor("#A6CAF0"), Shader.TileMode.CLAMP)
    }
    canvas.drawRect(titleRect, paint)
    paint.shader = null

    paint.color = Color.WHITE
    paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
    paint.textSize = 24f
    canvas.drawText("Kiosk Task Manager — CPU & Memory", 22f, 40f, paint)

    // Close / Skin button on title bar
    paint.color = if (isLuna) Color.parseColor("#E04343") else Color.parseColor("#D4D0C8")
    canvas.drawRect(RectF(width - 48f, 12f, width - 14f, 48f), paint)
    paint.color = if (isLuna) Color.WHITE else Color.BLACK
    paint.textSize = 22f
    canvas.drawText("×", width - 38f, 38f, paint)

    // Left CRT CPU Usage Box
    val crtLeft = RectF(20f, 74f, 240f, height - 24f)
    paint.color = Color.BLACK
    canvas.drawRect(crtLeft, paint)

    // Green CRT Grid lines
    paint.color = Color.parseColor("#004D00")
    paint.strokeWidth = 2f
    for (gx in 40..220 step 25) {
        canvas.drawLine(gx.toFloat(), 74f, gx.toFloat(), height - 24f, paint)
    }
    for (gy in 94..(height - 24) step 25) {
        canvas.drawLine(20f, gy.toFloat(), 240f, gy.toFloat(), paint)
    }

    paint.color = Color.parseColor("#00FF00")
    paint.typeface = Typeface.MONOSPACE
    paint.textSize = 22f
    canvas.drawText("CPU USAGE", 36f, 108f, paint)

    // LED Bar gauge
    val barTop = 124f
    val barBottom = height - 74f
    val segments = 10
    val activeSegments = ((snapshot.cpuLoadPercent * segments) / 100).coerceIn(1, segments)
    val segHeight = (barBottom - barTop) / segments
    for (s in 0 until segments) {
        val invertIdx = segments - 1 - s
        paint.color = if (invertIdx < activeSegments) Color.parseColor("#00FF00") else Color.parseColor("#003300")
        canvas.drawRect(RectF(56f, barTop + s * segHeight + 2f, 204f, barTop + (s + 1) * segHeight - 2f), paint)
    }

    paint.color = Color.parseColor("#00FF00")
    paint.textSize = 36f
    canvas.drawText("${snapshot.cpuLoadPercent}%", 96f, height - 34f, paint)

    // Right CRT Memory & History Box
    val crtRight = RectF(258f, 74f, width - 20f, height - 24f)
    paint.color = Color.BLACK
    canvas.drawRect(crtRight, paint)

    paint.color = Color.parseColor("#004D00")
    var gx = 280f
    while (gx < width - 20f) {
        canvas.drawLine(gx, 74f, gx, height - 24f, paint)
        gx += 28f
    }
    for (gy in 94..(height - 24) step 25) {
        canvas.drawLine(258f, gy.toFloat(), width - 20f, gy.toFloat(), paint)
    }

    // Draw live CPU history trace in bright green and RAM in yellow
    val hist = snapshot.cpuHistory
    if (hist.size > 1) {
        paint.color = Color.parseColor("#00FF00")
        paint.strokeWidth = 4f
        val plotTop = 120f
        val plotBottom = height - 78f
        val plotW = (width - 40f) - 274f
        val stepX = plotW / (hist.size - 1).coerceAtLeast(1)
        for (i in 0 until hist.size - 1) {
            val x1 = 274f + i * stepX
            val y1 = plotBottom - (hist[i] / 100f) * (plotBottom - plotTop)
            val x2 = 274f + (i + 1) * stepX
            val y2 = plotBottom - (hist[i + 1] / 100f) * (plotBottom - plotTop)
            canvas.drawLine(x1, y1, x2, y2, paint)
        }
    }

    paint.color = Color.parseColor("#FFFF00")
    paint.textSize = 24f
    canvas.drawText(
        "PF Usage: ${snapshot.ramUsedMb} MB / ${snapshot.ramTotalMb} MB (${snapshot.ramUsagePercent}%)",
        274f,
        106f,
        paint
    )

    paint.color = Color.parseColor("#00FF00")
    paint.textSize = 20f
    val footerText = if (isWideDeck) {
        "Cores: ${snapshot.cpuCoresCount} (${snapshot.averageFreqMhz}MHz) | Bat: ${snapshot.batteryPercent}% | Heap: ${String.format(Locale.US, "%.1fMB", snapshot.appHeapAllocatedMb)}"
    } else {
        "Heap: ${String.format(Locale.US, "%.1fMB", snapshot.appHeapAllocatedMb)} | Bat: ${snapshot.batteryPercent}%"
    }
    canvas.drawText(footerText, 274f, height - 38f, paint)
}
