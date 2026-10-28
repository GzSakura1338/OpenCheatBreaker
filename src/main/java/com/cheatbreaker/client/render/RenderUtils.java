package com.cheatbreaker.client.render;

import com.cheatbreaker.client.util.FastTrig;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.optifine.GlStateManager;
import org.lwjgl.opengl.GL11;

public class RenderUtils {
    public static ResourceLocation logoCircle1Png = new ResourceLocation("client/logo_circle_1.png");
    public static ResourceLocation logoWhite1Png = new ResourceLocation("client/logo_white_1.png");
    public static final float UNUSED_ZERO = 0.0F;
    public static ResourceLocation logoWhite2Png = new ResourceLocation("client/logo_white_2.png");
    public static final double CIRCLE_SEGMENTS = 30.0;

    public static void drawBorderedRect(float f, float f2, float f3, float f4, int n, int n2) {
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        float f5;
        float f6;
        float f7;
        float f8;
        Gui.drawRect((f5 = f * 2.0F) + 1.0F, (f6 = f2 * 2.0F) + 1.0F, (f7 = f3 * 2.0F) - 1.0F, (f8 = f4 * 2.0F) - 1.0F, n2);
        drawVerticalLine(f5, f6 + 1.0F, f8 - 2.0F, n);
        drawVerticalLine(f7 - 1.0F, f6 + 1.0F, f8 - 2.0F, n);
        drawHorizontalLine(f5 + 2.0F, f7 - 3.0F, f6, n);
        drawHorizontalLine(f5 + 2.0F, f7 - 3.0F, f8 - 1.0F, n);
        drawHorizontalLine(f5 + 1.0F, f5 + 1.0F, f6 + 1.0F, n);
        drawHorizontalLine(f7 - 2.0F, f7 - 2.0F, f6 + 1.0F, n);
        drawHorizontalLine(f7 - 2.0F, f7 - 2.0F, f8 - 2.0F, n);
        drawHorizontalLine(f5 + 1.0F, f5 + 1.0F, f8 - 2.0F, n);
        GL11.glScalef(2.0F, 2.0F, 2.0F);
    }

    public static void drawVerticalLine(float f, float f2, float f3, int n) {
        if (f3 < f2) {
            float f4 = f2;
            f2 = f3;
            f3 = f4;
        }

        Gui.drawRect(f, f2 + 1.0F, f + 1.0F, f3, n);
    }

    public static void setColorArgb(int n) {
        float f = (n >> 24 & 0xFF) / 255.0F;
        float f2 = (n >> 16 & 0xFF) / 255.0F;
        float f3 = (n >> 8 & 0xFF) / 255.0F;
        float f4 = (n & 0xFF) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        GL11.glColor4f(f2, f3, f4, f);
    }

    public static void scissor(int n, int n2, int n3, int n4, ScaledResolution scaledResolution) {
        int scaleFactor = scaledResolution.getScaleFactor();
        int n5 = n4 - n2;
        int n6 = n3 - n;
        int n7 = scaledResolution.getScaledHeight() - n4;
        GL11.glScissor(n * scaleFactor, n7 * scaleFactor, n6 * scaleFactor, n5 * scaleFactor);
    }

    public static void drawRectSized(float f, float f2, float f3, float f4, int n) {
        setColorArgb(n);
        float f5 = f + f3;
        float f6 = f2 + f4;
        Tessellator instance = Tessellator.instance;
        instance.startDrawingQuads();
        instance.addVertex(f, f6, 0.0);
        instance.addVertex(f5, f6, 0.0);
        instance.addVertex(f5, f2, 0.0);
        instance.addVertex(f, f2, 0.0);
        instance.draw();
        endColorDraw();
    }

    public static void drawHorizontalLine(float f, float f2, float f3, int n) {
        if (f2 < f) {
            float f4 = f;
            f = f2;
            f2 = f4;
        }

        Gui.drawRect(f, f3, f2 + 1.0F, f3 + 1.0F, n);
    }

