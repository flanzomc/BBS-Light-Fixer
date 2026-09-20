package dev.bbslight.cml;

import com.mojang.blaze3d.systems.RenderSystem;
import mchorse.bbs_mod.forms.forms.Form;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Per-form render state for the CML color/glow/paint port.
 *
 * <p>The important bit here is that CPU/non-VAO BBS geometry does not pass through
 * ModelVAORenderer.setupUniforms(). CML explicitly uploads the standard Minecraft
 * sampler/fog/light state for that path; without it, switching to the effect shader
 * can leave FogEnd/Samplers at their JSON defaults and make a model appear to vanish.</p>
 */
public final class EffectScope
{
    public record State(
        float[] grade,
        float[] glow,
        float[] paint,
        Matrix4f rootInverse,
        Matrix4f[] masks,
        float[] enabled,
        float[] shapes,
        boolean active
    ) {}

    public static final State NEUTRAL = new State(
        new float[4],
        new float[4],
        new float[4],
        new Matrix4f(),
        new Matrix4f[] {
            new Matrix4f(), new Matrix4f(), new Matrix4f(),
            new Matrix4f(), new Matrix4f(), new Matrix4f()
        },
        new float[6],
        new float[6],
        false
    );

    private static final ThreadLocal<Deque<State>> STACK = ThreadLocal.withInitial(ArrayDeque::new);

    private EffectScope() {}

    public static State current()
    {
        return STACK.get().isEmpty() ? NEUTRAL : STACK.get().peek();
    }

    public static void push(State state)
    {
        STACK.get().push(state == null ? NEUTRAL : state);
    }

    public static void pop()
    {
        if (!STACK.get().isEmpty())
        {
            STACK.get().pop();
        }
    }

    public static void replaceCurrent(State state)
    {
        if (!STACK.get().isEmpty())
        {
            pop();
            push(state);
        }
    }

    public static State capture(Form form, MatrixStack stack, boolean picking)
    {
        if (picking)
        {
            return NEUTRAL;
        }

        EffectValues v = EffectValues.of(form);
        var g = v.glowColor.get();
        var p = v.paintColor.get();

        Matrix4f[] masks = new Matrix4f[6];
        float[] enabled = new float[6];
        float[] shapes = new float[6];

        for (int i = 0; i < 6; i++)
        {
            masks[i] = v.masks[i].transform.get().createMatrix();

            boolean invertible = Math.abs(masks[i].determinant()) > 1.0e-10F;

            if (invertible)
            {
                masks[i].invert();
            }
            else
            {
                masks[i].identity().scaling(1.0e10F);
            }

            enabled[i] = v.masks[i].enabled.get() ? 1F : 0F;
            shapes[i] = v.masks[i].shape.get();
        }

        Matrix4f root = stack == null
            ? new Matrix4f()
            : new Matrix4f(RenderSystem.getModelViewMatrix()).mul(stack.peek().getPositionMatrix());

        if (Math.abs(root.determinant()) > 1.0e-10F)
        {
            root.invert();
        }
        else
        {
            root.identity();
        }

        return new State(
            new float[] {
                finite(v.brightness.get()),
                finite(v.contrast.get()),
                finite(v.hue.get()),
                finite(v.saturation.get())
            },
            new float[] {g.r, g.g, g.b, finite(v.glow.get())},
            new float[] {p.r, p.g, p.b, finite(v.paint.get())},
            root,
            masks,
            enabled,
            shapes,
            v.active()
        );
    }

    private static float finite(float value)
    {
        return Float.isFinite(value) ? value : 0F;
    }

    public static void uniforms(ShaderProgram shader)
    {
        if (shader != PortShaders.model && shader != PortShaders.block)
        {
            return;
        }

        /*
         * Mirror the standard state upload CML performs before CPU geometry draws.
         * VAO renders already upload most of these through BBS ModelVAORenderer, but
         * repeating them is harmless and keeps every custom-shader path consistent.
         */
        for (int i = 0; i < 12; i++)
        {
            shader.addSampler("Sampler" + i, RenderSystem.getShaderTexture(i));
        }

        if (shader.viewRotationMat != null)
        {
            shader.viewRotationMat.set(RenderSystem.getInverseViewRotationMatrix());
        }

        if (shader.fogStart != null)
        {
            shader.fogStart.set(RenderSystem.getShaderFogStart());
        }

        if (shader.fogEnd != null)
        {
            shader.fogEnd.set(RenderSystem.getShaderFogEnd());
        }

        if (shader.fogColor != null)
        {
            shader.fogColor.set(RenderSystem.getShaderFogColor());
        }

        if (shader.fogShape != null)
        {
            shader.fogShape.set(RenderSystem.getShaderFogShape().getId());
        }

        if (shader.colorModulator != null)
        {
            shader.colorModulator.set(1F, 1F, 1F, 1F);
        }

        if (shader.gameTime != null)
        {
            shader.gameTime.set(RenderSystem.getShaderGameTime());
        }

        if (shader.textureMat != null)
        {
            shader.textureMat.set(RenderSystem.getTextureMatrix());
        }

        RenderSystem.setupShaderLights(shader);

        State s = current();

        if (shader == PortShaders.block)
        {
            GlUniform normal = shader.getUniform("NormalMat");

            if (normal != null)
            {
                /* CPU/block vertices already carry their transformed normals. */
                normal.set(new Matrix3f());
            }
        }

        set(shader, "FormColorGrade", s.grade);
        set(shader, "GlowingColor", s.glow);
        set(shader, "PaintColor", s.paint);

        GlUniform root = shader.getUniform("RootInverse");

        if (root != null)
        {
            root.set(s.rootInverse);
        }

        String[] prefixes = {
            "Glow", "Paint", "GradeBrightness",
            "GradeContrast", "GradeSaturation", "GradeHue"
        };

        for (int i = 0; i < 6; i++)
        {
            String prefix = prefixes[i];
            GlUniform inverse = shader.getUniform(prefix + "Inverse");
            GlUniform active = shader.getUniform(prefix + "Active");
            GlUniform shape = shader.getUniform(prefix + "Shape");

            if (inverse != null) inverse.set(s.masks[i]);
            if (active != null) active.set(s.enabled[i]);
            if (shape != null) shape.set(s.shapes[i]);
        }
    }

    private static void set(ShaderProgram shader, String name, float[] values)
    {
        GlUniform uniform = shader.getUniform(name);

        if (uniform != null)
        {
            uniform.set(values);
        }
    }
}
