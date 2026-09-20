package dev.flanzomc.bbslightfixer;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.SpriteAtlasTexture;

public final class CmlEffectLayer extends RenderLayer
{
    private CmlEffectLayer(String name, VertexFormat format, VertexFormat.DrawMode mode, int size, boolean crumbling, boolean translucent, Runnable start, Runnable end)
    {
        super(name, format, mode, size, crumbling, translucent, start, end);
    }

    public static final RenderLayer BLOCK = of(
        "bbs_cml_block_effects",
        VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
        VertexFormat.DrawMode.QUADS,
        256,
        false,
        true,
        MultiPhaseParameters.builder()
            .program(new ShaderProgram(() -> CmlShaders.block))
            .texture(new Texture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, false, false))
            .transparency(TRANSLUCENT_TRANSPARENCY)
            .cull(DISABLE_CULLING)
            .lightmap(ENABLE_LIGHTMAP)
            .overlay(ENABLE_OVERLAY_COLOR)
            .build(false)
    );
}
