package dev.bbslight.mixin;

import dev.bbslight.ModelBlockAnchor;
import dev.bbslight.Lights;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets="mchorse.bbs_mod.client.renderer.ModelBlockEntityRenderer", remap=false)
abstract class ModelBlockRendererMixin {
    @Inject(method="render", at=@At("HEAD"), remap=false)
    private void bbsLight$anchor(@Coerce Object entity, float delta, MatrixStack matrices,
                               VertexConsumerProvider consumers, int light, int overlay, CallbackInfo ci) {
        BlockPos p = ((BlockEntity) entity).getPos();
        Lights.beginPlaced(entity);
        ModelBlockAnchor.begin(matrices.peek().getPositionMatrix(), p.getX(), p.getY(), p.getZ(), entity);
    }
    @Inject(method="render", at=@At("RETURN"), remap=false)
    private void bbsLight$end(@Coerce Object entity, float delta, MatrixStack matrices,
                            VertexConsumerProvider consumers, int light, int overlay, CallbackInfo ci) {
        ModelBlockAnchor.end();
    }
}
