package dev.bbslight.cml.mixin;
import dev.bbslight.cml.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.BlockRenderManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
@Mixin(BlockRenderManager.class)
abstract class BlockLayerMixin {
    @ModifyArg(method="renderBlockAsEntity",at=@At(value="INVOKE",target="Lnet/minecraft/client/render/VertexConsumerProvider;getBuffer(Lnet/minecraft/client/render/RenderLayer;)Lnet/minecraft/client/render/VertexConsumer;"),index=0)
    private RenderLayer layer(RenderLayer original) { return EffectScope.current().active() && PortShaders.block!=null?EffectLayer.BLOCK:original; }
}
