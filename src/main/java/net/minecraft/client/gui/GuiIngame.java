package net.minecraft.client.gui;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.RenderDebugOverlayEvent;
import com.cheatbreaker.client.event.RenderPlayerListEvent;
import com.cheatbreaker.client.event.ScaledResolutionEvent1;
import com.cheatbreaker.client.event.ScaledResolutionEvent3;
import com.cheatbreaker.client.event.ScaledResolutionEvent5;
import com.cheatbreaker.client.event.ScaledResolutionEvent6;
import com.cheatbreaker.client.event.ScaledResolutionEvent7;
import com.cheatbreaker.client.event.ScaledResolutionEvent8;
import com.cheatbreaker.client.event.ScaledResolutionIntEvent;
import com.cheatbreaker.client.mod.impl.PackTweaksMod;
import com.cheatbreaker.client.ui.screen.FriendsScreen;
import com.cheatbreaker.client.ui.screen.ModSettingsScreen;
import com.cheatbreaker.client.ui.screen.ModsScreen;
import com.cheatbreaker.client.ui.screen.ProfileNameScreen;
import java.awt.Color;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.util.Direction;
import net.minecraft.util.FoodStats;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.chunk.Chunk;
import org.lwjgl.opengl.GL11;

public class GuiIngame extends Gui {
    public static ResourceLocation vignetteTexPath = new ResourceLocation("textures/misc/vignette.png");
    public static ResourceLocation widgetsTexPath = new ResourceLocation("textures/gui/widgets.png");
    public static ResourceLocation pumpkinBlurTexPath = new ResourceLocation("textures/misc/pumpkinblur.png");
    public static RenderItem itemRenderer = new RenderItem();
    public Random rand = new Random();
    public String recordPlaying = "";
    public float prevVignetteBrightness = 1.0F;
    public int updateCounter;
    public Minecraft mc;
    public int recordPlayingUpFor;
    public boolean recordIsPlaying;
    public GuiNewChat persistantChatGUI;
    public int remainingHighlightTicks;
    public ItemStack highlightingItemStack;
    public static boolean flag = false;

    public GuiIngame(Minecraft mc) {
        this.mc = mc;
        this.persistantChatGUI = new GuiNewChat(mc);
    }

