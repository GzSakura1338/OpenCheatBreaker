package com.cheatbreaker.client.ui.screen;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.ui.component.TextButton;
import com.cheatbreaker.client.util.ClientVersion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

public class BuildInfoScreen extends MenuScreen {
    public TextButton back = new TextButton("BACK");

    @Override
    public void initGui() throws java.io.IOException {
        super.initGui();
        this.back.setBounds(this.getScaledWidth() / 2.0F - 30.0F, this.getScaledHeight() / 2.0F + 55.0F, 60.0F, 12.0F);
    }

    @Override
    public void drawScreenScaled(float f, float f2) {
        super.drawScreenScaled(f, f2);
        Gui.drawRect(
            this.getScaledWidth() / 2.0F - 80.0F,
            this.getScaledHeight() / 2.0F - 40.0F,
            this.getScaledWidth() / 2.0F + 80.0F,
            this.getScaledHeight() / 2.0F + 50.0F,
            788529152
        );
        this.back.renderAndTestHover(f, f2, true);
        CheatBreaker.getInstance()
            .getFontManager()
            .playRegular16
            .drawCenteredString(
                "Git Version: " + CheatBreaker.getInstance().getCommitId() + "/" + CheatBreaker.getInstance().getBranch(),
                this.getScaledWidth() / 2.0F,
                this.getScaledHeight() / 2.0F + 14.0F,
                -1118482
            );
        CheatBreaker.getInstance()
            .getFontManager()
            .playRegular16
            .drawCenteredString("Minecraft Version: " + ClientVersion.version, this.getScaledWidth() / 2.0F, this.getScaledHeight() / 2.0F, -1118482);
        CheatBreaker.getInstance()
            .getFontManager()
            .playRegular16
            .drawCenteredString(
                "Client Version: " + CheatBreaker.getInstance().getClientVersion(), this.getScaledWidth() / 2.0F, this.getScaledHeight() / 2.0F - 14.0F, -1118482
            );
    }

    @Override
    public void mouseClickedScaled(float f, float f2, int n) throws java.io.IOException {
        super.mouseClickedScaled(f, f2, n);
        if (this.back.containsPoint(f, f2)) {
            Minecraft.getMinecraft()
                .getSoundHandler()
                .playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
            this.mc2.displayGuiScreen(new MainMenuScreen());
        }
    }
}
