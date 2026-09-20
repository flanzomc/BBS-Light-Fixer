package mchorse.bbs_mod.settings.values.misc;

import dev.flanzomc.bbslightfixer.BbsLightKeyframeFactories;
import mchorse.bbs_mod.forms.forms.utils.StructureLightSettings;
import mchorse.bbs_mod.settings.values.base.BaseKeyframeFactoryValue;

public class ValueStructureLightSettings extends BaseKeyframeFactoryValue<StructureLightSettings>
{
    public ValueStructureLightSettings(String id, StructureLightSettings value)
    {
        super(id, BbsLightKeyframeFactories.STRUCTURE_LIGHT_SETTINGS, value);
    }
}
