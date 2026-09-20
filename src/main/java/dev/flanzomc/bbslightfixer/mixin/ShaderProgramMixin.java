package dev.flanzomc.bbslightfixer.mixin;
import dev.flanzomc.bbslightfixer.render.EffectRenderContext;
import net.minecraft.client.gl.ShaderProgram;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(ShaderProgram.class)
abstract class ShaderProgramMixin{
 @Inject(method="bind",at=@At("HEAD"))
 private void bbsLight$upload(CallbackInfo ci){EffectRenderContext.upload((ShaderProgram)(Object)this);}
}
