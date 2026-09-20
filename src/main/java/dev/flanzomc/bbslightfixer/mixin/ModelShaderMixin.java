package dev.flanzomc.bbslightfixer.mixin;

import dev.flanzomc.bbslightfixer.CmlRenderState;
import dev.flanzomc.bbslightfixer.CmlShaders;
import mchorse.bbs_mod.client.BBSRendering;
import mchorse.bbs_mod.client.BBSShaders;
import net.minecraft.client.gl.ShaderProgram;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Reliable VAO hook: whenever BBS asks for its model shader while a CML-effect
 * form is actively rendering, return the CML shader. This is scoped by
 * CmlRenderState, so unrelated models keep BBS's original shader.
 */
@Mixin(value = BBSShaders.class, remap = false)
abstract class ModelShaderMixin
{
    @Inject(method = "getModel", at = @At("RETURN"), cancellable = true)
    private static void bbsLight$cmlModelShader(CallbackInfoReturnable<ShaderProgram> ci)
    {
        if (CmlShaders.model != null
            && CmlRenderState.current().active()
            && !BBSRendering.isIrisShadowPass())
        {
            ci.setReturnValue(CmlShaders.model);
        }
    }
}
