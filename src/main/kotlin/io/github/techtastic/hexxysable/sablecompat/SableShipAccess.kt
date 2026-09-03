package io.github.techtastic.hexxysable.sablecompat

import dev.ryanhcode.sable.Sable
import dev.ryanhcode.sable.api.SubLevelAssemblyHelper
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer
import dev.ryanhcode.sable.companion.math.BoundingBox3d
import dev.ryanhcode.sable.companion.math.BoundingBox3i
import dev.ryanhcode.sable.physics.config.block_properties.PhysicsBlockPropertyHelper
import dev.ryanhcode.sable.sublevel.ServerSubLevel
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import org.joml.Vector3d
import java.util.UUID

object SableShipAccess {
    fun find(level: ServerLevel, id: UUID): ServerSubLevel? =
        SubLevelContainer.getContainer(level)?.getSubLevel(id) as? ServerSubLevel

    fun containing(level: ServerLevel, pos: BlockPos): ServerSubLevel? =
        Sable.HELPER.getContaining(level, pos) as? ServerSubLevel

    fun containing(level: ServerLevel, pos: Vec3): ServerSubLevel? =
        Sable.HELPER.getContaining(level, pos) as? ServerSubLevel

    fun intersecting(level: ServerLevel, area: AABB): List<ServerSubLevel> {
        val box = BoundingBox3d(area.minX, area.minY, area.minZ, area.maxX, area.maxY, area.maxZ)
        return Sable.HELPER.getAllIntersecting(level, box).filterIsInstance<ServerSubLevel>()
    }

    fun worldCenterOfMass(ship: ServerSubLevel): Vector3d =
        ship.logicalPose().transformPosition(Vector3d(ship.massTracker.centerOfMass))

    fun blockMass(level: ServerLevel, pos: BlockPos): Double =
        PhysicsBlockPropertyHelper.getMass(level, pos, level.getBlockState(pos))

    fun assemble(level: ServerLevel, positions: Set<BlockPos>): ServerSubLevel {
        val bounds = BoundingBox3i.from(positions)
        return SubLevelAssemblyHelper.assembleBlocks(level, positions.first(), positions, bounds)
    }

    fun ServerSubLevel.toTransferPose(): EntityCoordinateTransfer.Pose {
        val pose = logicalPose()
        return EntityCoordinateTransfer.Pose(
            pose.position(),
            pose.orientation(),
            pose.rotationPoint(),
            pose.scale(),
        )
    }
}
