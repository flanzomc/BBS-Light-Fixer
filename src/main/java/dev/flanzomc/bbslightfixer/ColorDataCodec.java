package dev.flanzomc.bbslightfixer;

import mchorse.bbs_mod.data.types.BaseType;
import mchorse.bbs_mod.data.types.IntType;
import mchorse.bbs_mod.data.types.MapType;
import mchorse.bbs_mod.forms.forms.utils.EffectTransform;
import mchorse.bbs_mod.utils.MathUtils;
import mchorse.bbs_mod.utils.colors.Color;
import mchorse.bbs_mod.utils.colors.ColorAdjustments;
import mchorse.bbs_mod.utils.interps.IInterp;
import mchorse.bbs_mod.utils.keyframes.factories.EffectTransformInterpolation;

public final class ColorDataCodec
{
    private ColorDataCodec() {}

    public static Color fromData(BaseType data)
    {
        if (data != null && data.isNumeric())
        {
            return Color.rgba(data.asNumeric().intValue());
        }

        if (!(data instanceof MapType map))
        {
            return new Color();
        }

        Color color = Color.rgba(map.getInt("color"));
        CmlColorAccess access = (CmlColorAccess) color;

        if (map.has("blend_a"))
        {
            float intensity = MathUtils.clamp(map.getFloat("blend_a"), 0F, 1F);
            float opacity = MathUtils.clamp(color.a, 0F, 1F);
            color.r = 1F + (color.r - 1F) * intensity;
            color.g = 1F + (color.g - 1F) * intensity;
            color.b = 1F + (color.b - 1F) * intensity;
            color.a = opacity;
        }

        if (map.has("a")) color.a = map.getFloat("a");

        if (map.has("transform"))
        {
            EffectTransform transform = new EffectTransform();
            transform.fromData(map.get("transform"));
            access.bbsLight$setTransform(transform);
        }

        access.bbsLight$setBrightness(ColorAdjustments.clampBrightness(map.getFloat("brightness", 0F)));
        access.bbsLight$setContrast(ColorAdjustments.clampContrast(map.getFloat("contrast", 0F)));
        access.bbsLight$setHue(ColorAdjustments.clampHue(map.getFloat("hue", 0F)));
        access.bbsLight$setSaturation(ColorAdjustments.clampSaturation(map.getFloat("saturation", 0F)));

        readTransform(map, "brightnessTransform", access.bbsLight$getBrightnessTransform());
        readTransform(map, "contrastTransform", access.bbsLight$getContrastTransform());
        readTransform(map, "hueTransform", access.bbsLight$getHueTransform());
        readTransform(map, "saturationTransform", access.bbsLight$getSaturationTransform());

        boolean anyGradeMask = map.has("brightnessTransform") || map.has("contrastTransform")
            || map.has("hueTransform") || map.has("saturationTransform");

        if (!anyGradeMask
            && ColorAdjustments.isActive(access.bbsLight$getBrightness(), access.bbsLight$getContrast(), access.bbsLight$getHue(), access.bbsLight$getSaturation())
            && access.bbsLight$getTransform().isActive())
        {
            access.bbsLight$setBrightnessTransform(access.bbsLight$getTransform().copy());
            access.bbsLight$setContrastTransform(access.bbsLight$getTransform().copy());
            access.bbsLight$setHueTransform(access.bbsLight$getTransform().copy());
            access.bbsLight$setSaturationTransform(access.bbsLight$getTransform().copy());
        }

        return color;
    }

