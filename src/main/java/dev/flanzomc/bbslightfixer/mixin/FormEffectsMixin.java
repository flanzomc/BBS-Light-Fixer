package dev.flanzomc.bbslightfixer.mixin;

import dev.flanzomc.bbslightfixer.CmlFormAccess;
import mchorse.bbs_mod.forms.forms.Form;
import mchorse.bbs_mod.forms.forms.utils.GlowSettings;
import mchorse.bbs_mod.forms.forms.utils.PaintSettings;
import mchorse.bbs_mod.settings.values.core.ValueColor;
import mchorse.bbs_mod.settings.values.misc.ValueGlowSettings;
import mchorse.bbs_mod.settings.values.misc.ValuePaintSettings;
import mchorse.bbs_mod.utils.colors.Color;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value=Form.class, remap=false)
abstract class FormEffectsMixin implements CmlFormAccess
{
    @Unique private ValueColor bbsLight$paintColor;
    @Unique private ValuePaintSettings bbsLight$paint;
    @Unique private ValueColor bbsLight$glowingColor;
    @Unique private ValueGlowSettings bbsLight$glow;

    @Inject(method="<init>", at=@At("RETURN"))
    private void bbsLight$addCmlValues(CallbackInfo ci)
    {
        Form self=(Form)(Object)this;

        this.bbsLight$paintColor=new ValueColor("paint_color", new Color().set(1F,1F,1F,0F));
        this.bbsLight$paint=new ValuePaintSettings("paint", new PaintSettings());
        this.bbsLight$glowingColor=new ValueColor("glowing_color", new Color().set(1F,1F,1F,1F));
        this.bbsLight$glow=new ValueGlowSettings("glow", new GlowSettings());

        self.add(this.bbsLight$paintColor);
        self.add(this.bbsLight$paint);
        self.add(this.bbsLight$glowingColor);
        self.add(this.bbsLight$glow);
    }

    public ValueColor bbsLight$paintColor(){return this.bbsLight$paintColor;}
    public ValuePaintSettings bbsLight$paint(){return this.bbsLight$paint;}
    public ValueColor bbsLight$glowingColor(){return this.bbsLight$glowingColor;}
    public ValueGlowSettings bbsLight$glow(){return this.bbsLight$glow;}
}
