package io.github.techtastic.hexxysable.casting.patterns

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getBlockPos
import at.petrak.hexcasting.api.casting.getPositiveDouble
import at.petrak.hexcasting.api.casting.getVec3
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.Vec3Iota
import io.github.techtastic.hexxysable.casting.iota.ShipIota
import io.github.techtastic.hexxysable.casting.mishaps.MishapNotOnShip
import io.github.techtastic.hexxysable.sablecompat.SableShipAccess
import io.github.techtastic.hexxysable.sablecompat.SableAmbitComponent
import io.github.techtastic.hexxysable.util.assertShipInRange
import io.github.techtastic.hexxysable.util.getShip
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import org.joml.Vector3d

object OpShipFromPos : ConstMediaAction {
    override val argc = 1
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val pos = args.getBlockPos(0, argc)
        val worldPosition = SableAmbitComponent.projectToWorld(env, pos.center)
        env.assertVecInWorld(worldPosition)
        if (!env.isVecInRange(pos.center)) {
            throw at.petrak.hexcasting.api.casting.mishaps.MishapBadLocation(worldPosition, "too_far")
        }
        val ship = SableShipAccess.containing(env.world, pos) ?: throw MishapNotOnShip(pos.center)
        env.assertShipInRange(ship)
        return listOf(ShipIota(ship.uniqueId, ship.name))
    }
}

object OpGetShipsBy : ConstMediaAction {
    override val argc = 2
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        var center = args.getVec3(0, argc)
        val radius = args.getPositiveDouble(1, argc)
        val worldCenter = SableAmbitComponent.projectToWorld(env, center)
        env.assertVecInWorld(worldCenter)
        if (!env.isVecInRange(center)) {
            throw at.petrak.hexcasting.api.casting.mishaps.MishapBadLocation(worldCenter, "too_far")
        }
        SableShipAccess.containing(env.world, center)?.let {
            center = it.logicalPose().transformPosition(center)
        }
        val area = AABB(center.add(-radius, -radius, -radius), center.add(radius, radius, radius))
        return SableShipAccess.intersecting(env.world, area)
            .distinctBy { it.uniqueId }
            .sortedBy { SableShipAccess.worldCenterOfMass(it).distanceSquared(center.x, center.y, center.z) }
            .map { ShipIota(it.uniqueId, it.name) }
    }
}

class OpShipGetCenterOfMass(private val worldSpace: Boolean) : ConstMediaAction {
    override val argc = 1
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val ship = args.getShip(env.world, 0, argc)
        env.assertShipInRange(ship)
        val center = if (worldSpace) SableShipAccess.worldCenterOfMass(ship) else Vector3d(ship.massTracker.centerOfMass)
        return listOf(Vec3Iota(center.toMinecraft()))
    }
}

object OpGetMass : ConstMediaAction {
    override val argc = 1
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val mass = if (args[0] is ShipIota) {
            args.getShip(env.world, 0, argc).massTracker.mass
        } else {
            val pos = args.getBlockPos(0, argc)
            val worldPosition = SableAmbitComponent.projectToWorld(env, pos.center)
            env.assertVecInWorld(worldPosition)
            if (!env.isVecInRange(pos.center)) {
                throw at.petrak.hexcasting.api.casting.mishaps.MishapBadLocation(worldPosition, "too_far")
            }
            SableShipAccess.blockMass(env.world, pos)
        }
        return listOf(DoubleIota(mass))
    }
}

object OpShipGetRotEuler : ConstMediaAction {
    override val argc = 1
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val ship = args.getShip(env.world, 0, argc)
        env.assertShipInRange(ship)
        return listOf(Vec3Iota(ship.logicalPose().orientation().getEulerAnglesYXZ(Vector3d()).toMinecraft()))
    }
}

object OpShipGetScale : ConstMediaAction {
    override val argc = 1
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val ship = args.getShip(env.world, 0, argc)
        env.assertShipInRange(ship)
        return listOf(DoubleIota(ship.logicalPose().scale().x()))
    }
}

class OpShipGetVelocity(private val linear: Boolean) : ConstMediaAction {
    override val argc = 1
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val ship = args.getShip(env.world, 0, argc)
        env.assertShipInRange(ship)
        val handle = dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem.require(env.world).getPhysicsHandle(ship)
        val velocity = if (linear) handle.getLinearVelocity(Vector3d()) else handle.getAngularVelocity(Vector3d())
        return listOf(Vec3Iota(velocity.toMinecraft()))
    }
}

object OpShipGetAABB : ConstMediaAction {
    override val argc = 1
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val ship = args.getShip(env.world, 0, argc)
        env.assertShipInRange(ship)
        val box = ship.plot.boundingBox
        return listOf(
            Vec3Iota(Vec3(box.minX().toDouble(), box.minY().toDouble(), box.minZ().toDouble())),
            Vec3Iota(Vec3(box.maxX().toDouble(), box.maxY().toDouble(), box.maxZ().toDouble())),
        )
    }
}

private fun org.joml.Vector3dc.toMinecraft() = Vec3(x(), y(), z())