    public void renderGameOverlay(float f, boolean flag2, int n, int n2) {
        ScaledResolution scaledResolution = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
        int scaledWidth = scaledResolution.getScaledWidth();
        int scaledHeight = scaledResolution.getScaledHeight();
        FontRenderer fontRendererObj = this.mc.fontRendererObj;
        this.mc.entityRenderer.setupOverlayRendering();
        GL11.glEnable(3042);
        PackTweaksMod packTweaksMod = CheatBreaker.getInstance().getModManager().packTweaksMod;
        boolean isEnabled = packTweaksMod.isEnabled();
        if (packTweaksMod.lowHealthOverlay.getBooleanValue() && packTweaksMod.isEnabled()) {
            packTweaksMod.render(packTweaksMod.lowHealthOpacity.getFloatValue() / 100.0F, scaledWidth, scaledHeight);
        } else {
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        }

        if (!Minecraft.isFancyGraphicsEnabled() && (!packTweaksMod.vignetteType.getValue().equals("Static") || !isEnabled)) {
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        } else {
            this.renderVignette(this.mc.thePlayer.getBrightness(f), scaledWidth, scaledHeight);
        }

        ItemStack armorItemInSlot = this.mc.thePlayer.inventory.armorItemInSlot(3);
        boolean flag3 = !isEnabled || (Boolean)packTweaksMod.showPumpkinOverlay.getValue();
        if (this.mc.gameSettings.thirdPersonView == 0 && armorItemInSlot != null && armorItemInSlot.getItem() == Item.getItemFromBlock(Blocks.pumpkin) && flag3) {
            this.renderPumpkinBlur(scaledWidth, scaledHeight);
        }

        if (!this.mc.thePlayer.isPotionActive(Potion.confusion)) {
            float f2 = this.mc.thePlayer.prevTimeInPortal + (this.mc.thePlayer.timeInPortal - this.mc.thePlayer.prevTimeInPortal) * f;
            if (f2 > 0.0F) {
                this.render(f2, scaledWidth, scaledHeight);
            }
        }

        float f2 = 1.0F;
        switch (CheatBreaker.getInstance().getKeybindSettings().hotbarScale.getStringValue()) {
            case "Small":
                f2 *= 0.5F / CheatBreaker.getInstance().getUiManager().getFloatValue();
                break;
            case "Normal":
                f2 /= CheatBreaker.getInstance().getUiManager().getFloatValue();
                break;
            case "Large":
                f2 *= 1.5F / CheatBreaker.getInstance().getUiManager().getFloatValue();
                break;
            case "Auto":
                f2 *= 2.0F / CheatBreaker.getInstance().getUiManager().getFloatValue();
        }

        f2 *= CheatBreaker.getInstance().getKeybindSettings().hotbarScaleMultiplier.getFloatValue();
        if (!this.mc.playerController.enableEverythingIsScrewedUpMode()) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(widgetsTexPath);
            InventoryPlayer inventory = this.mc.thePlayer.inventory;
            floatValue = -90.0F;
            GL11.glPushMatrix();
            GL11.glScalef(f2, f2, f2);
            drawTexturedRect(scaledWidth / 2 / f2 - 91.0F, scaledHeight / f2 - 22.0F, 0.0F, 0.0F, 182, 22);
            drawTexturedRect(scaledWidth / 2 / f2 - 91.0F - 1.0F + inventory.currentItem * 20, scaledHeight / f2 - 22.0F - 1.0F, 0.0F, 22.0F, 24, 22);
            GL11.glPopMatrix();
            ScaledResolutionIntEvent scaledResolutionIntEvent = new ScaledResolutionIntEvent(scaledResolution, scaledWidth / 2, scaledHeight / 2);
            CheatBreaker.getInstance().getEventBus().handleEvent(scaledResolutionIntEvent);
            if (!scaledResolutionIntEvent.isCancelled()) {
                this.mc.getTextureManager().bindTexture(icons);
                GL11.glEnable(3042);
                GL11.glEnable(3008);
                OpenGlHelper.glBlendFunc(775, 769, 1, 0);
                drawTexturedRect(scaledWidth / 2 - 7, scaledHeight / 2 - 7, 0.0F, 0.0F, 16, 16);
                OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            }

            this.mc.mcProfiler.startSection("bossHealth");
            CheatBreaker.getInstance().getEventBus().handleEvent(new ScaledResolutionEvent8(scaledResolution));
            this.mc.mcProfiler.endSection();
            GL11.glPushMatrix();
            if (this.mc.playerController.shouldDrawHUD()) {
                this.process(scaledWidth, scaledHeight, f2);
            }

            GL11.glPopMatrix();
            this.mc.mcProfiler.startSection("actionBar");
            GL11.glEnable(32826);
            RenderHelper.enableGUIStandardItemLighting();
            GL11.glPushMatrix();
            GL11.glScalef(f2, f2, f2);

            for (int i = 0; i < 9; i++) {
                int n3 = (int)(scaledWidth / 2 / f2) - 90 + i * 20 + 2;
                int n4 = (int)(scaledHeight / f2) - 16 - 3;
                this.renderInventorySlot(i, n3, n4, f);
            }

            GL11.glPopMatrix();
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(32826);
            this.mc.mcProfiler.endSection();
            GL11.glDisable(3042);
        }

        if (this.mc.thePlayer.getSleepTimer() > 0) {
            this.mc.mcProfiler.startSection("sleep");
            GL11.glDisable(2929);
            GL11.glDisable(3008);
            int sleepTimer = this.mc.thePlayer.getSleepTimer();
            float f3 = sleepTimer / 100.0F;
            if (f3 > 1.0F) {
                f3 = 1.0F - (sleepTimer - 100) / 10.0F;
            }

            int n3 = (int)(220.0F * f3) << 24 | 1052704;
            drawRect(0.0F, 0.0F, scaledWidth, scaledHeight, n3);
            GL11.glEnable(3008);
            GL11.glEnable(2929);
            this.mc.mcProfiler.endSection();
        }

