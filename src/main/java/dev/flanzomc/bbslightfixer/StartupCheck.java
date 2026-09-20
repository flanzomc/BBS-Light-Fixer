package dev.flanzomc.bbslightfixer;

import mchorse.bbs_mod.forms.forms.BlockForm;

final class StartupCheck
{
    private StartupCheck() {}

    static void run()
    {
        try
        {
            BlockForm source = new BlockForm();
            CmlEffectValues values = CmlEffectValues.of(source);

            values.glowIntensity.set(0.4F);
            values.paintIntensity.set(-0.3F);
            values.brightness.set(0.2F);
            values.contrast.set(-0.1F);
            values.saturation.set(0.5F);
            values.hue.set(45F);
            values.emitLight.set(true);
            values.lightIntensity.set(7);

            values.glowMask.shape.set(1);
            values.glowMask.offsetX.set(0.25F);
            values.glowMask.offsetY.set(0.5F);
            values.glowMask.rotateZ.set(90F);
            values.glowMask.scaleX.set(0.75F);

            BlockForm copy = new BlockForm();
            copy.fromData(source.toData());
            CmlEffectValues copied = CmlEffectValues.of(copy);

            if (copied.glowIntensity.get() != 0.4F
                || copied.paintIntensity.get() != -0.3F
                || copied.brightness.get() != 0.2F
                || copied.contrast.get() != -0.1F
                || copied.saturation.get() != 0.5F
                || copied.hue.get() != 45F
                || copied.lightIntensity.get() != 7
                || copied.glowMask.shape.get() != 1
                || copied.glowMask.offsetX.get() != 0.25F
                || copied.glowMask.rotateZ.get() != 90F
                || copied.glowMask.scaleX.get() != 0.75F)
            {
                throw new AssertionError("CML effect values failed form round-trip");
            }

            if (CmlShaders.model == null || CmlShaders.block == null)
            {
                throw new AssertionError("CML shaders did not load");
            }

            for (var shader : new net.minecraft.client.gl.ShaderProgram[] {CmlShaders.model, CmlShaders.block})
            {
                for (String uniform : new String[] {
                    "PaintColor", "GlowingColor", "FormColorGrade",
                    "PaintEffectInverse", "GlowEffectInverse",
                    "GradeBrightnessInverse", "GradeContrastInverse",
                    "GradeHueInverse", "GradeSaturationInverse"
                })
                {
                    if (shader.getUniform(uniform) == null)
                    {
                        throw new AssertionError("Missing shader uniform: " + uniform);
                    }
                }
            }

            System.out.println("BBS LIGHT CML PARITY STARTUP CHECK PASSED");
        }
        catch (Throwable throwable)
        {
            throw new IllegalStateException("BBS Light Fixer CML parity self-test failed", throwable);
        }
    }
}
