package io.github.techtastic.hexxysable.sablecompat

import org.joml.Quaterniond
import org.joml.Vector3d
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ProjectedRangeTest {
    @Test
    fun `plot caster can reach a nearby world target`() {
        val casterWorld = CoordinateProjection.projectIfManaged(
            Vector3d(4.0, 2.0, 3.0),
            true,
            Vector3d(100.0, 64.0, 20.0),
            Quaterniond(),
            Vector3d(),
            Vector3d(1.0, 1.0, 1.0),
        )

        assertTrue(ProjectedRange.within(casterWorld, Vector3d(106.0, 66.0, 23.0), 49.0))
    }

    @Test
    fun `two plot positions are compared after projection to world space`() {
        val casterWorld = Vector3d(102.0, 64.0, 20.0)
        val targetWorld = Vector3d(108.0, 64.0, 20.0)

        assertTrue(ProjectedRange.within(casterWorld, targetWorld, 36.0))
        assertFalse(ProjectedRange.within(casterWorld, targetWorld, 35.99))
    }
}
