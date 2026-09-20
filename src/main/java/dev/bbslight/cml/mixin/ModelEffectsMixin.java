package dev.bbslight.cml.mixin;

import dev.bbslight.cml.EffectScope;
import dev.bbslight.cml.EffectValues;
import dev.bbslight.cml.PortShaders;
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
    private void bbsLight$scope(
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
        EffectScope.push(EffectScope.capture(this.form, stack, stencil != null));
    }

    @Inject(method = "renderModel", at = @At("RETURN"))
    private void bbsLight$end(CallbackInfo ci)
    {
        EffectScope.pop();
    }

    @ModifyVariable(method = "renderModel", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private Supplier<ShaderProgram> bbsLight$shader(
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
        EffectValues values = EffectValues.of(this.form);
        boolean irisWorld = BBSRendering.isIrisShadersEnabled() && BBSRendering.isRenderingWorld();

        if (stencil != null || !values.active() || irisWorld)
        {
            return original;
        }

        if (model.isVAORendered())
        {
            return PortShaders.model == null ? original : () -> PortShaders.model;
        }

        /*
         * FS CPU/non-VAO models have their positions/normals pre-transformed. The
         * block variant is the same effect program but uses identity NormalMat and
         * receives the standard sampler/fog/light state from EffectScope.uniforms().
         */
        return PortShaders.block == null ? original : () -> PortShaders.block;
    }
}
