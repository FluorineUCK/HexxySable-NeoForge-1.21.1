package io.github.techtastic.hexxysable.interop

import at.petrak.hexcasting.api.casting.iota.EntityIota
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.phys.Vec3

object HexicalInterop {
    private const val MESH_CLASS = "miyucomics.hexical.features.specklikes.mesh.MeshEntity"

    /** Reflection keeps Hexical optional and isolates its implementation types. */
    fun meshPoints(iota: EntityIota, level: ServerLevel): List<Vec3>? {
        val mesh = iota.getEntity(level) ?: return null
        if (mesh.javaClass.name != MESH_CLASS) return null
        val shape = mesh.javaClass.getMethod("getShape").invoke(mesh) as? Iterable<*> ?: return null
        return shape.map { point ->
            val iotaPoint = point ?: return null
            iotaPoint.javaClass.getMethod("getVec3").invoke(iotaPoint) as? Vec3 ?: return null
        }
    }
}
