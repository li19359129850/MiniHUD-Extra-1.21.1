package com.ecaree.minihudextra.integration;

import net.minecraft.client.resource.language.I18n;
import net.minecraft.world.World;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.season.SeasonTime;

import java.util.Locale;

public class SereneSeasons {
    public static SeasonTime getSeasonTime(World world) {
        return new SeasonTime(SeasonHelper.getSeasonState(world).getSeasonCycleTicks());
    }

    public static int getSeasonDuration(World world) {
        SeasonTime time = getSeasonTime(world);
        return time.getSeasonDuration() / time.getDayDuration();
    }

    public static int getSubSeasonDuration(World world) {
        SeasonTime time = getSeasonTime(world);
        return time.getSubSeasonDuration() / time.getDayDuration();
    }

    public static int getTropicalSeasonDuration(World world) {
        return getSubSeasonDuration(world) * 2;
    }

    public static int getDayOfSeason(World world) {
        SeasonTime time = getSeasonTime(world);
        int seasonDuration = getSeasonDuration(world);
        return time.getDay() % seasonDuration + 1;
    }

    public static int getDayOfSubSeason(World world) {
        SeasonTime time = getSeasonTime(world);
        int subSeasonDuration = getSubSeasonDuration(world);
        return time.getDay() % subSeasonDuration + 1;
    }

    public static int getDayOfTropicalSeason(World world) {
        SeasonTime time = getSeasonTime(world);
        int subSeasonDuration = getSubSeasonDuration(world);
        return (time.getDay() + subSeasonDuration) % getTropicalSeasonDuration(world) + 1;
    }

    public static String getSeasonName(World world) {
        SeasonTime time = getSeasonTime(world);
        return I18n.translate("desc.sereneseasons." + time.getSeason().toString().toLowerCase(Locale.ROOT));
    }

    public static String getSubSeasonName(World world) {
        SeasonTime time = getSeasonTime(world);
        return I18n.translate("desc.sereneseasons." + time.getSubSeason().toString().toLowerCase(Locale.ROOT));
    }

    public static String getTropicalSeasonName(World world) {
        SeasonTime time = getSeasonTime(world);
        return I18n.translate("desc.sereneseasons." + time.getTropicalSeason().toString().toLowerCase(Locale.ROOT));
    }
}