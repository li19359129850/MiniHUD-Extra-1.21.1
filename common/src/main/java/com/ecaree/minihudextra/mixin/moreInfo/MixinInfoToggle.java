package com.ecaree.minihudextra.mixin.moreInfo;

import com.ecaree.minihudextra.config.MHExInfoToggle;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import fi.dy.masa.minihud.config.InfoToggle;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(value = InfoToggle.class, remap = false)
public abstract class MixinInfoToggle {
    @Shadow
    @Final
    @Mutable
    private static InfoToggle[] $VALUES;

    @Invoker("<init>")
    private static InfoToggle minihudextra$newInfoToggle(
            String enumName,
            int enumOrdinal,
            String name,
            boolean defaultValue,
            boolean serverDataRequired,
            int linePosition,
            String defaultHotkey,
            String comment,
            KeybindSettings settings,
            String translatedName,
            String prettyName
    ) {
        throw new AssertionError();
    }

    @Inject(
            method = "<clinit>",
            at = @At(
                    value = "FIELD",
                    target = "Lfi/dy/masa/minihud/config/InfoToggle;$VALUES:[Lfi/dy/masa/minihud/config/InfoToggle;",
                    shift = At.Shift.AFTER
            )
    )
    private static void minihudextra$addCustomInfo(CallbackInfo ci) {
        List<InfoToggle> infos = new ArrayList<>(Arrays.asList($VALUES));

        int ordinal = infos.get(infos.size() - 1).ordinal() + 1;

        for (MHExInfoToggle info : MHExInfoToggle.values()) {
            if (info == MHExInfoToggle.WEATHER) {
                continue;
            }

            infos.add(minihudextra$newInfoToggle(
                    info.name(),
                    ordinal++,
                    info.getName(),
                    info.getDefaultBooleanValue(),
                    false,
                    info.getLinePosition(),
                    "",
                    info.getComment(),
                    KeybindSettings.DEFAULT,
                    info.getName(),
                    info.getPrettyName()
            ));
        }

        $VALUES = infos.toArray(new InfoToggle[0]);
    }
}
