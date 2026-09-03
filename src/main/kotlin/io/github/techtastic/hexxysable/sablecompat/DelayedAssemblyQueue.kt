package io.github.techtastic.hexxysable.sablecompat

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

class DelayedAssemblyQueue<K, V> {
    private val pending = ConcurrentHashMap<K, ConcurrentLinkedQueue<V>>()

    val size: Int
        get() = pending.values.sumOf { it.size }

    fun clear() = pending.clear()

    fun enqueue(key: K, value: V) {
        pending.computeIfAbsent(key) { ConcurrentLinkedQueue() }.add(value)
    }

    fun drainFor(key: K, consumer: (V) -> Unit) {
        val queue = pending.remove(key) ?: return
        val initialSize = queue.size
        for (index in 0 until initialSize) {
            val value = queue.poll() ?: break
            consumer(value)
        }
        if (queue.isNotEmpty()) {
            pending.merge(key, queue) { current, remainder ->
                remainder.addAll(current)
                remainder
            }
        }
    }
}
