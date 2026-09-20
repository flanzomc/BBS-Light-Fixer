package dev.flanzomc.bbslightfixer;

import dev.flanzomc.bbslightfixer.value.GlowSettingsFactory;
import dev.flanzomc.bbslightfixer.value.PaintSettingsFactory;
import dev.flanzomc.bbslightfixer.value.StructureLightSettingsFactory;
import mchorse.bbs_mod.utils.keyframes.factories.IKeyframeFactory;
import mchorse.bbs_mod.forms.forms.utils.GlowSettings;
import mchorse.bbs_mod.forms.forms.utils.PaintSettings;
import mchorse.bbs_mod.forms.forms.utils.StructureLightSettings;

public final class CmlFactories {
    public static final IKeyframeFactory<GlowSettings> GLOW = new GlowSettingsFactory();
    public static final IKeyframeFactory<PaintSettings> PAINT = new PaintSettingsFactory();
    public static final IKeyframeFactory<StructureLightSettings> STRUCTURE_LIGHT = new StructureLightSettingsFactory();
    private CmlFactories() {}
}
