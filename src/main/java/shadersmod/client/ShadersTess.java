package shadersmod.client;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import shadersmod.common.SMCLog;

public class ShadersTess {
    public static final int vertexStride = 18;
    public float midTextureU;
    public float midTextureV;
    public float normalX;
    public float normalY;
    public float normalZ;
    public float v0x;
    public float v0y;
    public float v0z;
    public float v0u;
    public float v0v;
    public float v1x;
    public float v1y;
    public float v1z;
    public float v1u;
    public float v1v;
    public float v2x;
    public float v2y;
    public float v2z;
    public float v2u;
    public float v2v;
    public float v3x;
    public float v3y;
    public float v3z;
    public float v3u;
    public float v3v;

    public static int draw(Tessellator tessellator) {
        if (!tessellator.isDrawing) {
            throw new IllegalStateException("Not tesselating!");
        }

        tessellator.isDrawing = false;
        if (tessellator.drawMode == 7 && tessellator.vertexCount % 4 != 0) {
            SMCLog.warning("%s", "bad vertexCount");
        }

        int n = 0;
        int drawMode = tessellator.drawMode;

        while (n < tessellator.vertexCount) {
            int min = Math.min(tessellator.vertexCount - n, tessellator.byteBuffer.capacity() / 72);
            if (drawMode == 7) {
                min = min / 4 * 4;
            }

            ((Buffer)tessellator.floatBuffer).clear();
            ((Buffer)tessellator.shortBuffer).clear();
            ((Buffer)tessellator.intBuffer).clear();
            tessellator.intBuffer.put(tessellator.rawBuffer, n * 18, min * 18);
            ((Buffer)tessellator.byteBuffer).position(0);
            ((Buffer)tessellator.byteBuffer).limit(min * 72);
            n += min;
            if (tessellator.hasTexture) {
                ((Buffer)tessellator.floatBuffer).position(3);
                GL11.glTexCoordPointer(2, 72, tessellator.floatBuffer);
                GL11.glEnableClientState(32888);
            }

            if (tessellator.hasBrightness) {
                OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
                ((Buffer)tessellator.shortBuffer).position(12);
                GL11.glTexCoordPointer(2, 72, tessellator.shortBuffer);
                GL11.glEnableClientState(32888);
                OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
            }

            if (tessellator.hasColor) {
                ((Buffer)tessellator.byteBuffer).position(20);
                GL11.glColorPointer(4, true, 72, tessellator.byteBuffer);
                GL11.glEnableClientState(32886);
            }

            if (tessellator.hasNormals) {
                ((Buffer)tessellator.floatBuffer).position(9);
                GL11.glNormalPointer(72, tessellator.floatBuffer);
                GL11.glEnableClientState(32885);
            }

            ((Buffer)tessellator.floatBuffer).position(0);
            GL11.glVertexPointer(3, 72, tessellator.floatBuffer);
            preDrawArray(tessellator);
            GL11.glEnableClientState(32884);
            GL11.glDrawArrays(drawMode, 0, min);
        }

        GL11.glDisableClientState(32884);
        postDrawArray(tessellator);
        if (tessellator.hasTexture) {
            GL11.glDisableClientState(32888);
        }

        if (tessellator.hasBrightness) {
            OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
            GL11.glDisableClientState(32888);
            OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
        }

        if (tessellator.hasColor) {
            GL11.glDisableClientState(32886);
        }

        if (tessellator.hasNormals) {
            GL11.glDisableClientState(32885);
        }

        int n2 = tessellator.rawBufferIndex * 4;
        tessellator.reset();
        return n2;
    }

