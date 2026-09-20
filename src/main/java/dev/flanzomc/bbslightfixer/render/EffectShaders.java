package dev.flanzomc.bbslightfixer.render;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
public final class EffectShaders{
 public static ShaderProgram model,block;
 private EffectShaders(){}
 public static void register(){CoreShaderRegistrationCallback.EVENT.register(c->{
  c.register(new Identifier("bbs_light_fixer","model_effects"),VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,p->model=p);
  c.register(new Identifier("bbs_light_fixer","block_effects"),VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,p->block=p);
 });}
}