    public RenderUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static void drawSquareTextureViaRenderEngine(ResourceLocation resourceLocation, float f, float f2, float f3) {
        float f4 = f * 2.0F;
        float f5 = f * 2.0F;
        float f6 = 0.0F;
        float f7 = 0.0F;
        GlStateManager.enableBlend();
        Minecraft.getMinecraft().renderEngine.bindTexture(resourceLocation);
        GL11.glBegin(7);
        GL11.glTexCoord2d(f6 / f, f7 / f);
        GL11.glVertex2d(f2, f3);
        GL11.glTexCoord2d(f6 / f, (f7 + f) / f);
        GL11.glVertex2d(f2, f3 + f5);
        GL11.glTexCoord2d((f6 + f) / f, (f7 + f) / f);
        GL11.glVertex2d(f2 + f4, f3 + f5);
        GL11.glTexCoord2d((f6 + f) / f, f7 / f);
        GL11.glVertex2d(f2 + f4, f3);
        GL11.glEnd();
        GlStateManager.disableBlend();
    }

    public static void drawSquareTexture(ResourceLocation resourceLocation, float f, float f2, float f3) {
        float f4 = f * 2.0F;
        float f5 = f * 2.0F;
        float f6 = 0.0F;
        float f7 = 0.0F;
        GlStateManager.enableBlend();
        Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
        GL11.glBegin(7);
        GL11.glTexCoord2d(f6 / f, f7 / f);
        GL11.glVertex2d(f2, f3);
        GL11.glTexCoord2d(f6 / f, (f7 + f) / f);
        GL11.glVertex2d(f2, f3 + f5);
        GL11.glTexCoord2d((f6 + f) / f, (f7 + f) / f);
        GL11.glVertex2d(f2 + f4, f3 + f5);
        GL11.glTexCoord2d((f6 + f) / f, f7 / f);
        GL11.glVertex2d(f2 + f4, f3);
        GL11.glEnd();
        GlStateManager.disableBlend();
    }

    public static void drawRectWithShadow(float f, float f2, float f3, float f4, int n, boolean flag) {
        if (flag) {
            Gui.drawRect(
                f + 1.0F,
                f2 + 1.0F,
                f3 + 1.0F,
                f4 + 1.0F,
                new Color((n >> 16 & 0xFF) / 4, (n >> 8 & 0xFF) / 4, (n & 0xFF) / 4, n >> 24 & 0xFF).getRGB()
            );
        }

        Gui.drawRect(f, f2, f3, f4, n);
    }

    public static void drawProgressRing(double d, double d2, double d3, double d4, double d5, int n, double d6) {
        GlStateManager.enableBlend();
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        d5 = (d5 + n) % n;
        Tessellator instance = Tessellator.instance;

        for (double d7 = 360.0 / n * d5; d7 < 360.0 / n * (d5 + d6); d7++) {
            double d8 = d7 * Math.PI / 180.0;
            double d9 = (d7 - 1.0) * Math.PI / 180.0;
            double[] doubles = new double[]{FastTrig.cos(d8) * d3, -FastTrig.sin(d8) * d3, FastTrig.cos(d9) * d3, -FastTrig.sin(d9) * d3};
            double[] doubles2 = new double[]{FastTrig.cos(d8) * d4, -FastTrig.sin(d8) * d4, FastTrig.cos(d9) * d4, -FastTrig.sin(d9) * d4};
            instance.startDrawing(7);
            instance.addVertex(d + doubles2[0], d2 + doubles2[1], 0.0);
            instance.addVertex(d + doubles2[2], d2 + doubles2[3], 0.0);
            instance.addVertex(d + doubles[2], d2 + doubles[3], 0.0);
            instance.addVertex(d + doubles[0], d2 + doubles[1], 0.0);
            instance.draw();
        }

        GL11.glEnable(3553);
        GlStateManager.disableBlend();
        GL11.glDisable(2848);
        GlStateManager.disableBlend();
        GL11.glEnable(3553);
    }

