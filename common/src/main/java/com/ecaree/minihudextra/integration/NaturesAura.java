package com.ecaree.minihudextra.integration;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NaturesAura {
    @ExpectPlatform
    public static int getAura(World world, BlockPos pos) {
        throw new AssertionError();
    }
}