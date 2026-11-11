package com.example

import com.example.data.KioskPreferencesRepository
import com.example.model.KioskSkin
import com.example.model.PollingMode
import com.example.model.TileSpan
import com.example.model.TileSurfaceVariant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun verifySkinAndTileModels() {
        assertEquals(KioskSkin.NOTHING_DOT_MATRIX, KioskSkin.fromId("nothing_dot_matrix"))
        assertEquals(KioskSkin.WIN_XP_LUNA, KioskSkin.fromId("win_xp_luna"))
        assertEquals(KioskSkin.WIN_XP_CLASSIC, KioskSkin.fromId("win_xp_classic"))

        assertEquals(PollingMode.REAL_TIME_1S, PollingMode.fromId("realtime"))
        assertEquals(TileSpan.PILL_2X1, TileSpan.CIRCLE_1X1.next())
        assertEquals(TileSurfaceVariant.STARK_WHITE, TileSurfaceVariant.DARK_CHARCOAL.next())

        val defaults = KioskPreferencesRepository.defaultTiles()
        assertTrue(defaults.size >= 10)
        assertTrue(defaults.all { it.isVisible })
    }
}
