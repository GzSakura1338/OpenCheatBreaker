package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Random;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.Charsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuiWinGame
extends GuiScreen {
    public static Logger logger = LogManager.getLogger();
    public static ResourceLocation MINECRAFT_LOGO = new ResourceLocation("textures/gui/title/minecraft.png");
    public static ResourceLocation VIGNETTE_TEXTURE = new ResourceLocation("textures/misc/vignette.png");
    public int totalTicks;
    public List<String> credits;
    public int creditsHeight;
    public float speed = 0.5f;

    @Override
    public void updateScreen() {
        MusicTicker musicTicker = this.mc.getMusicTicker();
        SoundHandler soundHandler = this.mc.getSoundHandler();
        if (this.totalTicks == 0) {
            musicTicker.stop();
            musicTicker.play(MusicTicker.MusicType.CREDITS);
            soundHandler.resumeSounds();
        }
        soundHandler.update();
        ++this.totalTicks;
        float f = (float)(this.creditsHeight + this.height + this.height + 24) / this.speed;
        if ((float)this.totalTicks > f) {
            this.sendRespawnPacket();
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            this.sendRespawnPacket();
        }
    }

    public void sendRespawnPacket() {
        this.mc.thePlayer.sendQueue.addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.PERFORM_RESPAWN));
        this.mc.displayGuiScreen(null);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }

    @Override
    public void initGui() {
        if (this.credits == null) {
            this.credits = Lists.newArrayList();
            try {
                String string = "";
                String string2 = "" + (Object)((Object)EnumChatFormatting.WHITE) + (Object)((Object)EnumChatFormatting.OBFUSCATED) + (Object)((Object)EnumChatFormatting.GREEN) + (Object)((Object)EnumChatFormatting.AQUA);
                int n = 274;
                InputStream inputStream = this.mc.getResourceManager().getResource(new ResourceLocation("texts/end.txt")).getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charsets.UTF_8));
                Random random = new Random(8124371L);
                while ((string = bufferedReader.readLine()) != null) {
                    string = string.replaceAll("PLAYERNAME", this.mc.getSession().getUsername());
                    while (string.contains(string2)) {
                        int n2 = string.indexOf(string2);
                        String string3 = string.substring(0, n2);
                        String string4 = string.substring(n2 + string2.length());
                        string = string3 + (Object)((Object)EnumChatFormatting.WHITE) + (Object)((Object)EnumChatFormatting.OBFUSCATED) + "XXXXXXXX".substring(0, random.nextInt(4) + 3) + string4;
                    }
                    this.credits.addAll(this.mc.fontRendererObj.listFormattedStringToWidth(string, n));
                    this.credits.add("");
                }
                inputStream.close();
                for (int i = 0; i < 8; ++i) {
                    this.credits.add("");
                }
                inputStream = this.mc.getResourceManager().getResource(new ResourceLocation("texts/credits.txt")).getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charsets.UTF_8));
                while ((string = bufferedReader.readLine()) != null) {
                    string = string.replaceAll("PLAYERNAME", this.mc.getSession().getUsername());
                    string = string.replaceAll("\t", "    ");
                    this.credits.addAll(this.mc.fontRendererObj.listFormattedStringToWidth(string, n));
                    this.credits.add("");
                }
                inputStream.close();
                this.creditsHeight = this.credits.size() * 12;
            }
            catch (Exception exception) {
                logger.error("Couldn't load credits", (Throwable)exception);
            }
        }
    }

    public void drawWinGameScreen(int n, int n2, float f) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        this.mc.getTextureManager().bindTexture(Gui.optionsBackground);
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        int n3 = this.width;
        float f2 = 0.0f - ((float)this.totalTicks + f) * 0.5f * this.speed;
        float f3 = (float)this.height - ((float)this.totalTicks + f) * 0.5f * this.speed;
        float f4 = 0.015625f;
        float f5 = ((float)this.totalTicks + f - 0.0f) * 0.02f;
        float f6 = (float)(this.creditsHeight + this.height + this.height + 24) / this.speed;
        float f7 = (f6 - 20.0f - ((float)this.totalTicks + f)) * 0.005f;
        if (f7 < f5) {
            f5 = f7;
        }
        if (f5 > 1.0f) {
            f5 = 1.0f;
        }
        f5 *= f5;
        f5 = f5 * 96.0f / 255.0f;
        worldRenderer.pos(0.0, this.height, zLevel).tex(0.0, f2 * f4).color(f5, f5, f5, 1.0f).endVertex();
        worldRenderer.pos(n3, this.height, zLevel).tex((float)n3 * f4, f2 * f4).color(f5, f5, f5, 1.0f).endVertex();
        worldRenderer.pos(n3, 0.0, zLevel).tex((float)n3 * f4, f3 * f4).color(f5, f5, f5, 1.0f).endVertex();
        worldRenderer.pos(0.0, 0.0, zLevel).tex(0.0, f3 * f4).color(f5, f5, f5, 1.0f).endVertex();
        tessellator.draw();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        int n;
        this.drawWinGameScreen(mouseX, mouseY, partialTicks);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        int n2 = 274;
        int n3 = this.width / 2 - n2 / 2;
        int n4 = this.height + 50;
        float f = -((float)this.totalTicks + partialTicks) * this.speed;
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0f, f, 0.0f);
        this.mc.getTextureManager().bindTexture(MINECRAFT_LOGO);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        this.drawTexturedModalRect(n3, n4, 0, 0, 155, 44);
        this.drawTexturedModalRect(n3 + 155, n4, 0, 45, 155, 44);
        int n5 = n4 + 200;
        for (n = 0; n < this.credits.size(); ++n) {
            float f2;
            if (n == this.credits.size() - 1 && (f2 = (float)n5 + f - (float)(this.height / 2 - 6)) < 0.0f) {
                GlStateManager.translate(0.0f, -f2, 0.0f);
            }
            if ((float)n5 + f + 12.0f + 8.0f > 0.0f && (float)n5 + f < (float)this.height) {
                String string = this.credits.get(n);
                if (string.startsWith("[C]")) {
                    this.fontRendererObj.drawStringWithShadow(string.substring(3), n3 + (n2 - this.fontRendererObj.getStringWidth(string.substring(3))) / 2, n5, 0xFFFFFF);
                } else {
                    this.fontRendererObj.fontRandom.setSeed((long)n * 4238972211L + (long)(this.totalTicks / 4));
                    this.fontRendererObj.drawString(string, n3, n5, 0xFFFFFF);
                }
            }
            n5 += 12;
        }
        GlStateManager.popMatrix();
        this.mc.getTextureManager().bindTexture(VIGNETTE_TEXTURE);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(0, 769);
        n = this.width;
        int n6 = this.height;
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        worldRenderer.pos(0.0, n6, zLevel).tex(0.0, 1.0).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
        worldRenderer.pos(n, n6, zLevel).tex(1.0, 1.0).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
        worldRenderer.pos(n, 0.0, zLevel).tex(1.0, 0.0).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
        worldRenderer.pos(0.0, 0.0, zLevel).tex(0.0, 0.0).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
        tessellator.draw();
        GlStateManager.disableBlend();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
