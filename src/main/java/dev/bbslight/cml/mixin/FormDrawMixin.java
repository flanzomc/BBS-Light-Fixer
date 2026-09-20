package dev.bbslight.cml.mixin;
import dev.bbslight.cml.*;
import mchorse.bbs_mod.forms.forms.Form;
import mchorse.bbs_mod.forms.renderers.*;
import mchorse.bbs_mod.ui.framework.UIContext;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(value=FormRenderer.class,remap=false)
abstract class FormDrawMixin {
    @Shadow protected Form form;
    @Inject(method="render",at=@At(value="INVOKE",target="Lmchorse/bbs_mod/forms/renderers/FormRenderer;render3D(Lmchorse/bbs_mod/forms/renderers/FormRenderingContext;)V"))
    private void before(FormRenderingContext context,CallbackInfo ci) {
        EffectScope.push(EffectScope.capture(form,context.stack,context.isPicking()));
        if(!context.ui && !context.isPicking()) PortLights.capture(form,context);
    }
    @Inject(method="render",at=@At(value="INVOKE",target="Lmchorse/bbs_mod/forms/renderers/FormRenderer;render3D(Lmchorse/bbs_mod/forms/renderers/FormRenderingContext;)V",shift=At.Shift.AFTER))
    private void after(FormRenderingContext context,CallbackInfo ci) { EffectScope.pop(); }
    @Inject(method="renderUI",at=@At("HEAD"))
    private void ui(UIContext context,int x1,int y1,int x2,int y2,CallbackInfo ci) { EffectScope.push(EffectScope.capture(form,null,false)); }
    @Inject(method="renderUI",at=@At("RETURN"))
    private void uiEnd(UIContext context,int x1,int y1,int x2,int y2,CallbackInfo ci) { EffectScope.pop(); }
}
