package io.github.techtastic.hexxysable.sablecompat

import org.joml.Quaterniond
import org.joml.Vector3d
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AmbitCoordinateRemapTest {
    @Test
    fun `world target is mapped into caster plot space`() {
        val mapped = AmbitCoordinateRemap.targetInCasterSpace(
            Vector3d(14.0, 8.0, 5.0),
            pose(position = Vector3d(10.0, 2.0, 1.0), scale = Vector3d(2.0, 2.0, 2.0)),
            null,
        )
        assertVectorEquals(Vector3d(2.0, 3.0, 2.0), mapped)
    }

    @Test
    fun `plot target is mapped into world space for a world caster`() {
        val mapped = AmbitCoordinateRemap.targetInCasterSpace(
            Vector3d(2.0, 3.0, 4.0),
            null,
            pose(position = Vector3d(10.0, 20.0, 30.0), scale = Vector3d(2.0, 2.0, 2.0)),
        )
        assertVectorEquals(Vector3d(14.0, 26.0, 38.0), mapped)
    }

    @Test
    fun `target transfers between different plot coordinate systems`() {
        val mapped = AmbitCoordinateRemap.targetInCasterSpace(
            Vector3d(2.0, 0.0, 0.0),
            pose(position = Vector3d(4.0, 0.0, 0.0), scale = Vector3d(2.0, 1.0, 1.0)),
            pose(position = Vector3d(10.0, 0.0, 0.0)),
        )
        assertVectorEquals(Vector3d(4.0, 0.0, 0.0), mapped)
    }

    @Test
    fun `ordinary world target remains unchanged`() {
        val input = Vector3d(1.0, 2.0, 3.0)
        assertVectorEquals(input, AmbitCoordinateRemap.targetInCasterSpace(input, null, null))
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
