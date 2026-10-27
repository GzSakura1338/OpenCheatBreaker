package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.event.ScaledResolutionScoreObjectiveScoreboardIntEvent;
import com.cheatbreaker.client.mod.AbstractHudMod;
import com.cheatbreaker.client.mod.ModCategory;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.mod.setting.SettingGroup;
import com.cheatbreaker.client.mod.setting.SettingsLevel;
import com.cheatbreaker.client.ui.AnchorPoint;
import com.cheatbreaker.client.ui.screen.ModsScreen;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

public class PlayerListMod extends AbstractHudMod {
   public Pattern pattern = Pattern.compile("(?i)§[0-689A-E]");
   public Setting generalOptions;
   public Setting showBackground;
   public Setting showRowBackground;
   public Setting showHeader;
   public Setting showFooter;
   public Setting showPlayerHeads;
   public Setting highlightOwnRow;
   public Setting highlightCheatbreakerPlayersRow;
   public Setting usePingColorForRowBackground;
   public Setting rowBackgroundColor;
   public Setting selfRowColor;
   public Setting cheatbreakerRowColor;
   public Setting backgroundColor;
   public Setting headerBackgroundColor;
   public Setting footerBackgroundColor;
   public Setting nameOptions;
   public Setting nameTextShadow;
   public Setting headerTextShadow;
   public Setting footerTextShadow;
   public Setting showCheatbreakerLogo;
   public Setting cheatbreakerLogoType;
   public Setting highlightOwnName;
   public Setting highlightCheatbreakerPlayersName;
   public Setting selfTextColor;
   public Setting cheatbreakerTextColor;
   public Setting pingOptions;
   public Setting showPing;
   public Setting showPingAsNumber;
   public static ResourceLocation iconsPng = new ResourceLocation("textures/gui/icons.png");
   public Setting pingTextShadow;
   public Setting dynamicPingNumberColor;
   public Setting staticPingColor;
   public Setting lowPingColor;
   public Setting mediumPingColor;
   public Setting highPingColor;
   public Setting extremelyHighPingColor;

   public String formatText(String str) {
      return str == null ? null : this.pattern.matcher(str).replaceAll("§r");
   }

   @Override
   public void renderPreview(int n, int n2, int n3, int n4) {
      Gui.drawRect(n + 20, n2 + n4 / 2 - 44, n + n3 - 20, n2 + n4 / 2 - 7, 1862270976);
      Gui.drawRect(n + 21, n2 + n4 / 2 - 43, n + n3 - 21, n2 + n4 / 2 - 35, 553648127);
      Gui.drawRect(n + 21, n2 + n4 / 2 - 34, n + n3 - 21, n2 + n4 / 2 - 26, 553648127);
      Gui.drawRect(n + 21, n2 + n4 / 2 - 25, n + n3 - 21, n2 + n4 / 2 - 17, 553648127);
      Gui.drawRect(n + 21, n2 + n4 / 2 - 16, n + n3 - 21, n2 + n4 / 2 - 8, 553648127);
      Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(Minecraft.getMinecraft().getSession().getUsername(), n + 21, n2 + n4 / 2 - 43, -1);
      Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow("Steve", n + 21, n2 + n4 / 2 - 34, -1);
      Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow("Alex", n + 21, n2 + n4 / 2 - 25, -1);
      Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow("Notch", n + 21, n2 + n4 / 2 - 16, -1);
      Minecraft.getMinecraft()
         .fontRendererObj
         .drawStringWithShadow(
            EnumChatFormatting.GREEN + "15", n + n3 - 21 - Minecraft.getMinecraft().fontRendererObj.getStringWidth("15"), n2 + n4 / 2 - 43, -1
         );
      Minecraft.getMinecraft()
         .fontRendererObj
         .drawStringWithShadow(
            EnumChatFormatting.GREEN + "75", n + n3 - 21 - Minecraft.getMinecraft().fontRendererObj.getStringWidth("75"), n2 + n4 / 2 - 34, -1
         );
      Minecraft.getMinecraft()
         .fontRendererObj
         .drawStringWithShadow(
            EnumChatFormatting.YELLOW + "155", n + n3 - 21 - Minecraft.getMinecraft().fontRendererObj.getStringWidth("155"), n2 + n4 / 2 - 25, -1
         );
      Minecraft.getMinecraft()
         .fontRendererObj
         .drawStringWithShadow(
            EnumChatFormatting.YELLOW + "250", n + n3 - 21 - Minecraft.getMinecraft().fontRendererObj.getStringWidth("250"), n2 + n4 / 2 - 16, -1
         );
   }

