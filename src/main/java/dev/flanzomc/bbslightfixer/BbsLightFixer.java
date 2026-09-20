package dev.flanzomc.bbslightfixer;

import dev.flanzomc.bbslightfixer.light.DynamicLightBridge;
import dev.flanzomc.bbslightfixer.render.EffectShaders;
import dev.flanzomc.bbslightfixer.test.StartupCheck;
import mchorse.bbs_mod.utils.keyframes.factories.KeyframeFactories;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public final class BbsLightFixer implements ClientModInitializer {
 @Override public void onInitializeClient(){
  KeyframeFactories.FACTORIES.put("structure_light_settings",CmlFactories.STRUCTURE_LIGHT);
  KeyframeFactories.FACTORIES.put("glow_settings",CmlFactories.GLOW);
  KeyframeFactories.FACTORIES.put("glow",CmlFactories.GLOW);
  KeyframeFactories.FACTORIES.put("paint_settings",CmlFactories.PAINT);
  KeyframeFactories.FACTORIES.put("paint",CmlFactories.PAINT);
  EffectShaders.register();
  ClientTickEvents.END_CLIENT_TICK.register(client->{
   DynamicLightBridge.tick();
   if(Boolean.getBoolean("bbsLight.selfTest")&&client.currentScreen!=null&&client.getOverlay()==null&&EffectShaders.model!=null){
    StartupCheck.run();client.scheduleStop();
   }
  });
 }
}
