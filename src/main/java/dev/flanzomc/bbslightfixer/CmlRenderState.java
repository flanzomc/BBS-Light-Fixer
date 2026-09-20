package dev.flanzomc.bbslightfixer;

import mchorse.bbs_mod.forms.forms.BlockForm;
import mchorse.bbs_mod.forms.forms.Form;
import mchorse.bbs_mod.forms.forms.ModelForm;
import mchorse.bbs_mod.utils.colors.Color;
import mchorse.bbs_mod.utils.pose.Transform;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayDeque;
import java.util.Deque;

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

    private static final Mask NEUTRAL_MASK = new Mask(new Matrix4f(), new Vector3f(1F, 1F, 1F), 0F, 0F);

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

    private static final ThreadLocal<Deque<State>> STACK = ThreadLocal.withInitial(ArrayDeque::new);

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

    public static State capture(Form form, boolean picking)
    {
        if (picking || form == null) return NEUTRAL;

        CmlEffectValues values = CmlEffectValues.of(form);
        Color tint = Color.white();

        if (form instanceof ModelForm model) tint = model.color.get();
        else if (form instanceof BlockForm block) tint = block.color.get();

        Color paint = values.paintColor.get();
        Color glow = values.glowingColor.get();
        float baseHalf = form instanceof BlockForm ? 0.5F : 1F;

        return new State(
            new float[] {tint.r, tint.g, tint.b, tint.a},
            new float[] {paint.r, paint.g, paint.b, clamp(values.paintIntensity.get(), -1F, 1F)},
            new float[] {glow.r, glow.g, glow.b, finite(values.glowIntensity.get())},
            new float[] {
                finite(values.brightness.get()),
                finite(values.contrast.get()),
                finite(values.hue.get()),
                finite(values.saturation.get())
            },
            values.glowPaintOnly.get(),
            mask(values.colorMask, baseHalf),
            mask(values.paintMask, baseHalf),
            mask(values.glowMask, baseHalf),
            mask(values.brightnessMask, baseHalf),
            mask(values.contrastMask, baseHalf),
            mask(values.hueMask, baseHalf),
            mask(values.saturationMask, baseHalf),
            values.active()
        );
    }

    private static Mask mask(CmlEffectValues.Mask values, float baseHalf)
    {
        Transform transform = values.transform.get();
        Matrix4f matrix = new Matrix4f();

        if (transform != null)
        {
            matrix
                .translate(transform.translate)
                .translate(values.pivotX.get(), values.pivotY.get(), values.pivotZ.get())
                .rotateXYZ(transform.rotate.x, transform.rotate.y, transform.rotate.z)
                .translate(-values.pivotX.get(), -values.pivotY.get(), -values.pivotZ.get());
        }

        if (Math.abs(matrix.determinant()) > 1.0e-10F) matrix.invert();
        else matrix.identity();

        float sx = transform == null ? 1F : transform.scale.x;
        float sy = transform == null ? 1F : transform.scale.y;
        float sz = transform == null ? 1F : transform.scale.z;

        if (sx == 0F) sx = 0.001F;
        if (sy == 0F) sy = 0.001F;
        if (sz == 0F) sz = 0.001F;

        return new Mask(
            matrix,
            new Vector3f(baseHalf * sx, baseHalf * sy, baseHalf * sz),
            values.active() ? 1F : 0F,
            values.shape.get()
        );
    }

    private static float finite(float value)
    {
        return Float.isFinite(value) ? value : 0F;
    }

    private static float clamp(float value, float min, float max)
    {
        return Math.max(min, Math.min(max, finite(value)));
    }

    public static void upload(ShaderProgram shader)
    {
        if (shader != CmlShaders.model && shader != CmlShaders.block) return;

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