    public static void drawRoundedRectFilledPrecise(double d, double d2, double d3, double d4, double d5, int n) {
        float f = (n >> 24 & 0xFF) / 255.0F;
        float f2 = (n >> 16 & 0xFF) / 255.0F;
        float f3 = (n >> 8 & 0xFF) / 255.0F;
        float f4 = (n & 0xFF) / 255.0F;
        GL11.glPushAttrib(0);
        GL11.glScaled(0.5, 0.5, 0.5);
        d *= 2.0;
        d2 *= 2.0;
        d3 *= 2.0;
        d4 *= 2.0;
        GlStateManager.enableBlend();
        GL11.glDisable(3553);
        GlStateManager.color(f2, f3, f4, f);
        GL11.glEnable(2848);
        GL11.glBegin(9);

        for (int i = 0; i <= 90; i += 3) {
            GL11.glVertex2d(
                d + d5 + FastTrig.sin(i * Math.PI / 180.0) * (d5 * -1.0), d2 + d5 + FastTrig.cos(i * Math.PI / 180.0) * (d5 * -1.0)
            );
        }

        for (int i = 90; i <= 180; i += 3) {
            GL11.glVertex2d(
                d + d5 + FastTrig.sin(i * Math.PI / 180.0) * (d5 * -1.0), d4 - d5 + FastTrig.cos(i * Math.PI / 180.0) * (d5 * -1.0)
            );
        }

        for (int i = 0; i <= 90; i += 3) {
            GL11.glVertex2d(d3 - d5 + FastTrig.sin(i * Math.PI / 180.0) * d5, d4 - d5 + FastTrig.cos(i * Math.PI / 180.0) * d5);
        }

        for (int i = 90; i <= 180; i += 3) {
            GL11.glVertex2d(d3 - d5 + FastTrig.sin(i * Math.PI / 180.0) * d5, d2 + d5 + FastTrig.cos(i * Math.PI / 180.0) * d5);
        }

        GL11.glEnd();
        GL11.glEnable(3553);
        GlStateManager.disableBlend();
        GL11.glDisable(2848);
        GlStateManager.disableBlend();
        GL11.glEnable(3553);
        GL11.glScaled(2.0, 2.0, 2.0);
        GL11.glPopAttrib();
    }

    public static void drawBorderedGradientRect(float f, float f2, float f3, float f4, int n, int n2, int n3) {
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        float f5;
        float f6;
        float f7;
        float f8;
        Gui.drawVerticalGradient((f5 = f * 2.0F) + 1.0F, (f6 = f2 * 2.0F) + 1.0F, (f7 = f3 * 2.0F) - 1.0F, (f8 = f4 * 2.0F) - 1.0F, n2, n3);
        drawVerticalLine(f5, f6 + 1.0F, f8 - 2.0F, n);
        drawVerticalLine(f7 - 1.0F, f6 + 1.0F, f8 - 2.0F, n);
        drawHorizontalLine(f5 + 2.0F, f7 - 3.0F, f6, n);
        drawHorizontalLine(f5 + 2.0F, f7 - 3.0F, f8 - 1.0F, n);
        drawHorizontalLine(f5 + 1.0F, f5 + 1.0F, f6 + 1.0F, n);
        drawHorizontalLine(f7 - 2.0F, f7 - 2.0F, f6 + 1.0F, n);
        drawHorizontalLine(f7 - 2.0F, f7 - 2.0F, f8 - 2.0F, n);
        drawHorizontalLine(f5 + 1.0F, f5 + 1.0F, f8 - 2.0F, n);
        GL11.glScalef(2.0F, 2.0F, 2.0F);
    }

    public static void drawRoundedBorderLines(float f, float f2, float f3, float f4, float f5, float f6, int n) {
        GL11.glPushAttrib(0);
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        f = (float)(f * 2.0);
        f2 = (float)(f2 * 2.0);
        f3 = (float)(f3 * 2.0);
        f4 = (float)(f4 * 2.0);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        applyColor(n);
        GL11.glEnable(2848);
        GL11.glLineWidth(f6);
        GL11.glBegin(2);

        for (int i = 0; i <= 90; i += 3) {
            GL11.glVertex2d(f + f5 + FastTrig.sin(i * Math.PI / 180.0) * f5 * -1.0, f2 + f5 + FastTrig.cos(i * Math.PI / 180.0) * f5 * -1.0);
        }

        for (int i = 90; i <= 180; i += 3) {
            GL11.glVertex2d(f + f5 + FastTrig.sin(i * Math.PI / 180.0) * f5 * -1.0, f4 - f5 + FastTrig.cos(i * Math.PI / 180.0) * f5 * -1.0);
        }

        for (int i = 0; i <= 90; i += 3) {
            GL11.glVertex2d(f3 - f5 + FastTrig.sin(i * Math.PI / 180.0) * f5, f4 - f5 + FastTrig.cos(i * Math.PI / 180.0) * f5);
        }

        for (int i = 90; i <= 180; i += 3) {
            GL11.glVertex2d(f3 - f5 + FastTrig.sin(i * Math.PI / 180.0) * f5, f2 + f5 + FastTrig.cos(i * Math.PI / 180.0) * f5);
        }

        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glDisable(3042);
        GL11.glEnable(3553);
        GL11.glScalef(2.0F, 2.0F, 2.0F);
        GL11.glPopAttrib();
        GL11.glLineWidth(1.0F);
        applyColor(n);
    }

