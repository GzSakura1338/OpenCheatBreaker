package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.chat.ChatPrinter;
import com.cheatbreaker.client.event.ClientTickEvent;
import com.cheatbreaker.client.event.DoubleEvent1;
import com.cheatbreaker.client.event.FloatEvent5;
import com.cheatbreaker.client.event.IntEvent1;
import com.cheatbreaker.client.event.IntEvent4;
import com.cheatbreaker.client.event.S03PacketTimeUpdateEvent;
import com.cheatbreaker.client.event.SimpleEvent46;
import com.cheatbreaker.client.event.SimpleEvent47;
import com.cheatbreaker.client.event.StringEvent1;
import com.cheatbreaker.client.event.WorldClientEvent;
import com.cheatbreaker.client.mod.AbstractMod;
import com.cheatbreaker.client.mod.ModCategory;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.mod.setting.SettingGroup;
import com.cheatbreaker.client.mod.setting.SettingsLevel;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.optifine.Config;
import net.optifine.CustomSky;

public class EnvironmentChangerMod extends AbstractMod {
   public Setting timeSettings;
   public Setting timeType;
   public Setting worldTime;
   public Setting timeMultiplierType;
   public Setting timeMultiplierDelay;
   public Setting timeMultiplierAmount;
   public Setting adjustmentAmount;
   public Setting increaseTimeKeybind;
   public Setting decreaseTimeKeybind;
   public Setting skySettings;
   public Setting showHorizonLayer;
   public Setting horizonYLevel;
   public Setting weatherSettings;
   public Setting allowThroughBlocks;
   public Date date;
   public Setting customWeather;
   public Setting currentWeather;

   public void onSimpleEvent47(SimpleEvent47 simpleEvent47) {
      if (this.customWeather.getBooleanValue() && this.currentWeather.getStringValue().equals("Rain")) {
         simpleEvent47.setFlag(true);
      }
   }

   public void onSimpleEvent46(SimpleEvent46 simpleEvent46) {
      if (this.customWeather.getBooleanValue() && !this.currentWeather.getStringValue().equals("Rain")) {
         simpleEvent46.setFlag(true);
      }
   }

   public void onClientTickEvent(ClientTickEvent clientTickEvent) {
      if (this.mc.theWorld != null && this.timeType.getValue().equals("Real Time")) {
         this.mc.theWorld.setWorldTime(this.getIntValue());
      }
   }

   public void onIntEvent4(IntEvent4 intEvent4) {
      this.process(intEvent4.getIntValue());
   }

   public void onWorldClientEvent(WorldClientEvent worldClientEvent) {
      this.run();
   }

   public void process(int n) {
      if (this.timeType.getStringValue().equalsIgnoreCase("Static") && this.mc.theWorld != null) {
         if (n == this.increaseTimeKeybind.getIntValue()) {
            int n2 = this.worldTime.getIntValue() + this.adjustmentAmount.getIntValue();
            this.worldTime.changeValue(Math.min(n2, -6100));
         }

         if (n == this.decreaseTimeKeybind.getIntValue()) {
            int n2 = this.worldTime.getIntValue() - this.adjustmentAmount.getIntValue();
            this.worldTime.changeValue(Math.max(n2, -22880));
         }
      }
   }

