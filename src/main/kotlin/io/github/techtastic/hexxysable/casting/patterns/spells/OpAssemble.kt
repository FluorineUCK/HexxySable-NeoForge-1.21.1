package io.github.techtastic.hexxysable.casting.patterns.spells

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.misc.MediaConstants
import io.github.techtastic.hexxysable.registry.HexxySableBlockTags
import io.github.techtastic.hexxysable.sablecompat.SableAmbitComponent
import io.github.techtastic.hexxysable.sablecompat.SableAssemblyBridge
import io.github.techtastic.hexxysable.util.getListOfVecs
import net.minecraft.core.BlockPos

object OpAssemble : SpellAction {
    override val argc = 1

    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val positions = args.getListOfVecs(env.world, 0, argc).map {
            val worldPosition = SableAmbitComponent.projectToWorld(env, it)
            env.assertVecInWorld(worldPosition)
            if (!env.isVecInRange(it)) {
                throw at.petrak.hexcasting.api.casting.mishaps.MishapBadLocation(worldPosition, "too_far")
            }
            BlockPos.containing(it)
        }.filterNot { env.world.getBlockState(it).`is`(HexxySableBlockTags.ASSEMBLE_BLACKLIST) }.toSet()
        if (positions.isEmpty()) throw MishapInvalidIota.ofType(args[0], 0, "list.vec.empty")
        return SpellAction.Result(
            Spell(positions),
            MediaConstants.SHARD_UNIT * positions.size,
            positions.map { ParticleSpray.cloud(it.center, 1.2) },
        )
    }

    private data class Spell(val positions: Set<BlockPos>) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            SableAssemblyBridge.enqueue(env.world, positions)
        }
    }
}
