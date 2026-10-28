package net.minecraft.client.gui.achievement;

import com.cheatbreaker.client.CheatBreaker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.stats.Achievement;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

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
        this.renderItem = new RenderItem();
    }

    public void displayAchievement(Achievement achievement) {
        this.achievementTitle = I18n.format("achievement.get");
        this.achievementDescription = achievement.getStatName().getUnformattedText();
        this.notificationTime = Minecraft.getSystemTime();
        this.theAchievement = achievement;
        this.permanentNotification = false;
    }

    public void displayUnformattedAchievement(Achievement achievement) {
        this.achievementTitle = achievement.getStatName().getUnformattedText();
        this.achievementDescription = achievement.getDescription();
        this.notificationTime = Minecraft.getSystemTime() + 2500L;
        this.theAchievement = achievement;
        this.permanentNotification = true;
    }

    public void updateAchievementWindowScale() {
        GL11.glViewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
        GL11.glMatrixMode(5889);
        GL11.glLoadIdentity();
        GL11.glMatrixMode(5888);
        GL11.glLoadIdentity();
        this.width = this.mc.displayWidth;
        this.height = this.mc.displayHeight;
        ScaledResolution scaledResolution = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
        this.width = scaledResolution.getScaledWidth();
        this.height = scaledResolution.getScaledHeight();
        GL11.glClear(256);
        GL11.glMatrixMode(5889);
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0, this.width, this.height, 0.0, 1000.0, 3000.0);
        GL11.glMatrixMode(5888);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
    }

    public void updateAchievementWindow() {
        if (this.theAchievement != null
            && this.notificationTime != 0L
            && Minecraft.getMinecraft().thePlayer != null
            && (Boolean)CheatBreaker.getInstance().getKeybindSettings().showAchievements.getValue()) {
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
            GL11.glDisable(2929);
            GL11.glDepthMask(false);
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
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glEnable(3553);
            this.mc.getTextureManager().bindTexture(achievementBg);
            GL11.glDisable(2896);
            drawTexturedRect(n, n2, 96.0F, 202.0F, 160, 32);
            if (this.permanentNotification) {
                this.mc.fontRendererObj.drawSplitString(this.achievementDescription, n + 30, n2 + 7, 120, -1);
            } else {
                this.mc.fontRendererObj.drawString(this.achievementTitle, n + 30, n2 + 7, -256);
                this.mc.fontRendererObj.drawString(this.achievementDescription, n + 30, n2 + 18, -1);
            }

            RenderHelper.enableGUIStandardItemLighting();
            GL11.glDisable(2896);
            GL11.glEnable(32826);
            GL11.glEnable(2903);
            GL11.glEnable(2896);
            this.renderItem.renderItemAndEffectIntoGUI(this.mc.fontRendererObj, this.mc.getTextureManager(), this.theAchievement.theItemStack, n + 8, n2 + 8);
            GL11.glDisable(2896);
            GL11.glDepthMask(true);
            GL11.glEnable(2929);
        }
    }

    public void clearAchievements() {
        this.theAchievement = null;
        this.notificationTime = 0L;
    }
}
