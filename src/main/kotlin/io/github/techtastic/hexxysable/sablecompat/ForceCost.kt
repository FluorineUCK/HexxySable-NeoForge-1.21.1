package io.github.techtastic.hexxysable.sablecompat

import kotlin.math.sqrt

object ForceCost {
    fun dustUnits(motionLength: Double, mass: Double): Double {
        require(motionLength.isFinite() && motionLength >= 0.0) { "Motion magnitude must be finite and non-negative" }
        require(mass.isFinite() && mass > 0.0) { "Mass must be finite and positive" }

        val normalizedMotion = motionLength / (10.0 * mass * 60.0)
        return 0.1 + 10.0 * normalizedMotion * normalizedMotion * sqrt(mass / 1_000.0)
    }
}
