package dev.flanzomc.bbslightfixer.mixin;

import dev.flanzomc.bbslightfixer.CmlEffectValues;
import dev.flanzomc.bbslightfixer.CmlIrisBridge;
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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(value = ModelFormRenderer.class, remap = false)
abstract class ModelEffectsMixin extends FormRenderer<ModelForm>
{
    @Unique
    private boolean bbsLight$irisOverride;

    private ModelEffectsMixin(ModelForm form)
    {
        super(form);
    }

    @Inject(method = "renderModel", at = @At("HEAD"))
    private void bbsLight$beforeModel(
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
        CmlEffectValues values = CmlEffectValues.of(this.form);

        CmlRenderState.setCpuPretransformed(!model.isVAORendered());

        this.bbsLight$irisOverride = stencil == null
            && values.active()
            && CmlShaders.model != null
            && BBSRendering.isIrisShadersEnabled()
            && BBSRendering.isRenderingWorld()
            && !BBSRendering.isIrisShadowPass();

        if (this.bbsLight$irisOverride)
        {
            CmlIrisBridge.begin();
        }
    }

    @Inject(method = "renderModel", at = @At("RETURN"))
    private void bbsLight$afterModel(
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

        if (this.bbsLight$irisOverride)
        {
            CmlIrisBridge.end();
            this.bbsLight$irisOverride = false;
        }
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

        if (stencil != null
            || !values.active()
            || CmlShaders.model == null
            || BBSRendering.isIrisShadowPass())
        {
            return original;
        }

        /*
         * FS CPU geometry already bakes the render stack into Position. Unmasked
         * effects can still use the CML shader with the CPU-pretransformed uniform
         * setup. Spatial masks need true model-local Position, so those stay on the
         * safe BBS path until the CPU geometry path itself is ported.
         */
        if (!model.isVAORendered() && values.hasSpatialMask())
        {
            return original;
        }

        return () -> CmlShaders.model;
    }
}
