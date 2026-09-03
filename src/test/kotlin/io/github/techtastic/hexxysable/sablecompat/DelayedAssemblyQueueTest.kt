package io.github.techtastic.hexxysable.sablecompat

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DelayedAssemblyQueueTest {
    @Test
    fun `draining a dimension preserves assemblies queued during that drain`() {
        val queue = DelayedAssemblyQueue<String, Int>()
        queue.enqueue("overworld", 1)

        val firstPass = mutableListOf<Int>()
        queue.drainFor("overworld") { value ->
            firstPass += value
            queue.enqueue("overworld", 2)
        }

        assertEquals(listOf(1), firstPass)
        assertEquals(1, queue.size)

        val secondPass = mutableListOf<Int>()
        queue.drainFor("overworld", secondPass::add)
        assertEquals(listOf(2), secondPass)
        assertEquals(0, queue.size)
    }

    @Test
    fun `assemblies remain isolated by dimension`() {
        val queue = DelayedAssemblyQueue<String, Int>()
        queue.enqueue("overworld", 1)
        queue.enqueue("nether", 2)

        val drained = mutableListOf<Int>()
        queue.drainFor("overworld", drained::add)

        assertEquals(listOf(1), drained)
        assertEquals(1, queue.size)
    }
}
