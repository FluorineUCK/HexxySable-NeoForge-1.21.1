package io.github.techtastic.hexxysable.sablecompat

import org.joml.Quaterniond
import org.joml.Vector3d
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class EntityCoordinateTransferTest {
    @Test
    fun `embark converts world position into target plot coordinates`() {
        val local = EntityCoordinateTransfer.embark(
            position = Vector3d(14.0, 8.0, 5.0),
            currentPose = null,
            targetPose = pose(position = Vector3d(10.0, 2.0, 1.0), scale = Vector3d(2.0, 2.0, 2.0)),
        )

        assertVectorEquals(Vector3d(2.0, 3.0, 2.0), local)
    }

    @Test
    fun `embark transfers between two plot coordinate systems through world space`() {
        val local = EntityCoordinateTransfer.embark(
            position = Vector3d(2.0, 0.0, 0.0),
            currentPose = pose(position = Vector3d(10.0, 0.0, 0.0)),
            targetPose = pose(position = Vector3d(4.0, 0.0, 0.0), scale = Vector3d(2.0, 1.0, 1.0)),
        )

        assertVectorEquals(Vector3d(4.0, 0.0, 0.0), local)
    }

    @Test
    fun `disembark converts plot position into world coordinates`() {
        val world = EntityCoordinateTransfer.disembark(
            Vector3d(2.0, 3.0, 4.0),
            pose(position = Vector3d(10.0, 20.0, 30.0), scale = Vector3d(2.0, 2.0, 2.0)),
        )

        assertVectorEquals(Vector3d(14.0, 26.0, 38.0), world)
    }

    private fun pose(
        position: Vector3d,
        orientation: Quaterniond = Quaterniond(),
        rotationPoint: Vector3d = Vector3d(),
        scale: Vector3d = Vector3d(1.0, 1.0, 1.0),
    ) = EntityCoordinateTransfer.Pose(position, orientation, rotationPoint, scale)

    private fun assertVectorEquals(expected: Vector3d, actual: Vector3d) {
        assertEquals(expected.x, actual.x, 1.0e-9)
        assertEquals(expected.y, actual.y, 1.0e-9)
        assertEquals(expected.z, actual.z, 1.0e-9)
    }
}
