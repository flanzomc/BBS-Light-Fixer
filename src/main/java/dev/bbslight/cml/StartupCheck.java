package dev.bbslight.cml;
import mchorse.bbs_mod.forms.forms.BlockForm;
/** Opt-in CI check. Never runs during normal play. */
final class StartupCheck {
 static void run() {
  try {
   for(String name:new String[]{"forms.renderers.ModelFormRenderer","forms.renderers.BlockFormRenderer","client.renderer.ModelBlockEntityRenderer","ui.forms.editors.panels.UIFormPanel","forms.FormTranslucentQueue$ModelVAOCommand","forms.FormTranslucentQueue$BOBJCommand","forms.FormTranslucentQueue$RenderLayerCommand","forms.FormTranslucentQueue$VertexBufferCommand"}) Class.forName("mchorse.bbs_mod."+name);
   BlockForm a=new BlockForm(),b=new BlockForm();
   EffectValues.of(a).glow.set(.4F);EffectValues.of(a).brightness.set(.25F);EffectValues.of(a).level.set(7);EffectValues.of(a).masks[0].enabled.set(true);
   b.fromData(a.toData());
   if(EffectValues.of(b).glow.get()!=.4F || EffectValues.of(b).brightness.get()!=.25F || EffectValues.of(b).level.get()!=7 || !EffectValues.of(b).masks[0].enabled.get())throw new AssertionError("Effect values lost on form round trip");
   for(var shader:new net.minecraft.client.gl.ShaderProgram[]{PortShaders.model,PortShaders.block}) {
    if(shader==null)throw new AssertionError("Shader did not load");
    for(String name:new String[]{"FormColorGrade","GlowingColor","PaintColor","RootInverse"})if(shader.getUniform(name)==null)throw new AssertionError("Missing uniform "+name);
    for(String prefix:new String[]{"Glow","Paint","GradeBrightness","GradeContrast","GradeSaturation","GradeHue"})for(String suffix:new String[]{"Inverse","Active","Shape"})if(shader.getUniform(prefix+suffix)==null)throw new AssertionError("Missing mask uniform "+prefix+suffix);
   }
   System.out.println("BBS LIGHT PORT STARTUP CHECK PASSED");
  } catch(Throwable e) { throw new IllegalStateException("BBS light port startup check failed",e); }
 }
}
