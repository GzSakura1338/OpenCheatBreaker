package com.cheatbreaker.client.ui.screen;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.animation.CosineAnimation;
import com.cheatbreaker.client.render.RenderUtils;
import java.awt.Color;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiShareToLan;
import net.minecraft.client.gui.achievement.GuiAchievements;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class ClientHomeScreen extends GuiScreen {
    public ResourceLocation logoWhitePng = new ResourceLocation("client/logo_white.png");
    public ResourceLocation logoRedPng = new ResourceLocation("client/logo_red.png");
    public ResourceLocation logo255OuterPng = new ResourceLocation("client/logo_255_outer.png");
    public ResourceLocation logo108InnerPng = new ResourceLocation("client/logo_108_inner.png");
    public CosineAnimation cosineAnimation = new CosineAnimation(4000L);
    public boolean flag = false;
    public String text;
    public boolean flag2 = false;
    public int intValue;
    public boolean flag3 = false;
    public int intValue2;
    public long longValue;
    public boolean flag4 = false;
    public CosineAnimation cosineAnimation2 = new CosineAnimation(1500L);
    public long longValue2;
    public GuiButton serverList;
    public GuiButton mods;
    public long longValue3;
    public long longValue4;

    @Override
    public void drawScreen(int n, int n2, float f) throws java.io.IOException {
        this.drawDefaultBackground();
        short s = 600;
        short s2 = 356;
        double d = Math.min(this.width, this.height) / (s * 9.0);
        int n3 = (int)(s * d);
        int n4 = (int)(s2 * d);
        if (CheatBreaker.getInstance().getKeybindSettings().getCurrentMenu().getIntValue() == 1) {
            this.render(this.width, this.height);
        } else {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            RenderUtils.drawTexture(this.logoWhitePng, this.width / 2 - n3 / 2, n4 * 2, n3, n4);
        }

        boolean flag = false;

        for (ServiceStatus serviceStatus : CheatBreaker.getInstance().serviceStatuses) {
            if (serviceStatus.getStatusLevel() == StatusLevel.DOWN) {
                flag = true;
            }
        }

        if (CheatBreaker.getInstance().getKeybindSettings().disableDisconnectButton.getBooleanValue()) {
            int n5 = this.longValue4 == 0L
                ? (int)CheatBreaker.getInstance().getKeybindSettings().disableDisconnectDuration.getFloatValue()
                : (int)(
                    (
                            CheatBreaker.getInstance().getKeybindSettings().disableDisconnectDuration.getFloatValue() * 1000.0F
                                + 999.0F
                                - (float)(System.currentTimeMillis() - this.longValue4)
                        )
                        / 1000.0F
                );
            if (n5 <= 0) {
                this.buttonList.get(0).enabled = true;
            }
        }

        if (CheatBreaker.getInstance().getKeybindSettings().disableServerListButton.getBooleanValue()) {
            int n5 = this.longValue4 == 0L
                ? (int)CheatBreaker.getInstance().getKeybindSettings().disableServerListDuration.getFloatValue()
                : (int)(
                    (
                            CheatBreaker.getInstance().getKeybindSettings().disableServerListDuration.getFloatValue() * 1000.0F
                                + 999.0F
                                - (float)(System.currentTimeMillis() - this.longValue4)
                        )
                        / 1000.0F
                );
            if (n5 <= 0) {
                this.serverList.enabled = true;
            }
        }

        if (CheatBreaker.getInstance().getKeybindSettings().disableModsButton.getBooleanValue()) {
            int n5 = this.longValue4 == 0L
                ? (int)CheatBreaker.getInstance().getKeybindSettings().disableModsDuration.getFloatValue()
                : (int)(
                    (
                            CheatBreaker.getInstance().getKeybindSettings().disableModsDuration.getFloatValue() * 1000.0F
                                + 999.0F
                                - (float)(System.currentTimeMillis() - this.longValue4)
                        )
                        / 1000.0F
                );
            if (n5 <= 0) {
                this.mods.enabled = true;
            }
        }

        if (this.flag3 && Mouse.isButtonDown(0) && CheatBreaker.getInstance().getKeybindSettings().holdDisconnectButton.getBooleanValue()) {
            float f2 = this.longValue2 == 0L
                ? CheatBreaker.getInstance().getKeybindSettings().holdDisconnectDuration.getFloatValue()
                : (
                        CheatBreaker.getInstance().getKeybindSettings().holdDisconnectDuration.getFloatValue() * 1000.0F
                            + 999.0F
                            - (float)(System.currentTimeMillis() - this.longValue2)
                    )
                    / 1000.0F;
            this.buttonList.get(0).displayString = this.text + " (" + (int)f2 + ")";
            if (f2 < 1.0F && f2 > 0.9F) {
                this.onDisconnectClicked(this.buttonList.get(0));
            }
        } else {
            this.buttonList.get(0).displayString = this.text;
            this.longValue2 = 0L;
            this.flag3 = false;
        }

        if (this.serverList.enabled
            && this.flag2
            && Mouse.isButtonDown(0)
            && CheatBreaker.getInstance().getKeybindSettings().holdServerListButton.getBooleanValue()) {
            float f2 = this.longValue == 0L
                ? CheatBreaker.getInstance().getKeybindSettings().holdServerListDuration.getFloatValue()
                : (
                        CheatBreaker.getInstance().getKeybindSettings().holdServerListDuration.getFloatValue() * 1000.0F
                            + 999.0F
                            - (float)(System.currentTimeMillis() - this.longValue)
                    )
                    / 1000.0F;
            this.serverList.displayString = "Server List (" + (int)f2 + ")";
            if (f2 < 1.0F && f2 > 0.9F) {
                this.mc.displayGuiScreen(new GuiMultiplayer(this));
            }
        } else {
            this.serverList.displayString = "Server List";
            this.longValue = 0L;
            this.flag2 = false;
        }

        if (this.flag
            && Mouse.isButtonDown(0)
            && CheatBreaker.getInstance().getKeybindSettings().streamerMode.getBooleanValue()
            && CheatBreaker.getInstance().getKeybindSettings().holdModsButton.getBooleanValue()) {
            float f2 = this.longValue3 == 0L
                ? CheatBreaker.getInstance().getKeybindSettings().holdModsDuration.getFloatValue()
                : (
                        CheatBreaker.getInstance().getKeybindSettings().holdModsDuration.getFloatValue() * 1000.0F
                            + 999.0F
                            - (float)(System.currentTimeMillis() - this.longValue3)
                    )
                    / 1000.0F;
            this.mods.displayString = "Mods (" + (int)f2 + ")";
            if ((int)f2 == 0) {
                this.mc.displayGuiScreen(new ModsScreen());
            }
        } else {
            this.mods.displayString = "Mods";
            this.longValue3 = 0L;
            this.flag = false;
        }

        if (flag) {
            if (!this.cosineAnimation2.hasStarted()) {
                this.cosineAnimation2.start();
            }

            this.cosineAnimation2.enableRepeat();
            drawRect(this.width / 2 - 100, this.height / 4 + 128, this.width / 2 + 100, this.height / 4 + 142, 1862270976);
            drawRect(
                this.width / 2 - 100,
                this.height / 4 + 128,
                this.width / 2 + 100,
                this.height / 4 + 142,
                new Color(1.0F, 0.15F, 0.15F, 0.65F * this.cosineAnimation2.getProgress()).getRGB()
            );
            CheatBreaker.getInstance()
                .getFontManager()
                .ubuntuM16
                .drawCenteredString("Some login services might be offline".toUpperCase(), this.width / 2, this.height / 4 + 130, -1);
        }

        super.drawScreen(n, n2, f);
    }

    @Override
    public void initGui() {
        this.intValue2 = 0;
        this.buttonList.clear();
        byte b = -16;
        boolean flag = true;
        this.longValue4 = System.currentTimeMillis();
        this.text = I18n.format("menu.returnToMenu");
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + b, this.text));
        if (!this.mc.isIntegratedServerRunning()) {
            this.buttonList.get(0).displayString = this.text = I18n.format("menu.disconnect");
        }

        this.buttonList.add(new GuiButton(4, this.width / 2 - 100, this.height / 4 + 24 + b, I18n.format("menu.returnToGame")));
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + b, 98, 20, I18n.format("menu.options")));
        GuiButton guiButton = new GuiButton(7, this.width / 2 + 2, this.height / 4 + 96 + b, 98, 20, I18n.format("menu.shareToLan"));
        guiButton.enabled = this.mc.isSingleplayer() && !this.mc.getIntegratedServer().getPublic();
        this.buttonList.add(new GuiButton(5, this.width / 2 - 100, this.height / 4 + 48 + b, 98, 20, I18n.format("gui.achievements")));
        this.buttonList.add(new GuiButton(6, this.width / 2 + 2, this.height / 4 + 48 + b, 98, 20, I18n.format("gui.stats")));
        if (!guiButton.enabled) {
            this.mods = new GuiButton(10, this.width / 2 + 2, this.height / 4 + 96 + b, 98, 20, "Mods");
            this.buttonList.add(this.mods);
            this.serverList = new GuiButton(16, this.width / 2 - 100, this.height / 4 + 72 + b, 200, 20, "Server List");
        } else {
            this.buttonList.add(guiButton);
            this.serverList = new GuiButton(16, this.width / 2 - 100, this.height / 4 + 72 + b, 98, 20, "Server List");
            this.mods = new GuiButton(10, this.width / 2 + 2, this.height / 4 + 72 + b, 98, 20, "Mods");
            this.buttonList.add(this.mods);
        }

        boolean isSingleplayer = this.mc.isSingleplayer();
        boolean flag2 = !CheatBreaker.getInstance().getKeybindSettings().disableDisconnectIn.getValue().equals("Multiplayer") && isSingleplayer;
        boolean flag3 = !CheatBreaker.getInstance().getKeybindSettings().disableDisconnectIn.getValue().equals("Singleplayer") && !isSingleplayer;
        boolean booleanValue = CheatBreaker.getInstance().getKeybindSettings().disableServerListButton.getBooleanValue();
        if (booleanValue) {
            this.serverList.enabled = isSingleplayer && CheatBreaker.getInstance().getKeybindSettings().disableServerListIn.getStringValue().equalsIgnoreCase("Multiplayer")
                || !isSingleplayer && CheatBreaker.getInstance().getKeybindSettings().disableServerListIn.getStringValue().equalsIgnoreCase("Singleplayer");
        }

        this.buttonList.add(this.serverList);
        if (CheatBreaker.getInstance().getKeybindSettings().disableDisconnectButton.getBooleanValue() && (flag2 || flag3)) {
            this.buttonList.get(0).enabled = false;
        }

        if (CheatBreaker.getInstance().getKeybindSettings().streamerMode.getBooleanValue()
            && CheatBreaker.getInstance().getKeybindSettings().disableModsButton.getBooleanValue()) {
            this.mods.enabled = false;
        }

        if (CheatBreaker.isAprilFoolsEnabled() && CheatBreaker.getInstance().getKeybindSettings().aprilFools.getBooleanValue()) {
            this.cosineAnimation = new CosineAnimation((long)Math.abs(Math.random() * 1000.0));
        }
    }

    public void render(double d, double d2) {
        try {
            if (!this.cosineAnimation.hasStarted()) {
                this.cosineAnimation.start();
                this.cosineAnimation.enableRepeat();
            }

            float f = 18.0F;
            double d3 = d / 2.0 - f;
            double d4 = this.buttonList.size() > 2 ? this.buttonList.get(1).yPosition - f - 32.0F : -100.0;
            GL11.glPushMatrix();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glTranslatef((float)d3, (float)d4, 1.0F);
            GL11.glTranslatef(f, f, f);
            GL11.glRotatef(180.0F * this.cosineAnimation.getProgress(), 0.0F, 0.0F, 1.0F);
            GL11.glTranslatef(-f, -f, -f);
            RenderUtils.drawSquareTexture(this.logo255OuterPng, f, 0.0F, 0.0F);
            GL11.glPopMatrix();
            RenderUtils.drawSquareTexture(this.logo108InnerPng, f, (float)d3, (float)d4);
        } catch (Exception e) {
        }
    }

    @Override
    public void updateScreen() throws java.io.IOException {
        super.updateScreen();
        this.intValue++;
    }

    public void onDisconnectClicked(GuiButton guiButton) throws java.io.IOException {
        if (CheatBreaker.getInstance().getNetHandler().isCompetitiveGamemode()) {
            this.mc.displayGuiScreen(new WarningScreen(this));
        } else {
            if (CheatBreaker.getInstance().getKeybindSettings().disconnectConfirmationPrompt.getBooleanValue()) {
                boolean flag = !CheatBreaker.getInstance().getKeybindSettings().showPromptIn.getValue().equals("Multiplayer") && this.mc.isSingleplayer();
                boolean flag2 = !CheatBreaker.getInstance().getKeybindSettings().showPromptIn.getValue().equals("Singleplayer") && !this.mc.isSingleplayer();
                if (CheatBreaker.getInstance().getKeybindSettings().smartDisconnect.getBooleanValue()) {
                    int n = this.longValue4 == 0L
                        ? (int)CheatBreaker.getInstance().getKeybindSettings().waitDuration.getFloatValue()
                        : (int)(
                            (
                                    CheatBreaker.getInstance().getKeybindSettings().waitDuration.getFloatValue() * 1000.0F
                                        + 999.0F
                                        - (float)(System.currentTimeMillis() - this.longValue4)
                                )
                                / 1000.0F
                        );
                    if (n > 0 && (flag || flag2)) {
                        this.mc.displayGuiScreen(new GameMenuScreen(this));
                        return;
                    }
                } else if (flag || flag2) {
                    this.mc.displayGuiScreen(new GameMenuScreen(this));
                    return;
                }
            }

            guiButton.enabled = false;
            this.mc.theWorld.sendQuittingDisconnectingPacket();
            this.mc.loadWorld(null);
            this.mc.displayGuiScreen(new ClassicMainMenuScreen());
        }
    }

    @Override
    public void actionPerformed(GuiButton guiButton) throws java.io.IOException {
        this.flag = false;
        switch (guiButton.id) {
            case 0:
                this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
                break;
            case 1:
                boolean flag = !CheatBreaker.getInstance().getKeybindSettings().holdDisconnectIn.getValue().equals("Multiplayer") && this.mc.isSingleplayer();
                boolean flag2 = !CheatBreaker.getInstance().getKeybindSettings().holdDisconnectIn.getValue().equals("Singleplayer") && !this.mc.isSingleplayer();
                if (!CheatBreaker.getInstance().getKeybindSettings().holdDisconnectButton.getBooleanValue() || !flag && !flag2) {
                    this.onDisconnectClicked(guiButton);
                } else {
                    this.flag3 = true;
                    this.longValue2 = System.currentTimeMillis();
                }
            case 2:
            case 3:
            case 8:
            case 9:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            default:
                break;
            case 4:
                this.mc.displayGuiScreen(null);
                this.mc.setIngameFocus();
                break;
            case 5:
                this.mc.displayGuiScreen(new GuiAchievements(this, this.mc.thePlayer.getStatFileWriter()));
                break;
            case 6:
                this.mc.displayGuiScreen(new GuiStats(this, this.mc.thePlayer.getStatFileWriter()));
                break;
            case 7:
                this.mc.displayGuiScreen(new GuiShareToLan(this));
                break;
            case 10:
                if (CheatBreaker.getInstance().getKeybindSettings().streamerMode.getBooleanValue()
                    && CheatBreaker.getInstance().getKeybindSettings().holdModsButton.getBooleanValue()) {
                    this.flag = true;
                    this.longValue3 = System.currentTimeMillis();
                } else {
                    this.mc.displayGuiScreen(new ModsScreen());
                }
                break;
            case 16:
                boolean flag3 = !CheatBreaker.getInstance().getKeybindSettings().holdServerListIn.getValue().equals("Multiplayer") && this.mc.isSingleplayer();
                boolean flag4 = !CheatBreaker.getInstance().getKeybindSettings().holdServerListIn.getValue().equals("Singleplayer") && !this.mc.isSingleplayer();
                if (!CheatBreaker.getInstance().getKeybindSettings().holdServerListButton.getBooleanValue() || !flag3 && !flag4) {
                    this.mc.displayGuiScreen(new GuiMultiplayer(this));
                } else {
                    this.flag2 = true;
                    this.longValue = System.currentTimeMillis();
                }
        }
    }
}
