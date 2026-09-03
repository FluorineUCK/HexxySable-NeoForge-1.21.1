package io.github.techtastic.hexxysable.sablecompat

import org.joml.Vector3dc

object ProjectedRange {
    fun within(first: Vector3dc, second: Vector3dc, maximumDistanceSquared: Double): Boolean =
        first.distanceSquared(second) <= maximumDistanceSquared
}
