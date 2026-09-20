package dev.flanzomc.bbslightfixer.mixin;
import dev.flanzomc.bbslightfixer.ui.CmlBlockPanel;
import dev.flanzomc.bbslightfixer.ui.CmlEffectPanel;
import mchorse.bbs_mod.forms.forms.BlockForm;
import mchorse.bbs_mod.ui.forms.editors.forms.UIForm;
import mchorse.bbs_mod.ui.forms.editors.panels.UIBlockFormPanel;
import mchorse.bbs_mod.ui.forms.editors.panels.UIFormPanel;
import mchorse.bbs_mod.ui.framework.elements.input.UIColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(value=UIBlockFormPanel.class,remap=false)
abstract class UIBlockFormPanelMixin extends UIFormPanel<BlockForm>{
 @Shadow public UIColor color;@Unique private CmlEffectPanel bbsLight$effects;@Unique private CmlBlockPanel bbsLight$block;
 private UIBlockFormPanelMixin(UIForm e){super(e);}
 @Inject(method="<init>",at=@At("RETURN"))
 private void bbsLight$init(UIForm e,CallbackInfo ci){bbsLight$effects=new CmlEffectPanel(()->this.form);bbsLight$block=new CmlBlockPanel(()->this.form);this.options.addAfter(this.color,bbsLight$effects);this.options.addAfter(bbsLight$effects,bbsLight$block);}
 @Inject(method="startEdit",at=@At("RETURN"))
 private void bbsLight$sync(BlockForm form,CallbackInfo ci){bbsLight$effects.sync();bbsLight$block.sync();}
}
