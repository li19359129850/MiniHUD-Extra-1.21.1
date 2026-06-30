package com.ecaree.minihudextra.util;

import com.ecaree.minihudextra.config.Configs;

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

    /**
     * Architectury ClientTickEvent entry.
     *
     * FTB_ULTIMINE_SUPPORT = true:
     *   Ultimine 激活时临时关闭 MiniHUD/BoccHUD 主显示，松开后恢复。
     *
     * FTB_ULTIMINE_SUPPORT = false:
     *   不关闭 MiniHUD/BoccHUD。
     *   但是 render mixin 会在 Ultimine 激活期间绕过 MiniHUD Extra 自定义颜色/描边渲染，
     *   避免 MiniHUD Extra 的背景/描边绘制造成 FTB HUD 下的残留背景阴影。
     */
    public static void clientTick(Object client) {
        if (!isFeatureEnabled() || client == null || !hasPlayer(client) || !isFtbUltimineDown()) {
            restoreHudIfNeeded();
            return;
        }

        disableHudIfNeeded();
    }

    /**
     * 给 MiniHUD Extra 渲染 mixin 使用。
     *
     * 注意：
     * 这里不是“隐藏 BoccHUD”。
     * 这里只表示：
     *   FTB 支持配置关闭时，如果 Ultimine 正在按住，
     *   暂时不要走 MiniHUD Extra 自己的颜色/描边/背景绘制路径，
     *   而是回退到 BoccHUD/MaLiLib 原版 renderText。
     */
    public static boolean shouldBypassMiniHUDExtraRenderStyleForUltimine() {
        if (isFeatureEnabled()) {
            return false;
        }

        try {
            return isFtbUltimineDown();
        } catch (Throwable ignored) {
            return false;
        }
    }

    /**
     * 调试/兼容用，只返回 Ultimine 按键状态。
     */
    public static boolean isUltimineDownForDebugOrCompat() {
        try {
            return isFtbUltimineDown();
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static boolean isFeatureEnabled() {
        return Configs.Generic.FTB_ULTIMINE_SUPPORT.getBooleanValue();
    }

    public static boolean shouldDisableTextBackgroundForUltimine() {
        /*
         * FTB_ULTIMINE_SUPPORT = true：
         *   仍然走原来的“自动隐藏 BoccHUD/MiniHUD”逻辑。
         *
         * FTB_ULTIMINE_SUPPORT = false：
         *   不隐藏 BoccHUD，只在 Ultimine HUD 激活期间关闭 BoccHUD 文本背景。
         */
        if (isFeatureEnabled()) {
            return false;
        }

        try {
            return isFtbUltimineDown();
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static boolean hasPlayer(Object client) {
        try {
            Field playerField = client.getClass().getField("player");
            return playerField.get(client) != null;
        } catch (Throwable ignored) {
            /*
             * 不同映射/平台下字段可能不同。
             * 这里失败时不要直接禁用功能，避免 Fabric/NeoForge 环境差异导致误判。
             */
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
            Class<?> clientClass = findClass(
                    "dev.ftb.mods.ftbultimine.client.FTBUltimineClient",
                    "dev.ftb.mods.ftbultimine.FTBUltimineClient"
            );

            Field keyField = findField(
                    clientClass,
                    "keyBindUltimine",
                    "keyMappingUltimine",
                    "ultimineKey",
                    "key"
            );

            Object keyBind = keyField.get(null);

            Method pressedMethod = findBooleanMethod(
                    keyBind.getClass(),
                    "isDown",
                    "isPressed"
            );

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

    private static Class<?> findClass(String... names) throws ClassNotFoundException {
        ClassLoader loader = UltimineMiniHUDHandler.class.getClassLoader();

        for (String name : names) {
            try {
                return Class.forName(name, false, loader);
            } catch (ClassNotFoundException ignored) {
            }
        }

        throw new ClassNotFoundException(String.join("/", names));
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