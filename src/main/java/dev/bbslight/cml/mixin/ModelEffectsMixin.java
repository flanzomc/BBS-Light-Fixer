package dev.bbslight.cml.mixin;
import dev.bbslight.cml.*;
import mchorse.bbs_mod.forms.forms.ModelForm;
import mchorse.bbs_mod.forms.entities.IEntity;
import mchorse.bbs_mod.forms.renderers.*;
import mchorse.bbs_mod.client.BBSRendering;
import mchorse.bbs_mod.cubic.ModelInstance;
import mchorse.bbs_mod.utils.colors.Color;
import mchorse.bbs_mod.ui.framework.elements.utils.StencilMap;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.util.math.MatrixStack;
import java.util.function.Supplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(value=ModelFormRenderer.class,remap=false)
abstract class ModelEffectsMixin extends FormRenderer<ModelForm> {
 private ModelEffectsMixin(ModelForm form){super(form);}
 @Inject(method="renderModel",at=@At("HEAD"))
 private void scope(IEntity entity,Supplier<ShaderProgram> program,MatrixStack stack,ModelInstance model,int light,int overlay,Color context,Color color,boolean additive,boolean ui,StencilMap stencil,float transition,MatrixStack world,CallbackInfo ci){
  EffectScope.push(EffectScope.capture(form,stack,stencil!=null));
 }
 @Inject(method="renderModel",at=@At("RETURN"))
 private void end(CallbackInfo ci){EffectScope.pop();}
 @ModifyVariable(method="renderModel",at=@At("HEAD"),argsOnly=true,ordinal=0)
 private Supplier<ShaderProgram> shader(Supplier<ShaderProgram> original,IEntity entity,Supplier<ShaderProgram> program,MatrixStack stack,ModelInstance model,int light,int overlay,Color context,Color color,boolean additive,boolean ui,StencilMap stencil,float transition,MatrixStack world){
  if(stencil!=null||!EffectValues.of(form).active()||PortShaders.model==null||(BBSRendering.isIrisShadersEnabled()&&BBSRendering.isRenderingWorld()))return original;
  return model.isVAORendered()?()->PortShaders.model:()->PortShaders.block;
 }
}
