package dev.bbslight.cml.mixin;
import dev.bbslight.cml.PortShaders;
import mchorse.bbs_mod.client.BBSShaders;
import net.minecraft.client.gl.ShaderProgram;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(value=BBSShaders.class,remap=false)
abstract class ModelShaderMixin {
    @Inject(method="getModel",at=@At("RETURN"),cancellable=true)
    private static void program(CallbackInfoReturnable<ShaderProgram> ci) { if(PortShaders.model!=null)ci.setReturnValue(PortShaders.model); }
}
