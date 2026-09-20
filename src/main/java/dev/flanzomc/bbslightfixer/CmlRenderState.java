package dev.flanzomc.bbslightfixer;

import com.mojang.blaze3d.systems.RenderSystem;
import mchorse.bbs_mod.forms.forms.BlockForm;
import mchorse.bbs_mod.forms.forms.Form;
import mchorse.bbs_mod.forms.forms.ModelForm;
import mchorse.bbs_mod.utils.MathUtils;
import mchorse.bbs_mod.utils.colors.Color;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * CML effect state translated to BBS FS shader uniforms.
 *
 * The mask matrix/half-extents match CML EffectTransformMath:
 * rotation is degrees, scale lives in half-extents, and model masks are bottom anchored.
 */
public final class CmlRenderState
{
    public record Mask(Matrix4f inverse, Vector3f half, float active, float shape) {}

    public record State(
        float[] formTint,
        float[] paint,
        float[] glow,
        float[] grade,
        boolean glowPaintOnly,
        Mask colorMask,
        Mask paintMask,
        Mask glowMask,
        Mask brightnessMask,
        Mask contrastMask,
        Mask hueMask,
        Mask saturationMask,
        boolean active
    ) {}

    private static final Mask NEUTRAL_MASK =
        new Mask(new Matrix4f(), new Vector3f(1F, 1F, 1F), 0F, 0F);

    public static final State NEUTRAL = new State(
        new float[] {1F, 1F, 1F, 1F},
        new float[] {1F, 1F, 1F, 0F},
        new float[] {1F, 1F, 1F, 0F},
        new float[4],
        false,
        NEUTRAL_MASK, NEUTRAL_MASK, NEUTRAL_MASK,
        NEUTRAL_MASK, NEUTRAL_MASK, NEUTRAL_MASK, NEUTRAL_MASK,
        false
    );

    private static final ThreadLocal<Deque<State>> STACK =
        ThreadLocal.withInitial(ArrayDeque::new);
    private static final ThreadLocal<Boolean> CPU_PRETRANSFORMED =
        ThreadLocal.withInitial(() -> false);

    private CmlRenderState() {}

    public static State current()
    {
        Deque<State> stack = STACK.get();
        return stack.isEmpty() ? NEUTRAL : stack.peek();
    }

    public static void push(State state)
    {
        STACK.get().push(state == null ? NEUTRAL : state);
    }

    public static void pop()
    {
        Deque<State> stack = STACK.get();
        if (!stack.isEmpty()) stack.pop();
    }

    public static void setCpuPretransformed(boolean value)
    {
        CPU_PRETRANSFORMED.set(value);
    }

    public static State capture(Form form, boolean picking)
    {
        if (picking || form == null) return NEUTRAL;

        CmlEffectValues values = CmlEffectValues.of(form);
        Color tint = Color.white();

        if (form instanceof ModelForm model) tint = model.color.get();
        else if (form instanceof BlockForm block) tint = block.color.get();

        Color paint = values.paintColor.get();
        Color glow = values.glowingColor.get();
        boolean block = form instanceof BlockForm;

        return new State(
            new float[] {tint.r, tint.g, tint.b, tint.a},
            new float[] {
                paint.r, paint.g, paint.b,
                MathUtils.clamp(finite(values.paintIntensity.get()), -1F, 1F)
            },
            new float[] {
                glow.r, glow.g, glow.b,
                finite(values.glowIntensity.get())
            },
            new float[] {
                finite(values.brightness.get()),
                finite(values.contrast.get()),
                finite(values.hue.get()),
                finite(values.saturation.get())
            },
            values.glowPaintOnly.get(),
            mask(values.colorMask, block),
            mask(values.paintMask, block),
            mask(values.glowMask, block),
            mask(values.brightnessMask, block),
            mask(values.contrastMask, block),
            mask(values.hueMask, block),
            mask(values.saturationMask, block),
            values.active()
        );
    }

    private static Mask mask(CmlEffectValues.Mask values, boolean block)
    {
        boolean active = values.active();

        if (!active)
        {
            return new Mask(
                new Matrix4f(),
                block ? new Vector3f(0.5F, 0.5F, 0.5F) : new Vector3f(1F, 1F, 1F),
                0F,
                values.shape.get()
            );
        }

        Matrix4f matrix = new Matrix4f()
            .translate(values.offsetX.get(), values.offsetY.get(), values.offsetZ.get())
            .translate(values.pivotX.get(), values.pivotY.get(), values.pivotZ.get())
            .rotateXYZ(
                MathUtils.toRad(values.rotateX.get()),
                MathUtils.toRad(values.rotateY.get()),
                MathUtils.toRad(values.rotateZ.get())
            )
            .translate(-values.pivotX.get(), -values.pivotY.get(), -values.pivotZ.get());

        if (Math.abs(matrix.determinant()) > 1.0e-10F) matrix.invert();
        else matrix.identity();

        float sx = zeroSafe(values.scaleX.get());
        float sy = zeroSafe(values.scaleY.get());
        float sz = zeroSafe(values.scaleZ.get());
        float base = block ? 0.5F : 1F;

        return new Mask(
            matrix,
            new Vector3f(base * sx, base * sy, base * sz),
            1F,
            values.shape.get()
        );
    }

