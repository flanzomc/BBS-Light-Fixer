package dev.bbslight.mixin;

import dev.bbslight.Lights;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.light.ChunkLightProvider;
import net.minecraft.world.chunk.light.ChunkBlockLightProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Covers direct terrain light reads as well as WorldRenderer's packed-light helpers. */
@Mixin(ChunkLightProvider.class)
abstract class ChunkLightProviderMixin {
    @Shadow @Final protected ChunkProvider chunkProvider;
    @Inject(method="getLightLevel",at=@At("RETURN"),cancellable=true)
    private void bbsLight$worldLight(BlockPos pos,CallbackInfoReturnable<Integer> cir) {
        if((Object)this instanceof ChunkBlockLightProvider)
            cir.setReturnValue(Lights.sampleWorldBlockLight(chunkProvider.getWorld(),pos,cir.getReturnValue()));
    }
}
