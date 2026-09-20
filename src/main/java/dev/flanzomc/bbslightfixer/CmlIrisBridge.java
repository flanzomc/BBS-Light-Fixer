package dev.flanzomc.bbslightfixer;

import mchorse.bbs_mod.client.BBSRendering;

/**
 * Temporarily tells Iris that the main framebuffer is not the active target while
 * BBS Light Fixer runs a vanilla core shader. BBS FS already exposes this exact
 * compatibility hook for core-program rendering inside an Iris world pass.
 */
public final class CmlIrisBridge
{
    private static final ThreadLocal<Integer> DEPTH = ThreadLocal.withInitial(() -> 0);

    private CmlIrisBridge() {}

    public static void begin()
    {
        int depth = DEPTH.get();

        if (depth == 0)
        {
            BBSRendering.setIrisMainBound(false);
        }

        DEPTH.set(depth + 1);
    }

    public static void end()
    {
        int depth = DEPTH.get();

        if (depth <= 0)
        {
            return;
        }

        depth--;

        if (depth == 0)
        {
            BBSRendering.setIrisMainBound(true);
        }

        DEPTH.set(depth);
    }
}
