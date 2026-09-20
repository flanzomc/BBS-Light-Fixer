package dev.bbslight.cml;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
public final class LightPort implements ClientModInitializer {
 public void onInitializeClient() {
  PortShaders.register();
  ClientTickEvents.END_CLIENT_TICK.register(client->{
   PortLights.tick();
   if(Boolean.getBoolean("bbsLight.selfTest") && client.currentScreen!=null && client.getOverlay()==null && PortShaders.model!=null) {
    StartupCheck.run();client.scheduleStop();
   }
  });
 }
}
