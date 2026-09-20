package dev.flanzomc.bbslightfixer.mixin;

import dev.flanzomc.bbslightfixer.access.CmlColorAccess;
import dev.flanzomc.bbslightfixer.render.EffectRenderContext;
import dev.flanzomc.bbslightfixer.render.EffectShaders;
import dev.flanzomc.bbslightfixer.render.EffectState;
import mchorse.bbs_mod.client.BBSRendering;
import mchorse.bbs_mod.cubic.ModelInstance;
import mchorse.bbs_mod.forms.entities.IEntity;
import mchorse.bbs_mod.forms.forms.ModelForm;
import mchorse.bbs_mod.forms.renderers.FormRenderer;
import mchorse.bbs_mod.forms.renderers.ModelFormRenderer;
import mchorse.bbs_mod.ui.framework.elements.utils.StencilMap;
import mchorse.bbs_mod.utils.colors.Color;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(value=ModelFormRenderer.class,remap=false)
abstract class ModelFormRendererMixin extends FormRenderer<ModelForm>{
 private ModelFormRendererMixin(ModelForm form){super(form);}

 @Inject(method="renderModel",at=@At("HEAD"))
 private void bbsLight$begin(IEntity target,Supplier<ShaderProgram> program,MatrixStack stack,ModelInstance model,int light,int overlay,
  Color contextColor,Color formColor,boolean additive,boolean ui,StencilMap stencil,float transition,MatrixStack world,CallbackInfo ci){
  EffectRenderContext.push(EffectState.model(this.form,stack,!model.isVAORendered(),stencil!=null));
 }
 @Inject(method="renderModel",at=@At("RETURN"))
 private void bbsLight$end(CallbackInfo ci){EffectRenderContext.pop();}

 @ModifyVariable(method="renderModel",at=@At("HEAD"),argsOnly=true,ordinal=0)
 private Supplier<ShaderProgram> bbsLight$program(Supplier<ShaderProgram> original,IEntity target,Supplier<ShaderProgram> program,MatrixStack stack,
  ModelInstance model,int light,int overlay,Color contextColor,Color formColor,boolean additive,boolean ui,StencilMap stencil,float transition,MatrixStack world){
  if(stencil!=null||BBSRendering.isIrisShadersEnabled()&&BBSRendering.isRenderingWorld())return original;
  EffectState s=EffectState.model(this.form,stack,!model.isVAORendered(),false);
  if(!s.active)return original;
  return model.isVAORendered()?(EffectShaders.model==null?original:()->EffectShaders.model):(EffectShaders.block==null?original:()->EffectShaders.block);
 }

 @ModifyVariable(method="renderModel",at=@At("HEAD"),argsOnly=true,ordinal=1)
 private Color bbsLight$deferMaskedColor(Color original){
  CmlColorAccess x=(CmlColorAccess)(Object)original;
  if(!x.bbsLight$getColorTransform().isActive())return original;
  return new Color(1F,1F,1F,original.a);
 }
}
