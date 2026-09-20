package dev.flanzomc.bbslightfixer;

import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

public final class CmlShaders
{
    public static ShaderProgram model;
    public static ShaderProgram block;

    private CmlShaders() {}

    public static void register()
    {
        CoreShaderRegistrationCallback.EVENT.register(context ->
        {
            context.register(
                new Identifier("bbs_light_fixer", "cml_model"),
                VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
                program -> model = program
            );
            context.register(
                new Identifier("bbs_light_fixer", "cml_block"),
                VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
                program -> block = program
            );
        });
    }
}
