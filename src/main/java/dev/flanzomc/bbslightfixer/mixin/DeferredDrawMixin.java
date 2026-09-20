package dev.flanzomc.bbslightfixer.mixin;
import dev.flanzomc.bbslightfixer.render.DeferredStates;
import dev.flanzomc.bbslightfixer.render.EffectRenderContext;
import dev.flanzomc.bbslightfixer.render.EffectState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(targets={
 "mchorse.bbs_mod.forms.FormTranslucentQueue$ModelVAOCommand",
 "mchorse.bbs_mod.forms.FormTranslucentQueue$BOBJCommand",
 "mchorse.bbs_mod.forms.FormTranslucentQueue$RenderLayerCommand",
 "mchorse.bbs_mod.forms.FormTranslucentQueue$VertexBufferCommand"
},remap=false)
abstract class DeferredDrawMixin{
 @Inject(method="draw",at=@At("HEAD"))
 private void bbsLight$begin(CallbackInfo ci){EffectState s=DeferredStates.get(this);if(s!=null)EffectRenderContext.push(s);}
 @Inject(method="draw",at=@At("RETURN"))
 private void bbsLight$end(CallbackInfo ci){EffectState s=DeferredStates.get(this);if(s!=null){EffectRenderContext.pop();DeferredStates.forget(this);}}
}
