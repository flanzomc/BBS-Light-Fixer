package dev.flanzomc.bbslightfixer;

import mchorse.bbs_mod.settings.values.core.ValueColor;
import mchorse.bbs_mod.settings.values.misc.ValueGlowSettings;
import mchorse.bbs_mod.settings.values.misc.ValuePaintSettings;

public interface CmlFormAccess
{
    ValueColor bbsLight$paintColor();
    ValuePaintSettings bbsLight$paint();
    ValueColor bbsLight$glowingColor();
    ValueGlowSettings bbsLight$glow();
}
