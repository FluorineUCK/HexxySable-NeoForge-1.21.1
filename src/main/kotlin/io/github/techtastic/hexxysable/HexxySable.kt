package io.github.techtastic.hexxysable

import net.minecraft.resources.ResourceLocation
import org.slf4j.LoggerFactory

object HexxySable {
    const val MOD_ID = "hexxysable"
    @JvmField val LOGGER = LoggerFactory.getLogger(MOD_ID)

    @JvmStatic
    fun id(path: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath(MOD_ID, path)
}
