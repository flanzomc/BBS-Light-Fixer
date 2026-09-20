package dev.bbslight.cml;

import mchorse.bbs_mod.forms.forms.Form;
import mchorse.bbs_mod.settings.values.core.*;
import mchorse.bbs_mod.settings.values.numeric.*;
import mchorse.bbs_mod.utils.colors.Color;
import mchorse.bbs_mod.utils.pose.Transform;

/** Native BBS values participate in form copying, serialization and animation tracks. */
public final class EffectValues extends ValueGroup {
    public final ValueColor glowColor=new ValueColor("glowing_color",Color.white());
    public final ValueFloat glow=new ValueFloat("glow_intensity",0F);
    public final ValueColor paintColor=new ValueColor("paint_color",Color.white());
    public final ValueFloat paint=new ValueFloat("paint_intensity",0F);
    public final ValueFloat brightness=new ValueFloat("brightness",0F);
    public final ValueFloat contrast=new ValueFloat("contrast",0F);
    public final ValueFloat saturation=new ValueFloat("saturation",0F);
    public final ValueFloat hue=new ValueFloat("hue",0F);
    public final ValueBoolean emit=new ValueBoolean("emit_light",false);
    public final ValueInt level=new ValueInt("light_intensity",15,0,15);
    public final ValueInt breaking=new ValueInt("breaking",0,0,10);
    public final Mask[] masks=new Mask[6];
    public EffectValues() {
        super("cml_effects");
        add(glowColor);add(glow);add(paintColor);add(paint);
        add(brightness);add(contrast);add(saturation);add(hue);add(emit);add(level);add(breaking);
        String[] names={"glow","paint","brightness","contrast","saturation","hue"};
        for(int i=0;i<names.length;i++) { masks[i]=new Mask(names[i]+"_transform");add(masks[i]); }
    }
    public static EffectValues of(Form form) { return ((EffectOwner)form).bbsLight$effects(); }
    public boolean active() {
        return glow.get()!=0 || paint.get()!=0 || brightness.get()!=0 || contrast.get()!=0 || saturation.get()!=0 || hue.get()!=0;
    }
    public static final class Mask extends ValueGroup {
        public final ValueBoolean enabled=new ValueBoolean("enabled",false);
        public final ValueInt shape=new ValueInt("shape",0,0,2);
        public final ValueTransform transform=new ValueTransform("transform",new Transform());
        public Mask(String id) { super(id);add(enabled);add(shape);add(transform); }
    }
}
