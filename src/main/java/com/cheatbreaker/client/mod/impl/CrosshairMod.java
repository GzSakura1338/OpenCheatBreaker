package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.ScaledResolutionIntEvent;
import com.cheatbreaker.client.mod.AbstractMod;
import com.cheatbreaker.client.mod.ModCategory;
import com.cheatbreaker.client.mod.setting.CrosshairSetting;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.mod.setting.SettingGroup;
import com.cheatbreaker.client.mod.setting.SettingsLevel;
import com.cheatbreaker.client.render.RenderUtils;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

public class CrosshairMod extends AbstractMod {
   public Setting crosshairPreview;
   public Setting previewBackground;
   public Setting generalOptions;
   public Setting scale;
   public Setting guiScale;
   public Setting usePackCrosshair;
   public Setting cross;
   public Setting dot;
   public Setting outline;
   public Setting thickness;
   public Setting outlineThickness;
   public Setting size;
   public Setting gap;
   public Setting dotSize;
   public Setting colorOptions;
   public Setting vanillaBlending;
   public Setting dynamicColors;
   public Setting crosshairColor;
   public Setting dotColor;
   public Setting outlineColor;
   public Setting friendlyColor;
   public Setting enemyColor;

   @Override
   public void renderPreview(int n, int n2, int n3, int n4) {
      GlStateManager.pushMatrix();
      GL11.glScalef(2.0F, 2.0F, 0.0F);
      float f = 6.0F;
      float f2 = 4.0F;
      float f3 = 2.0F;
      float f4 = (n + n3 / 2) / 2.0F;
      float f5 = (n2 + n4 / 2 - 27) / 2.0F;
      Gui.drawRectWithOuterBorder(f4 - f2 - f, f5 - f3 / 2.0F, f4 - f2, f5 + f3 / 2.0F, 0.5F, -1358954496, -1);
      Gui.drawRectWithOuterBorder(f4 + f2, f5 - f3 / 2.0F, f4 + f2 + f, f5 + f3 / 2.0F, 0.5F, -1358954496, -1);
      Gui.drawRectWithOuterBorder(f4 - f3 / 2.0F, f5 - f2 - f, f4 + f3 / 2.0F, f5 - f2, 0.5F, -1358954496, -1);
      Gui.drawRectWithOuterBorder(f4 - f3 / 2.0F, f5 + f2, f4 + f3 / 2.0F, f5 + f2 + f, 0.5F, -1358954496, -1);
      GlStateManager.popMatrix();
   }

   public void onScaledResolutionIntEvent(ScaledResolutionIntEvent scaledResolutionIntEvent) {
      this.render(scaledResolutionIntEvent.getIntValue1(), scaledResolutionIntEvent.getIntValue2(), true);
      scaledResolutionIntEvent.setFlag(true);
   }

