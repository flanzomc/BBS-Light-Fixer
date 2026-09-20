package dev.flanzomc.bbslightfixer;

import mchorse.bbs_mod.forms.forms.Form;
import mchorse.bbs_mod.settings.values.core.ValueColor;
import mchorse.bbs_mod.settings.values.core.ValueGroup;
import mchorse.bbs_mod.settings.values.numeric.ValueBoolean;
import mchorse.bbs_mod.settings.values.numeric.ValueFloat;
import mchorse.bbs_mod.settings.values.numeric.ValueInt;
import mchorse.bbs_mod.utils.colors.Color;

/**
 * FS-side storage for the BBS-CML form effects.
 *
 * Mask fields intentionally mirror CML EffectTransform: offset, scale, rotation
 * (degrees), pivot and shape. Keeping these separate from BBS FS Transform avoids
 * the old port's radians/degrees mismatch.
 */
public final class CmlEffectValues extends ValueGroup
{
    public final ValueColor glowingColor = new ValueColor("glowing_color", Color.white());
    public final ValueFloat glowIntensity = new ValueFloat("glow_intensity", 0F);
    public final ValueBoolean glowSync = new ValueBoolean("glow_sync", false);
    public final ValueBoolean glowPaintOnly = new ValueBoolean("glow_paint_only", false);
    public final ValueFloat glowRadius = new ValueFloat("glow_radius", 0F);
    public final ValueFloat glowSpread = new ValueFloat("glow_spread", 0F, 0F, 1F);

    public final ValueColor paintColor = new ValueColor("paint_color", Color.white());
    public final ValueFloat paintIntensity = new ValueFloat("paint_intensity", 0F, -1F, 1F);
    public final ValueBoolean paintSync = new ValueBoolean("paint_sync", false);
    public final ValueFloat paintShaderShadow = new ValueFloat("paint_shader_shadow", 1F);

    public final ValueFloat brightness = new ValueFloat("brightness", 0F);
    public final ValueFloat contrast = new ValueFloat("contrast", 0F);
    public final ValueFloat saturation = new ValueFloat("saturation", 0F);
    public final ValueFloat hue = new ValueFloat("hue", 0F);

    public final ValueBoolean emitLight = new ValueBoolean("emit_light", false);
    public final ValueInt lightIntensity = new ValueInt("light_intensity", 15, 0, 15);
    public final ValueInt breaking = new ValueInt("breaking", 0, 0, 10);

    public final Mask colorMask = new Mask("color_transform");
    public final Mask glowMask = new Mask("glow_transform");
    public final Mask paintMask = new Mask("paint_transform");
    public final Mask brightnessMask = new Mask("brightness_transform");
    public final Mask contrastMask = new Mask("contrast_transform");
    public final Mask saturationMask = new Mask("saturation_transform");
    public final Mask hueMask = new Mask("hue_transform");

    public CmlEffectValues()
    {
        super("cml_effects");

        this.add(this.glowingColor);
        this.add(this.glowIntensity);
        this.add(this.glowSync);
        this.add(this.glowPaintOnly);
        this.add(this.glowRadius);
        this.add(this.glowSpread);

        this.add(this.paintColor);
        this.add(this.paintIntensity);
        this.add(this.paintSync);
        this.add(this.paintShaderShadow);

        this.add(this.brightness);
        this.add(this.contrast);
        this.add(this.saturation);
        this.add(this.hue);

        this.add(this.emitLight);
        this.add(this.lightIntensity);
        this.add(this.breaking);

        this.add(this.colorMask);
        this.add(this.glowMask);
        this.add(this.paintMask);
        this.add(this.brightnessMask);
        this.add(this.contrastMask);
        this.add(this.saturationMask);
        this.add(this.hueMask);
    }

    public static CmlEffectValues of(Form form)
    {
        return ((CmlEffectsOwner) form).bbsLight$cmlEffects();
    }

    public boolean active()
    {
        return Math.abs(this.glowIntensity.get()) >= 0.001F
            || Math.abs(this.paintIntensity.get()) >= 0.001F
            || Math.abs(this.brightness.get()) >= 0.001F
            || Math.abs(this.contrast.get()) >= 0.001F
            || Math.abs(this.saturation.get()) >= 0.001F
            || Math.abs(this.hue.get()) >= 0.001F
            || this.colorMask.active();
    }

    public boolean hasSpatialMask()
    {
        return this.colorMask.active()
            || this.glowMask.active()
            || this.paintMask.active()
            || this.brightnessMask.active()
            || this.contrastMask.active()
            || this.saturationMask.active()
            || this.hueMask.active();
    }

    public static final class Mask extends ValueGroup
    {
        private static final float EPSILON = 0.001F;

        public final ValueFloat offsetX = new ValueFloat("offsetX", 0F);
        public final ValueFloat offsetY = new ValueFloat("offsetY", 0F);
        public final ValueFloat offsetZ = new ValueFloat("offsetZ", 0F);
        public final ValueFloat scaleX = new ValueFloat("scaleX", 1F);
        public final ValueFloat scaleY = new ValueFloat("scaleY", 1F);
        public final ValueFloat scaleZ = new ValueFloat("scaleZ", 1F);
        public final ValueFloat rotateX = new ValueFloat("rotateX", 0F);
        public final ValueFloat rotateY = new ValueFloat("rotateY", 0F);
        public final ValueFloat rotateZ = new ValueFloat("rotateZ", 0F);
        public final ValueFloat pivotX = new ValueFloat("pivotX", 0F);
        public final ValueFloat pivotY = new ValueFloat("pivotY", 0F);
        public final ValueFloat pivotZ = new ValueFloat("pivotZ", 0F);
        public final ValueInt shape = new ValueInt("shape", 0, 0, 2);

        public Mask(String id)
        {
            super(id);

            this.add(this.offsetX);
            this.add(this.offsetY);
            this.add(this.offsetZ);
            this.add(this.scaleX);
            this.add(this.scaleY);
            this.add(this.scaleZ);
            this.add(this.rotateX);
            this.add(this.rotateY);
            this.add(this.rotateZ);
            this.add(this.pivotX);
            this.add(this.pivotY);
            this.add(this.pivotZ);
            this.add(this.shape);
        }

        public boolean active()
        {
            return this.shape.get() != 0
                || Math.abs(this.offsetX.get()) > EPSILON
                || Math.abs(this.offsetY.get()) > EPSILON
                || Math.abs(this.offsetZ.get()) > EPSILON
                || Math.abs(this.scaleX.get() - 1F) > EPSILON
                || Math.abs(this.scaleY.get() - 1F) > EPSILON
                || Math.abs(this.scaleZ.get() - 1F) > EPSILON
                || Math.abs(this.rotateX.get()) > EPSILON
                || Math.abs(this.rotateY.get()) > EPSILON
                || Math.abs(this.rotateZ.get()) > EPSILON
                || Math.abs(this.pivotX.get()) > EPSILON
                || Math.abs(this.pivotY.get()) > EPSILON
                || Math.abs(this.pivotZ.get()) > EPSILON;
        }
    }
}
