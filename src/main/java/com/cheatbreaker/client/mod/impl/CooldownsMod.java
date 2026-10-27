package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.ClientTickEvent;
import com.cheatbreaker.client.event.ScaledResolutionEvent1;
import com.cheatbreaker.client.event.ScaledResolutionEvent3;
import com.cheatbreaker.client.event.ScaledResolutionEvent7;
import com.cheatbreaker.client.mod.AbstractHudMod;
import com.cheatbreaker.client.mod.ModCategory;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.mod.setting.SettingGroup;
import com.cheatbreaker.client.mod.setting.SettingsLevel;
import com.cheatbreaker.client.ui.AnchorPoint;
import com.cheatbreaker.client.ui.screen.ModSettingsScreen;
import com.cheatbreaker.client.ui.screen.ModsScreen;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.GlStateManager;

public class CooldownsMod extends AbstractHudMod {
   public static List<CooldownEntry> cooldownEntryList = new ArrayList<>();
   public List<CooldownEntry> cooldownEntryList2 = new ArrayList<>();
   public Setting generalOptions;
   public Setting listMode;
   public Setting usePearlCooldownViaXp;
   public Setting decimals;
   public Setting colorOptions;
   public Setting colorTheme;
   public Setting coloredColor;

   public void onScaledResolutionEvent1(ScaledResolutionEvent1 scaledResolutionEvent1) {
      if (this.mc.thePlayer.experience >= 0.992F
         && !this.mc.isIntegratedServerRunning()
         && this.mc.theWorld != null
         && this.usePearlCooldownViaXp.getBooleanValue()) {
         handleString("EnderPearl", (long)(this.mc.thePlayer.experience * 15900.0F), 368);
      }
   }

   public void onClientTickEvent(ClientTickEvent clientTickEvent) {
      if (!cooldownEntryList.isEmpty()) {
         cooldownEntryList.removeIf(CooldownEntry::isExpired);
      }

      if (!this.cooldownEntryList2.isEmpty()) {
         this.cooldownEntryList2.removeIf(CooldownEntry::isExpired);
      }
   }

   public void onScaledResolutionEvent3(ScaledResolutionEvent3 scaledResolutionEvent3) {
      if (this.isRenderHud() && (!this.hidden || this.mc.currentScreen instanceof ModsScreen)) {
         GlStateManager.pushMatrix();
         if (cooldownEntryList.size() > 0) {
            this.applyHudTransformForSize(scaledResolutionEvent3.getScaledResolution());
            boolean equalsIgnoreCase = ((String)this.listMode.getValue()).equalsIgnoreCase("vertical");
            byte b = 36;
            byte b2 = 36;
            int n = equalsIgnoreCase ? b : cooldownEntryList.size() * b;
            int n2 = equalsIgnoreCase ? cooldownEntryList.size() * b2 : b2;
            this.setHudSize(n, n2 - 1);

            for (int i = 0; i < cooldownEntryList.size(); i++) {
               CooldownEntry cooldownEntry = cooldownEntryList.get(i);
               if (((String)this.listMode.getValue()).equalsIgnoreCase("vertical")) {
                  cooldownEntry.render(this.colorTheme, this.hudWidth / 2.0F - b / 2, i * b2, this.coloredColor.getColorValue());
               } else {
                  cooldownEntry.render(this.colorTheme, i * b, 0.0F, this.coloredColor.getColorValue());
               }
            }
         } else if (!(this.mc.currentScreen instanceof ModsScreen) && !(this.mc.currentScreen instanceof ModSettingsScreen)) {
            this.setHudSize(50.0F, 24.0F);
            this.applyHudTransformForSize(scaledResolutionEvent3.getScaledResolution());
         }

         GlStateManager.popMatrix();
      }
   }

   public static void handleString(String str, long l, int n) {
      for (CooldownEntry cooldownEntry : cooldownEntryList) {
         if (cooldownEntry.getName().equalsIgnoreCase(str) && cooldownEntry.getItemId() == n) {
            cooldownEntry.restart();
            cooldownEntry.setDuration(l);
            return;
         }
      }

      cooldownEntryList.add(new CooldownEntry(str, n, l));
   }

