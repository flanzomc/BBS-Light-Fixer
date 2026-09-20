package dev.flanzomc.bbslightfixer.value;
import dev.flanzomc.bbslightfixer.CmlFactories;
import mchorse.bbs_mod.forms.forms.utils.PaintSettings;
import mchorse.bbs_mod.settings.values.base.BaseKeyframeFactoryValue;
public final class ValuePaintSettings extends BaseKeyframeFactoryValue<PaintSettings>{
 public ValuePaintSettings(String id,PaintSettings v){super(id,CmlFactories.PAINT,v);}
}
