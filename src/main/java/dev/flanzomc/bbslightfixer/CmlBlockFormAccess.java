package dev.flanzomc.bbslightfixer;

import mchorse.bbs_mod.settings.values.misc.ValueStructureLightSettings;
import mchorse.bbs_mod.settings.values.numeric.ValueBoolean;
import mchorse.bbs_mod.settings.values.numeric.ValueInt;

public interface CmlBlockFormAccess
{
    ValueInt bbsLight$breaking();
    ValueBoolean bbsLight$emitLight();
    ValueInt bbsLight$lightIntensity();
    ValueStructureLightSettings bbsLight$structureLight();
}
