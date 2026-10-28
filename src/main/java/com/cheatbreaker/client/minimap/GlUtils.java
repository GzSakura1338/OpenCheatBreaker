package com.cheatbreaker.client.minimap;

import java.awt.image.BufferedImage;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

public class GlUtils {
    public static IntBuffer imageBuffer = GLAllocation.createDirectIntBuffer(4194304);
    public static int framebufferId = 0;
    public static boolean framebufferSupported = GLContext.getCapabilities().GL_EXT_framebuffer_object && GLContext.getCapabilities().OpenGL14;
    public static int framebufferTexture = 0;
    public static TextureManager textureManager;
    public static boolean stencilSupported = GL11.glGetInteger(3413) > 0;
    public static Tessellator tessellator = Tessellator.instance;
    public static int previousFramebuffer = 0;

    public static void bindTextureId(int n) {
        GL11.glBindTexture(3553, n);
    }

    public static void bindTextureByName(String str) {
        textureManager.bindTexture(new ResourceLocation(str));
    }

    public static void startDrawingQuads() {
        tessellator.startDrawingQuads();
    }

    public static void drawVertex(double d, double d2, double d3) {
        tessellator.addVertex(d, d2, d3);
    }

    public static void drawQuadSized(int n, float f, int n2) {
        int n3 = n2 / 4;
        drawVertexTex(n - n3, f + n3, 1.0, 0.0, 1.0);
        drawVertexTex(n + n3, f + n3, 1.0, 1.0, 1.0);
        drawVertexTex(n + n3, f - n3, 1.0, 1.0, 0.0);
        drawVertexTex(n - n3, f - n3, 1.0, 0.0, 0.0);
    }

    public static void bindFramebuffer() {
        previousFramebuffer = GL11.glGetInteger(36006);
        EXTFramebufferObject.glBindFramebufferEXT(36160, framebufferId);
    }

    public static int uploadTexture(BufferedImage bufferedImage) {
        int glGenTextures = GL11.glGenTextures();
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        int[] ints = new int[width * height];
        bufferedImage.getRGB(0, 0, width, height, ints, 0, width);
        GL11.glBindTexture(3553, glGenTextures);
        ((Buffer)imageBuffer).clear();
        imageBuffer.put(ints, 0, width * height);
        ((Buffer)imageBuffer).position(0).limit(width * height);
        GL11.glTexImage2D(3553, 0, 6408, width, height, 0, 32993, 33639, imageBuffer);
        return glGenTextures;
    }

    public static void drawVertexTex(double d, double d2, double d3, double d4, double d5) {
        tessellator.addVertexWithUV(d, d2, d3, d4, d5);
    }

    public static void deleteTexture(int n) {
        GL11.glDeleteTextures(n);
    }

    public static void drawQuad(int n, int n2) {
        drawQuadSized(n, n2, 128);
    }

    public static void setupFramebuffer() {
        previousFramebuffer = GL11.glGetInteger(36006);
        framebufferId = EXTFramebufferObject.glGenFramebuffersEXT();
        framebufferTexture = GL11.glGenTextures();
        short s = 256;
        short s2 = 256;
        EXTFramebufferObject.glBindFramebufferEXT(36160, framebufferId);
        ByteBuffer createByteBuffer = BufferUtils.createByteBuffer(4 * s * s2);
        GL11.glBindTexture(3553, framebufferTexture);
        GL11.glTexParameteri(3553, 10242, 10496);
        GL11.glTexParameteri(3553, 10243, 10496);
        GL11.glTexParameteri(3553, 10241, 9729);
        GL11.glTexParameteri(3553, 10240, 9729);
        GL11.glTexImage2D(3553, 0, 6408, s, s2, 0, 6408, 5120, createByteBuffer);
        EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064, 3553, framebufferTexture, 0);
        int glGenRenderbuffersEXT = EXTFramebufferObject.glGenRenderbuffersEXT();
        EXTFramebufferObject.glBindRenderbufferEXT(36161, glGenRenderbuffersEXT);
        EXTFramebufferObject.glRenderbufferStorageEXT(36161, 33190, 256, 256);
        EXTFramebufferObject.glFramebufferRenderbufferEXT(36160, 36096, 36161, glGenRenderbuffersEXT);
        EXTFramebufferObject.glBindRenderbufferEXT(36161, 0);
        EXTFramebufferObject.glBindFramebufferEXT(36160, previousFramebuffer);
    }

    public static void bindTextureLocation(ResourceLocation resourceLocation) {
        textureManager.bindTexture(resourceLocation);
    }

    public static void drawVertexIntTex(int n, int n2, double d, double d2, double d3) {
        tessellator.addVertexWithUV(n, n2, d, d2, d3);
    }

    public static void startDrawing() {
        tessellator.draw();
    }

    public static void unbindFramebuffer() {
        EXTFramebufferObject.glBindFramebufferEXT(36160, previousFramebuffer);
    }
}
