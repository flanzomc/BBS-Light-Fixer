package dev.flanzomc.bbslightfixer.mixin;

import dev.flanzomc.bbslightfixer.access.CmlFormAccess;
import dev.flanzomc.bbslightfixer.value.ValueGlowSettings;
import dev.flanzomc.bbslightfixer.value.ValuePaintSettings;
import mchorse.bbs_mod.data.types.BaseType;
import mchorse.bbs_mod.data.types.MapType;
import mchorse.bbs_mod.forms.forms.Form;
import mchorse.bbs_mod.forms.forms.utils.GlowSettings;
import mchorse.bbs_mod.forms.forms.utils.PaintSettings;
import mchorse.bbs_mod.settings.values.base.BaseValue;
import mchorse.bbs_mod.settings.values.core.ValueColor;
import mchorse.bbs_mod.utils.colors.Color;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value=Form.class, remap=false)
abstract class FormMixin implements CmlFormAccess {
    @Shadow public abstract void add(BaseValue value);
    @Unique private ValueColor bbsLight$paintColor;
    @Unique private ValuePaintSettings bbsLight$paintSettings;
    @Unique private ValueColor bbsLight$glowingColor;
    @Unique private ValueGlowSettings bbsLight$glowSettings;

    @Inject(method="<init>",at=@At("TAIL"))
    private void bbsLight$init(CallbackInfo ci){
        bbsLight$paintColor=new ValueColor("paint_color",new Color().set(1F,1F,1F,0F));
        bbsLight$paintSettings=new ValuePaintSettings("paint",new PaintSettings());
        bbsLight$glowingColor=new ValueColor("glowing_color",new Color().set(1F,1F,1F,1F));
        bbsLight$glowSettings=new ValueGlowSettings("glow",new GlowSettings());
        add(bbsLight$paintColor);add(bbsLight$paintSettings);add(bbsLight$glowingColor);add(bbsLight$glowSettings);
    }

    @Inject(method="fromData",at=@At("RETURN"))
    private void bbsLight$migrate(BaseType data,CallbackInfo ci){
        if(!(data instanceof MapType map))return;
        if(!map.has("glow") && map.has("glowing_color")){
            Color legacy=bbsLight$glowingColor.get();GlowSettings s=bbsLight$glowSettings.get().copy();
            s.r=legacy.r;s.g=legacy.g;s.b=legacy.b;s.intensity=legacy.a==1F?0F:legacy.a;
            bbsLight$glowSettings.set(s);
        }
        if(!map.has("paint") && map.has("paint_color")){
            Color legacy=bbsLight$paintColor.get();PaintSettings s=bbsLight$paintSettings.get().copy();
            s.r=legacy.r;s.g=legacy.g;s.b=legacy.b;s.intensity=PaintSettings.resolveLegacyPaintIntensity(legacy);
            bbsLight$paintSettings.set(s);
        }
    }

    public ValueColor bbsLight$getPaintColor(){return bbsLight$paintColor;}
    public ValuePaintSettings bbsLight$getPaintSettings(){return bbsLight$paintSettings;}
    public ValueColor bbsLight$getGlowingColor(){return bbsLight$glowingColor;}
    public ValueGlowSettings bbsLight$getGlowSettings(){return bbsLight$glowSettings;}
}
