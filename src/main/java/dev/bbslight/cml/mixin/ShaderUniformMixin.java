package dev.bbslight.cml.mixin;
import dev.bbslight.cml.EffectScope;
import net.minecraft.client.gl.ShaderProgram;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(ShaderProgram.class)
abstract class ShaderUniformMixin {
    @Inject(method="bind",at=@At("HEAD"))
    private void uniforms(CallbackInfo ci) { EffectScope.uniforms((ShaderProgram)(Object)this); }
}
