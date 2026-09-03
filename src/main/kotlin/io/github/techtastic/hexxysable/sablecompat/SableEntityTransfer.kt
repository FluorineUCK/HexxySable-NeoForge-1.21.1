package io.github.techtastic.hexxysable.sablecompat

import dev.ryanhcode.sable.sublevel.ServerSubLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.Vec3
import org.joml.Vector3d
import io.github.techtastic.hexxysable.sablecompat.SableShipAccess.toTransferPose

/** Moves retained entities between Sable's world and plot coordinate spaces. */
object SableEntityTransfer {
    fun embark(entity: Entity, target: ServerSubLevel) {
        val level = target.level
        val current = SableShipAccess.containing(level, entity.position())
        if (current?.uniqueId == target.uniqueId) return

        // Sable's push/pop local helpers are a temporary paired transform:
        // pop restores orientation through thread-local state captured by push.
        // A wisp may remain embarked indefinitely, so persist only the plot
        // position and let Sable's retain_in_sub_level handling own tracking.
        val targetPosition = EntityCoordinateTransfer.embark(
            entity.position().toJoml(),
            current?.let { it.toTransferPose() },
            target.toTransferPose(),
        )
        entity.moveTo(targetPosition.toMinecraft())
    }

    fun disembark(entity: Entity, target: ServerSubLevel) {
        SableShipAccess.containing(target.level, entity.position()) ?: return
        // Preserve upstream semantics: the supplied ship iota determines the projection.
        entity.moveTo(
            EntityCoordinateTransfer.disembark(
                entity.position().toJoml(),
                target.toTransferPose(),
            ).toMinecraft(),
        )
    }

    private fun Vec3.toJoml() = Vector3d(x, y, z)

    private fun Vector3d.toMinecraft() = Vec3(x, y, z)
}
