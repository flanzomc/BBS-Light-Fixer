package dev.bbslight.cml.mixin;

import dev.bbslight.cml.DeferredEffectStates;
import dev.bbslight.cml.EffectScope;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
    targets = {
        "mchorse.bbs_mod.forms.FormTranslucentQueue$ModelVAOCommand",
        "mchorse.bbs_mod.forms.FormTranslucentQueue$BOBJCommand",
        "mchorse.bbs_mod.forms.FormTranslucentQueue$RenderLayerCommand",
        "mchorse.bbs_mod.forms.FormTranslucentQueue$VertexBufferCommand"
    },
    remap = false
)
abstract class DeferredEffectsMixin
{
    @Inject(method = "draw", at = @At("HEAD"))
    private void bbsLight$beforeDraw(CallbackInfo ci)
    {
        EffectScope.push(DeferredEffectStates.get(this));
    }

    @Inject(method = "draw", at = @At("RETURN"))
    private void bbsLight$afterDraw(CallbackInfo ci)
    {
        EffectScope.pop();
        DeferredEffectStates.forget(this);
    }
}
