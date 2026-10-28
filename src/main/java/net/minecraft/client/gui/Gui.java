package net.minecraft.client.gui;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class Gui {
    public static ResourceLocation optionsBackground = new ResourceLocation("textures/gui/options_background.png");
    public static ResourceLocation statIcons = new ResourceLocation("textures/gui/container/stats_icons.png");
    public static ResourceLocation icons = new ResourceLocation("textures/gui/icons.png");
    public static float floatValue;

    public void drawHorizontalLine(int startX, int endX, int y, int color) {
        if (endX < startX) {
            int startX2 = startX;
            startX = endX;
            endX = startX2;
        }

        drawRect(startX, y, endX + 1, y + 1, color);
    }

    public void drawVerticalLine(int x, int startY, int endY, int color) {
        if (endY < startY) {
            int startY2 = startY;
            startY = endY;
            endY = startY2;
        }

        drawRect(x, startY + 1, x + 1, endY, color);
    }

    public void drawCenteredString(FontRenderer fontRendererIn, String text, int x, int y, int color) {
        fontRendererIn.drawShadowedString(text, x - fontRendererIn.getStringWidth(text) / 2, y, color);
    }

    public void drawString(FontRenderer fontRendererIn, String text, int x, int y, int color) {
        fontRendererIn.drawShadowedString(text, x, y, color);
    }

    public void drawTexturedModelRectFromIcon(int x, int y, IIcon icon, int width, int height) {
        Tessellator instance = Tessellator.instance;
        instance.startDrawingQuads();
        instance.addVertexWithUV(x + 0, y + height, floatValue, icon.getMinU(), icon.getMaxV());
        instance.addVertexWithUV(x + width, y + height, floatValue, icon.getMaxU(), icon.getMaxV());
        instance.addVertexWithUV(x + width, y + 0, floatValue, icon.getMaxU(), icon.getMinV());
        instance.addVertexWithUV(x + 0, y + 0, floatValue, icon.getMinU(), icon.getMinV());
        instance.draw();
    }

    public static void drawModalRectWithCustomSizedTexture(int x, int y, float u, float v, int width, int height, float textureWidth, float textureHeight) {
        float f = 1.0F / textureWidth;
        float f2 = 1.0F / textureHeight;
        Tessellator instance = Tessellator.instance;
        instance.startDrawingQuads();
        instance.addVertexWithUV(x, y + height, 0.0, u * f, (v + height) * f2);
        instance.addVertexWithUV(x + width, y + height, 0.0, (u + width) * f, (v + height) * f2);
        instance.addVertexWithUV(x + width, y, 0.0, (u + width) * f, v * f2);
        instance.addVertexWithUV(x, y, 0.0, u * f, v * f2);
        instance.draw();
    }

    public static void drawScaledCustomSizeModalRect(
        int x, int y, float u, float v, int uWidth, int vHeight, int width, int height, float tileWidth, float tileHeight
    ) {
        float f = 1.0F / tileWidth;
        float f2 = 1.0F / tileHeight;
        Tessellator instance = Tessellator.instance;
        instance.startDrawingQuads();
        instance.addVertexWithUV(x, y + height, 0.0, u * f, (v + vHeight) * f2);
        instance.addVertexWithUV(x + width, y + height, 0.0, (u + uWidth) * f, (v + vHeight) * f2);
        instance.addVertexWithUV(x + width, y, 0.0, (u + uWidth) * f, v * f2);
        instance.addVertexWithUV(x, y, 0.0, u * f, v * f2);
        instance.draw();
    }

    public static void drawVerticalGradient(float f, float f2, float f3, float f4, int n, int n2) {
        float f5 = (n >> 24 & 0xFF) / 255.0F;
        float f6 = (n >> 16 & 0xFF) / 255.0F;
        float f7 = (n >> 8 & 0xFF) / 255.0F;
        float f8 = (n & 0xFF) / 255.0F;
        float f9 = (n2 >> 24 & 0xFF) / 255.0F;
        float f10 = (n2 >> 16 & 0xFF) / 255.0F;
        float f11 = (n2 >> 8 & 0xFF) / 255.0F;
        float f12 = (n2 & 0xFF) / 255.0F;
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glDisable(3008);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glShadeModel(7425);
        Tessellator instance = Tessellator.instance;
        instance.startDrawingQuads();
        instance.setColorRGBA_F(f6, f7, f8, f5);
        instance.addVertex(f3, f2, floatValue);
        instance.addVertex(f, f2, floatValue);
        instance.setColorRGBA_F(f10, f11, f12, f9);
        instance.addVertex(f, f4, floatValue);
        instance.addVertex(f3, f4, floatValue);
        instance.draw();
        GL11.glShadeModel(7424);
        GL11.glDisable(3042);
        GL11.glEnable(3008);
        GL11.glEnable(3553);
    }

    public static void drawTexturedRect(float f, float f2, float f3, float f4, int n, int n2) {
        float f5 = 0.00390625F;
        float f6 = 0.00390625F;
        Tessellator instance = Tessellator.instance;
        instance.startDrawingQuads();
        instance.addVertexWithUV(f + 0.0F, f2 + n2, floatValue, (f3 + 0.0F) * f5, (f4 + n2) * f6);
        instance.addVertexWithUV(f + n, f2 + n2, floatValue, (f3 + n) * f5, (f4 + n2) * f6);
        instance.addVertexWithUV(f + n, f2 + 0.0F, floatValue, (f3 + n) * f5, (f4 + 0.0F) * f6);
        instance.addVertexWithUV(f + 0.0F, f2 + 0.0F, floatValue, (f3 + 0.0F) * f5, (f4 + 0.0F) * f6);
        instance.draw();
    }

    public static void drawRectWithInsetBorder(float f, float f2, float f3, float f4, float f5, int n, int n2) {
        drawRect(f + f5, f2 + f5, f3 - f5, f4 - f5, n2);
        drawRect(f, f2 + f5, f + f5, f4 - f5, n);
        drawRect(f3 - f5, f2 + f5, f3, f4 - f5, n);
        drawRect(f, f2, f3, f2 + f5, n);
        drawRect(f, f4 - f5, f3, f4, n);
    }

    public static void drawRectWithOutsetBorder(float f, float f2, float f3, float f4, float f5, int n, int n2) {
        drawRect(f, f2, f3, f4, n2);
        drawRect(f - f5, f2 - f5, f, f4 + f5, n);
        drawRect(f3, f2 - f5, f3 + f5, f4 + f5, n);
        drawRect(f, f2 - f5, f3, f2, n);
        drawRect(f, f4, f3, f4 + f5, n);
    }

    public static void drawInsetBorder(float f, float f2, float f3, float f4, float f5, int n) {
        drawRect(f, f2 + f5, f + f5, f4 - f5, n);
        drawRect(f3 - f5, f2 + f5, f3, f4 - f5, n);
        drawRect(f, f2, f3, f2 + f5, n);
        drawRect(f, f4 - f5, f3, f4, n);
    }

    public static void drawRect(float f, float f2, float f3, float f4, int n) {
        if (f < f3) {
            float f5 = f;
            f = f3;
            f3 = f5;
        }

        if (f2 < f4) {
            float f5 = f2;
            f2 = f4;
            f4 = f5;
        }

        float f5 = (n >> 24 & 0xFF) / 255.0F;
        float f6 = (n >> 16 & 0xFF) / 255.0F;
        float f7 = (n >> 8 & 0xFF) / 255.0F;
        float f8 = (n & 0xFF) / 255.0F;
        Tessellator instance = Tessellator.instance;
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor4f(f6, f7, f8, f5);
        instance.startDrawingQuads();
        instance.addVertex(f, f4, 0.0);
        instance.addVertex(f3, f4, 0.0);
        instance.addVertex(f3, f2, 0.0);
        instance.addVertex(f, f2, 0.0);
        instance.draw();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
    }
}
