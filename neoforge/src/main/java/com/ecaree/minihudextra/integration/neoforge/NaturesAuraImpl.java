package com.ecaree.minihudextra.integration.neoforge;

import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import dev.architectury.platform.Platform;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.mutable.MutableInt;

@SuppressWarnings("unused")
public class NaturesAuraImpl {
    public static int getAura(World world, BlockPos pos) {
        if (Platform.isDevelopmentEnvironment()) { // 由于只加了 API 所以开发环境会崩溃。
            return 0;
        }

        var amount = new MutableInt(IAuraChunk.DEFAULT_AURA);
        // https://github.com/Ellpeck/NaturesAura/blob/main/src/main/java/de/ellpeck/naturesaura/events/ClientEvents.java#L85
        IAuraChunk.getSpotsInArea(world, pos, 35, (blockPos, drainSpot) -> amount.add(drainSpot));
        return amount.intValue();
    }
}