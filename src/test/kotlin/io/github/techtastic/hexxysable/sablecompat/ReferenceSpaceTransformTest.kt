package io.github.techtastic.hexxysable.sablecompat

import org.joml.Quaterniond
import org.joml.Vector3d
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ReferenceSpaceTransformTest {
    @Test
    fun `world impulse is converted into Sable local coordinates`() {
        val orientation = Quaterniond().rotateY(Math.PI / 2.0)
        val local = ReferenceSpaceTransform.worldVectorToLocal(
            Vector3d(1.0, 0.0, 0.0),
            orientation,
            Vector3d(1.0, 1.0, 1.0),
        )

        assertVectorEquals(Vector3d(0.0, 0.0, 1.0), local)
    }

    @Test
    fun `world point is converted into model coordinates around the rotation point`() {
        val local = ReferenceSpaceTransform.worldPointToModel(
            Vector3d(12.0, 8.0, 7.0),
            Vector3d(10.0, 5.0, 2.0),
            Quaterniond(),
            Vector3d(100.0, 50.0, -20.0),
            Vector3d(2.0, 2.0, 2.0),
        )

        assertVectorEquals(Vector3d(101.0, 51.5, -17.5), local)
    }

    @Test
    fun `body point is offset from the local center of mass`() {
        val model = ReferenceSpaceTransform.bodyPointToModel(
            Vector3d(2.0, -1.0, 3.0),
            Vector3d(100.0, 50.0, -20.0),
        )

        assertVectorEquals(Vector3d(102.0, 49.0, -17.0), model)
    }

    private fun assertVectorEquals(expected: Vector3d, actual: Vector3d) {
        assertEquals(expected.x, actual.x, 1.0e-9)
        assertEquals(expected.y, actual.y, 1.0e-9)
        assertEquals(expected.z, actual.z, 1.0e-9)
    }
}
