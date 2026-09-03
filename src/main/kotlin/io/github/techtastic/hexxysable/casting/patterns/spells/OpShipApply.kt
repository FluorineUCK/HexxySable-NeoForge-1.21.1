package io.github.techtastic.hexxysable.casting.patterns.spells

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getVec3
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.misc.MediaConstants
import io.github.techtastic.hexxysable.sablecompat.ForceCost
import io.github.techtastic.hexxysable.sablecompat.MotionRequest
import io.github.techtastic.hexxysable.sablecompat.ReferenceSpace
import io.github.techtastic.hexxysable.sablecompat.SableMotionBridge
import io.github.techtastic.hexxysable.sablecompat.SableShipAccess
import io.github.techtastic.hexxysable.util.assertShipInRange
import io.github.techtastic.hexxysable.util.getShip
import net.minecraft.world.phys.Vec3
import org.joml.Vector3d
import java.util.UUID

class OpShipApply(private val type: Type, private val reference: ReferenceSpace) : SpellAction {
    override val argc = if (type == Type.FORCE) 3 else 2

    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val ship = args.getShip(env.world, 0, argc)
        val motion = args.getVec3(1, argc)
        val position = if (type == Type.FORCE) args.getVec3(2, argc) else Vec3.ZERO
        env.assertShipInRange(ship)
        val cost = (ForceCost.dustUnits(motion.length(), ship.massTracker.mass) * MediaConstants.DUST_UNIT).toLong()
        return SpellAction.Result(
            Spell(type, reference, ship.uniqueId, motion.toJoml(), position.toJoml()),
            cost,
            listOf(ParticleSpray(ship.logicalPose().position().toMinecraft(), motion.normalize(), 0.0, 0.1)),
        )
    }

    private data class Spell(
        val type: Type,
        val reference: ReferenceSpace,
        val shipId: UUID,
        val motion: Vector3d,
        val position: Vector3d,
    ) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            if (SableShipAccess.find(env.world, shipId) == null) return
            when (type) {
                Type.FORCE -> SableMotionBridge.enqueue(env.world.dimension(), MotionRequest.Force(shipId, reference, motion, position))
                Type.TORQUE -> SableMotionBridge.enqueue(env.world.dimension(), MotionRequest.Torque(shipId, reference, motion))
            }
        }
    }

    enum class Type { FORCE, TORQUE }
}

private fun Vec3.toJoml() = Vector3d(x, y, z)
private fun org.joml.Vector3dc.toMinecraft() = Vec3(x(), y(), z())
