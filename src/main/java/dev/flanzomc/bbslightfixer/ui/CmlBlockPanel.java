package dev.flanzomc.bbslightfixer.ui;

import dev.flanzomc.bbslightfixer.access.CmlBlockAccess;
import mchorse.bbs_mod.forms.forms.BlockForm;
import mchorse.bbs_mod.forms.forms.utils.StructureLightSettings;
import mchorse.bbs_mod.l10n.keys.IKey;
import mchorse.bbs_mod.ui.framework.elements.UIElement;
import mchorse.bbs_mod.ui.framework.elements.UISection;
import mchorse.bbs_mod.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs_mod.ui.framework.elements.input.UITrackpad;
import mchorse.bbs_mod.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs_mod.ui.utils.UI;

import java.util.function.Supplier;

public final class CmlBlockPanel extends UIElement{
 private final Supplier<BlockForm> form;private final UIToggle light,cx,cy,cz;private final UITrackpad level,breaking,rx,ry,rz;private final UITextbox biome;
 public CmlBlockPanel(Supplier<BlockForm> form){
  this.form=form;this.column(5).vertical().stretch();UISection s=new UISection(IKey.constant("CML Block"));
  light=new UIToggle(IKey.constant("Light"),false,t->{StructureLightSettings x=a().bbsLight$getStructureLight().get().copy();x.enabled=t.getValue();a().bbsLight$getStructureLight().set(x);});
  level=intpad(0,15,n->{StructureLightSettings x=a().bbsLight$getStructureLight().get().copy();x.intensity=n;a().bbsLight$getStructureLight().set(x);});
  breaking=intpad(0,10,n->a().bbsLight$getBreaking().set(n));
  rx=intpad(1,64,n->a().bbsLight$getRepeatX().set(n));ry=intpad(1,64,n->a().bbsLight$getRepeatY().set(n));rz=intpad(1,64,n->a().bbsLight$getRepeatZ().set(n));
  cx=new UIToggle(IKey.constant("Center X"),false,t->a().bbsLight$getRepeatCenterX().set(t.getValue()));
  cy=new UIToggle(IKey.constant("Center Y"),false,t->a().bbsLight$getRepeatCenterY().set(t.getValue()));
  cz=new UIToggle(IKey.constant("Center Z"),false,t->a().bbsLight$getRepeatCenterZ().set(t.getValue()));
  biome=new UITextbox(v->a().bbsLight$getBiomeId().set(v));
  s.fields.add(UI.row(light,UI.column(UI.label(IKey.constant("Light intensity")),level)),UI.labelRow(IKey.constant("Breaking 0-10"),breaking),
   UI.labelRow(IKey.constant("Repeat X / Y / Z"),UI.row(rx,ry,rz)),UI.row(cx,cy,cz),UI.labelRow(IKey.constant("Biome ID"),biome));
  this.add(s);
 }
 private interface IntEdit{void set(int n);}
 private UITrackpad intpad(int min,int max,IntEdit e){return new UITrackpad(n->e.set(n.intValue())).integer().limit(min,max);}
 private CmlBlockAccess a(){return (CmlBlockAccess)(Object)form.get();}
 public void sync(){
  if(form.get()==null)return;CmlBlockAccess a=a();StructureLightSettings s=a.bbsLight$getStructureLight().get();
  light.setValue(s.enabled);level.setValue(s.intensity);breaking.setValue(a.bbsLight$getBreaking().get());
  rx.setValue(a.bbsLight$getRepeatX().get());ry.setValue(a.bbsLight$getRepeatY().get());rz.setValue(a.bbsLight$getRepeatZ().get());
  cx.setValue(a.bbsLight$getRepeatCenterX().get());cy.setValue(a.bbsLight$getRepeatCenterY().get());cz.setValue(a.bbsLight$getRepeatCenterZ().get());biome.setText(a.bbsLight$getBiomeId().get());
 }
}
