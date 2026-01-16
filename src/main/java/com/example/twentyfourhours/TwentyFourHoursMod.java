package com.example.twentyfourhours;

import com.example.twentyfourhours.config.ModConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TwentyFourHoursMod implements ModInitializer {
    public static final String MOD_ID = "twentyfourhours";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static MinecraftServer currentServer;
    private static long lastUpdateTick = 0;
    private static final long UPDATE_INTERVAL = 10; // Update setiap 0.5 detik (10 ticks)

    @Override
    public void onInitialize() {
        LOGGER.info("24 Hours MOD Initializing...");

        // Load config
        ModConfig.load();

        // Register config change listener untuk instant sync
        ModConfig.addChangeListener(config -> {
            if (currentServer != null && config.enabled) {
                LOGGER.info("Config changed - forcing immediate time sync");
                // Force sync berkali-kali untuk memastikan berhasil
                for (int i = 0; i < 5; i++) {
                    TimeManager.updateServerTime(currentServer);
                }
            }
        });

        // Register server lifecycle events
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            currentServer = server;
            LOGGER.info("Server started - 24 Hours MOD active");

            // Jika mod sudah enabled saat server start, langsung sync
            if (ModConfig.getInstance().enabled) {
                LOGGER.info("Mod is enabled - performing initial time sync");
                for (int i = 0; i < 5; i++) {
                    TimeManager.updateServerTime(server);
                }
            }
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            currentServer = null;
        });

        // Register server tick event untuk update waktu
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (ModConfig.getInstance().enabled) {
                long currentTick = server.getTicks();

                // Update waktu setiap 10 ticks (0.5 detik) untuk performa
                if (currentTick - lastUpdateTick >= UPDATE_INTERVAL) {
                    TimeManager.updateServerTime(server);
                    lastUpdateTick = currentTick;
                }
            }
        });

        LOGGER.info("24 Hours MOD Initialized successfully!");
    }

    public static MinecraftServer getCurrentServer() {
        return currentServer;
    }

    /**
     * Force sync waktu sekarang juga
     * Dipanggil dari client saat config berubah
     */
    public static void forceSyncTime() {
        if (currentServer != null) {
            LOGGER.info("Force syncing time...");
            for (int i = 0; i < 5; i++) {
                TimeManager.updateServerTime(currentServer);
            }
            lastUpdateTick = currentServer.getTicks();
        }
    }
}