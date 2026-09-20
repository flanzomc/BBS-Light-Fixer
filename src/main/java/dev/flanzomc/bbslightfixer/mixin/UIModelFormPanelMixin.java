package dev.flanzomc.bbslightfixer.mixin;
import dev.flanzomc.bbslightfixer.ui.CmlEffectPanel;
import mchorse.bbs_mod.forms.forms.ModelForm;
import mchorse.bbs_mod.ui.forms.editors.forms.UIForm;
import mchorse.bbs_mod.ui.forms.editors.panels.UIFormPanel;
import mchorse.bbs_mod.ui.forms.editors.panels.UIModelFormPanel;
import mchorse.bbs_mod.ui.framework.elements.input.UIColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(value=UIModelFormPanel.class,remap=false)
abstract class UIModelFormPanelMixin extends UIFormPanel<ModelForm>{
 @Shadow public UIColor color;@Unique private CmlEffectPanel bbsLight$effects;
 private UIModelFormPanelMixin(UIForm e){super(e);}
 @Inject(method="<init>",at=@At("RETURN"))
 private void bbsLight$init(UIForm e,CallbackInfo ci){bbsLight$effects=new CmlEffectPanel(()->this.form);this.options.addAfter(this.color,bbsLight$effects);}
 @Inject(method="startEdit",at=@At("RETURN"))
 private void bbsLight$sync(ModelForm form,CallbackInfo ci){bbsLight$effects.sync();}
}
