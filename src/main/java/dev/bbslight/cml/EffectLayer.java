package dev.bbslight.cml;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.SpriteAtlasTexture;
public final class EffectLayer extends RenderLayer {
    private EffectLayer(String n,VertexFormat f,VertexFormat.DrawMode m,int size,boolean c,boolean t,Runnable s,Runnable e) { super(n,f,m,size,c,t,s,e); }
    public static final RenderLayer BLOCK=of("bbs_cml_translucent_effects",VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
        VertexFormat.DrawMode.QUADS,256,false,true,MultiPhaseParameters.builder()
        .program(new ShaderProgram(()->PortShaders.block)).texture(new Texture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE,false,false))
        .transparency(TRANSLUCENT_TRANSPARENCY).cull(DISABLE_CULLING).lightmap(ENABLE_LIGHTMAP).overlay(ENABLE_OVERLAY_COLOR).build(false));
}
