package dev.flanzomc.bbslightfixer.access;
import dev.flanzomc.bbslightfixer.value.ValueGlowSettings;
import dev.flanzomc.bbslightfixer.value.ValuePaintSettings;
import mchorse.bbs_mod.settings.values.core.ValueColor;
public interface CmlFormAccess{
 ValueColor bbsLight$getPaintColor();
 ValuePaintSettings bbsLight$getPaintSettings();
 ValueColor bbsLight$getGlowingColor();
 ValueGlowSettings bbsLight$getGlowSettings();
}
