package net.optifine;

import java.nio.Buffer;
import java.nio.IntBuffer;
import net.minecraft.client.renderer.OpenGlHelper;
import org.lwjgl.opengl.GL11;

public class GlStateManager {
    public static int activeTextureUnit = 0;
    public static int GL_FRAMEBUFFER = OpenGlHelper.framebuffer;
    public static int GL_RENDERBUFFER = OpenGlHelper.renderbuffer;
    public static int GL_COLOR_ATTACHMENT0 = OpenGlHelper.colorAttachment;
    public static int GL_DEPTH_ATTACHMENT = OpenGlHelper.depthAttachment;

    public static int getActiveTextureUnit() {
        return activeTextureUnit;
    }

    public static void bindTexture(int n) {
        GL11.glBindTexture(3553, n);
    }

    public static void deleteTexture(int n) {
        if (n != 0) {
            GL11.glDeleteTextures(n);
        }
    }

    public static void deleteTextures(IntBuffer intBuffer) {
        ((Buffer)intBuffer).rewind();

        while (intBuffer.position() < intBuffer.limit()) {
            int n = intBuffer.get();
            deleteTexture(n);
        }

        ((Buffer)intBuffer).rewind();
    }

    public static void setActiveTexture(int n) {
        OpenGlHelper.setActiveTexture(n);
    }

    public static void colorMask(boolean flag, boolean flag2, boolean flag3, boolean flag4) {
        GL11.glColorMask(flag, flag2, flag3, flag4);
    }

    public static void enableCull() {
        GL11.glEnable(2884);
    }

    public static void disableCull() {
        GL11.glDisable(2884);
    }

    public static void enableDepth() {
        GL11.glEnable(2929);
    }

    public static void disableDepth() {
        GL11.glDisable(2929);
    }

    public static void enableTexture2D() {
        GL11.glEnable(3553);
    }

    public static void disableTexture2D() {
        GL11.glDisable(3553);
    }

    public static void enableAlpha() {
        GL11.glEnable(3008);
    }

    public static void disableAlpha() {
        GL11.glDisable(3008);
    }

    public static void enableBlend() {
        GL11.glEnable(3042);
    }

    public static void disableBlend() {
        GL11.glDisable(3042);
    }

    public static void depthFunc(int n) {
        GL11.glDepthFunc(n);
    }

    public static void depthMask(boolean flag) {
        GL11.glDepthMask(flag);
    }

    public static void enableLighting() {
        GL11.glEnable(2896);
    }

    public static void disableLighting() {
        GL11.glDisable(2896);
    }

    public static void color(float f, float f2, float f3, float f4) {
        GL11.glColor4f(f, f2, f3, f4);
    }

    public static void color(float f, float f2, float f3) {
        color(f, f2, f3, 1.0F);
    }

    public static void clearColor(float f, float f2, float f3, float f4) {
        GL11.glClearColor(f, f2, f3, f4);
    }

    public static void alphaFunc(int n, float f) {
        GL11.glAlphaFunc(n, f);
    }

    public static void blendFunc(int n, int n2) {
        GL11.glBlendFunc(n, n2);
    }

    public static void setFog(int n) {
        GL11.glFogi(2917, n);
    }

    public static void setFogDensity(float f) {
        GL11.glFogf(2914, f);
    }

    public static void setFogStart(float f) {
        GL11.glFogf(2915, f);
    }

    public static void setFogEnd(float f) {
        GL11.glFogf(2916, f);
    }

    public static void shadeModel(int n) {
        GL11.glShadeModel(n);
    }

    public static void clear(int n) {
        GL11.glClear(n);
    }

    public static void matrixMode(int n) {
        GL11.glMatrixMode(n);
    }

    public static void loadIdentity() {
        GL11.glLoadIdentity();
    }

    public static void pushMatrix() {
        GL11.glPushMatrix();
    }

    public static void popMatrix() {
        GL11.glPopMatrix();
    }

    public static void tryBlendFuncSeparate(int n, int n2, int n3, int n4) {
        OpenGlHelper.glBlendFunc(n, n2, n3, n4);
    }

    public static void rotate(float f, float f2, float f3, float f4) {
        GL11.glRotatef(f, f2, f3, f4);
    }
}
