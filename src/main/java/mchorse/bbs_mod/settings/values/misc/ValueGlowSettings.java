package mchorse.bbs_mod.settings.values.misc;

import dev.flanzomc.bbslightfixer.BbsLightKeyframeFactories;
import mchorse.bbs_mod.forms.forms.utils.GlowSettings;
import mchorse.bbs_mod.settings.values.base.BaseKeyframeFactoryValue;

public class ValueGlowSettings extends BaseKeyframeFactoryValue<GlowSettings>
{
    public ValueGlowSettings(String id, GlowSettings value)
    {
        super(id, BbsLightKeyframeFactories.GLOW_SETTINGS, value);
    }
}
