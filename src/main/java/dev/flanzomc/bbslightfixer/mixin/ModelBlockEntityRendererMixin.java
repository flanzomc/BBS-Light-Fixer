package dev.flanzomc.bbslightfixer.mixin;
import dev.flanzomc.bbslightfixer.light.DynamicLightBridge;
import mchorse.bbs_mod.blocks.entities.ModelBlockEntity;
import mchorse.bbs_mod.client.renderer.ModelBlockEntityRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(value=ModelBlockEntityRenderer.class,remap=false)
abstract class ModelBlockEntityRendererMixin{
 @Inject(method="render(Lmchorse/bbs_mod/blocks/entities/ModelBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V",at=@At("HEAD"),remap=true)
 private void bbsLight$begin(ModelBlockEntity b,float delta,MatrixStack s,VertexConsumerProvider v,int light,int overlay,CallbackInfo ci){DynamicLightBridge.begin(b,s);}
 @Inject(method="render(Lmchorse/bbs_mod/blocks/entities/ModelBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V",at=@At("RETURN"),remap=true)
 private void bbsLight$end(ModelBlockEntity b,float delta,MatrixStack s,VertexConsumerProvider v,int light,int overlay,CallbackInfo ci){DynamicLightBridge.end();}
}
