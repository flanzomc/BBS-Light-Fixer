package dev.bbslight;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.SpriteAtlasTexture;

/** Cutout emissive pass, intentionally not deferred by BBS's translucent form queue. */
public final class EmissiveLayers extends RenderLayer {
    private static net.minecraft.client.gl.ShaderProgram emissiveShader;
    public static void setShader(net.minecraft.client.gl.ShaderProgram shader) { emissiveShader=shader; }
    private EmissiveLayers(String name, VertexFormat format, VertexFormat.DrawMode mode, int size,
                           boolean crumbling, boolean translucent, Runnable start, Runnable end) {
        super(name, format, mode, size, crumbling, translucent, start, end);
    }
    public static final RenderLayer BLOCK = of("bbs_light_emissive",
        VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS,
        256, false, false, MultiPhaseParameters.builder()
            .program(new net.minecraft.client.render.RenderPhase.ShaderProgram(() -> emissiveShader))
            .texture(new Texture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, false, false))
            .transparency(NO_TRANSPARENCY).cull(DISABLE_CULLING)
            .lightmap(DISABLE_LIGHTMAP).overlay(ENABLE_OVERLAY_COLOR)
            .writeMaskState(ALL_MASK).build(false));
}
