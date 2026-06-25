package com.ecaree.minihudextra;

import com.ecaree.minihudextra.util.UltimineMiniHUDHandler;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.platform.Platform;
import fi.dy.masa.malilib.event.InitializationHandler;

public class MiniHUDExtra {
    public static final String MOD_ID = "minihudextra";
    public static final String MOD_NAME = "MiniHUDExtra";

    public static void init() {
        InitializationHandler.getInstance().registerInitializationHandler(new InitHandler());

        if (Platform.isModLoaded("ftbultimine")) {
            ClientTickEvent.CLIENT_PRE.register(UltimineMiniHUDHandler::clientTick);
        }
    }
}