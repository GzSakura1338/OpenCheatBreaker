package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.ScaledResolutionEvent3;
import com.cheatbreaker.client.event.ScaledResolutionEvent7;
import com.cheatbreaker.client.mod.AbstractHudMod;
import com.cheatbreaker.client.mod.ModCategory;
import com.cheatbreaker.client.mod.ModForceState;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.mod.setting.SettingGroup;
import com.cheatbreaker.client.mod.setting.SettingsLevel;
import com.cheatbreaker.client.ui.AnchorPoint;
import java.util.Collection;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumChatFormatting;

public class ScoreboardMod extends AbstractHudMod {
   public Pattern pattern = Pattern.compile("(?i)§[0-9A-F]");
   public Pattern pattern2 = Pattern.compile("(?i)§[1-69A-E]");
   public Setting backgroundOptions;
   public Setting showBackground;
   public static ModForceState modForceState = ModForceState.NEUTRAL;
   public Setting backgroundWidthPadding;
   public Setting showBorder;
   public Setting borderThickness;
   public Setting generalOptions;
   public Setting removeScoreboardNumbers;
   public Setting showScoreboardTitle;
   public Setting persistentScoreboardTitle;
   public Setting textShadow;
   public Setting colorOptions;
   public Setting stripColorFormatting;
   public Setting textColor;
   public Setting scoreboardNumbersColor;
   public Setting topBackgroundColor;
   public Setting backgroundColor;
   public Setting borderColor;

   @Override
   public void renderPreview(int n, int n2, int n3, int n4) {
      Gui.drawRect(n + 20, n2 + n4 / 2 - 44, n + n3 - 20, n2 + n4 / 2 - 6, 1862270976);
      Minecraft.getMinecraft().fontRendererObj.computeInt2("Score", n + n3 / 2, n2 + n4 / 2 - 40, -1);
      Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow("Steve", n + 24, n2 + n4 / 2 - 28, -1);
      Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow("Alex", n + 24, n2 + n4 / 2 - 18, -1);
      Minecraft.getMinecraft().fontRendererObj.computeInt2(EnumChatFormatting.RED + "0", n + n3 - 26, n2 + n4 / 2 - 18, -1);
      Minecraft.getMinecraft().fontRendererObj.computeInt2(EnumChatFormatting.RED + "1", n + n3 - 26, n2 + n4 / 2 - 28, -1);
   }

   public String formatText(String str) {
      if (str == null) {
         return null;
      } else {
         return this.stripColorFormatting.getValue().equals("OFF")
            ? str
            : (this.stripColorFormatting.getValue().equals("ALL") ? this.pattern : this.pattern2).matcher(str).replaceAll("§r");
      }
   }