    public static Color toColor(int n) {
        float f = (n >> 16 & 0xFF) / 255.0F;
        float f2 = (n >> 8 & 0xFF) / 255.0F;
        float f3 = (n & 0xFF) / 255.0F;
        float f4 = (n >> 24 & 0xFF) / 255.0F;
        return new Color(f, f2, f3, f4);
    }

    public static void scissorScaled(int n, int n2, int n3, int n4, float f, int n5) {
        int n6 = n4 - n2;
        int n7 = n3 - n;
        int n8 = n5 - n4;
        GL11.glScissor((int)(n * f), (int)(n8 * f), (int)(n7 * f), (int)(n6 * f));
    }

    public static void drawTexture(ResourceLocation resourceLocation, float f, float f2, float f3, float f4) {
        float f5 = f3 / 2.0F;
        float f6 = 0.0F;
        float f7 = 0.0F;
        GlStateManager.enableBlend();
        Minecraft.getMinecraft().renderEngine.bindTexture(resourceLocation);
        GL11.glBegin(7);
        GL11.glTexCoord2d(f6 / f5, f7 / f5);
        GL11.glVertex2d(f, f2);
        GL11.glTexCoord2d(f6 / f5, (f7 + f5) / f5);
        GL11.glVertex2d(f, f2 + f4);
        GL11.glTexCoord2d((f6 + f5) / f5, (f7 + f5) / f5);
        GL11.glVertex2d(f + f3, f2 + f4);
        GL11.glTexCoord2d((f6 + f5) / f5, f7 / f5);
        GL11.glVertex2d(f + f3, f2);
        GL11.glEnd();
        GlStateManager.disableBlend();
    }

    public static void drawFilledCircle(double d, double d2, double d3) {
        GlStateManager.enableBlend();
        GL11.glDisable(3553);
        GlStateManager.blendFunc(770, 771);
        Tessellator instance = Tessellator.instance;
        instance.startDrawing(6);
        instance.addVertex(d, d2, 0.0);
        double d4 = Math.PI * 2;
        double d5 = d4 / 30.0;

        for (double d6 = -d5; d6 < d4; d6 += d5) {
            instance.addVertex(d + d3 * FastTrig.cos(-d6), d2 + d3 * FastTrig.sin(-d6), 0.0);
        }

        instance.draw();
        GL11.glEnable(3553);
        GlStateManager.disableBlend();
    }

