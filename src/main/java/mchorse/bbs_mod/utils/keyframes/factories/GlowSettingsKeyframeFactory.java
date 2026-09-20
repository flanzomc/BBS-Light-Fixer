package mchorse.bbs_mod.utils.keyframes.factories;

import mchorse.bbs_mod.data.types.BaseType;
import mchorse.bbs_mod.data.types.MapType;
import mchorse.bbs_mod.forms.forms.utils.EffectTransform;
import mchorse.bbs_mod.forms.forms.utils.GlowSettings;
import mchorse.bbs_mod.utils.MathUtils;
import mchorse.bbs_mod.utils.interps.IInterp;

public class GlowSettingsKeyframeFactory implements IKeyframeFactory<GlowSettings>
{
    private final GlowSettings i = new GlowSettings();

    @Override public GlowSettings fromData(BaseType data)
    {
        GlowSettings value = new GlowSettings();
        if (data.isMap()) value.fromData(data.asMap());
        return value;
    }

    @Override public BaseType toData(GlowSettings value)
    {
        return value == null ? new MapType() : value.toData();
    }

    @Override public GlowSettings createEmpty() { return new GlowSettings(); }
    @Override public GlowSettings copy(GlowSettings value) { return value == null ? null : value.copy(); }

    @Override
    public GlowSettings interpolate(GlowSettings preA, GlowSettings a, GlowSettings b, GlowSettings postB, IInterp interpolation, float x)
    {
        preA = normalize(preA); a = normalize(a); b = normalize(b); postB = normalize(postB);

        this.i.r = MathUtils.clamp((float) interpolation.interpolate(IInterp.context.set(preA.r, a.r, b.r, postB.r, x)), 0F, 1F);
        this.i.g = MathUtils.clamp((float) interpolation.interpolate(IInterp.context.set(preA.g, a.g, b.g, postB.g, x)), 0F, 1F);
        this.i.b = MathUtils.clamp((float) interpolation.interpolate(IInterp.context.set(preA.b, a.b, b.b, postB.b, x)), 0F, 1F);
        this.i.intensity = (float) interpolation.interpolate(IInterp.context.set(preA.intensity, a.intensity, b.intensity, postB.intensity, x));
        this.i.radius = (float) interpolation.interpolate(IInterp.context.set(preA.radius, a.radius, b.radius, postB.radius, x));
        this.i.spread = (float) interpolation.interpolate(IInterp.context.set(preA.spread, a.spread, b.spread, postB.spread, x));
        this.i.centerX = (float) interpolation.interpolate(IInterp.context.set(preA.centerX, a.centerX, b.centerX, postB.centerX, x));
        this.i.centerY = (float) interpolation.interpolate(IInterp.context.set(preA.centerY, a.centerY, b.centerY, postB.centerY, x));
        this.i.centerZ = (float) interpolation.interpolate(IInterp.context.set(preA.centerZ, a.centerZ, b.centerZ, postB.centerZ, x));
        this.i.width = (float) interpolation.interpolate(IInterp.context.set(preA.width, a.width, b.width, postB.width, x));
        this.i.height = (float) interpolation.interpolate(IInterp.context.set(preA.height, a.height, b.height, postB.height, x));
        this.i.sync = x >= 0.5F ? b.sync : a.sync;
        this.i.paintOnly = x >= 0.5F ? b.paintOnly : a.paintOnly;
        EffectTransformInterpolation.interpolate(this.i.transform, preA.transform, a.transform, b.transform, postB.transform, interpolation, x);
        return this.i;
    }

    private static GlowSettings normalize(GlowSettings value)
    {
        if (value == null) value = new GlowSettings();
        if (value.transform == null) value.transform = new EffectTransform();
        return value;
    }
}
