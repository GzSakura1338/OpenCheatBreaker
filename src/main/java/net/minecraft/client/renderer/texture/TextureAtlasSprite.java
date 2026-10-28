package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.AnimationFrame;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.IIcon;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.optifine.Config;
import net.optifine.Mipmaps;
import net.optifine.TextureUtils;
import org.lwjgl.opengl.GL11;
import shadersmod.client.Shaders;

public class TextureAtlasSprite implements IIcon {
    public List framesTextureData = Lists.newArrayList();
    public int indexInMap = -1;
    public AnimationMetadataSection animationMetadata;
    public boolean rotated;
    public boolean useAnisotropicFiltering;
    public int originX;
    public int originY;
    public int width;
    public int height;
    public float minU;
    public float maxU;
    public float minV;
    public float maxV;
    public int frameCounter;
    public int tickCounter;
    public boolean mipmapActive = false;
    public float baseU;
    public float baseV;
    public int glOwnTextureId = -1;
    public int uploadedFrameIndex = -1;
    public int uploadedOwnFrameIndex = -1;
    public int mipmapLevels = 0;
    public IntBuffer[] frameBuffers;
    public Mipmaps[] frameMipmaps;
    public TextureAtlasSprite spriteNormal = null;
    public TextureAtlasSprite spriteSpecular = null;
    public boolean isShadersSprite = false;
    public String iconName;
    public int intValue;
    public int intValue2;

    public TextureAtlasSprite(String str) {
        this.iconName = str;
    }

    public void initSprite(int n, int n2, int n3, int n4, boolean flag) {
        this.originX = n3;
        this.originY = n4;
        this.rotated = flag;
        float f = (float)(0.009999999776482582 / (double)n);
        float f2 = (float)(0.009999999776482582 / (double)n2);
        this.minU = n3 / (float)n + f;
        this.maxU = (n3 + this.width) / (float)n - f;
        this.minV = (float)n4 / n2 + f2;
        this.maxV = (float)(n4 + this.height) / n2 - f2;
        if (this.useAnisotropicFiltering) {
            float f3 = 8.0F / n;
            float f4 = 8.0F / n2;
            this.minU += f3;
            this.maxU -= f3;
            this.minV += f4;
            this.maxV -= f4;
        }

        this.baseU = Math.min(this.minU, this.maxU);
        this.baseV = Math.min(this.minV, this.maxV);
        if (this.spriteNormal != null) {
            this.spriteNormal.initSprite(n, n2, n3, n4, flag);
        }

        if (this.spriteSpecular != null) {
            this.spriteSpecular.initSprite(n, n2, n3, n4, flag);
        }
    }

    public void copyFrom(TextureAtlasSprite textureAtlasSprite) {
        this.originX = textureAtlasSprite.originX;
        this.originY = textureAtlasSprite.originY;
        this.width = textureAtlasSprite.width;
        this.height = textureAtlasSprite.height;
        this.rotated = textureAtlasSprite.rotated;
        this.minU = textureAtlasSprite.minU;
        this.maxU = textureAtlasSprite.maxU;
        this.minV = textureAtlasSprite.minV;
        this.maxV = textureAtlasSprite.maxV;
        this.baseU = Math.min(this.minU, this.maxU);
        this.baseV = Math.min(this.minV, this.maxV);
    }

    public int getOriginX() {
        return this.originX;
    }

    public int getOriginY() {
        return this.originY;
    }

    @Override
    public int getIconWidth() {
        return this.width;
    }

    @Override
    public int getIconHeight() {
        return this.height;
    }

    @Override
    public float getMinU() {
        return this.minU;
    }

    @Override
    public float getMaxU() {
        return this.maxU;
    }

    @Override
    public float getInterpolatedU(double d) {
        float f = this.maxU - this.minU;
        return this.minU + f * (float)d / 16.0F;
    }

    @Override
    public float getMinV() {
        return this.minV;
    }

