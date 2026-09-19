package dev.bbslight.mixin;

import dev.bbslight.FormBridge;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.texture.SpriteAtlasTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/** Fullbright lightmap alone does not bypass entity directional shading. */
@Mixin(BlockRenderManager.class)
abstract class BlockRenderManagerMixin {
    @ModifyArg(method="renderBlockAsEntity", at=@At(value="INVOKE",
        target="Lnet/minecraft/client/render/VertexConsumerProvider;getBuffer(Lnet/minecraft/client/render/RenderLayer;)Lnet/minecraft/client/render/VertexConsumer;"), index=0)
    private RenderLayer bbsLight$emissive(RenderLayer original) {
        return FormBridge.glowing() ? RenderLayer.getEntityTranslucentEmissive(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE) : original;
    }
}