   public ScoreboardMod() {
      super("Scoreboard");
      this.setAnchorPointAndDefault(AnchorPoint.RIGHT_MIDDLE);
      this.setApplyDefaultColorOnEnable(false);
      this.backgroundOptions = new SettingGroup(this, "Background Options").changeValue(true);
      this.showBackground = new Setting(this, "Show Background").withHierarchyParents(this.backgroundOptions).changeValue(true);
      this.backgroundWidthPadding = new Setting(this, "Background Width Padding")
         .withHierarchyParents(this.backgroundOptions)
         .changeValue(6.0F)
         .setRange(0.0F, 10.0F)
         .setUnit("px")
         .setCustomizationLevel(SettingsLevel.MEDIUM);
      this.showBorder = new Setting(this, "Show Border").withHierarchyParents(this.showBackground).changeValue(false);
      this.borderThickness = new Setting(this, "Border Thickness")
         .withHierarchyParents(this.backgroundOptions)
         .changeValue(1.0F)
         .setRange(0.25F, 3.0F)
         .setUnit("px")
         .setCustomizationLevel(SettingsLevel.MEDIUM)
         .withHierarchyParents(this.showBorder);
      this.generalOptions = new SettingGroup(this, "General Options").changeValue(true);
      this.removeScoreboardNumbers = new Setting(this, "Remove Scoreboard numbers").withHierarchyParents(this.generalOptions).changeValue(true);
      this.showScoreboardTitle = new Setting(this, "Show Scoreboard Title").withHierarchyParents(this.generalOptions).changeValue(true);
      this.persistentScoreboardTitle = new Setting(this, "Persistent Scoreboard Title").withHierarchyParents(this.showScoreboardTitle).changeValue(false);
      this.textShadow = new Setting(this, "Text Shadow").withHierarchyParents(this.generalOptions).changeValue(false);
      this.colorOptions = new SettingGroup(this, "Color Options").changeValue(true);
      this.stripColorFormatting = new Setting(this, "Strip Color Formatting")
         .setDescription("Remove all color formatting so you can set your own color.")
         .changeValue("OFF")
         .setStringValues("OFF", "Not Neutral", "ALL")
         .withHierarchyParents(this.colorOptions);
      this.textColor = new Setting(this, "Text Color")
         .changeValue(-1)
         .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
         .withParents(this.colorOptions)
         .setCondition(() -> !this.stripColorFormatting.getValue().equals("OFF"));
      this.scoreboardNumbersColor = new Setting(this, "Scoreboard Numbers Color")
         .changeValue(-43691)
         .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
         .withParents(this.colorOptions)
         .withInvertedParents(this.removeScoreboardNumbers);
      this.topBackgroundColor = new Setting(this, "Top Background Color")
         .changeValue(1610612736)
         .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
         .withParents(this.colorOptions, this.showBackground, this.showScoreboardTitle);
      this.backgroundColor = new Setting(this, "Background Color")
         .changeValue(1342177280)
         .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
         .withParents(this.colorOptions, this.showBackground);
      this.borderColor = new Setting(this, "Border Color")
         .changeValue(Integer.MIN_VALUE)
         .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
         .withParents(this.colorOptions, this.showBackground, this.showBorder);
      this.setDescription("Move and customize the Minecraft scoreboard to your liking.");
      this.setKeywords("Sidebar");
      this.setCategories(ModCategory.HUD, ModCategory.SERVER);
      this.setShowLabelInList(false);
      this.addEventHandler(ScaledResolutionEvent7.class, this::onScaledResolutionEvent7);
      this.addEventHandler(ScaledResolutionEvent3.class, this::onScaledResolutionEvent3);
      this.setEnabledByDefault(true);
   }

   public void onScaledResolutionEvent7(ScaledResolutionEvent7 scaledResolutionEvent7) {
      if (this.isRenderHud()) {
         ScoreObjective objectiveInDisplaySlot = this.mc.theWorld.getScoreboard().getObjectiveInDisplaySlot(1);
         if (objectiveInDisplaySlot != null) {
            Scoreboard scoreboard = objectiveInDisplaySlot.getScoreboard();
            Collection sortedScores = scoreboard.getSortedScores(objectiveInDisplaySlot);
            if (sortedScores.size() <= 15 && (sortedScores.size() > 0 || this.persistentScoreboardTitle.getBooleanValue() && this.showScoreboardTitle.getBooleanValue())) {
               return;
            }
         }

         GlStateManager.pushMatrix();
         this.applyHudTransformForSize(scaledResolutionEvent7.getScaledResolution());
         Scoreboard scoreboard = new Scoreboard();
         ScoreObjective scoreObjective = new ScoreObjective(scoreboard, CheatBreaker.clientName, IScoreObjectiveCriteria.DUMMY);
         scoreObjective.setDisplayName(EnumChatFormatting.RED + "" + EnumChatFormatting.BOLD + "Cheat" + EnumChatFormatting.WHITE + EnumChatFormatting.BOLD + "Breaker");
         scoreboard.getValueFromObjective("Steve", scoreObjective);
         scoreboard.getValueFromObjective("Alex", scoreObjective);
         this.render(scoreObjective);
         GlStateManager.popMatrix();
      }
   }

   public boolean check10() {
      return modForceState == ModForceState.NEUTRAL ? (Boolean)this.removeScoreboardNumbers.getValue() : modForceState == ModForceState.FORCED_OFF;
   }

   public void onScaledResolutionEvent3(ScaledResolutionEvent3 scaledResolutionEvent3) {
      if (this.shouldRender()) {
         GlStateManager.pushMatrix();
         this.applyHudTransformForSize(scaledResolutionEvent3.getScaledResolution());
         ScoreObjective objectiveInDisplaySlot = this.mc.theWorld.getScoreboard().getObjectiveInDisplaySlot(1);
         if (objectiveInDisplaySlot != null) {
            Scoreboard scoreboard = objectiveInDisplaySlot.getScoreboard();
            Collection sortedScores = scoreboard.getSortedScores(objectiveInDisplaySlot);
            if (sortedScores.size() <= 15 && (sortedScores.size() > 0 || this.persistentScoreboardTitle.getBooleanValue() && this.showScoreboardTitle.getBooleanValue())) {
               this.render(objectiveInDisplaySlot);
            }
         }

         GlStateManager.popMatrix();
      }
   }

