package net.minecraft.client.gui.achievement;

import com.cheatbreaker.client.CheatBreaker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.stats.Achievement;
import net.minecraft.util.ResourceLocation;

public class GuiAchievement extends Gui {
   public static ResourceLocation achievementBg = new ResourceLocation("textures/gui/achievement/achievement_background.png");
   public Minecraft mc;
   public int width;
   public int height;
   public String achievementTitle;
   public String achievementDescription;
   public Achievement theAchievement;
   public long notificationTime;
   public RenderItem renderItem;
   public boolean permanentNotification;

   public GuiAchievement(Minecraft mc) {
      this.mc = mc;
      this.renderItem = mc.getRenderItem();
   }

   public void displayAchievement(Achievement ach) {
      this.achievementTitle = I18n.format("achievement.get");
      this.achievementDescription = ach.getStatName().getUnformattedText();
      this.notificationTime = Minecraft.getSystemTime();
      this.theAchievement = ach;
      this.permanentNotification = false;
   }

   public void displayUnformattedAchievement(Achievement achievementIn) {
      this.achievementTitle = achievementIn.getStatName().getUnformattedText();
      this.achievementDescription = achievementIn.getDescription();
      this.notificationTime = Minecraft.getSystemTime() + 2500L;
      this.theAchievement = achievementIn;
      this.permanentNotification = true;
   }

   public void updateAchievementWindowScale() {
      GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
      GlStateManager.matrixMode(5889);
      GlStateManager.loadIdentity();
      GlStateManager.matrixMode(5888);
      GlStateManager.loadIdentity();
      this.width = this.mc.displayWidth;
      this.height = this.mc.displayHeight;
      ScaledResolution scaledResolution = new ScaledResolution(this.mc);
      this.width = scaledResolution.getScaledWidth();
      this.height = scaledResolution.getScaledHeight();
      GlStateManager.clear(256);
      GlStateManager.matrixMode(5889);
      GlStateManager.loadIdentity();
      GlStateManager.ortho(0.0, this.width, this.height, 0.0, 1000.0, 3000.0);
      GlStateManager.matrixMode(5888);
      GlStateManager.loadIdentity();
      GlStateManager.translate(0.0F, 0.0F, -2000.0F);
   }

   public void updateAchievementWindow() {
      if (this.theAchievement != null
         && this.notificationTime != 0L
         && Minecraft.getMinecraft().thePlayer != null
         && CheatBreaker.getInstance().getKeybindSettings().showAchievements.getBooleanValue()) {
         double d = (Minecraft.getSystemTime() - this.notificationTime) / 3000.0;
         if (!this.permanentNotification) {
            if (d < 0.0 || d > 1.0) {
               this.notificationTime = 0L;
               return;
            }
         } else if (d > 0.5) {
            d = 0.5;
         }

         this.updateAchievementWindowScale();
         GlStateManager.disableDepth();
         GlStateManager.depthMask(false);
         double d2 = d * 2.0;
         if (d2 > 1.0) {
            d2 = 2.0 - d2;
         }

         d2 *= 4.0;
         d2 = 1.0 - d2;
         if (d2 < 0.0) {
            d2 = 0.0;
         }

         d2 *= d2;
         d2 *= d2;
         int n = this.width - 160;
         int n2 = 0 - (int)(d2 * 36.0);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.enableTexture2D();
         this.mc.getTextureManager().bindTexture(achievementBg);
         GlStateManager.disableLighting();
         this.drawTexturedModalRect(n, n2, 96, 202, 160, 32);
         if (this.permanentNotification) {
            this.mc.fontRendererObj.drawSplitString(this.achievementDescription, n + 30, n2 + 7, 120, -1);
         } else {
            this.mc.fontRendererObj.drawString(this.achievementTitle, n + 30, n2 + 7, -256);
            this.mc.fontRendererObj.drawString(this.achievementDescription, n + 30, n2 + 18, -1);
         }

         RenderHelper.enableGUIStandardItemLighting();
         GlStateManager.disableLighting();
         GlStateManager.enableRescaleNormal();
         GlStateManager.enableColorMaterial();
         GlStateManager.enableLighting();
         this.renderItem.renderItemAndEffectIntoGUI(this.theAchievement.theItemStack, n + 8, n2 + 8);
         GlStateManager.disableLighting();
         GlStateManager.depthMask(true);
         GlStateManager.enableDepth();
      }
   }

   public void clearAchievements() {
      this.theAchievement = null;
      this.notificationTime = 0L;
   }
}
