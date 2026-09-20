package dev.flanzomc.bbslightfixer.mixin;

import dev.flanzomc.bbslightfixer.CmlRenderState;
import net.minecraft.client.gl.ShaderProgram;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ShaderProgram.class)
abstract class ShaderUniformMixin
{
    @Inject(method = "bind", at = @At("HEAD"))
    private void bbsLight$uploadCmlUniforms(CallbackInfo ci)
    {
        CmlRenderState.upload((ShaderProgram) (Object) this);
    }
}
