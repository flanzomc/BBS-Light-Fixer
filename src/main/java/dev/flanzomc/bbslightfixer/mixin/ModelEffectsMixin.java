package dev.flanzomc.bbslightfixer.mixin;

import dev.flanzomc.bbslightfixer.CmlEffectValues;
import dev.flanzomc.bbslightfixer.CmlRenderState;
import dev.flanzomc.bbslightfixer.CmlShaders;
import mchorse.bbs_mod.client.BBSRendering;
import mchorse.bbs_mod.cubic.ModelInstance;
import mchorse.bbs_mod.forms.entities.IEntity;
import mchorse.bbs_mod.forms.forms.ModelForm;
import mchorse.bbs_mod.forms.renderers.FormRenderer;
import mchorse.bbs_mod.forms.renderers.ModelFormRenderer;
import mchorse.bbs_mod.ui.framework.elements.utils.StencilMap;
import mchorse.bbs_mod.utils.colors.Color;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(value = ModelFormRenderer.class, remap = false)
abstract class ModelEffectsMixin extends FormRenderer<ModelForm>
{
    private ModelEffectsMixin(ModelForm form)
    {
        super(form);
    }

    @Inject(method = "renderModel", at = @At("HEAD"))
    private void bbsLight$cpuState(
        IEntity entity,
        Supplier<ShaderProgram> program,
        MatrixStack stack,
        ModelInstance model,
        int light,
        int overlay,
        Color context,
        Color color,
        boolean additive,
        boolean ui,
        StencilMap stencil,
        float transition,
        MatrixStack world,
        CallbackInfo ci
    )
    {
        CmlRenderState.setCpuPretransformed(!model.isVAORendered());
    }

    @Inject(method = "renderModel", at = @At("RETURN"))
    private void bbsLight$clearCpuState(
        IEntity entity,
        Supplier<ShaderProgram> program,
        MatrixStack stack,
        ModelInstance model,
        int light,
        int overlay,
        Color context,
        Color color,
        boolean additive,
        boolean ui,
        StencilMap stencil,
        float transition,
        MatrixStack world,
        CallbackInfo ci
    )
    {
        CmlRenderState.setCpuPretransformed(false);
    }

    @ModifyVariable(method = "renderModel", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private Supplier<ShaderProgram> bbsLight$useCmlShader(
        Supplier<ShaderProgram> original,
        IEntity entity,
        Supplier<ShaderProgram> program,
        MatrixStack stack,
        ModelInstance model,
        int light,
        int overlay,
        Color context,
        Color color,
        boolean additive,
        boolean ui,
        StencilMap stencil,
        float transition,
        MatrixStack world
    )
    {
        CmlEffectValues values = CmlEffectValues.of(this.form);
        boolean irisWorld = BBSRendering.isIrisShadersEnabled() && BBSRendering.isRenderingWorld();

        if (stencil != null || !values.active() || CmlShaders.model == null || irisWorld)
        {
            return original;
        }

        /*
         * Form-level effects can run on CPU models too. Spatial masks require true
         * model-local Position, which FS's CPU renderer has already transformed, so
         * keep masked CPU models on BBS's safe shader rather than producing a wrong
         * mask or disappearing geometry.
         */
        if (!model.isVAORendered() && values.hasSpatialMask())
        {
            return original;
        }

        return () -> CmlShaders.model;
    }
}
