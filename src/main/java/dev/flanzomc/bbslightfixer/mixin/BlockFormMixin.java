package dev.flanzomc.bbslightfixer.mixin;

import dev.flanzomc.bbslightfixer.access.CmlBlockAccess;
import dev.flanzomc.bbslightfixer.value.ValueStructureLightSettings;
import mchorse.bbs_mod.forms.forms.BlockForm;
import mchorse.bbs_mod.forms.forms.utils.StructureLightSettings;
import mchorse.bbs_mod.settings.values.base.BaseValue;
import mchorse.bbs_mod.settings.values.core.ValueString;
import mchorse.bbs_mod.settings.values.numeric.ValueBoolean;
import mchorse.bbs_mod.settings.values.numeric.ValueInt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value=BlockForm.class, remap=false)
abstract class BlockFormMixin implements CmlBlockAccess {
 @Shadow public abstract void add(BaseValue v);
 @Unique private ValueString bbsLight$biomeId;
 @Unique private ValueInt bbsLight$breaking,bbsLight$repeatX,bbsLight$repeatY,bbsLight$repeatZ,bbsLight$lightIntensity;
 @Unique private ValueBoolean bbsLight$repeatCenterX,bbsLight$repeatCenterY,bbsLight$repeatCenterZ,bbsLight$emitLight;
 @Unique private ValueStructureLightSettings bbsLight$structureLight;
 @Inject(method="<init>",at=@At("TAIL"))
 private void bbsLight$init(CallbackInfo ci){
  bbsLight$biomeId=new ValueString("biome_id","");
  bbsLight$breaking=new ValueInt("breaking",0,0,10);
  bbsLight$repeatX=new ValueInt("repeat_x",1,1,64);bbsLight$repeatY=new ValueInt("repeat_y",1,1,64);bbsLight$repeatZ=new ValueInt("repeat_z",1,1,64);
  bbsLight$repeatCenterX=new ValueBoolean("repeat_center_x",false);bbsLight$repeatCenterY=new ValueBoolean("repeat_center_y",false);bbsLight$repeatCenterZ=new ValueBoolean("repeat_center_z",false);
  bbsLight$emitLight=new ValueBoolean("emit_light",false);bbsLight$lightIntensity=new ValueInt("light_intensity",15,0,15);
  bbsLight$structureLight=new ValueStructureLightSettings("structure_light",new StructureLightSettings(false,15));
  bbsLight$emitLight.invisible();bbsLight$lightIntensity.invisible();
  add(bbsLight$biomeId);add(bbsLight$breaking);add(bbsLight$repeatX);add(bbsLight$repeatY);add(bbsLight$repeatZ);
  add(bbsLight$repeatCenterX);add(bbsLight$repeatCenterY);add(bbsLight$repeatCenterZ);add(bbsLight$emitLight);add(bbsLight$lightIntensity);add(bbsLight$structureLight);
 }
 public ValueString bbsLight$getBiomeId(){return bbsLight$biomeId;}
 public ValueInt bbsLight$getBreaking(){return bbsLight$breaking;}
 public ValueInt bbsLight$getRepeatX(){return bbsLight$repeatX;} public ValueInt bbsLight$getRepeatY(){return bbsLight$repeatY;} public ValueInt bbsLight$getRepeatZ(){return bbsLight$repeatZ;}
 public ValueBoolean bbsLight$getRepeatCenterX(){return bbsLight$repeatCenterX;} public ValueBoolean bbsLight$getRepeatCenterY(){return bbsLight$repeatCenterY;} public ValueBoolean bbsLight$getRepeatCenterZ(){return bbsLight$repeatCenterZ;}
 public ValueBoolean bbsLight$getEmitLight(){return bbsLight$emitLight;} public ValueInt bbsLight$getLightIntensity(){return bbsLight$lightIntensity;}
 public ValueStructureLightSettings bbsLight$getStructureLight(){return bbsLight$structureLight;}
}
