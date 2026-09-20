package dev.flanzomc.bbslightfixer.mixin;

import dev.flanzomc.bbslightfixer.light.DynamicLightBridge;
import dev.flanzomc.bbslightfixer.render.EffectRenderContext;
import dev.flanzomc.bbslightfixer.render.EffectState;
import mchorse.bbs_mod.forms.forms.BlockForm;
import mchorse.bbs_mod.forms.forms.Form;
import mchorse.bbs_mod.forms.renderers.FormRenderer;
import mchorse.bbs_mod.forms.renderers.FormRenderingContext;
import mchorse.bbs_mod.ui.framework.UIContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value=FormRenderer.class,remap=false)
abstract class FormRendererMixin{
 @Shadow protected Form form;
 @Inject(method="render",at=@At(value="INVOKE",target="Lmchorse/bbs_mod/forms/renderers/FormRenderer;render3D(Lmchorse/bbs_mod/forms/renderers/FormRenderingContext;)V"))
 private void bbsLight$before(FormRenderingContext context,CallbackInfo ci){
  if(form instanceof BlockForm)EffectRenderContext.push(EffectState.block(form,context.stack,context.isPicking()));
  else EffectRenderContext.push(EffectState.model(form,context.stack,false,context.isPicking()));
  if(!context.ui&&!context.isPicking())DynamicLightBridge.capture(form,context);
 }
 @Inject(method="render",at=@At(value="INVOKE",target="Lmchorse/bbs_mod/forms/renderers/FormRenderer;render3D(Lmchorse/bbs_mod/forms/renderers/FormRenderingContext;)V",shift=At.Shift.AFTER))
 private void bbsLight$after(FormRenderingContext context,CallbackInfo ci){EffectRenderContext.pop();}
 @Inject(method="renderUI",at=@At("HEAD"))
 private void bbsLight$ui(UIContext context,int x1,int y1,int x2,int y2,CallbackInfo ci){
  EffectRenderContext.push(form instanceof BlockForm?EffectState.block(form,null,false):EffectState.model(form,null,false,false));
 }
 @Inject(method="renderUI",at=@At("RETURN"))
 private void bbsLight$uiEnd(UIContext context,int x1,int y1,int x2,int y2,CallbackInfo ci){EffectRenderContext.pop();}
}
