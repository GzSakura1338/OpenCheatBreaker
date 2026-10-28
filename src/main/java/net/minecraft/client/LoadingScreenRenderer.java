package net.minecraft.client;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MinecraftError;
import org.lwjgl.opengl.GL11;

public class LoadingScreenRenderer implements IProgressUpdate {
    public String text = "";
    public String currentlyDisplayedText = "";
    public long longValue = Minecraft.getSystemTime();
    public Minecraft mc;
    public boolean flag;
    public ScaledResolution window;
    public Framebuffer framebuffer;

    public LoadingScreenRenderer(Minecraft mc) {
        this.mc = mc;
        this.window = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        this.framebuffer = new Framebuffer(mc.displayWidth, mc.displayHeight, false);
        this.framebuffer.setFramebufferFilter(9728);
    }

    @Override
    public void resetProgressAndMessage(String str) {
        this.flag = false;
        this.render(str);
    }

    @Override
    public void displaySavingString(String str) {
        this.flag = true;
        this.render(str);
    }

    public void render(String str) {
        this.currentlyDisplayedText = str;
        if (!this.mc.running) {
            if (!this.flag) {
                throw new MinecraftError();
            }
        } else {
            GL11.glClear(256);
            GL11.glMatrixMode(5889);
            GL11.glLoadIdentity();
            if (OpenGlHelper.isFramebufferEnabled()) {
                int scaleFactor = this.window.getScaleFactor();
                GL11.glOrtho(0.0, this.window.getScaledWidth() * scaleFactor, this.window.getScaledHeight() * scaleFactor, 0.0, 100.0, 300.0);
            } else {
                ScaledResolution scaledResolution = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
                GL11.glOrtho(0.0, scaledResolution.getScaledWidth_double(), scaledResolution.getScaledHeight_double(), 0.0, 100.0, 300.0);
            }

            GL11.glMatrixMode(5888);
            GL11.glLoadIdentity();
            GL11.glTranslatef(0.0F, 0.0F, -200.0F);
        }
    }

    @Override
    public void displayLoadingString(String str) throws java.io.IOException {
        if (!this.mc.running) {
            if (!this.flag) {
                throw new MinecraftError();
            }
        } else {
            this.longValue = 0L;
            this.text = str;
            this.setLoadingProgress(-1);
            this.longValue = 0L;
        }
    }

    @Override
    public void setLoadingProgress(int n) throws java.io.IOException {
        if (!this.mc.running) {
            if (!this.flag) {
                throw new MinecraftError();
            }
        } else {
            long systemTime = Minecraft.getSystemTime();
            if (systemTime - this.longValue >= 100L) {
                this.longValue = systemTime;
                ScaledResolution scaledResolution = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
                int scaleFactor = scaledResolution.getScaleFactor();
                int scaledWidth = scaledResolution.getScaledWidth();
                int scaledHeight = scaledResolution.getScaledHeight();
                if (OpenGlHelper.isFramebufferEnabled()) {
                    this.framebuffer.framebufferClear();
                } else {
                    GL11.glClear(256);
                }

                this.framebuffer.bindFramebuffer(false);
                GL11.glMatrixMode(5889);
                GL11.glLoadIdentity();
                GL11.glOrtho(0.0, scaledResolution.getScaledWidth_double(), scaledResolution.getScaledHeight_double(), 0.0, 100.0, 300.0);
                GL11.glMatrixMode(5888);
                GL11.glLoadIdentity();
                GL11.glTranslatef(0.0F, 0.0F, -200.0F);
                if (!OpenGlHelper.isFramebufferEnabled()) {
                    GL11.glClear(16640);
                }

                Tessellator instance = Tessellator.instance;
                this.mc.getTextureManager().bindTexture(Gui.optionsBackground);
                float f = 32.0F;
                instance.startDrawingQuads();
                instance.setColorOpaque_I(4210752);
                instance.addVertexWithUV(0.0, scaledHeight, 0.0, 0.0, scaledHeight / f);
                instance.addVertexWithUV(scaledWidth, scaledHeight, 0.0, scaledWidth / f, scaledHeight / f);
                instance.addVertexWithUV(scaledWidth, 0.0, 0.0, scaledWidth / f, 0.0);
                instance.addVertexWithUV(0.0, 0.0, 0.0, 0.0, 0.0);
                instance.draw();
                if (n >= 0) {
                    byte b = 100;
                    byte b2 = 2;
                    int n2 = scaledWidth / 2 - b / 2;
                    int n3 = scaledHeight / 2 + 16;
                    GL11.glDisable(3553);
                    instance.startDrawingQuads();
                    instance.setColorOpaque_I(8421504);
                    instance.addVertex(n2, n3, 0.0);
                    instance.addVertex(n2, n3 + b2, 0.0);
                    instance.addVertex(n2 + b, n3 + b2, 0.0);
                    instance.addVertex(n2 + b, n3, 0.0);
                    instance.setColorOpaque_I(8454016);
                    instance.addVertex(n2, n3, 0.0);
                    instance.addVertex(n2, n3 + b2, 0.0);
                    instance.addVertex(n2 + n, n3 + b2, 0.0);
                    instance.addVertex(n2 + n, n3, 0.0);
                    instance.draw();
                    GL11.glEnable(3553);
                }

                GL11.glEnable(3042);
                OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                this.mc
                    .fontRendererObj
                    .drawShadowedString(
                        this.currentlyDisplayedText, (scaledWidth - this.mc.fontRendererObj.getStringWidth(this.currentlyDisplayedText)) / 2, scaledHeight / 2 - 4 - 16, 16777215
                    );
                this.mc.fontRendererObj.drawShadowedString(this.text, (scaledWidth - this.mc.fontRendererObj.getStringWidth(this.text)) / 2, scaledHeight / 2 - 4 + 8, 16777215);
                this.framebuffer.unbindFramebuffer();
                if (OpenGlHelper.isFramebufferEnabled()) {
                    this.framebuffer.framebufferRender(scaledWidth * scaleFactor, scaledHeight * scaleFactor);
                }

                this.mc.resetSize();

                try {
                    Thread.yield();
                } catch (Exception e) {
                }
            }
        }
    }

    @Override
    public void setDoneWorking() {
    }
}
