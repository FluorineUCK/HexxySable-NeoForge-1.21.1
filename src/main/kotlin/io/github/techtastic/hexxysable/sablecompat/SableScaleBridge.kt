package io.github.techtastic.hexxysable.sablecompat

import dev.ryanhcode.sable.sublevel.ServerSubLevel

object SableScaleBridge {
    /**
     * Sable 2.0.3 exposes render/network pose scale but no public Rapier collider
     * rescale operation. Keep this isolated so a future Sable API can replace it.
     */
    fun setVisualScale(ship: ServerSubLevel, scale: Double) {
        ship.logicalPose().scale().set(scale, scale, scale)
        ship.forceUpdateGlobalBounds()
    }
}
