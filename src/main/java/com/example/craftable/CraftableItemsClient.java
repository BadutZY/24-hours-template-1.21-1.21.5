package com.example.craftable;

import net.fabricmc.api.ClientModInitializer;

public class CraftableItemsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        CraftableItems.LOGGER.info("Craftable Items client initialized!");
    }
}