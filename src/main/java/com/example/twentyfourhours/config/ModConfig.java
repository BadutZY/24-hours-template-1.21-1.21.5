package com.example.twentyfourhours.config;

import com.example.twentyfourhours.TwentyFourHoursMod;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ModConfig {

    private static ModConfig INSTANCE;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File(
            FabricLoader.getInstance().getConfigDir().toFile(),
            "24hours-config.json"
    );

    // Listeners untuk perubahan config
    private static final List<Consumer<ModConfig>> changeListeners = new ArrayList<>();

    // Main settings
    public boolean enabled = false;

    // Time mode selection (only one can be active)
    public TimeMode timeMode = TimeMode.MANUAL_START_HOUR;

    // Manual start time settings (when game starts at this time)
    public int manualStartHour = 19;    // 0-23
    public int manualStartMinute = 0;   // 0-59

    // Country timezone settings
    public String selectedCountry = "Indonesia";

    // Clock format
    public ClockFormat clockFormat = ClockFormat.HOUR_24;

    // UI Settings
    public boolean showClock = true;
    public int clockX = 10;
    public int clockY = 10;
    public int clockColor = 0xFFFFFF;
    public float clockScale = 1.0f;
    public boolean showBackground = true;
    public int backgroundColor = 0x80000000;

    public enum TimeMode {
        MANUAL_START_HOUR,  // Game starts at specified time
        COUNTRY_TIMEZONE    // Use country timezone
    }

    public enum ClockFormat {
        HOUR_24,  // 00:00 - 23:59 (Indonesian/European style)
        HOUR_12   // 12:00 AM - 11:59 PM (American style)
    }

    public static ModConfig getInstance() {
        if (INSTANCE == null) {
            load();
        }
        return INSTANCE;
    }

    public static void load() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                INSTANCE = GSON.fromJson(reader, ModConfig.class);
                TwentyFourHoursMod.LOGGER.info("Config loaded successfully");
            } catch (IOException e) {
                TwentyFourHoursMod.LOGGER.error("Failed to load config", e);
                INSTANCE = new ModConfig();
            }
        } else {
            INSTANCE = new ModConfig();
            save();
        }
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(INSTANCE, writer);
            TwentyFourHoursMod.LOGGER.info("Config saved successfully");

            // Trigger listeners setelah save
            notifyListeners();
        } catch (IOException e) {
            TwentyFourHoursMod.LOGGER.error("Failed to save config", e);
        }
    }

    /**
     * Register listener untuk perubahan config
     */
    public static void addChangeListener(Consumer<ModConfig> listener) {
        changeListeners.add(listener);
    }

    /**
     * Notify semua listeners bahwa config berubah
     */
    private static void notifyListeners() {
        ModConfig config = getInstance();
        for (Consumer<ModConfig> listener : changeListeners) {
            try {
                listener.accept(config);
            } catch (Exception e) {
                TwentyFourHoursMod.LOGGER.error("Error in config change listener", e);
            }
        }
    }

    public void toggleEnabled() {
        this.enabled = !this.enabled;
        save();
    }

    public void toggleShowClock() {
        this.showClock = !this.showClock;
        save();
    }

    public void setTimeMode(TimeMode mode) {
        this.timeMode = mode;
        save();
    }

    public void setManualStartTime(int hour, int minute) {
        this.manualStartHour = Math.max(0, Math.min(23, hour));
        this.manualStartMinute = Math.max(0, Math.min(59, minute));
        save();
    }

    public void setSelectedCountry(String country) {
        this.selectedCountry = country;
        save();
    }

    public void setClockFormat(ClockFormat format) {
        this.clockFormat = format;
        save();
    }

    public boolean isManualMode() {
        return timeMode == TimeMode.MANUAL_START_HOUR;
    }

    public boolean isCountryMode() {
        return timeMode == TimeMode.COUNTRY_TIMEZONE;
    }

    public boolean is24HourFormat() {
        return clockFormat == ClockFormat.HOUR_24;
    }

    public boolean is12HourFormat() {
        return clockFormat == ClockFormat.HOUR_12;
    }
}