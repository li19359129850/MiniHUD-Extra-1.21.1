package com.ecaree.minihudextra.integration.neoforge;

//import mekanism.common.config.MekanismConfig;
//import mekanism.common.lib.radiation.RadiationManager;
//import mekanism.common.util.UnitDisplayUtils;
//import mekanism.common.util.text.TextUtils;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.util.Formatting;
//
//@SuppressWarnings("unused")
//public class MekRadiationImpl {
//    public static String getRadiationString(PlayerEntity player) {
//        RadiationManager.LevelAndMaxMagnitude levelAndMaxMagnitude = RadiationManager.get().getRadiationLevelAndMaxMagnitude(player);
//        double magnitude = levelAndMaxMagnitude.level();
//        String colorCode = RadiationManager.RadiationScale.getSeverityColor(magnitude).code;
//        return colorCode
//                + UnitDisplayUtils.getDisplayShort(magnitude, UnitDisplayUtils.RadiationUnit.SVH, 3).getString()
//                + Formatting.RESET;
//    }
//
//    public static String getDecayTime(PlayerEntity player) {
//        RadiationManager.LevelAndMaxMagnitude levelAndMaxMagnitude = RadiationManager.get().getRadiationLevelAndMaxMagnitude(player);
//        double magnitude = levelAndMaxMagnitude.level();
//        String colorCode = RadiationManager.RadiationScale.getSeverityColor(magnitude).code;
//        if (MekanismConfig.common.enableDecayTimers.get() && magnitude > 1.0E-7) {
//            return colorCode
//                    + TextUtils.getHoursMinutes(RadiationManager.get().getDecayTime(levelAndMaxMagnitude.maxMagnitude(), true)).getString()
//                    + Formatting.RESET;
//        } else {
//            return "N/A";
//        }
//    }
//}