    public static BaseType toData(Color color)
    {
        CmlColorAccess access = (CmlColorAccess) color;
        boolean outOfByteAlpha = color.a < 0F || color.a > 1F;
        boolean extended = access.bbsLight$getTransform().isActive()
            || ColorAdjustments.isActive(access.bbsLight$getBrightness(), access.bbsLight$getContrast(), access.bbsLight$getHue(), access.bbsLight$getSaturation())
            || access.bbsLight$getBrightnessTransform().isActive()
            || access.bbsLight$getContrastTransform().isActive()
            || access.bbsLight$getHueTransform().isActive()
            || access.bbsLight$getSaturationTransform().isActive();

        if (!extended && !outOfByteAlpha)
        {
            return new IntType(color.getARGBColor());
        }

        MapType map = new MapType();

        if (outOfByteAlpha)
        {
            int r = (int) (MathUtils.clamp(color.r, 0F, 1F) * 255F);
            int g = (int) (MathUtils.clamp(color.g, 0F, 1F) * 255F);
            int b = (int) (MathUtils.clamp(color.b, 0F, 1F) * 255F);
            map.putInt("color", 0xFF000000 | (r << 16) | (g << 8) | b);
            map.putFloat("a", color.a);
        }
        else
        {
            map.putInt("color", color.getARGBColor());
        }

        if (access.bbsLight$getTransform().isActive()) map.put("transform", access.bbsLight$getTransform().toData());
        if (Math.abs(access.bbsLight$getBrightness()) > ColorAdjustments.EPSILON) map.putFloat("brightness", access.bbsLight$getBrightness());
        if (Math.abs(access.bbsLight$getContrast()) > ColorAdjustments.EPSILON) map.putFloat("contrast", access.bbsLight$getContrast());
        if (Math.abs(access.bbsLight$getHue()) > ColorAdjustments.EPSILON) map.putFloat("hue", access.bbsLight$getHue());
        if (Math.abs(access.bbsLight$getSaturation()) > ColorAdjustments.EPSILON) map.putFloat("saturation", access.bbsLight$getSaturation());

        writeTransform(map, "brightnessTransform", access.bbsLight$getBrightnessTransform());
        writeTransform(map, "contrastTransform", access.bbsLight$getContrastTransform());
        writeTransform(map, "hueTransform", access.bbsLight$getHueTransform());
        writeTransform(map, "saturationTransform", access.bbsLight$getSaturationTransform());

        return map;
    }

    public static void interpolateExtras(Color out, Color preA, Color a, Color b, Color postB, IInterp interpolation, float x)
    {
        CmlColorAccess o=(CmlColorAccess)out, p=(CmlColorAccess)preA, aa=(CmlColorAccess)a, bb=(CmlColorAccess)b, q=(CmlColorAccess)postB;

        o.bbsLight$setBrightness(ColorAdjustments.clampBrightness((float) interpolation.interpolate(IInterp.context.set(p.bbsLight$getBrightness(), aa.bbsLight$getBrightness(), bb.bbsLight$getBrightness(), q.bbsLight$getBrightness(), x))));
        o.bbsLight$setContrast(ColorAdjustments.clampContrast((float) interpolation.interpolate(IInterp.context.set(p.bbsLight$getContrast(), aa.bbsLight$getContrast(), bb.bbsLight$getContrast(), q.bbsLight$getContrast(), x))));
        o.bbsLight$setHue(ColorAdjustments.clampHue((float) interpolation.interpolate(IInterp.context.set(p.bbsLight$getHue(), aa.bbsLight$getHue(), bb.bbsLight$getHue(), q.bbsLight$getHue(), x))));
        o.bbsLight$setSaturation(ColorAdjustments.clampSaturation((float) interpolation.interpolate(IInterp.context.set(p.bbsLight$getSaturation(), aa.bbsLight$getSaturation(), bb.bbsLight$getSaturation(), q.bbsLight$getSaturation(), x))));

        EffectTransformInterpolation.interpolate(o.bbsLight$getTransform(), p.bbsLight$getTransform(), aa.bbsLight$getTransform(), bb.bbsLight$getTransform(), q.bbsLight$getTransform(), interpolation, x);
        EffectTransformInterpolation.interpolate(o.bbsLight$getBrightnessTransform(), p.bbsLight$getBrightnessTransform(), aa.bbsLight$getBrightnessTransform(), bb.bbsLight$getBrightnessTransform(), q.bbsLight$getBrightnessTransform(), interpolation, x);
        EffectTransformInterpolation.interpolate(o.bbsLight$getContrastTransform(), p.bbsLight$getContrastTransform(), aa.bbsLight$getContrastTransform(), bb.bbsLight$getContrastTransform(), q.bbsLight$getContrastTransform(), interpolation, x);
        EffectTransformInterpolation.interpolate(o.bbsLight$getHueTransform(), p.bbsLight$getHueTransform(), aa.bbsLight$getHueTransform(), bb.bbsLight$getHueTransform(), q.bbsLight$getHueTransform(), interpolation, x);
        EffectTransformInterpolation.interpolate(o.bbsLight$getSaturationTransform(), p.bbsLight$getSaturationTransform(), aa.bbsLight$getSaturationTransform(), bb.bbsLight$getSaturationTransform(), q.bbsLight$getSaturationTransform(), interpolation, x);
    }

    private static void readTransform(MapType map, String key, EffectTransform target)
    {
        if (map.has(key)) target.fromData(map.get(key));
    }

    private static void writeTransform(MapType map, String key, EffectTransform transform)
    {
        if (transform != null && transform.isActive()) map.put(key, transform.toData());
    }
}
