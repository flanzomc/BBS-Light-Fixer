package mchorse.bbs_mod.utils.keyframes.factories;

import mchorse.bbs_mod.data.types.BaseType;
import mchorse.bbs_mod.data.types.MapType;
import mchorse.bbs_mod.forms.forms.utils.PaintSettings;
import mchorse.bbs_mod.utils.MathUtils;
import mchorse.bbs_mod.utils.colors.Color;
import mchorse.bbs_mod.utils.colors.Colors;
import mchorse.bbs_mod.utils.interps.IInterp;

public class PaintSettingsKeyframeFactory implements IKeyframeFactory<PaintSettings>
{
    private final PaintSettings i = new PaintSettings();

    @Override public PaintSettings fromData(BaseType data)
    {
        PaintSettings value = new PaintSettings();
        if (data.isMap()) value.fromData(data.asMap());
        return value;
    }

    @Override public BaseType toData(PaintSettings value)
    {
        return value == null ? new MapType() : value.toData();
    }

    @Override public PaintSettings createEmpty() { return new PaintSettings(); }
    @Override public PaintSettings copy(PaintSettings value) { return value == null ? null : value.copy(); }

    @Override
    public PaintSettings interpolate(PaintSettings preA, PaintSettings a, PaintSettings b, PaintSettings postB, IInterp interpolation, float x)
    {
        preA = preA == null ? new PaintSettings() : preA;
        a = a == null ? new PaintSettings() : a;
        b = b == null ? new PaintSettings() : b;
        postB = postB == null ? new PaintSettings() : postB;

        this.i.r = MathUtils.clamp((float) interpolation.interpolate(IInterp.context.set(preA.r, a.r, b.r, postB.r, x)), 0F, 1F);
        this.i.g = MathUtils.clamp((float) interpolation.interpolate(IInterp.context.set(preA.g, a.g, b.g, postB.g, x)), 0F, 1F);
        this.i.b = MathUtils.clamp((float) interpolation.interpolate(IInterp.context.set(preA.b, a.b, b.b, postB.b, x)), 0F, 1F);
        this.i.intensity = PaintSettings.clampIntensity((float) interpolation.interpolate(IInterp.context.set(preA.intensity, a.intensity, b.intensity, postB.intensity, x)));
        this.i.sync = x >= 0.5F ? b.sync : a.sync;
        this.i.shaderShadow = PaintSettings.resolveAutoShaderShadow(this.i.intensity);
        EffectTransformInterpolation.interpolate(this.i.transform, preA.transform, a.transform, b.transform, postB.transform, interpolation, x);
        return this.i;
    }
}
