package dev.bbslight;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public final class LightFixer implements ClientModInitializer {
    public static volatile boolean enabled = true;
    @Override public void onInitializeClient() {
        CoreShaderRegistrationCallback.EVENT.register(context -> context.register(
            new Identifier("bbs_light_fixer", "emissive"),
            VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, EmissiveLayers::setShader));
        WorldRenderEvents.BEFORE_ENTITIES.register(context -> Lights.begin(context.matrixStack(), context.camera().getPos()));
        WorldRenderEvents.END.register(context -> Lights.end());
        ClientTickEvents.END_CLIENT_TICK.register(Lights::tick);
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, access) -> dispatcher.register(literal("bbslight")
            .executes(c -> {
                c.getSource().sendFeedback(Text.literal("BBS Light Fixer: " + (enabled ? "ON" : "OFF") + "; " + Lights.count() + " active lights. Name a form: Torch [light=15] [glow]. /bbslight toggle"));
                c.getSource().sendFeedback(Text.literal(Lights.diagnostic()));
                return 1;
            })
            .then(literal("toggle").executes(c -> {
                enabled = !enabled;
                Lights.clear();
                c.getSource().sendFeedback(Text.literal("BBS lights " + (enabled ? "enabled" : "disabled")));
                return 1;
            }))));
    }
}
