package com.ecaree.minihudextra.integration;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.entity.player.PlayerEntity;

public class BloodMagic {
    @ExpectPlatform
    public static int getLP(PlayerEntity player) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getOrbTier(PlayerEntity player) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static String getOrbTierName(PlayerEntity player) {
        throw new AssertionError();
    }
}