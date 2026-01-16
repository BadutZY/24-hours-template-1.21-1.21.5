package com.example.twentyfourhours.mixin;

import com.example.twentyfourhours.config.ModConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {

    /**
     * Hook untuk trySleep - saat ini hanya observasi
     * Logic utama pencegahan skip waktu ada di ServerWorldMixin
     *
     * Bisa dikembangkan lebih lanjut jika perlu modifikasi perilaku tidur
     */
    @Inject(method = "trySleep", at = @At("HEAD"))
    private void onTrySleep(BlockPos pos, CallbackInfoReturnable<PlayerEntity.SleepFailureReason> cir) {
        ModConfig config = ModConfig.getInstance();

        if (config.enabled) {
            // Mod aktif - pemain akan tidur normal
            // Waktu tidak akan di-skip (dihandle oleh ServerWorldMixin)
            // Pemain akan bangun otomatis saat pagi
        }
    }
}