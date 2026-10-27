package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.event.EntityTNTPrimedRenderTNTPrimedDoubleFloatEvent;
import com.cheatbreaker.client.mod.AbstractMod;
import com.cheatbreaker.client.mod.ModCategory;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.mod.setting.SettingGroup;
import com.cheatbreaker.client.mod.setting.SettingsLevel;
import java.awt.Color;
import java.text.DecimalFormat;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderTNTPrimed;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TNTTimerMod extends AbstractMod {
   public DecimalFormat decimalFormat = new DecimalFormat("0.00");
   public Setting timerOptions;
   public Setting fuseOffset;
   public Setting adjustForBedwarsAndSkywars;
   public Setting textOptions;
   public Setting prefixString;
   public Setting suffixString;
   public Setting textShadow;
   public Setting dynamicTextColor;
   public boolean flag;
   public Setting staticTextColor;
   public Setting backgroundOptions;
   public Setting showBackground;
   public Setting backgroundColor;

   public void onEntityTNTPrimedRenderTNTPrimedDoubleFloatEvent(EntityTNTPrimedRenderTNTPrimedDoubleFloatEvent entityTNTPrimedRenderTNTPrimedDoubleFloatEvent) {
      this.render(entityTNTPrimedRenderTNTPrimedDoubleFloatEvent.getRenderTNTPrimed(), entityTNTPrimedRenderTNTPrimedDoubleFloatEvent.getEntityTNTPrimed(), entityTNTPrimedRenderTNTPrimedDoubleFloatEvent.getX(), entityTNTPrimedRenderTNTPrimedDoubleFloatEvent.getY(), entityTNTPrimedRenderTNTPrimedDoubleFloatEvent.getZ(), entityTNTPrimedRenderTNTPrimedDoubleFloatEvent.getPartialTicks());
   }

   public void render(RenderTNTPrimed renderTNTPrimed, EntityTNTPrimed entityTNTPrimed, double d, double d2, double d3, float f) {
      if (this.mc.getCurrentServerData() != null && this.mc.theWorld != null) {
         this.flag = this.mc.getCurrentServerData().serverIP.toLowerCase().contains("hypixel")
               && this.adjustForBedwarsAndSkywars.getValue().equals("Hypixel Only")
            || this.adjustForBedwarsAndSkywars.getValue().equals("Always");
      }

      float f2 = 0.02666667F;
      int n = (int)(this.fuseOffset.getFloatValue() * 20.0F) + (this.flag ? entityTNTPrimed.fuse - 28 : entityTNTPrimed.fuse);
      if (n >= 1 && entityTNTPrimed.getDistanceSqToEntity(renderTNTPrimed.getRenderManager().livingPlayer) <= 4095.0) {
         FontRenderer fontRendererFromRenderManager = renderTNTPrimed.getFontRendererFromRenderManager();
         GlStateManager.pushMatrix();
         GlStateManager.translate((float)d + 0.0F, (float)d2 + entityTNTPrimed.height + 0.5F, (float)d3);
         GL11.glNormal3f(0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(-renderTNTPrimed.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(renderTNTPrimed.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
         GlStateManager.scale(-f2, -f2, f2);
         GlStateManager.disableLighting();
         GlStateManager.depthMask(false);
         GlStateManager.disableDepth();
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
         Tessellator instance = Tessellator.getInstance();
         WorldRenderer worldRenderer = instance.getWorldRenderer();
         GlStateManager.enableDepth();
         GlStateManager.depthMask(true);
         GlStateManager.disableTexture2D();
         int n2 = fontRendererFromRenderManager.getStringWidth(this.createStringBuilder(n, f)) >> 1;
         float f3 = (this.backgroundColor.getColorValue() >> 24 & 0xFF) / 255.0F;
         float f4 = (this.backgroundColor.getColorValue() >> 16 & 0xFF) / 255.0F;
         float f5 = (this.backgroundColor.getColorValue() >> 8 & 0xFF) / 255.0F;
         float f6 = (this.backgroundColor.getColorValue() & 0xFF) / 255.0F;
         if (this.showBackground.getBooleanValue()) {
            worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
            worldRenderer.pos(-n2 - 1, -1.0, 0.0).color(f4, f5, f6, f3).endVertex();
            worldRenderer.pos(-n2 - 1, 8.0, 0.0).color(f4, f5, f6, f3).endVertex();
            worldRenderer.pos(n2 + 1, 8.0, 0.0).color(f4, f5, f6, f3).endVertex();
            worldRenderer.pos(n2 + 1, -1.0, 0.0).color(f4, f5, f6, f3).endVertex();
            instance.draw();
         }

         GlStateManager.enableTexture2D();
         fontRendererFromRenderManager.drawString(
            this.createStringBuilder(n, f),
            -fontRendererFromRenderManager.getStringWidth(this.createStringBuilder(n, f)) >> 1,
            0.0F,
            this.createColor(n).getRGB(),
            this.textShadow.getBooleanValue()
         );
         GlStateManager.enableLighting();
         GlStateManager.disableBlend();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.popMatrix();
      }
   }

   public TNTTimerMod() {
      super("TNT Timer");
      this.setEnabledByDefault(false);
      this.setDescription("Shows a timer above TNT.");
      this.setAuthors("Sk1er");
      this.setKeywords("TNT Countdown");
      this.setCategories(ModCategory.HUD);
      this.setIcon(new ResourceLocation("client/icons/mods/tnt.png"), 36, 40);
      this.timerOptions = new SettingGroup(this, "Timer Options").changeValue(true);
      this.fuseOffset = new Setting(this, "Fuse Offset").changeValue(0.0F).setRange(-4.0F, 4.0F).setUnit("s").withParents(this.timerOptions);
      this.adjustForBedwarsAndSkywars = new Setting(this, "Adjust for Bedwars and Skywars")
         .changeValue("Hypixel Only")
         .setStringValues("Never", "Hypixel Only", "Always")
         .withParents(this.timerOptions);
      this.textOptions = new SettingGroup(this, "Text Options").changeValue(true);
      this.prefixString = new Setting(this, "Prefix String").changeValue("").withParents(this.textOptions);
      this.suffixString = new Setting(this, "Suffix String").changeValue("").withParents(this.textOptions);
      this.textShadow = new Setting(this, "Text Shadow").changeValue(false).withParents(this.textOptions);
      this.dynamicTextColor = new Setting(this, "Dynamic Text Color").changeValue(true).withParents(this.textOptions);
      this.staticTextColor = new Setting(this, "Static Text Color")
         .changeValue(-1)
         .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
         .withInvertedHierarchyParents(this.dynamicTextColor);
      this.backgroundOptions = new SettingGroup(this, "Background Options").changeValue(true);
      this.showBackground = new Setting(this, "Show Background")
         .changeValue(true)
         .setCustomizationLevel(SettingsLevel.SIMPLE)
         .withParents(this.backgroundOptions);
      this.backgroundColor = new Setting(this, "Background Color")
         .changeValue(1862270976)
         .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
         .setCustomizationLevel(SettingsLevel.SIMPLE)
         .withHierarchyParents(this.showBackground);
      this.addEventHandler(EntityTNTPrimedRenderTNTPrimedDoubleFloatEvent.class, this::onEntityTNTPrimedRenderTNTPrimedDoubleFloatEvent);
   }

   public Color createColor(int n) {
      return this.dynamicTextColor.getBooleanValue()
         ? new Color(1.0F - Math.min(n / (this.flag ? 52.0F : 80.0F), 1.0F), Math.min(n / (this.flag ? 52.0F : 80.0F), 1.0F), 0.0F)
         : this.staticTextColor.toColor(this.staticTextColor.getColorValue());
   }

   public String createStringBuilder(int n, float f) {
      return this.prefixString.getValue() + this.decimalFormat.format((n - f) / 20.0F) + this.suffixString.getValue();
   }
}
