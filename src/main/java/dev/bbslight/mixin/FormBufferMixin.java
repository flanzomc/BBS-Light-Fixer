package dev.bbslight.mixin;

import dev.bbslight.EmissiveLayers;
import net.minecraft.client.render.RenderLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets="mchorse.bbs_mod.forms.CustomVertexConsumerProvider", remap=false)
abstract class FormBufferMixin {
    // Optional: older BBS builds do not have this translucent queue.
    @Inject(method="isDeferrableTranslucent", at=@At("HEAD"), cancellable=true, require=0, remap=false)
    private static void bbsLight$immediate(RenderLayer layer, CallbackInfoReturnable<Boolean> cir) {
        if(layer==EmissiveLayers.BLOCK) cir.setReturnValue(false);
    }
}
