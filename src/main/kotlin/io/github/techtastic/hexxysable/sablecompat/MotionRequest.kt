package io.github.techtastic.hexxysable.sablecompat

import org.joml.Vector3d
import java.util.UUID

sealed interface MotionRequest {
    val subLevelId: UUID
    val referenceSpace: ReferenceSpace
    val motion: Vector3d

    data class Force(
        override val subLevelId: UUID,
        override val referenceSpace: ReferenceSpace,
        override val motion: Vector3d,
        val position: Vector3d,
    ) : MotionRequest

    data class Torque(
        override val subLevelId: UUID,
        override val referenceSpace: ReferenceSpace,
        override val motion: Vector3d,
    ) : MotionRequest
}
