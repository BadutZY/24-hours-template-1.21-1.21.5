package com.example.twentyfourhours;

import com.example.twentyfourhours.config.ModConfig;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TimeManager {

    private static final long MINECRAFT_TICKS_PER_DAY = 24000L;
    private static final long REAL_MS_PER_DAY = 86400000L; // 24 hours in ms

    /**
     * Update waktu server berdasarkan waktu nyata
     * Dipanggil setiap tick dari TwentyFourHoursMod
     */
    public static void updateServerTime(MinecraftServer server) {
        ModConfig config = ModConfig.getInstance();

        if (!config.enabled) {
            return;
        }

        long newTime = calculateRealWorldTime(config);

        for (ServerWorld world : server.getWorlds()) {
            if (world.getRegistryKey() == World.OVERWORLD) {
                // Dapatkan current day count (world age / 24000)
                long currentWorldTime = world.getTimeOfDay();
                long currentDay = currentWorldTime / MINECRAFT_TICKS_PER_DAY;

                // Hitung world time baru dengan mempertahankan day count
                long newWorldTime = (currentDay * MINECRAFT_TICKS_PER_DAY) + newTime;

                // Set waktu baru
                world.setTimeOfDay(newWorldTime);

                TwentyFourHoursMod.LOGGER.debug("Updated time - Day: {}, Time: {}, Total: {}",
                        currentDay, newTime, newWorldTime);
            }
        }
    }

    /**
     * Hitung waktu Minecraft berdasarkan waktu nyata
     */
    public static long calculateRealWorldTime(ModConfig config) {
        long totalSeconds;

        // Tentukan mode mana yang digunakan
        if (config.isCountryMode()) {
            // Mode Country Timezone - gunakan waktu nyata dari negara
            ZoneId zoneId = getZoneIdForCountry(config.selectedCountry);
            ZonedDateTime now = ZonedDateTime.now(zoneId);

            int hour = now.getHour();
            int minute = now.getMinute();
            int second = now.getSecond();

            totalSeconds = hour * 3600L + minute * 60L + second;
        } else {
            // Mode Manual - game mulai pada waktu yang ditentukan
            totalSeconds = config.manualStartHour * 3600L + config.manualStartMinute * 60L;

            // Tambahkan detik saat ini untuk progres smooth
            ZonedDateTime now = ZonedDateTime.now();
            totalSeconds += now.getSecond();
        }

        // Convert ke Minecraft ticks (24000 ticks per 24 jam)
        long minecraftTicks = (totalSeconds * MINECRAFT_TICKS_PER_DAY) / (24 * 60 * 60);

        // Minecraft hari dimulai jam 6:00 AM (tick 0), jadi offset 6 jam
        long offset = (6 * 1000); // 6 jam dalam Minecraft ticks
        minecraftTicks = (minecraftTicks - offset + MINECRAFT_TICKS_PER_DAY) % MINECRAFT_TICKS_PER_DAY;

        return minecraftTicks;
    }

    public static long calculateMinecraftTime(World world) {
        if (world == null) {
            return 0;
        }

        long timeOfDay = world.getTimeOfDay() % MINECRAFT_TICKS_PER_DAY;
        return timeOfDay;
    }

    public static String getFormattedTime(World world) {
        ModConfig config = ModConfig.getInstance();

        if (!config.enabled) {
            // Minecraft time mode (20 min per day)
            long minecraftTime = calculateMinecraftTime(world);
            return formatMinecraftTime(minecraftTime, config);
        } else {
            // Real world time mode
            int hour, minute, second;

            if (config.isCountryMode()) {
                // Use country timezone
                ZoneId zoneId = getZoneIdForCountry(config.selectedCountry);
                ZonedDateTime now = ZonedDateTime.now(zoneId);
                hour = now.getHour();
                minute = now.getMinute();
                second = now.getSecond();
            } else {
                // Use manual start time (fixed time)
                hour = config.manualStartHour;
                minute = config.manualStartMinute;
                second = ZonedDateTime.now().getSecond(); // Only seconds progress
            }

            return formatTime(hour, minute, second, config);
        }
    }

    private static String formatTime(int hour, int minute, int second, ModConfig config) {
        if (config.is24HourFormat()) {
            // 24-hour format: 00:00:00 - 23:59:59
            return String.format("%02d:%02d:%02d", hour, minute, second);
        } else {
            // 12-hour format with AM/PM: 12:00:00 AM - 11:59:59 PM
            String period = hour >= 12 ? "PM" : "AM";
            int displayHour = hour % 12;
            if (displayHour == 0) displayHour = 12; // 0 -> 12
            return String.format("%02d:%02d:%02d %s", displayHour, minute, second, period);
        }
    }

    private static String formatMinecraftTime(long ticks, ModConfig config) {
        // Convert ticks to Minecraft time
        // 0 ticks = 6:00 AM, 6000 = 12:00 PM, 12000 = 6:00 PM, 18000 = 12:00 AM
        long adjustedTicks = (ticks + 6000) % MINECRAFT_TICKS_PER_DAY;

        int totalMinutes = (int) ((adjustedTicks * 24 * 60) / MINECRAFT_TICKS_PER_DAY);
        int hour = totalMinutes / 60;
        int minute = totalMinutes % 60;
        int second = (int) (((adjustedTicks * 24 * 60 * 60) / MINECRAFT_TICKS_PER_DAY) % 60);

        return formatTime(hour, minute, second, config);
    }

    private static ZoneId getZoneIdForCountry(String country) {
        return switch (country) {
            case "Indonesia" -> ZoneId.of("Asia/Jakarta");
            case "United States" -> ZoneId.of("America/New_York");
            case "United Kingdom" -> ZoneId.of("Europe/London");
            case "Japan" -> ZoneId.of("Asia/Tokyo");
            case "China" -> ZoneId.of("Asia/Shanghai");
            case "Germany" -> ZoneId.of("Europe/Berlin");
            case "France" -> ZoneId.of("Europe/Paris");
            case "Brazil" -> ZoneId.of("America/Sao_Paulo");
            case "Australia" -> ZoneId.of("Australia/Sydney");
            case "Canada" -> ZoneId.of("America/Toronto");
            case "India" -> ZoneId.of("Asia/Kolkata");
            case "Russia" -> ZoneId.of("Europe/Moscow");
            case "South Korea" -> ZoneId.of("Asia/Seoul");
            case "Mexico" -> ZoneId.of("America/Mexico_City");
            case "Spain" -> ZoneId.of("Europe/Madrid");
            case "Italy" -> ZoneId.of("Europe/Rome");
            case "Netherlands" -> ZoneId.of("Europe/Amsterdam");
            case "Sweden" -> ZoneId.of("Europe/Stockholm");
            case "Turkey" -> ZoneId.of("Europe/Istanbul");
            case "Poland" -> ZoneId.of("Europe/Warsaw");
            default -> ZoneId.systemDefault();
        };
    }
}