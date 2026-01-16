package com.example.twentyfourhours.mixin;

import com.example.twentyfourhours.config.ModConfig;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {

    @Shadow
    public abstract List<ServerPlayerEntity> getPlayers();

    /**
     * Mencegah waktu di-skip saat pemain tidur
     * Dan memastikan pemain tidur terus sampai benar-benar pagi
     */
    @Inject(method = "wakeSleepingPlayers", at = @At("HEAD"), cancellable = true)
    private void onWakeSleepingPlayers(CallbackInfo ci) {
        ModConfig config = ModConfig.getInstance();

        if (config.enabled) {
            ServerWorld world = (ServerWorld) (Object) this;

            // Dapatkan waktu dalam hari (0-24000 ticks)
            // 0 = 6:00 AM, 6000 = 12:00 PM, 12000 = 6:00 PM, 18000 = 12:00 AM, 23999 = 5:59 AM
            long timeOfDay = world.getTimeOfDay() % 24000;

            // Definisi pagi: dari tick 23500 (sekitar 5:52 AM) sampai tick 500 (sekitar 6:02 AM)
            // Ini memberikan window kecil untuk pemain bangun di pagi hari
            boolean isMorning = timeOfDay >= 23500 || timeOfDay <= 500;

            if (!isMorning) {
                // Belum pagi, PAKSA pemain tetap tidur
                // Cancel method ini agar pemain tidak bangun
                ci.cancel();

                // Log untuk debug (opsional, bisa dihapus jika tidak perlu)
                // TwentyFourHoursMod.LOGGER.debug("Players still sleeping. Time: {}", timeOfDay);
            } else {
                // Sudah pagi, izinkan pemain bangun
                // Biarkan method vanilla berjalan normal
                // TwentyFourHoursMod.LOGGER.debug("Morning arrived! Waking players. Time: {}", timeOfDay);
            }
        }
    }

    /**
     * Mencegah reset cuaca saat tidur
     * Biarkan cuaca tetap berjalan normal
     */
    @Inject(method = "resetWeather", at = @At("HEAD"), cancellable = true)
    private void onResetWeather(CallbackInfo ci) {
        ModConfig config = ModConfig.getInstance();

        if (config.enabled) {
            // Jangan reset cuaca saat 24 Hours Mode aktif
            // Biarkan cuaca berlanjut secara alami
            ci.cancel();
        }
    }
}