package com.cheatbreaker.client.ui.screen;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.cosmetic.SkinModel;
import com.cheatbreaker.client.render.RenderUtils;
import com.cheatbreaker.client.ui.component.TextButton;
import com.cheatbreaker.client.ui.notification.Notification;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

public class SkinSelectScreen extends MenuScreen {
    public int intValue = 0;
    public File selectedFile = null;
    public SkinModel skinModel = SkinModel.STEVE;
    public TextButton cancel = new TextButton("CANCEL");
    public TextButton back = new TextButton("BACK");
    public TextButton chooseSkin = new TextButton("CHOOSE SKIN");
    public TextButton apply = new TextButton("APPLY");
    public TextButton steve = new TextButton("STEVE");
    public TextButton alex = new TextButton("ALEX");
    public CheatBreaker cheatBreaker = CheatBreaker.getInstance();

    public String getText() {
        return this.skinModel == SkinModel.STEVE ? "Steve" : "Alex";
    }

    public void playButtonSound() {
        Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
    }

    @Override
    public void initGui() throws IOException {
        super.initGui();
        this.cancel.setBounds(this.getScaledWidth() / 2.0F - 30.0F, this.getScaledHeight() / 2.0F + 105.0F, 60.0F, 12.0F);
        this.chooseSkin.setBounds(this.getScaledWidth() / 2.0F - 30.0F, this.getScaledHeight() / 2.0F + 25.0F, 60.0F, 12.0F);
        this.steve.setBounds(this.getScaledWidth() / 2.0F - 55.0F, this.getScaledHeight() / 2.0F + 25.0F, 50.0F, 12.0F);
        this.alex.setBounds(this.getScaledWidth() / 2.0F + 4.0F, this.getScaledHeight() / 2.0F + 25.0F, 50.0F, 12.0F);
        this.back.setBounds(this.getScaledWidth() / 2.0F - 30.0F, this.getScaledHeight() / 2.0F + 115.0F, 60.0F, 12.0F);
        this.apply.setBounds(this.getScaledWidth() / 2.0F - 30.0F, this.getScaledHeight() / 2.0F + 130.0F, 60.0F, 12.0F);
    }

    @Override
    public void drawScreenScaled(float f, float f2) {
        super.drawScreenScaled(f, f2);
        float f3 = this.getScaledWidth() / 2.0F;
        float f4 = this.getScaledHeight() / 2.0F;
        if (this.intValue != 2) {
            Gui.drawRect(
                this.getScaledWidth() / 2.0F - 180.0F,
                this.getScaledHeight() / 2.0F - 78.0F,
                this.getScaledWidth() / 2.0F + 180.0F,
                this.getScaledHeight() / 2.0F + 100.0F,
                788529152
            );
        }

        if (this.intValue != 0) {
            this.back.renderAndTestHover(f, f2, true);
        }

        if (this.intValue == 0) {
            this.cheatBreaker
                .getFontManager()
                .playBold16
                .drawCenteredStringWithShadow("Welcome to the Skin Changer!  First, choose your preferred skin model.", f3, f4, -1);
            this.steve.renderAndTestHover(f, f2, true);
            this.alex.renderAndTestHover(f, f2, true);
            this.cancel.renderAndTestHover(f, f2, true);
            this.back.setBounds(this.getScaledWidth() / 2.0F - 30.0F, this.getScaledHeight() / 2.0F + 105.0F, 60.0F, 12.0F);
        } else if (this.intValue == 1) {
            this.cheatBreaker.getFontManager().playBold16.drawCenteredStringWithShadow("Finally, select your skin's PNG file.", f3, f4, -1);
            this.chooseSkin.renderAndTestHover(f, f2, true);
            this.back.setBounds(this.getScaledWidth() / 2.0F - 30.0F, this.getScaledHeight() / 2.0F + 105.0F, 60.0F, 12.0F);
        } else if (this.intValue == 2) {
            RenderUtils.drawRoundedRectFilledPrecise(f3 - 90.0F, f4 - 88.0F, f3 + 90.0F, f4 + 110.0F, 3.0, 788529152);
            float f5 = f3 + this.width - 50.0F - f;
            float f6 = f4 + 30.0F - f2;
            PlayerViewer.render(f3, f4 + 15.0F, f5, f6, f, f2 + 50.0F);
            this.cheatBreaker.getFontManager().playRegular16.drawCenteredStringWithShadow("Like what you see?", f3 - 23.0F, f4 + 85.0F, -1);
            this.cheatBreaker.getFontManager().playBold16.drawCenteredStringWithShadow("Click Apply!", f3 + 38.0F, f4 + 85.0F, -1);
            this.cheatBreaker.getFontManager().playRegular16.drawCenteredStringWithShadow("Go", f3 - 42.0F, f4 + 95.0F, -1);
            this.cheatBreaker.getFontManager().playBold16.drawCenteredStringWithShadow("Back", f3 - 25.5F, f4 + 95.0F, -1);
            this.cheatBreaker.getFontManager().playRegular16.drawCenteredStringWithShadow("to make changes.", f3 + 20.0F, f4 + 95.0F, -1);
            this.apply.renderAndTestHover(f, f2, true);
        }
    }

