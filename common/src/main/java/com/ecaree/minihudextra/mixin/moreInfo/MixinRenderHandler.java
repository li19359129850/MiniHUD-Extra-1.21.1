package com.ecaree.minihudextra.mixin.moreInfo;

import com.ecaree.minihudextra.config.Configs;
import com.ecaree.minihudextra.config.MHExInfoToggle;
import com.ecaree.minihudextra.integration.*;
import dev.architectury.platform.Platform;
import fi.dy.masa.minihud.config.InfoToggle;
import fi.dy.masa.minihud.event.RenderHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RenderHandler.class, remap = false)
public abstract class MixinRenderHandler {
    @Shadow
    @Final
    private MinecraftClient mc;

    @Shadow
    protected abstract void addLine(String text);

    @Inject(method = "addLine(Lfi/dy/masa/minihud/config/InfoToggle;)V", at = @At("TAIL"))
    private void onAddLine(InfoToggle toggle, CallbackInfo ci) {
        MinecraftClient mc = this.mc;
        Entity entity = mc.getCameraEntity();
        ClientWorld world = mc.world;
        ClientPlayerEntity player = mc.player;
        if (entity == null || world == null || player == null) return;

        BlockPos pos = BlockPos.ofFloored(entity.getX(), entity.getY(), entity.getZ());
        @SuppressWarnings("deprecation")
        boolean isChunkLoaded = world.getChunkManager().isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4);

        if (!isChunkLoaded) return;

        if (toggle.getName().equals(MHExInfoToggle.WEATHER.getName())) {
            try {
                String str = Configs.Vanilla.WEATHER_FORMAT.getStringValue();

                boolean isRaining = world.isRaining();
                boolean isThundering = world.isThundering();
                String weather = isRaining
                        ? isThundering ? I18n.translate("desc.minihudextra.weather_t") : I18n.translate("desc.minihudextra.weather_r")
                        : I18n.translate("desc.minihudextra.weather_c");
                String weatherAbbr = isRaining
                        ? isThundering ? "T" : "R"
                        : "C";
                str = str.replace("{IS_RAINING}", String.format("%b", isRaining));
                str = str.replace("{IS_THUNDERING}", String.format("%b", isThundering));
                str = str.replace("{WEATHER}", String.format("%s", weather));
                str = str.replace("{WEATHER_ABBR}", String.format("%s", weatherAbbr));
                this.addLine(str);
            } catch (Exception e) {
                this.addLine("Weather Format Failed");
            }
        }

        if (Platform.isModLoaded("bloodmagic")
                && toggle.getName().equals(MHExInfoToggle.BLOOD_MAGIC.getName())) {
            try {
                String str = Configs.ModIntegration.BLOOD_MAGIC_FORMAT.getStringValue();

                str = str.replace("{LP}", String.format("%d", BloodMagic.getLP(player)));
                str = str.replace("{ORB_TIER}", String.format("%d", BloodMagic.getOrbTier(player)));
                str = str.replace("{ORB_TIER_NAME}", String.format("%s", BloodMagic.getOrbTierName(player)));
                this.addLine(str);
            } catch (Exception e) {
                this.addLine("Blood Magic Format Failed");
            }
        }

        if (Platform.isModLoaded("deepresonance")
                && toggle.getName().equals(MHExInfoToggle.DEEP_RESONANCE_RADIATION.getName())) {
            try {
                StringBuilder str;
                str = new StringBuilder(128);
                String radiationFmtStr = Configs.ModIntegration.DEEP_RESONANCE_RADIATION_FORMAT.getStringValue();
                str.append(String.format(radiationFmtStr, DeepResonanceRadiation.getRadiation(player)));
                this.addLine(str.toString());
            } catch (Exception e) {
                this.addLine("Deep Resonance Radiation Format Failed");
            }
        }

        if (Platform.isModLoaded("mekanism")
                && toggle.getName().equals(MHExInfoToggle.MEK_RADIATION.getName())) {
            try {
                String str = Configs.ModIntegration.MEK_RADIATION_FORMAT.getStringValue();
                str = str.replace("{RADIATION_EXPOSURE}", String.format("%s", MekRadiation.getRadiationString(player)));
                str = str.replace("{DECAY_TIME}", String.format("%s", MekRadiation.getDecayTime(player)));
                this.addLine(str);
            } catch (Exception e) {
                this.addLine("Mek Radiation Format Failed");
            }
        }

        if (Platform.isModLoaded("naturesaura")
                && toggle.getName().equals(MHExInfoToggle.NATURES_AURA.getName())) {
            try {
                StringBuilder str;
                str = new StringBuilder(128);
                String auraFmtStr = Configs.ModIntegration.NATURES_AURA_FORMAT.getStringValue();
                str.append(String.format(auraFmtStr, NaturesAura.getAura(world, player.getBlockPos())));
                this.addLine(str.toString());
            } catch (Exception e) {
                this.addLine("Natures Aura Format Failed");
            }
        }

        if (Platform.isModLoaded("sereneseasons")
                && toggle.getName().equals(MHExInfoToggle.SERENE_SEASONS.getName())) {
            try {
                String str = Configs.ModIntegration.SERENE_SEASONS_FORMAT.getStringValue();

                // https://github.com/maruohon/minihud/blob/pre-rewrite/fabric/1.20.x/src/main/java/fi/dy/masa/minihud/event/RenderHandler.java#L322-L350
                long timeDay = world.getTimeOfDay();
                long day = (int) (timeDay / 24000);
                int dayTicks = (int) (timeDay % 24000);
                int hour = (int) ((dayTicks / 1000) + 6) % 24;
                int min = (int) (dayTicks / 16.666666) % 60;
                int sec = (int) (dayTicks / 0.277777) % 60;
                String moon = I18n.translate("desc.minihudextra.moon_phase_" + ((int) day % 8 + 1));
                str = str.replace("{DAY}", String.format("%d", day));
                str = str.replace("{DAY_1}", String.format("%d", day + 1));
                str = str.replace("{HOUR}", String.format("%02d", hour));
                str = str.replace("{MIN}", String.format("%02d", min));
                str = str.replace("{SEC}", String.format("%02d", sec));
                str = str.replace("{MOON}", String.format("%s", moon));

                str = str.replace("{SEASON_DURATION}", String.format("%d", SereneSeasons.getSeasonDuration(world)));
                str = str.replace("{SUB_SEASON_DURATION}", String.format("%d", SereneSeasons.getSubSeasonDuration(world)));
                str = str.replace("{TROPICAL_SEASON_DURATION}", String.format("%d", SereneSeasons.getTropicalSeasonDuration(world)));
                str = str.replace("{DAY_OF_SEASON}", String.format("%d", SereneSeasons.getDayOfSeason(world)));
                str = str.replace("{DAY_OF_SUB_SEASON}", String.format("%d", SereneSeasons.getDayOfSubSeason(world)));
                str = str.replace("{DAY_OF_TROPICAL_SEASON}", String.format("%d", SereneSeasons.getDayOfTropicalSeason(world)));
                str = str.replace("{SEASON_NAME}", String.format("%s", SereneSeasons.getSeasonName(world)));
                str = str.replace("{SUB_SEASON_NAME}", String.format("%s", SereneSeasons.getSubSeasonName(world)));
                str = str.replace("{TROPICAL_SEASON_NAME}", String.format("%s", SereneSeasons.getTropicalSeasonName(world)));
                this.addLine(str);
            } catch (Exception e) {
                this.addLine("Serene Seasons Format Failed");
            }
        }
    }
}