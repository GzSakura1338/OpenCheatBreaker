package com.cheatbreaker.client.ui.screen;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.discord.DiscordLinkData;
import com.cheatbreaker.client.ui.component.TextButton;
import com.cheatbreaker.client.util.TextUtils;
import java.net.URI;
import java.net.URISyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

public class DiscordLinkScreen extends MenuScreen {
    public TextButton back = new TextButton("BACK");
    public TextButton joinDiscord = new TextButton("JOIN DISCORD");

    @Override
    public void mouseClickedScaled(float f, float f2, int n) throws java.io.IOException {
        super.mouseClickedScaled(f, f2, n);
        if (this.back.containsPoint(f, f2)) {
            Minecraft.getMinecraft()
                .getSoundHandler()
                .playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
            this.mc2.displayGuiScreen(new MainMenuScreen());
        } else if (this.joinDiscord.containsPoint(f, f2)) {
            try {
                this.openUri(new URI("https://discord.gg/3EAzBxf"));

                try {
                    Thread.sleep(50L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (URISyntaxException e) {
                CheatBreaker.getInstance().getLogger().error("Couldn't open link", e);
            }
        }
    }

    public void drawMessage(String str, float f, boolean flag) {
        String wrapToString = TextUtils.wrapSequence(str).setMaxWidth(48).setHyphenate(false).wrapToString();
        String[] split = wrapToString.split("\n");
        float f2 = CheatBreaker.getInstance().getFontManager().playRegular14.getFont().getSize() / 2.0F;
        float f3 = split.length * f2;
        float f4;
        if (flag) {
            f4 = this.getScaledHeight() / 2.0F - f + 5.0F - f3 / 2.0F;
        } else {
            f4 = this.getScaledHeight() / 2.0F - f + 5.0F;
        }

        int n = 0;

        for (String str2 : split) {
            CheatBreaker.getInstance().getFontManager().playRegular14.drawCenteredString(str2, this.getScaledWidth() / 2.0F, f4 + n * f2, -1118482);
            n++;
        }
    }

    @Override
    public void initGui() throws java.io.IOException {
        super.initGui();
        this.back.setBounds(this.getScaledWidth() / 2.0F - 30.0F, this.getScaledHeight() / 2.0F + 55.0F, 60.0F, 12.0F);
        this.joinDiscord.setBounds(this.getScaledWidth() / 2.0F - 30.0F, this.getScaledHeight() / 2.0F + 70.0F, 60.0F, 12.0F);
    }

    @Override
    public void drawScreenScaled(float f, float f2) {
        super.drawScreenScaled(f, f2);
        Gui.drawRect(
            this.getScaledWidth() / 2.0F - 100.0F,
            this.getScaledHeight() / 2.0F - 40.0F,
            this.getScaledWidth() / 2.0F + 100.0F,
            this.getScaledHeight() / 2.0F + 50.0F,
            788529152
        );
        this.back.renderAndTestHover(f, f2, true);
        this.joinDiscord.renderAndTestHover(f, f2, true);
        CheatBreaker.getInstance()
            .getFontManager()
            .playBold18
            .drawCenteredString("Discord Account Link", this.getScaledWidth() / 2.0F, this.getScaledHeight() / 2.0F - 32.0F, -1118482);
        switch (DiscordLinkData.discordLinkState) {
            case STARTED:
                if (DiscordLinkData.text != null) {
                    this.drawMessage("You have requested to link your Discord account with your Minecraft account", 24.0F, false);
                    CheatBreaker.getInstance()
                        .getFontManager()
                        .playBold22
                        .drawCenteredString(DiscordLinkData.text, this.getScaledWidth() / 2.0F, this.getScaledHeight() / 2.0F, -1118482);
                    this.drawMessage(
                        "This code should be pasted into the LINK CODE field in the link button on our Discord server.\n\nExpires in " + getText(), -10.0F, false
                    );
                } else {
                    this.drawMessage(
                        "There was an issue with getting your Discord link code. Please try again later.\n\nIf the issue persists, please contact support."
                    );
                }
                break;
            case IN_PROGRESS:
                if (DiscordLinkData.text != null) {
                    this.drawMessage("You have requested to link your Discord account with your Minecraft account", 24.0F, false);
                    CheatBreaker.getInstance()
                        .getFontManager()
                        .playBold22
                        .drawCenteredString(DiscordLinkData.text, this.getScaledWidth() / 2.0F, this.getScaledHeight() / 2.0F, -1118482);
                    this.drawMessage(
                        "This code should be pasted into the LINK CODE field in the link button on our Discord server.\n\nExpires in " + getText(), -10.0F, false
                    );
                } else {
                    this.drawMessage(
                        "You have requested to link your Discord account with your Minecraft account; however, you have an existing code!\n\nPlease wait 2 minutes for it to expire or check your clipboard history to use the existing code."
                    );
                }
                break;
            case EXPIRED:
                this.drawMessage("Your Discord link code has expired! Please try again to generate a new code.");
                break;
            case ALREADY_LINKED:
                this.drawMessage("You have already linked your account to Discord!\n\nIf you wish to unlink your current Discord account, please contact staff.");
            case SUCCESS:
                this.drawMessage(
                    "You have successfully linked your account to Discord!\n\nIf you wish to unlink your current Discord account, please contact staff."
                );
            case PENDING:
                this.drawMessage("Starting Discord Link. Please wait...\n\nIf you still see this after a few minutes, please contact support.");
        }
    }

    public static String getText() {
        long l = DiscordLinkData.expiresAt - System.currentTimeMillis();
        if (l < 0L) {
            l = 0L;
        }

        long l2 = l / 1000L;
        long l3 = l2 / 60L;
        long l4 = l2 % 60L;
        return String.format("%d:%02d", l3, l4);
    }

    public void drawMessage(String str) {
        this.drawMessage(str, 0.0F, true);
    }
}
