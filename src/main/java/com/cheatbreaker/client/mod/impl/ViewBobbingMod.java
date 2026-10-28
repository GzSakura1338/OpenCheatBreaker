package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.event.SimpleEvent3;
import com.cheatbreaker.client.event.StringFloatEvent;
import com.cheatbreaker.client.event.WorldClientEvent;
import com.cheatbreaker.client.mod.AbstractMod;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.mod.setting.SettingGroup;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemMap;

public class ViewBobbingMod extends AbstractMod {
    public List<ViewBobbingMod.BobbingSettings> bobbingSettings = new ArrayList<>();
    public ViewBobbingMod.BobbingSettings screen = new ViewBobbingMod.BobbingSettings("Screen");
    public ScheduledExecutorService executor;
    public ViewBobbingMod.BobbingSettings hand = new ViewBobbingMod.BobbingSettings("Hand");

    public void updateAll() {
        this.bobbingSettings.forEach(this::update);
    }

    public ViewBobbingMod() {
        super("View Bobbing");
        this.bobbingSettings.add(this.screen);
        this.bobbingSettings.add(this.hand);
        this.setLabel("View Bobbing", 1.0F);
        this.addEventHandler(StringFloatEvent.class, this::onStringFloatEvent);
        this.addEventHandler(WorldClientEvent.class, this::onWorldClientEvent);
        this.addEventHandler(SimpleEvent3.class, this::onSimpleEvent3);
    }

    public Map<String, Float> computeTargets(ViewBobbingMod.BobbingSettings bobbingSettings) {
        HashMap map = new HashMap();
        float floatValue = bobbingSettings.bobbingIntensity.getFloatValue();
        map.put("Horizontal", this.isNotSelected(bobbingSettings, "Vertical") ? floatValue : 0.0F);
        map.put("Vertical", this.isNotSelected(bobbingSettings, "Horizontal") ? floatValue : 0.0F);
        map.put("Pitch", bobbingSettings.bob2.getBooleanValue() ? floatValue : 0.0F);
        return map;
    }

    public void setAllTargets(ViewBobbingMod.BobbingSettings bobbingSettings, float f) {
        bobbingSettings.targetHorizontal = f;
        bobbingSettings.targetVertical = f;
        bobbingSettings.targetPitch = f;
    }

    @Override
    public void onEnable() throws java.io.IOException {
        super.onEnable();
        this.startExecutor();
    }

    public void onSimpleEvent3(SimpleEvent3 simpleEvent3) {
        this.stopExecutor();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.stopExecutor();
    }

    public void stopExecutor() {
        this.executor.shutdownNow();
    }

    public void update(ViewBobbingMod.BobbingSettings bobbingSettings) {
        if (!this.isDisabledByActiveMode(bobbingSettings) && !this.isOff(bobbingSettings)) {
            this.setTargets(bobbingSettings, this.computeTargets(bobbingSettings));
        } else {
            this.setAllTargets(bobbingSettings, 0.0F);
        }

        this.stepAll(bobbingSettings);
    }

    public void startExecutor() {
        this.executor = Executors.newScheduledThreadPool(1);
        this.executor.scheduleAtFixedRate(this::updateAll, 0L, 1L, TimeUnit.MILLISECONDS);
    }

    public float getTarget(ViewBobbingMod.BobbingSettings bobbingSettings, String str) {
        switch (str) {
            case "Horizontal":
                return bobbingSettings.targetHorizontal;
            case "Vertical":
                return bobbingSettings.targetVertical;
            case "Pitch":
                return bobbingSettings.targetPitch;
            default:
                throw new IllegalArgumentException("Invalid intensity type: " + str);
        }
    }

    public boolean isDisabledByActiveMode(ViewBobbingMod.BobbingSettings bobbingSettings) {
        return bobbingSettings.modes.stream().anyMatch(bobbingMode -> !bobbingMode.bob.getBooleanValue() && bobbingMode.condition.getAsBoolean());
    }

    public void setCurrent(ViewBobbingMod.BobbingSettings bobbingSettings, String str, float f) {
        switch (str) {
            case "Horizontal":
                bobbingSettings.currentHorizontal = f;
                break;
            case "Vertical":
                bobbingSettings.currentVertical = f;
                break;
            case "Pitch":
                bobbingSettings.currentPitch = f;
        }
    }

    public ViewBobbingMod.BobbingSettings getSettingsByName(String str) {
        for (ViewBobbingMod.BobbingSettings bobbingSettings : this.bobbingSettings) {
            if (str.equals(bobbingSettings.name)) {
                return bobbingSettings;
            }
        }

        return this.hand;
    }

    public boolean isNotSelected(ViewBobbingMod.BobbingSettings bobbingSettings, String str) {
        return !str.equals(bobbingSettings.bob.getStringValue());
    }

    public boolean isOff(ViewBobbingMod.BobbingSettings bobbingSettings) {
        return bobbingSettings.bob.getStringValue().equals("OFF");
    }

    public void onWorldClientEvent(WorldClientEvent worldClientEvent) {
        this.startExecutor();
    }

    public float stepToward(float f, float f2) {
        if (f < f2) {
            f++;
        } else if (f > f2) {
            f--;
        }

        return Math.max(0.0F, f);
    }

    public void stepIntensity(ViewBobbingMod.BobbingSettings bobbingSettings, String str) {
        float current = this.getCurrent(bobbingSettings, str);
        float target = this.getTarget(bobbingSettings, str);
        current = this.stepToward(current, target);
        this.setCurrent(bobbingSettings, str, current);
    }

