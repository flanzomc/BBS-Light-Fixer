package dev.flanzomc.bbslightfixer;

public final class BbsLightColorMath
{
    private BbsLightColorMath() {}

    public static float smootherstep(float x)
    {
        x = Math.max(0F, Math.min(1F, x));
        return x * x * x * (x * (x * 6F - 15F) + 10F);
    }

    public static float longPathHueTarget(float from, float to)
    {
        float d = to - from;

        if (Math.abs(d) < 0.5F)
        {
            return d >= 0F ? to - 1F : to + 1F;
        }

        return to;
    }
}
