package dev.flanzomc.bbslightfixer.value;

import mchorse.bbs_mod.data.types.BaseType;
import mchorse.bbs_mod.data.types.MapType;
import mchorse.bbs_mod.forms.forms.utils.PaintSettings;
import mchorse.bbs_mod.utils.MathUtils;
import mchorse.bbs_mod.utils.interps.IInterp;
import mchorse.bbs_mod.utils.keyframes.factories.EffectTransformInterpolation;
import mchorse.bbs_mod.utils.keyframes.factories.IKeyframeFactory;

public final class PaintSettingsFactory implements IKeyframeFactory<PaintSettings> {
    public PaintSettings fromData(BaseType data){PaintSettings v=new PaintSettings();if(data!=null&&data.isMap())v.fromData(data);return v;}
    public BaseType toData(PaintSettings value){return value==null?new MapType():value.toData();}
    public PaintSettings createEmpty(){return new PaintSettings();}
    public PaintSettings copy(PaintSettings value){return value==null?null:value.copy();}
    public PaintSettings interpolate(PaintSettings preA,PaintSettings a,PaintSettings b,PaintSettings postB,IInterp interpolation,float x){
        preA=or(preA);a=or(a);b=or(b);postB=or(postB);PaintSettings o=new PaintSettings();
        o.r=MathUtils.clamp(f(interpolation,preA.r,a.r,b.r,postB.r,x),0F,1F);
        o.g=MathUtils.clamp(f(interpolation,preA.g,a.g,b.g,postB.g,x),0F,1F);
        o.b=MathUtils.clamp(f(interpolation,preA.b,a.b,b.b,postB.b,x),0F,1F);
        o.intensity=PaintSettings.clampIntensity(f(interpolation,preA.intensity,a.intensity,b.intensity,postB.intensity,x));
        o.sync=x>=.5F?b.sync:a.sync;o.shaderShadow=PaintSettings.resolveAutoShaderShadow(o.intensity);
        EffectTransformInterpolation.interpolate(o.transform,preA.transform,a.transform,b.transform,postB.transform,interpolation,x);
        return o;
    }
    private static PaintSettings or(PaintSettings v){return v==null?new PaintSettings():v;}
    private static float f(IInterp i,float p,float a,float b,float q,float x){return (float)i.interpolate(IInterp.context.set(p,a,b,q,x));}
}