    public static void preDrawArray(Tessellator tessellator) {
        if (Shaders.useMultiTexCoord3Attrib && tessellator.hasTexture) {
            GL13.glClientActiveTexture(33987);
            GL11.glTexCoordPointer(2, 72, (FloatBuffer)((Buffer)tessellator.floatBuffer).position(16));
            GL11.glEnableClientState(32888);
            GL13.glClientActiveTexture(33984);
        }

        if (Shaders.useMidTexCoordAttrib && tessellator.hasTexture) {
            ARBVertexShader.glVertexAttribPointerARB(Shaders.midTexCoordAttrib, 2, false, 72, (FloatBuffer)((Buffer)tessellator.floatBuffer).position(16));
            ARBVertexShader.glEnableVertexAttribArrayARB(Shaders.midTexCoordAttrib);
        }

        if (Shaders.useTangentAttrib && tessellator.hasTexture) {
            ARBVertexShader.glVertexAttribPointerARB(Shaders.tangentAttrib, 4, false, 72, (FloatBuffer)((Buffer)tessellator.floatBuffer).position(12));
            ARBVertexShader.glEnableVertexAttribArrayARB(Shaders.tangentAttrib);
        }

        if (Shaders.useEntityAttrib) {
            ARBVertexShader.glVertexAttribPointerARB(Shaders.entityAttrib, 3, false, false, 72, (ShortBuffer)((Buffer)tessellator.shortBuffer).position(14));
            ARBVertexShader.glEnableVertexAttribArrayARB(Shaders.entityAttrib);
        }
    }

    public static void preDrawArrayVBO(Tessellator tessellator) {
        if (Shaders.useMultiTexCoord3Attrib && tessellator.hasTexture) {
            GL13.glClientActiveTexture(33987);
            GL11.glTexCoordPointer(2, 5126, 72, 64L);
            GL11.glEnableClientState(32888);
            GL13.glClientActiveTexture(33984);
        }

        if (Shaders.useMidTexCoordAttrib && tessellator.hasTexture) {
            ARBVertexShader.glVertexAttribPointerARB(Shaders.midTexCoordAttrib, 2, 5126, false, 72, 64L);
            ARBVertexShader.glEnableVertexAttribArrayARB(Shaders.midTexCoordAttrib);
        }

        if (Shaders.useTangentAttrib && tessellator.hasTexture) {
            ARBVertexShader.glVertexAttribPointerARB(Shaders.tangentAttrib, 4, 5126, false, 72, 48L);
            ARBVertexShader.glEnableVertexAttribArrayARB(Shaders.tangentAttrib);
        }

        if (Shaders.useEntityAttrib) {
            ARBVertexShader.glVertexAttribPointerARB(Shaders.entityAttrib, 3, 5122, false, 72, 28L);
            ARBVertexShader.glEnableVertexAttribArrayARB(Shaders.entityAttrib);
        }
    }

    public static void postDrawArray(Tessellator tessellator) {
        if (Shaders.useEntityAttrib) {
            ARBVertexShader.glDisableVertexAttribArrayARB(Shaders.entityAttrib);
        }

        if (Shaders.useMidTexCoordAttrib && tessellator.hasTexture) {
            ARBVertexShader.glDisableVertexAttribArrayARB(Shaders.midTexCoordAttrib);
        }

        if (Shaders.useTangentAttrib && tessellator.hasTexture) {
            ARBVertexShader.glDisableVertexAttribArrayARB(Shaders.tangentAttrib);
        }

        if (Shaders.useMultiTexCoord3Attrib && tessellator.hasTexture) {
            GL13.glClientActiveTexture(33987);
            GL11.glDisableClientState(32888);
            GL13.glClientActiveTexture(33984);
        }
    }

