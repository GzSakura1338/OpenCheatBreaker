package com.cheatbreaker.client.ui.screen;

import com.cheatbreaker.client.font.CustomFontRenderer;
import com.cheatbreaker.client.render.RenderUtils;
import com.cheatbreaker.client.ui.screen.UIScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import net.optifine.GlStateManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

public class InitializationScreen extends UIScreen {
    public int phaseIndex;
    public String currentPhase;
    public ResourceLocation logo108Png = new ResourceLocation("client/logo_108.png");
    public CustomFontRenderer customFontRenderer = new CustomFontRenderer(new ResourceLocation("client/font/Ubuntu-M.ttf"), 14.0f);
    public Logger logger = LogManager.getLogger((String)"CheatBreaker Initialization");
    public int phaseCount;
    public Framebuffer framebuffer;

    public void advance() throws java.io.IOException {
        ++this.phaseIndex;
        if (this.phaseCount <= this.phaseIndex) {
            this.phaseCount = this.phaseIndex + 1;
        }
        this.drawScreenScaled(0.0f, 0.0f);
    }

    public void beginFrame() {
        try {
            this.framebuffer.bindFramebuffer(false);
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            GL11.glOrtho((double)0.0, (double)this.scaledResolution.getScaledWidth(), (double)this.scaledResolution.getScaledHeight(), (double)0.0, (double)1000.0, (double)3000.0);
            GlStateManager.matrixMode(5888);
            GlStateManager.loadIdentity();
            GL11.glTranslatef((float)0.0f, (float)0.0f, (float)-2000.0f);
            GL11.glDisable((int)2896);
            GL11.glDisable((int)2912);
            GL11.glDisable((int)2929);
            GL11.glEnable((int)3553);
            GL11.glEnable((int)3008);
        }
        catch (RuntimeException runtimeException) {
            // empty catch block
        }
    }

    public void drawLogo(float f, float f2) {
        float f3 = 27.0f;
        float f4 = f / 2.0f - f3;
        float f5 = f2 / 2.0f - f3;
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        RenderUtils.drawSquareTexture(this.logo108Png, f3, f4, f5);
    }

    @Override
    public void mouseClickedScaled(float f, float f2, int n) {
    }

    public InitializationScreen(int n) throws java.io.IOException {
        this.phaseCount = n;
        this.mc = Minecraft.getMinecraft();
        this.scaledResolution = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
        this.framebuffer = new Framebuffer(this.scaledResolution.getScaledWidth() * this.scaledResolution.getScaleFactor(), this.scaledResolution.getScaledHeight() * this.scaledResolution.getScaleFactor(), true);
    }

    public void endFrame() {
        int n = this.scaledResolution.getScaleFactor();
        GL11.glDisable((int)2896);
        GL11.glDisable((int)2912);
        this.framebuffer.unbindFramebuffer();
        this.framebuffer.framebufferRender(this.scaledResolution.getScaledWidth() * n, this.scaledResolution.getScaledHeight() * n);
        GL11.glAlphaFunc((int)516, (float)0.1f);
        GL11.glFlush();
    }

    @Override
    public void mouseReleasedScaled(float f, float f2, int n) {
    }

    public void setPhase(String string) throws java.io.IOException {
        if (this.currentPhase != null) {
            this.logger.info("[PHASE] {} (Completed) -> {}", (Object)this.currentPhase, (Object)string);
        } else {
            this.logger.info("[PHASE] {}", (Object)string);
        }
        this.currentPhase = string;
        this.advance();
    }

    @Override
    public void drawScreenScaled(float f, float f2) throws java.io.IOException {
        this.beginFrame();
        float f3 = this.getUiScale();
        float f4 = (float)this.scaledResolution.getScaledWidth() / f3;
        float f5 = (float)this.scaledResolution.getScaledHeight() / f3;
        GL11.glScaled((double)f3, (double)f3, (double)f3);
        Gui.drawRect(0.0f, 0.0f, f4, f5, -15987700);
        this.drawLogo(f4, f5);
        float f6 = 160.0f;
        float f7 = f4 / 2.0f - 80.0f;
        float f8 = f5 - 40.0f;
        RenderUtils.drawRoundedRectFilledPrecise(f7, f8, f7 + f6, f8 + 10.0f, 8.0, -15395563);
        float f9 = f6 * ((float)this.phaseIndex / (float)this.phaseCount);
        if (this.currentPhase != null) {
            this.customFontRenderer.drawCenteredString(this.currentPhase.toLowerCase(), f4 / 2.0f, f8 - 11.0f, -3092272);
        }
        RenderUtils.drawRoundedRectFilledPrecise(f7, f8, f7 + Math.max(f9, 8.0f), f8 + 10.0f, 8.0, -2473389);
        this.endFrame();
        this.mc.resetSize();
    }
}
