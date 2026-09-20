package dev.flanzomc.bbslightfixer.mixin;
import dev.flanzomc.bbslightfixer.render.DeferredStates;
import mchorse.bbs_mod.forms.FormTranslucentQueue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(value=FormTranslucentQueue.class,remap=false)
abstract class QueueCaptureMixin{
 @Inject(method="add",at=@At("HEAD"))
 private static void bbsLight$capture(FormTranslucentQueue.DrawCommand command,CallbackInfo ci){DeferredStates.capture(command);}
}
