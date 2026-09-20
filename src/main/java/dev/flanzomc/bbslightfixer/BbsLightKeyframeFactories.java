package dev.flanzomc.bbslightfixer;

import mchorse.bbs_mod.utils.keyframes.factories.GlowSettingsKeyframeFactory;
import mchorse.bbs_mod.utils.keyframes.factories.PaintSettingsKeyframeFactory;
import mchorse.bbs_mod.utils.keyframes.factories.StructureLightSettingsKeyframeFactory;

public final class BbsLightKeyframeFactories
{
    public static final PaintSettingsKeyframeFactory PAINT_SETTINGS = new PaintSettingsKeyframeFactory();
    public static final GlowSettingsKeyframeFactory GLOW_SETTINGS = new GlowSettingsKeyframeFactory();
    public static final StructureLightSettingsKeyframeFactory STRUCTURE_LIGHT_SETTINGS = new StructureLightSettingsKeyframeFactory();

    private BbsLightKeyframeFactories() {}
}
