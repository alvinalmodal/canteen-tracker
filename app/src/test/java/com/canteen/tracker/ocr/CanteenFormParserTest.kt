package com.canteen.tracker.ocr

import android.graphics.Rect
import com.google.mlkit.vision.text.Text
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [33])
class CanteenFormParserTest {

    private lateinit var parser: CanteenFormParser

    @Before
    fun setup() {
        parser = CanteenFormParser()
    }

    @Test
    fun `parse single employee form extracts name and entries`() {
        // Simulate Image 3: "Iman" with entries
        val text = buildMockText(
            lines = listOf(
                MockLine("CANTEEN DEDUCTION", Rect(200, 10, 600, 40)),
                MockLine("Name: Iman", Rect(30, 50, 200, 80)),
                MockLine("Cut off Period: March 1-15, 2025", Rect(300, 50, 700, 80)),
                MockLine("Date Description Amount", Rect(30, 90, 700, 110)),
                MockLine("2 tinapay, canton, egg 45", Rect(30, 130, 700, 155)),
                MockLine("3 powder, bar, candy 40", Rect(30, 165, 700, 190)),
                MockLine("5 egg, tinapay, assorted etc 100", Rect(30, 200, 700, 225)),
                MockLine("6 lemon, 2 egg 85", Rect(30, 235, 700, 260)),
                MockLine("7 hotdog, Egg, chunky 82", Rect(30, 270, 700, 295)),
                MockLine("sardines, chicken 105", Rect(30, 305, 700, 330)),
                MockLine("8 p.canton, noodles, egg 92", Rect(30, 340, 700, 365)),
                MockLine("9 amikal, bar, drinks 49", Rect(30, 375, 700, 400)),
                MockLine("10 tinapay, meatloaf 60", Rect(30, 410, 700, 435)),
                MockLine("11 Egg, sbag, chunga 46", Rect(30, 445, 700, 470)),
                MockLine("740", Rect(600, 490, 700, 515))
            )
        )

        val result = parser.parse(text)

        assertNotNull(result.cutoffPeriodText)
        assertTrue(result.cutoffPeriodText!!.contains("March"))
        assertNotNull(result.cutoffStartDate)
        assertEquals(3, result.cutoffStartDate!!.monthValue)
        assertEquals(1, result.cutoffStartDate!!.dayOfMonth)
        assertEquals(15, result.cutoffEndDate!!.dayOfMonth)
        assertEquals(2025, result.cutoffStartDate!!.year)

        assertEquals(1, result.employees.size)
        val iman = result.employees[0]
        assertEquals("Iman", iman.name)
        assertTrue(iman.entries.size >= 8)

        // Check first entry
        val firstEntry = iman.entries[0]
        assertEquals(2, firstEntry.date)
        assertEquals(45.0, firstEntry.amount!!, 0.01)
    }

    @Test
    fun `parse multi-employee form splits sections correctly`() {
        // Simulate Image 1: Elena, Edilyn, Helen on one form
        val text = buildMockText(
            lines = listOf(
                MockLine("CANTEEN DEDUCTION", Rect(200, 10, 600, 40)),
                MockLine("Name: Elena", Rect(30, 50, 200, 80)),
                MockLine("Cut off Period:", Rect(300, 50, 500, 80)),
                MockLine("Date Description Amount", Rect(30, 90, 700, 110)),
                MockLine("18 Yakult 13", Rect(30, 130, 700, 155)),
                MockLine("20 Bihon Guisado 25", Rect(30, 165, 700, 190)),
                // Section break: Edilyn
                MockLine("Edilyn", Rect(200, 230, 400, 260)),
                MockLine("24 Ginataang langka 50", Rect(30, 280, 700, 305)),
                MockLine("25 Igado 60", Rect(30, 315, 700, 340)),
                MockLine("26 1/2 Ampalaya 1/2 Sinigang Tulingan 75", Rect(30, 350, 700, 375)),
                // Section break: Helen
                MockLine("Helen", Rect(200, 420, 400, 450)),
                MockLine("24 1rice, Adobong Manok, coke 100", Rect(30, 470, 700, 495)),
                MockLine("25 1/2 Ginataang Puso, Camote Q, cake 75", Rect(30, 505, 700, 530))
            )
        )

        val result = parser.parse(text)

        assertEquals(3, result.employees.size)

        val elena = result.employees[0]
        assertEquals("Elena", elena.name)
        assertEquals(2, elena.entries.size)

        val edilyn = result.employees[1]
        assertEquals("Edilyn", edilyn.name)
        assertEquals(3, edilyn.entries.size)

        val helen = result.employees[2]
        assertEquals("Helen", helen.name)
        assertEquals(2, helen.entries.size)
    }

    @Test
    fun `parse empty text returns empty form`() {
        val text = buildMockText(lines = emptyList())
        val result = parser.parse(text)
        assertTrue(result.employees.isEmpty())
    }

    @Test
    fun `entry parsing extracts date description and amount`() {
        val text = buildMockText(
            lines = listOf(
                MockLine("CANTEEN DEDUCTION", Rect(200, 10, 600, 40)),
                MockLine("Name: Joy", Rect(30, 50, 200, 80)),
                MockLine("Date Description Amount", Rect(30, 90, 700, 110)),
                MockLine("2 Spaghetti, Arriba 65", Rect(30, 130, 700, 155)),
                MockLine("3 binagoongang baboy 60", Rect(30, 165, 700, 190)),
                MockLine("935", Rect(600, 220, 700, 250))
            )
        )

        val result = parser.parse(text)
        assertEquals(1, result.employees.size)

        val joy = result.employees[0]
        assertEquals("Joy", joy.name)
        assertEquals(2, joy.entries.size)

        assertEquals(2, joy.entries[0].date)
        assertTrue(joy.entries[0].description.contains("Spaghetti"))
        assertEquals(65.0, joy.entries[0].amount!!, 0.01)

        assertEquals(3, joy.entries[1].date)
        assertEquals(60.0, joy.entries[1].amount!!, 0.01)
    }

    // --- Helpers ---

    private data class MockLine(val text: String, val bounds: Rect)

    private fun buildMockText(lines: List<MockLine>): Text {
        val mockText = mock(Text::class.java)

        val textBlocks = if (lines.isEmpty()) {
            emptyList()
        } else {
            listOf(buildMockBlock(lines))
        }

        `when`(mockText.textBlocks).thenReturn(textBlocks)
        return mockText
    }

    private fun buildMockBlock(lines: List<MockLine>): Text.TextBlock {
        val block = mock(Text.TextBlock::class.java)
        val mockLines = lines.map { mockLine ->
            val line = mock(Text.Line::class.java)
            `when`(line.text).thenReturn(mockLine.text)
            `when`(line.boundingBox).thenReturn(mockLine.bounds)
            line
        }
        `when`(block.lines).thenReturn(mockLines)
        return block
    }
}
