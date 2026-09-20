package dev.bbslight.cml.mixin;
import dev.bbslight.cml.*;
import mchorse.bbs_mod.forms.forms.Form;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(value=Form.class,remap=false)
abstract class FormDataMixin implements EffectOwner {
    @Unique private EffectValues bbsLight$values;
    @Inject(method="<init>",at=@At("RETURN"))
    private void init(CallbackInfo ci) { bbsLight$values=new EffectValues();((Form)(Object)this).add(bbsLight$values); }
    public EffectValues bbsLight$effects() { return bbsLight$values; }
}
