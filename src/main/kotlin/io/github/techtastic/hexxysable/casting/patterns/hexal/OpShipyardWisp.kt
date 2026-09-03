package io.github.techtastic.hexxysable.casting.patterns.hexal

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster
import io.github.techtastic.hexxysable.sablecompat.SableEntityTransfer
import io.github.techtastic.hexxysable.sablecompat.SableShipAccess
import io.github.techtastic.hexxysable.util.assertShipInRange
import io.github.techtastic.hexxysable.util.getShip
import ram.talia.hexal.api.casting.eval.env.WispCastEnv
import java.util.UUID

/** Hexal's shipyard-wisp actions translated from VS shipyard space to Sable plot space. */
class OpShipyardWisp(private val toSubLevel: Boolean) : SpellAction {
    override val argc = 1

    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val ship = args.getShip(env.world, 0, argc)
        env.assertShipInRange(ship)
        val wispEnv = env as? WispCastEnv ?: throw MishapBadCaster()

        return SpellAction.Result(
            Spell(ship.uniqueId, toSubLevel),
            0,
            listOf(ParticleSpray.burst(wispEnv.wisp.position(), 0.75)),
        )
    }

    private data class Spell(val shipId: UUID, val toSubLevel: Boolean) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            val wispEnv = env as? WispCastEnv ?: return
            val ship = SableShipAccess.find(env.world, shipId) ?: return
            if (toSubLevel) {
                SableEntityTransfer.embark(wispEnv.wisp, ship)
            } else {
                SableEntityTransfer.disembark(wispEnv.wisp, ship)
            }
        }
    }
}
