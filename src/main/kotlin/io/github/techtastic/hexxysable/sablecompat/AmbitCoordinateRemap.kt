package io.github.techtastic.hexxysable.sablecompat

import org.joml.Vector3d
import org.joml.Vector3dc

/** Matches VS ShipAmbit: compare a target in the caster's coordinate space. */
object AmbitCoordinateRemap {
    fun targetInCasterSpace(
        targetPosition: Vector3dc,
        casterPose: EntityCoordinateTransfer.Pose?,
        targetPose: EntityCoordinateTransfer.Pose?,
    ): Vector3d {
        val targetWorld = targetPose?.let {
            EntityCoordinateTransfer.disembark(targetPosition, it)
        } ?: Vector3d(targetPosition)

        return casterPose?.let {
            EntityCoordinateTransfer.embark(targetWorld, null, it)
        } ?: targetWorld
    }
}
