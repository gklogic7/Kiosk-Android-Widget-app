package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.KioskPreferencesRepository
import com.example.data.LowOverheadTelemetryEngine
import com.example.model.KioskSkin
import com.example.widget.renderWidgetBitmap
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `verify Kiosk app name and telemetry engine`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Kiosk", appName)

        val engine = LowOverheadTelemetryEngine.getInstance(context)
        val snapshot = engine.sampleSnapshot()
        assertTrue(snapshot.cpuLoadPercent in 1..100)
        assertTrue(snapshot.ramTotalMb > 0)
        assertTrue(snapshot.perCoreInfo.isNotEmpty())
        assertTrue(snapshot.processList.isNotEmpty())

        val prefs = KioskPreferencesRepository(context)
        prefs.setSkin(KioskSkin.WIN_XP_LUNA)
        assertEquals(KioskSkin.WIN_XP_LUNA, prefs.getSkin())

        val compactBmp = renderWidgetBitmap(KioskSkin.NOTHING_DOT_MATRIX, snapshot, isWideDeck = false)
        val xpBmp = renderWidgetBitmap(KioskSkin.WIN_XP_LUNA, snapshot, isWideDeck = true)
        assertNotNull(compactBmp)
        assertNotNull(xpBmp)
    }
}
