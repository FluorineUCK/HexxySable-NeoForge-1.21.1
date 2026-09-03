package io.github.techtastic.hexxysable.sablecompat

import org.joml.Quaterniondc
import org.joml.Vector3d
import org.joml.Vector3dc

/** Pure coordinate-space conversion shared by Hexal entity transfer and JVM tests. */
object EntityCoordinateTransfer {
    data class Pose(
        val position: Vector3dc,
        val orientation: Quaterniondc,
        val rotationPoint: Vector3dc,
        val scale: Vector3dc,
    )

    fun embark(position: Vector3dc, currentPose: Pose?, targetPose: Pose): Vector3d {
        val worldPosition = currentPose?.let { modelToWorld(position, it) } ?: Vector3d(position)
        return ReferenceSpaceTransform.worldPointToModel(
            worldPosition,
            targetPose.position,
            targetPose.orientation,
            targetPose.rotationPoint,
            targetPose.scale,
        )
    }

    fun disembark(position: Vector3dc, pose: Pose): Vector3d = modelToWorld(position, pose)

    private fun modelToWorld(position: Vector3dc, pose: Pose): Vector3d = CoordinateProjection.projectIfManaged(
        position,
        true,
        pose.position,
        pose.orientation,
        pose.rotationPoint,
        pose.scale,
    )
}
