package com.ecaree.minihudextra.neoforge;

import com.ecaree.minihudextra.MiniHUDExtra;
import com.ecaree.minihudextra.config.GuiConfigs;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(MiniHUDExtra.MOD_ID)
public class MiniHUDExtraNeoForge {
    public MiniHUDExtraNeoForge(ModContainer modContainer) {
        MiniHUDExtra.init();

        modContainer.registerExtensionPoint(
                IConfigScreenFactory.class,
                (minecraft, screen) -> {
                    GuiConfigs gui = new GuiConfigs();
                    gui.setParent(screen);
                    return gui;
                }
        );
    }
}