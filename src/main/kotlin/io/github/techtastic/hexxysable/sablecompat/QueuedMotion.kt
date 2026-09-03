package io.github.techtastic.hexxysable.sablecompat

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

class QueuedMotion {
    private val requests = ConcurrentHashMap<UUID, ConcurrentLinkedQueue<MotionRequest>>()

    val size: Int
        get() = requests.values.sumOf { it.size }

    fun clear() = requests.clear()

    fun enqueueForce(request: MotionRequest.Force) = enqueue(request)

    fun enqueueTorque(request: MotionRequest.Torque) = enqueue(request)

    fun drainFor(
        subLevelId: UUID,
        timeStep: Double,
        consumer: (MotionRequest, Double) -> Unit,
    ) {
        require(timeStep.isFinite() && timeStep >= 0.0) { "Physics timestep must be finite and non-negative" }
        val queue = requests.remove(subLevelId) ?: return
        val initialSize = queue.size
        for (index in 0 until initialSize) {
            val request = queue.poll() ?: break
            consumer(request, timeStep)
        }
        if (queue.isNotEmpty()) {
            requests.merge(subLevelId, queue) { current, pending ->
                pending.addAll(current)
                pending
            }
        }
    }

    private fun enqueue(request: MotionRequest) {
        requests.computeIfAbsent(request.subLevelId) { ConcurrentLinkedQueue() }.add(request)
    }
}
