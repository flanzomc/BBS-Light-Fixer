package dev.flanzomc.bbslightfixer.ui;

import mchorse.bbs_mod.forms.forms.utils.EffectTransform;
import mchorse.bbs_mod.l10n.keys.IKey;
import mchorse.bbs_mod.ui.framework.elements.UIElement;
import mchorse.bbs_mod.ui.framework.elements.input.UITrackpad;
import mchorse.bbs_mod.ui.utils.UI;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class EffectTransformEditor extends UIElement{
 private final Supplier<EffectTransform> getter;
 private final BiConsumer<EffectTransform,Boolean> setter;
 private final UITrackpad tx,ty,tz,sx,sy,sz,rx,ry,rz,px,py,pz,shape;
 public EffectTransformEditor(Supplier<EffectTransform> getter,BiConsumer<EffectTransform,Boolean> setter){
  this.getter=getter;this.setter=setter;this.column(3).vertical().stretch();
  tx=pad(t->t.offsetX,(t,v)->t.offsetX=v);ty=pad(t->t.offsetY,(t,v)->t.offsetY=v);tz=pad(t->t.offsetZ,(t,v)->t.offsetZ=v);
  sx=pad(t->t.scaleX,(t,v)->t.scaleX=v);sy=pad(t->t.scaleY,(t,v)->t.scaleY=v);sz=pad(t->t.scaleZ,(t,v)->t.scaleZ=v);
  rx=pad(t->t.rotateX,(t,v)->t.rotateX=v);ry=pad(t->t.rotateY,(t,v)->t.rotateY=v);rz=pad(t->t.rotateZ,(t,v)->t.rotateZ=v);
  px=pad(t->t.pivotX,(t,v)->t.pivotX=v);py=pad(t->t.pivotY,(t,v)->t.pivotY=v);pz=pad(t->t.pivotZ,(t,v)->t.pivotZ=v);
  shape=new UITrackpad(n->{EffectTransform t=copy();t.shape=mchorse.bbs_mod.forms.forms.utils.PaintMaskShape.fromId(n.intValue());setter.accept(t,true);}).integer().limit(0,2);
  this.add(UI.labelRow(IKey.constant("Move"),UI.row(tx,ty,tz)),UI.labelRow(IKey.constant("Scale"),UI.row(sx,sy,sz)),
   UI.labelRow(IKey.constant("Rotate"),UI.row(rx,ry,rz)),UI.labelRow(IKey.constant("Pivot"),UI.row(px,py,pz)),
   UI.labelRow(IKey.constant("Shape  0 Box / 1 Circle / 2 Triangle"),shape));
 }
 private interface Write{void set(EffectTransform t,float v);}
 private UITrackpad pad(Function<EffectTransform,Float> read,Write write){
  UITrackpad p=new UITrackpad(n->{EffectTransform t=copy();write.set(t,n.floatValue());setter.accept(t,true);}).increment(.05).values(.1,.05,.25);return p;
 }
 private EffectTransform copy(){EffectTransform t=getter.get();return t==null?new EffectTransform():t.copy();}
 public void sync(){
  EffectTransform t=getter.get();if(t==null)t=new EffectTransform();
  tx.setValue(t.offsetX);ty.setValue(t.offsetY);tz.setValue(t.offsetZ);sx.setValue(t.scaleX);sy.setValue(t.scaleY);sz.setValue(t.scaleZ);
  rx.setValue(t.rotateX);ry.setValue(t.rotateY);rz.setValue(t.rotateZ);px.setValue(t.pivotX);py.setValue(t.pivotY);pz.setValue(t.pivotZ);shape.setValue(t.shape.id);
 }
}
