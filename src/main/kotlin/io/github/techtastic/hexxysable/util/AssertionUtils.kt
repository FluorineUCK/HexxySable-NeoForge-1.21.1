package io.github.techtastic.hexxysable.util

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.env.CircleCastEnv
import dev.ryanhcode.sable.Sable
import dev.ryanhcode.sable.sublevel.ServerSubLevel
import io.github.techtastic.hexxysable.casting.mishaps.MishapShipTooFarAway
import net.minecraft.world.phys.Vec3

fun CastingEnvironment.assertShipInRange(ship: ServerSubLevel) {
    val position = io.github.techtastic.hexxysable.sablecompat.SableShipAccess.worldCenterOfMass(ship)
        .let { Vec3(it.x(), it.y(), it.z()) }
    assertVecInWorld(position)

    if (this is CircleCastEnv && impetus?.blockPos?.let {
            io.github.techtastic.hexxysable.sablecompat.SableShipAccess.containing(world, it)?.uniqueId
        } == ship.uniqueId
    ) return

    if (castingEntity?.let { Sable.HELPER.getTrackingOrVehicleSubLevel(it)?.uniqueId } == ship.uniqueId) return
    if (!isVecInRange(position)) throw MishapShipTooFarAway(ship)
}
