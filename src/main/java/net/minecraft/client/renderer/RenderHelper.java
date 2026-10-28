package net.minecraft.client.renderer;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

public class RenderHelper {
    public static FloatBuffer colorBuffer = GLAllocation.createDirectFloatBuffer(16);
    public static Vec3 vec3 = Vec3.createVectorHelper(0.2F, 1.0, -0.7F).normalize();
    public static Vec3 vec3_2 = Vec3.createVectorHelper(-0.2F, 1.0, 0.7F).normalize();

    public static void disableStandardItemLighting() {
        GL11.glDisable(2896);
        GL11.glDisable(16384);
        GL11.glDisable(16385);
        GL11.glDisable(2903);
    }

    public static void enableStandardItemLighting() {
        GL11.glEnable(2896);
        GL11.glEnable(16384);
        GL11.glEnable(16385);
        GL11.glEnable(2903);
        GL11.glColorMaterial(1032, 5634);
        float f = 0.4F;
        float f2 = 0.6F;
        float f3 = 0.0F;
        GL11.glLight(16384, 4611, setColorBuffer(vec3.xCoord, vec3.yCoord, vec3.zCoord, 0.0));
        GL11.glLight(16384, 4609, setColorBuffer(f2, f2, f2, 1.0F));
        GL11.glLight(16384, 4608, setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
        GL11.glLight(16384, 4610, setColorBuffer(f3, f3, f3, 1.0F));
        GL11.glLight(16385, 4611, setColorBuffer(vec3_2.xCoord, vec3_2.yCoord, vec3_2.zCoord, 0.0));
        GL11.glLight(16385, 4609, setColorBuffer(f2, f2, f2, 1.0F));
        GL11.glLight(16385, 4608, setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
        GL11.glLight(16385, 4610, setColorBuffer(f3, f3, f3, 1.0F));
        GL11.glShadeModel(7424);
        GL11.glLightModel(2899, setColorBuffer(f, f, f, 1.0F));
    }

    public static FloatBuffer setColorBuffer(double d, double d2, double d3, double d4) {
        return setColorBuffer((float)d, (float)d2, (float)d3, (float)d4);
    }

    public static FloatBuffer setColorBuffer(float f, float f2, float f3, float f4) {
        ((Buffer)colorBuffer).clear();
        colorBuffer.put(f).put(f2).put(f3).put(f4);
        ((Buffer)colorBuffer).flip();
        return colorBuffer;
    }

    public static void enableGUIStandardItemLighting() {
        GL11.glPushMatrix();
        GL11.glRotatef(-30.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(165.0F, 1.0F, 0.0F, 0.0F);
        enableStandardItemLighting();
        GL11.glPopMatrix();
    }
}
