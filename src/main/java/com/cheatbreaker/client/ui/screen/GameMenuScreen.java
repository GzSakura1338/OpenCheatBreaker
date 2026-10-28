package com.cheatbreaker.client.ui.screen;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.animation.ColorAnimation;
import com.cheatbreaker.client.ui.component.TextButton;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class GameMenuScreen extends UIScreen {
    public boolean closing;
    public GuiScreen guiScreen;
    public ColorAnimation colorAnimation3;
    public TextButton backToGameMenu;
    public TextButton menuDisconnect;
    public TextButton reconnect;

    @Override
    public void onGuiClosed() {
        this.mc.entityRenderer.stopUseShader();
    }

    @Override
    public void drawScreenScaled(float f, float f2) {
        if (this.closing && this.colorAnimation3.isFinished()) {
            this.closing = false;
        } else if (!this.closing && this.colorAnimation3.isFinished()) {
            this.closing = true;
        }

        this.drawBlurredBackground(this.getScaledWidth(), this.getScaledHeight());
        float f3 = this.getScaledWidth() / 2.0F;
        float f4 = this.getScaledHeight() / 2.0F - 50.0F;
        CheatBreaker.getInstance().getFontManager().playBold18.drawCenteredString("WARNING!", f3, f4, this.colorAnimation3.getColor(this.closing).getRGB());
        CheatBreaker.getInstance().getFontManager().playRegular16.drawCenteredString("Are you sure you want to disconnect?", f3, f4 + 15.0F, -1);
        this.backToGameMenu.renderAndTestHover(f, f2, true);
        this.menuDisconnect.renderAndTestHover(f, f2, true);
        if (!this.mc.isSingleplayer() && CheatBreaker.getInstance().getKeybindSettings().showReconnectOption.getBooleanValue()) {
            this.reconnect.renderAndTestHover(f, f2, true);
        }
    }

    public GameMenuScreen(GuiScreen guiScreen) {
        this.guiScreen = guiScreen;
        this.colorAnimation3 = new ColorAnimation(2000L, -1, -52429);
        this.backToGameMenu = new TextButton("Back to Game Menu");
        this.menuDisconnect = new TextButton(I18n.format("menu.disconnect"));
        this.reconnect = new TextButton("Reconnect");
    }

    @Override
    public void mouseClickedScaled(float f, float f2, int n) throws java.io.IOException {
        if (this.backToGameMenu.containsPoint(f, f2)) {
            this.mc.getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
            this.mc.displayGuiScreen(this.guiScreen);
        } else if (this.menuDisconnect.containsPoint(f, f2)) {
            this.mc.getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
            this.mc.theWorld.sendQuittingDisconnectingPacket();
            this.mc.loadWorld(null);
            this.mc.displayGuiScreen(new ClassicMainMenuScreen());
        } else if (this.reconnect.containsPoint(f, f2)) {
            if (this.mc.getCurrentServerData() != null && this.mc.theWorld != null) {
                this.mc.theWorld.sendQuittingDisconnectingPacket();
                this.mc.loadWorld(null);
            }

            if (this.mc.serverData != null) {
                this.mc.displayGuiScreen(new GuiConnecting(this, this.mc, this.mc.serverData));
            } else if (this.mc.getCurrentServerData() != null) {
                this.mc.displayGuiScreen(new GuiConnecting(this, this.mc, this.mc.getCurrentServerData()));
            }
        }
    }

    @Override
    public void initGui() throws java.io.IOException {
        if (this.mc.theWorld == null) {
            this.mc.displayGuiScreen(new ClassicMainMenuScreen());
        }

        this.startBlur();
        this.closing = true;
        float f = this.getScaledWidth() / 2.0F;
        float f2 = this.getScaledHeight() / 2.0F - 50.0F;
        if (!this.mc.isSingleplayer() && CheatBreaker.getInstance().getKeybindSettings().showReconnectOption.getBooleanValue()) {
            this.menuDisconnect.setBounds(f - 75.0F, f2 + 50.0F, 74.0F, 12.0F);
            this.reconnect.setBounds(f + 1.0F, f2 + 50.0F, 74.0F, 12.0F);
        } else {
            this.menuDisconnect.setBounds(f - 75.0F, f2 + 50.0F, 150.0F, 12.0F);
        }

        this.backToGameMenu.setBounds(f - 75.0F, f2 + 64.0F, 150.0F, 12.0F);
        this.backToGameMenu.useLightColors();
    }

    @Override
    public void mouseReleasedScaled(float f, float f2, int n) {
    }
}
