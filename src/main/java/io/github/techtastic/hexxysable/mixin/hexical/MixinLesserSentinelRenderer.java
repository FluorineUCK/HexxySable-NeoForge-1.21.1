package io.github.techtastic.hexxysable.mixin.hexical;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

/** Projects Hexical's world-space sentinel marker through Sable's render pose. */
@Pseudo
@Mixin(targets = "miyucomics.hexical.features.lesser_sentinels.LesserSentinelRenderer", remap = false)
public abstract class MixinLesserSentinelRenderer {
    @WrapOperation(
        method = "onRenderLevel",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(DDD)V",
            remap = true
        ),
        remap = false
    )
    private void hexxysable$renderOnSable(
        PoseStack matrices,
        double x,
        double y,
        double z,
        Operation<Void> original
    ) {
        Vec3 camera = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        Vec3 localPosition = new Vec3(x + camera.x, y + camera.y, z + camera.z);
        ClientSubLevel subLevel = Sable.HELPER.getContainingClient(localPosition);
        if (subLevel == null) {
            original.call(matrices, x, y, z);
            return;
        }

        Pose3dc pose = subLevel.renderPose();
        Vec3 worldPosition = pose.transformPosition(localPosition);
        original.call(
            matrices,
            worldPosition.x - camera.x,
            worldPosition.y - camera.y,
            worldPosition.z - camera.z
        );
        Vector3dc scale = pose.scale();
        matrices.scale((float) scale.x(), (float) scale.y(), (float) scale.z());
    }
}
