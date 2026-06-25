package com.ecaree.minihudextra.config;

import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigInteger;
import fi.dy.masa.malilib.util.StringUtils;

import static com.ecaree.minihudextra.config.Configs.ModIntegration;
import static com.ecaree.minihudextra.config.Configs.Vanilla;

@SuppressWarnings({"FieldMayBeFinal", "unused"})
public enum MHExInfoToggle {
    WEATHER(Vanilla.WEATHER, Vanilla.WEATHER_LINE_POSITION),
    MEK_RADIATION(ModIntegration.MEK_RADIATION, ModIntegration.MEK_RADIATION_LINE_POSITION),
    DEEP_RESONANCE_RADIATION(ModIntegration.DEEP_RESONANCE_RADIATION, ModIntegration.DEEP_RESONANCE_RADIATION_LINE_POSITION),
    SERENE_SEASONS(ModIntegration.SERENE_SEASONS, ModIntegration.SERENE_SEASONS_LINE_POSITION),
    BLOOD_MAGIC(ModIntegration.BLOOD_MAGIC, ModIntegration.BLOOD_MAGIC_LINE_POSITION),
    NATURES_AURA(ModIntegration.NATURES_AURA, ModIntegration.NATURES_AURA_LINE_POSITION);

    private final String name;
    private final String prettyName;
    private final String comment;
    private final boolean defaultValueBoolean;
    private final int defaultLinePosition;
    private boolean valueBoolean;
    private int linePosition;

    MHExInfoToggle(ConfigBoolean configBoolean, ConfigInteger configInteger) {
        this.name = configBoolean.getName();
        this.prettyName = configBoolean.getName();
        this.comment = configBoolean.getComment();
        this.defaultValueBoolean = configBoolean.getDefaultBooleanValue();
        this.defaultLinePosition = configInteger.getIntegerValue();
        this.valueBoolean = this.defaultValueBoolean;
        this.linePosition = this.defaultLinePosition;
//        configBoolean.setValueChangeCallback(this::onConfigBooleanChanged);
//        configInteger.setValueChangeCallback(this::onConfigIntegerChanged);
    }

    public String getName() {
        return this.name;
    }

    public String getPrettyName() {
        return this.prettyName;
    }

    public boolean getBooleanValue() {
        return this.valueBoolean;
    }

    public boolean getDefaultBooleanValue() {
        return this.defaultValueBoolean;
    }

    public int getLinePosition() {
        return this.linePosition;
    }

    public int getDefaultLinePosition() {
        return this.defaultLinePosition;
    }

    public String getComment() {
        return StringUtils.getTranslatedOrFallback("config.comment." + this.getName().toLowerCase(), this.comment);
    }

    public void setBooleanValue(boolean value) {
        this.valueBoolean = value;
    }

    public void setIntegerValue(int value) {
        this.linePosition = value;
    }

//    void onConfigBooleanChanged(ConfigBoolean configBoolean) {
//        this.setBooleanValue(configBoolean.getBooleanValue());
//    }
//
//    void onConfigIntegerChanged(ConfigInteger configInteger) {
//        this.setIntegerValue(configInteger.getIntegerValue());
//    }
}