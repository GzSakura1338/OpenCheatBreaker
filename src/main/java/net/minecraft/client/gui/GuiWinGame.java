package net.minecraft.client.gui;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.Charsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

public class GuiWinGame extends GuiScreen {
    public static Logger logger = LogManager.getLogger();
    public static ResourceLocation MINECRAFT_TEXTURE = new ResourceLocation("textures/gui/title/minecraft.png");
    public static ResourceLocation VIGNETTE_TEXTURE = new ResourceLocation("textures/misc/vignette.png");
    public int totalTicks;
    public List credits;
    public int creditsHeight;
    public final float speed = 0.5f;

    @Override
    public void updateScreen() throws java.io.IOException {
        ++this.totalTicks;
        float f = (float)(this.creditsHeight + this.height + this.height + 24) / this.speed;
        if ((float)this.totalTicks > f) {
            this.sendRespawnPacket();
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws java.io.IOException {
        if (keyCode == 1) {
            this.sendRespawnPacket();
        }
    }

    public void sendRespawnPacket() throws java.io.IOException {
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
            this.credits = new ArrayList();
            try {
                int n;
                String string = "";
                String string2 = "" + (Object)((Object)EnumChatFormatting.WHITE) + (Object)((Object)EnumChatFormatting.OBFUSCATED) + (Object)((Object)EnumChatFormatting.GREEN) + (Object)((Object)EnumChatFormatting.AQUA);
                int n2 = 274;
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.mc.getResourceManager().getResource(new ResourceLocation("texts/end.txt")).getInputStream(), Charsets.UTF_8));
                Random random = new Random(8124371L);
                while ((string = bufferedReader.readLine()) != null) {
                    string = string.replaceAll("PLAYERNAME", this.mc.getSession().getUsername());
                    while (string.contains(string2)) {
                        n = string.indexOf(string2);
                        String string3 = string.substring(0, n);
                        String string4 = string.substring(n + string2.length());
                        string = string3 + (Object)((Object)EnumChatFormatting.WHITE) + (Object)((Object)EnumChatFormatting.OBFUSCATED) + "XXXXXXXX".substring(0, random.nextInt(4) + 3) + string4;
                    }
                    this.credits.addAll(this.mc.fontRendererObj.listFormattedStringToWidth(string, n2));
                    this.credits.add("");
                }
                for (n = 0; n < 8; ++n) {
                    this.credits.add("");
                }
                bufferedReader = new BufferedReader(new InputStreamReader(this.mc.getResourceManager().getResource(new ResourceLocation("texts/credits.txt")).getInputStream(), Charsets.UTF_8));
                while ((string = bufferedReader.readLine()) != null) {
                    string = string.replaceAll("PLAYERNAME", this.mc.getSession().getUsername());
                    string = string.replaceAll("\t", "    ");
                    this.credits.addAll(this.mc.fontRendererObj.listFormattedStringToWidth(string, n2));
                    this.credits.add("");
                }
                this.creditsHeight = this.credits.size() * 12;
            }
            catch (Exception exception) {
                logger.error("Couldn't load credits", (Throwable)exception);
            }
        }
    }

    public void drawWinGameScreen(int n, int n2, float f) {
        Tessellator tessellator = Tessellator.instance;
        this.mc.getTextureManager().bindTexture(Gui.optionsBackground);
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_F(1.0f, 1.0f, 1.0f, 1.0f);
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
        tessellator.setColorOpaque_F(f5, f5, f5);
        tessellator.addVertexWithUV(0.0, this.height, floatValue, 0.0, f2 * f4);
        tessellator.addVertexWithUV(n3, this.height, floatValue, (float)n3 * f4, f2 * f4);
        tessellator.addVertexWithUV(n3, 0.0, floatValue, (float)n3 * f4, f3 * f4);
        tessellator.addVertexWithUV(0.0, 0.0, floatValue, 0.0, f3 * f4);
        tessellator.draw();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) throws java.io.IOException {
        int n;
        this.drawWinGameScreen(mouseX, mouseY, partialTicks);
        Tessellator tessellator = Tessellator.instance;
        int n2 = 274;
        int n3 = this.width / 2 - n2 / 2;
        int n4 = this.height + 50;
        float f = -((float)this.totalTicks + partialTicks) * this.speed;
        GL11.glPushMatrix();
        GL11.glTranslatef((float)0.0f, (float)f, (float)0.0f);
        this.mc.getTextureManager().bindTexture(MINECRAFT_TEXTURE);
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GuiWinGame.drawTexturedRect(n3, n4, 0.0f, 0.0f, 155, 44);
        GuiWinGame.drawTexturedRect(n3 + 155, n4, 0.0f, 45.0f, 155, 44);
        tessellator.setColorOpaque_I(0xFFFFFF);
        int n5 = n4 + 200;
        for (n = 0; n < this.credits.size(); ++n) {
            float f2;
            if (n == this.credits.size() - 1 && (f2 = (float)n5 + f - (float)(this.height / 2 - 6)) < 0.0f) {
                GL11.glTranslatef((float)0.0f, (float)(-f2), (float)0.0f);
            }
            if ((float)n5 + f + 12.0f + 8.0f > 0.0f && (float)n5 + f < (float)this.height) {
                String string = (String)this.credits.get(n);
                if (string.startsWith("[C]")) {
                    this.fontRendererObj.drawShadowedString(string.substring(3), n3 + (n2 - this.fontRendererObj.getStringWidth(string.substring(3))) / 2, n5, 0xFFFFFF);
                } else {
                    this.fontRendererObj.fontRandom.setSeed((long)n * 4238972211L + (long)(this.totalTicks / 4));
                    this.fontRendererObj.drawShadowedString(string, n3, n5, 0xFFFFFF);
                }
            }
            n5 += 12;
        }
        GL11.glPopMatrix();
        this.mc.getTextureManager().bindTexture(VIGNETTE_TEXTURE);
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)0, (int)769);
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_F(1.0f, 1.0f, 1.0f, 1.0f);
        n = this.width;
        int n6 = this.height;
        tessellator.addVertexWithUV(0.0, n6, floatValue, 0.0, 1.0);
        tessellator.addVertexWithUV(n, n6, floatValue, 1.0, 1.0);
        tessellator.addVertexWithUV(n, 0.0, floatValue, 1.0, 0.0);
        tessellator.addVertexWithUV(0.0, 0.0, floatValue, 0.0, 0.0);
        tessellator.draw();
        GL11.glDisable((int)3042);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
