package dev.flanzomc.bbslightfixer.mixin;

import dev.flanzomc.bbslightfixer.access.CmlBlockAccess;
import dev.flanzomc.bbslightfixer.access.CmlColorAccess;
import dev.flanzomc.bbslightfixer.render.EffectRenderContext;
import dev.flanzomc.bbslightfixer.render.EffectState;
import mchorse.bbs_mod.forms.forms.BlockForm;
import mchorse.bbs_mod.forms.renderers.BlockFormRenderer;
import mchorse.bbs_mod.forms.renderers.FormRenderer;
import mchorse.bbs_mod.forms.renderers.utils.FormColorBlend;
import mchorse.bbs_mod.utils.MathUtils;
import mchorse.bbs_mod.utils.colors.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayVertexConsumer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value=BlockFormRenderer.class,remap=false)
abstract class BlockFormRendererMixin extends FormRenderer<BlockForm>{
 private BlockFormRendererMixin(BlockForm form){super(form);}

 @Inject(method="renderBlock",at=@At("HEAD"))
 private void bbsLight$local(MatrixStack matrices,VertexConsumerProvider consumers,int light,int overlay,boolean picking,CallbackInfo ci){
  EffectRenderContext.replace(EffectState.block(form,matrices,picking));
 }

 @Redirect(method={"render3D","renderInUI"},at=@At(value="INVOKE",target="Lmchorse/bbs_mod/forms/renderers/utils/FormColorBlend;blend(Lmchorse/bbs_mod/utils/colors/Color;Lmchorse/bbs_mod/utils/colors/Color;Z)V"))
 private void bbsLight$deferMaskedColor(Color base,Color overlay,boolean additive){
  CmlColorAccess x=(CmlColorAccess)(Object)overlay;
  if(x.bbsLight$getColorTransform().isActive()){
   base.a*=MathUtils.clamp(overlay.a,0F,1F);
  }else{
   FormColorBlend.blend(base,overlay,additive);
  }
 }

 @Inject(method="renderBlock",at=@At("RETURN"))
 private void bbsLight$breaking(MatrixStack matrices,VertexConsumerProvider consumers,int light,int overlay,boolean picking,CallbackInfo ci){
  int stage=((CmlBlockAccess)(Object)form).bbsLight$getBreaking().get();
  MinecraftClient mc=MinecraftClient.getInstance();
  if(picking||stage<=0||mc.world==null)return;
  VertexConsumer out=new OverlayVertexConsumer(consumers.getBuffer(ModelLoader.BLOCK_DESTRUCTION_RENDER_LAYERS.get(Math.min(9,stage-1))),
    matrices.peek().getPositionMatrix(),matrices.peek().getNormalMatrix(),1F);
  mc.getBlockRenderManager().renderDamage(form.blockState.get(),BlockPos.ORIGIN,mc.world,matrices,out);
 }
}
