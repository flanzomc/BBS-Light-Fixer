package dev.flanzomc.bbslightfixer;

import mchorse.bbs_mod.BBSMod;
import mchorse.bbs_mod.forms.forms.LightForm;
import mchorse.bbs_mod.resources.Link;
import mchorse.bbs_mod.utils.keyframes.factories.KeyframeFactories;
import net.fabricmc.api.ModInitializer;

public final class BbsLightFixerCommon implements ModInitializer
{
    @Override
    public void onInitialize()
    {
        BBSMod.getForms().register(Link.bbs("light"), LightForm.class, null);

        KeyframeFactories.FACTORIES.put("paint_settings", BbsLightKeyframeFactories.PAINT_SETTINGS);
        KeyframeFactories.FACTORIES.put("paint", BbsLightKeyframeFactories.PAINT_SETTINGS);
        KeyframeFactories.FACTORIES.put("glow_settings", BbsLightKeyframeFactories.GLOW_SETTINGS);
        KeyframeFactories.FACTORIES.put("glow", BbsLightKeyframeFactories.GLOW_SETTINGS);
        KeyframeFactories.FACTORIES.put("structure_light_settings", BbsLightKeyframeFactories.STRUCTURE_LIGHT_SETTINGS);
    }
}
