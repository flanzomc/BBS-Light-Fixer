package dev.flanzomc.bbslightfixer.mixin;
import dev.flanzomc.bbslightfixer.render.EffectLayer;
import dev.flanzomc.bbslightfixer.render.EffectRenderContext;
import dev.flanzomc.bbslightfixer.render.EffectShaders;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
@Mixin(BlockRenderManager.class)
abstract class BlockRenderManagerMixin{
 @ModifyArg(method="renderBlockAsEntity",at=@At(value="INVOKE",target="Lnet/minecraft/client/render/VertexConsumerProvider;getBuffer(Lnet/minecraft/client/render/RenderLayer;)Lnet/minecraft/client/render/VertexConsumer;"),index=0)
 private RenderLayer bbsLight$layer(RenderLayer original){return EffectRenderContext.active()&&EffectShaders.block!=null?EffectLayer.BLOCK:original;}
}
