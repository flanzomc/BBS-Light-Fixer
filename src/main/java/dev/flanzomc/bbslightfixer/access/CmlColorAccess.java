package dev.flanzomc.bbslightfixer.access;
import mchorse.bbs_mod.forms.forms.utils.EffectTransform;
public interface CmlColorAccess{
 float bbsLight$getBrightness(); void bbsLight$setBrightness(float v);
 float bbsLight$getContrast(); void bbsLight$setContrast(float v);
 float bbsLight$getHue(); void bbsLight$setHue(float v);
 float bbsLight$getSaturation(); void bbsLight$setSaturation(float v);
 EffectTransform bbsLight$getColorTransform();
 EffectTransform bbsLight$getBrightnessTransform();
 EffectTransform bbsLight$getContrastTransform();
 EffectTransform bbsLight$getHueTransform();
 EffectTransform bbsLight$getSaturationTransform();
}
