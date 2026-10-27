package net.minecraft.client.gui;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.ScaledResolutionEvent3;
import com.cheatbreaker.client.event.ScaledResolutionEvent5;
import com.cheatbreaker.client.event.ScaledResolutionEvent7;
import com.cheatbreaker.client.event.ScaledResolutionScoreObjectiveScoreboardIntEvent;
import com.cheatbreaker.client.event.ScaledResolutionStringFloatIntEvent;
import com.cheatbreaker.client.event.SimpleEvent23;
import com.cheatbreaker.client.mod.impl.PackTweaksMod;
import com.cheatbreaker.client.ui.screen.FriendsScreen;
import com.cheatbreaker.client.ui.screen.ModSettingsScreen;
import com.cheatbreaker.client.ui.screen.ModsScreen;
import com.cheatbreaker.client.ui.screen.ProfileNameScreen;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiOverlayDebug;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.gui.GuiSpectator;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.FoodStats;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.world.border.WorldBorder;
import net.optifine.Config;
import net.optifine.CustomColors;
import org.lwjgl.opengl.GL11;

public class GuiIngame
extends Gui {
    public static ResourceLocation vignetteTexPath = new ResourceLocation("textures/misc/vignette.png");
    public static ResourceLocation widgetsTexPath = new ResourceLocation("textures/gui/widgets.png");
    public static ResourceLocation pumpkinBlurTexPath = new ResourceLocation("textures/misc/pumpkinblur.png");
    public Random rand = new Random();
    public String recordPlaying = "";
    public float prevVignetteBrightness = 1.0f;
    public float floatValue = 1.0f;
    public int updateCounter;
    public String displayedTitle = "";
    public int recordPlayingUpFor;
    public boolean recordIsPlaying;
    public String displayedSubTitle = "";
    public int remainingHighlightTicks;
    public ItemStack highlightingItemStack;
    public int playerHealth = 0;
    public int lastPlayerHealth = 0;
    public long lastSystemTime = 0L;
    public int titlesTimer;
    public long healthUpdateCounter = 0L;
    public Minecraft mc;
    public int titleFadeIn;
    public int titleDisplayTime;
    public int titleFadeOut;
    public RenderItem itemRenderer;
    public GuiOverlayDebug overlayDebug;
    public GuiSpectator spectatorGui;
    public GuiNewChat persistantChatGUI;
    public float floatValue2;
    public GuiPlayerTabOverlay overlayPlayerList;
    public static boolean flag = false;

    public GuiIngame(Minecraft mcIn) {
        this.mc = mcIn;
        this.itemRenderer = mcIn.getRenderItem();
        this.overlayDebug = new GuiOverlayDebug(mcIn);
        this.spectatorGui = new GuiSpectator(mcIn);
        this.persistantChatGUI = new GuiNewChat(mcIn);
        this.overlayPlayerList = new GuiPlayerTabOverlay(mcIn, this);
        this.setDefaultTitlesTimes();
    }

    public void setDefaultTitlesTimes() {
        this.titleFadeIn = 10;
        this.titleDisplayTime = 70;
        this.titleFadeOut = 20;
    }

    public void renderGameOverlay(float partialTicks) {
        int n;
        int n2;
        float f;
        float f2;
        boolean bl;
        ScaledResolution scaledResolution = new ScaledResolution(this.mc);
        int n3 = scaledResolution.getScaledWidth();
        int n4 = scaledResolution.getScaledHeight();
        this.mc.entityRenderer.setupOverlayRendering();
        GlStateManager.enableBlend();
        PackTweaksMod packTweaksMod = CheatBreaker.getInstance().getModManager().packTweaksMod;
        boolean bl2 = packTweaksMod.isEnabled();
        if (Config.isVignetteEnabled() || packTweaksMod.vignetteType.getValue().equals("Static") && bl2) {
            this.process(this.mc.thePlayer.getBrightness(partialTicks), scaledResolution);
        } else {
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        }
        if (packTweaksMod.lowHealthOverlay.getBooleanValue() && packTweaksMod.isEnabled()) {
            this.renderVignette(packTweaksMod.lowHealthOpacity.getFloatValue() / 100.0f, scaledResolution);
        } else {
            GlStateManager.enableDepth();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        }
        ItemStack itemStack = this.mc.thePlayer.inventory.armorItemInSlot(3);
        boolean bl3 = bl = !bl2 || (Boolean)packTweaksMod.showPumpkinOverlay.getValue() != false;
        if (this.mc.gameSettings.thirdPersonView == 0 && itemStack != null && itemStack.getItem() == Item.getItemFromBlock(Blocks.pumpkin) && bl) {
            this.renderPumpkinOverlay(scaledResolution);
        }
        if (!this.mc.thePlayer.isPotionActive(Potion.confusion) && (f2 = this.mc.thePlayer.prevTimeInPortal + (this.mc.thePlayer.timeInPortal - this.mc.thePlayer.prevTimeInPortal) * partialTicks) > 0.0f) {
            this.renderPortal(f2, scaledResolution);
        }
        if (this.mc.playerController.isSpectator()) {
            this.spectatorGui.renderTooltip(scaledResolution, partialTicks);
        } else {
            this.renderTooltip(scaledResolution, partialTicks);
        }
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.getTextureManager().bindTexture(icons);
        GlStateManager.enableBlend();
        if (this.showCrosshair()) {
            if (CheatBreaker.getInstance().getModManager().crosshairMod.isEnabled()) {
                CheatBreaker.getInstance().getModManager().crosshairMod.render(n3 / 2, n4 / 2, true);
            } else {
                GlStateManager.tryBlendFuncSeparate(775, 769, 1, 0);
                GlStateManager.enableAlpha();
                this.drawTexturedModalRect(n3 / 2 - 7, n4 / 2 - 7, 0, 0, 16, 16);
            }
        }
        GlStateManager.enableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        this.mc.mcProfiler.startSection("bossHealth");
        if (!CheatBreaker.getInstance().getModManager().bossBarMod.isRenderHud() && CheatBreaker.getInstance().getModManager().bossBarMod.isEnabled()) {
            if (BossStatus.bossName != null && BossStatus.statusBarTime > 0) {
                CheatBreaker.getInstance().getModManager().bossBarMod.render(BossStatus.bossName, BossStatus.healthScale, scaledResolution.getScaledWidth() / 2 - 91, 12);
            }
            this.mc.getTextureManager().bindTexture(icons);
        }
        this.mc.mcProfiler.endSection();
        if (this.mc.playerController.shouldDrawHUD()) {
            this.handleScaledResolution(scaledResolution, this.floatValue2);
        }
        GlStateManager.disableBlend();
        if (this.mc.thePlayer.getSleepTimer() > 0) {
            this.mc.mcProfiler.startSection("sleep");
            GlStateManager.disableDepth();
            GlStateManager.disableAlpha();
            int n5 = this.mc.thePlayer.getSleepTimer();
            f = (float)n5 / 100.0f;
            if (f > 1.0f) {
                f = 1.0f - (float)(n5 - 100) / 10.0f;
            }
            n2 = (int)(220.0f * f) << 24 | 0x101020;
            GuiIngame.drawRect(0, 0, n3, n4, n2);
            GlStateManager.enableAlpha();
            GlStateManager.enableDepth();
            this.mc.mcProfiler.endSection();
        }
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        int n6 = (int)((float)n3 / this.floatValue2) / 2 - 91;
        if (this.mc.thePlayer.isRidingHorse()) {
            this.renderHorseJumpBar(scaledResolution, n6);
        } else if (this.mc.playerController.gameIsSurvivalOrAdventure()) {
            GlStateManager.pushMatrix();
            GlStateManager.scale(this.floatValue2, this.floatValue2, this.floatValue2);
            this.renderExpBar(scaledResolution, n6);
            GlStateManager.popMatrix();
        }
        if (this.mc.gameSettings.heldItemTooltips && !this.mc.playerController.isSpectator()) {
            this.renderSelectedItem(scaledResolution);
        } else if (this.mc.thePlayer.isSpectator()) {
            this.spectatorGui.renderSelectedItem(scaledResolution);
        }
        if (this.mc.isDemo()) {
            this.renderDemo(scaledResolution);
        }
        if (this.mc.gameSettings.showDebugInfo) {
            this.overlayDebug.renderDebugInfo(scaledResolution);
        }
        if (flag) {
            this.mc.fontRendererObj.drawStringWithShadow("Uploading screenshot...", 4.0f, scaledResolution.getScaledHeight() - 24, -1);
        }
        if (CheatBreaker.getInstance().getModManager().chatMod.hidden && this.mc.currentScreen instanceof GuiChat) {
            this.mc.fontRendererObj.drawStringWithShadow("Chat is hidden", 4.0f, scaledResolution.getScaledHeight() - 24, -8947849);
        }
        if (this.mc.currentScreen instanceof ModsScreen || this.mc.currentScreen instanceof ModSettingsScreen || this.mc.currentScreen instanceof ProfileNameScreen) {
            CheatBreaker.getInstance().getEventBus().handleEvent(new ScaledResolutionEvent7(scaledResolution));
        }
        CheatBreaker.getInstance().getEventBus().handleEvent(new ScaledResolutionEvent3(scaledResolution));
        if (this.mc.currentScreen == null) {
            FriendsScreen.getFriendsScreen().renderNotifications();
        }
        if (this.recordPlayingUpFor > 0) {
            this.mc.mcProfiler.startSection("overlayMessage");
            f = (float)this.recordPlayingUpFor - partialTicks;
            n2 = (int)(f * 255.0f / 20.0f);
            if (n2 > 255) {
                n2 = 255;
            }
            if (n2 > 8) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(n3 / 2, n4 - 68, 0.0f);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                n = 0xFFFFFF;
                if (this.recordIsPlaying) {
                    n = MathHelper.hsvToRGB(f / 50.0f, 0.7f, 0.6f) & 0xFFFFFF;
                }
                this.getFontRenderer().drawString(this.recordPlaying, -this.getFontRenderer().getStringWidth(this.recordPlaying) / 2, -4.0f, n + (n2 << 24 & 0xFF000000));
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }
            this.mc.mcProfiler.endSection();
        }
        this.mc.mcProfiler.startSection("titleAndSubtitle");
        CheatBreaker.getInstance().getEventBus().handleEvent(new ScaledResolutionStringFloatIntEvent(scaledResolution, this.displayedTitle, this.displayedSubTitle, partialTicks, this.titlesTimer, this.titleDisplayTime, this.titleFadeOut, this.titleFadeIn));
        this.mc.mcProfiler.endSection();
        Scoreboard scoreboard = this.mc.theWorld.getScoreboard();
        ScorePlayerTeam scorePlayerTeam = scoreboard.getPlayersTeam(this.mc.thePlayer.getName());
        if (scorePlayerTeam != null && (n = scorePlayerTeam.getChatFormat().getColorIndex()) >= 0) {
            scoreboard.getObjectiveInDisplaySlot(3 + n);
        }
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.disableAlpha();
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0f, n4 - 48, 0.0f);
        this.mc.mcProfiler.startSection("chat");
        ScaledResolutionEvent5 scaledResolutionEvent5 = new ScaledResolutionEvent5(scaledResolution);
        CheatBreaker.getInstance().getEventBus().handleEvent(scaledResolutionEvent5);
        this.mc.mcProfiler.endSection();
        GlStateManager.popMatrix();
        ScoreObjective scoreObjective = scoreboard.getObjectiveInDisplaySlot(0);
        CheatBreaker.getInstance().getEventBus().handleEvent(new ScaledResolutionScoreObjectiveScoreboardIntEvent(scaledResolution, n3, scoreboard, scoreObjective));
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.disableLighting();
        GlStateManager.enableAlpha();
    }

    public void renderTooltip(ScaledResolution sr, float partialTicks) {
        if (this.mc.getRenderViewEntity() instanceof EntityPlayer) {
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            this.mc.getTextureManager().bindTexture(widgetsTexPath);
            EntityPlayer entityPlayer = (EntityPlayer)this.mc.getRenderViewEntity();
            int n = sr.getScaledWidth() / 2;
            float f = zLevel;
            zLevel = -90.0f;
            this.floatValue2 = 1.0f;
            switch (CheatBreaker.getInstance().getKeybindSettings().hotbarScale.getStringValue()) {
                case "Small": {
                    this.floatValue2 *= 0.5f / CheatBreaker.getInstance().getUiManager().getFloatValue();
                    break;
                }
                case "Normal": {
                    this.floatValue2 /= CheatBreaker.getInstance().getUiManager().getFloatValue();
                    break;
                }
                case "Large": {
                    this.floatValue2 *= 1.5f / CheatBreaker.getInstance().getUiManager().getFloatValue();
                    break;
                }
                case "Auto": {
                    this.floatValue2 *= 2.0f / CheatBreaker.getInstance().getUiManager().getFloatValue();
                }
            }
            this.floatValue2 *= CheatBreaker.getInstance().getKeybindSettings().hotbarScaleMultiplier.getFloatValue();
            int n2 = sr.getScaledWidth();
            int n3 = sr.getScaledHeight();
            GL11.glPushMatrix();
            GL11.glScalef((float)this.floatValue2, (float)this.floatValue2, (float)this.floatValue2);
            this.drawTexturedModalRect((float)(n2 / 2) / this.floatValue2 - 91.0f, (float)n3 / this.floatValue2 - 22.0f, 0, 0, 182, 22);
            this.drawTexturedModalRect((float)(n2 / 2) / this.floatValue2 - 91.0f - 1.0f + (float)(entityPlayer.inventory.currentItem * 20), (float)n3 / this.floatValue2 - 22.0f - 1.0f, 0, 22, 24, 22);
            GL11.glPopMatrix();
            zLevel = f;
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            RenderHelper.enableGUIStandardItemLighting();
            GL11.glPushMatrix();
            GL11.glScalef((float)this.floatValue2, (float)this.floatValue2, (float)this.floatValue2);
            for (int i = 0; i < 9; ++i) {
                int n4 = (int)((float)(n2 / 2) / this.floatValue2) - 90 + i * 20 + 2;
                int n5 = (int)((float)n3 / this.floatValue2) - 16 - 3;
                this.renderHotbarItem(i, n4, n5, partialTicks, entityPlayer);
            }
            GL11.glPopMatrix();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableBlend();
        }
    }

    public void renderHorseJumpBar(ScaledResolution scaledRes, int x) {
        GlStateManager.pushMatrix();
        GlStateManager.scale(this.floatValue2, this.floatValue2, this.floatValue2);
        this.mc.mcProfiler.startSection("jumpBar");
        this.mc.getTextureManager().bindTexture(Gui.icons);
        float f = this.mc.thePlayer.getHorseJumpPower();
        int n = 182;
        int n2 = (int)(f * (float)(n + 1));
        int n3 = (int)((float)scaledRes.getScaledHeight() / this.floatValue2) - 32 + 3;
        this.drawTexturedModalRect(x, n3, 0, 84, n, 5);
        if (n2 > 0) {
            this.drawTexturedModalRect(x, n3, 0, 89, n2, 5);
        }
        this.mc.mcProfiler.endSection();
        GlStateManager.popMatrix();
    }

    public void renderExpBar(ScaledResolution scaledRes, int x) {
        int n;
        int n2;
        this.mc.mcProfiler.startSection("expBar");
        this.mc.getTextureManager().bindTexture(Gui.icons);
        int n3 = this.mc.thePlayer.xpBarCap();
        if (n3 > 0) {
            n2 = 182;
            int n4 = (int)(this.mc.thePlayer.experience * (float)(n2 + 1));
            n = (int)((float)scaledRes.getScaledHeight() / this.floatValue2) - 32 + 3;
            this.drawTexturedModalRect(x, n, 0, 64, n2, 5);
            if (n4 > 0) {
                this.drawTexturedModalRect(x, n, 0, 69, n4, 5);
            }
        }
        this.mc.mcProfiler.endSection();
        if (this.mc.thePlayer.experienceLevel > 0) {
            this.mc.mcProfiler.startSection("expLevel");
            n2 = 8453920;
            if (Config.isCustomColors()) {
                n2 = CustomColors.getExpBarTextColor(n2);
            }
            String string = "" + this.mc.thePlayer.experienceLevel;
            n = ((int)((float)scaledRes.getScaledWidth() / this.floatValue2) - this.getFontRenderer().getStringWidth(string)) / 2;
            int n5 = (int)((float)scaledRes.getScaledHeight() / this.floatValue2) - 31 - 4;
            boolean bl = false;
            this.getFontRenderer().drawString(string, n + 1, n5, 0);
            this.getFontRenderer().drawString(string, n - 1, n5, 0);
            this.getFontRenderer().drawString(string, n, n5 + 1, 0);
            this.getFontRenderer().drawString(string, n, n5 - 1, 0);
            this.getFontRenderer().drawString(string, n, n5, n2);
            this.mc.mcProfiler.endSection();
        }
    }

    public void renderSelectedItem(ScaledResolution scaledRes) {
        GlStateManager.pushMatrix();
        GlStateManager.scale(this.floatValue2, this.floatValue2, this.floatValue2);
        this.mc.mcProfiler.startSection("selectedItemName");
        if (this.remainingHighlightTicks > 0 && this.highlightingItemStack != null) {
            int n;
            String string = this.highlightingItemStack.getDisplayName();
            if (this.highlightingItemStack.hasDisplayName()) {
                string = (Object)((Object)EnumChatFormatting.ITALIC) + string;
            }
            int n2 = scaledRes.getScaledWidth();
            int n3 = scaledRes.getScaledHeight();
            int n4 = ((int)((float)n2 / this.floatValue2) - this.getFontRenderer().getStringWidth(string)) / 2;
            int n5 = (int)((float)n3 / this.floatValue2) - 59;
            if (!this.mc.playerController.shouldDrawHUD()) {
                n5 += 14;
            }
            if ((n = (int)((float)this.remainingHighlightTicks * 256.0f / 10.0f)) > 255) {
                n = 255;
            }
            if (n > 0) {
                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                this.getFontRenderer().drawStringWithShadow(string, n4, n5, 0xFFFFFF + (n << 24));
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }
        }
        this.mc.mcProfiler.endSection();
        GlStateManager.popMatrix();
    }

    public void renderDemo(ScaledResolution scaledRes) {
        this.mc.mcProfiler.startSection("demo");
        String string = "";
        string = this.mc.theWorld.getTotalWorldTime() >= 120500L ? I18n.format("demo.demoExpired", new Object[0]) : I18n.format("demo.remainingTime", StringUtils.ticksToElapsedTime((int)(120500L - this.mc.theWorld.getTotalWorldTime())));
        int n = this.getFontRenderer().getStringWidth(string);
        this.getFontRenderer().drawStringWithShadow(string, scaledRes.getScaledWidth() - n - 10, 5.0f, 0xFFFFFF);
        this.mc.mcProfiler.endSection();
    }

    public boolean showCrosshair() {
        if (this.mc.gameSettings.showDebugInfo && !this.mc.thePlayer.hasReducedDebug() && !this.mc.gameSettings.reducedDebugInfo) {
            return false;
        }
        if (this.mc.playerController.isSpectator()) {
            if (this.mc.pointedEntity != null) {
                return true;
            }
            if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                BlockPos blockPos = this.mc.objectMouseOver.getBlockPos();
                return this.mc.theWorld.getTileEntity(blockPos) instanceof IInventory;
            }
            return false;
        }
        return true;
    }

    public void renderPumpkinOverlay(ScaledResolution scaledRes) {
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.disableAlpha();
        this.mc.getTextureManager().bindTexture(pumpkinBlurTexPath);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldRenderer.pos(0.0, scaledRes.getScaledHeight(), -90.0).tex(0.0, 1.0).endVertex();
        worldRenderer.pos(scaledRes.getScaledWidth(), scaledRes.getScaledHeight(), -90.0).tex(1.0, 1.0).endVertex();
        worldRenderer.pos(scaledRes.getScaledWidth(), 0.0, -90.0).tex(1.0, 0.0).endVertex();
        worldRenderer.pos(0.0, 0.0, -90.0).tex(0.0, 0.0).endVertex();
        tessellator.draw();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public void renderVignette(float lightLevel, ScaledResolution scaledRes) {
        PackTweaksMod packTweaksMod = CheatBreaker.getInstance().getModManager().packTweaksMod;
        lightLevel = this.mc.thePlayer.getHealth() / 2.0f <= packTweaksMod.lowHealthStart.getFloatValue() ? lightLevel : 0.0f;
        lightLevel = MathHelper.clamp_float(lightLevel, 0.0f, 1.0f);
        WorldBorder worldBorder = this.mc.theWorld.getWorldBorder();
        float f = (float)worldBorder.getClosestDistance(this.mc.thePlayer);
        double d = Math.min(worldBorder.getResizeSpeed() * (double)worldBorder.getWarningTime() * 1000.0, Math.abs(worldBorder.getTargetSize() - worldBorder.getDiameter()));
        double d2 = Math.max((double)worldBorder.getWarningDistance(), d);
        f = (double)f < d2 ? 1.0f - (float)((double)f / d2) : 0.0f;
        this.floatValue = (float)((double)this.floatValue + (double)(lightLevel - this.floatValue) * 0.01);
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.tryBlendFuncSeparate(0, 769, 1, 0);
        if (f > 0.0f) {
            GlStateManager.color(0.0f, f, f, 1.0f);
        } else {
            GlStateManager.color(0.0f, this.floatValue, this.floatValue, 1.0f);
        }
        this.mc.getTextureManager().bindTexture(vignetteTexPath);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldRenderer.pos(0.0, scaledRes.getScaledHeight(), -90.0).tex(0.0, 1.0).endVertex();
        worldRenderer.pos(scaledRes.getScaledWidth(), scaledRes.getScaledHeight(), -90.0).tex(1.0, 1.0).endVertex();
        worldRenderer.pos(scaledRes.getScaledWidth(), 0.0, -90.0).tex(1.0, 0.0).endVertex();
        worldRenderer.pos(0.0, 0.0, -90.0).tex(0.0, 0.0).endVertex();
        tessellator.draw();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    }

    public void renderPortal(float timeInPortal, ScaledResolution scaledRes) {
        if (timeInPortal < 1.0f) {
            timeInPortal *= timeInPortal;
            timeInPortal *= timeInPortal;
            timeInPortal = timeInPortal * 0.8f + 0.2f;
        }
        GlStateManager.disableAlpha();
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(1.0f, 1.0f, 1.0f, timeInPortal);
        this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        TextureAtlasSprite textureAtlasSprite = this.mc.getBlockRendererDispatcher().getBlockModelShapes().getTexture(Blocks.portal.getDefaultState());
        float f = textureAtlasSprite.getMinU();
        float f2 = textureAtlasSprite.getMinV();
        float f3 = textureAtlasSprite.getMaxU();
        float f4 = textureAtlasSprite.getMaxV();
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldRenderer.pos(0.0, scaledRes.getScaledHeight(), -90.0).tex(f, f4).endVertex();
        worldRenderer.pos(scaledRes.getScaledWidth(), scaledRes.getScaledHeight(), -90.0).tex(f3, f4).endVertex();
        worldRenderer.pos(scaledRes.getScaledWidth(), 0.0, -90.0).tex(f3, f2).endVertex();
        worldRenderer.pos(0.0, 0.0, -90.0).tex(f, f2).endVertex();
        tessellator.draw();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public void renderHotbarItem(int index, int xPos, int yPos, float partialTicks, EntityPlayer player) {
        ItemStack itemStack = player.inventory.mainInventory[index];
        if (itemStack != null) {
            float f = (float)itemStack.animationsToGo - partialTicks;
            if (f > 0.0f) {
                GlStateManager.pushMatrix();
                float f2 = 1.0f + f / 5.0f;
                GlStateManager.translate(xPos + 8, yPos + 12, 0.0f);
                GlStateManager.scale(1.0f / f2, (f2 + 1.0f) / 2.0f, 1.0f);
                GlStateManager.translate(-(xPos + 8), -(yPos + 12), 0.0f);
            }
            this.itemRenderer.renderItemAndEffectIntoGUI(itemStack, xPos, yPos);
            if (f > 0.0f) {
                GlStateManager.popMatrix();
            }
            this.itemRenderer.renderItemOverlays(this.mc.fontRendererObj, itemStack, xPos, yPos);
        }
    }

    public void updateTick() {
        if (this.recordPlayingUpFor > 0) {
            --this.recordPlayingUpFor;
        }
        if (this.titlesTimer > 0) {
            --this.titlesTimer;
            if (this.titlesTimer <= 0) {
                this.displayedTitle = "";
                this.displayedSubTitle = "";
            }
        }
        ++this.updateCounter;
        if (this.mc.thePlayer != null) {
            ItemStack itemStack = this.mc.thePlayer.inventory.getCurrentItem();
            if (itemStack == null) {
                this.remainingHighlightTicks = 0;
            } else if (this.highlightingItemStack != null && itemStack.getItem() == this.highlightingItemStack.getItem() && ItemStack.areItemStackTagsEqual(itemStack, this.highlightingItemStack) && (itemStack.isItemStackDamageable() || itemStack.getMetadata() == this.highlightingItemStack.getMetadata())) {
                if (this.remainingHighlightTicks > 0) {
                    --this.remainingHighlightTicks;
                }
            } else {
                this.remainingHighlightTicks = 40;
            }
            this.highlightingItemStack = itemStack;
        }
    }

    public void setRecordPlayingMessage(String recordName) {
        this.setRecordPlaying(I18n.format("record.nowPlaying", recordName), true);
    }

    public void setRecordPlaying(String message, boolean isPlaying) {
        this.recordPlaying = message;
        this.recordPlayingUpFor = 60;
        this.recordIsPlaying = isPlaying;
    }

    public void displayTitle(String title, String subTitle, int timeFadeIn, int displayTime, int timeFadeOut) {
        if (title == null && subTitle == null && timeFadeIn < 0 && displayTime < 0 && timeFadeOut < 0) {
            this.displayedTitle = "";
            this.displayedSubTitle = "";
            this.titlesTimer = 0;
        } else if (title != null) {
            this.displayedTitle = title;
            this.titlesTimer = this.titleFadeIn + this.titleDisplayTime + this.titleFadeOut;
        } else if (subTitle != null) {
            this.displayedSubTitle = subTitle;
        } else {
            if (timeFadeIn >= 0) {
                this.titleFadeIn = timeFadeIn;
            }
            if (displayTime >= 0) {
                this.titleDisplayTime = displayTime;
            }
            if (timeFadeOut >= 0) {
                this.titleFadeOut = timeFadeOut;
            }
            if (this.titlesTimer > 0) {
                this.titlesTimer = this.titleFadeIn + this.titleDisplayTime + this.titleFadeOut;
            }
        }
    }

    public void setRecordPlaying(IChatComponent component, boolean isPlaying) {
        this.setRecordPlaying(component.getUnformattedText(), isPlaying);
    }

    public GuiNewChat getChatGUI() {
        return this.persistantChatGUI;
    }

    public int getUpdateCounter() {
        return this.updateCounter;
    }

    public FontRenderer getFontRenderer() {
        return this.mc.fontRendererObj;
    }

    public GuiSpectator getSpectatorGui() {
        return this.spectatorGui;
    }

    public GuiPlayerTabOverlay getTabList() {
        return this.overlayPlayerList;
    }

    public void resetPlayersOverlayFooterHeader() {
        this.overlayPlayerList.resetFooterHeader();
    }

    public void setDisplayedTitle(String string) {
        this.displayedTitle = string;
    }

    public void setDisplayedSubTitle(String string) {
        this.displayedSubTitle = string;
    }

    public void handleScaledResolution(ScaledResolution scaledResolution, float f) {
        if (this.mc.getRenderViewEntity() instanceof EntityPlayer) {
            int n;
            int n2;
            int n3;
            int n4;
            int n5;
            int n6;
            int n7;
            boolean bl;
            GlStateManager.pushMatrix();
            GlStateManager.scale(this.floatValue2, this.floatValue2, this.floatValue2);
            EntityPlayer entityPlayer = (EntityPlayer)this.mc.getRenderViewEntity();
            int n8 = MathHelper.ceiling_float_int(entityPlayer.getHealth());
            boolean bl2 = bl = this.healthUpdateCounter > (long)this.updateCounter && (this.healthUpdateCounter - (long)this.updateCounter) / 3L % 2L == 1L;
            if (n8 < this.playerHealth && entityPlayer.hurtResistantTime > 0) {
                this.lastSystemTime = Minecraft.getSystemTime();
                this.healthUpdateCounter = this.updateCounter + 20;
            } else if (n8 > this.playerHealth && entityPlayer.hurtResistantTime > 0) {
                this.lastSystemTime = Minecraft.getSystemTime();
                this.healthUpdateCounter = this.updateCounter + 10;
            }
            if (Minecraft.getSystemTime() - this.lastSystemTime > 1000L) {
                this.playerHealth = n8;
                this.lastPlayerHealth = n8;
                this.lastSystemTime = Minecraft.getSystemTime();
            }
            this.playerHealth = n8;
            int n9 = this.lastPlayerHealth;
            this.rand.setSeed((long)this.updateCounter * 312871L);
            boolean bl3 = false;
            FoodStats foodStats = entityPlayer.getFoodStats();
            int n10 = foodStats.getFoodLevel();
            int n11 = foodStats.getPrevFoodLevel();
            IAttributeInstance iAttributeInstance = entityPlayer.getEntityAttribute(SharedMonsterAttributes.maxHealth);
            int n12 = (int)((float)(scaledResolution.getScaledWidth() / 2) / f) - 91;
            int n13 = (int)((float)(scaledResolution.getScaledWidth() / 2) / f) + 91;
            int n14 = (int)((float)scaledResolution.getScaledHeight() / f) - 39;
            float f2 = (float)iAttributeInstance.getAttributeValue();
            float f3 = entityPlayer.getAbsorptionAmount();
            int n15 = MathHelper.ceiling_float_int((f2 + f3) / 2.0f / 10.0f);
            int n16 = Math.max(10 - (n15 - 2), 3);
            int n17 = n14 - (n15 - 1) * n16 - 10;
            float f4 = f3;
            int n18 = entityPlayer.getTotalArmorValue();
            int n19 = -1;
            if (entityPlayer.isPotionActive(Potion.regeneration)) {
                n19 = this.updateCounter % MathHelper.ceiling_float_int(f2 + 5.0f);
            }
            this.mc.mcProfiler.startSection("armor");
            for (n7 = 0; n7 < 10; ++n7) {
                if (n18 <= 0) continue;
                n6 = n12 + n7 * 8;
                if (n7 * 2 + 1 < n18) {
                    this.drawTexturedModalRect(n6, n17, 34, 9, 9, 9);
                }
                if (n7 * 2 + 1 == n18) {
                    this.drawTexturedModalRect(n6, n17, 25, 9, 9, 9);
                }
                if (n7 * 2 + 1 <= n18) continue;
                this.drawTexturedModalRect(n6, n17, 16, 9, 9, 9);
            }
            this.mc.mcProfiler.endStartSection("health");
            for (n7 = MathHelper.ceiling_float_int((f2 + f3) / 2.0f) - 1; n7 >= 0; --n7) {
                n6 = 16;
                if (entityPlayer.isPotionActive(Potion.poison)) {
                    n6 += 36;
                } else if (entityPlayer.isPotionActive(Potion.wither)) {
                    n6 += 72;
                }
                n5 = 0;
                if (bl) {
                    n5 = 1;
                }
                n4 = MathHelper.ceiling_float_int((float)(n7 + 1) / 10.0f) - 1;
                n3 = n12 + n7 % 10 * 8;
                n2 = n14 - n4 * n16;
                if (n8 <= 4) {
                    n2 += this.rand.nextInt(2);
                }
                if (n7 == n19) {
                    n2 -= 2;
                }
                n = 0;
                if (entityPlayer.worldObj.getWorldInfo().isHardcoreModeEnabled()) {
                    n = 5;
                }
                this.drawTexturedModalRect(n3, n2, 16 + n5 * 9, 9 * n, 9, 9);
                SimpleEvent23 simpleEvent23 = new SimpleEvent23();
                CheatBreaker.getInstance().getEventBus().handleEvent(simpleEvent23);
                if (bl && !simpleEvent23.isFlag()) {
                    if (n7 * 2 + 1 < n9) {
                        this.drawTexturedModalRect(n3, n2, n6 + 54, 9 * n, 9, 9);
                    }
                    if (n7 * 2 + 1 == n9) {
                        this.drawTexturedModalRect(n3, n2, n6 + 63, 9 * n, 9, 9);
                    }
                }
                if (f4 <= 0.0f) {
                    if (n7 * 2 + 1 < n8) {
                        this.drawTexturedModalRect(n3, n2, n6 + 36, 9 * n, 9, 9);
                    }
                    if (n7 * 2 + 1 != n8) continue;
                    this.drawTexturedModalRect(n3, n2, n6 + 45, 9 * n, 9, 9);
                    continue;
                }
                if (f4 == f3 && f3 % 2.0f == 1.0f) {
                    this.drawTexturedModalRect(n3, n2, n6 + 153, 9 * n, 9, 9);
                } else {
                    this.drawTexturedModalRect(n3, n2, n6 + 144, 9 * n, 9, 9);
                }
                f4 -= 2.0f;
            }
            Entity entity = entityPlayer.ridingEntity;
            if (entity == null) {
                this.mc.mcProfiler.endStartSection("food");
                for (n6 = 0; n6 < 10; ++n6) {
                    n5 = n14;
                    n4 = 16;
                    n3 = 0;
                    if (entityPlayer.isPotionActive(Potion.hunger)) {
                        n4 += 36;
                        n3 = 13;
                    }
                    if (entityPlayer.getFoodStats().getSaturationLevel() <= 0.0f && this.updateCounter % (n10 * 3 + 1) == 0) {
                        n5 = n14 + (this.rand.nextInt(3) - 1);
                    }
                    if (bl3) {
                        n3 = 1;
                    }
                    n2 = n13 - n6 * 8 - 9;
                    this.drawTexturedModalRect(n2, n5, 16 + n3 * 9, 27, 9, 9);
                    if (bl3) {
                        if (n6 * 2 + 1 < n11) {
                            this.drawTexturedModalRect(n2, n5, n4 + 54, 27, 9, 9);
                        }
                        if (n6 * 2 + 1 == n11) {
                            this.drawTexturedModalRect(n2, n5, n4 + 63, 27, 9, 9);
                        }
                    }
                    if (n6 * 2 + 1 < n10) {
                        this.drawTexturedModalRect(n2, n5, n4 + 36, 27, 9, 9);
                    }
                    if (n6 * 2 + 1 != n10) continue;
                    this.drawTexturedModalRect(n2, n5, n4 + 45, 27, 9, 9);
                }
            } else if (entity instanceof EntityLivingBase) {
                this.mc.mcProfiler.endStartSection("mountHealth");
                EntityLivingBase entityLivingBase = (EntityLivingBase)entity;
                n5 = (int)Math.ceil(entityLivingBase.getHealth());
                float f5 = entityLivingBase.getMaxHealth();
                n3 = (int)(f5 + 0.5f) / 2;
                if (n3 > 30) {
                    n3 = 30;
                }
                n2 = n14;
                n = 0;
                while (n3 > 0) {
                    int n20 = Math.min(n3, 10);
                    n3 -= n20;
                    for (int i = 0; i < n20; ++i) {
                        int n21 = 52;
                        int n22 = 0;
                        if (bl3) {
                            n22 = 1;
                        }
                        int n23 = n13 - i * 8 - 9;
                        this.drawTexturedModalRect(n23, n2, n21 + n22 * 9, 9, 9, 9);
                        if (i * 2 + 1 + n < n5) {
                            this.drawTexturedModalRect(n23, n2, n21 + 36, 9, 9, 9);
                        }
                        if (i * 2 + 1 + n != n5) continue;
                        this.drawTexturedModalRect(n23, n2, n21 + 45, 9, 9, 9);
                    }
                    n2 -= 10;
                    n += 20;
                }
            }
            this.mc.mcProfiler.endStartSection("air");
            if (entityPlayer.isInsideOfMaterial(Material.water)) {
                int n24 = this.mc.thePlayer.getAir();
                n5 = MathHelper.ceiling_double_int((double)(n24 - 2) * 10.0 / 300.0);
                int n25 = MathHelper.ceiling_double_int((double)n24 * 10.0 / 300.0) - n5;
                for (n3 = 0; n3 < n5 + n25; ++n3) {
                    if (n3 < n5) {
                        this.drawTexturedModalRect(n13 - n3 * 8 - 9, n17, 16, 18, 9, 9);
                        continue;
                    }
                    this.drawTexturedModalRect(n13 - n3 * 8 - 9, n17, 25, 18, 9, 9);
                }
            }
            this.mc.mcProfiler.endSection();
            GlStateManager.popMatrix();
        }
    }

    public void process(float f, ScaledResolution scaledResolution) {
        PackTweaksMod packTweaksMod = CheatBreaker.getInstance().getModManager().packTweaksMod;
        float f2 = ((Float)packTweaksMod.vignetteOpacity.getValue()).floatValue() / 100.0f;
        f = packTweaksMod.vignetteType.getValue().equals("Static") && packTweaksMod.isEnabled() ? f2 : 1.0f - f;
        if (!Config.isVignetteEnabled()) {
            GlStateManager.enableDepth();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        } else {
            f = MathHelper.clamp_float(f, 0.0f, 1.0f);
            WorldBorder worldBorder = this.mc.theWorld.getWorldBorder();
            float f3 = (float)worldBorder.getClosestDistance(this.mc.thePlayer);
            double d = Math.min(worldBorder.getResizeSpeed() * (double)worldBorder.getWarningTime() * 1000.0, Math.abs(worldBorder.getTargetSize() - worldBorder.getDiameter()));
            double d2 = Math.max((double)worldBorder.getWarningDistance(), d);
            f3 = (double)f3 < d2 ? 1.0f - (float)((double)f3 / d2) : 0.0f;
            float f4 = 0.0f;
            float f5 = 1.0f;
            if (packTweaksMod.vignetteType.getValue().equals("Amplified") && packTweaksMod.isEnabled()) {
                f4 = packTweaksMod.vignetteMinimumOpacity.getFloatValue() / 100.0f;
                if (f4 > (f5 = packTweaksMod.vignetteMaximumOpacity.getFloatValue() / 100.0f)) {
                    packTweaksMod.vignetteMaximumOpacity.changeValue(Float.valueOf(packTweaksMod.vignetteMinimumOpacity.getFloatValue()));
                }
                f *= packTweaksMod.vignetteOpacityMultiplier.getFloatValue();
            }
            if (f < f4) {
                f = f4;
            }
            if (f > f5) {
                f = f5;
            }
            this.prevVignetteBrightness = (float)((double)this.prevVignetteBrightness + (double)(f - this.prevVignetteBrightness) * 0.01);
            GlStateManager.disableDepth();
            GlStateManager.depthMask(false);
            GlStateManager.tryBlendFuncSeparate(0, 769, 1, 0);
            if (f3 > 0.0f) {
                GlStateManager.color(0.0f, f3, f3, 1.0f);
            } else {
                GlStateManager.color(this.prevVignetteBrightness, this.prevVignetteBrightness, this.prevVignetteBrightness, 1.0f);
            }
            this.mc.getTextureManager().bindTexture(vignetteTexPath);
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldRenderer = tessellator.getWorldRenderer();
            worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
            worldRenderer.pos(0.0, scaledResolution.getScaledHeight(), -90.0).tex(0.0, 1.0).endVertex();
            worldRenderer.pos(scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight(), -90.0).tex(1.0, 1.0).endVertex();
            worldRenderer.pos(scaledResolution.getScaledWidth(), 0.0, -90.0).tex(1.0, 0.0).endVertex();
            worldRenderer.pos(0.0, 0.0, -90.0).tex(0.0, 0.0).endVertex();
            tessellator.draw();
            GlStateManager.depthMask(true);
            GlStateManager.enableDepth();
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        }
    }
}
