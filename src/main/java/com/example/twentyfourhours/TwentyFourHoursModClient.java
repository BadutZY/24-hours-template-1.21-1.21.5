package com.example.twentyfourhours;

import com.example.twentyfourhours.client.KeybindingHandler;
import com.example.twentyfourhours.client.TimeHud;
import com.example.twentyfourhours.config.ModConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class TwentyFourHoursModClient implements ClientModInitializer {

    private static TimeHud timeHud;

    @Override
    public void onInitializeClient() {
        TwentyFourHoursMod.LOGGER.info("24 Hours MOD Client Initializing...");

        // Load config
        ModConfig.load();

        // Initialize keybindings
        KeybindingHandler.register();

        // Initialize HUD
        timeHud = new TimeHud();

        // Register HUD rendering
        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> {
            if (ModConfig.getInstance().showClock) {
                timeHud.render(drawContext, tickCounter.getTickDelta(true));
            }
        });

        // Update time every client tick
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world != null && ModConfig.getInstance().enabled) {
                timeHud.updateTime(client.world);
            }
        });

        TwentyFourHoursMod.LOGGER.info("24 Hours MOD Client Initialized!");
    }

    public static TimeHud getTimeHud() {
        return timeHud;
    }
}