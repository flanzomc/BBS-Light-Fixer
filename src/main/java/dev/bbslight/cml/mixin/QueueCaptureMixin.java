package dev.bbslight.cml.mixin;

import dev.bbslight.cml.DeferredEffectStates;
import mchorse.bbs_mod.forms.FormTranslucentQueue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Capture the form-effect scope when BBS actually queues a deferred command.
 *
 * <p>This is more compatible than injecting every command constructor: the command
 * constructor descriptors vary across BBS FS versions, while add(DrawCommand) is the
 * stable queue boundary we need.</p>
 */
@Mixin(value = FormTranslucentQueue.class, remap = false)
abstract class QueueCaptureMixin
{
    @Inject(method = "add", at = @At("HEAD"))
    private static void bbsLight$captureState(FormTranslucentQueue.DrawCommand command, CallbackInfo ci)
    {
        DeferredEffectStates.capture(command);
    }
}