   public void render(float f, float f2, boolean flag) {
      GlStateManager.pushMatrix();
      float floatValue = this.scale.getFloatValue();
      if (flag) {
         if (!this.guiScale.getValue().equals("Global")) {
            switch (this.guiScale.getStringValue()) {
               case "Small":
                  floatValue = floatValue * 0.5F / CheatBreaker.getInstance().getUiManager().getFloatValue();
                  break;
               case "Normal":
                  floatValue /= CheatBreaker.getInstance().getUiManager().getFloatValue();
                  break;
               case "Large":
                  floatValue = floatValue * 1.5F / CheatBreaker.getInstance().getUiManager().getFloatValue();
                  break;
               case "Auto":
                  floatValue = floatValue * 2.0F / CheatBreaker.getInstance().getUiManager().getFloatValue();
            }
         } else {
            floatValue *= this.getHudScale();
         }
      }

      float f3 = f / floatValue;
      float f4 = f2 / floatValue;
      GL11.glScalef(floatValue, floatValue, floatValue);
      float floatValue2 = this.size.getFloatValue();
      float floatValue3 = this.gap.getFloatValue();
      float floatValue4 = this.thickness.getFloatValue();
      float floatValue5 = this.outlineThickness.getFloatValue();
      float floatValue6 = this.dotSize.getFloatValue();
      int intValue = this.getIntValue();
      boolean booleanValue = this.outline.getBooleanValue();
      GlStateManager.color((intValue >> 16 & 0xFF) / 255.0F, (intValue >> 8 & 0xFF) / 255.0F, (intValue & 0xFF) / 255.0F, (intValue >> 24 & 0xFF) / 255.0F);
      this.mc.getTextureManager().bindTexture(Gui.icons);
      GL11.glEnable(3008);
      if ((Boolean)this.usePackCrosshair.getValue()) {
         if ((Boolean)this.vanillaBlending.getValue()) {
            OpenGlHelper.glBlendFunc(775, 769, 1, 0);
         } else {
            OpenGlHelper.glBlendFunc(768, 769, 1, 0);
         }

         this.mc.ingameGUI.drawTexturedModalRect((float)(f3 - 7.0F), (float)(f4 - 7.0F), 0, 0, 16, 16);
      } else {
         if (this.vanillaBlending.getBooleanValue()) {
            OpenGlHelper.glBlendFunc(775, 769, 1, 0);
         }

         if (this.dot.getBooleanValue()) {
            RenderUtils.drawRoundedRectFilledPrecise(f3 - floatValue6, f4 - floatValue6, f3 + floatValue6, f4 + floatValue6, 0.0, intValue);
         }

         if (this.cross.getBooleanValue()) {
            RenderUtils.drawRoundedRectFilledPrecise(f3 - floatValue3 - floatValue2, f4 - floatValue4 / 2.0F, f3 - floatValue3, f4 + floatValue4 / 2.0F, 0.0, intValue);
            RenderUtils.drawRoundedRectFilledPrecise(f3 + floatValue3, f4 - floatValue4 / 2.0F, f3 + floatValue3 + floatValue2, f4 + floatValue4 / 2.0F, 0.0, intValue);
            RenderUtils.drawRoundedRectFilledPrecise(f3 - floatValue4 / 2.0F, f4 - floatValue3 - floatValue2, f3 + floatValue4 / 2.0F, f4 - floatValue3, 0.0, intValue);
            RenderUtils.drawRoundedRectFilledPrecise(f3 - floatValue4 / 2.0F, f4 + floatValue3, f3 + floatValue4 / 2.0F, f4 + floatValue3 + floatValue2, 0.0, intValue);
         }

         if (booleanValue) {
            if (this.cross.getBooleanValue()) {
               Gui.drawRectWithOuterBorder(
                  f3 - floatValue3 - floatValue2, f4 - floatValue4 / 2.0F, f3 - floatValue3, f4 + floatValue4 / 2.0F, floatValue5, this.outlineColor.getColorValue(), 0
               );
               Gui.drawRectWithOuterBorder(
                  f3 + floatValue3, f4 - floatValue4 / 2.0F, f3 + floatValue3 + floatValue2, f4 + floatValue4 / 2.0F, floatValue5, this.outlineColor.getColorValue(), 0
               );
               Gui.drawRectWithOuterBorder(
                  f3 - floatValue4 / 2.0F, f4 - floatValue3 - floatValue2, f3 + floatValue4 / 2.0F, f4 - floatValue3, floatValue5, this.outlineColor.getColorValue(), 0
               );
               Gui.drawRectWithOuterBorder(
                  f3 - floatValue4 / 2.0F, f4 + floatValue3, f3 + floatValue4 / 2.0F, f4 + floatValue3 + floatValue2, floatValue5, this.outlineColor.getColorValue(), 0
               );
            }

            if (this.dot.getBooleanValue()) {
               Gui.drawRectWithOuterBorder(f3 - floatValue6, f4 - floatValue6, f3 + floatValue6, f4 + floatValue6, floatValue5, this.outlineColor.getColorValue(), 0);
            }
         }
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      OpenGlHelper.glBlendFunc(770, 771, 1, 0);
      GlStateManager.popMatrix();
   }

   public int getIntValue() {
      if (this.dynamicColors.getBooleanValue()) {
         Entity pointedEntity = Minecraft.getMinecraft().pointedEntity;
         if (pointedEntity instanceof EntityMob) {
            return this.enemyColor.getColorValue();
         }

         if (pointedEntity instanceof EntityAnimal) {
            return this.friendlyColor.getColorValue();
         }

         if (pointedEntity instanceof EntityPlayer) {
            UUID uniqueID = pointedEntity.getUniqueID();
            if (CheatBreaker.getInstance().getModManager().teamManager.getTeamMemberList().toString().contains(uniqueID.toString())) {
               return this.friendlyColor.getColorValue();
            }

            if (!pointedEntity.isInvisibleToPlayer(Minecraft.getMinecraft().thePlayer)) {
               return this.enemyColor.getColorValue();
            }
         }
      }

      return this.crosshairColor.getColorValue();
   }

   public CrosshairMod() {
      super("Crosshair");
      this.setEnabledByDefault(false);
      this.setApplyDefaultColorOnEnable(false);
      this.setShowLabelInList(false);
      this.setCategories(ModCategory.HUD);
      this.crosshairPreview = new SettingGroup(this, "Crosshair Preview").changeValue(true);
      this.previewBackground = new CrosshairSetting(this, "Preview Background")
         .setDescription("Change the preview background to see what the crosshair can look like in multiple environments.")
         .changeValue("Birch")
         .setStringValues("Birch", "Roofed", "Swamp", "Hills", "Desert", "Mesa", "Nether", "Sky")
         .withParents(this.crosshairPreview);
      this.generalOptions = new SettingGroup(this, "General Options").changeValue(true);
      this.scale = new Setting(this, "Scale")
         .setDescription("Change the scale of the mod.")
         .changeValue(1.0F)
         .setRange(0.5F, 1.5F)
         .setUnit("x")
         .setCenterLabel("1.0x")
         .setIconNames("letter-t-64", "letter-t-64")
         .setIconScales(6.0F, 8.0F)
         .setIconXOffsets(0.0F, -2.0F)
         .setIconYOffsets(2.0F, -1.5F)
         .withHierarchyParents(this.generalOptions);
      this.guiScale = new Setting(this, "GUI Scale")
         .setDescription("Change the scale of the mod.")
         .changeValue("Normal")
         .setStringValues("Global", "Default", "Small", "Normal", "Large", "Auto")
         .setCustomizationLevel(SettingsLevel.MEDIUM)
         .withHierarchyParents(this.generalOptions);
      this.usePackCrosshair = new Setting(this, "Use Pack Crosshair").changeValue(false).withHierarchyParents(this.generalOptions);
      this.cross = new Setting(this, "Cross").changeValue(true).withInvertedHierarchyParents(this.usePackCrosshair);
      this.dot = new Setting(this, "Dot").changeValue(false).withInvertedHierarchyParents(this.usePackCrosshair);
      this.outline = new Setting(this, "Outline")
         .setDescription("Add an outline around the crosshair.")
         .changeValue(false)
         .withOptionalHierarchyParents(this.dot, this.cross);
      this.thickness = new Setting(this, "Thickness")
         .setDescription("Change the thickness of the crosshair.")
         .changeValue(2.0F)
         .setRange(0.5F, 3.0F)
         .setUnit("px")
         .withHierarchyParents(this.cross);
      this.outlineThickness = new Setting(this, "Outline Thickness")
         .setDescription("Change the outline thickness of the crosshair.")
         .changeValue(0.5F)
         .setRange(0.5F, 3.0F)
         .setUnit("px")
         .withHierarchyParents(this.outline);
      this.size = new Setting(this, "Size")
         .setDescription("Change the size of the crosshair.")
         .changeValue(4.0F)
         .setRange(1.0F, 10.0F)
         .setUnit("px")
         .withHierarchyParents(this.cross);
      this.gap = new Setting(this, "Gap")
         .setDescription("Change how distant the cross is from the center.")
         .changeValue(2.0F)
         .setRange(1.0F, 7.5F)
         .setUnit("px")
         .withHierarchyParents(this.cross);
      this.dotSize = new Setting(this, "Dot Size")
         .setDescription("Change the size of the dot.")
         .changeValue(1.0F)
         .setRange(0.5F, 3.0F)
         .setUnit("px")
         .withHierarchyParents(this.dot);
      this.colorOptions = new SettingGroup(this, "Color Options").changeValue(true).withOptionalParents(this.dot, this.cross, this.usePackCrosshair);
      this.vanillaBlending = new Setting(this, "Vanilla Blending")
         .setDescription("Make the crosshair's color invert depending on what you are looking at.")
         .changeValue(true)
         .withHierarchyParents(this.colorOptions);
      this.dynamicColors = new Setting(this, "Dynamic Colors")
         .setDescription("Change the color of the crosshair depending on what you are looking at.")
         .changeValue(false)
         .withHierarchyParents(this.colorOptions);
      this.crosshairColor = new Setting(this, "Crosshair Color")
         .setDescription("Change the color of the crosshair.")
         .changeValue(-1)
         .withParents(this.colorOptions)
         .withOptionalParents(this.cross, this.usePackCrosshair)
         .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE);
      this.dotColor = new Setting(this, "Dot Color")
         .setDescription("Change the color of the crosshair dot.")
         .changeValue(-1)
         .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
         .withInvertedParents(this.usePackCrosshair)
         .withParents(this.colorOptions, this.dot);
      this.outlineColor = new Setting(this, "Outline Color")
         .setDescription("Change the color of the crosshair outline.")
         .changeValue(-1358954496)
         .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
         .withInvertedParents(this.usePackCrosshair)
         .withParents(this.colorOptions, this.outline);
      this.friendlyColor = new Setting(this, "Friendly Color")
         .setDescription("The color when you are looking at friendly entities.")
         .changeValue(-13369549)
         .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
         .withHierarchyParents(this.dynamicColors);
      this.enemyColor = new Setting(this, "Enemy Color")
         .setDescription("The color when you are looking at enemies.")
         .changeValue(-52429)
         .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
         .withHierarchyParents(this.dynamicColors);
      this.setDescription("Replace the Vanilla crosshair with your own custom crosshair.");
      this.addEventHandler(ScaledResolutionIntEvent.class, this::onScaledResolutionIntEvent);
   }
}