    public float getCurrent(ViewBobbingMod.BobbingSettings bobbingSettings, String str) {
        switch (str) {
            case "Horizontal":
                return bobbingSettings.currentHorizontal;
            case "Vertical":
                return bobbingSettings.currentVertical;
            case "Pitch":
                return bobbingSettings.currentPitch;
            default:
                throw new IllegalArgumentException("Invalid intensity type: " + str);
        }
    }

    public void onStringFloatEvent(StringFloatEvent stringFloatEvent) {
        ViewBobbingMod.BobbingSettings settingsByName = this.getSettingsByName(stringFloatEvent.getText());
        if (settingsByName.currentHorizontal == 0.0F && settingsByName.currentVertical == 0.0F && settingsByName.currentPitch == 0.0F) {
            stringFloatEvent.setCancelled(true);
        } else {
            stringFloatEvent.setFloatValue2(settingsByName.currentHorizontal / 100.0F);
            stringFloatEvent.setFloatValue3(settingsByName.currentVertical / 100.0F);
            stringFloatEvent.setFloatValue4(settingsByName.currentPitch / 100.0F);
        }
    }

    public void stepAll(ViewBobbingMod.BobbingSettings bobbingSettings) {
        this.stepIntensity(bobbingSettings, "Horizontal");
        this.stepIntensity(bobbingSettings, "Vertical");
        this.stepIntensity(bobbingSettings, "Pitch");
    }

    public void setTargets(ViewBobbingMod.BobbingSettings bobbingSettings, Map<String, Float> map) {
        bobbingSettings.targetHorizontal = map.getOrDefault("Horizontal", 0.0F);
        bobbingSettings.targetVertical = map.getOrDefault("Vertical", 0.0F);
        bobbingSettings.targetPitch = map.getOrDefault("Pitch", 0.0F);
    }

    public class BobbingMode {
        public Setting bob;
        public BooleanSupplier condition;
        public float intensity;

        public BobbingMode(ViewBobbingMod.BobbingSettings bobbingSettings, String str, String str2) {
            this.bob = new Setting(ViewBobbingMod.this, "Bob " + str2 + " While " + str)
                .changeValue(true)
                .setCondition(() -> !bobbingSettings.bob.getStringValue().equals("OFF"));
            bobbingSettings.modes.add(this);
        }

        public ViewBobbingMod.BobbingMode setBob(Setting setting) {
            this.bob = setting;
            return this;
        }

        public ViewBobbingMod.BobbingMode setIntensity(float f) {
            this.intensity = f;
            return this;
        }

        public ViewBobbingMod.BobbingMode setCondition(BooleanSupplier booleanSupplier) {
            this.condition = booleanSupplier;
            return this;
        }
    }

    public class BobbingSettings {
        public float currentHorizontal = 0.0F;
        public float currentVertical = 0.0F;
        public float currentPitch = 0.0F;
        public float targetHorizontal = 0.0F;
        public float targetVertical = 0.0F;
        public float targetPitch = 0.0F;
        public List<ViewBobbingMod.BobbingMode> modes = new ArrayList<>();
        public String name;
        public Setting bobbingOptions;
        public Setting bob;
        public ViewBobbingMod.BobbingMode walking;
        public ViewBobbingMod.BobbingMode sprinting;
        public ViewBobbingMod.BobbingMode sneaking;
        public ViewBobbingMod.BobbingMode holdingMap;
        public Setting bob2;
        public Setting bobbingIntensity;

        public BobbingSettings(String str) {
            this.name = str.toLowerCase();
            this.bobbingOptions = new SettingGroup(ViewBobbingMod.this, str + " Bobbing Options").changeValue(true);
            this.bob = new Setting(ViewBobbingMod.this, "Bob " + str)
                .changeValue("ON")
                .setStringValues("OFF", "Horizontal", "Vertical", "ON")
                .withHierarchyParents(this.bobbingOptions);
            this.walking = ViewBobbingMod.this.new BobbingMode(this, "Walking", str)
                .setCondition(
                    () -> Minecraft.getMinecraft().thePlayer != null
                        && !Minecraft.getMinecraft().thePlayer.isSprinting()
                        && !Minecraft.getMinecraft().thePlayer.isSneaking()
                );
            this.sprinting = ViewBobbingMod.this.new BobbingMode(this, "Sprinting", str)
                .setCondition(() -> Minecraft.getMinecraft().thePlayer != null && Minecraft.getMinecraft().thePlayer.isSprinting());
            this.sneaking = ViewBobbingMod.this.new BobbingMode(this, "Sneaking", str)
                .setCondition(() -> Minecraft.getMinecraft().thePlayer != null && Minecraft.getMinecraft().thePlayer.isSneaking());
            this.holdingMap = ViewBobbingMod.this.new BobbingMode(this, "Holding Map", str)
                .setCondition(
                    () -> Minecraft.getMinecraft().thePlayer != null
                        && Minecraft.getMinecraft().thePlayer.getHeldItem() != null
                        && Minecraft.getMinecraft().thePlayer.getHeldItem().getItem() instanceof ItemMap
                );
            this.bob2 = new Setting(ViewBobbingMod.this, "Bob " + str + " While Falling")
                .changeValue(true)
                .setCondition(() -> !this.bob.getStringValue().equals("OFF"));
            this.bobbingIntensity = new Setting(ViewBobbingMod.this, str + " Bobbing Intensity")
                .setUnit("%")
                .changeValue(100.0F)
                .setRange(0.0F, 200.0F)
                .setCondition(() -> !this.bob.getStringValue().equals("OFF"));
        }
    }
}