   public EnvironmentChangerMod() {
      super("Environment Changer");
      this.setDescription("Allows you to change the environment around you to your liking.");
      this.setAuthors("Fyu (Time Changer)", "Sk1er (Snow)");
      this.setCategories(ModCategory.MISC);
      this.setIcon(new ResourceLocation("client/icons/mods/sun-moon.png"), 39, 40);
      this.timeSettings = new SettingGroup(this, "Time Settings").changeValue(true);
      this.timeType = new Setting(this, "Time Type").changeValue("Server").setStringValues("Server", "Real Time", "Static").setOnUpdate(obj -> {
         if (this.isEnabled()) {
            this.run();
         }
      }).withParents(this.timeSettings);
      this.worldTime = new Setting(this, "World Time")
         .changeValue(-14490)
         .setRange(-22880, -6100)
         .setShowValue(false)
         .setIconNames("moon-64", "sun-64")
         .setIconScales(7.5F, 10.0F)
         .setIconYOffsets(0.0F, -4.0F)
         .setCondition(() -> this.timeType.getValue().equals("Static"))
         .withParents(this.timeSettings)
         .setOnUpdate(obj -> {
            if (this.mc.theWorld != null && this.isEnabled()) {
               this.mc.theWorld.setWorldTime(Integer.parseInt(obj.toString()));
            }
         });
      this.timeMultiplierType = new Setting(this, "Time Multiplier Type")
         .changeValue("Vanilla")
         .setStringValues("Vanilla", "Accelerate", "Decelerate")
         .setCustomizationLevel(SettingsLevel.MEDIUM)
         .setCondition(() -> this.timeType.getValue().equals("Static"))
         .withParents(this.timeSettings);
      this.timeMultiplierDelay = new Setting(this, "Time Multiplier Delay")
         .setUnit("ms")
         .changeValue(300)
         .setRange(300, 5000)
         .setCustomizationLevel(SettingsLevel.ADVANCED)
         .setCondition(() -> this.timeType.getValue().equals("Static"))
         .withParents(this.timeSettings);
      this.timeMultiplierAmount = new Setting(this, "Time Multiplier Amount")
         .changeValue(0)
         .setRange(0, 10)
         .setCustomizationLevel(SettingsLevel.MEDIUM)
         .setCondition(() -> this.timeType.getValue().equals("Static"))
         .withParents(this.timeSettings);
      this.adjustmentAmount = new Setting(this, "Adjustment Amount")
         .changeValue(100)
         .setRange(1, 1000)
         .setCondition(() -> this.timeType.getStringValue().equalsIgnoreCase("Static"))
         .withParents(this.timeSettings)
         .setCustomizationLevel(SettingsLevel.MEDIUM);
      this.increaseTimeKeybind = new Setting(this, "Increase Time Keybind")
         .changeValue(0)
         .setMouseBind(false)
         .setCondition(() -> this.timeType.getStringValue().equalsIgnoreCase("Static"))
         .withParents(this.timeSettings)
         .setCustomizationLevel(SettingsLevel.MEDIUM);
      this.decreaseTimeKeybind = new Setting(this, "Decrease Time Keybind")
         .changeValue(0)
         .setMouseBind(false)
         .setCondition(() -> this.timeType.getStringValue().equalsIgnoreCase("Static"))
         .withParents(this.timeSettings)
         .setCustomizationLevel(SettingsLevel.MEDIUM);
      this.skySettings = new SettingGroup(this, "Sky Settings").changeValue(true).setCondition(Config::isSkyEnabled);
      this.showHorizonLayer = new Setting(this, "Show Horizon Layer")
         .changeValue("No Custom Sky")
         .setStringValues("OFF", "No Custom Sky", "ON")
         .withHierarchyParents(this.skySettings);
      this.horizonYLevel = new Setting(this, "Horizon Y Level")
         .changeValue(63.0)
         .setRange(0.0, 63.0)
         .withHierarchyParents(this.skySettings)
         .setCondition(() -> !this.showHorizonLayer.getValue().equals("OFF"));
      this.weatherSettings = new SettingGroup(this, "Weather Settings").changeValue(true);
      this.allowThroughBlocks = new Setting(this, "Allow Through Blocks")
         .changeValue(false)
         .setCustomizationLevel(SettingsLevel.MEDIUM)
         .withHierarchyParents(this.weatherSettings);
      this.customWeather = new Setting(this, "Custom Weather").changeValue(false).withHierarchyParents(this.weatherSettings);
      this.currentWeather = new Setting(this, "Current Weather")
         .changeValue("Clear")
         .setStringValues("Clear", "Rain", "Snow")
         .withHierarchyParents(this.customWeather);
      this.addEventHandler(S03PacketTimeUpdateEvent.class, this::onS03PacketTimeUpdateEvent);
      this.addEventHandler(FloatEvent5.class, this::onFloatEvent5);
      this.addEventHandler(DoubleEvent1.class, this::onDoubleEvent1);
      this.addEventHandler(SimpleEvent46.class, this::onSimpleEvent46);
      this.addEventHandler(SimpleEvent47.class, this::onSimpleEvent47);
      this.addEventHandler(StringEvent1.class, this::onStringEvent1);
      this.addEventHandler(WorldClientEvent.class, this::onWorldClientEvent);
      this.addEventHandler(ClientTickEvent.class, this::onClientTickEvent);
      this.addEventHandler(IntEvent4.class, this::onIntEvent4);
      this.addEventHandler(IntEvent1.class, this::onIntEvent1);
      Runnable runnable = () -> {
         if (this.timeType.getStringValue().equalsIgnoreCase("Static") && this.mc.theWorld != null) {
            String stringValue = this.timeMultiplierType.getStringValue();
            if (stringValue.equalsIgnoreCase("Vanilla")) {
               return;
            }

            if (stringValue.equalsIgnoreCase("Accelerate")) {
               this.mc.theWorld.setWorldTime(this.mc.theWorld.getWorldTime() * this.timeMultiplierAmount.getIntValue());
            }

            if (stringValue.equalsIgnoreCase("Decelerate")) {
               this.mc.theWorld.setWorldTime(this.mc.theWorld.getWorldTime() / this.timeMultiplierAmount.getIntValue());
            }

            try {
               Thread.sleep(this.timeMultiplierDelay.getIntValue());
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
         }
      };
      ScheduledExecutorService newScheduledThreadPool = Executors.newScheduledThreadPool(1);
      newScheduledThreadPool.scheduleAtFixedRate(runnable, 1L, 1L, TimeUnit.MILLISECONDS);
   }

   public void onDoubleEvent1(DoubleEvent1 doubleEvent1) {
      doubleEvent1.setDoubleValue(this.horizonYLevel.getDoubleValue());
   }

   public void onStringEvent1(StringEvent1 stringEvent1) {
      if (stringEvent1.getMessage().toLowerCase().startsWith("/cb time")) {
         stringEvent1.setFlag(true);
         String[] split = stringEvent1.getMessage().split(" ");
         if (split.length == 3) {
            if (this.timeType.getStringValue().equalsIgnoreCase("Static")) {
               if (this.mc.theWorld != null) {
                  int intValue = this.worldTime.getIntValue();
                  switch (split[2]) {
                     case "increase":
                        intValue += this.adjustmentAmount.getIntValue();
                        this.worldTime.changeValue(Math.min(intValue, (Integer)this.worldTime.getMaximumValue()));
                        return;
                     case "decrease":
                        intValue -= this.adjustmentAmount.getIntValue();
                        this.worldTime.changeValue(Math.max(intValue, (Integer)this.worldTime.getMinimumValue()));
                        return;
                  }
               }
            } else {
               ChatPrinter.print(EnumChatFormatting.RED + "This command only works with \"Static\" time.", true, false);
            }
         }

         ChatPrinter.print(EnumChatFormatting.RED + "Usage: /cb time <increase/decrease>", true, false);
      }
   }

   public int getIntValue() {
      if (this.date == null) {
         this.date = new Date();
      }

      this.date.setTime(System.currentTimeMillis());
      int n = this.date.getHours() - 6;
      if (n <= 0) {
         n += 24;
      }

      return (int)(n * 1000 + (this.date.getSeconds() + this.date.getMinutes() * 60) / 3.6);
   }

   public void onS03PacketTimeUpdateEvent(S03PacketTimeUpdateEvent s03PacketTimeUpdateEvent) {
      if (!this.timeType.getValue().equals("Server")) {
         s03PacketTimeUpdateEvent.setFlag(true);
      }
   }

   public void onFloatEvent5(FloatEvent5 floatEvent5) {
      boolean flag = this.showHorizonLayer.getValue().equals("No Custom Sky")
         ? !Config.isCustomSky() || CustomSky.getWorldSkyLayers() == null
         : this.showHorizonLayer.getValue().equals("ON");
      if (!flag) {
         floatEvent5.setFlag(true);
      }
   }

   public void onIntEvent1(IntEvent1 intEvent1) {
      this.process(intEvent1.getEventButton());
   }

   public void run() {
      if (this.mc.theWorld != null) {
         if (this.timeType.getValue().equals("Real Time")) {
            this.mc.theWorld.setWorldTime(this.getIntValue());
         } else if (this.timeType.getValue().equals("Static")) {
            this.mc.theWorld.setWorldTime(this.worldTime.getIntValue());
         }
      }
   }
}
