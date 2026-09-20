package dev.flanzomc.bbslightfixer.test;

import dev.flanzomc.bbslightfixer.access.CmlBlockAccess;
import dev.flanzomc.bbslightfixer.access.CmlColorAccess;
import dev.flanzomc.bbslightfixer.access.CmlFormAccess;
import dev.flanzomc.bbslightfixer.light.DynamicLightBridge;
import dev.flanzomc.bbslightfixer.render.EffectShaders;
import dev.flanzomc.bbslightfixer.render.EffectState;
import mchorse.bbs_mod.data.types.MapType;
import mchorse.bbs_mod.forms.forms.BlockForm;
import mchorse.bbs_mod.forms.forms.ModelForm;
import mchorse.bbs_mod.forms.forms.utils.GlowSettings;
import mchorse.bbs_mod.forms.forms.utils.PaintSettings;
import mchorse.bbs_mod.forms.forms.utils.StructureLightSettings;
import net.minecraft.block.Blocks;

public final class StartupCheck {
 private static boolean done;
 private StartupCheck(){}
 public static void run(){
  if(done)return;done=true;
  try{
   ModelForm model=new ModelForm();
   CmlFormAccess f=(CmlFormAccess)(Object)model;
   GlowSettings g=f.bbsLight$getGlowSettings().get().copy();g.intensity=.6F;g.r=.2F;g.g=.7F;g.b=1F;g.transform.scaleX=.75F;f.bbsLight$getGlowSettings().set(g);
   PaintSettings p=f.bbsLight$getPaintSettings().get().copy();p.intensity=.35F;p.r=1F;p.g=.1F;p.b=.2F;f.bbsLight$getPaintSettings().set(p);
   CmlColorAccess color=(CmlColorAccess)(Object)model.color.get();color.bbsLight$setBrightness(.2F);color.bbsLight$setContrast(.15F);color.bbsLight$setHue(35F);color.bbsLight$setSaturation(.3F);
   color.bbsLight$getBrightnessTransform().scaleY=.8F;

   MapType saved=model.toData().asMap();
   if(!saved.has("glow")||!saved.has("paint")||saved.has("cml_effects"))throw new AssertionError("Form is not using CML-compatible glow/paint keys");
   ModelForm round=new ModelForm();round.fromData(saved);
   CmlFormAccess rf=(CmlFormAccess)(Object)round;CmlColorAccess rc=(CmlColorAccess)(Object)round.color.get();
   if(Math.abs(rf.bbsLight$getGlowSettings().get().intensity-.6F)>.001F||Math.abs(rf.bbsLight$getPaintSettings().get().intensity-.35F)>.001F||Math.abs(rc.bbsLight$getBrightness()-.2F)>.001F)throw new AssertionError("CML form values failed round-trip");
   EffectState state=EffectState.model(round,null,false,false);
   if(!state.active||Math.abs(state.glow[3]-.6F)>.001F||state.brightnessMask.active<.5F)throw new AssertionError("Effect render state is not live");

   BlockForm block=new BlockForm();block.blockState.set(Blocks.TORCH.getDefaultState());
   CmlBlockAccess ba=(CmlBlockAccess)(Object)block;StructureLightSettings sl=ba.bbsLight$getStructureLight().get().copy();sl.enabled=true;sl.intensity=7;ba.bbsLight$getStructureLight().set(sl);
   if(DynamicLightBridge.luminance(block)!=7)throw new AssertionError("CML block-light cap semantics failed");

   for(var shader:new net.minecraft.client.gl.ShaderProgram[]{EffectShaders.model,EffectShaders.block}){
    if(shader==null)throw new AssertionError("Effect shader missing");
    for(String n:new String[]{"PaintColor","GlowingColor","FormColorGrade","FormColorTint","FormRootInverse","PaintMaskHalf","GlowMaskHalf","GradeBrightnessHalf"})
     if(shader.getUniform(n)==null)throw new AssertionError("Missing shader uniform "+n);
   }
   System.out.println("BBS LIGHT FIXER CLEAN CML PORT STARTUP CHECK PASSED");
  }catch(Throwable t){throw new IllegalStateException("BBS Light Fixer clean CML port self-test failed",t);}
 }
}
