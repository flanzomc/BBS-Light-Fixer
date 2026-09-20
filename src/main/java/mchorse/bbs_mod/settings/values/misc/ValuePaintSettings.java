package mchorse.bbs_mod.settings.values.misc;

import dev.flanzomc.bbslightfixer.BbsLightKeyframeFactories;
import mchorse.bbs_mod.forms.forms.utils.PaintSettings;
import mchorse.bbs_mod.settings.values.base.BaseKeyframeFactoryValue;

public class ValuePaintSettings extends BaseKeyframeFactoryValue<PaintSettings>
{
    public ValuePaintSettings(String id, PaintSettings value)
    {
        super(id, BbsLightKeyframeFactories.PAINT_SETTINGS, value);
    }
}
