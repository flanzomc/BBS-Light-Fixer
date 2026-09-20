package dev.flanzomc.bbslightfixer.mixin;

import dev.flanzomc.bbslightfixer.CmlColorAccess;
import mchorse.bbs_mod.forms.forms.utils.EffectTransform;
import mchorse.bbs_mod.utils.colors.Color;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value=Color.class, remap=false)
abstract class ColorEffectsMixin implements CmlColorAccess
{
    @Unique private float bbsLight$brightness;
    @Unique private float bbsLight$contrast;
    @Unique private float bbsLight$hue;
    @Unique private float bbsLight$saturation;
    @Unique private EffectTransform bbsLight$transform=new EffectTransform();
    @Unique private EffectTransform bbsLight$brightnessTransform=new EffectTransform();
    @Unique private EffectTransform bbsLight$contrastTransform=new EffectTransform();
    @Unique private EffectTransform bbsLight$hueTransform=new EffectTransform();
    @Unique private EffectTransform bbsLight$saturationTransform=new EffectTransform();

    @Inject(method="copy(Lmchorse/bbs_mod/utils/colors/Color;)Lmchorse/bbs_mod/utils/colors/Color;", at=@At("RETURN"))
    private void bbsLight$copyExtras(Color source, CallbackInfoReturnable<Color> cir)
    {
        if(!(source instanceof CmlColorAccess from)) return;
        this.bbsLight$brightness=from.bbsLight$getBrightness();
        this.bbsLight$contrast=from.bbsLight$getContrast();
        this.bbsLight$hue=from.bbsLight$getHue();
        this.bbsLight$saturation=from.bbsLight$getSaturation();
        this.bbsLight$transform=from.bbsLight$getTransform().copy();
        this.bbsLight$brightnessTransform=from.bbsLight$getBrightnessTransform().copy();
        this.bbsLight$contrastTransform=from.bbsLight$getContrastTransform().copy();
        this.bbsLight$hueTransform=from.bbsLight$getHueTransform().copy();
        this.bbsLight$saturationTransform=from.bbsLight$getSaturationTransform().copy();
    }

    public float bbsLight$getBrightness(){return this.bbsLight$brightness;}
    public void bbsLight$setBrightness(float v){this.bbsLight$brightness=v;}
    public float bbsLight$getContrast(){return this.bbsLight$contrast;}
    public void bbsLight$setContrast(float v){this.bbsLight$contrast=v;}
    public float bbsLight$getHue(){return this.bbsLight$hue;}
    public void bbsLight$setHue(float v){this.bbsLight$hue=v;}
    public float bbsLight$getSaturation(){return this.bbsLight$saturation;}
    public void bbsLight$setSaturation(float v){this.bbsLight$saturation=v;}
    public EffectTransform bbsLight$getTransform(){return this.bbsLight$transform;}
    public void bbsLight$setTransform(EffectTransform v){this.bbsLight$transform=v==null?new EffectTransform():v;}
    public EffectTransform bbsLight$getBrightnessTransform(){return this.bbsLight$brightnessTransform;}
    public void bbsLight$setBrightnessTransform(EffectTransform v){this.bbsLight$brightnessTransform=v==null?new EffectTransform():v;}
    public EffectTransform bbsLight$getContrastTransform(){return this.bbsLight$contrastTransform;}
    public void bbsLight$setContrastTransform(EffectTransform v){this.bbsLight$contrastTransform=v==null?new EffectTransform():v;}
    public EffectTransform bbsLight$getHueTransform(){return this.bbsLight$hueTransform;}
    public void bbsLight$setHueTransform(EffectTransform v){this.bbsLight$hueTransform=v==null?new EffectTransform():v;}
    public EffectTransform bbsLight$getSaturationTransform(){return this.bbsLight$saturationTransform;}
    public void bbsLight$setSaturationTransform(EffectTransform v){this.bbsLight$saturationTransform=v==null?new EffectTransform():v;}
}
