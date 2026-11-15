package org.brianwebb.hamsprint.ui

import org.junit.Assert.assertEquals
import org.junit.Test

class FormatTest {
    @Test
    fun `pass chance never rounds a real possibility to 0 or 100 percent`() {
        assertEquals("0%", formatChance(0.0))
        assertEquals("<1%", formatChance(0.0001))
        assertEquals("1%", formatChance(0.01))
        assertEquals("50%", formatChance(0.5))
        assertEquals(">99%", formatChance(0.9996))
        assertEquals("100%", formatChance(1.0))
    }

    @Test
    fun `expected score shows one decimal so small gains are visible`() {
        assertEquals("8.8", formatScore(8.75))
        assertEquals("26.0", formatScore(26.0))
    }
}
