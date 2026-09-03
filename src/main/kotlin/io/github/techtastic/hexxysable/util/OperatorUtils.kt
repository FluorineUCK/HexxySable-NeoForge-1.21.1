package io.github.techtastic.hexxysable.util

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.casting.iota.EntityIota
import at.petrak.hexcasting.api.casting.iota.Vec3Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.casting.mishaps.MishapNotEnoughArgs
import io.github.techtastic.hexxysable.casting.iota.ShipIota
import io.github.techtastic.hexxysable.casting.mishaps.MishapShipNotLoaded
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.phys.Vec3
import dev.ryanhcode.sable.sublevel.ServerSubLevel

fun List<Iota>.getShip(level: ServerLevel, index: Int, argc: Int): ServerSubLevel {
    val iota = getOrElse(index) { throw MishapNotEnoughArgs(index + 1, size) }
    if (iota !is ShipIota) throw MishapInvalidIota.ofType(iota, argc - index - 1, "ship")
    return iota.getShip(level) ?: throw MishapShipNotLoaded()
}

fun List<Iota>.getListOfVecs(level: ServerLevel, index: Int, argc: Int): List<Vec3> {
    val iota = getOrElse(index) { throw MishapNotEnoughArgs(index + 1, size) }
    if (iota is ListIota) {
        return iota.list.map {
            (it as? Vec3Iota)?.vec3 ?: throw MishapInvalidIota.ofType(iota, argc - index - 1, "list.vec")
        }
    }
    if (iota is EntityIota && net.neoforged.fml.ModList.get().isLoaded("hexical")) {
        return io.github.techtastic.hexxysable.interop.HexicalInterop.meshPoints(iota, level)
            ?: throw MishapInvalidIota.ofType(iota, argc - index - 1, "list.vec")
    }
    throw MishapInvalidIota.ofType(iota, argc - index - 1, "list.vec")
}
