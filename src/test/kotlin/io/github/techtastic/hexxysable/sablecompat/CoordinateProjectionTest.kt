package io.github.techtastic.hexxysable.sablecompat

import org.joml.Quaterniond
import org.joml.Vector3d
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CoordinateProjectionTest {
    @Test
    fun `plot coordinates are projected before vanilla world-bound checks`() {
        val projected = CoordinateProjection.projectIfManaged(
            Vector3d(12_000_000.0, 64.0, -8_000_000.0),
            true,
            Vector3d(42.0, 80.0, -17.0),
            Quaterniond(),
            Vector3d(12_000_000.0, 64.0, -8_000_000.0),
            Vector3d(1.0, 1.0, 1.0),
        )

        assertVectorEquals(Vector3d(42.0, 80.0, -17.0), projected)
    }

    @Test
    fun `ordinary world coordinates remain unchanged`() {
        val input = Vector3d(12.0, 70.0, 9.0)
        val projected = CoordinateProjection.projectIfManaged(
            input,
            false,
            Vector3d(100.0, 100.0, 100.0),
            Quaterniond().rotateY(1.2),
            Vector3d(),
            Vector3d(2.0, 2.0, 2.0),
        )

        assertVectorEquals(input, projected)
    }

    private fun assertVectorEquals(expected: Vector3d, actual: Vector3d) {
        assertEquals(expected.x, actual.x, 1.0e-9)
        assertEquals(expected.y, actual.y, 1.0e-9)
        assertEquals(expected.z, actual.z, 1.0e-9)
    }
}
