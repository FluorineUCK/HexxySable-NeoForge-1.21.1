package io.github.techtastic.hexxysable.mixin.moreiotas;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import io.github.techtastic.hexxysable.casting.iota.ShipIota;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ram.talia.moreiotas.api.casting.iota.StringIota;

import java.util.List;

@Pseudo
@Mixin(targets = "ram.talia.moreiotas.common.casting.actions.strings.OpNameGet", remap = false)
public abstract class MixinOpNameGet {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true, remap = false)
    private void hexxysable$nameShip(
        List<? extends Iota> args,
        CastingEnvironment env,
        CallbackInfoReturnable<List<Iota>> cir
    ) {
        if (!args.isEmpty() && args.getFirst() instanceof ShipIota ship) {
            cir.setReturnValue(List.of(StringIota.makeUnchecked(ship.getSlug() == null ? "" : ship.getSlug())));
        }
    }
}