    @Override
    public float getMaxV() {
        return this.maxV;
    }

    @Override
    public float getInterpolatedV(double d) {
        float f = this.maxV - this.minV;
        return this.minV + f * ((float)d / 16.0F);
    }

    @Override
    public String getIconName() {
        return this.iconName;
    }

    public void updateAnimation() {
        if (this.animationMetadata != null) {
            this.tickCounter++;
            if (this.tickCounter >= this.animationMetadata.getFrameTimeSingle(this.frameCounter)) {
                int frameIndex = this.animationMetadata.getFrameIndex(this.frameCounter);
                int n = this.animationMetadata.getFrameCount() == 0 ? this.framesTextureData.size() : this.animationMetadata.getFrameCount();
                this.frameCounter = (this.frameCounter + 1) % n;
                this.tickCounter = 0;
                int frameIndex2 = this.animationMetadata.getFrameIndex(this.frameCounter);
                boolean flag = false;
                boolean flag2 = false;
                if (frameIndex != frameIndex2 && frameIndex2 >= 0 && frameIndex2 < this.framesTextureData.size()) {
                    TextureUtil.uploadTextureMipmap((int[][])this.framesTextureData.get(frameIndex2), this.width, this.height, this.originX, this.originY, flag, flag2);
                    this.uploadedFrameIndex = frameIndex2;
                }
            }
        }
    }

    public int[][] getFrameTextureData(int n) {
        return (int[][])this.framesTextureData.get(n);
    }

    public int getFrameCount() {
        return this.framesTextureData.size();
    }

    public void setIconWidth(int n) {
        this.width = n;
    }

    public void setIconHeight(int n) {
        this.height = n;
    }

    public void loadSprite(BufferedImage[] bufferedImages, AnimationMetadataSection animationMetadataSection, boolean flag) {
        this.resetSprite();
        this.useAnisotropicFiltering = flag;
        int width = bufferedImages[0].getWidth();
        int height = bufferedImages[0].getHeight();
        this.width = width;
        this.height = height;
        if (flag) {
            this.width += 16;
            this.height += 16;
        }

        int[][] intses = new int[bufferedImages.length][];

        for (int i = 0; i < bufferedImages.length; i++) {
            BufferedImage bufferedImage = bufferedImages[i];
            if (bufferedImage != null) {
                if (i > 0 && (bufferedImage.getWidth() != width >> i || bufferedImage.getHeight() != height >> i)) {
                    throw new RuntimeException(
                        String.format(
                            "Unable to load miplevel: %d, image is size: %dx%d, expected %dx%d", i, bufferedImage.getWidth(), bufferedImage.getHeight(), width >> i, height >> i
                        )
                    );
                }

                intses[i] = new int[bufferedImage.getWidth() * bufferedImage.getHeight()];
                bufferedImage.getRGB(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), intses[i], 0, bufferedImage.getWidth());
            }
        }

        if (animationMetadataSection == null) {
            if (height != width) {
                throw new RuntimeException("broken aspect ratio and not an animation");
            }

            this.fixTransparentPixels(intses);
            this.framesTextureData.add(this.prepareAnisotropicFiltering(intses, width, height));
        } else {
            int n = height / width;
            int n2 = width;
            int n3 = width;
            this.height = this.width;
            if (animationMetadataSection.getFrameCount() > 0) {
                for (int n4 : (Iterable<Integer>)animationMetadataSection.getFrameIndexSet()) {
                    if (n4 >= n) {
                        throw new RuntimeException("invalid frameindex " + n4);
                    }

                    this.allocateFrameTextureData(n4);
                    this.framesTextureData.set(n4, this.prepareAnisotropicFiltering(getFrameTextureData(intses, n2, n3, n4), n2, n3));
                }

                this.animationMetadata = animationMetadataSection;
            } else {
                ArrayList newArrayList = Lists.newArrayList();

                for (int i = 0; i < n; i++) {
                    this.framesTextureData.add(this.prepareAnisotropicFiltering(getFrameTextureData(intses, n2, n3, i), n2, n3));
                    newArrayList.add(new AnimationFrame(i, -1));
                }

                this.animationMetadata = new AnimationMetadataSection(newArrayList, this.width, this.height, animationMetadataSection.getFrameTime());
            }
        }

