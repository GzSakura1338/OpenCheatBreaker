package net.optifine;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Properties;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TextureAnimation {
    public String srcTex = null;
    public String dstTex = null;
    public ResourceLocation dstTexLoc = null;
    public int dstTextId = -1;
    public int dstX = 0;
    public int dstY = 0;
    public int frameWidth = 0;
    public int frameHeight = 0;
    public TextureAnimationFrame[] frames = null;
    public int activeFrame = 0;
    public byte[] srcData = null;
    public ByteBuffer imageData = null;

    public TextureAnimation(String str, byte[] bytes, String str2, ResourceLocation resourceLocation, int n, int n2, int n3, int n4, Properties properties, int n5) {
        this.srcTex = str;
        this.dstTex = str2;
        this.dstTexLoc = resourceLocation;
        this.dstX = n;
        this.dstY = n2;
        this.frameWidth = n3;
        this.frameHeight = n4;
        int n6 = n3 * n4 * 4;
        if (bytes.length % n6 != 0) {
            Config.warn("Invalid animated texture length: " + bytes.length + ", frameWidth: " + n3 + ", frameHeight: " + n4);
        }

        this.srcData = bytes;
        int n7 = bytes.length / n6;
        if (properties.get("tile.0") != null) {
            for (int i = 0; properties.get("tile." + i) != null; i++) {
                n7 = i + 1;
            }
        }

        String str3 = (String)properties.get("duration");
        int parseInt = Config.parseInt(str3, n5);
        this.frames = new TextureAnimationFrame[n7];

        for (int i = 0; i < this.frames.length; i++) {
            String str4 = (String)properties.get("tile." + i);
            int parseInt2 = Config.parseInt(str4, i);
            String str5 = (String)properties.get("duration." + i);
            int parseInt3 = Config.parseInt(str5, parseInt);
            TextureAnimationFrame textureAnimationFrame = new TextureAnimationFrame(parseInt2, parseInt3);
            this.frames[i] = textureAnimationFrame;
        }
    }

    public boolean nextFrame() {
        if (this.frames.length <= 0) {
            return false;
        }

        if (this.activeFrame >= this.frames.length) {
            this.activeFrame = 0;
        }

        TextureAnimationFrame textureAnimationFrame = this.frames[this.activeFrame];
        textureAnimationFrame.counter++;
        if (textureAnimationFrame.counter < textureAnimationFrame.duration) {
            return false;
        }

        textureAnimationFrame.counter = 0;
        this.activeFrame++;
        if (this.activeFrame >= this.frames.length) {
            this.activeFrame = 0;
        }

        return true;
    }

    public int getActiveFrameIndex() {
        if (this.frames.length <= 0) {
            return 0;
        }

        if (this.activeFrame >= this.frames.length) {
            this.activeFrame = 0;
        }

        TextureAnimationFrame textureAnimationFrame = this.frames[this.activeFrame];
        return textureAnimationFrame.index;
    }

    public int getFrameCount() {
        return this.frames.length;
    }

    public boolean updateTexture() {
        if (this.dstTextId < 0) {
            ITextureObject texture = TextureUtils.getTexture(this.dstTexLoc);
            if (texture == null) {
                return false;
            }

            this.dstTextId = texture.getGlTextureId();
        }

        if (this.imageData == null) {
            this.imageData = GLAllocation.createDirectByteBuffer(this.srcData.length);
            this.imageData.put(this.srcData);
            this.srcData = null;
        }

        if (!this.nextFrame()) {
            return false;
        }

        int n = this.frameWidth * this.frameHeight * 4;
        int activeFrameIndex = this.getActiveFrameIndex();
        int n2 = n * activeFrameIndex;
        if (n2 + n > this.imageData.capacity()) {
            return false;
        }

        ((Buffer)this.imageData).position(n2);
        GlStateManager.bindTexture(this.dstTextId);
        GL11.glTexSubImage2D(3553, 0, this.dstX, this.dstY, this.frameWidth, this.frameHeight, 6408, 5121, this.imageData);
        return true;
    }

    public String getSrcTex() {
        return this.srcTex;
    }

    public String getDstTex() {
        return this.dstTex;
    }

    public ResourceLocation getDstTexLoc() {
        return this.dstTexLoc;
    }
}