        int n3 = 16777215;
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        int n4 = (int)(scaledWidth / 2 / f2) - 91;
        GL11.glPushMatrix();
        GL11.glScalef(f2, f2, f2);
        if (this.mc.thePlayer.isRidingHorse()) {
            this.mc.mcProfiler.startSection("jumpBar");
            this.mc.getTextureManager().bindTexture(Gui.icons);
            float horseJumpPower = this.mc.thePlayer.getHorseJumpPower();
            short s = 182;
            int n5 = (int)(horseJumpPower * (s + 1));
            int n6 = (int)(scaledHeight / f2) - 32 + 3;
            drawTexturedRect(n4, n6, 0.0F, 84.0F, s, 5);
            if (n5 > 0) {
                drawTexturedRect(n4, n6, 0.0F, 89.0F, n5, 5);
            }

            this.mc.mcProfiler.endSection();
        } else if (this.mc.playerController.gameIsSurvivalOrAdventure()) {
            this.mc.mcProfiler.startSection("expBar");
            this.mc.getTextureManager().bindTexture(Gui.icons);
            int xpBarCap = this.mc.thePlayer.xpBarCap();
            CheatBreaker.getInstance().getEventBus().handleEvent(new ScaledResolutionEvent1(scaledResolution));
            if (xpBarCap > 0) {
                short s = 182;
                int n5 = (int)(this.mc.thePlayer.experience * (s + 1));
                int n6 = (int)(scaledHeight / f2) - 32 + 3;
                drawTexturedRect(n4, n6, 0.0F, 64.0F, s, 5);
                if (n5 > 0) {
                    drawTexturedRect(n4, n6, 0.0F, 69.0F, n5, 5);
                }
            }

            this.mc.mcProfiler.endSection();
            if (this.mc.thePlayer.experienceLevel > 0) {
                this.mc.mcProfiler.startSection("expLevel");
                boolean flag = false;
                int n5 = flag ? 16777215 : 8453920;
                String str = "" + this.mc.thePlayer.experienceLevel;
                int n6 = ((int)(scaledWidth / f2) - fontRendererObj.getStringWidth(str)) / 2;
                int n7 = (int)(scaledHeight / f2) - 31 - 4;
                boolean flag4 = false;
                fontRendererObj.drawString(str, n6 + 1, n7, 0);
                fontRendererObj.drawString(str, n6 - 1, n7, 0);
                fontRendererObj.drawString(str, n6, n7 + 1, 0);
                fontRendererObj.drawString(str, n6, n7 - 1, 0);
                fontRendererObj.drawString(str, n6, n7, n5);
                this.mc.mcProfiler.endSection();
            }
        }

        GL11.glPopMatrix();
        if (this.mc.gameSettings.heldItemTooltips) {
            GL11.glPushMatrix();
            GL11.glScalef(f2, f2, f2);
            this.mc.mcProfiler.startSection("toolHighlight");
            if (this.remainingHighlightTicks > 0 && this.highlightingItemStack != null) {
                String displayName = this.highlightingItemStack.getDisplayName();
                int n5 = ((int)(scaledWidth / f2) - fontRendererObj.getStringWidth(displayName)) / 2;
                int n6 = (int)(scaledHeight / f2) - 59;
                if (!this.mc.playerController.shouldDrawHUD()) {
                    n6 += 14;
                }

                int n7 = (int)(this.remainingHighlightTicks * 256.0F / 10.0F);
                if (n7 > 255) {
                    n7 = 255;
                }

                if (n7 > 0) {
                    GL11.glPushMatrix();
                    GL11.glEnable(3042);
                    OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                    fontRendererObj.drawShadowedString(displayName, n5, n6, 16777215 + (n7 << 24));
                    GL11.glDisable(3042);
                    GL11.glPopMatrix();
                }
            }

            this.mc.mcProfiler.endSection();
            GL11.glPopMatrix();
        }

