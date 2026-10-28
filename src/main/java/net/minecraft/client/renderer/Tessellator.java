package net.minecraft.client.renderer;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.TessellatorColorOpaqueEvent;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Optional;
import java.util.PriorityQueue;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.shader.TesselatorVertexState;
import net.minecraft.client.util.QuadComparator;
import net.optifine.Config;
import net.optifine.VertexData;
import org.lwjgl.opengl.GL11;
import shadersmod.client.ShadersTess;

public class Tessellator {
    public boolean renderingChunk = false;
    public boolean defaultTexture = true;
    public int textureID = 0;
    public boolean autoGrow = true;
    public VertexData[] vertexDatas = null;
    public int vertexCount;
    public double textureU;
    public double textureV;
    public int brightness;
    public int color;
    public boolean hasColor;
    public boolean hasTexture;
    public boolean hasBrightness;
    public boolean hasNormals;
    public int rawBufferIndex;
    public int addedVertices;
    public boolean isColorDisabled;
    public int drawMode;
    public double xOffset;
    public double yOffset;
    public double zOffset;
    public int normal;
    public static Tessellator instance = new Tessellator(524288);
    public boolean isDrawing;
    public boolean[] drawnIcons = new boolean[256];
    public TextureAtlasSprite[] vertexQuadIcons = null;
    public static boolean littleEndianByteOrder = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN;
    public static boolean renderingWorldRenderer = false;
    public int bufferSize;
    public ByteBuffer byteBuffer;
    public IntBuffer intBuffer;
    public FloatBuffer floatBuffer;
    public ShortBuffer shortBuffer;
    public int[] rawBuffer;
    public ShadersTess shadersTess;

    public Tessellator() {
        this(65536);
        this.defaultTexture = false;
    }

    public Tessellator(int n) {
        this.bufferSize = n;
        this.byteBuffer = GLAllocation.createDirectByteBuffer(n * 4);
        this.intBuffer = this.byteBuffer.asIntBuffer();
        this.floatBuffer = this.byteBuffer.asFloatBuffer();
        this.shortBuffer = this.byteBuffer.asShortBuffer();
        this.rawBuffer = new int[n];
        this.vertexDatas = new VertexData[4];

        for (int i = 0; i < this.vertexDatas.length; i++) {
            this.vertexDatas[i] = new VertexData();
        }

        this.shadersTess = new ShadersTess();
    }

    public int draw() {
        if (Config.isShaders()) {
            return ShadersTess.draw(this);
        }

        if (!this.isDrawing) {
            throw new IllegalStateException("Not tesselating!");
        }

        this.isDrawing = false;
        if (this.vertexCount > 0 && (!this.renderingChunk || !Config.isMultiTexture())) {
            ((Buffer)this.intBuffer).clear();
            this.intBuffer.put(this.rawBuffer, 0, this.rawBufferIndex);
            ((Buffer)this.byteBuffer).position(0);
            ((Buffer)this.byteBuffer).limit(this.rawBufferIndex * 4);
            if (this.hasTexture) {
                ((Buffer)this.floatBuffer).position(3);
                GL11.glTexCoordPointer(2, 32, this.floatBuffer);
                GL11.glEnableClientState(32888);
            }

            if (this.hasBrightness) {
                OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
                ((Buffer)this.shortBuffer).position(14);
                GL11.glTexCoordPointer(2, 32, this.shortBuffer);
                GL11.glEnableClientState(32888);
                OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
            }

            if (this.hasColor) {
                ((Buffer)this.byteBuffer).position(20);
                GL11.glColorPointer(4, true, 32, this.byteBuffer);
                GL11.glEnableClientState(32886);
            }

            if (this.hasNormals) {
                ((Buffer)this.byteBuffer).position(24);
                GL11.glNormalPointer(32, this.byteBuffer);
                GL11.glEnableClientState(32885);
            }

            ((Buffer)this.floatBuffer).position(0);
            GL11.glVertexPointer(3, 32, this.floatBuffer);
            GL11.glEnableClientState(32884);
            GL11.glDrawArrays(this.drawMode, 0, this.vertexCount);
            GL11.glDisableClientState(32884);
            if (this.hasTexture) {
                GL11.glDisableClientState(32888);
            }

            if (this.hasBrightness) {
                OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
                GL11.glDisableClientState(32888);
                OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
            }

            if (this.hasColor) {
                GL11.glDisableClientState(32886);
            }

            if (this.hasNormals) {
                GL11.glDisableClientState(32885);
            }
        }

        int n = this.rawBufferIndex * 4;
        this.reset();
        return n;
    }

