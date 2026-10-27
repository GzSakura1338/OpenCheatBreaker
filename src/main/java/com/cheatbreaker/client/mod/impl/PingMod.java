package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.event.SimpleEvent26;
import com.cheatbreaker.client.event.SimpleEvent3;
import com.cheatbreaker.client.mod.AbstractValueHudMod;
import com.cheatbreaker.client.mod.ModCategory;
import com.cheatbreaker.client.mod.ValueRange;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.mod.setting.SettingsLevel;
import java.awt.Color;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.OldServerPinger;

public class PingMod extends AbstractValueHudMod {
   public static ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(10);
   public OldServerPinger oldServerPinger = new OldServerPinger();
   public ScheduledFuture<?> scheduledFuture;
   public Setting setting;
   public long longValue = -1L;
   public Setting dynamicColorRange;
   public Setting refreshTime;

   public void handleServerData(ServerData serverData) {
      if (this.mc.isIntegratedServerRunning() || this.mc.theWorld == null) {
         this.longValue = -1L;
      } else if (serverData != null) {
         this.handleServerData2(serverData);
      }
   }

   public void handleServerData2(ServerData serverData) {
      if (this.scheduledFuture != null) {
         this.scheduledFuture.cancel(true);
      }

      this.scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(() -> {
         try {
            this.oldServerPinger.ping(serverData);
         } catch (UnknownHostException e) {
            e.printStackTrace();
         }
      }, 0L, TimeUnit.SECONDS.toMillis(((Integer)this.refreshTime.getValue()).intValue()), TimeUnit.MILLISECONDS);
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.handleServerData(this.mc.getCurrentServerData());
   }

   @Override
   public int getTextColor() {
      if (this.dynamicColorRange.getBooleanValue()) {
         return this.getDynamicText() != null
            ? Color.getHSBColor(Math.max((125.0F - (float)this.longValue * 10.0F / this.setting.getFloatValue()) / 360.0F, 0.0F), 1.0F, 1.0F).getRGB()
            : Color.getHSBColor(Math.max((125.0F - 1190.0F / this.setting.getFloatValue()) / 360.0F, 0.0F), 1.0F, 1.0F).getRGB();
      } else {
         return this.textColor.getColorValue();
      }
   }

   public PingMod() {
      super("Ping");
      this.setDescription("Displays your server latency.");
      this.handleString("[19 ms]");
      this.setCategories(ModCategory.HUD, ModCategory.SERVER);
      this.addEventHandler(SimpleEvent26.class, simpleEvent26 -> this.handleServerData(this.mc.getCurrentServerData()));
      this.addEventHandler(SimpleEvent3.class, simpleEvent3 -> scheduledExecutorService.shutdown());
   }

   @Override
   public void createGeneralOptions() {
      super.createGeneralOptions();
      this.refreshTime = new Setting(this, "Refresh Time")
         .setDescription("Changes how many seconds the mod should refresh itself.")
         .withHierarchyParents(this.generalOptions)
         .setUnit("s")
         .changeValue(15)
         .setAliases("Refresh Interval Amount", "Refresh Frequency Amount", "Update Interval Amount", "Update Frequency Amount")
         .setRange(1, 90)
         .setCustomizationLevel(SettingsLevel.MEDIUM)
         .setOnUpdate(obj -> this.handleServerData(this.mc.getCurrentServerData()));
      this.dynamicColorRange = new Setting(this, "Dynamic Color Range")
         .setDescription("Determine if the text color should change dynamically based on ping amount.")
         .withHierarchyParents(this.generalOptions)
         .changeValue(false)
         .setCustomizationLevel(SettingsLevel.MEDIUM);
      this.setting = new Setting(this, "Bad Ping Range Threshold")
         .withHierarchyParents(this.generalOptions)
         .changeValue(25.0F)
         .setRange(10.0F, 100.0F)
         .setCondition(this.dynamicColorRange::getBooleanValue);
   }

   @Override
   public String getStaticText() {
      return "119";
   }

   @Override
   public ValueRange getHiddenValueRange() {
      return new ValueRange(0, 100, 1000);
   }

   @Override
   public String getLabelText() {
      return "ms";
   }

   @Override
   public Number getNumericValue() {
      ServerData currentServerData = this.mc.getCurrentServerData();
      if (currentServerData != null && currentServerData.pingToServer > 0L) {
         this.longValue = currentServerData.pingToServer;
      }

      return this.longValue;
   }

   @Override
   public void createColorOptions() {
      this.textColor.withInvertedParents(this.dynamicColorRange);
   }

   @Override
   public void onDisable() {
      super.onDisable();
      if (this.scheduledFuture != null) {
         this.scheduledFuture.cancel(true);
      }
   }
}