        if (this.mc.gameSettings.showDebugInfo) {
            this.mc.mcProfiler.startSection("debug");
            GL11.glPushMatrix();
            fontRendererObj.drawShadowedString("Minecraft 1.7.10 (" + this.mc.debug + ")", 2.0F, 2.0F, 16777215);
            fontRendererObj.drawShadowedString(this.mc.debugInfoRenders(), 2.0F, 12.0F, 16777215);
            fontRendererObj.drawShadowedString(this.mc.getEntityDebug(), 2.0F, 22.0F, 16777215);
            fontRendererObj.drawShadowedString(this.mc.debugInfoEntities(), 2.0F, 32.0F, 16777215);
            fontRendererObj.drawShadowedString(this.mc.getWorldProviderName(), 2.0F, 42.0F, 16777215);
            long maxMemory = Runtime.getRuntime().maxMemory();
            long totalMemory = Runtime.getRuntime().totalMemory();
            long freeMemory = Runtime.getRuntime().freeMemory();
            long l = totalMemory - freeMemory;
            String str = "Used memory: " + l * 100L / maxMemory + "% (" + l / 1024L / 1024L + "MB) of " + maxMemory / 1024L / 1024L + "MB";
            int n5 = 14737632;
            this.drawString(fontRendererObj, str, scaledWidth - fontRendererObj.getStringWidth(str) - 2, 2, 14737632);
            str = "Allocated memory: " + totalMemory * 100L / maxMemory + "% (" + totalMemory / 1024L / 1024L + "MB)";
            this.drawString(fontRendererObj, str, scaledWidth - fontRendererObj.getStringWidth(str) - 2, 12, 14737632);
            int n6 = MathHelper.floor_double(this.mc.thePlayer.posX);
            int n7 = MathHelper.floor_double(this.mc.thePlayer.posY);
            int n8 = MathHelper.floor_double(this.mc.thePlayer.posZ);
            this.drawString(fontRendererObj, String.format("x: %.5f (%d) // c: %d (%d)", this.mc.thePlayer.posX, n6, n6 >> 4, n6 & 15), 2, 64, 14737632);
            this.drawString(fontRendererObj, String.format("y: %.3f (feet pos, %.3f eyes pos)", this.mc.thePlayer.boundingBox.minY, this.mc.thePlayer.posY), 2, 72, 14737632);
            this.drawString(fontRendererObj, String.format("z: %.5f (%d) // c: %d (%d)", this.mc.thePlayer.posZ, n8, n8 >> 4, n8 & 15), 2, 80, 14737632);
            int n9 = MathHelper.floor_double(this.mc.thePlayer.rotationYaw * 4.0F / 360.0F + 0.5) & 3;
            this.drawString(
                fontRendererObj, "f: " + n9 + " (" + Direction.directions[n9] + ") / " + MathHelper.wrapAngleTo180_float(this.mc.thePlayer.rotationYaw), 2, 88, 14737632
            );
            if (this.mc.theWorld != null && this.mc.theWorld.blockExists(n6, n7, n8)) {
                Chunk chunkFromBlockCoords = this.mc.theWorld.getChunkFromBlockCoords(n6, n8);
                this.drawString(
                    fontRendererObj,
                    "lc: "
                        + (chunkFromBlockCoords.getTopFilledSegment() + 15)
                        + " b: "
                        + chunkFromBlockCoords.getBiomeGenForWorldCoords(n6 & 15, n8 & 15, this.mc.theWorld.getWorldChunkManager()).biomeName
                        + " bl: "
                        + chunkFromBlockCoords.getSavedLightValue(EnumSkyBlock.Block, n6 & 15, n7, n8 & 15)
                        + " sl: "
                        + chunkFromBlockCoords.getSavedLightValue(EnumSkyBlock.Sky, n6 & 15, n7, n8 & 15)
                        + " rl: "
                        + chunkFromBlockCoords.getBlockLightValue(n6 & 15, n7, n8 & 15, 0),
                    2,
                    96,
                    14737632
                );
            }

            this.drawString(
                fontRendererObj,
                String.format(
                    "ws: %.3f, fs: %.3f, g: %b, fl: %d",
                    this.mc.thePlayer.capabilities.getWalkSpeed(),
                    this.mc.thePlayer.capabilities.getFlySpeed(),
                    this.mc.thePlayer.onGround,
                    this.mc.theWorld.getHeightValue(n6, n8)
                ),
                2,
                104,
                14737632
            );
            if (this.mc.entityRenderer != null && this.mc.entityRenderer.isShaderActive()) {
                this.drawString(fontRendererObj, String.format("shader: %s", this.mc.entityRenderer.getShaderGroup().getShaderGroupName()), 2, 112, 14737632);
            }

            CheatBreaker.getInstance().getEventBus().handleEvent(new RenderDebugOverlayEvent(scaledResolution));
            GL11.glPopMatrix();
            this.mc.mcProfiler.endSection();
        }

        if (flag) {
            this.mc.fontRendererObj.drawShadowedString("Uploading screenshot...", 4.0F, scaledResolution.getScaledHeight() - 24, -1);
        }

        if (this.mc.currentScreen instanceof ModsScreen
            || this.mc.currentScreen instanceof ModSettingsScreen
            || this.mc.currentScreen instanceof ProfileNameScreen) {
            CheatBreaker.getInstance().getEventBus().handleEvent(new ScaledResolutionEvent7(scaledResolution));
        }

        if (this.mc.currentScreen instanceof GuiChat) {
            CheatBreaker.getInstance().getEventBus().handleEvent(new ScaledResolutionEvent6(scaledResolution));
        }

        CheatBreaker.getInstance().getEventBus().handleEvent(new ScaledResolutionEvent3(scaledResolution));
        if (this.mc.currentScreen == null) {
            FriendsScreen.getFriendsScreen().renderNotifications();
        }

