package dev.bbslight.mixin;

import dev.bbslight.FormBridge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets="mchorse.bbs_mod.forms.renderers.FormRenderer",remap=false)
abstract class FormRendererMixin {
    @Inject(method="render(Lmchorse/bbs_mod/forms/renderers/FormRenderingContext;)V",
        at=@At(value="INVOKE",target="Lmchorse/bbs_mod/forms/renderers/FormRenderer;render3D(Lmchorse/bbs_mod/forms/renderers/FormRenderingContext;)V"),remap=false)
    private void bbsLight$prepare(@Coerce Object context,CallbackInfo ci) { FormBridge.prepare(this,context); }
}
