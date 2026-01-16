package com.example.twentyfourhours.client;

import com.example.twentyfourhours.TwentyFourHoursMod;
import com.example.twentyfourhours.config.ModMenuIntegration;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KeybindingHandler {

    private static KeyBinding openConfigKey;

    public static void register() {
        // Register keybinding (default: O key)
        openConfigKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.twentyfourhours.openConfig",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_O,
                "category.twentyfourhours"
        ));

        // Register tick event to check for key press
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (openConfigKey.wasPressed()) {
                openConfigScreen(client);
            }
        });
    }

    private static void openConfigScreen(MinecraftClient client) {
        if (client.currentScreen == null) {
            // Only open if no screen is currently open
            ModMenuIntegration integration = new ModMenuIntegration();
            client.setScreen(integration.getModConfigScreenFactory().create(null));

            // Log untuk debugging
            TwentyFourHoursMod.LOGGER.debug("Config screen opened via keybinding");
        }
    }
}