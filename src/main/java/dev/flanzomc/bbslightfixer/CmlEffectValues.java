package dev.flanzomc.bbslightfixer;

import mchorse.bbs_mod.forms.forms.Form;
import mchorse.bbs_mod.settings.values.core.ValueColor;
import mchorse.bbs_mod.settings.values.core.ValueGroup;
import mchorse.bbs_mod.settings.values.core.ValueTransform;
import mchorse.bbs_mod.settings.values.numeric.ValueBoolean;
import mchorse.bbs_mod.settings.values.numeric.ValueFloat;
import mchorse.bbs_mod.settings.values.numeric.ValueInt;
import mchorse.bbs_mod.utils.colors.Color;
import mchorse.bbs_mod.utils.pose.Transform;

public final class CmlEffectValues extends ValueGroup
{
    public final ValueColor glowingColor = new ValueColor("glowing_color", Color.white());
    public final ValueFloat glowIntensity = new ValueFloat("glow_intensity", 0F);
    public final ValueBoolean glowPaintOnly = new ValueBoolean("glow_paint_only", false);

    public final ValueColor paintColor = new ValueColor("paint_color", Color.white());
    public final ValueFloat paintIntensity = new ValueFloat("paint_intensity", 0F, -1F, 1F);

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
        this.add(this.glowPaintOnly);
        this.add(this.paintColor);
        this.add(this.paintIntensity);
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
        return this.glowIntensity.get() != 0F
            || this.paintIntensity.get() != 0F
            || this.brightness.get() != 0F
            || this.contrast.get() != 0F
            || this.saturation.get() != 0F
            || this.hue.get() != 0F
            || this.colorMask.active();
    }

    public static final class Mask extends ValueGroup
    {
        public final ValueInt shape = new ValueInt("shape", 0, 0, 2);
        public final ValueTransform transform = new ValueTransform("transform", new Transform());
        public final ValueFloat pivotX = new ValueFloat("pivot_x", 0F);
        public final ValueFloat pivotY = new ValueFloat("pivot_y", 0F);
        public final ValueFloat pivotZ = new ValueFloat("pivot_z", 0F);

        public Mask(String id)
        {
            super(id);

            this.add(this.shape);
            this.add(this.transform);
            this.add(this.pivotX);
            this.add(this.pivotY);
            this.add(this.pivotZ);
        }

        public boolean active()
        {
            Transform t = this.transform.get();

            return this.shape.get() != 0
                || this.pivotX.get() != 0F
                || this.pivotY.get() != 0F
                || this.pivotZ.get() != 0F
                || (t != null && !t.isDefault());
        }
    }
}
