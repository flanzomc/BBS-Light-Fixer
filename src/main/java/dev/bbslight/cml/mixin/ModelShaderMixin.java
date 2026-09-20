package dev.bbslight.cml.mixin;

import dev.bbslight.cml.EffectScope;
import dev.bbslight.cml.PortShaders;
import mchorse.bbs_mod.client.BBSRendering;
import mchorse.bbs_mod.client.BBSShaders;
import net.minecraft.client.gl.ShaderProgram;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Swap BBS's model shader only for an active effect scope.
 *
 * <p>The old port replaced BBSShaders.getModel() globally, so even completely
 * neutral forms ran through the addon shader. CML keeps the special shader path
 * scoped to the effect draw; doing the same also preserves BBS's normal shader
 * and Iris behavior everywhere else.</p>
 */
@Mixin(value = BBSShaders.class, remap = false)
abstract class ModelShaderMixin
{
    @Inject(method = "getModel", at = @At("RETURN"), cancellable = true)
    private static void bbsLight$program(CallbackInfoReturnable<ShaderProgram> ci)
    {
        boolean irisWorld = BBSRendering.isIrisShadersEnabled() && BBSRendering.isRenderingWorld();

        if (!irisWorld && PortShaders.model != null && EffectScope.current().active())
        {
            ci.setReturnValue(PortShaders.model);
        }
    }
}
