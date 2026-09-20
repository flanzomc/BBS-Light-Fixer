package dev.flanzomc.bbslightfixer.ui;

import dev.flanzomc.bbslightfixer.access.CmlColorAccess;
import dev.flanzomc.bbslightfixer.access.CmlFormAccess;
import mchorse.bbs_mod.forms.forms.BlockForm;
import mchorse.bbs_mod.forms.forms.Form;
import mchorse.bbs_mod.forms.forms.ModelForm;
import mchorse.bbs_mod.forms.forms.utils.EffectTransform;
import mchorse.bbs_mod.forms.forms.utils.GlowSettings;
import mchorse.bbs_mod.forms.forms.utils.PaintSettings;
import mchorse.bbs_mod.l10n.keys.IKey;
import mchorse.bbs_mod.settings.values.core.ValueColor;
import mchorse.bbs_mod.ui.framework.elements.UIElement;
import mchorse.bbs_mod.ui.framework.elements.UISection;
import mchorse.bbs_mod.ui.framework.elements.input.UIColor;
import mchorse.bbs_mod.ui.framework.elements.input.UITrackpad;
import mchorse.bbs_mod.ui.utils.UI;
import mchorse.bbs_mod.utils.colors.Color;

import java.util.function.Consumer;
import java.util.function.Supplier;

public final class CmlEffectPanel extends UIElement{
 private final Supplier<Form> form;
 private final UIColor glowColor,paintColor;
 private final UITrackpad glowIntensity,paintIntensity,brightness,contrast,saturation,hue;
 private final EffectTransformEditor colorTransform,glowTransform,paintTransform,brightnessTransform,contrastTransform,saturationTransform,hueTransform;

 public CmlEffectPanel(Supplier<Form> form){
  this.form=form;this.column(5).vertical().stretch();
  UISection extra=new UISection(IKey.constant("Color extras"));
  glowColor=new UIColor(c->editGlow(g->{Color v=Color.rgba(c);g.r=v.r;g.g=v.g;g.b=v.b;}));
  glowIntensity=num(v->editGlow(g->g.intensity=v));
  paintColor=new UIColor(c->editPaint(p->{Color v=Color.rgba(c);p.r=v.r;p.g=v.g;p.b=v.b;}));
  paintIntensity=num(v->editPaint(p->p.intensity=PaintSettings.clampIntensity(v)));
  extra.fields.add(UI.row(UI.column(UI.label(IKey.constant("Glowing color")),glowColor),UI.column(UI.label(IKey.constant("Glow intensity")),glowIntensity)));
  extra.fields.add(UI.row(UI.column(UI.label(IKey.constant("Paint color")),paintColor),UI.column(UI.label(IKey.constant("Paint intensity")),paintIntensity)));

  colorTransform=transform("Color Transform",x->((CmlColorAccess)(Object)main().get()).bbsLight$getColorTransform(),t->editMain(a->copy(t,a.bbsLight$getColorTransform())));
  glowTransform=transform("Glow Transform",x->((CmlFormAccess)(Object)current()).bbsLight$getGlowSettings().get().transform,t->editGlow(g->g.transform=t.copy()));
  paintTransform=transform("Paint Transform",x->((CmlFormAccess)(Object)current()).bbsLight$getPaintSettings().get().transform,t->editPaint(p->p.transform=t.copy()));
  extra.fields.add(colorTransform,glowTransform,paintTransform);

  UISection grade=new UISection(IKey.constant("Color grade"));
  brightness=num(v->editMain(a->a.bbsLight$setBrightness(v)));contrast=num(v->editMain(a->a.bbsLight$setContrast(v)));
  saturation=num(v->editMain(a->a.bbsLight$setSaturation(v)));hue=num(v->editMain(a->a.bbsLight$setHue(v)));
  grade.fields.add(UI.row(UI.column(UI.label(IKey.constant("Brightness")),brightness),UI.column(UI.label(IKey.constant("Contrast")),contrast)),
   UI.row(UI.column(UI.label(IKey.constant("Saturation")),saturation),UI.column(UI.label(IKey.constant("Hue")),hue)));
  brightnessTransform=transform("Brightness Transform",x->((CmlColorAccess)(Object)main().get()).bbsLight$getBrightnessTransform(),t->editMain(a->copy(t,a.bbsLight$getBrightnessTransform())));
  contrastTransform=transform("Contrast Transform",x->((CmlColorAccess)(Object)main().get()).bbsLight$getContrastTransform(),t->editMain(a->copy(t,a.bbsLight$getContrastTransform())));
  saturationTransform=transform("Saturation Transform",x->((CmlColorAccess)(Object)main().get()).bbsLight$getSaturationTransform(),t->editMain(a->copy(t,a.bbsLight$getSaturationTransform())));
  hueTransform=transform("Hue Transform",x->((CmlColorAccess)(Object)main().get()).bbsLight$getHueTransform(),t->editMain(a->copy(t,a.bbsLight$getHueTransform())));
  grade.fields.add(brightnessTransform,contrastTransform,saturationTransform,hueTransform);
  this.add(extra,grade);
 }

 private EffectTransformEditor transform(String label,Function1 getter,Consumer<EffectTransform> setter){
  EffectTransformEditor e=new EffectTransformEditor(()->getter.get(null),(t,notify)->setter.accept(t));
  UISection s=new UISection(IKey.constant(label));s.setExpanded(false);s.fields.add(e);this.add(s);return e;
 }
 private interface Function1{EffectTransform get(Void v);}
 private UITrackpad num(Consumer<Float> c){UITrackpad p=new UITrackpad(n->c.accept(n.floatValue())).increment(.05).values(.1,.05,.25);return p;}
 private Form current(){return form.get();}
 private ValueColor main(){Form f=current();return f instanceof ModelForm m?m.color:f instanceof BlockForm b?b.color:null;}
 private void editMain(Consumer<CmlColorAccess> edit){ValueColor v=main();if(v==null)return;Color c=v.get().copy();edit.accept((CmlColorAccess)(Object)c);v.set(c);}
 private void editGlow(Consumer<GlowSettings> e){CmlFormAccess a=(CmlFormAccess)(Object)current();GlowSettings g=a.bbsLight$getGlowSettings().get().copy();e.accept(g);a.bbsLight$getGlowSettings().set(g);}
 private void editPaint(Consumer<PaintSettings> e){CmlFormAccess a=(CmlFormAccess)(Object)current();PaintSettings p=a.bbsLight$getPaintSettings().get().copy();e.accept(p);a.bbsLight$getPaintSettings().set(p);}
 private static void copy(EffectTransform s,EffectTransform d){d.fromData(s.toData());}
 public void sync(){
  if(current()==null)return;CmlFormAccess a=(CmlFormAccess)(Object)current();
  GlowSettings g=a.bbsLight$getGlowSettings().get();PaintSettings p=a.bbsLight$getPaintSettings().get();
  Color gc=new Color(g.r,g.g,g.b,1F),pc=new Color(p.r,p.g,p.b,1F);glowColor.setColor(gc.getARGBColor());paintColor.setColor(pc.getARGBColor());
  glowIntensity.setValue(g.intensity);paintIntensity.setValue(p.intensity);
  CmlColorAccess c=(CmlColorAccess)(Object)main().get();brightness.setValue(c.bbsLight$getBrightness());contrast.setValue(c.bbsLight$getContrast());
  saturation.setValue(c.bbsLight$getSaturation());hue.setValue(c.bbsLight$getHue());
  colorTransform.sync();glowTransform.sync();paintTransform.sync();brightnessTransform.sync();contrastTransform.sync();saturationTransform.sync();hueTransform.sync();
 }
}
