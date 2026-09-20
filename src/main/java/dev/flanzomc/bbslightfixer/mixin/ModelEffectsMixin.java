package dev.flanzomc.bbslightfixer.mixin;

import dev.flanzomc.bbslightfixer.CmlEffectValues;
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
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.function.Supplier;

@Mixin(value = ModelFormRenderer.class, remap = false)
abstract class ModelEffectsMixin extends FormRenderer<ModelForm>
{
    private ModelEffectsMixin(ModelForm form) { super(form); }

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
        if (stencil != null
            || !CmlEffectValues.of(this.form).active()
            || CmlShaders.model == null
            || !model.isVAORendered()
            || (BBSRendering.isIrisShadersEnabled() && BBSRendering.isRenderingWorld()))
        {
            return original;
        }

        return () -> CmlShaders.model;
    }
}
