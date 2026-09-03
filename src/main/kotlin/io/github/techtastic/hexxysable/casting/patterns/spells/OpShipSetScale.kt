package io.github.techtastic.hexxysable.casting.patterns.spells

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getDoubleBetween
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.misc.MediaConstants
import io.github.techtastic.hexxysable.sablecompat.SableScaleBridge
import io.github.techtastic.hexxysable.sablecompat.SableShipAccess
import io.github.techtastic.hexxysable.util.assertShipInRange
import io.github.techtastic.hexxysable.util.getShip
import net.minecraft.world.phys.Vec3
import java.util.UUID
import kotlin.math.abs

object OpShipSetScale : SpellAction {
    override val argc = 2

    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val ship = args.getShip(env.world, 0, argc)
        val scale = args.getDoubleBetween(1, 1.0 / 16.0, 16.0, argc)
        env.assertShipInRange(ship)
        return SpellAction.Result(
            Spell(ship.uniqueId, scale),
            (MediaConstants.SHARD_UNIT * abs(ship.logicalPose().scale().x() - scale)).toLong(),
            listOf(ParticleSpray.burst(ship.logicalPose().position().toMinecraft(), scale)),
        )
    }

    private data class Spell(val shipId: UUID, val scale: Double) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            SableShipAccess.find(env.world, shipId)?.let { SableScaleBridge.setVisualScale(it, scale) }
        }
    }
}

private fun org.joml.Vector3dc.toMinecraft() = Vec3(x(), y(), z())
