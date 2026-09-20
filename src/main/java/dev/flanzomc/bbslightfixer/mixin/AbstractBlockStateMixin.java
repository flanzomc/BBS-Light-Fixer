package dev.flanzomc.bbslightfixer.mixin;

import dev.flanzomc.bbslightfixer.ModelBlockLightState;
import mchorse.bbs_mod.BBSMod;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
abstract class AbstractBlockStateMixin
{
    @Inject(method="getLuminance", at=@At("HEAD"), cancellable=true)
    private void bbsLight$modelBlockLuminance(CallbackInfoReturnable<Integer> cir)
    {
        BlockState state=(BlockState)(Object)this;
        if(state.isOf(BBSMod.MODEL_BLOCK) && state.contains(ModelBlockLightState.LIGHT_LEVEL))
        {
            cir.setReturnValue(state.get(ModelBlockLightState.LIGHT_LEVEL));
        }
    }
}