    public static void endColorDraw() {
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void drawRoundedBorder(float f, float f2, float f3, float f4, float f5, float f6, int n) {
        if (f5 == 0.0F) {
            Gui.drawInsetBorder(f - f6 / 2.0F, f2 - f6 / 2.0F, f3 + f6 / 2.0F, f4 + f6 / 2.0F, f6, n);
        } else {
            f5 = Math.min(f5, Math.abs(f4 - f2));
            f5 = Math.min(f5, Math.abs(f3 - f));
            drawRoundedBorderLines(f, f2, f3, f4, f5, f6 * 2.5F, n);
        }
    }

    public static void drawRoundedRectFilled(float f, float f2, float f3, float f4, float f5, int n) {
        GL11.glPushAttrib(0);
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        f = (float)(f * 2.0);
        f2 = (float)(f2 * 2.0);
        f3 = (float)(f3 * 2.0);
        f4 = (float)(f4 * 2.0);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        applyColor(n);
        GL11.glEnable(2848);
        GL11.glBegin(9);

        for (int i = 0; i <= 90; i += 3) {
            GL11.glVertex2d(f + f5 + FastTrig.sin(i * Math.PI / 180.0) * f5 * -1.0, f2 + f5 + FastTrig.cos(i * Math.PI / 180.0) * f5 * -1.0);
        }

        for (int i = 90; i <= 180; i += 3) {
            GL11.glVertex2d(f + f5 + FastTrig.sin(i * Math.PI / 180.0) * f5 * -1.0, f4 - f5 + FastTrig.cos(i * Math.PI / 180.0) * f5 * -1.0);
        }

        for (int i = 0; i <= 90; i += 3) {
            GL11.glVertex2d(f3 - f5 + FastTrig.sin(i * Math.PI / 180.0) * f5, f4 - f5 + FastTrig.cos(i * Math.PI / 180.0) * f5);
        }

        for (int i = 90; i <= 180; i += 3) {
            GL11.glVertex2d(f3 - f5 + FastTrig.sin(i * Math.PI / 180.0) * f5, f2 + f5 + FastTrig.cos(i * Math.PI / 180.0) * f5);
        }

        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glScalef(2.0F, 2.0F, 2.0F);
        GL11.glEnable(3042);
        GL11.glPopAttrib();
        applyColor(n);
    }

    public static void drawTexturedRect(float f, float f2, float f3, float f4, int n, int n2) {
        float f5 = 0.00390625F;
        Tessellator instance = Tessellator.instance;
        instance.startDrawingQuads();
        instance.addVertexWithUV(f, f2 + n2, 0.0, f3 * f5, (f4 + n2) * f5);
        instance.addVertexWithUV(f + n, f2 + n2, 0.0, (f3 + n) * f5, (f4 + n2) * f5);
        instance.addVertexWithUV(f + n, f2, 0.0, (f3 + n) * f5, f4 * f5);
        instance.addVertexWithUV(f, f2, 0.0, f3 * f5, f4 * f5);
        instance.draw();
    }

    public static void drawTriangleDown(float f, float f2, float f3, int n) {
        setColorArgb(n);
        float f4;
        float f5 = (f4 = f - f3 / 2.0F) + f3;
        float f6 = f2 + f3 / 1.5F;
        Tessellator instance = Tessellator.instance;
        instance.startDrawing(4);
        instance.addVertex(f4 + f3 / 2.0F, f6, 0.0);
        instance.addVertex(f5, f2, 0.0);
        instance.addVertex(f4, f2, 0.0);
        instance.draw();
        endColorDraw();
    }

    public static void drawArc(double d, double d2, double d3, double d4, double d5, double d6) {
        GlStateManager.enableBlend();
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        Tessellator instance = Tessellator.instance;

        for (double d7 = d5; d7 < d6; d7 += 0.5) {
            double d8 = d7 * Math.PI / 180.0;
            double d9 = (d7 - 1.0) * Math.PI / 180.0;
            double[] doubles = new double[]{FastTrig.cos(d8) * d3, -FastTrig.sin(d8) * d3, FastTrig.cos(d9) * d3, -FastTrig.sin(d9) * d3};
            double[] doubles2 = new double[]{FastTrig.cos(d8) * d4, -FastTrig.sin(d8) * d4, FastTrig.cos(d9) * d4, -FastTrig.sin(d9) * d4};
            instance.startDrawingQuads();
            instance.addVertex(d + doubles2[0], d2 + doubles2[1], 0.0);
            instance.addVertex(d + doubles2[2], d2 + doubles2[3], 0.0);
            instance.addVertex(d + doubles[2], d2 + doubles[3], 0.0);
            instance.addVertex(d + doubles[0], d2 + doubles[1], 0.0);
            instance.draw();
        }

        GL11.glEnable(3553);
        GlStateManager.disableBlend();
        GL11.glDisable(2848);
        GlStateManager.disableBlend();
        GL11.glEnable(3553);
    }

    public static void drawRoundedRect(float f, float f2, float f3, float f4, float f5, int n) {
        if (f5 == 0.0F) {
            Gui.drawRect(f, f2, f3, f4, n);
        } else {
            f5 = Math.min(f5, Math.abs(f4 - f2));
            f5 = Math.min(f5, Math.abs(f3 - f));
            drawRoundedRectFilled(f, f2, f3, f4, f5, n);
        }
    }

    public static void applyColor(int n) {
        float f = (n >> 24 & 0xFF) / 255.0F;
        float f2 = (n >> 16 & 0xFF) / 255.0F;
        float f3 = (n >> 8 & 0xFF) / 255.0F;
        float f4 = (n & 0xFF) / 255.0F;
        GL11.glColor4f(f2, f3, f4, f);
    }
}
