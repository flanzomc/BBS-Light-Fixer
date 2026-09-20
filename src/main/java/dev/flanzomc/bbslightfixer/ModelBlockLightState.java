package dev.flanzomc.bbslightfixer;

import mchorse.bbs_mod.blocks.entities.ModelBlockEntity;
import mchorse.bbs_mod.blocks.entities.ModelProperties;
import mchorse.bbs_mod.forms.forms.BlockForm;
import mchorse.bbs_mod.forms.forms.Form;
import mchorse.bbs_mod.forms.forms.LightForm;
import mchorse.bbs_mod.forms.forms.utils.StructureLightSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class ModelBlockLightState
{
    public static final IntProperty LIGHT_LEVEL = IntProperty.of("light_level", 0, 15);

    private ModelBlockLightState() {}

    public static int resolve(Form form, int fallback)
    {
        int target = clamp(fallback);

        if (form instanceof LightForm light)
        {
            return light.enabled.get() ? clamp(light.level.get()) : target;
        }

        if (form instanceof BlockForm block)
        {
            CmlBlockFormAccess access = (CmlBlockFormAccess) block;
            StructureLightSettings settings = access.bbsLight$structureLight().get();
            boolean enabled = settings != null ? settings.enabled : access.bbsLight$emitLight().get();
            int intensity = settings != null ? settings.intensity : access.bbsLight$lightIntensity().get();
            BlockState state = block.blockState.get();

            if (enabled && state != null && state.getLuminance() > 0)
            {
                return clamp(Math.min(state.getLuminance(), intensity));
            }
        }

        return target;
    }

    public static void tickServer(ModelBlockEntity entity, World world, BlockPos pos, BlockState state)
    {
        ModelProperties properties = entity.getProperties();
        ModelPropertiesLightAccess access = (ModelPropertiesLightAccess) properties;
        int target = resolve(properties.getForm(), access.bbsLight$getLightLevel());

        access.bbsLight$setLightLevel(target);

        if (state.contains(LIGHT_LEVEL) && state.get(LIGHT_LEVEL) != target)
        {
            world.setBlockState(pos, state.with(LIGHT_LEVEL, target), Block.NOTIFY_LISTENERS);
        }
    }

    public static int clamp(int level)
    {
        return Math.max(0, Math.min(15, level));
    }
}
