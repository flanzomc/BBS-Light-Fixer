package dev.bbslight.cml;

import mchorse.bbs_mod.forms.forms.*;
import mchorse.bbs_mod.l10n.keys.IKey;
import mchorse.bbs_mod.ui.framework.elements.*;
import mchorse.bbs_mod.ui.framework.elements.buttons.*;
import mchorse.bbs_mod.ui.framework.elements.input.*;
import mchorse.bbs_mod.ui.utils.UI;
import mchorse.bbs_mod.ui.utils.icons.Icons;
import mchorse.bbs_mod.utils.colors.Color;
import mchorse.bbs_mod.settings.values.numeric.ValueFloat;

/** Native FS widgets exposing CML's color extras, grade channels and block controls. */
public final class EffectsPanel extends UIElement {
    public EffectsPanel(Form form) {
        EffectValues v=EffectValues.of(form);
        column(5).vertical().stretch();
        if(form instanceof BlockForm) {
            UIToggle enabled=new UIToggle(IKey.constant("Light"),v.emit.get(),t->v.emit.set(t.getValue()));
            UITrackpad level=new UITrackpad(n->v.level.set(n.intValue())).integer().limit(0,15);
            level.setValue(v.level.get());level.tooltip(IKey.constant("Light intensity (0–15)"));
            UITrackpad breaking=new UITrackpad(n->v.breaking.set(n.intValue())).integer().limit(0,10);
            breaking.setValue(v.breaking.get());breaking.tooltip(IKey.constant("Breaking stage (0 = none)"));
            add(UI.label(IKey.constant("Block")),UI.row(enabled,level),UI.labelRow(IKey.constant("Breaking"),breaking));
        }
        UISection extras=new UISection(IKey.constant("Color extras"));
        UIColor glow=new UIColor(c->v.glowColor.set(Color.rgba(c)));glow.setColor(v.glowColor.get().getARGBColor());
        UIColor paint=new UIColor(c->v.paintColor.set(Color.rgba(c)));paint.setColor(v.paintColor.get().getARGBColor());
        extras.fields.add(UI.row(UI.column(UI.label(IKey.constant("Glowing color")),masked(glow,v.masks[0])),
            UI.column(UI.label(IKey.constant("Glow intensity")),number(v.glow))));
        extras.fields.add(UI.row(UI.column(UI.label(IKey.constant("Paint color")),masked(paint,v.masks[1])),
            UI.column(UI.label(IKey.constant("Paint intensity")),number(v.paint))));
        UISection grade=new UISection(IKey.constant("Color grade"));
        grade.fields.add(UI.row(channel("Brightness",v.brightness,v.masks[2]),channel("Contrast",v.contrast,v.masks[3])),
            UI.row(channel("Saturation",v.saturation,v.masks[4]),channel("Hue",v.hue,v.masks[5])));
        add(extras,grade);
    }
    private UIElement channel(String label,ValueFloat value,EffectValues.Mask mask) {
        return UI.column(UI.label(IKey.constant(label)),masked(number(value),mask));
    }
    private UITrackpad number(ValueFloat value) {
        UITrackpad input=new UITrackpad(n->value.set(n.floatValue())).increment(.05).values(.1,.05,.25);
        input.setValue(value.get());return input;
    }
    private UIElement masked(UIElement control,EffectValues.Mask mask) {
        UIElement column=UI.column();
        UISection transform=new UISection(IKey.constant("Effect transform"));
        UIToggle active=new UIToggle(IKey.constant("Limit effect to shape"),mask.enabled.get(),t->mask.enabled.set(t.getValue()));
        UITrackpad shape=new UITrackpad(n->mask.shape.set(n.intValue())).integer().limit(0,2);
        shape.setValue(mask.shape.get());shape.tooltip(IKey.constant("0: box, 1: sphere, 2: triangle"));
        UISimpleTransform editor=new UISimpleTransform(()->{});editor.setValue(mask.transform);
        transform.fields.add(active,shape,editor);
        UIIcon button=new UIIcon(Icons.BLOCK,b->{if(transform.getParent()!=null)transform.removeFromParent();else column.add(transform);if(EffectsPanel.this.getParent()!=null)EffectsPanel.this.getParent().resize();else EffectsPanel.this.resize();});
        button.tooltip(IKey.constant("Effect transform"));
        column.add(UI.row(control,button));return column;
    }
}
