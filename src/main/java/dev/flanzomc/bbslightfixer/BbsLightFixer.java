package dev.flanzomc.bbslightfixer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public final class BbsLightFixer implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        CmlShaders.register();

        ClientTickEvents.END_CLIENT_TICK.register(client ->
        {
            CmlDynamicLights.tick();

            if (Boolean.getBoolean("bbsLight.selfTest")
                && client.currentScreen != null
                && client.getOverlay() == null
                && CmlShaders.model != null
                && CmlShaders.block != null)
            {
                StartupCheck.run();
                client.scheduleStop();
            }
        });
    }
}