   public void onScaledResolutionScoreObjectiveScoreboardIntEvent(ScaledResolutionScoreObjectiveScoreboardIntEvent scaledResolutionScoreObjectiveScoreboardIntEvent) {
      if (this.shouldRender()) {
         GlStateManager.pushMatrix();
         this.applyHudTransformForSize(scaledResolutionScoreObjectiveScoreboardIntEvent.getScaledResolution());
         if (this.mc.currentScreen instanceof ModsScreen && this.isRenderHud()
            || this.mc.gameSettings.keyBindPlayerList.isKeyDown()
               && (!this.mc.isIntegratedServerRunning() || this.mc.thePlayer.sendQueue.getPlayerInfoMap().size() > 1 || scaledResolutionScoreObjectiveScoreboardIntEvent.getScoreObjective() != null)) {
            this.mc.ingameGUI.getTabList().render(scaledResolutionScoreObjectiveScoreboardIntEvent.getIntValue(), scaledResolutionScoreObjectiveScoreboardIntEvent.getScoreboard(), scaledResolutionScoreObjectiveScoreboardIntEvent.getScoreObjective());
         }

         GlStateManager.popMatrix();
      }
   }

   public PlayerListMod() {
      super("Player List");
      this.setAnchorPointAndDefault(AnchorPoint.MIDDLE_TOP);
      this.setTranslationAndDefault(0.0F, 7.0F);
      this.setShowLabelInList(false);
      this.setUseDefaultTranslation(true);
      this.hudOnly = false;
      this.setRenderHudAndDefault(false);
      this.generalOptions = new SettingGroup(this, "General Options").changeValue(true);
      this.showBackground = new Setting(this, "Show Background").changeValue(true).withParents(this.generalOptions);
      this.showRowBackground = new Setting(this, "Show Row Background").changeValue(true).withParents(this.generalOptions);
      this.showHeader = new Setting(this, "Show Header").changeValue(true).withParents(this.generalOptions).setCustomizationLevel(SettingsLevel.MEDIUM);
      this.showFooter = new Setting(this, "Show Footer").changeValue(true).withParents(this.generalOptions).setCustomizationLevel(SettingsLevel.MEDIUM);
      this.showPlayerHeads = new Setting(this, "Show Player Heads")
         .changeValue(true)
         .withParents(this.generalOptions)
         .setCustomizationLevel(SettingsLevel.MEDIUM);
      this.highlightOwnRow = new Setting(this, "Highlight Own Row").changeValue(false).withHierarchyParents(this.showRowBackground);
      this.highlightCheatbreakerPlayersRow = new Setting(this, "Highlight CheatBreaker Players' Row")
         .changeValue(false)
         .withHierarchyParents(this.showRowBackground);
      this.usePingColorForRowBackground = new Setting(this, "Use Ping Color for Row Background")
         .changeValue(false)
         .withHierarchyParents(this.showRowBackground);
      this.rowBackgroundColor = new Setting(this, "Row Background Color")
         .changeValue(553648127)
         .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
         .withHierarchyParents(this.showRowBackground);
      this.selfRowColor = new Setting(this, "Self Row Color")
         .changeValue(1084882944)
         .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
         .setCustomizationLevel(SettingsLevel.MEDIUM)
         .withHierarchyParents(this.highlightOwnRow);
      this.cheatbreakerRowColor = new Setting(this, "CheatBreaker Row Color")
         .changeValue(1088045651)
         .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
         .setCustomizationLevel(SettingsLevel.MEDIUM)
         .withHierarchyParents(this.highlightCheatbreakerPlayersRow);
      this.backgroundColor = new Setting(this, "Background Color")
         .changeValue(Integer.MIN_VALUE)
         .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
         .withHierarchyParents(this.showBackground);
      this.headerBackgroundColor = new Setting(this, "Header Background Color")
         .changeValue(Integer.MIN_VALUE)
         .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
         .withHierarchyParents(this.showHeader);
      this.footerBackgroundColor = new Setting(this, "Footer Background Color")
         .changeValue(Integer.MIN_VALUE)
         .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
         .withHierarchyParents(this.showFooter);
      this.nameOptions = new SettingGroup(this, "Name Options").changeValue(true);
      this.nameTextShadow = new Setting(this, "Name Text Shadow").changeValue(true).withParents(this.nameOptions);
      this.headerTextShadow = new Setting(this, "Header Text Shadow").changeValue(true).withParents(this.nameOptions);
      this.footerTextShadow = new Setting(this, "Footer Text Shadow").changeValue(true).withParents(this.nameOptions);
      this.showCheatbreakerLogo = new Setting(this, "Show CheatBreaker Logo").changeValue(true).withParents(this.nameOptions);
      this.cheatbreakerLogoType = new Setting(this, "CheatBreaker Logo Type")
         .withHierarchyParents(this.showCheatbreakerLogo)
         .changeValue(false)
         .setToggleTexts("Text", "Circle");
      this.highlightOwnName = new Setting(this, "Highlight Own Name").changeValue(false).withParents(this.nameOptions);
      this.highlightCheatbreakerPlayersName = new Setting(this, "Highlight CheatBreaker Players' Name").changeValue(false).withParents(this.nameOptions);
      this.selfTextColor = new Setting(this, "Self Text Color")
         .changeValue(-3399134)
         .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
         .setCustomizationLevel(SettingsLevel.MEDIUM)
         .withHierarchyParents(this.highlightOwnName);
      this.cheatbreakerTextColor = new Setting(this, "CheatBreaker Text Color")
         .changeValue(-301706)
         .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
         .setCustomizationLevel(SettingsLevel.MEDIUM)
         .withHierarchyParents(this.highlightCheatbreakerPlayersName);
      this.pingOptions = new SettingGroup(this, "Ping Options").changeValue(true);
      this.showPing = new Setting(this, "Show Ping").changeValue(true).withParents(this.pingOptions);
      this.showPingAsNumber = new Setting(this, "Show Ping as Number").changeValue(false).withHierarchyParents(this.showPing);
      this.pingTextShadow = new Setting(this, "Ping Text Shadow")
         .changeValue(true)
         .setCustomizationLevel(SettingsLevel.MEDIUM)
         .withHierarchyParents(this.showPingAsNumber);
      this.dynamicPingNumberColor = new Setting(this, "Dynamic Ping Number Color")
         .changeValue(true)
         .setCustomizationLevel(SettingsLevel.MEDIUM)
         .withHierarchyParents(this.showPingAsNumber);
      this.staticPingColor = new Setting(this, "Static Ping Color")
         .changeValue(-171)
         .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
         .setCustomizationLevel(SettingsLevel.ADVANCED)
         .withInvertedHierarchyParents(this.dynamicPingNumberColor);
      this.lowPingColor = new Setting(this, "Low Ping Color")
         .changeValue(-11141291)
         .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
         .setCustomizationLevel(SettingsLevel.ADVANCED)
         .withHierarchyParents(this.dynamicPingNumberColor);
      this.mediumPingColor = new Setting(this, "Medium Ping Color")
         .changeValue(-171)
         .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
         .setCustomizationLevel(SettingsLevel.ADVANCED)
         .withHierarchyParents(this.dynamicPingNumberColor);
      this.highPingColor = new Setting(this, "High Ping Color")
         .changeValue(-43691)
         .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
         .setCustomizationLevel(SettingsLevel.ADVANCED)
         .withHierarchyParents(this.dynamicPingNumberColor);
      this.extremelyHighPingColor = new Setting(this, "Extremely High Ping Color")
         .changeValue(-5636096)
         .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
         .setCustomizationLevel(SettingsLevel.ADVANCED)
         .withHierarchyParents(this.dynamicPingNumberColor);
      this.setDescription("Customize the player list tab.");
      this.setKeywords("Tab");
      this.setCategories(ModCategory.HUD, ModCategory.SERVER);
      this.addEventHandler(ScaledResolutionScoreObjectiveScoreboardIntEvent.class, this::onScaledResolutionScoreObjectiveScoreboardIntEvent);
      this.setEnabledByDefault(true);
   }
}
