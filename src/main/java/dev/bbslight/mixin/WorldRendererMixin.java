package dev.bbslight.mixin;

import dev.bbslight.Lights;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import net.minecraft.block.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldRenderer.class)
abstract class WorldRendererMixin {
    @Inject(method="getLightmapCoordinates(Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/util/math/BlockPos;)I",at=@At("RETURN"),cancellable=true)
    private static void bbsLight$sample(BlockRenderView world,BlockPos pos,CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(Lights.sample(pos,cir.getReturnValue()));
    }
    @Inject(method="getLightmapCoordinates(Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;)I",at=@At("RETURN"),cancellable=true)
    private static void bbsLight$sampleState(BlockRenderView world,BlockState state,BlockPos pos,CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(Lights.sample(pos,cir.getReturnValue()));
    }
}