    public static void addVertex(Tessellator tessellator, double d, double d2, double d3) {
        ShadersTess shadersTess = tessellator.shadersTess;
        int[] rawBuffer = tessellator.rawBuffer;
        int rawBufferIndex = tessellator.rawBufferIndex;
        float f = (float)(d + tessellator.xOffset);
        float f2 = (float)(d2 + tessellator.yOffset);
        float f3 = (float)(d3 + tessellator.zOffset);
        if (rawBufferIndex >= tessellator.bufferSize - 72) {
            if (tessellator.bufferSize >= 16777216) {
                if (tessellator.addedVertices % 4 == 0) {
                    tessellator.draw();
                    tessellator.isDrawing = true;
                }
            } else if (tessellator.bufferSize > 0) {
                tessellator.bufferSize *= 2;
                tessellator.rawBuffer = rawBuffer = Arrays.copyOf(tessellator.rawBuffer, tessellator.bufferSize);
                SMCLog.info("Expand tesselator buffer %d", tessellator.bufferSize);
            } else {
                tessellator.bufferSize = 65536;
                tessellator.rawBuffer = rawBuffer = new int[tessellator.bufferSize];
            }
        }

        if (tessellator.drawMode == 7) {
            int n = tessellator.addedVertices % 4;
            switch (n) {
                case 0:
                    shadersTess.v0x = f;
                    shadersTess.v0y = f2;
                    shadersTess.v0z = f3;
                    shadersTess.v0u = (float)tessellator.textureU;
                    shadersTess.v0v = (float)tessellator.textureV;
                    break;
                case 1:
                    shadersTess.v1x = f;
                    shadersTess.v1y = f2;
                    shadersTess.v1z = f3;
                    shadersTess.v1u = (float)tessellator.textureU;
                    shadersTess.v1v = (float)tessellator.textureV;
                    break;
                case 2:
                    shadersTess.v2x = f;
                    shadersTess.v2y = f2;
                    shadersTess.v2z = f3;
                    shadersTess.v2u = (float)tessellator.textureU;
                    shadersTess.v2v = (float)tessellator.textureV;
                    break;
                case 3:
                    shadersTess.v3x = f;
                    shadersTess.v3y = f2;
                    shadersTess.v3z = f3;
                    shadersTess.v3u = (float)tessellator.textureU;
                    shadersTess.v3v = (float)tessellator.textureV;
                    float f4 = shadersTess.v2x - shadersTess.v0x;
                    float f5 = shadersTess.v2y - shadersTess.v0y;
                    float f6 = shadersTess.v2z - shadersTess.v0z;
                    float f7 = shadersTess.v3x - shadersTess.v1x;
                    float f8 = shadersTess.v3y - shadersTess.v1y;
                    float f9 = shadersTess.v3z - shadersTess.v1z;
                    float f10 = f5 * f9 - f8 * f6;
                    float f11 = f6 * f7 - f9 * f4;
                    float f12 = f4 * f8 - f7 * f5;
                    float f13 = f10 * f10 + f11 * f11 + f12 * f12;
                    float f14 = f13 != 0.0 ? (float)(1.0 / Math.sqrt(f13)) : 1.0F;
                    f10 *= f14;
                    f11 *= f14;
                    f12 *= f14;
                    tessellator.hasNormals = true;
                    f4 = shadersTess.v1x - shadersTess.v0x;
                    f5 = shadersTess.v1y - shadersTess.v0y;
                    f6 = shadersTess.v1z - shadersTess.v0z;
                    float f15 = shadersTess.v1u - shadersTess.v0u;
                    float f16 = shadersTess.v1v - shadersTess.v0v;
                    f7 = shadersTess.v2x - shadersTess.v0x;
                    f8 = shadersTess.v2y - shadersTess.v0y;
                    f9 = shadersTess.v2z - shadersTess.v0z;
                    float f17 = shadersTess.v2u - shadersTess.v0u;
                    float f18 = shadersTess.v2v - shadersTess.v0v;
                    float f19 = f15 * f18 - f17 * f16;
                    float f20 = f19 != 0.0F ? 1.0F / f19 : 1.0F;
                    float f21 = (f18 * f4 - f16 * f7) * f20;
                    float f22 = (f18 * f5 - f16 * f8) * f20;
                    float f23 = (f18 * f6 - f16 * f9) * f20;
                    float f24 = (f15 * f7 - f17 * f4) * f20;
                    float f25 = (f15 * f8 - f17 * f5) * f20;
                    float f26 = (f15 * f9 - f17 * f6) * f20;
                    f13 = f21 * f21 + f22 * f22 + f23 * f23;
                    f14 = f13 != 0.0 ? (float)(1.0 / Math.sqrt(f13)) : 1.0F;
                    f21 *= f14;
                    f22 *= f14;
                    f23 *= f14;
                    f13 = f24 * f24 + f25 * f25 + f26 * f26;
                    f14 = f13 != 0.0 ? (float)(1.0 / Math.sqrt(f13)) : 1.0F;
                    f24 *= f14;
                    f25 *= f14;
                    f26 *= f14;
                    float f27 = f12 * f22 - f11 * f23;
                    float f28 = f10 * f23 - f12 * f21;
                    float f29 = f11 * f21 - f10 * f22;
                    float f30 = f24 * f27 + f25 * f28 + f26 * f29 < 0.0F ? -1.0F : 1.0F;
                    rawBuffer[rawBufferIndex + -45] = rawBuffer[rawBufferIndex + -27] = rawBuffer[rawBufferIndex + -9] = rawBuffer[rawBufferIndex + 9] = Float.floatToRawIntBits(shadersTess.normalX = f10);
                    rawBuffer[rawBufferIndex + -44] = rawBuffer[rawBufferIndex + -26] = rawBuffer[rawBufferIndex + -8] = rawBuffer[rawBufferIndex + 10] = Float.floatToRawIntBits(shadersTess.normalY = f11);
                    rawBuffer[rawBufferIndex + -43] = rawBuffer[rawBufferIndex + -25] = rawBuffer[rawBufferIndex + -7] = rawBuffer[rawBufferIndex + 11] = Float.floatToRawIntBits(shadersTess.normalZ = f12);
                    rawBuffer[rawBufferIndex + -42] = rawBuffer[rawBufferIndex + -24] = rawBuffer[rawBufferIndex + -6] = rawBuffer[rawBufferIndex + 12] = Float.floatToRawIntBits(f21);
                    rawBuffer[rawBufferIndex + -41] = rawBuffer[rawBufferIndex + -23] = rawBuffer[rawBufferIndex + -5] = rawBuffer[rawBufferIndex + 13] = Float.floatToRawIntBits(f22);
                    rawBuffer[rawBufferIndex + -40] = rawBuffer[rawBufferIndex + -22] = rawBuffer[rawBufferIndex + -4] = rawBuffer[rawBufferIndex + 14] = Float.floatToRawIntBits(f23);
                    rawBuffer[rawBufferIndex + -39] = rawBuffer[rawBufferIndex + -21] = rawBuffer[rawBufferIndex + -3] = rawBuffer[rawBufferIndex + 15] = Float.floatToRawIntBits(f30);
                    shadersTess.midTextureU = (
                            Float.intBitsToFloat(rawBuffer[rawBufferIndex + -51])
                                + Float.intBitsToFloat(rawBuffer[rawBufferIndex + -33])
                                + Float.intBitsToFloat(rawBuffer[rawBufferIndex + -15])
                                + (float)tessellator.textureU
                        )
                        / 4.0F;
                    shadersTess.midTextureV = (
                            Float.intBitsToFloat(rawBuffer[rawBufferIndex + -50])
                                + Float.intBitsToFloat(rawBuffer[rawBufferIndex + -32])
                                + Float.intBitsToFloat(rawBuffer[rawBufferIndex + -14])
                                + (float)tessellator.textureV
                        )
                        / 4.0F;
                    rawBuffer[rawBufferIndex + -38] = rawBuffer[rawBufferIndex + -20] = rawBuffer[rawBufferIndex + -2] = rawBuffer[rawBufferIndex + 16] = Float.floatToRawIntBits(shadersTess.midTextureU);
                    rawBuffer[rawBufferIndex + -37] = rawBuffer[rawBufferIndex + -19] = rawBuffer[rawBufferIndex + -1] = rawBuffer[rawBufferIndex + 17] = Float.floatToRawIntBits(shadersTess.midTextureV);
            }
        } else if (tessellator.drawMode == 4) {
            int n = tessellator.addedVertices % 3;
            switch (n) {
                case 0:
                    shadersTess.v0x = f;
                    shadersTess.v0y = f2;
                    shadersTess.v0z = f3;
                    shadersTess.v0u = (float)tessellator.textureU;
                    shadersTess.v0v = (float)tessellator.textureV;
                    break;
                case 1:
                    shadersTess.v1x = f;
                    shadersTess.v1y = f2;
                    shadersTess.v1z = f3;
                    shadersTess.v1u = (float)tessellator.textureU;
                    shadersTess.v1v = (float)tessellator.textureV;
                    break;
                case 2:
                    shadersTess.v2x = f;
                    shadersTess.v2y = f2;
                    shadersTess.v2z = f3;
                    shadersTess.v2u = (float)tessellator.textureU;
                    shadersTess.v2v = (float)tessellator.textureV;
                    float f4 = shadersTess.v1x - shadersTess.v0x;
                    float f5 = shadersTess.v1y - shadersTess.v0y;
                    float f6 = shadersTess.v1z - shadersTess.v0z;
                    float f7 = shadersTess.v2x - shadersTess.v1x;
                    float f8 = shadersTess.v2y - shadersTess.v1y;
                    float f9 = shadersTess.v2z - shadersTess.v1z;
                    float f10 = f5 * f9 - f8 * f6;
                    float f11 = f6 * f7 - f9 * f4;
                    float f12 = f4 * f8 - f7 * f5;
                    float f13 = f10 * f10 + f11 * f11 + f12 * f12;
                    float f14 = f13 != 0.0 ? (float)(1.0 / Math.sqrt(f13)) : 1.0F;
                    f10 *= f14;
                    f11 *= f14;
                    f12 *= f14;
                    tessellator.hasNormals = true;
                    f4 = shadersTess.v1x - shadersTess.v0x;
                    f5 = shadersTess.v1y - shadersTess.v0y;
                    f6 = shadersTess.v1z - shadersTess.v0z;
                    float f15 = shadersTess.v1u - shadersTess.v0u;
                    float f16 = shadersTess.v1v - shadersTess.v0v;
                    f7 = shadersTess.v2x - shadersTess.v0x;
                    f8 = shadersTess.v2y - shadersTess.v0y;
                    f9 = shadersTess.v2z - shadersTess.v0z;
                    float f17 = shadersTess.v2u - shadersTess.v0u;
                    float f18 = shadersTess.v2v - shadersTess.v0v;
                    float f19 = f15 * f18 - f17 * f16;
                    float f20 = f19 != 0.0F ? 1.0F / f19 : 1.0F;
                    float f21 = (f18 * f4 - f16 * f7) * f20;
                    float f22 = (f18 * f5 - f16 * f8) * f20;
                    float f23 = (f18 * f6 - f16 * f9) * f20;
                    float f24 = (f15 * f7 - f17 * f4) * f20;
                    float f25 = (f15 * f8 - f17 * f5) * f20;
                    float f26 = (f15 * f9 - f17 * f6) * f20;
                    f13 = f21 * f21 + f22 * f22 + f23 * f23;
                    f14 = f13 != 0.0 ? (float)(1.0 / Math.sqrt(f13)) : 1.0F;
                    f21 *= f14;
                    f22 *= f14;
                    f23 *= f14;
                    f13 = f24 * f24 + f25 * f25 + f26 * f26;
                    f14 = f13 != 0.0 ? (float)(1.0 / Math.sqrt(f13)) : 1.0F;
                    f24 *= f14;
                    f25 *= f14;
                    f26 *= f14;
                    float f27 = f12 * f22 - f11 * f23;
                    float f28 = f10 * f23 - f12 * f21;
                    float f29 = f11 * f21 - f10 * f22;
                    float f30 = f24 * f27 + f25 * f28 + f26 * f29 < 0.0F ? -1.0F : 1.0F;
                    rawBuffer[rawBufferIndex + -27] = rawBuffer[rawBufferIndex + -9] = rawBuffer[rawBufferIndex + 9] = Float.floatToRawIntBits(shadersTess.normalX = f10);
                    rawBuffer[rawBufferIndex + -26] = rawBuffer[rawBufferIndex + -8] = rawBuffer[rawBufferIndex + 10] = Float.floatToRawIntBits(shadersTess.normalY = f11);
                    rawBuffer[rawBufferIndex + -25] = rawBuffer[rawBufferIndex + -7] = rawBuffer[rawBufferIndex + 11] = Float.floatToRawIntBits(shadersTess.normalZ = f12);
                    rawBuffer[rawBufferIndex + -24] = rawBuffer[rawBufferIndex + -6] = rawBuffer[rawBufferIndex + 12] = Float.floatToRawIntBits(f21);
                    rawBuffer[rawBufferIndex + -23] = rawBuffer[rawBufferIndex + -5] = rawBuffer[rawBufferIndex + 13] = Float.floatToRawIntBits(f22);
                    rawBuffer[rawBufferIndex + -22] = rawBuffer[rawBufferIndex + -4] = rawBuffer[rawBufferIndex + 14] = Float.floatToRawIntBits(f23);
                    rawBuffer[rawBufferIndex + -21] = rawBuffer[rawBufferIndex + -3] = rawBuffer[rawBufferIndex + 15] = Float.floatToRawIntBits(f30);
                    shadersTess.midTextureU = (Float.intBitsToFloat(rawBuffer[rawBufferIndex + -33]) + Float.intBitsToFloat(rawBuffer[rawBufferIndex + -15]) + (float)tessellator.textureU) / 3.0F;
                    shadersTess.midTextureV = (Float.intBitsToFloat(rawBuffer[rawBufferIndex + -32]) + Float.intBitsToFloat(rawBuffer[rawBufferIndex + -14]) + (float)tessellator.textureV) / 3.0F;
                    rawBuffer[rawBufferIndex + -20] = rawBuffer[rawBufferIndex + -2] = rawBuffer[rawBufferIndex + 16] = Float.floatToRawIntBits(shadersTess.midTextureU);
                    rawBuffer[rawBufferIndex + -19] = rawBuffer[rawBufferIndex + -1] = rawBuffer[rawBufferIndex + 17] = Float.floatToRawIntBits(shadersTess.midTextureV);
            }
        }

        tessellator.addedVertices++;
        rawBuffer[rawBufferIndex + 0] = Float.floatToRawIntBits(f);
        rawBuffer[rawBufferIndex + 1] = Float.floatToRawIntBits(f2);
        rawBuffer[rawBufferIndex + 2] = Float.floatToRawIntBits(f3);
        rawBuffer[rawBufferIndex + 3] = Float.floatToRawIntBits((float)tessellator.textureU);
        rawBuffer[rawBufferIndex + 4] = Float.floatToRawIntBits((float)tessellator.textureV);
        rawBuffer[rawBufferIndex + 5] = tessellator.color;
        rawBuffer[rawBufferIndex + 6] = tessellator.brightness;
        rawBuffer[rawBufferIndex + 7] = Shaders.getEntityData();
        rawBuffer[rawBufferIndex + 8] = Shaders.getEntityData2();
        rawBuffer[rawBufferIndex + 9] = Float.floatToRawIntBits(shadersTess.normalX);
        rawBuffer[rawBufferIndex + 10] = Float.floatToRawIntBits(shadersTess.normalY);
        rawBuffer[rawBufferIndex + 11] = Float.floatToRawIntBits(shadersTess.normalZ);
        rawBufferIndex += 18;
        tessellator.rawBufferIndex = rawBufferIndex;
        tessellator.vertexCount++;
    }
}
