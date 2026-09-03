package io.github.techtastic.hexxysable.casting.mishaps

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.api.utils.TreeList
import dev.ryanhcode.sable.sublevel.ServerSubLevel
import net.minecraft.network.chat.Component
import net.minecraft.world.item.DyeColor
import net.minecraft.world.phys.Vec3

class MishapNotOnShip(private val pos: Vec3) : Mishap() {
    override fun accentColor(ctx: CastingEnvironment, errorCtx: Context): FrozenPigment = ctx.pigment
    override fun errorMessage(ctx: CastingEnvironment, errorCtx: Context): Component =
        Component.translatable("hexxysable.mishap.invalid.ship.pos", pos)
    override fun execute(env: CastingEnvironment, errorCtx: Context, stack: TreeList<Iota>) = stack
}

class MishapShipNotLoaded : Mishap() {
    override fun accentColor(ctx: CastingEnvironment, errorCtx: Context): FrozenPigment = ctx.pigment
    override fun errorMessage(ctx: CastingEnvironment, errorCtx: Context): Component =
        Component.translatable("hexxysable.mishap.invalid.ship.unloaded")
    override fun execute(env: CastingEnvironment, errorCtx: Context, stack: TreeList<Iota>) = stack
}

class MishapShipTooFarAway(private val ship: ServerSubLevel) : Mishap() {
    override fun accentColor(ctx: CastingEnvironment, errorCtx: Context): FrozenPigment = dyeColor(DyeColor.PINK)
    override fun errorMessage(ctx: CastingEnvironment, errorCtx: Context): Component =
        error("ship_too_far", ship.name ?: ship.uniqueId)
    override fun execute(env: CastingEnvironment, errorCtx: Context, stack: TreeList<Iota>): TreeList<Iota> {
        env.mishapEnvironment.yeetHeldItemsTowards(ship.logicalPose().position().toMinecraft())
        return stack
    }
}

private fun org.joml.Vector3dc.toMinecraft() = Vec3(x(), y(), z())
