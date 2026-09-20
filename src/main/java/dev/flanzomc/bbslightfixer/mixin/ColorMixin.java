package dev.flanzomc.bbslightfixer.mixin;

import dev.flanzomc.bbslightfixer.access.CmlColorAccess;
import mchorse.bbs_mod.forms.forms.utils.EffectTransform;
import mchorse.bbs_mod.utils.colors.Color;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(value=Color.class, remap=false)
public abstract class ColorMixin implements CmlColorAccess {
    @Unique private float bbsLight$brightness;
    @Unique private float bbsLight$contrast;
    @Unique private float bbsLight$hue;
    @Unique private float bbsLight$saturation;
    @Unique private EffectTransform bbsLight$transform = new EffectTransform();
    @Unique private EffectTransform bbsLight$brightnessTransform = new EffectTransform();
    @Unique private EffectTransform bbsLight$contrastTransform = new EffectTransform();
    @Unique private EffectTransform bbsLight$hueTransform = new EffectTransform();
    @Unique private EffectTransform bbsLight$saturationTransform = new EffectTransform();

    public float bbsLight$getBrightness(){return bbsLight$brightness;}
    public void bbsLight$setBrightness(float v){bbsLight$brightness=v;}
    public float bbsLight$getContrast(){return bbsLight$contrast;}
    public void bbsLight$setContrast(float v){bbsLight$contrast=v;}
    public float bbsLight$getHue(){return bbsLight$hue;}
    public void bbsLight$setHue(float v){bbsLight$hue=v;}
    public float bbsLight$getSaturation(){return bbsLight$saturation;}
    public void bbsLight$setSaturation(float v){bbsLight$saturation=v;}
    public EffectTransform bbsLight$getColorTransform(){return bbsLight$transform;}
    public EffectTransform bbsLight$getBrightnessTransform(){return bbsLight$brightnessTransform;}
    public EffectTransform bbsLight$getContrastTransform(){return bbsLight$contrastTransform;}
    public EffectTransform bbsLight$getHueTransform(){return bbsLight$hueTransform;}
    public EffectTransform bbsLight$getSaturationTransform(){return bbsLight$saturationTransform;}

    @Inject(method="copy(Lmchorse/bbs_mod/utils/colors/Color;)Lmchorse/bbs_mod/utils/colors/Color;", at=@At("RETURN"))
    private void bbsLight$copyExtras(Color source, CallbackInfoReturnable<Color> cir){
        CmlColorAccess s=(CmlColorAccess)(Object)source;
        this.bbsLight$brightness=s.bbsLight$getBrightness();
        this.bbsLight$contrast=s.bbsLight$getContrast();
        this.bbsLight$hue=s.bbsLight$getHue();
        this.bbsLight$saturation=s.bbsLight$getSaturation();
        this.bbsLight$transform=s.bbsLight$getColorTransform().copy();
        this.bbsLight$brightnessTransform=s.bbsLight$getBrightnessTransform().copy();
        this.bbsLight$contrastTransform=s.bbsLight$getContrastTransform().copy();
        this.bbsLight$hueTransform=s.bbsLight$getHueTransform().copy();
        this.bbsLight$saturationTransform=s.bbsLight$getSaturationTransform().copy();
    }

    @Inject(method="mul(Lmchorse/bbs_mod/utils/colors/Color;)V", at=@At("RETURN"))
    private void bbsLight$mulExtras(Color source, CallbackInfo ci){
        CmlColorAccess s=(CmlColorAccess)(Object)source;
        if(s.bbsLight$getColorTransform().isActive()) this.bbsLight$transform=s.bbsLight$getColorTransform().copy();
        if(Math.abs(s.bbsLight$getBrightness())>.001F || Math.abs(s.bbsLight$getContrast())>.001F ||
           Math.abs(s.bbsLight$getHue())>.001F || Math.abs(s.bbsLight$getSaturation())>.001F ||
           s.bbsLight$getBrightnessTransform().isActive() || s.bbsLight$getContrastTransform().isActive() ||
           s.bbsLight$getHueTransform().isActive() || s.bbsLight$getSaturationTransform().isActive()){
            this.bbsLight$brightness=s.bbsLight$getBrightness();
            this.bbsLight$contrast=s.bbsLight$getContrast();
            this.bbsLight$hue=s.bbsLight$getHue();
            this.bbsLight$saturation=s.bbsLight$getSaturation();
            this.bbsLight$brightnessTransform=s.bbsLight$getBrightnessTransform().copy();
            this.bbsLight$contrastTransform=s.bbsLight$getContrastTransform().copy();
            this.bbsLight$hueTransform=s.bbsLight$getHueTransform().copy();
            this.bbsLight$saturationTransform=s.bbsLight$getSaturationTransform().copy();
        }
    }

    @Inject(method="equals", at=@At("HEAD"), cancellable=true)
    private void bbsLight$equals(Object obj, CallbackInfoReturnable<Boolean> cir){
        if(!(obj instanceof Color c)) return;
        Color self=(Color)(Object)this;
        CmlColorAccess o=(CmlColorAccess)(Object)c;
        cir.setReturnValue(Float.compare(self.r,c.r)==0 && Float.compare(self.g,c.g)==0 &&
            Float.compare(self.b,c.b)==0 && Float.compare(self.a,c.a)==0 &&
            Float.compare(bbsLight$brightness,o.bbsLight$getBrightness())==0 &&
            Float.compare(bbsLight$contrast,o.bbsLight$getContrast())==0 &&
            Float.compare(bbsLight$hue,o.bbsLight$getHue())==0 &&
            Float.compare(bbsLight$saturation,o.bbsLight$getSaturation())==0 &&
            Objects.equals(bbsLight$transform,o.bbsLight$getColorTransform()) &&
            Objects.equals(bbsLight$brightnessTransform,o.bbsLight$getBrightnessTransform()) &&
            Objects.equals(bbsLight$contrastTransform,o.bbsLight$getContrastTransform()) &&
            Objects.equals(bbsLight$hueTransform,o.bbsLight$getHueTransform()) &&
            Objects.equals(bbsLight$saturationTransform,o.bbsLight$getSaturationTransform()));
    }
}
