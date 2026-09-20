package dev.flanzomc.bbslightfixer.mixin;

import dev.flanzomc.bbslightfixer.CmlEffectValues;
import dev.flanzomc.bbslightfixer.CmlEffectsOwner;
import mchorse.bbs_mod.forms.forms.Form;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Form.class, remap = false)
abstract class FormValuesMixin implements CmlEffectsOwner
{
    @Unique private CmlEffectValues bbsLight$cmlValues;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void bbsLight$initCmlValues(CallbackInfo ci)
    {
        this.bbsLight$cmlValues = new CmlEffectValues();
        ((Form) (Object) this).add(this.bbsLight$cmlValues);
    }

    @Override
    public CmlEffectValues bbsLight$cmlEffects()
    {
        return this.bbsLight$cmlValues;
    }
}
