package com.ecaree.minihudextra.integration;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.entity.player.PlayerEntity;

public class MekRadiation {
    @ExpectPlatform
    public static String getRadiationString(PlayerEntity player) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static String getDecayTime(PlayerEntity player) {
        throw new AssertionError();
    }
}