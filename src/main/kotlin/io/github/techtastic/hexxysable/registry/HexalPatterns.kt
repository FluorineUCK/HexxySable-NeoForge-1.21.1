package io.github.techtastic.hexxysable.registry

import at.petrak.hexcasting.api.casting.ActionRegistryEntry
import at.petrak.hexcasting.api.casting.math.HexDir
import at.petrak.hexcasting.api.casting.math.HexPattern
import io.github.techtastic.hexxysable.HexxySable
import io.github.techtastic.hexxysable.casting.patterns.hexal.OpShipyardWisp
import net.minecraft.resources.ResourceLocation

/** Isolated so the JVM never resolves Hexal classes when Hexal is absent. */
object HexalPatterns {
    @JvmStatic
    fun register(registrar: (ResourceLocation, ActionRegistryEntry) -> Unit) {
        registrar(
            HexxySable.id("embark"),
            ActionRegistryEntry(
                HexPattern.fromAngles("wdewwedwawwdewdwewd", HexDir.EAST),
                OpShipyardWisp(true),
            ),
        )
        registrar(
            HexxySable.id("disembark"),
            ActionRegistryEntry(
                HexPattern.fromAngles("wdewwedwawwqawqwawq", HexDir.EAST),
                OpShipyardWisp(false),
            ),
        )
    }
}
