package dev.flanzomc.bbslightfixer.mixin;

import dev.flanzomc.bbslightfixer.access.CmlColorAccess;
import mchorse.bbs_mod.data.types.BaseType;
import mchorse.bbs_mod.data.types.IntType;
import mchorse.bbs_mod.data.types.MapType;
import mchorse.bbs_mod.forms.forms.utils.EffectTransform;
import mchorse.bbs_mod.utils.MathUtils;
import mchorse.bbs_mod.utils.colors.Color;
import mchorse.bbs_mod.utils.colors.ColorAdjustments;
import mchorse.bbs_mod.utils.interps.IInterp;
import mchorse.bbs_mod.utils.keyframes.factories.ColorKeyframeFactory;
import mchorse.bbs_mod.utils.keyframes.factories.EffectTransformInterpolation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value=ColorKeyframeFactory.class, remap=false)
abstract class ColorKeyframeFactoryMixin {
    @Inject(method="fromData",at=@At("HEAD"),cancellable=true)
    private void bbsLight$fromData(BaseType data, CallbackInfoReturnable<Color> cir){
        if(data instanceof IntType){cir.setReturnValue(Color.rgba(data.asNumeric().intValue()));return;}
        if(!(data instanceof MapType map)){cir.setReturnValue(new Color());return;}
        Color color=Color.rgba(map.getInt("color"));
        if(map.has("a")) color.a=map.getFloat("a");
        CmlColorAccess x=(CmlColorAccess)(Object)color;
        if(map.has("transform")) x.bbsLight$getColorTransform().fromData(map.get("transform"));
        x.bbsLight$setBrightness(ColorAdjustments.clampBrightness(map.getFloat("brightness",0F)));
        x.bbsLight$setContrast(ColorAdjustments.clampContrast(map.getFloat("contrast",0F)));
        x.bbsLight$setHue(ColorAdjustments.clampHue(map.getFloat("hue",0F)));
        x.bbsLight$setSaturation(ColorAdjustments.clampSaturation(map.getFloat("saturation",0F)));
        read(map,"brightnessTransform",x.bbsLight$getBrightnessTransform());
        read(map,"contrastTransform",x.bbsLight$getContrastTransform());
        read(map,"hueTransform",x.bbsLight$getHueTransform());
        read(map,"saturationTransform",x.bbsLight$getSaturationTransform());
        boolean gradeTransforms=map.has("brightnessTransform")||map.has("contrastTransform")||map.has("hueTransform")||map.has("saturationTransform");
        if(!gradeTransforms && activeGrade(x) && x.bbsLight$getColorTransform().isActive()){
            copy(x.bbsLight$getColorTransform(),x.bbsLight$getBrightnessTransform());
            copy(x.bbsLight$getColorTransform(),x.bbsLight$getContrastTransform());
            copy(x.bbsLight$getColorTransform(),x.bbsLight$getHueTransform());
            copy(x.bbsLight$getColorTransform(),x.bbsLight$getSaturationTransform());
        }
        cir.setReturnValue(color);
    }

    @Inject(method="toData",at=@At("HEAD"),cancellable=true)
    private void bbsLight$toData(Color color, CallbackInfoReturnable<BaseType> cir){
        CmlColorAccess x=(CmlColorAccess)(Object)color;
        boolean outA=color.a<0F||color.a>1F;
        boolean mapNeeded=outA||x.bbsLight$getColorTransform().isActive()||activeGrade(x)||
            x.bbsLight$getBrightnessTransform().isActive()||x.bbsLight$getContrastTransform().isActive()||
            x.bbsLight$getHueTransform().isActive()||x.bbsLight$getSaturationTransform().isActive();
        if(!mapNeeded){cir.setReturnValue(new IntType(color.getARGBColor()));return;}
        MapType map=new MapType();
        if(outA){
            int r=(int)(MathUtils.clamp(color.r,0F,1F)*255F),g=(int)(MathUtils.clamp(color.g,0F,1F)*255F),b=(int)(MathUtils.clamp(color.b,0F,1F)*255F);
            map.putInt("color",0xFF000000|(r<<16)|(g<<8)|b);map.putFloat("a",color.a);
        }else map.putInt("color",color.getARGBColor());
        if(x.bbsLight$getColorTransform().isActive()) map.put("transform",x.bbsLight$getColorTransform().toData());
        if(Math.abs(x.bbsLight$getBrightness())>.001F)map.putFloat("brightness",x.bbsLight$getBrightness());
        if(Math.abs(x.bbsLight$getContrast())>.001F)map.putFloat("contrast",x.bbsLight$getContrast());
        if(Math.abs(x.bbsLight$getHue())>.001F)map.putFloat("hue",x.bbsLight$getHue());
        if(Math.abs(x.bbsLight$getSaturation())>.001F)map.putFloat("saturation",x.bbsLight$getSaturation());
        write(map,"brightnessTransform",x.bbsLight$getBrightnessTransform());
        write(map,"contrastTransform",x.bbsLight$getContrastTransform());
        write(map,"hueTransform",x.bbsLight$getHueTransform());
        write(map,"saturationTransform",x.bbsLight$getSaturationTransform());
        cir.setReturnValue(map);
    }

