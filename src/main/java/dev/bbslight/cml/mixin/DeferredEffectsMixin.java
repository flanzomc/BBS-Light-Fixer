package dev.bbslight.cml.mixin;
import dev.bbslight.cml.EffectScope;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(targets={"mchorse.bbs_mod.forms.FormTranslucentQueue$ModelVAOCommand","mchorse.bbs_mod.forms.FormTranslucentQueue$BOBJCommand","mchorse.bbs_mod.forms.FormTranslucentQueue$RenderLayerCommand","mchorse.bbs_mod.forms.FormTranslucentQueue$VertexBufferCommand"},remap=false)
abstract class DeferredEffectsMixin {
    @Unique private EffectScope.State bbsLight$state;
    @Inject(method="<init>",at=@At("RETURN"))
    private void capture(CallbackInfo ci) { bbsLight$state=EffectScope.current(); }
    @Inject(method="draw",at=@At("HEAD"))
    private void before(CallbackInfo ci) { EffectScope.push(bbsLight$state); }
    @Inject(method="draw",at=@At("RETURN"))
    private void after(CallbackInfo ci) { EffectScope.pop(); }
}
