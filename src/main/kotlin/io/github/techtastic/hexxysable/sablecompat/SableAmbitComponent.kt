package io.github.techtastic.hexxysable.sablecompat

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.CastingEnvironmentComponent
import at.petrak.hexcasting.api.casting.eval.env.CircleCastEnv
import dev.ryanhcode.sable.Sable
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec3
import org.joml.Vector3d
import io.github.techtastic.hexxysable.sablecompat.SableShipAccess.toTransferPose

/**
 * Hex checks vanilla world bounds before it asks the environment extensions.
 * Sable plot positions intentionally live outside those bounds, so callers must
 * project positions before assertion. This component then reuses the original
 * environment's range and permission policy at the corresponding world point.
 */
class SableAmbitComponent(
    private val env: CastingEnvironment,
    private val remapRange: Boolean = true,
) :
    CastingEnvironmentComponent.IsVecInRange,
    CastingEnvironmentComponent.HasEditPermissionsAt {

    private var checkingProjectedPermission = false
    private var checkingRemappedRange = false

    override fun getKey(): CastingEnvironmentComponent.Key<*> = KEY

    override fun onIsVecInRange(vec: Vec3, current: Boolean): Boolean {
        if (current) return true
        if (!remapRange) return false
        if (checkingRemappedRange) return false

        val casterPosition = casterPosition() ?: return false
        val casterShip = SableShipAccess.containing(env.world, casterPosition)
        val targetShip = SableShipAccess.containing(env.world, vec)
        if (casterShip?.uniqueId == targetShip?.uniqueId) return false

        val remappedTarget = AmbitCoordinateRemap.targetInCasterSpace(
            vec.toJoml(),
            casterShip?.toTransferPose(),
            targetShip?.toTransferPose(),
        ).toMinecraft()
        checkingRemappedRange = true
        return try {
            env.isVecInRange(remappedTarget)
        } finally {
            checkingRemappedRange = false
        }
    }

    override fun onHasEditPermissionsAt(pos: BlockPos, current: Boolean): Boolean {
        if (checkingProjectedPermission) return current
        val ship = SableShipAccess.containing(env.world, pos) ?: return current
        val projected = ship.logicalPose().transformPosition(pos.center)
        val projectedPos = BlockPos.containing(projected)
        checkingProjectedPermission = true
        return try {
            AmbitPermissionPolicy.resolve(
                current = current,
                managedPosition = true,
                projectedPermission = env.hasEditPermissionsAt(projectedPos),
            )
        } finally {
            checkingProjectedPermission = false
        }
    }

    companion object {
        @JvmField
        val KEY = object : CastingEnvironmentComponent.Key<SableAmbitComponent> {}

        fun projectToWorld(env: CastingEnvironment, pos: Vec3): Vec3 =
            Sable.HELPER.projectOutOfSubLevel(env.world, pos)
    }

    private fun casterPosition(): Vec3? = when (env) {
        is CircleCastEnv -> env.impetus?.blockPos?.center
        else -> env.castingEntity?.position()
    }
}

private fun Vec3.toJoml() = Vector3d(x, y, z)

private fun Vector3d.toMinecraft() = Vec3(x, y, z)
