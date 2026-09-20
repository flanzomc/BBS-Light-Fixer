package dev.flanzomc.bbslightfixer.mixin;

import dev.flanzomc.bbslightfixer.CmlBlockFormAccess;
import mchorse.bbs_mod.forms.forms.BlockForm;
import mchorse.bbs_mod.forms.forms.utils.StructureLightSettings;
import mchorse.bbs_mod.settings.values.misc.ValueStructureLightSettings;
import mchorse.bbs_mod.settings.values.numeric.ValueBoolean;
import mchorse.bbs_mod.settings.values.numeric.ValueInt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value=BlockForm.class, remap=false)
abstract class BlockFormEffectsMixin implements CmlBlockFormAccess
{
    @Unique private ValueInt bbsLight$breaking;
    @Unique private ValueBoolean bbsLight$emitLight;
    @Unique private ValueInt bbsLight$lightIntensity;
    @Unique private ValueStructureLightSettings bbsLight$structureLight;

    @Inject(method="<init>", at=@At("RETURN"))
    private void bbsLight$addBlockValues(CallbackInfo ci)
    {
        BlockForm self=(BlockForm)(Object)this;
        this.bbsLight$breaking=new ValueInt("breaking",0,0,10);
        this.bbsLight$emitLight=new ValueBoolean("emit_light",false);
        this.bbsLight$lightIntensity=new ValueInt("light_intensity",15,0,15);
        this.bbsLight$structureLight=new ValueStructureLightSettings("structure_light",new StructureLightSettings(false,15));

        self.add(this.bbsLight$breaking);
        self.add(this.bbsLight$emitLight);
        self.add(this.bbsLight$lightIntensity);
        self.add(this.bbsLight$structureLight);
    }

    public ValueInt bbsLight$breaking(){return this.bbsLight$breaking;}
    public ValueBoolean bbsLight$emitLight(){return this.bbsLight$emitLight;}
    public ValueInt bbsLight$lightIntensity(){return this.bbsLight$lightIntensity;}
    public ValueStructureLightSettings bbsLight$structureLight(){return this.bbsLight$structureLight;}
}
