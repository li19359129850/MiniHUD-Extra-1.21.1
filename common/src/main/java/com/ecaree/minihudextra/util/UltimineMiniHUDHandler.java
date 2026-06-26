package com.ecaree.minihudextra.util;

import dev.ftb.mods.ftbultimine.client.FTBUltimineClient;
import fi.dy.masa.minihud.config.Configs;
import net.minecraft.client.MinecraftClient;

public class UltimineMiniHUDHandler {
    private static boolean wasEnabledInitially = false;

    public static void clientTick(MinecraftClient client) {
        if (!com.ecaree.minihudextra.config.Configs.Generic.FTB_ULTIMINE_SUPPORT.getBooleanValue()) {
            restoreMiniHudIfNeeded();
            return;
        }

        if (client == null || client.player == null || FTBUltimineClient.keyBindUltimine == null) {
            restoreMiniHudIfNeeded();
            return;
        }

        boolean ultimineActive = FTBUltimineClient.keyBindUltimine.isPressed();

        if (ultimineActive) {
            disableMiniHudIfNeeded();
        } else {
            restoreMiniHudIfNeeded();
        }
    }

    private static void disableMiniHudIfNeeded() {
        if (!wasEnabledInitially) {
            wasEnabledInitially = Configs.Generic.MAIN_RENDERING_TOGGLE.getBooleanValue();

            if (wasEnabledInitially) {
                Configs.Generic.MAIN_RENDERING_TOGGLE.setBooleanValue(false);
            }
        }
    }

    private static void restoreMiniHudIfNeeded() {
        if (wasEnabledInitially) {
            Configs.Generic.MAIN_RENDERING_TOGGLE.setBooleanValue(true);
            wasEnabledInitially = false;
        }
    }
}
