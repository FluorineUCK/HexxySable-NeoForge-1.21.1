package io.github.techtastic.hexxysable.registry

import at.petrak.hexcasting.api.casting.ActionRegistryEntry
import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.math.HexDir
import at.petrak.hexcasting.api.casting.math.HexPattern
import io.github.techtastic.hexxysable.HexxySable
import io.github.techtastic.hexxysable.casting.patterns.*
import io.github.techtastic.hexxysable.casting.patterns.spells.OpAssemble
import io.github.techtastic.hexxysable.casting.patterns.spells.OpShipApply
import io.github.techtastic.hexxysable.casting.patterns.spells.OpShipSetScale
import io.github.techtastic.hexxysable.casting.patterns.moreiotas.OpShipGetMatrix
import io.github.techtastic.hexxysable.sablecompat.ReferenceSpace
import net.minecraft.resources.ResourceLocation

object HexxySablePatterns {
    data class Definition(val id: ResourceLocation, val entry: ActionRegistryEntry)
    private val definitions = mutableListOf<Definition>()

    @JvmStatic
    fun registerAll(registrar: (ResourceLocation, ActionRegistryEntry) -> Unit) =
        definitions.forEach { registrar(it.id, it.entry) }

    @JvmStatic
    fun registerMoreIotas(registrar: (ResourceLocation, ActionRegistryEntry) -> Unit) = listOf(
        definition("matrix/world_ship", "wdwaedewwedww", HexDir.NORTH_EAST, OpShipGetMatrix(OpShipGetMatrix.Type.WORLD_TO_SHIP)),
        definition("matrix/ship_world", "awdwdwwedwwwd", HexDir.SOUTH_EAST, OpShipGetMatrix(OpShipGetMatrix.Type.SHIP_TO_WORLD)),
        definition("matrix/moment_of_inertia_tensor", "ewwedwwwqdawdw", HexDir.SOUTH_WEST, OpShipGetMatrix(OpShipGetMatrix.Type.MOMENT_OF_INERTIA_TENSOR)),
    ).forEach { registrar(it.id, it.entry) }

    init {
        add("pos", "wdewwedw", HexDir.EAST, OpShipFromPos)
        add("zone_ship", "qqqqqwdeddwwwaqww", HexDir.SOUTH_EAST, OpGetShipsBy)
        add("com/world", "wdewwedwqqaq", HexDir.EAST, OpShipGetCenterOfMass(true))
        add("com/model", "wdewwedwdqaq", HexDir.EAST, OpShipGetCenterOfMass(false))
        add("mass", "wdewwedweeaa", HexDir.EAST, OpGetMass)
        add("rot/euler", "wdewwedwqwa", HexDir.EAST, OpShipGetRotEuler)
        add("scale/get", "wdewed", HexDir.EAST, OpShipGetScale)
        add("velocity/linear", "wdewwedwqwq", HexDir.EAST, OpShipGetVelocity(true))
        add("velocity/angular", "wdewwedwawe", HexDir.EAST, OpShipGetVelocity(false))
        add("aabb", "dewwedaeadewwedae", HexDir.EAST, OpShipGetAABB)
        add("assemble", "wewewewewewqdwwdwqqwdwwdwqqwdwwdwqq", HexDir.EAST, OpAssemble)
        add("scale/set", "wdewwedwadwwd", HexDir.EAST, OpShipSetScale)
        add("force/world", "qwwqawwwweqwaeawqaw", HexDir.SOUTH_EAST, OpShipApply(OpShipApply.Type.FORCE, ReferenceSpace.WORLD))
        add("torque/world", "wdewwedwqqqdaqqqa", HexDir.EAST, OpShipApply(OpShipApply.Type.TORQUE, ReferenceSpace.WORLD))
        add("force/model", "qwwqawwweqwaeawqaw", HexDir.SOUTH_EAST, OpShipApply(OpShipApply.Type.FORCE, ReferenceSpace.MODEL))
        add("torque/model", "wdewwedwqqqadeeed", HexDir.EAST, OpShipApply(OpShipApply.Type.TORQUE, ReferenceSpace.MODEL))
        add("force/body", "wdewwedwaqwaea", HexDir.EAST, OpShipApply(OpShipApply.Type.FORCE, ReferenceSpace.BODY))
        add("torque/body", "wdewwedweweedaqqqa", HexDir.EAST, OpShipApply(OpShipApply.Type.TORQUE, ReferenceSpace.BODY))
    }

    private fun add(path: String, signature: String, direction: HexDir, action: Action) {
        definitions += definition(path, signature, direction, action)
    }

    private fun definition(path: String, signature: String, direction: HexDir, action: Action): Definition =
        Definition(
            HexxySable.id(path),
            ActionRegistryEntry(HexPattern.fromAngles(signature, direction), action),
        )
}
