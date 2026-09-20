package dev.flanzomc.bbslightfixer;

import mchorse.bbs_mod.forms.forms.BlockForm;
import mchorse.bbs_mod.forms.forms.Form;
import mchorse.bbs_mod.l10n.keys.IKey;
import mchorse.bbs_mod.settings.values.numeric.ValueFloat;
import mchorse.bbs_mod.ui.framework.elements.UIElement;
import mchorse.bbs_mod.ui.framework.elements.UISection;
import mchorse.bbs_mod.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs_mod.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs_mod.ui.framework.elements.input.UIColor;
import mchorse.bbs_mod.ui.framework.elements.input.UISimpleTransform;
import mchorse.bbs_mod.ui.framework.elements.input.UITrackpad;
import mchorse.bbs_mod.ui.utils.UI;
import mchorse.bbs_mod.ui.utils.icons.Icons;
import mchorse.bbs_mod.utils.colors.Color;

public final class CmlEffectsPanel extends UIElement
{
    private final CmlEffectValues values;

    public CmlEffectsPanel(Form form)
    {
        this.values = CmlEffectValues.of(form);
        this.column(5).vertical().stretch();

        UISection shading = new UISection(IKey.constant("CML Color / Glow"));

        UIColor glowColor = new UIColor(color ->
        {
            Color picked = Color.rgba(color);
            this.values.glowingColor.set(new Color(picked.r, picked.g, picked.b, 1F));
        });
        glowColor.setColor(this.values.glowingColor.get().getRGBColor());

        UITrackpad glow = number(this.values.glowIntensity, false);

        UIColor paintColor = new UIColor(color ->
        {
            Color picked = Color.rgba(color);
            this.values.paintColor.set(new Color(picked.r, picked.g, picked.b, 1F));
        });
        paintColor.setColor(this.values.paintColor.get().getRGBColor());

        UITrackpad paint = number(this.values.paintIntensity, false).limit(-1D, 1D);

        UIToggle paintOnly = new UIToggle(
            IKey.constant("Glow only on Paint"),
            this.values.glowPaintOnly.get(),
            toggle -> this.values.glowPaintOnly.set(toggle.getValue())
        );

        shading.fields.add(
            UI.row(
                UI.column(UI.label(IKey.constant("Glowing color")), transform(glowColor, this.values.glowMask)),
                UI.column(UI.label(IKey.constant("Glow intensity")), glow)
            ),
            UI.row(
                UI.column(UI.label(IKey.constant("Paint color")), transform(paintColor, this.values.paintMask)),
                UI.column(UI.label(IKey.constant("Paint intensity")), paint)
            ),
            paintOnly
        );

        UISection grade = new UISection(IKey.constant("Color grade"));
        grade.fields.add(
            UI.row(
                gradeChannel("Brightness", this.values.brightness, this.values.brightnessMask, false),
                gradeChannel("Contrast", this.values.contrast, this.values.contrastMask, false)
            ),
            UI.row(
                gradeChannel("Saturation", this.values.saturation, this.values.saturationMask, false),
                gradeChannel("Hue", this.values.hue, this.values.hueMask, true)
            )
        );

        this.add(shading, grade);

        if (form instanceof BlockForm)
        {
            UISection block = new UISection(IKey.constant("Block"));
            UIToggle light = new UIToggle(IKey.constant("Light"), this.values.emitLight.get(), toggle -> this.values.emitLight.set(toggle.getValue()));

            UITrackpad intensity = new UITrackpad(value -> this.values.lightIntensity.set(value.intValue())).integer().limit(0D, 15D);
            intensity.setValue(this.values.lightIntensity.get());

            UITrackpad breaking = new UITrackpad(value -> this.values.breaking.set(value.intValue())).integer().limit(0D, 10D);
            breaking.setValue(this.values.breaking.get());

            block.fields.add(UI.row(light, intensity), UI.labelRow(IKey.constant("Breaking"), breaking));
            this.add(block);
        }
    }

    private UIElement gradeChannel(String label, ValueFloat value, CmlEffectValues.Mask mask, boolean hue)
    {
        return UI.column(UI.label(IKey.constant(label)), transform(number(value, hue), mask));
    }

    private UITrackpad number(ValueFloat value, boolean hue)
    {
        UITrackpad input = new UITrackpad(number -> value.set(number.floatValue()));

        if (hue) input.increment(1D).values(5D, 1D, 15D);
        else input.increment(0.05D).values(0.1D, 0.05D, 0.25D);

        input.setValue(value.get());
        return input;
    }

    private UIElement transform(UIElement field, CmlEffectValues.Mask mask)
    {
        UIElement host = UI.column();
        UISection section = new UISection(IKey.constant("Transform"));

        UITrackpad shape = new UITrackpad(value -> mask.shape.set(value.intValue())).integer().limit(0D, 2D);
        shape.setValue(mask.shape.get());
        shape.tooltip(IKey.constant("0 Box, 1 Circle, 2 Triangle"));

        UISimpleTransform transform = new UISimpleTransform(() -> {});
        transform.setValue(mask.transform);

        UITrackpad pivotX = new UITrackpad(value -> mask.pivotX.set(value.floatValue()));
        UITrackpad pivotY = new UITrackpad(value -> mask.pivotY.set(value.floatValue()));
        UITrackpad pivotZ = new UITrackpad(value -> mask.pivotZ.set(value.floatValue()));

        pivotX.setValue(mask.pivotX.get());
        pivotY.setValue(mask.pivotY.get());
        pivotZ.setValue(mask.pivotZ.get());

        section.fields.add(
            UI.labelRow(IKey.constant("Shape"), shape),
            transform,
            UI.label(IKey.constant("Pivot")),
            UI.row(pivotX, pivotY, pivotZ)
        );

        UIIcon button = new UIIcon(Icons.SCALE, ignored ->
        {
            if (section.getParent() != null) section.removeFromParent();
            else host.add(section);

            CmlEffectsPanel.this.resize();
        });

        button.tooltip(IKey.constant("Transform"));
        host.add(UI.row(field, button));
        return host;
    }
}
