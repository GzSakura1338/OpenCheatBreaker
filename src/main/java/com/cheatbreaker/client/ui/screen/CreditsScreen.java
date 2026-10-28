package com.cheatbreaker.client.ui.screen;

import com.cheatbreaker.client.CheatBreaker;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.optifine.GlStateManager;
import org.apache.commons.io.Charsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

public class CreditsScreen extends GuiScreen {
    public final float floatValue2 = 0.5F;
    public static Logger logger = LogManager.getLogger();
    public int intValue;
    public static ResourceLocation minecraftPng = new ResourceLocation("textures/gui/title/minecraft.png");
    public int intValue2;
    public static ResourceLocation vignettePng = new ResourceLocation("textures/misc/vignette.png");
    public List list;

    public void drawBackground(int n, int n2, float f) {
        Tessellator instance = Tessellator.instance;
        this.mc.getTextureManager().bindTexture(Gui.optionsBackground);
        instance.startDrawingQuads();
        instance.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F);
        int width = this.width;
        float f2 = 0.0F - (this.intValue2 + f) * 0.5F * 0.5F;
        float f3 = this.height - (this.intValue2 + f) * 0.5F * 0.5F;
        float f4 = 0.015625F;
        float f5 = (this.intValue2 + f - 0.0F) * 0.02F;
        float f6 = (this.intValue + this.height + this.height + 24) / 0.5F;
        float f7 = (f6 - 20.0F - (this.intValue2 + f)) * 0.005F;
        if (f7 < f5) {
            f5 = f7;
        }

        if (f5 > 1.0F) {
            f5 = 1.0F;
        }

        f5 *= f5;
        f5 = f5 * 96.0F / 255.0F;
        instance.setColorOpaque_F(f5, f5, f5);
        instance.addVertexWithUV(0.0, this.height, floatValue, 0.0, f2 * f4);
        instance.addVertexWithUV(width, this.height, floatValue, width * f4, f2 * f4);
        instance.addVertexWithUV(width, 0.0, floatValue, width * f4, f3 * f4);
        instance.addVertexWithUV(0.0, 0.0, floatValue, 0.0, f3 * f4);
        instance.draw();
    }

    public void returnToMenu() throws java.io.IOException {
        this.mc.displayGuiScreen(CheatBreaker.getInstance().getGuiScreen());
    }

    @Override
    public void keyTyped(char c, int n) throws java.io.IOException {
        if (n == 1) {
            this.returnToMenu();
        }
    }

    @Override
    public void updateScreen() throws java.io.IOException {
        this.intValue2++;
        float f = (this.intValue + this.height + this.height + 24) / 0.5F;
        if (this.intValue2 > f) {
            this.returnToMenu();
        }
    }

    @Override
    public void drawScreen(int n, int n2, float f) throws java.io.IOException {
        this.drawBackground(n, n2, f);
        Tessellator instance = Tessellator.instance;
        short s = 274;
        int n3 = this.width / 2 - s / 2;
        int n4 = this.height + 50;
        float f2 = -(this.intValue2 + f) * 0.5F;
        GlStateManager.pushMatrix();
        GL11.glTranslatef(0.0F, f2, 0.0F);
        this.mc.getTextureManager().bindTexture(minecraftPng);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(3008);
        drawTexturedRect(n3, n4, 0.0F, 0.0F, 155, 44);
        drawTexturedRect(n3 + 155, n4, 0.0F, 45.0F, 155, 44);
        GL11.glDisable(3008);
        int n5 = n4 + 200;

        for (int i = 0; i < this.list.size(); i++) {
            if (i == this.list.size() - 1) {
                float f3 = n5 + f2 - (this.height / 2 - 6);
                if (f3 < 0.0F) {
                    GL11.glTranslatef(0.0F, -f3, 0.0F);
                }
            }

            if (n5 + f2 + 12.0F + 8.0F > 0.0F && n5 + f2 < this.height) {
                String str = (String)this.list.get(i);
                if (str.startsWith("[C]")) {
                    this.fontRendererObj
                        .drawShadowedString(str.substring(3), n3 + (s - this.fontRendererObj.getStringWidth(str.substring(3))) / 2, n5, 16777215);
                } else {
                    this.fontRendererObj.fontRandom.setSeed(i * 4238972211L + this.intValue2 / 4);
                    this.fontRendererObj.drawShadowedString(str, n3, n5, 16777215);
                }
            }

            n5 += 12;
        }

        GlStateManager.popMatrix();
        this.mc.getTextureManager().bindTexture(vignettePng);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(0, 769);
        instance.startDrawingQuads();
        instance.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F);
        int width = this.width;
        int height = this.height;
        instance.addVertexWithUV(0.0, height, floatValue, 0.0, 1.0);
        instance.addVertexWithUV(width, height, floatValue, 1.0, 1.0);
        instance.addVertexWithUV(width, 0.0, floatValue, 1.0, 0.0);
        instance.addVertexWithUV(0.0, 0.0, floatValue, 0.0, 0.0);
        instance.draw();
        GlStateManager.disableBlend();
        super.drawScreen(n, n2, f);
    }

    @Override
    public void initGui() {
        if (this.list == null) {
            this.list = new ArrayList();

            try {
                short s = 274;
                BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(this.mc.getResourceManager().getResource(new ResourceLocation("texts/credits.txt")).getInputStream(), Charsets.UTF_8)
                );

                String str;
                while ((str = bufferedReader.readLine()) != null) {
                    str = str.replaceAll("PLAYERNAME", this.mc.getSession().getUsername());
                    str = str.replaceAll("\t", "    ");
                    this.list.addAll(this.mc.fontRendererObj.listFormattedStringToWidth(str, s));
                    this.list.add("");
                }

                this.intValue = this.list.size() * 12;
            } catch (Exception e) {
                logger.error("Couldn't load credits", e);
            }
        }
    }
}
