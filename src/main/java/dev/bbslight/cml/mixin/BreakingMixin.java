package dev.bbslight.cml.mixin;
import dev.bbslight.cml.EffectValues;
import dev.bbslight.cml.EffectScope;
import mchorse.bbs_mod.forms.forms.BlockForm;
import mchorse.bbs_mod.forms.renderers.BlockFormRenderer;
import mchorse.bbs_mod.forms.renderers.FormRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(value=BlockFormRenderer.class,remap=false)
abstract class BreakingMixin extends FormRenderer<BlockForm> {
 private BreakingMixin(BlockForm form){super(form);}
 @Inject(method="renderBlock",at=@At("HEAD"))
 private void localEffects(MatrixStack matrices,VertexConsumerProvider consumers,int light,int overlay,boolean picking,CallbackInfo ci){
  MatrixStack root=new MatrixStack();
  root.peek().getPositionMatrix().set(matrices.peek().getPositionMatrix()).translate(.5F,0,.5F);
  EffectScope.replaceCurrent(EffectScope.capture(form,root,picking));
 }
 @Inject(method="renderBlock",at=@At("RETURN"))
 private void damage(MatrixStack matrices,VertexConsumerProvider consumers,int light,int overlay,boolean picking,CallbackInfo ci){
  int stage=EffectValues.of(form).breaking.get();var mc=MinecraftClient.getInstance();
  if(picking||stage==0||mc.world==null)return;
  VertexConsumer out=new OverlayVertexConsumer(consumers.getBuffer(ModelLoader.BLOCK_DESTRUCTION_RENDER_LAYERS.get(stage-1)),matrices.peek().getPositionMatrix(),matrices.peek().getNormalMatrix(),1F);
  mc.getBlockRenderManager().renderDamage(form.blockState.get(),BlockPos.ORIGIN,mc.world,matrices,out);
 }
}
