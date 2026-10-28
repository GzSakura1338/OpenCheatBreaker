package com.cheatbreaker.client.render;

import net.minecraft.client.renderer.OpenGlHelper;
import org.lwjgl.opengl.GL11;

public class GlStateCache {
    public static ColorState colorState = new ColorState();
    public static BlendState blendState = new BlendState();
    public static int shadeModel = 7425;

    public static void enableBlend() {
        blendState.glCapability.enable();
    }

    public static void glPushMatrix() {
        GL11.glPushMatrix();
    }

    public static void setBlendFuncSeparate(int n, int n2, int n3, int n4) {
        if (n != blendState.srcRgb || n2 != blendState.dstRgb || n3 != blendState.srcAlpha || n4 != blendState.dstAlpha) {
            blendState.srcRgb = n;
            blendState.dstRgb = n2;
            blendState.srcAlpha = n3;
            blendState.dstAlpha = n4;
            OpenGlHelper.glBlendFunc(n, n2, n3, n4);
        }
    }

    public static void setShadeModel(int n) {
        if (n != shadeModel) {
            shadeModel = n;
            GL11.glShadeModel(n);
        }
    }

    public static void setColor(float f, float f2, float f3, float f4) {
        if (f != colorState.red || f2 != colorState.green || f3 != colorState.blue || f4 != colorState.alpha) {
            colorState.red = f;
            colorState.green = f2;
            colorState.blue = f3;
            colorState.alpha = f4;
            setColor(f, f2, f3, f4);
        }
    }

    public static void glPopMatrix() {
        GL11.glPopMatrix();
    }

    public static void setBlendFunc(int n, int n2) {
        if (n != blendState.srcRgb || n2 != blendState.dstRgb) {
            blendState.srcRgb = n;
            blendState.dstRgb = n2;
            setBlendFunc(n, n2);
        }
    }

    public static void disableBlend() {
        blendState.glCapability.disable();
    }
}