    public TesselatorVertexState getVertexState(float f, float f2, float f3) {
        if (this.rawBufferIndex < 1) {
            return null;
        }

        int[] ints = new int[this.rawBufferIndex];
        PriorityQueue priorityQueue = new PriorityQueue(
            this.rawBufferIndex, new QuadComparator(this.rawBuffer, f + (float)this.xOffset, f2 + (float)this.yOffset, f3 + (float)this.zOffset)
        );
        int n = 32;
        if (Config.isShaders()) {
            n = 72;
        }

        for (int i = 0; i < this.rawBufferIndex; i += n) {
            priorityQueue.add(Integer.valueOf(i));
        }

        for (int i = 0; !priorityQueue.isEmpty(); i += n) {
            int n2 = (Integer)priorityQueue.remove();

            for (int j = 0; j < n; j++) {
                ints[i + j] = this.rawBuffer[n2 + j];
            }
        }

        System.arraycopy(ints, 0, this.rawBuffer, 0, ints.length);
        return new TesselatorVertexState(ints, this.rawBufferIndex, this.vertexCount, this.hasTexture, this.hasBrightness, this.hasNormals, this.hasColor);
    }

    public void setVertexState(TesselatorVertexState tesselatorVertexState) {
        System.arraycopy(tesselatorVertexState.getRawBuffer(), 0, this.rawBuffer, 0, tesselatorVertexState.getRawBuffer().length);
        this.rawBufferIndex = tesselatorVertexState.getRawBufferIndex();
        this.vertexCount = tesselatorVertexState.getVertexCount();
        this.hasTexture = tesselatorVertexState.getHasTexture();
        this.hasBrightness = tesselatorVertexState.getHasBrightness();
        this.hasColor = tesselatorVertexState.getHasColor();
        this.hasNormals = tesselatorVertexState.getHasNormals();
    }

    public void reset() {
        this.vertexCount = 0;
        ((Buffer)this.byteBuffer).clear();
        this.rawBufferIndex = 0;
        this.addedVertices = 0;
    }

    public void startDrawingQuads() {
        this.startDrawing(7);
    }

    public void startDrawing(int n) {
        if (this.isDrawing) {
            throw new IllegalStateException("Already tesselating!");
        }

        this.isDrawing = true;
        this.reset();
        this.drawMode = n;
        this.hasNormals = false;
        this.hasColor = false;
        this.hasTexture = false;
        this.hasBrightness = false;
        this.isColorDisabled = false;
    }

    public void setTextureUV(double d, double d2) {
        this.hasTexture = true;
        this.textureU = d;
        this.textureV = d2;
    }

    public void setBrightness(int n) {
        this.hasBrightness = true;
        this.brightness = n;
    }

    public void setColorOpaque_F(float f, float f2, float f3) {
        this.setColorOpaque((int)(f * 255.0F), (int)(f2 * 255.0F), (int)(f3 * 255.0F));
    }

    public void setColorRGBA_F(float f, float f2, float f3, float f4) {
        this.setColorRGBA((int)(f * 255.0F), (int)(f2 * 255.0F), (int)(f3 * 255.0F), (int)(f4 * 255.0F));
    }

    public void setColorOpaque(int n, int n2, int n3) {
        TessellatorColorOpaqueEvent tessellatorColorOpaqueEvent = new TessellatorColorOpaqueEvent(n, n2, n3, 255);
        Optional.ofNullable(CheatBreaker.getInstance()).map(CheatBreaker::getEventBus).ifPresent(eventBus -> eventBus.handleEvent(tessellatorColorOpaqueEvent));
        this.setColorRGBA(tessellatorColorOpaqueEvent.getRed(), tessellatorColorOpaqueEvent.getGreen(), tessellatorColorOpaqueEvent.getBlue(), tessellatorColorOpaqueEvent.getAlpha());
    }

    public void setColorRGBA(int red, int green, int blue, int alpha) {
        if (!this.isColorDisabled) {
            if (red > 255) {
                red = 255;
            }

            if (green > 255) {
                green = 255;
            }

            if (blue > 255) {
                blue = 255;
            }

            if (alpha > 255) {
                alpha = 255;
            }

            if (red < 0) {
                red = 0;
            }

            if (green < 0) {
                green = 0;
            }

            if (blue < 0) {
                blue = 0;
            }

            if (alpha < 0) {
                alpha = 0;
            }

            this.hasColor = true;
            if (littleEndianByteOrder) {
                this.color = alpha << 24 | blue << 16 | green << 8 | red;
            } else {
                this.color = red << 24 | green << 16 | blue << 8 | alpha;
            }
        }
    }

    public void setColorOpaque(byte b, byte b2, byte b3) {
        this.setColorOpaque(b & 255, b2 & 255, b3 & 255);
    }

    public void addVertexWithUV(double d, double d2, double d3, double d4, double d5) {
        this.setTextureUV(d4, d5);
        this.addVertex(d, d2, d3);
    }

