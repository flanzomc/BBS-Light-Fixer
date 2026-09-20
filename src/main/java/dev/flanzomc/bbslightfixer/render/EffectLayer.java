package dev.flanzomc.bbslightfixer.render;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.SpriteAtlasTexture;
public final class EffectLayer extends RenderLayer{
 private EffectLayer(String n,VertexFormat f,VertexFormat.DrawMode m,int s,boolean c,boolean t,Runnable a,Runnable b){super(n,f,m,s,c,t,a,b);}
 public static final RenderLayer BLOCK=of("bbs_light_fixer_cml_block_effects",VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,VertexFormat.DrawMode.QUADS,256,false,true,
  MultiPhaseParameters.builder().program(new ShaderProgram(()->EffectShaders.block)).texture(new Texture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE,false,false))
  .transparency(TRANSLUCENT_TRANSPARENCY).cull(DISABLE_CULLING).lightmap(ENABLE_LIGHTMAP).overlay(ENABLE_OVERLAY_COLOR).build(false));
}
