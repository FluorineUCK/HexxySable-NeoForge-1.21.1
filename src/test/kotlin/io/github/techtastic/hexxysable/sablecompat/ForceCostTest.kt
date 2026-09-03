package io.github.techtastic.hexxysable.sablecompat

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class ForceCostTest {
    @Test
    fun `preserves upstream calibrated cost`() {
        assertEquals(10.1, ForceCost.dustUnits(600_000.0, 1_000.0), 1.0e-9)
    }

    @Test
    fun `rejects invalid mass`() {
        assertThrows(IllegalArgumentException::class.java) {
            ForceCost.dustUnits(1.0, 0.0)
        }
    }
}
