package dev.flanzomc.bbslightfixer.mixin;

import dev.flanzomc.bbslightfixer.CmlDynamicLights;
import mchorse.bbs_mod.blocks.entities.ModelBlockEntity;
import mchorse.bbs_mod.client.renderer.ModelBlockEntityRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ModelBlockEntityRenderer.class, remap = false)
abstract class ModelBlockMixin
{
    @Inject(method = "render(Lmchorse/bbs_mod/blocks/entities/ModelBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V", at = @At("HEAD"), remap = true)
    private void bbsLight$begin(ModelBlockEntity block, float delta, MatrixStack stack, VertexConsumerProvider consumers, int light, int overlay, CallbackInfo ci)
    {
        CmlDynamicLights.begin(block, stack);
    }

    @Inject(method = "render(Lmchorse/bbs_mod/blocks/entities/ModelBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V", at = @At("RETURN"), remap = true)
    private void bbsLight$end(ModelBlockEntity block, float delta, MatrixStack stack, VertexConsumerProvider consumers, int light, int overlay, CallbackInfo ci)
    {
        CmlDynamicLights.end();
    }
}
