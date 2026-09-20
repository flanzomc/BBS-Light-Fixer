package dev.flanzomc.bbslightfixer.mixin;

import dev.flanzomc.bbslightfixer.CmlEffectsPanel;
import mchorse.bbs_mod.forms.forms.Form;
import mchorse.bbs_mod.ui.forms.editors.panels.UIFormPanel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = UIFormPanel.class, remap = false)
abstract class PanelMixin
{
    @Unique private CmlEffectsPanel bbsLight$cmlPanel;

    @Inject(method = "startEdit", at = @At("RETURN"))
    private void bbsLight$attachCmlPanel(Form form, CallbackInfo ci)
    {
        if (this.bbsLight$cmlPanel != null) this.bbsLight$cmlPanel.removeFromParent();

        String name = this.getClass().getSimpleName();
        if (!name.equals("UIModelFormPanel") && !name.equals("UIBlockFormPanel")) return;

        this.bbsLight$cmlPanel = new CmlEffectsPanel(form);
        ((UIFormPanel<?>) (Object) this).options.add(this.bbsLight$cmlPanel);
        ((UIFormPanel<?>) (Object) this).options.resize();
    }
}
