package dev.flanzomc.bbslightfixer.mixin;

import dev.flanzomc.bbslightfixer.ModelPropertiesLightAccess;
import mchorse.bbs_mod.blocks.entities.ModelProperties;
import mchorse.bbs_mod.data.types.MapType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value=ModelProperties.class, remap=false)
abstract class ModelPropertiesLightMixin implements ModelPropertiesLightAccess
{
    @Unique private int bbsLight$lightLevel;
    @Unique private boolean bbsLight$localLighting=true;

    @Inject(method="fromData", at=@At("RETURN"))
    private void bbsLight$read(MapType data, CallbackInfo ci)
    {
        if(data.has("light_level")) this.bbsLight$setLightLevel(data.getInt("light_level"));
        if(data.has("local_lighting")) this.bbsLight$localLighting=data.getBool("local_lighting");
        else if(data.has("form_lighting")) this.bbsLight$localLighting=data.getBool("form_lighting");
    }

    @Inject(method="toData", at=@At("RETURN"))
    private void bbsLight$write(MapType data, CallbackInfo ci)
    {
        data.putInt("light_level",this.bbsLight$lightLevel);
        data.putBool("local_lighting",this.bbsLight$localLighting);
    }

    public int bbsLight$getLightLevel(){return this.bbsLight$lightLevel;}
    public void bbsLight$setLightLevel(int level){this.bbsLight$lightLevel=Math.max(0,Math.min(15,level));}
    public boolean bbsLight$isLocalLighting(){return this.bbsLight$localLighting;}
    public void bbsLight$setLocalLighting(boolean local){this.bbsLight$localLighting=local;}
}
