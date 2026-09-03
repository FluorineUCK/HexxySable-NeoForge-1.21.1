package io.github.techtastic.hexxysable.mixin.hexical;

import io.github.techtastic.hexxysable.sablecompat.SableShipAccess;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Keeps Hexical's pedestal display entity in the Sable plot after addFreshEntity may kick it out. */
@Pseudo
@Mixin(targets = "miyucomics.hexical.features.pedestal.PedestalBlockEntity", remap = false)
public abstract class MixinPedestalBlockEntity {
    @Shadow(remap = false)
    private ItemEntity heldEntity;

    @Shadow(remap = false)
    public abstract Vec3 getItemPosition();

    @Shadow(remap = false)
    public abstract Vec3i getNormalVector();

    @Inject(method = "configureItemEntity", at = @At("TAIL"), remap = false)
    private void hexxysable$restoreAfterConfigure(CallbackInfo ci) {
        hexxysable$restorePlotPosition();
    }

    @Inject(method = "updateItemEntity", at = @At("TAIL"), remap = false)
    private void hexxysable$restoreAfterUpdate(CallbackInfo ci) {
        hexxysable$restorePlotPosition();
    }

    private void hexxysable$restorePlotPosition() {
        BlockEntity self = (BlockEntity) (Object) this;
        if (!(self.getLevel() instanceof ServerLevel level) || heldEntity == null) {
            return;
        }
        if (SableShipAccess.INSTANCE.containing(level, self.getBlockPos()) == null) {
            return;
        }

        Vec3 displayPosition = getItemPosition();
        Vec3 entityPosition = displayPosition.subtract(Vec3.atLowerCornerOf(getNormalVector()).scale(0.1));
        heldEntity.setPos(entityPosition);
        heldEntity.setBoundingBox(new AABB(displayPosition, displayPosition).inflate(0.25));
    }
}
