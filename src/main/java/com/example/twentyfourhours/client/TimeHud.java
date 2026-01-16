package com.example.twentyfourhours.client;

import com.example.twentyfourhours.TimeManager;
import com.example.twentyfourhours.config.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.world.World;

public class TimeHud {

    private String currentTime = "00:00:00";
    private int tickCounter = 0;

    public void updateTime(World world) {
        // Update every tick for smooth display
        tickCounter++;
        if (tickCounter >= 1) { // Update every tick for real-time accuracy
            currentTime = TimeManager.getFormattedTime(world);
            tickCounter = 0;
        }
    }

    public void render(DrawContext context, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) {
            return;
        }

        ModConfig config = ModConfig.getInstance();
        TextRenderer textRenderer = client.textRenderer;

        // Get current time string
        String timeText = currentTime;

        // Calculate text dimensions
        int textWidth = textRenderer.getWidth(timeText);
        int textHeight = textRenderer.fontHeight;

        // Apply scale
        float scale = config.clockScale;
        int scaledWidth = (int) (textWidth * scale);
        int scaledHeight = (int) (textHeight * scale);

        // Position
        int x = config.clockX;
        int y = config.clockY;

        // Save matrix state
        context.getMatrices().push();
        context.getMatrices().translate(x, y, 0);
        context.getMatrices().scale(scale, scale, 1.0f);

        // Draw background if enabled
        if (config.showBackground) {
            int padding = 4;
            context.fill(
                    -padding,
                    -padding,
                    textWidth + padding,
                    textHeight + padding,
                    config.backgroundColor
            );
        }

        // Draw text with shadow
        context.drawTextWithShadow(
                textRenderer,
                timeText,
                0,
                0,
                config.clockColor
        );

        // Restore matrix state
        context.getMatrices().pop();
    }
}