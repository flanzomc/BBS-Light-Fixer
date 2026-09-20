package dev.flanzomc.bbslightfixer.mixin;

import dev.flanzomc.bbslightfixer.CmlDynamicLights;
import dev.flanzomc.bbslightfixer.CmlRenderState;
import mchorse.bbs_mod.forms.forms.Form;
import mchorse.bbs_mod.forms.renderers.FormRenderer;
import mchorse.bbs_mod.forms.renderers.FormRenderingContext;
import mchorse.bbs_mod.ui.framework.UIContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = FormRenderer.class, remap = false)
abstract class FormDrawMixin
{
    @Shadow protected Form form;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lmchorse/bbs_mod/forms/renderers/FormRenderer;render3D(Lmchorse/bbs_mod/forms/renderers/FormRenderingContext;)V"))
    private void bbsLight$beforeRender(FormRenderingContext context, CallbackInfo ci)
    {
        CmlRenderState.push(CmlRenderState.capture(this.form, context.isPicking()));
        if (!context.ui && !context.isPicking()) CmlDynamicLights.capture(this.form, context);
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lmchorse/bbs_mod/forms/renderers/FormRenderer;render3D(Lmchorse/bbs_mod/forms/renderers/FormRenderingContext;)V", shift = At.Shift.AFTER))
    private void bbsLight$afterRender(FormRenderingContext context, CallbackInfo ci)
    {
        CmlRenderState.pop();
    }

    @Inject(method = "renderUI", at = @At("HEAD"))
    private void bbsLight$uiStart(UIContext context, int x1, int y1, int x2, int y2, CallbackInfo ci)
    {
        CmlRenderState.push(CmlRenderState.capture(this.form, false));
    }

    @Inject(method = "renderUI", at = @At("RETURN"))
    private void bbsLight$uiEnd(UIContext context, int x1, int y1, int x2, int y2, CallbackInfo ci)
    {
        CmlRenderState.pop();
    }
}