        if (!this.isShadersSprite && Config.isShaders()) {
            this.loadShadersSprites();
        }
    }

    public void generateMipmaps(int n) {
        ArrayList newArrayList = Lists.newArrayList();

        for (int i = 0; i < this.framesTextureData.size(); i++) {
            final int[][] intses = (int[][])this.framesTextureData.get(i);
            if (intses != null) {
                try {
                    newArrayList.add(TextureUtil.generateMipmapData(n, this.width, intses));
                } catch (Throwable e) {
                    CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Generating mipmaps for frame");
                    CrashReportCategory makeCategory = makeCrashReport.makeCategory("Frame being iterated");
                    makeCategory.addCrashSection("Frame index", i);
                    makeCategory.addCrashSectionCallable("Frame sizes", new Callable() {
                        public String call() {
                            StringBuilder sb = new StringBuilder();

                            for (int[] ints : intses) {
                                if (sb.length() > 0) {
                                    sb.append(", ");
                                }

                                sb.append(ints == null ? "null" : ints.length);
                            }

                            return sb.toString();
                        }
                    });
                    throw new ReportedException(makeCrashReport);
                }
            }
        }

        this.setFramesTextureData(newArrayList);
    }

    public void fixTransparentPixels(int[][] intses) {
        int[] ints = intses[0];
        int n = 0;
        int n2 = 0;
        int n3 = 0;
        int n4 = 0;

        for (int i = 0; i < ints.length; i++) {
            if ((ints[i] & 0xFF000000) != 0) {
                n2 += ints[i] >> 16 & 0xFF;
                n3 += ints[i] >> 8 & 0xFF;
                n4 += ints[i] >> 0 & 0xFF;
                n++;
            }
        }

        if (n != 0) {
            n2 /= n;
            n3 /= n;
            n4 /= n;

            for (int i = 0; i < ints.length; i++) {
                if ((ints[i] & 0xFF000000) == 0) {
                    ints[i] = n2 << 16 | n3 << 8 | n4;
                }
            }
        }
    }

    public int[][] prepareAnisotropicFiltering(int[][] intses, int n, int n2) {
        if (!this.useAnisotropicFiltering) {
            return intses;
        }

        int[][] intses2 = new int[intses.length][];

        for (int i = 0; i < intses.length; i++) {
            int[] ints = intses[i];
            if (ints != null) {
                int[] ints2 = new int[(n + 16 >> i) * (n2 + 16 >> i)];
                System.arraycopy(ints, 0, ints2, 0, ints.length);
                intses2[i] = TextureUtil.prepareAnisotropicData(ints2, n >> i, n2 >> i, 8 >> i);
            }
        }

        return intses2;
    }

    public void allocateFrameTextureData(int n) {
        if (this.framesTextureData.size() <= n) {
            for (int size = this.framesTextureData.size(); size <= n; size++) {
                this.framesTextureData.add(null);
            }
        }
    }

    public static int[][] getFrameTextureData(int[][] intses, int n, int n2, int n3) {
        int[][] intses2 = new int[intses.length][];

        for (int i = 0; i < intses.length; i++) {
            int[] ints = intses[i];
            if (ints != null) {
                intses2[i] = new int[(n >> i) * (n2 >> i)];
                System.arraycopy(ints, n3 * intses2[i].length, intses2[i], 0, intses2[i].length);
            }
        }

        return intses2;
    }

    public void clearFramesTextureData() {
        this.framesTextureData.clear();
    }

    public boolean hasAnimationMetadata() {
        return this.animationMetadata != null;
    }

    public void setFramesTextureData(List list) {
        this.framesTextureData = list;

        for (int i = 0; i < this.framesTextureData.size(); i++) {
            int[][] intses = (int[][])this.framesTextureData.get(i);
            if (intses != null && !this.iconName.startsWith("leaves_")) {
                for (int j = 0; j < intses.length; j++) {
                    int[] ints = intses[j];
                    if (ints != null) {
                        this.fixTransparentColor(ints);
                    }
                }
            }
        }
    }

    public void resetSprite() {
        this.animationMetadata = null;
        this.setFramesTextureData(Lists.newArrayList());
        this.frameCounter = 0;
        this.tickCounter = 0;
        this.deleteOwnTexture();
        this.uploadedFrameIndex = -1;
        this.uploadedOwnFrameIndex = -1;
        this.frameBuffers = null;
        this.frameMipmaps = null;
    }

    @Override
    public String toString() {
        return "TextureAtlasSprite{name='"
            + this.iconName
            + '\''
            + ", frameCount="
            + this.framesTextureData.size()
            + ", rotated="
            + this.rotated
            + ", x="
            + this.originX
            + ", y="
            + this.originY
            + ", height="
            + this.height
            + ", width="
            + this.width
            + ", u0="
            + this.minU
            + ", u1="
            + this.maxU
            + ", v0="
            + this.minV
            + ", v1="
            + this.maxV
            + '}';
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getIndexInMap() {
        return this.indexInMap;
    }

    public void setIndexInMap(int n) {
        this.indexInMap = n;
    }

    public void setMipmapActive(boolean flag) {
        this.mipmapActive = flag;
        this.frameMipmaps = null;
    }

    public void uploadFrameTexture() {
        this.uploadFrameTexture(this.frameCounter, this.originX, this.originY);
    }

    public void uploadFrameTexture(int n, int n2, int n3) {
    }

    public void uploadFrameMipmaps(int n, int n2, int n3) {
    }

    public void bindOwnTexture() {
    }

    public void bindUploadOwnTexture() {
        this.bindOwnTexture();
        this.uploadFrameTexture(this.frameCounter, 0, 0);
    }

    public void uploadOwnAnimation() {
        if (this.uploadedFrameIndex != this.uploadedOwnFrameIndex) {
            TextureUtil.bindTexture(this.glOwnTextureId);
            this.uploadFrameTexture(this.uploadedFrameIndex, 0, 0);
            this.uploadedOwnFrameIndex = this.uploadedFrameIndex;
        }
    }

    public void deleteOwnTexture() {
        if (this.glOwnTextureId >= 0) {
            GL11.glDeleteTextures(this.glOwnTextureId);
            this.glOwnTextureId = -1;
        }
    }

    public void fixTransparentColor(int[] ints) {
        if (!this.isShadersSprite) {
            long l = 0L;
            long l2 = 0L;
            long l3 = 0L;
            long l4 = 0L;

            for (int i = 0; i < ints.length; i++) {
                int n = ints[i];
                int n2 = n >> 24 & 0xFF;
                if (n2 >= 16) {
                    int n3 = n >> 16 & 0xFF;
                    int n4 = n >> 8 & 0xFF;
                    int n5 = n & 0xFF;
                    l += n3;
                    l2 += n4;
                    l3 += n5;
                    l4++;
                }
            }

            if (l4 > 0L) {
                int n = (int)(l / l4);
                int n2 = (int)(l2 / l4);
                int n3 = (int)(l3 / l4);
                int n4 = n << 16 | n2 << 8 | n3;

                for (int i = 0; i < ints.length; i++) {
                    int n5 = ints[i];
                    int n6 = n5 >> 24 & 0xFF;
                    if (n6 <= 16) {
                        ints[i] = n4;
                    }
                }
            }
        }
    }

    public boolean hasCustomLoader(IResourceManager resourceManager, ResourceLocation resourceLocation) {
        return false;
    }

    public boolean load(IResourceManager resourceManager, ResourceLocation resourceLocation) {
        return true;
    }

    public void loadShadersSprites() {
        this.mipmapLevels = Config.getTextureMap().getMipmapLevels();
        if (Shaders.configNormalMap) {
            String str = this.iconName + "_n";
            ResourceLocation resourceLocation = new ResourceLocation(str);
            resourceLocation = Config.getTextureMap().completeResourceLocation(resourceLocation, 0);
            if (Config.hasResource(resourceLocation)) {
                try {
                    TextureAtlasSprite textureAtlasSprite = new TextureAtlasSprite(str);
                    textureAtlasSprite.isShadersSprite = true;
                    textureAtlasSprite.copyFrom(this);
                    textureAtlasSprite.loadShaderSpriteFrames(resourceLocation, this.mipmapLevels + 1);
                    textureAtlasSprite.generateMipmaps(this.mipmapLevels);
                    this.spriteNormal = textureAtlasSprite;
                } catch (IOException e) {
                    Config.warn("Error loading normal texture: " + str);
                    Config.warn(e.getClass().getName() + ": " + e.getMessage());
                }
            }
        }

        if (Shaders.configSpecularMap) {
            String str = this.iconName + "_s";
            ResourceLocation resourceLocation = new ResourceLocation(str);
            resourceLocation = Config.getTextureMap().completeResourceLocation(resourceLocation, 0);
            if (Config.hasResource(resourceLocation)) {
                try {
                    TextureAtlasSprite textureAtlasSprite = new TextureAtlasSprite(str);
                    textureAtlasSprite.isShadersSprite = true;
                    textureAtlasSprite.copyFrom(this);
                    textureAtlasSprite.loadShaderSpriteFrames(resourceLocation, this.mipmapLevels + 1);
                    textureAtlasSprite.generateMipmaps(this.mipmapLevels);
                    this.spriteSpecular = textureAtlasSprite;
                } catch (IOException e) {
                    Config.warn("Error loading specular texture: " + str);
                    Config.warn(e.getClass().getName() + ": " + e.getMessage());
                }
            }
        }
    }

    public void loadShaderSpriteFrames(ResourceLocation resourceLocation, int n) throws IOException {
        IResource resource = Config.getResource(resourceLocation);
        BufferedImage readBufferedImage = TextureUtils.readBufferedImage(resource.getInputStream());
        if (this.width != readBufferedImage.getWidth()) {
            readBufferedImage = TextureUtils.scaleImage(readBufferedImage, this.width);
        }

        AnimationMetadataSection metadata = (AnimationMetadataSection)resource.getMetadata("animation");
        int[][] intses = new int[n][];
        intses[0] = new int[readBufferedImage.getWidth() * readBufferedImage.getHeight()];
        readBufferedImage.getRGB(0, 0, readBufferedImage.getWidth(), readBufferedImage.getHeight(), intses[0], 0, readBufferedImage.getWidth());
        if (metadata == null) {
            this.framesTextureData.add(intses);
        } else {
            int n2 = readBufferedImage.getHeight() / this.width;
            if (metadata.getFrameCount() > 0) {
                for (int n3 : (Iterable<Integer>)metadata.getFrameIndexSet()) {
                    if (n3 >= n2) {
                        throw new RuntimeException("invalid frameindex " + n3);
                    }

                    this.allocateFrameTextureData(n3);
                    this.framesTextureData.set(n3, getFrameTextureData(intses, this.width, this.width, n3));
                }

                this.animationMetadata = metadata;
            } else {
                ArrayList newArrayList = Lists.newArrayList();

                for (int i = 0; i < n2; i++) {
                    this.framesTextureData.add(getFrameTextureData(intses, this.width, this.width, i));
                    newArrayList.add(new AnimationFrame(i, -1));
                }

                this.animationMetadata = new AnimationMetadataSection(newArrayList, this.width, this.height, metadata.getFrameTime());
            }
        }
    }
}
