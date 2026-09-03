package io.github.techtastic.hexxysable.sablecompat

import org.joml.Quaterniondc
import org.joml.Vector3d
import org.joml.Vector3dc

object CoordinateProjection {
    fun projectIfManaged(
        position: Vector3dc,
        managedBySubLevel: Boolean,
        worldPosition: Vector3dc,
        orientation: Quaterniondc,
        rotationPoint: Vector3dc,
        scale: Vector3dc,
    ): Vector3d = if (managedBySubLevel) {
        Vector3d(position)
            .sub(rotationPoint)
            .mul(scale)
            .let { orientation.transform(it) }
            .add(worldPosition)
    } else {
        Vector3d(position)
    }
}
