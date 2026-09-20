package dev.flanzomc.bbslightfixer.access;
import dev.flanzomc.bbslightfixer.value.ValueStructureLightSettings;
import mchorse.bbs_mod.settings.values.core.ValueString;
import mchorse.bbs_mod.settings.values.numeric.ValueBoolean;
import mchorse.bbs_mod.settings.values.numeric.ValueInt;
public interface CmlBlockAccess{
 ValueString bbsLight$getBiomeId();
 ValueInt bbsLight$getBreaking();
 ValueInt bbsLight$getRepeatX(); ValueInt bbsLight$getRepeatY(); ValueInt bbsLight$getRepeatZ();
 ValueBoolean bbsLight$getRepeatCenterX(); ValueBoolean bbsLight$getRepeatCenterY(); ValueBoolean bbsLight$getRepeatCenterZ();
 ValueBoolean bbsLight$getEmitLight(); ValueInt bbsLight$getLightIntensity();
 ValueStructureLightSettings bbsLight$getStructureLight();
}