    public void addVertex(double d, double d2, double d3) {
        if (Config.isShaders()) {
            ShadersTess.addVertex(this, d, d2, d3);
        } else {
            if (this.autoGrow && this.rawBufferIndex >= this.bufferSize - 32) {
                Config.dbg("Expand tessellator buffer, old: " + this.bufferSize + ", new: " + this.bufferSize * 2);
                this.bufferSize *= 2;
                int[] ints = new int[this.bufferSize];
                System.arraycopy(this.rawBuffer, 0, ints, 0, this.rawBuffer.length);
                this.rawBuffer = ints;
                this.byteBuffer = GLAllocation.createDirectByteBuffer(this.bufferSize * 4);
                this.intBuffer = this.byteBuffer.asIntBuffer();
                this.floatBuffer = this.byteBuffer.asFloatBuffer();
                this.shortBuffer = this.byteBuffer.asShortBuffer();
                if (this.vertexQuadIcons != null) {
                    TextureAtlasSprite[] textureAtlasSprites = new TextureAtlasSprite[this.bufferSize / 4];
                    System.arraycopy(this.vertexQuadIcons, 0, textureAtlasSprites, 0, this.vertexQuadIcons.length);
                    this.vertexQuadIcons = textureAtlasSprites;
                }
            }

            this.addedVertices++;
            if (this.hasTexture) {
                this.rawBuffer[this.rawBufferIndex + 3] = Float.floatToRawIntBits((float)this.textureU);
                this.rawBuffer[this.rawBufferIndex + 4] = Float.floatToRawIntBits((float)this.textureV);
            }

            if (this.hasBrightness) {
                this.rawBuffer[this.rawBufferIndex + 7] = this.brightness;
            }

            if (this.hasColor) {
                this.rawBuffer[this.rawBufferIndex + 5] = this.color;
            }

            if (this.hasNormals) {
                this.rawBuffer[this.rawBufferIndex + 6] = this.normal;
            }

            this.rawBuffer[this.rawBufferIndex + 0] = Float.floatToRawIntBits((float)(d + this.xOffset));
            this.rawBuffer[this.rawBufferIndex + 1] = Float.floatToRawIntBits((float)(d2 + this.yOffset));
            this.rawBuffer[this.rawBufferIndex + 2] = Float.floatToRawIntBits((float)(d3 + this.zOffset));
            this.rawBufferIndex += 8;
            this.vertexCount++;
            if (!this.autoGrow && this.addedVertices % 4 == 0 && this.rawBufferIndex >= this.bufferSize - 32) {
                this.draw();
                this.isDrawing = true;
            }
        }
    }

    public void setColorOpaque_I(int n) {
        int n2 = n >> 16 & 0xFF;
        int n3 = n >> 8 & 0xFF;
        int n4 = n & 0xFF;
        this.setColorOpaque(n2, n3, n4);
    }

    public void setColorRGBA_I(int n, int n2) {
        int n3 = n >> 16 & 0xFF;
        int n4 = n >> 8 & 0xFF;
        int n5 = n & 0xFF;
        this.setColorRGBA(n3, n4, n5, n2);
    }

    public void disableColor() {
        this.isColorDisabled = true;
    }

    public void setNormal(float f, float f2, float f3) {
        if (Config.isShaders()) {
            ShadersTess shadersTess = this.shadersTess;
            shadersTess.normalX = f;
            shadersTess.normalY = f2;
            shadersTess.normalZ = f3;
        }

        this.hasNormals = true;
        byte b = (byte)(f * 127.0F);
        byte b2 = (byte)(f2 * 127.0F);
        byte b3 = (byte)(f3 * 127.0F);
        this.normal = b & 255 | (b2 & 255) << 8 | (b3 & 255) << 16;
    }

    public void setTranslation(double d, double d2, double d3) {
        this.xOffset = d;
        this.yOffset = d2;
        this.zOffset = d3;
    }

    public void addTranslation(float f, float f2, float f3) {
        this.xOffset += f;
        this.yOffset += f2;
        this.zOffset += f3;
    }

    public boolean isRenderingChunk() {
        return this.renderingChunk;
    }

    public void setRenderingChunk(boolean flag) {
        this.renderingChunk = flag;
    }

    public void draw(int n, int n2) {
        int n3 = n2 - n;
        if (n3 > 0) {
            int n4 = n * 4;
            int n5 = n3 * 4;
            ((Buffer)this.floatBuffer).position(3);
            GL11.glTexCoordPointer(2, 32, this.floatBuffer);
            OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
            ((Buffer)this.shortBuffer).position(14);
            GL11.glTexCoordPointer(2, 32, this.shortBuffer);
            GL11.glEnableClientState(32888);
            OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
            ((Buffer)this.byteBuffer).position(20);
            GL11.glColorPointer(4, true, 32, this.byteBuffer);
            ((Buffer)this.floatBuffer).position(0);
            GL11.glVertexPointer(3, 32, this.floatBuffer);
            GL11.glDrawArrays(this.drawMode, n4, n5);
        }
    }

    public int drawForIcon(TextureAtlasSprite textureAtlasSprite, int n) {
        textureAtlasSprite.bindOwnTexture();
        int n2 = -1;
        int n3 = -1;
        int n4 = this.addedVertices / 4;

        for (int i = n; i < n4; i++) {
            TextureAtlasSprite textureAtlasSprite2 = this.vertexQuadIcons[i];
            if (textureAtlasSprite2 == textureAtlasSprite) {
                if (n3 < 0) {
                    n3 = i;
                }
            } else if (n3 >= 0) {
                this.draw(n3, i);
                n3 = -1;
                if (n2 < 0) {
                    n2 = i;
                }
            }
        }

        if (n3 >= 0) {
            this.draw(n3, n4);
        }

        if (n2 < 0) {
            n2 = n4;
        }

        return n2;
    }
}