    @Override
    public void mouseClickedScaled(float f, float f2, int n) throws IOException {
        super.mouseClickedScaled(f, f2, n);
        if (this.back.containsPoint(f, f2) && this.intValue != 0) {
            this.playButtonSound();
            switch (this.intValue) {
                case 1:
                    this.intValue = 0;
                    break;
                case 2:
                    this.intValue = 1;
            }
        } else if (this.cancel.containsPoint(f, f2) && this.intValue != 2) {
            this.playButtonSound();
            this.mc2.displayGuiScreen(new MainMenuScreen());
        } else if (this.intValue == 0) {
            if (this.steve.containsPoint(f, f2)) {
                this.playButtonSound();
                this.skinModel = SkinModel.STEVE;
                this.intValue = 1;
            } else if (this.alex.containsPoint(f, f2)) {
                this.playButtonSound();
                this.skinModel = SkinModel.ALEX;
                this.intValue = 1;
            }
        } else if (this.intValue == 1) {
            if (this.chooseSkin.containsPoint(f, f2)) {
                this.playButtonSound();
                FileDialog fileDialog = new FileDialog((Frame)null);
                fileDialog.setFilenameFilter((file, str) -> str.toLowerCase().endsWith(".png"));
                fileDialog.setVisible(true);
                String directory = fileDialog.getDirectory();
                String file = fileDialog.getFile();
                if (directory != null && file != null) {
                    if (file.toLowerCase().endsWith(".png")) {
                        this.selectedFile = new File(directory + file);
                        this.intValue = 2;
                        this.back.setBounds(this.getScaledWidth() / 2.0F - 30.0F, this.getScaledHeight() / 2.0F + 115.0F, 60.0F, 12.0F);
                        this.apply.setBounds(this.getScaledWidth() / 2.0F - 30.0F, this.getScaledHeight() / 2.0F + 130.0F, 60.0F, 12.0F);
                        float f3 = this.apply.getX();
                        float f4 = this.apply.getY();
                        this.apply.setBounds(this.back.getX(), this.back.getY(), this.apply.getWidth(), this.apply.getHeight());
                        this.back.setBounds(f3, f4, this.back.getWidth(), this.back.getHeight());

                        try {
                            PlayerViewer.resourceLocation = PlayerViewer.loadSkinTexture("fakePlayerSkin_" + PlayerViewer.intValue, directory + file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Notification.post("Skin Changer", EnumChatFormatting.RED + "Only PNG formatted files are supported for skins.");
                    }
                }
            }
        } else {
            if (this.intValue == 2 && this.apply.containsPoint(f, f2)) {
                this.playButtonSound();
                PlayerViewer.uploadSkin(this.selectedFile, this.skinModel);
                this.mc2.displayGuiScreen(new MainMenuScreen());
            }
        }
    }
}