        if (this.recordPlayingUpFor > 0) {
            this.mc.mcProfiler.startSection("overlayMessage");
            float f3 = this.recordPlayingUpFor - f;
            int n5 = (int)(f3 * 255.0F / 20.0F);
            if (n5 > 255) {
                n5 = 255;
            }

            if (n5 > 8) {
                GL11.glPushMatrix();
                GL11.glTranslatef(scaledWidth / 2, scaledHeight - 68, 0.0F);
                GL11.glEnable(3042);
                OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                int n6 = 16777215;
                if (this.recordIsPlaying) {
                    n6 = Color.HSBtoRGB(f3 / 50.0F, 0.7F, 0.6F) & 16777215;
                }

                fontRendererObj.drawString(this.recordPlaying, -fontRendererObj.getStringWidth(this.recordPlaying) / 2, -4, n6 + (n5 << 24 & 0xFF000000));
                GL11.glDisable(3042);
                GL11.glPopMatrix();
            }

            this.mc.mcProfiler.endSection();
        }

        this.mc.theWorld.getScoreboard().getObjectiveInDisplaySlot(1);
        GL11.glEnable(3042);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glDisable(3008);
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, scaledHeight - 48, 0.0F);
        this.mc.mcProfiler.startSection("chat");
        ScaledResolutionEvent5 scaledResolutionEvent5 = new ScaledResolutionEvent5(scaledResolution);
        CheatBreaker.getInstance().getEventBus().handleEvent(scaledResolutionEvent5);
        this.mc.mcProfiler.endSection();
        GL11.glPopMatrix();
        ScoreObjective objectiveInDisplaySlot = this.mc.theWorld.getScoreboard().getObjectiveInDisplaySlot(0);
        if (this.mc.gameSettings.keyBindPlayerList.getIsKeyPressed()
            && (!this.mc.isIntegratedServerRunning() || this.mc.thePlayer.sendQueue.playerInfoList.size() > 1 || objectiveInDisplaySlot != null)) {
            this.mc.mcProfiler.startSection("playerList");
            NetHandlerPlayClient sendQueue = this.mc.thePlayer.sendQueue;
            List playerInfoList = sendQueue.playerInfoList;
            int currentServerMaxPlayers = sendQueue.currentServerMaxPlayers;
            int n5 = currentServerMaxPlayers;

            int n6;
            for (n6 = 1; n5 > 20; n5 = (currentServerMaxPlayers + n6 - 1) / n6) {
                n6++;
            }

            int n7 = 300 / n6;
            if (n7 > 150) {
                n7 = 150;
            }

            int n8 = (scaledWidth - n6 * n7) / 2;
            CheatBreaker.getInstance().getEventBus().handleEvent(new RenderPlayerListEvent(scaledResolution, n8 - 1, 9));
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(2896);
        GL11.glEnable(3008);
    }

    public void renderBossHealth() {
        if (BossStatus.bossName != null && BossStatus.statusBarTime > 0) {
            BossStatus.statusBarTime--;
            FontRenderer fontRendererObj = this.mc.fontRendererObj;
            ScaledResolution scaledResolution = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
            int scaledWidth = scaledResolution.getScaledWidth();
            short s = 182;
            int n = scaledWidth / 2 - s / 2;
            int n2 = (int)(BossStatus.healthScale * (s + 1));
            byte b = 12;
            drawTexturedRect(n, b, 0.0F, 74.0F, s, 5);
            drawTexturedRect(n, b, 0.0F, 74.0F, s, 5);
            if (n2 > 0) {
                drawTexturedRect(n, b, 0.0F, 79.0F, n2, 5);
            }

            String bossName = BossStatus.bossName;
            fontRendererObj.drawShadowedString(bossName, scaledWidth / 2 - fontRendererObj.getStringWidth(bossName) / 2, b - 10, 16777215);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(icons);
        }
    }

    public void renderPumpkinBlur(int n, int n2) {
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        if (CheatBreaker.getInstance().getModManager().packTweaksMod.isEnabled()) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, (Float)CheatBreaker.getInstance().getModManager().packTweaksMod.pumpkinOverlayOpacity.getValue() / 100.0F);
        } else {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        }

        GL11.glDisable(3008);
        this.mc.getTextureManager().bindTexture(pumpkinBlurTexPath);
        Tessellator instance = Tessellator.instance;
        instance.startDrawingQuads();
        instance.addVertexWithUV(0.0, n2, -90.0, 0.0, 1.0);
        instance.addVertexWithUV(n, n2, -90.0, 1.0, 1.0);
        instance.addVertexWithUV(n, 0.0, -90.0, 1.0, 0.0);
        instance.addVertexWithUV(0.0, 0.0, -90.0, 0.0, 0.0);
        instance.draw();
        GL11.glDepthMask(true);
        GL11.glEnable(2929);
        GL11.glEnable(3008);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public void renderVignette(float f, int n, int n2) {
        PackTweaksMod packTweaksMod = CheatBreaker.getInstance().getModManager().packTweaksMod;
        float f2 = (Float)packTweaksMod.vignetteOpacity.getValue() / 100.0F;
        if (packTweaksMod.vignetteType.getValue().equals("Static") && packTweaksMod.isEnabled()) {
            f = f2;
        } else {
            f = 1.0F - f;
        }

        float f3 = 0.0F;
        float f4 = 1.0F;
        if (packTweaksMod.vignetteType.getValue().equals("Amplified") && packTweaksMod.isEnabled()) {
            f3 = packTweaksMod.vignetteMinimumOpacity.getFloatValue() / 100.0F;
            f4 = packTweaksMod.vignetteMaximumOpacity.getFloatValue() / 100.0F;
            if (f3 > f4) {
                packTweaksMod.vignetteMaximumOpacity.changeValue(packTweaksMod.vignetteMinimumOpacity.getFloatValue());
            }

            f *= packTweaksMod.vignetteOpacityMultiplier.getFloatValue();
        }

        if (f < f3) {
            f = f3;
        }

        if (f > f4) {
            f = f4;
        }

        this.prevVignetteBrightness = (float)(this.prevVignetteBrightness + (f - this.prevVignetteBrightness) * 0.01);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        OpenGlHelper.glBlendFunc(0, 769, 1, 0);
        GL11.glColor4f(this.prevVignetteBrightness, this.prevVignetteBrightness, this.prevVignetteBrightness, 1.0F);
        this.mc.getTextureManager().bindTexture(vignetteTexPath);
        Tessellator instance = Tessellator.instance;
        instance.startDrawingQuads();
        instance.addVertexWithUV(0.0, n2, -90.0, 0.0, 1.0);
        instance.addVertexWithUV(n, n2, -90.0, 1.0, 1.0);
        instance.addVertexWithUV(n, 0.0, -90.0, 1.0, 0.0);
        instance.addVertexWithUV(0.0, 0.0, -90.0, 0.0, 0.0);
        instance.draw();
        GL11.glDepthMask(true);
        GL11.glEnable(2929);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
    }

    public void render(float f, int n, int n2) {
        if (f < 1.0F) {
            f *= f;
            f *= f;
            f = f * 0.8F + 0.2F;
        }

        GL11.glDisable(3008);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, f);
        IIcon blockTextureFromSide = Blocks.portal.getBlockTextureFromSide(1);
        this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        float minU = blockTextureFromSide.getMinU();
        float minV = blockTextureFromSide.getMinV();
        float maxU = blockTextureFromSide.getMaxU();
        float maxV = blockTextureFromSide.getMaxV();
        Tessellator instance = Tessellator.instance;
        instance.startDrawingQuads();
        instance.addVertexWithUV(0.0, n2, -90.0, minU, maxV);
        instance.addVertexWithUV(n, n2, -90.0, maxU, maxV);
        instance.addVertexWithUV(n, 0.0, -90.0, maxU, minV);
        instance.addVertexWithUV(0.0, 0.0, -90.0, minU, minV);
        instance.draw();
        GL11.glDepthMask(true);
        GL11.glEnable(2929);
        GL11.glEnable(3008);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public void renderInventorySlot(int n, int n2, int n3, float f) {
        ItemStack itemStack = this.mc.thePlayer.inventory.mainInventory[n];
        if (itemStack != null) {
            float f2 = itemStack.animationsToGo - f;
            if (f2 > 0.0F) {
                GL11.glPushMatrix();
                float f3 = 1.0F + f2 / 5.0F;
                GL11.glTranslatef(n2 + 8, n3 + 12, 0.0F);
                GL11.glScalef(1.0F / f3, (f3 + 1.0F) / 2.0F, 1.0F);
                GL11.glTranslatef(-(n2 + 8), -(n3 + 12), 0.0F);
            }

            itemRenderer.renderItemAndEffectIntoGUI(this.mc.fontRendererObj, this.mc.getTextureManager(), itemStack, n2, n3);
            if (f2 > 0.0F) {
                GL11.glPopMatrix();
            }

            itemRenderer.renderItemOverlayIntoGUI(this.mc.fontRendererObj, this.mc.getTextureManager(), itemStack, n2, n3);
        }
    }

    public void updateTick() {
        if (this.recordPlayingUpFor > 0) {
            this.recordPlayingUpFor--;
        }

        this.updateCounter++;
        if (this.mc.thePlayer != null) {
            ItemStack currentItem = this.mc.thePlayer.inventory.getCurrentItem();
            if (currentItem == null) {
                this.remainingHighlightTicks = 0;
            } else if (this.highlightingItemStack != null
                && currentItem.getItem() == this.highlightingItemStack.getItem()
                && ItemStack.areItemStackTagsEqual(currentItem, this.highlightingItemStack)
                && (currentItem.isItemStackDamageable() || currentItem.getMetadata() == this.highlightingItemStack.getMetadata())) {
                if (this.remainingHighlightTicks > 0) {
                    this.remainingHighlightTicks--;
                }
            } else {
                this.remainingHighlightTicks = 40;
            }

            this.highlightingItemStack = currentItem;
        }
    }

    public void setRecordPlayingMessage(String str) {
        this.setRecordPlaying(I18n.format("record.nowPlaying", str), true);
    }

    public void setRecordPlaying(String str, boolean flag) {
        this.recordPlaying = str;
        this.recordPlayingUpFor = 60;
        this.recordIsPlaying = flag;
    }

    public GuiNewChat getChatGUI() {
        return this.persistantChatGUI;
    }

    public int getUpdateCounter() {
        return this.updateCounter;
    }

    public void process(int n, int n2, float f) {
        boolean flag = this.mc.thePlayer.hurtResistantTime / 3 % 2 == 1;
        GL11.glScalef(f, f, f);
        if (this.mc.thePlayer.hurtResistantTime < 10) {
            flag = false;
        }

        int n3 = MathHelper.ceiling_float_int(this.mc.thePlayer.getHealth());
        int n4 = MathHelper.ceiling_float_int(this.mc.thePlayer.prevHealth);
        this.rand.setSeed(this.updateCounter * 312871);
        boolean flag2 = false;
        FoodStats foodStats = this.mc.thePlayer.getFoodStats();
        int foodLevel = foodStats.getFoodLevel();
        int prevFoodLevel = foodStats.getPrevFoodLevel();
        IAttributeInstance entityAttribute = this.mc.thePlayer.getEntityAttribute(SharedMonsterAttributes.maxHealth);
        int n5 = (int)(n / 2 / f) - 91;
        int n6 = (int)(n / 2 / f) + 91;
        int n7 = (int)(n2 / f) - 39;
        float attributeValue = (float)entityAttribute.getAttributeValue();
        float absorptionAmount = this.mc.thePlayer.getAbsorptionAmount();
        int n8 = MathHelper.ceiling_float_int((attributeValue + absorptionAmount) / 2.0F / 10.0F);
        int max = Math.max(10 - (n8 - 2), 3);
        int n9 = n7 - (n8 - 1) * max - 10;
        float f2 = absorptionAmount;
        int totalArmorValue = this.mc.thePlayer.getTotalArmorValue();
        int n10 = -1;
        if (this.mc.thePlayer.isPotionActive(Potion.regeneration)) {
            n10 = this.updateCounter % MathHelper.ceiling_float_int(attributeValue + 5.0F);
        }

        this.mc.mcProfiler.startSection("armor");

        for (int i = 0; i < 10; i++) {
            if (totalArmorValue > 0) {
                int n11 = n5 + i * 8;
                if (i * 2 + 1 < totalArmorValue) {
                    drawTexturedRect(n11, n9, 34.0F, 9.0F, 9, 9);
                }

                if (i * 2 + 1 == totalArmorValue) {
                    drawTexturedRect(n11, n9, 25.0F, 9.0F, 9, 9);
                }

                if (i * 2 + 1 > totalArmorValue) {
                    drawTexturedRect(n11, n9, 16.0F, 9.0F, 9, 9);
                }
            }
        }

        this.mc.mcProfiler.endStartSection("health");

        for (int i = MathHelper.ceiling_float_int((attributeValue + absorptionAmount) / 2.0F) - 1; i >= 0; i--) {
            int n11 = 16;
            if (this.mc.thePlayer.isPotionActive(Potion.poison)) {
                n11 += 36;
            } else if (this.mc.thePlayer.isPotionActive(Potion.wither)) {
                n11 += 72;
            }

            int n12 = 0;
            if (flag) {
                n12 = 1;
            }

            int n13 = MathHelper.ceiling_float_int((i + 1) / 10.0F) - 1;
            int n14 = n5 + i % 10 * 8;
            int n15 = n7 - n13 * max;
            if (n3 <= 4) {
                n15 += this.rand.nextInt(2);
            }

            if (i == n10) {
                n15 -= 2;
            }

            int n16 = 0;
            if (this.mc.theWorld.getWorldInfo().isHardcoreModeEnabled()) {
                n16 = 5;
            }

            drawTexturedRect(n14, n15, 16 + n12 * 9, 9 * n16, 9, 9);
            if (flag) {
                if (i * 2 + 1 < n4) {
                    drawTexturedRect(n14, n15, n11 + 54, 9 * n16, 9, 9);
                }

                if (i * 2 + 1 == n4) {
                    drawTexturedRect(n14, n15, n11 + 63, 9 * n16, 9, 9);
                }
            }

            if (f2 > 0.0F) {
                if (f2 == absorptionAmount && absorptionAmount % 2.0F == 1.0F) {
                    drawTexturedRect(n14, n15, n11 + 153, 9 * n16, 9, 9);
                } else {
                    drawTexturedRect(n14, n15, n11 + 144, 9 * n16, 9, 9);
                }

                f2 -= 2.0F;
            } else {
                if (i * 2 + 1 < n3) {
                    drawTexturedRect(n14, n15, n11 + 36, 9 * n16, 9, 9);
                }

                if (i * 2 + 1 == n3) {
                    drawTexturedRect(n14, n15, n11 + 45, 9 * n16, 9, 9);
                }
            }
        }

        Entity ridingEntity = this.mc.thePlayer.ridingEntity;
        if (ridingEntity == null) {
            this.mc.mcProfiler.endStartSection("food");

            for (int i = 0; i < 10; i++) {
                int n11 = n7;
                int n12 = 16;
                int n13 = 0;
                if (this.mc.thePlayer.isPotionActive(Potion.hunger)) {
                    n12 += 36;
                    n13 = 13;
                }

                if (this.mc.thePlayer.getFoodStats().getSaturationLevel() <= 0.0F && this.updateCounter % (foodLevel * 3 + 1) == 0) {
                    n11 = n7 + (this.rand.nextInt(3) - 1);
                }

                if (flag2) {
                    n13 = 1;
                }

                int n14 = n6 - i * 8 - 9;
                drawTexturedRect(n14, n11, 16 + n13 * 9, 27.0F, 9, 9);
                if (flag2) {
                    if (i * 2 + 1 < prevFoodLevel) {
                        drawTexturedRect(n14, n11, n12 + 54, 27.0F, 9, 9);
                    }

                    if (i * 2 + 1 == prevFoodLevel) {
                        drawTexturedRect(n14, n11, n12 + 63, 27.0F, 9, 9);
                    }
                }

                if (i * 2 + 1 < foodLevel) {
                    drawTexturedRect(n14, n11, n12 + 36, 27.0F, 9, 9);
                }

                if (i * 2 + 1 == foodLevel) {
                    drawTexturedRect(n14, n11, n12 + 45, 27.0F, 9, 9);
                }
            }
        } else if (ridingEntity instanceof EntityLivingBase) {
            this.mc.mcProfiler.endStartSection("mountHealth");
            EntityLivingBase entityLivingBase = (EntityLivingBase)ridingEntity;
            int ceil = (int)Math.ceil(entityLivingBase.getHealth());
            float maxHealth = entityLivingBase.getMaxHealth();
            int n11 = (int)(maxHealth + 0.5F) / 2;
            if (n11 > 30) {
                n11 = 30;
            }

            int n12 = n7;

            for (int i = 0; n11 > 0; i += 20) {
                int min = Math.min(n11, 10);
                n11 -= min;

                for (int j = 0; j < min; j++) {
                    byte b = 52;
                    int n13 = 0;
                    if (flag2) {
                        n13 = 1;
                    }

                    int n14 = n6 - j * 8 - 9;
                    drawTexturedRect(n14, n12, b + n13 * 9, 9.0F, 9, 9);
                    if (j * 2 + 1 + i < ceil) {
                        drawTexturedRect(n14, n12, b + 36, 9.0F, 9, 9);
                    }

                    if (j * 2 + 1 + i == ceil) {
                        drawTexturedRect(n14, n12, b + 45, 9.0F, 9, 9);
                    }
                }

                n12 -= 10;
            }
        }

        this.mc.mcProfiler.endStartSection("air");
        if (this.mc.thePlayer.isInsideOfMaterial(Material.water)) {
            int air = this.mc.thePlayer.getAir();
            int n11 = MathHelper.ceiling_double_int((air - 2) * 10.0 / 300.0);
            int n12 = MathHelper.ceiling_double_int(air * 10.0 / 300.0) - n11;

            for (int i = 0; i < n11 + n12; i++) {
                if (i < n11) {
                    drawTexturedRect(n6 - i * 8 - 9, n9, 16.0F, 18.0F, 9, 9);
                } else {
                    drawTexturedRect(n6 - i * 8 - 9, n9, 25.0F, 18.0F, 9, 9);
                }
            }
        }

        this.mc.mcProfiler.endSection();
    }
}
