package com.cheatbreaker.client.ui.screen;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.render.RenderUtils;
import com.cheatbreaker.client.ui.component.TextButton;
import com.cheatbreaker.client.ui.screen.UIScreen;
import com.cheatbreaker.client.voice.VoiceChannel;
import com.cheatbreaker.client.voice.VoiceChatPlayer;
import com.cheatbreaker.nethandler.CBPacket;
import com.cheatbreaker.nethandler.client.CBPacketVoiceChannelSwitch;
import com.cheatbreaker.nethandler.client.CBPacketVoiceMute;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class VoiceChatScreen
extends UIScreen {
    public ResourceLocation headphonesPng = new ResourceLocation("client/icons/headphones.png");
    public ResourceLocation speakerPng = new ResourceLocation("client/icons/speaker.png");
    public List<TextButton> textButtonList;
    public static CheatBreaker cheatBreaker = CheatBreaker.getInstance();
    public TextButton joinChannel;
    public TextButton unDeafen;
    public ResourceLocation speakerMutePng = new ResourceLocation("client/icons/speaker-mute.png");
    public ResourceLocation microphone64Png = new ResourceLocation("client/icons/microphone-64.png");
    public VoiceChannel voiceChannel = null;

    public VoiceChannel findVoiceChannel(String string) {
        VoiceChannel voiceChannel;
        Iterator<VoiceChannel> iterator = cheatBreaker.getNetHandler().getVoiceChannels().iterator();
        do {
            if (iterator.hasNext()) continue;
            return null;
        } while (!(voiceChannel = iterator.next()).getText().equals(string));
        return voiceChannel;
    }

    public void process(float f, float f2, float f3, float f4) {
        float f5 = 14.0f;
        float f6 = (float)this.voiceChannel.getVoiceChatPlayerList().size() * f5;
        f4 -= f6 / 2.0f;
        int n = 0;
        for (VoiceChatPlayer voiceChatPlayer : this.voiceChannel.getVoiceChatPlayerList()) {
            boolean bl;
            float f7 = f4 + (float)n * f5;
            boolean bl2 = bl = f > f3 + 158.0f && f < f3 + 184.0f && f2 > f7 && f2 < f7 + f5;
            if (!voiceChatPlayer.getUuid().equals(this.mc.thePlayer.getUniqueID()) && bl) {
                this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0f));
                cheatBreaker.getNetHandler().sendPacket((CBPacket)new CBPacketVoiceMute(voiceChatPlayer.getUuid()));
                if (!cheatBreaker.getNetHandler().getMutedPlayers().removeIf(uUID -> uUID.equals(voiceChatPlayer.getUuid()))) {
                    cheatBreaker.getNetHandler().getMutedPlayers().add(voiceChatPlayer.getUuid());
                }
            }
            ++n;
        }
    }

    @Override
    public void initGui() {
        this.run2();
        if (cheatBreaker.getNetHandler().isVoiceEnabled() && cheatBreaker.getNetHandler().getVoiceChannels() != null) {
            this.voiceChannel = cheatBreaker.getNetHandler().getVoiceChannel();
            boolean bl = cheatBreaker.getNetHandler().getMutedPlayers().contains(this.mc.thePlayer.getGameProfile().getId());
            this.joinChannel = new TextButton("Join Channel");
            this.unDeafen = new TextButton(bl ? "Un-deafen" : "Deafen");
            this.textButtonList = new ArrayList<TextButton>();
            float f = 16.0f;
            float f2 = this.getScaledWidth() / 8.0f;
            float f3 = this.getScaledHeight() / 2.0f - 8.0f - f * (float)cheatBreaker.getNetHandler().getVoiceChannels().size() / 2.0f;
            int n = 0;
            for (VoiceChannel voiceChannel : cheatBreaker.getNetHandler().getVoiceChannels()) {
                TextButton textButton = new TextButton(voiceChannel.getText());
                this.textButtonList.add(textButton);
                textButton.setBounds(f2, f3 + 12.0f + f * (float)n, 110.0f, 12.0f);
                if (this.voiceChannel == voiceChannel) {
                    textButton.run2();
                }
                ++n;
            }
        }
    }

    @Override
    public void drawScreenScaled(float f, float f2) {
        this.process(this.getScaledWidth(), this.getScaledHeight());
        float f3 = this.getScaledWidth() / 8.0f;
        if (cheatBreaker.getNetHandler().isVoiceEnabled() && cheatBreaker.getNetHandler().getVoiceChannels() != null) {
            float f4 = 16.0f;
            float f5 = this.getScaledHeight() / 2.0f - 8.0f - f4 * (float)cheatBreaker.getNetHandler().getVoiceChannels().size() / 2.0f;
            VoiceChatScreen.cheatBreaker.getFontManager().playBold18.drawString("VOICE CHAT", f3, f5 - 4.0f, -1);
            this.unDeafen.setBounds(f3 + 60.0f, f5 - 4.0f, 50.0f, 12.0f);
            this.unDeafen.renderAndTestHover(f, f2, true);
            this.textButtonList.forEach(textButton -> {
                if (this.findVoiceChannel(textButton.getLabel()) == cheatBreaker.getNetHandler().getVoiceChannel()) {
                    textButton.render2(f, f2, true);
                    RenderUtils.drawTexture(new ResourceLocation("client/icons/microphone-64.png"), textButton.getX() + 4.0f, textButton.getY() + 2.0f, 8.0f, 8.0f);
                } else if (this.voiceChannel != null && this.voiceChannel.getText().equals(textButton.getLabel())) {
                    textButton.render2(f, f2, true);
                } else {
                    textButton.renderAndTestHover(f, f2, true);
                }
            });
            if (this.voiceChannel != null) {
                this.render(f, f2, f3 + 130.0f, this.getScaledHeight() / 2.0f);
            }
        } else {
            float f6 = this.getScaledHeight() / 2.0f - 8.0f;
            VoiceChatScreen.cheatBreaker.getFontManager().playBold18.drawString("VOICE CHAT IS NOT SUPPORTED", f3, f6, -1);
        }
    }

    @Override
    public void keyTyped(char c, int n) throws java.io.IOException {
        super.keyTyped(c, n);
        if (n == 25 && colorAnimation.isFinished()) {
            if (((Boolean)VoiceChatScreen.cheatBreaker.getKeybindSettings().guiBlur.getValue()).booleanValue()) {
                this.mc.entityRenderer.run();
            }
            this.mc.displayGuiScreen(null);
            this.mc.setIngameFocus();
        }
    }

    @Override
    public void mouseReleasedScaled(float f, float f2, int n) {
    }

    @Override
    public void mouseClickedScaled(float f, float f2, int n) {
        if (this.textButtonList != null) {
            Iterator<TextButton> iterator = this.textButtonList.iterator();
            while (true) {
                VoiceChannel voiceChannel;
                if (!iterator.hasNext()) {
                    if (this.voiceChannel != null) {
                        if (this.joinChannel.containsPoint(f, f2)) {
                            this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0f));
                            cheatBreaker.getNetHandler().sendPacket((CBPacket)new CBPacketVoiceChannelSwitch(this.voiceChannel.getUuid()));
                            for (TextButton textButton2 : this.textButtonList) {
                                textButton2.run4();
                            }
                            for (TextButton textButton2 : this.textButtonList) {
                                if (!textButton2.getLabel().equals(this.voiceChannel.getText())) continue;
                                textButton2.run2();
                            }
                        }
                        if (this.unDeafen.containsPoint(f, f2)) {
                            UUID iterator2 = this.mc.thePlayer.getGameProfile().getId();
                            this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0f));
                            cheatBreaker.getNetHandler().sendPacket((CBPacket)new CBPacketVoiceMute(iterator2));
                            if (!cheatBreaker.getNetHandler().getMutedPlayers().removeIf(uUID2 -> uUID2.equals(iterator2))) {
                                cheatBreaker.getNetHandler().getMutedPlayers().add((UUID)((Object)iterator2));
                            }
                            this.unDeafen.setLabel(cheatBreaker.getNetHandler().getMutedPlayers().contains(this.mc.thePlayer.getGameProfile().getId()) ? "Un-deafen" : "Deafen");
                        }
                        this.process(f, f2, this.getScaledWidth() / 8.0f + 130.0f, this.getScaledHeight() / 2.0f);
                    }
                    return;
                }
                TextButton textButton = iterator.next();
                if (!textButton.containsPoint(f, f2) || this.voiceChannel == (voiceChannel = this.findVoiceChannel(textButton.getLabel()))) continue;
                for (TextButton textButton2 : this.textButtonList) {
                    if (this.voiceChannel == cheatBreaker.getNetHandler().getVoiceChannel() || !textButton2.getLabel().equals(this.voiceChannel.getText())) continue;
                    textButton2.run4();
                }
                this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0f));
                this.voiceChannel = voiceChannel;
                if (this.voiceChannel == cheatBreaker.getNetHandler().getVoiceChannel()) continue;
                textButton.run();
            }
        }
    }

    public boolean check() {
        return this.voiceChannel == cheatBreaker.getNetHandler().getVoiceChannel();
    }

    public void render(float f, float f2, float f3, float f4) {
        float f5 = 14.0f;
        float f6 = (float)this.voiceChannel.getVoiceChatPlayerList().size() * f5;
        VoiceChatScreen.cheatBreaker.getFontManager().playBold18.drawString(this.voiceChannel.getText(), f3, (f4 -= f6 / 2.0f) - 14.0f, -1);
        if (!this.check()) {
            this.joinChannel.setBounds(f3 + 125.0f, f4 - 14.0f, 50.0f, 12.0f);
            this.joinChannel.renderAndTestHover(f, f2, true);
        }
        Gui.drawRect(f3, f4, f3 + 175.0f, f4 + f6, -1626337264);
        int n = 0;
        ArrayList<VoiceChatPlayer> arrayList = Lists.newArrayList(this.voiceChannel.getVoiceChatPlayerList());
        arrayList.sort((voiceChatPlayer, voiceChatPlayer2) -> {
            if (this.voiceChannel.checkUUID2(voiceChatPlayer.getUuid()) && !this.voiceChannel.checkUUID2(voiceChatPlayer2.getUuid())) {
                return -1;
            }
            return !this.voiceChannel.checkUUID2(voiceChatPlayer.getUuid()) && this.voiceChannel.checkUUID2(voiceChatPlayer2.getUuid()) ? 1 : 0;
        });
        for (VoiceChatPlayer voiceChatPlayer3 : arrayList) {
            boolean bl = this.voiceChannel.checkUUID2(voiceChatPlayer3.getUuid());
            boolean bl2 = cheatBreaker.getNetHandler().getMutedPlayers().contains(voiceChatPlayer3.getUuid());
            float f7 = f4 + (float)n * f5;
            boolean bl3 = f > f3 + 158.0f && f < f3 + 184.0f && f2 > f7 && f2 < f7 + f5;
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            if (!bl) {
                RenderUtils.drawTexture(this.headphonesPng, f3 + 4.0f, f7 + 3.0f, 8.0f, 8.0f);
            } else {
                RenderUtils.drawTexture(this.microphone64Png, f3 + 4.0f, f7 + 3.0f, 8.0f, 8.0f);
            }
            float f8 = f3 + 10.0f;
            if (!voiceChatPlayer3.getUuid().equals(this.mc.thePlayer.getUniqueID())) {
                if (bl2) {
                    GlStateManager.color(1.0f, 0.1f, 0.1f, bl3 ? 1.0f : 0.6f);
                    RenderUtils.drawTexture(this.speakerMutePng, f3 + 162.0f, f7 + 3.0f, 8.0f, 8.0f);
                } else {
                    GlStateManager.color(1.0f, 1.0f, 1.0f, bl3 ? 1.0f : 0.6f);
                    RenderUtils.drawTexture(this.speakerPng, f3 + 162.0f, f7 + 3.0f, 8.0f, 8.0f);
                }
            }
            VoiceChatScreen.cheatBreaker.getFontManager().ubuntuM16.drawString(voiceChatPlayer3.getName().toUpperCase(), f8 + 6.0f, f7 + 2.0f, bl ? -1 : 0x6FFFFFFF);
            ++n;
        }
    }

    @Override
    public void onGuiClosed() {
        this.mc.entityRenderer.run();
    }
}
