package dev.flanzomc.bbslightfixer.mixin;

import dev.flanzomc.bbslightfixer.ModelBlockLightState;
import mchorse.bbs_mod.BBSMod;
import mchorse.bbs_mod.blocks.ModelBlock;
import mchorse.bbs_mod.blocks.entities.ModelBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.state.StateManager;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value=ModelBlock.class, remap=false)
abstract class ModelBlockStateMixin
{
    @Inject(method="appendProperties", at=@At("TAIL"))
    private void bbsLight$addLightProperty(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci)
    {
        builder.add(ModelBlockLightState.LIGHT_LEVEL);
    }

    @Inject(method="getTicker", at=@At("RETURN"), cancellable=true, remap=true)
    private <T extends BlockEntity> void bbsLight$serverTicker(World world, BlockState state, BlockEntityType<T> type, CallbackInfoReturnable<BlockEntityTicker<T>> cir)
    {
        if(!world.isClient() && type==BBSMod.MODEL_BLOCK_ENTITY)
        {
            cir.setReturnValue((w,p,s,e)->ModelBlockLightState.tickServer((ModelBlockEntity)e,w,p,s));
        }
    }
}
