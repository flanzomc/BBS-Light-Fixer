package dev.flanzomc.bbslightfixer.value;

import mchorse.bbs_mod.data.types.BaseType;
import mchorse.bbs_mod.data.types.MapType;
import mchorse.bbs_mod.forms.forms.utils.GlowSettings;
import mchorse.bbs_mod.utils.MathUtils;
import mchorse.bbs_mod.utils.interps.IInterp;
import mchorse.bbs_mod.utils.keyframes.factories.IKeyframeFactory;

public final class GlowSettingsFactory implements IKeyframeFactory<GlowSettings> {
    public GlowSettings fromData(BaseType data) {
        GlowSettings v=new GlowSettings();
        if(data!=null && data.isMap()) v.fromData(data);
        return v;
    }
    public BaseType toData(GlowSettings value) { return value==null?new MapType():value.toData(); }
    public GlowSettings createEmpty(){ return new GlowSettings(); }
    public GlowSettings copy(GlowSettings value){ return value==null?null:value.copy(); }
    public GlowSettings interpolate(GlowSettings preA,GlowSettings a,GlowSettings b,GlowSettings postB,IInterp interpolation,float x){
        preA=or(preA);a=or(a);b=or(b);postB=or(postB);
        GlowSettings o=new GlowSettings();
        o.r=MathUtils.clamp(f(interpolation,preA.r,a.r,b.r,postB.r,x),0F,1F);
        o.g=MathUtils.clamp(f(interpolation,preA.g,a.g,b.g,postB.g,x),0F,1F);
        o.b=MathUtils.clamp(f(interpolation,preA.b,a.b,b.b,postB.b,x),0F,1F);
        o.intensity=f(interpolation,preA.intensity,a.intensity,b.intensity,postB.intensity,x);
        o.radius=f(interpolation,preA.radius,a.radius,b.radius,postB.radius,x);
        o.spread=f(interpolation,preA.spread,a.spread,b.spread,postB.spread,x);
        o.centerX=f(interpolation,preA.centerX,a.centerX,b.centerX,postB.centerX,x);
        o.centerY=f(interpolation,preA.centerY,a.centerY,b.centerY,postB.centerY,x);
        o.centerZ=f(interpolation,preA.centerZ,a.centerZ,b.centerZ,postB.centerZ,x);
        o.width=f(interpolation,preA.width,a.width,b.width,postB.width,x);
        o.height=f(interpolation,preA.height,a.height,b.height,postB.height,x);
        o.sync=x>=.5F?b.sync:a.sync;
        o.paintOnly=x>=.5F?b.paintOnly:a.paintOnly;
        mchorse.bbs_mod.utils.keyframes.factories.EffectTransformInterpolation.interpolate(o.transform,preA.transform,a.transform,b.transform,postB.transform,interpolation,x);
        return o;
    }
    private static GlowSettings or(GlowSettings v){return v==null?new GlowSettings():v;}
    private static float f(IInterp i,float p,float a,float b,float q,float x){return (float)i.interpolate(IInterp.context.set(p,a,b,q,x));}
}
