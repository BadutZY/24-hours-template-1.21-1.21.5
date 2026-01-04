package com.example.craftable;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CraftableItems implements ModInitializer {
    public static final String MOD_ID = "craftable_items";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Craftable Items mod initialized!");

        // Register custom recipes
        CraftableRecipeManager.registerRecipes();

        // Debug logging
        LOGGER.info("Custom recipes registered:");

        // Event untuk debugging
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            LOGGER.info("Server started");
        });
    }
}