    private static float zeroSafe(float value)
    {
        if (!Float.isFinite(value)) return 1F;
        return value == 0F ? 0.001F : value;
    }

    private static float finite(float value)
    {
        return Float.isFinite(value) ? value : 0F;
    }

    public static void upload(ShaderProgram shader)
    {
        if (shader != CmlShaders.model && shader != CmlShaders.block) return;

        /*
         * CML has a dedicated CPU-pretransformed path. FS 2.5.2 does not, so when a
         * CPU/shape-key model uses the port shader we recreate the same state here.
         */
        if (CPU_PRETRANSFORMED.get())
        {
            for (int i = 0; i < 12; i++)
            {
                shader.addSampler("Sampler" + i, RenderSystem.getShaderTexture(i));
            }

            if (shader.projectionMat != null)
                shader.projectionMat.set(RenderSystem.getProjectionMatrix());

            if (shader.modelViewMat != null)
                shader.modelViewMat.set(RenderSystem.getModelViewMatrix());

            GlUniform normal = shader.getUniform("NormalMat");
            if (normal != null) normal.set(new Matrix3f());

            if (shader.viewRotationMat != null)
                shader.viewRotationMat.set(RenderSystem.getInverseViewRotationMatrix());

            if (shader.fogStart != null)
                shader.fogStart.set(RenderSystem.getShaderFogStart());

            if (shader.fogEnd != null)
                shader.fogEnd.set(RenderSystem.getShaderFogEnd());

            if (shader.fogColor != null)
                shader.fogColor.set(RenderSystem.getShaderFogColor());

            if (shader.fogShape != null)
                shader.fogShape.set(RenderSystem.getShaderFogShape().getId());

            if (shader.colorModulator != null)
                shader.colorModulator.set(1F, 1F, 1F, 1F);

            RenderSystem.setupShaderLights(shader);
        }

        State state = current();

        set4(shader, "PaintColor", state.paint);
        set4(shader, "GlowingColor", state.glow);
        set4(shader, "FormColorGrade", state.grade);
        set4(shader, "FormColorTint", state.formTint);

        set1(shader, "GlowPaintOnly", state.glowPaintOnly ? 1F : 0F);
        set1(shader, "PaintOverlay", 0F);
        set1(shader, "TextureBlendFactor", 0F);
        set1(shader, "TextureBlendActive", 0F);
        set1(shader, "ColorTintOverlay", 0F);
        set1(shader, "ColorGradeOverlay", 0F);
        set1(shader, "ColorTintMasked", state.colorMask.active);

        setMatrix(shader, "FormRootInverse", new Matrix4f());

        uploadMask(shader, "Paint", state.paintMask, false);
        uploadMask(shader, "Glow", state.glowMask, false);
        uploadMask(shader, "Color", state.colorMask, false);
        uploadMask(shader, "GradeBrightness", state.brightnessMask, true);
        uploadMask(shader, "GradeContrast", state.contrastMask, true);
        uploadMask(shader, "GradeHue", state.hueMask, true);
        uploadMask(shader, "GradeSaturation", state.saturationMask, true);
    }

    private static void uploadMask(ShaderProgram shader, String prefix, Mask mask, boolean grade)
    {
        if (grade)
        {
            setMatrix(shader, prefix + "Inverse", mask.inverse);
            set1(shader, prefix + "Active", mask.active);
            set3(shader, prefix + "Half", mask.half);
            set1(shader, prefix + "BottomAnchored", 1F);
            set1(shader, prefix + "Shape", mask.shape);
        }
        else
        {
            setMatrix(shader, prefix + "EffectInverse", mask.inverse);
            set1(shader, prefix + "EffectActive", mask.active);
            set3(shader, prefix + "MaskHalf", mask.half);
            set1(shader, prefix + "MaskBottomAnchored", 1F);
            set1(shader, prefix + "MaskShape", mask.shape);
        }
    }

    private static void set1(ShaderProgram shader, String name, float value)
    {
        GlUniform uniform = shader.getUniform(name);
        if (uniform != null) uniform.set(value);
    }

    private static void set3(ShaderProgram shader, String name, Vector3f value)
    {
        GlUniform uniform = shader.getUniform(name);
        if (uniform != null) uniform.set(value.x, value.y, value.z);
    }

    private static void set4(ShaderProgram shader, String name, float[] value)
    {
        GlUniform uniform = shader.getUniform(name);
        if (uniform != null) uniform.set(value);
    }

    private static void setMatrix(ShaderProgram shader, String name, Matrix4f value)
    {
        GlUniform uniform = shader.getUniform(name);
        if (uniform != null) uniform.set(value);
    }
}
