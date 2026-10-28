package net.optifine;

import org.lwjgl.opengl.GL11;

public class Blender {
    public static final int BLEND_ALPHA = 0;
    public static final int BLEND_SUBSTRACT = 2;
    public static final int BLEND_MULTIPLY = 3;
    public static final int BLEND_DODGE = 4;
    public static final int BLEND_BURN = 5;
    public static final int BLEND_SCREEN = 6;
    public static final int BLEND_OVERLAY = 7;
    public static final int BLEND_REPLACE = 8;
    public static final int CONSTANT_1 = 1;
    public static final int CONSTANT_1_2 = 1;

    public static int parseBlend(String str) {
        if (str == null) {
            return 1;
        }

        str = str.toLowerCase().trim();
        if (str.equals("alpha")) {
            return 0;
        }

        if (str.equals("add")) {
            return 1;
        }

        if (str.equals("subtract")) {
            return 2;
        }

        if (str.equals("multiply")) {
            return 3;
        }

        if (str.equals("dodge")) {
            return 4;
        }

        if (str.equals("burn")) {
            return 5;
        }

        if (str.equals("screen")) {
            return 6;
        }

        if (str.equals("overlay")) {
            return 7;
        }

        if (str.equals("replace")) {
            return 8;
        }

        Config.warn("Unknown blend: " + str);
        return 1;
    }

    public static void setupBlend(int n, float f) {
        switch (n) {
            case 0:
                GL11.glDisable(3008);
                GL11.glEnable(3042);
                GL11.glBlendFunc(770, 771);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, f);
                break;
            case 1:
                GL11.glDisable(3008);
                GL11.glEnable(3042);
                GL11.glBlendFunc(770, 1);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, f);
                break;
            case 2:
                GL11.glDisable(3008);
                GL11.glEnable(3042);
                GL11.glBlendFunc(775, 0);
                GL11.glColor4f(f, f, f, 1.0F);
                break;
            case 3:
                GL11.glDisable(3008);
                GL11.glEnable(3042);
                GL11.glBlendFunc(774, 771);
                GL11.glColor4f(f, f, f, f);
                break;
            case 4:
                GL11.glDisable(3008);
                GL11.glEnable(3042);
                GL11.glBlendFunc(1, 1);
                GL11.glColor4f(f, f, f, 1.0F);
                break;
            case 5:
                GL11.glDisable(3008);
                GL11.glEnable(3042);
                GL11.glBlendFunc(0, 769);
                GL11.glColor4f(f, f, f, 1.0F);
                break;
            case 6:
                GL11.glDisable(3008);
                GL11.glEnable(3042);
                GL11.glBlendFunc(1, 769);
                GL11.glColor4f(f, f, f, 1.0F);
                break;
            case 7:
                GL11.glDisable(3008);
                GL11.glEnable(3042);
                GL11.glBlendFunc(774, 768);
                GL11.glColor4f(f, f, f, 1.0F);
                break;
            case 8:
                GL11.glEnable(3008);
                GL11.glDisable(3042);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, f);
        }

        GL11.glEnable(3553);
    }

    public static void clearBlend(float f) {
        GL11.glDisable(3008);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 1);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, f);
    }
}
