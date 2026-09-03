package io.github.techtastic.hexxysable.sablecompat

import org.joml.Vector3d
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.UUID

class QueuedMotionTest {
    @Test
    fun `force is integrated over exactly one physics substep`() {
        val id = UUID.randomUUID()
        val queue = QueuedMotion()
        queue.enqueueForce(
            MotionRequest.Force(
                id,
                ReferenceSpace.WORLD,
                Vector3d(120.0, 0.0, 0.0),
                Vector3d(),
            ),
        )

        var applied = Vector3d()
        queue.drainFor(id, 0.05) { request, impulseScale ->
            applied = Vector3d(request.motion).mul(impulseScale)
        }

        assertEquals(6.0, applied.x, 1.0e-9)
        assertEquals(0, queue.size)
    }

    @Test
    fun `request is not consumed by another dimension or body`() {
        val id = UUID.randomUUID()
        val queue = QueuedMotion()
        queue.enqueueTorque(MotionRequest.Torque(id, ReferenceSpace.MODEL, Vector3d(0.0, 4.0, 0.0)))

        queue.drainFor(UUID.randomUUID(), 0.05) { _, _ -> error("must not apply") }

        assertEquals(1, queue.size)
    }

    @Test
    fun `draining a substep consumes only the requests present at substep start`() {
        val id = UUID.randomUUID()
        val queue = QueuedMotion()
        queue.enqueueTorque(MotionRequest.Torque(id, ReferenceSpace.BODY, Vector3d(1.0, 0.0, 0.0)))

        queue.drainFor(id, 0.05) { _, _ ->
            queue.enqueueTorque(MotionRequest.Torque(id, ReferenceSpace.BODY, Vector3d(2.0, 0.0, 0.0)))
        }

        assertEquals(1, queue.size)
    }
}
