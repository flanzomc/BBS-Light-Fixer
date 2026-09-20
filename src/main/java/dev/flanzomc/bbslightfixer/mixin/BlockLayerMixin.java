package dev.flanzomc.bbslightfixer.mixin;

import dev.flanzomc.bbslightfixer.CmlEffectLayer;
import dev.flanzomc.bbslightfixer.CmlRenderState;
import dev.flanzomc.bbslightfixer.CmlShaders;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.BlockRenderManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(BlockRenderManager.class)
abstract class BlockLayerMixin
{
    @ModifyArg(
        method = "renderBlockAsEntity",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumerProvider;getBuffer(Lnet/minecraft/client/render/RenderLayer;)Lnet/minecraft/client/render/VertexConsumer;"),
        index = 0
    )
    private RenderLayer bbsLight$effectLayer(RenderLayer original)
    {
        return CmlRenderState.current().active() && CmlShaders.block != null ? CmlEffectLayer.BLOCK : original;
    }
}