   public void render(ScoreObjective scoreObjective) {
      GlStateManager.enableBlend();
      Scoreboard scoreboard = scoreObjective.getScoreboard();
      Collection sortedScores = scoreboard.getSortedScores(scoreObjective);
      boolean flag = !this.check10();
      String displayName = scoreObjective.getDisplayName();
      int stringWidth = this.mc.fontRendererObj.getStringWidth(displayName);

      for (Score score : (Iterable<Score>)sortedScores) {
         ScorePlayerTeam playersTeam = scoreboard.getPlayersTeam(score.getPlayerName());
         String str = ScorePlayerTeam.formatPlayerName(playersTeam, score.getPlayerName()) + (flag ? ": " + EnumChatFormatting.RED + score.getScorePoints() : "");
         stringWidth = Math.max(stringWidth, this.mc.fontRendererObj.getStringWidth(str));
      }

      if (this.showBackground.getBooleanValue()) {
         if (this.showScoreboardTitle.getBooleanValue()) {
            Gui.drawRect(0.0F, 0.0F, this.hudWidth, this.mc.fontRendererObj.FONT_HEIGHT, this.topBackgroundColor.getColorValue());
         }

         Gui.drawRect(
            0.0F,
            this.showScoreboardTitle.getBooleanValue() ? this.mc.fontRendererObj.FONT_HEIGHT : 0.0F,
            this.hudWidth,
            this.hudHeight,
            this.backgroundColor.getColorValue()
         );
         if (this.showBorder.getBooleanValue()) {
            float floatValue = this.borderThickness.getFloatValue();
            Gui.drawInnerBorder(-floatValue, -floatValue, floatValue + this.hudWidth, floatValue + this.hudHeight, floatValue, this.borderColor.getColorValue());
         }
      }

      if (this.showScoreboardTitle.getBooleanValue()) {
         this.mc
            .fontRendererObj
            .drawString(
               this.formatText(displayName),
               this.hudWidth / 2.0F - this.mc.fontRendererObj.getStringWidth(displayName) / 2.0F,
               1.0F,
               !this.stripColorFormatting.getValue().equals("OFF") ? this.textColor.getColorValue() : -1,
               (Boolean)this.textShadow.getValue()
            );
      }

      GlStateManager.translate(0.0F, this.hudHeight, 0.0F);
      int n = 0;

      for (Score score : (Iterable<Score>)sortedScores) {
         ScorePlayerTeam playersTeam = scoreboard.getPlayersTeam(score.getPlayerName());
         String formatPlayerName = ScorePlayerTeam.formatPlayerName(playersTeam, score.getPlayerName());
         float f = -(++n) * this.mc.fontRendererObj.FONT_HEIGHT;
         this.mc
            .fontRendererObj
            .drawString(
               this.formatText(formatPlayerName),
               1.0F + this.backgroundWidthPadding.getFloatValue() / 2.0F,
               f,
               !this.stripColorFormatting.getValue().equals("OFF") ? this.textColor.getColorValue() : -1,
               (Boolean)this.textShadow.getValue()
            );
         if (flag) {
            String str = "" + score.getScorePoints();
            this.mc
               .fontRendererObj
               .drawString(
                  str,
                  this.hudWidth - this.mc.fontRendererObj.getStringWidth(str) - 1.0F - this.backgroundWidthPadding.getFloatValue() / 2.0F,
                  f,
                  this.scoreboardNumbersColor.getColorValue(),
                  (Boolean)this.textShadow.getValue()
               );
         }
      }

      GlStateManager.disableBlend();
      this.hudWidth = stringWidth + 2 + this.backgroundWidthPadding.getFloatValue();
      this.hudHeight = sortedScores.size() * this.mc.fontRendererObj.FONT_HEIGHT + (this.showScoreboardTitle.getBooleanValue() ? 10 : 1);
   }
}
