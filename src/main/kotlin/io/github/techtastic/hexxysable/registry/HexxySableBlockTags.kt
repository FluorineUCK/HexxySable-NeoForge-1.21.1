package io.github.techtastic.hexxysable.registry

import io.github.techtastic.hexxysable.HexxySable
import net.minecraft.core.registries.Registries
import net.minecraft.tags.TagKey
import net.minecraft.world.level.block.Block

object HexxySableBlockTags {
    @JvmField
    val ASSEMBLE_BLACKLIST: TagKey<Block> =
        TagKey.create(Registries.BLOCK, HexxySable.id("assemble_blacklist"))
}
