package com.ecaree.minihudextra.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.BooleanSupplier;

public final class UltimineMiniHUDHandler {
    private static boolean disabledByHandler = false;

    private static boolean ftbResolved = false;
    private static BooleanSupplier isUltimineDown = () -> false;

    private static boolean hudResolved = false;
    private static Object mainRenderingToggle = null;
    private static Method getBooleanValueMethod = null;
    private static Method setBooleanValueMethod = null;

    private UltimineMiniHUDHandler() {
    }

    public static void clientTick(Object client) {
        if (!isFeatureEnabled()
                || client == null
                || !hasPlayer(client)
                || !isFtbUltimineDown()) {
            restoreHudIfNeeded();
            return;
        }

        disableHudIfNeeded();
    }

    private static boolean isFeatureEnabled() {
        return com.ecaree.minihudextra.config.Configs.Generic.FTB_ULTIMINE_SUPPORT.getBooleanValue();
    }

    private static boolean hasPlayer(Object client) {
        try {
            Field playerField = client.getClass().getField("player");
            return playerField.get(client) != null;
        } catch (Throwable ignored) {
            return true;
        }
    }

    private static boolean isFtbUltimineDown() {
        if (!ftbResolved) {
            resolveFtbUltimineKeybind();
        }

        return isUltimineDown.getAsBoolean();
    }

    private static void resolveFtbUltimineKeybind() {
        ftbResolved = true;

        try {
            Class<?> clientClass = Class.forName(
                    "dev.ftb.mods.ftbultimine.client.FTBUltimineClient",
                    false,
                    UltimineMiniHUDHandler.class.getClassLoader()
            );

            Field keyField = findField(clientClass, "keyBindUltimine", "keyMappingUltimine", "ultimineKey");
            Object keyBind = keyField.get(null);

            Method pressedMethod = findBooleanMethod(keyBind.getClass(), "isDown", "isPressed");

            isUltimineDown = () -> {
                try {
                    return Boolean.TRUE.equals(pressedMethod.invoke(keyBind));
                } catch (Throwable ignored) {
                    return false;
                }
            };
        } catch (Throwable ignored) {
            isUltimineDown = () -> false;
        }
    }

    private static void disableHudIfNeeded() {
        if (!resolveHudToggle()) {
            return;
        }

        try {
            boolean enabled = Boolean.TRUE.equals(getBooleanValueMethod.invoke(mainRenderingToggle));

            if (!disabledByHandler && enabled) {
                setBooleanValueMethod.invoke(mainRenderingToggle, false);
                disabledByHandler = true;
            }
        } catch (Throwable ignored) {
        }
    }

    private static void restoreHudIfNeeded() {
        if (!disabledByHandler) {
            return;
        }

        if (!resolveHudToggle()) {
            disabledByHandler = false;
            return;
        }

        try {
            setBooleanValueMethod.invoke(mainRenderingToggle, true);
        } catch (Throwable ignored) {
        } finally {
            disabledByHandler = false;
        }
    }

    private static boolean resolveHudToggle() {
        if (hudResolved) {
            return mainRenderingToggle != null
                    && getBooleanValueMethod != null
                    && setBooleanValueMethod != null;
        }

        hudResolved = true;

        try {
            Class<?> genericConfigsClass = Class.forName(
                    "fi.dy.masa.minihud.config.Configs$Generic",
                    false,
                    UltimineMiniHUDHandler.class.getClassLoader()
            );

            Field toggleField = genericConfigsClass.getField("MAIN_RENDERING_TOGGLE");
            mainRenderingToggle = toggleField.get(null);

            getBooleanValueMethod = mainRenderingToggle.getClass().getMethod("getBooleanValue");
            setBooleanValueMethod = mainRenderingToggle.getClass().getMethod("setBooleanValue", boolean.class);

            return true;
        } catch (Throwable ignored) {
            mainRenderingToggle = null;
            getBooleanValueMethod = null;
            setBooleanValueMethod = null;
            return false;
        }
    }

    private static Field findField(Class<?> type, String... names) throws NoSuchFieldException {
        for (String name : names) {
            try {
                Field field = type.getField(name);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException ignored) {
            }
        }

        throw new NoSuchFieldException(String.join("/", names));
    }

    private static Method findBooleanMethod(Class<?> type, String... names) throws NoSuchMethodException {
        for (String name : names) {
            try {
                Method method = type.getMethod(name);
                method.setAccessible(true);
                return method;
            } catch (NoSuchMethodException ignored) {
            }
        }

        throw new NoSuchMethodException(String.join("/", names));
    }
}
