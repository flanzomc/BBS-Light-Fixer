package dev.flanzomc.bbslightfixer.mixin;

import dev.flanzomc.bbslightfixer.CmlEffectValues;
import mchorse.bbs_mod.forms.forms.BlockForm;
import mchorse.bbs_mod.forms.renderers.BlockFormRenderer;
import mchorse.bbs_mod.forms.renderers.FormRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.OverlayVertexConsumer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BlockFormRenderer.class, remap = false)
abstract class BlockBreakingMixin extends FormRenderer<BlockForm>
{
    private BlockBreakingMixin(BlockForm form) { super(form); }

    @Inject(method = "renderBlock", at = @At("RETURN"))
    private void bbsLight$breaking(MatrixStack matrices, VertexConsumerProvider consumers, int light, int overlay, boolean picking, CallbackInfo ci)
    {
        int stage = CmlEffectValues.of(this.form).breaking.get();
        MinecraftClient client = MinecraftClient.getInstance();

        if (picking || stage <= 0 || client.world == null) return;

        VertexConsumer output = new OverlayVertexConsumer(
            consumers.getBuffer(ModelLoader.BLOCK_DESTRUCTION_RENDER_LAYERS.get(stage - 1)),
            matrices.peek().getPositionMatrix(),
            matrices.peek().getNormalMatrix(),
            1F
        );

        client.getBlockRenderManager().renderDamage(this.form.blockState.get(), BlockPos.ORIGIN, client.world, matrices, output);
    }
}
