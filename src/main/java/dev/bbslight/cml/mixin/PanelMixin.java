package dev.bbslight.cml.mixin;
import dev.bbslight.cml.EffectsPanel;
import mchorse.bbs_mod.forms.forms.Form;
import mchorse.bbs_mod.ui.forms.editors.panels.UIFormPanel;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(value=UIFormPanel.class,remap=false)
abstract class PanelMixin {
    @Unique private EffectsPanel bbsLight$panel;
    @Inject(method="startEdit",at=@At("RETURN"))
    private void edit(Form form,CallbackInfo ci) {
        if(bbsLight$panel!=null) bbsLight$panel.removeFromParent();
        String name=getClass().getSimpleName();
        if(!name.equals("UIModelFormPanel") && !name.equals("UIBlockFormPanel"))return;
        bbsLight$panel=new EffectsPanel(form);
        ((UIFormPanel<?>)(Object)this).options.add(bbsLight$panel);
    }
}
