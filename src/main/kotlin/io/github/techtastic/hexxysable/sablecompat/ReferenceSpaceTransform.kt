package io.github.techtastic.hexxysable.sablecompat

import org.joml.Quaterniondc
import org.joml.Vector3d
import org.joml.Vector3dc

object ReferenceSpaceTransform {
    fun worldVectorToLocal(
        worldVector: Vector3dc,
        orientation: Quaterniondc,
        scale: Vector3dc,
    ): Vector3d {
        requireNonZeroScale(scale)
        return orientation.transformInverse(worldVector, Vector3d()).div(scale.x(), scale.y(), scale.z())
    }

    fun worldPointToModel(
        worldPoint: Vector3dc,
        position: Vector3dc,
        orientation: Quaterniondc,
        rotationPoint: Vector3dc,
        scale: Vector3dc,
    ): Vector3d {
        requireNonZeroScale(scale)
        return orientation
            .transformInverse(Vector3d(worldPoint).sub(position), Vector3d())
            .div(scale.x(), scale.y(), scale.z())
            .add(rotationPoint.x(), rotationPoint.y(), rotationPoint.z())
    }

    fun bodyPointToModel(bodyPoint: Vector3dc, centerOfMass: Vector3dc): Vector3d =
        Vector3d(centerOfMass).add(bodyPoint)

    private fun requireNonZeroScale(scale: Vector3dc) {
        require(scale.x().isFinite() && scale.y().isFinite() && scale.z().isFinite()) {
            "Pose scale must be finite"
        }
        require(scale.x() != 0.0 && scale.y() != 0.0 && scale.z() != 0.0) {
            "Pose scale must be non-zero"
        }
    }
}