   public void onScaledResolutionEvent7(ScaledResolutionEvent7 scaledResolutionEvent7) {
      if (this.isRenderHud()) {
         if (cooldownEntryList.isEmpty()) {
            GlStateManager.pushMatrix();
            if (this.cooldownEntryList2.isEmpty()) {
               this.cooldownEntryList2.add(new CooldownEntry("CombatTag", 283, 30000L));
               this.cooldownEntryList2.add(new CooldownEntry("EnderPearl", 368, 12000L));
            }

            this.applyHudTransformForSize(scaledResolutionEvent7.getScaledResolution());
            boolean equalsIgnoreCase = ((String)this.listMode.getValue()).equalsIgnoreCase("vertical");
            byte b = 36;
            byte b2 = 36;
            int n = equalsIgnoreCase ? b : this.cooldownEntryList2.size() * b;
            int n2 = equalsIgnoreCase ? this.cooldownEntryList2.size() * b2 : b2;
            this.setHudSize((int)n, (int)n2 - 1);

            for (int i = 0; i < this.cooldownEntryList2.size(); i++) {
               CooldownEntry cooldownEntry = this.cooldownEntryList2.get(i);
               if (((String)this.listMode.getValue()).equalsIgnoreCase("vertical")) {
                  cooldownEntry.render(this.colorTheme, this.hudWidth / 2.0F - b / 2, i * b2, this.coloredColor.getColorValue());
               } else {
                  cooldownEntry.render(this.colorTheme, i * b, 0.0F, this.coloredColor.getColorValue());
               }
            }

            GlStateManager.popMatrix();
         }
      }
   }

   public CooldownsMod() {
      super("Cooldowns");
      this.setAnchorPointAndDefault(AnchorPoint.MIDDLE_TOP);
      this.setTranslationAndDefault(0.0F, 5.0F);
      this.generalOptions = new SettingGroup(this, "General Options").changeValue(true);
      this.listMode = new Setting(this, "List Mode").changeValue("horizontal").setStringValues("vertical", "horizontal").withParents(this.generalOptions);
      this.usePearlCooldownViaXp = new Setting(this, "Use Pearl Cooldown via XP").changeValue(true).withParents(this.generalOptions);
      this.decimals = new Setting(this, "Decimals").changeValue(1).setRange(0, 3).setCustomizationLevel(SettingsLevel.MEDIUM).withParents(this.generalOptions);
      this.colorOptions = new SettingGroup(this, "Color Options").changeValue(true);
      this.colorTheme = new Setting(this, "Color Theme").changeValue("Bright").setStringValues("Bright", "Dark", "Colored").withParents(this.colorOptions);
      this.coloredColor = new Setting(this, "Colored color")
         .changeValue(-1)
         .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
         .setCondition(() -> this.colorTheme.getValue().equals("Colored"))
         .withParents(this.colorOptions);
      this.setDescription("Allows servers to display items or abilities that are on cooldown.");
      this.setAuthors("OpTic");
      this.setCategories(ModCategory.HUD, ModCategory.SERVER);
      this.setShowLabelInList(false);
      this.addEventHandler(ClientTickEvent.class, this::onClientTickEvent);
      this.addEventHandler(ScaledResolutionEvent1.class, this::onScaledResolutionEvent1);
      this.addEventHandler(ScaledResolutionEvent7.class, this::onScaledResolutionEvent7);
      this.addEventHandler(ScaledResolutionEvent3.class, this::onScaledResolutionEvent3);
      this.setEnabledByDefault(true);
   }

   @Override
   public void renderPreview(int n, int n2, int n3, int n4) {
      CooldownEntry cooldownEntry = new CooldownEntry("EnderPearl", 368, 9000L);
      cooldownEntry.render(CheatBreaker.getInstance().getModManager().cooldownsMod.colorTheme, n + n3 / 2 - 18, n2 + n4 / 2 - 26 - 18, -1);
   }
}
