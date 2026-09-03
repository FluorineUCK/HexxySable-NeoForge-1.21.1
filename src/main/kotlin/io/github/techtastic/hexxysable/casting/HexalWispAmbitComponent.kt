package io.github.techtastic.hexxysable.casting

import at.petrak.hexcasting.api.HexAPI
import at.petrak.hexcasting.api.casting.eval.CastingEnvironmentComponent
import io.github.techtastic.hexxysable.sablecompat.ProjectedRange
import io.github.techtastic.hexxysable.sablecompat.SableAmbitComponent
import io.github.techtastic.hexxysable.sablecompat.AmbitCoordinateRemap
import io.github.techtastic.hexxysable.sablecompat.SableShipAccess
import io.github.techtastic.hexxysable.sablecompat.SableShipAccess.toTransferPose
import net.minecraft.world.phys.Vec3
import net.minecraft.server.level.ServerPlayer
import org.joml.Vector3d
import ram.talia.hexal.api.casting.eval.env.WispCastEnv

/** Restores WispShipAmbit's projected caster range without linking Hexal when it is absent. */
class HexalWispAmbitComponent(private val env: WispCastEnv) : CastingEnvironmentComponent.IsVecInRange {
    override fun getKey(): CastingEnvironmentComponent.Key<*> = KEY

    override fun onIsVecInRange(vec: Vec3, current: Boolean): Boolean {
        if (current) return true

        val wispPosition = env.wisp.position()
        val wispShip = SableShipAccess.containing(env.world, wispPosition)
        val targetShip = SableShipAccess.containing(env.world, vec)
        val targetForWisp = AmbitCoordinateRemap.targetInCasterSpace(
            vec.toJoml(),
            wispShip?.toTransferPose(),
            targetShip?.toTransferPose(),
        )
        if (ProjectedRange.within(wispPosition.toJoml(), targetForWisp, env.wisp.maxSqrCastingDistance())) {
            return true
        }

        val targetWorld = SableAmbitComponent.projectToWorld(env, vec)
        val caster = env.castingEntity as? ServerPlayer ?: return false
        val sentinel = HexAPI.instance().getSentinel(caster) ?: return false
        return sentinel.extendsRange() &&
            caster.level().dimension() == sentinel.dimension() &&
            ProjectedRange.within(sentinel.position().toJoml(), targetWorld.toJoml(), 256.0)
    }

    companion object {
        @JvmField
        val KEY = object : CastingEnvironmentComponent.Key<HexalWispAmbitComponent> {}
    }
}

private fun Vec3.toJoml() = Vector3d(x, y, z)
