package net.minecraft.client.gui.achievement;

import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.IProgressMeter;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C16PacketClientStatus.EnumState;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class GuiAchievements extends GuiScreen implements IProgressMeter {
    public static int MIN_PAN_X = AchievementList.minDisplayColumn * 24 - 112;
    public static int MIN_PAN_Y = AchievementList.minDisplayRow * 24 - 112;
    public static int MAX_PAN_X = AchievementList.maxDisplayColumn * 24 - 77;
    public static int MAX_PAN_Y = AchievementList.maxDisplayRow * 24 - 77;
    public static ResourceLocation ACHIEVEMENT_BACKGROUND = new ResourceLocation("textures/gui/achievement/achievement_background.png");
    public int originX = 256;
    public int originY = 202;
    public float fov = 1.0F;
    public int targetMouseX;
    public int targetMouseY;
    public boolean loadingAchievements = true;
    public GuiScreen parentScreen;
    public StatFileWriter statFileWriter;
    public double centeredX;
    public double targetCenterX;
    public double attemptedCenterX;
    public double centeredY;
    public int isDragging;
    public double targetCenterY;
    public double attemptedCenterY;

    public GuiAchievements(GuiScreen guiScreen, StatFileWriter statFileWriter) {
        this.parentScreen = guiScreen;
        this.statFileWriter = statFileWriter;
        short s = 141;
        short s2 = 141;
        this.attemptedCenterX = this.targetCenterX = this.centeredX = AchievementList.openInventory.displayColumn * 24 - s / 2 - 12;
        this.attemptedCenterY = this.targetCenterY = this.centeredY = AchievementList.openInventory.displayRow * 24 - s2 / 2;
    }

    @Override
    public void initGui() {
        this.mc.getNetHandler().addToSendQueue(new C16PacketClientStatus(EnumState.REQUEST_STATS));
        this.buttonList.clear();
        this.buttonList.add(new GuiOptionButton(1, this.width / 2 + 24, this.height / 2 + 74, 80, 20, I18n.format("gui.done")));
    }

    @Override
    public void actionPerformed(GuiButton button) throws java.io.IOException {
        if (!this.loadingAchievements && button.id == 1) {
            this.mc.displayGuiScreen(this.parentScreen);
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws java.io.IOException {
        if (keyCode == this.mc.gameSettings.keyBindInventory.getKeyCode()) {
            this.mc.displayGuiScreen(null);
            this.mc.setIngameFocus();
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) throws java.io.IOException {
        if (this.loadingAchievements) {
            this.drawDefaultBackground();
            this.drawCenteredString(this.fontRendererObj, I18n.format("multiplayer.downloadingStats"), this.width / 2, this.height / 2, 16777215);
            this.drawCenteredString(
                this.fontRendererObj,
                lanSearchStates[(int)(Minecraft.getSystemTime() / 150L % lanSearchStates.length)],
                this.width / 2,
                this.height / 2 + this.fontRendererObj.FONT_HEIGHT * 2,
                16777215
            );
        } else {
            if (Mouse.isButtonDown(0)) {
                int n = (this.width - this.originX) / 2;
                int n2 = (this.height - this.originY) / 2;
                int n3 = n + 8;
                int n4 = n2 + 17;
                if ((this.isDragging == 0 || this.isDragging == 1) && mouseX >= n3 && mouseX < n3 + 224 && mouseY >= n4 && mouseY < n4 + 155) {
                    if (this.isDragging == 0) {
                        this.isDragging = 1;
                    } else {
                        this.targetCenterX = this.targetCenterX - (mouseX - this.targetMouseX) * this.fov;
                        this.targetCenterY = this.targetCenterY - (mouseY - this.targetMouseY) * this.fov;
                        this.centeredX = this.attemptedCenterX = this.targetCenterX;
                        this.centeredY = this.attemptedCenterY = this.targetCenterY;
                    }

                    this.targetMouseX = mouseX;
                    this.targetMouseY = mouseY;
                }
            } else {
                this.isDragging = 0;
            }

            int dWheel = Mouse.getDWheel();
            float fov = this.fov;
            if (dWheel < 0) {
                this.fov += 0.25F;
            } else if (dWheel > 0) {
                this.fov -= 0.25F;
            }

            this.fov = MathHelper.clamp_float(this.fov, 1.0F, 2.0F);
            if (this.fov != fov) {
                float f = fov - this.fov;
                float f2 = fov * this.originX;
                float f3 = fov * this.originY;
                float f4 = this.fov * this.originX;
                float f5 = this.fov * this.originY;
                this.targetCenterX -= (f4 - f2) * 0.5F;
                this.targetCenterY -= (f5 - f3) * 0.5F;
                this.centeredX = this.attemptedCenterX = this.targetCenterX;
                this.centeredY = this.attemptedCenterY = this.targetCenterY;
            }

            if (this.centeredX < MIN_PAN_X) {
                this.centeredX = MIN_PAN_X;
            }

            if (this.centeredY < MIN_PAN_Y) {
                this.centeredY = MIN_PAN_Y;
            }

            if (this.centeredX >= MAX_PAN_X) {
                this.centeredX = MAX_PAN_X - 1;
            }

            if (this.centeredY >= MAX_PAN_Y) {
                this.centeredY = MAX_PAN_Y - 1;
            }

            this.drawDefaultBackground();
            this.drawAchievementScreen(mouseX, mouseY, partialTicks);
            GL11.glDisable(2896);
            GL11.glDisable(2929);
            this.drawTitle();
            GL11.glEnable(2896);
            GL11.glEnable(2929);
        }
    }

    @Override
    public void doneLoading() {
        if (this.loadingAchievements) {
            this.loadingAchievements = false;
        }
    }

    @Override
    public void updateScreen() {
        if (!this.loadingAchievements) {
            this.attemptedCenterX = this.targetCenterX;
            this.attemptedCenterY = this.targetCenterY;
            double d = this.centeredX - this.targetCenterX;
            double d2 = this.centeredY - this.targetCenterY;
            if (d * d + d2 * d2 < 4.0) {
                this.targetCenterX += d;
                this.targetCenterY += d2;
            } else {
                this.targetCenterX += d * 0.85;
                this.targetCenterY += d2 * 0.85;
            }
        }
    }

    public void drawTitle() {
        int n = (this.width - this.originX) / 2;
        int n2 = (this.height - this.originY) / 2;
        this.fontRendererObj.drawString(I18n.format("gui.achievements"), n + 15, n2 + 5, 4210752);
    }

    public void drawAchievementScreen(int n, int n2, float f) throws java.io.IOException {
        int n3 = MathHelper.floor_double(this.attemptedCenterX + (this.targetCenterX - this.attemptedCenterX) * f);
        int n4 = MathHelper.floor_double(this.attemptedCenterY + (this.targetCenterY - this.attemptedCenterY) * f);
        if (n3 < MIN_PAN_X) {
            n3 = MIN_PAN_X;
        }

        if (n4 < MIN_PAN_Y) {
            n4 = MIN_PAN_Y;
        }

        if (n3 >= MAX_PAN_X) {
            n3 = MAX_PAN_X - 1;
        }

        if (n4 >= MAX_PAN_Y) {
            n4 = MAX_PAN_Y - 1;
        }

        int n5 = (this.width - this.originX) / 2;
        int n6 = (this.height - this.originY) / 2;
        int n7 = n5 + 16;
        int n8 = n6 + 17;
        floatValue = 0.0F;
        GL11.glDepthFunc(518);
        GL11.glPushMatrix();
        GL11.glTranslatef(n7, n8, -200.0F);
        GL11.glScalef(1.0F / this.fov, 1.0F / this.fov, 0.0F);
        GL11.glEnable(3553);
        GL11.glDisable(2896);
        GL11.glEnable(32826);
        GL11.glEnable(2903);
        int n9 = n3 + 288 >> 4;
        int n10 = n4 + 288 >> 4;
        int n11 = (n3 + 288) % 16;
        int n12 = (n4 + 288) % 16;
        boolean flag = true;
        boolean flag2 = true;
        boolean flag3 = true;
        boolean flag4 = true;
        boolean flag5 = true;
        Random random = new Random();
        float f2 = 16.0F / this.fov;
        float f3 = 16.0F / this.fov;

        for (int i = 0; i * f2 - n12 < 155.0F; i++) {
            float f4 = 0.6F - (n10 + i) / 25.0F * 0.3F;
            GL11.glColor4f(f4, f4, f4, 1.0F);

            for (int j = 0; j * f3 - n11 < 224.0F; j++) {
                random.setSeed(this.mc.getSession().getPlayerID().hashCode() + n9 + j + (n10 + i) * 16);
                int n13 = random.nextInt(1 + n10 + i) + (n10 + i) / 2;
                IIcon icon = Blocks.sand.getIcon(0, 0);
                if (n13 > 37 || n10 + i == 35) {
                    icon = Blocks.bedrock.getIcon(0, 0);
                } else if (n13 == 22) {
                    if (random.nextInt(2) == 0) {
                        icon = Blocks.diamond_ore.getIcon(0, 0);
                    } else {
                        icon = Blocks.redstone_ore.getIcon(0, 0);
                    }
                } else if (n13 == 10) {
                    icon = Blocks.iron_ore.getIcon(0, 0);
                } else if (n13 == 8) {
                    icon = Blocks.coal_ore.getIcon(0, 0);
                } else if (n13 > 4) {
                    icon = Blocks.stone.getIcon(0, 0);
                } else if (n13 > 0) {
                    icon = Blocks.dirt.getIcon(0, 0);
                }

                this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
                this.drawTexturedModelRectFromIcon(j * 16 - n11, i * 16 - n12, icon, 16, 16);
            }
        }

        GL11.glEnable(2929);
        GL11.glDepthFunc(515);
        this.mc.getTextureManager().bindTexture(ACHIEVEMENT_BACKGROUND);

        for (int i = 0; i < AchievementList.achievementList.size(); i++) {
            Achievement achievement = (Achievement)AchievementList.achievementList.get(i);
            if (achievement.parentAchievement != null) {
                int n13 = achievement.displayColumn * 24 - n3 + 11;
                int n14 = achievement.displayRow * 24 - n4 + 11;
                int n15 = achievement.parentAchievement.displayColumn * 24 - n3 + 11;
                int n16 = achievement.parentAchievement.displayRow * 24 - n4 + 11;
                boolean hasAchievementUnlocked = this.statFileWriter.hasAchievementUnlocked(achievement);
                boolean canUnlockAchievement = this.statFileWriter.canUnlockAchievement(achievement);
                int achievementDepth = this.statFileWriter.getAchievementDepth(achievement);
                if (achievementDepth <= 4) {
                    int n17 = -16777216;
                    if (hasAchievementUnlocked) {
                        n17 = -6250336;
                    } else if (canUnlockAchievement) {
                        n17 = -16711936;
                    }

                    this.drawHorizontalLine(n13, n15, n14, n17);
                    this.drawVerticalLine(n15, n14, n16, n17);
                    if (n13 > n15) {
                        drawTexturedRect(n13 - 11 - 7, n14 - 5, 114.0F, 234.0F, 7, 11);
                    } else if (n13 < n15) {
                        drawTexturedRect(n13 + 11, n14 - 5, 107.0F, 234.0F, 7, 11);
                    } else if (n14 > n16) {
                        drawTexturedRect(n13 - 5, n14 - 11 - 7, 96.0F, 234.0F, 11, 7);
                    } else if (n14 < n16) {
                        drawTexturedRect(n13 - 5, n14 + 11, 96.0F, 241.0F, 11, 7);
                    }
                }
            }
        }

        Achievement achievement = null;
        RenderItem renderItem = new RenderItem();
        float f4 = (n - n7) * this.fov;
        float f5 = (n2 - n8) * this.fov;
        RenderHelper.enableGUIStandardItemLighting();
        GL11.glDisable(2896);
        GL11.glEnable(32826);
        GL11.glEnable(2903);

        for (int i = 0; i < AchievementList.achievementList.size(); i++) {
            Achievement achievement2 = (Achievement)AchievementList.achievementList.get(i);
            int n13 = achievement2.displayColumn * 24 - n3;
            int n14 = achievement2.displayRow * 24 - n4;
            if (n13 >= -24 && n14 >= -24 && n13 <= 224.0F * this.fov && n14 <= 155.0F * this.fov) {
                int achievementDepth = this.statFileWriter.getAchievementDepth(achievement2);
                if (this.statFileWriter.hasAchievementUnlocked(achievement2)) {
                    float f6 = 0.75F;
                    GL11.glColor4f(f6, f6, f6, 1.0F);
                } else if (this.statFileWriter.canUnlockAchievement(achievement2)) {
                    float f6 = 1.0F;
                    GL11.glColor4f(f6, f6, f6, 1.0F);
                } else if (achievementDepth < 3) {
                    float f6 = 0.3F;
                    GL11.glColor4f(f6, f6, f6, 1.0F);
                } else if (achievementDepth == 3) {
                    float f6 = 0.2F;
                    GL11.glColor4f(f6, f6, f6, 1.0F);
                } else {
                    if (achievementDepth != 4) {
                        continue;
                    }

                    float f6 = 0.1F;
                    GL11.glColor4f(f6, f6, f6, 1.0F);
                }

                this.mc.getTextureManager().bindTexture(ACHIEVEMENT_BACKGROUND);
                if (achievement2.getSpecial()) {
                    drawTexturedRect(n13 - 2, n14 - 2, 26.0F, 202.0F, 26, 26);
                } else {
                    drawTexturedRect(n13 - 2, n14 - 2, 0.0F, 202.0F, 26, 26);
                }

                if (!this.statFileWriter.canUnlockAchievement(achievement2)) {
                    float f6 = 0.1F;
                    GL11.glColor4f(f6, f6, f6, 1.0F);
                    renderItem.renderWithColor = false;
                }

                GL11.glEnable(2896);
                GL11.glEnable(2884);
                renderItem.renderItemAndEffectIntoGUI(this.mc.fontRendererObj, this.mc.getTextureManager(), achievement2.theItemStack, n13 + 3, n14 + 3);
                GL11.glBlendFunc(770, 771);
                GL11.glDisable(2896);
                if (!this.statFileWriter.canUnlockAchievement(achievement2)) {
                    renderItem.renderWithColor = true;
                }

                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                if (f4 >= n13 && f4 <= n13 + 22 && f5 >= n14 && f5 <= n14 + 22) {
                    achievement = achievement2;
                }
            }
        }

        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glPopMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(ACHIEVEMENT_BACKGROUND);
        drawTexturedRect(n5, n6, 0.0F, 0.0F, this.originX, this.originY);
        floatValue = 0.0F;
        GL11.glDepthFunc(515);
        GL11.glDisable(2929);
        GL11.glEnable(3553);
        super.drawScreen(n, n2, f);
        if (achievement != null) {
            String unformattedText = achievement.getStatName().getUnformattedText();
            String description = achievement.getDescription();
            int n13 = n + 12;
            int n14 = n2 - 4;
            int achievementDepth = this.statFileWriter.getAchievementDepth(achievement);
            if (!this.statFileWriter.canUnlockAchievement(achievement)) {
                if (achievementDepth == 3) {
                    unformattedText = I18n.format("achievement.unknown");
                    int max = Math.max(this.fontRendererObj.getStringWidth(unformattedText), 120);
                    String unformattedText2 = new ChatComponentTranslation("achievement.requires", achievement.parentAchievement.getStatName()).getUnformattedText();
                    int splitStringWidth = this.fontRendererObj.splitStringWidth(unformattedText2, max);
                    drawVerticalGradient(n13 - 3, n14 - 3, n13 + max + 3, n14 + splitStringWidth + 12 + 3, -1073741824, -1073741824);
                    this.fontRendererObj.drawSplitString(unformattedText2, n13, n14 + 12, max, -9416624);
                } else if (achievementDepth < 3) {
                    int max = Math.max(this.fontRendererObj.getStringWidth(unformattedText), 120);
                    String unformattedText2 = new ChatComponentTranslation("achievement.requires", achievement.parentAchievement.getStatName()).getUnformattedText();
                    int splitStringWidth = this.fontRendererObj.splitStringWidth(unformattedText2, max);
                    drawVerticalGradient(n13 - 3, n14 - 3, n13 + max + 3, n14 + splitStringWidth + 12 + 3, -1073741824, -1073741824);
                    this.fontRendererObj.drawSplitString(unformattedText2, n13, n14 + 12, max, -9416624);
                } else {
                    unformattedText = null;
                }
            } else {
                int max = Math.max(this.fontRendererObj.getStringWidth(unformattedText), 120);
                int splitStringWidth = this.fontRendererObj.splitStringWidth(description, max);
                if (this.statFileWriter.hasAchievementUnlocked(achievement)) {
                    splitStringWidth += 12;
                }

                drawVerticalGradient(n13 - 3, n14 - 3, n13 + max + 3, n14 + splitStringWidth + 3 + 12, -1073741824, -1073741824);
                this.fontRendererObj.drawSplitString(description, n13, n14 + 12, max, -6250336);
                if (this.statFileWriter.hasAchievementUnlocked(achievement)) {
                    this.fontRendererObj.drawShadowedString(I18n.format("achievement.taken"), n13, n14 + splitStringWidth + 4, -7302913);
                }
            }

            if (unformattedText != null) {
                this.fontRendererObj
                    .drawShadowedString(
                        unformattedText,
                        n13,
                        n14,
                        this.statFileWriter.canUnlockAchievement(achievement) ? (achievement.getSpecial() ? -128 : -1) : (achievement.getSpecial() ? -8355776 : -8355712)
                    );
            }
        }

        GL11.glEnable(2929);
        GL11.glEnable(2896);
        RenderHelper.disableStandardItemLighting();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return !this.loadingAchievements;
    }
}
