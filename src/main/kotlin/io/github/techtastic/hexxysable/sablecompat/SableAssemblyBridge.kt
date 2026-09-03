package io.github.techtastic.hexxysable.sablecompat

import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.neoforged.neoforge.event.tick.LevelTickEvent
import net.neoforged.neoforge.event.server.ServerStoppedEvent

object SableAssemblyBridge {
    private val queue = DelayedAssemblyQueue<ResourceKey<Level>, Set<BlockPos>>()

    fun enqueue(level: ServerLevel, positions: Set<BlockPos>) {
        queue.enqueue(level.dimension(), positions.mapTo(linkedSetOf()) { it.immutable() })
    }

    @JvmStatic
    fun onLevelTick(event: LevelTickEvent.Post) {
        val level = event.level as? ServerLevel ?: return
        queue.drainFor(level.dimension()) { positions ->
            SableShipAccess.assemble(level, positions)
        }
    }

    @JvmStatic
    fun onServerStopped(event: ServerStoppedEvent) = queue.clear()
}
