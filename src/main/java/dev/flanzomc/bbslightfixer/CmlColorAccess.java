package dev.flanzomc.bbslightfixer;

import mchorse.bbs_mod.forms.forms.utils.EffectTransform;

public interface CmlColorAccess
{
    float bbsLight$getBrightness();
    void bbsLight$setBrightness(float value);
    float bbsLight$getContrast();
    void bbsLight$setContrast(float value);
    float bbsLight$getHue();
    void bbsLight$setHue(float value);
    float bbsLight$getSaturation();
    void bbsLight$setSaturation(float value);

    EffectTransform bbsLight$getTransform();
    void bbsLight$setTransform(EffectTransform value);
    EffectTransform bbsLight$getBrightnessTransform();
    void bbsLight$setBrightnessTransform(EffectTransform value);
    EffectTransform bbsLight$getContrastTransform();
    void bbsLight$setContrastTransform(EffectTransform value);
    EffectTransform bbsLight$getHueTransform();
    void bbsLight$setHueTransform(EffectTransform value);
    EffectTransform bbsLight$getSaturationTransform();
    void bbsLight$setSaturationTransform(EffectTransform value);
}