    @Inject(method="createEmpty",at=@At("HEAD"),cancellable=true)
    private void bbsLight$empty(CallbackInfoReturnable<Color> cir){cir.setReturnValue(new Color(1F,1F,1F,1F));}

    @Inject(method="copy",at=@At("HEAD"),cancellable=true)
    private void bbsLight$copy(Color value, CallbackInfoReturnable<Color> cir){cir.setReturnValue(value.copy());}

    @Inject(method="interpolate(Lmchorse/bbs_mod/utils/colors/Color;Lmchorse/bbs_mod/utils/colors/Color;Lmchorse/bbs_mod/utils/colors/Color;Lmchorse/bbs_mod/utils/colors/Color;Lmchorse/bbs_mod/utils/interps/IInterp;F)Lmchorse/bbs_mod/utils/colors/Color;",at=@At("HEAD"),cancellable=true)
    private void bbsLight$interpolate(Color p,Color a,Color b,Color q,IInterp interp,float t,CallbackInfoReturnable<Color> cir){
        Color o=new Color();
        o.r=MathUtils.clamp(f(interp,p.r,a.r,b.r,q.r,t),0F,1F);
        o.g=MathUtils.clamp(f(interp,p.g,a.g,b.g,q.g,t),0F,1F);
        o.b=MathUtils.clamp(f(interp,p.b,a.b,b.b,q.b,t),0F,1F);
        o.a=MathUtils.clamp(f(interp,p.a,a.a,b.a,q.a,t),0F,1F);
        CmlColorAccess po=(CmlColorAccess)(Object)p,ao=(CmlColorAccess)(Object)a,bo=(CmlColorAccess)(Object)b,qo=(CmlColorAccess)(Object)q,oo=(CmlColorAccess)(Object)o;
        oo.bbsLight$setBrightness(ColorAdjustments.clampBrightness(f(interp,po.bbsLight$getBrightness(),ao.bbsLight$getBrightness(),bo.bbsLight$getBrightness(),qo.bbsLight$getBrightness(),t)));
        oo.bbsLight$setContrast(ColorAdjustments.clampContrast(f(interp,po.bbsLight$getContrast(),ao.bbsLight$getContrast(),bo.bbsLight$getContrast(),qo.bbsLight$getContrast(),t)));
        oo.bbsLight$setHue(ColorAdjustments.clampHue(f(interp,po.bbsLight$getHue(),ao.bbsLight$getHue(),bo.bbsLight$getHue(),qo.bbsLight$getHue(),t)));
        oo.bbsLight$setSaturation(ColorAdjustments.clampSaturation(f(interp,po.bbsLight$getSaturation(),ao.bbsLight$getSaturation(),bo.bbsLight$getSaturation(),qo.bbsLight$getSaturation(),t)));
        EffectTransformInterpolation.interpolate(oo.bbsLight$getColorTransform(),po.bbsLight$getColorTransform(),ao.bbsLight$getColorTransform(),bo.bbsLight$getColorTransform(),qo.bbsLight$getColorTransform(),interp,t);
        EffectTransformInterpolation.interpolate(oo.bbsLight$getBrightnessTransform(),po.bbsLight$getBrightnessTransform(),ao.bbsLight$getBrightnessTransform(),bo.bbsLight$getBrightnessTransform(),qo.bbsLight$getBrightnessTransform(),interp,t);
        EffectTransformInterpolation.interpolate(oo.bbsLight$getContrastTransform(),po.bbsLight$getContrastTransform(),ao.bbsLight$getContrastTransform(),bo.bbsLight$getContrastTransform(),qo.bbsLight$getContrastTransform(),interp,t);
        EffectTransformInterpolation.interpolate(oo.bbsLight$getHueTransform(),po.bbsLight$getHueTransform(),ao.bbsLight$getHueTransform(),bo.bbsLight$getHueTransform(),qo.bbsLight$getHueTransform(),interp,t);
        EffectTransformInterpolation.interpolate(oo.bbsLight$getSaturationTransform(),po.bbsLight$getSaturationTransform(),ao.bbsLight$getSaturationTransform(),bo.bbsLight$getSaturationTransform(),qo.bbsLight$getSaturationTransform(),interp,t);
        cir.setReturnValue(o);
    }
    private static boolean activeGrade(CmlColorAccess x){return Math.abs(x.bbsLight$getBrightness())>.001F||Math.abs(x.bbsLight$getContrast())>.001F||Math.abs(x.bbsLight$getHue())>.001F||Math.abs(x.bbsLight$getSaturation())>.001F;}
    private static float f(IInterp i,float p,float a,float b,float q,float x){return (float)i.interpolate(IInterp.context.set(p,a,b,q,x));}
    private static void read(MapType map,String key,EffectTransform t){if(map.has(key))t.fromData(map.get(key));}
    private static void write(MapType map,String key,EffectTransform t){if(t!=null&&t.isActive())map.put(key,t.toData());}
    private static void copy(EffectTransform s,EffectTransform d){d.fromData(s.toData());}
}
