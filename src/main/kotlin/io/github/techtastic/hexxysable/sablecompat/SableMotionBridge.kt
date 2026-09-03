package io.github.techtastic.hexxysable.sablecompat

import dev.ryanhcode.sable.api.sublevel.SubLevelContainer
import dev.ryanhcode.sable.neoforge.event.ForgeSablePrePhysicsTickEvent
import dev.ryanhcode.sable.sublevel.ServerSubLevel
import org.joml.Vector3d
import net.neoforged.neoforge.event.server.ServerStoppedEvent
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

object SableMotionBridge {
    private data class ScopedShip(val dimension: ResourceKey<Level>, val id: UUID)
    private val scopedRequests = ConcurrentHashMap<ScopedShip, ConcurrentLinkedQueue<MotionRequest>>()

    fun enqueue(dimension: ResourceKey<Level>, request: MotionRequest.Force) {
        scopedRequests.computeIfAbsent(ScopedShip(dimension, request.subLevelId)) { ConcurrentLinkedQueue() }.add(request)
    }

    fun enqueue(dimension: ResourceKey<Level>, request: MotionRequest.Torque) {
        scopedRequests.computeIfAbsent(ScopedShip(dimension, request.subLevelId)) { ConcurrentLinkedQueue() }.add(request)
    }

    @JvmStatic
    fun onServerStopped(event: ServerStoppedEvent) {
        scopedRequests.clear()
    }

    @JvmStatic
    fun onPrePhysicsTick(event: ForgeSablePrePhysicsTickEvent) {
        val system = event.physicsSystem
        val dimension = system.level.dimension()
        val container = SubLevelContainer.getContainer(system.level) ?: return
        for (ship in container.allSubLevels.filterIsInstance<ServerSubLevel>()) {
            val handle = system.getPhysicsHandle(ship)
            if (!handle.isValid) continue
            val requestQueue = scopedRequests.remove(ScopedShip(dimension, ship.uniqueId)) ?: continue
            val initialSize = requestQueue.size
            for (index in 0 until initialSize) {
                val request = requestQueue.poll() ?: break
                val timeStep = event.timeStep
                val pose = ship.logicalPose()
                when (request) {
                    is MotionRequest.Force -> {
                        val localImpulse = when (request.referenceSpace) {
                            ReferenceSpace.WORLD -> pose.transformNormalInverse(Vector3d(request.motion))
                            ReferenceSpace.MODEL, ReferenceSpace.BODY -> Vector3d(request.motion)
                        }.mul(timeStep)
                        val modelPoint = when (request.referenceSpace) {
                            ReferenceSpace.WORLD -> pose.transformPositionInverse(Vector3d(request.position))
                            ReferenceSpace.MODEL -> Vector3d(request.position)
                            ReferenceSpace.BODY -> ReferenceSpaceTransform.bodyPointToModel(
                                request.position,
                                ship.massTracker.centerOfMass ?: continue,
                            )
                        }
                        handle.applyImpulseAtPoint(modelPoint, localImpulse)
                    }

                    is MotionRequest.Torque -> {
                        val localImpulse = when (request.referenceSpace) {
                            ReferenceSpace.WORLD -> pose.transformNormalInverse(Vector3d(request.motion))
                            ReferenceSpace.MODEL, ReferenceSpace.BODY -> Vector3d(request.motion)
                        }.mul(timeStep)
                        handle.applyTorqueImpulse(localImpulse)
                    }
                }
            }
            if (requestQueue.isNotEmpty()) {
                scopedRequests.merge(ScopedShip(dimension, ship.uniqueId), requestQueue) { current, pending ->
                    pending.addAll(current)
                    pending
                }
            }
        }
    }
}
