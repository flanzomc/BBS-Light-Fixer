package dev.flanzomc.bbslightfixer.mixin;

import dev.flanzomc.bbslightfixer.ColorDataCodec;
import mchorse.bbs_mod.data.types.BaseType;
import mchorse.bbs_mod.utils.colors.Color;
import mchorse.bbs_mod.utils.interps.IInterp;
import mchorse.bbs_mod.utils.keyframes.factories.ColorKeyframeFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value=ColorKeyframeFactory.class, remap=false)
abstract class ColorKeyframeFactoryMixin
{
    @Inject(method="fromData", at=@At("HEAD"), cancellable=true)
    private void bbsLight$fromData(BaseType data, CallbackInfoReturnable<Color> cir)
    {
        cir.setReturnValue(ColorDataCodec.fromData(data));
    }

    @Inject(method="toData", at=@At("HEAD"), cancellable=true)
    private void bbsLight$toData(Color value, CallbackInfoReturnable<BaseType> cir)
    {
        cir.setReturnValue(ColorDataCodec.toData(value));
    }

    @Inject(method="interpolate(Lmchorse/bbs_mod/utils/colors/Color;Lmchorse/bbs_mod/utils/colors/Color;Lmchorse/bbs_mod/utils/colors/Color;Lmchorse/bbs_mod/utils/colors/Color;Lmchorse/bbs_mod/utils/interps/IInterp;F)Lmchorse/bbs_mod/utils/colors/Color;", at=@At("RETURN"))
    private void bbsLight$interpolate(Color preA, Color a, Color b, Color postB, IInterp interpolation, float x, CallbackInfoReturnable<Color> cir)
    {
        ColorDataCodec.interpolateExtras(cir.getReturnValue(), preA, a, b, postB, interpolation, x);
    }
}
