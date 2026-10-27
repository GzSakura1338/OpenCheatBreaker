package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.AnimationFrame;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.optifine.Config;
import net.optifine.SmartAnimations;
import net.optifine.shaders.Shaders;
import net.optifine.util.CounterInt;
import net.optifine.util.TextureUtils;

public class TextureAtlasSprite {
   public List<int[][]> framesTextureData = Lists.newArrayList();
   public int indexInMap = -1;
   public int[][] interpolatedFrameData;
   public AnimationMetadataSection animationMetadata;
   public boolean rotated;
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
   public static String locationNameClock = "builtin/clock";
   public static String locationNameCompass = "builtin/compass";
   public int glSpriteTextureId = -1;
   public float baseU;
   public float baseV;
   public int sheetWidth;
   public int sheetHeight;
   public TextureAtlasSprite spriteSingle = null;
   public boolean isSpriteSingle = false;
   public int mipmapLevels = 0;
   public TextureAtlasSprite spriteNormal = null;
   public TextureAtlasSprite spriteSpecular = null;
   public boolean isShadersSprite = false;
   public boolean isEmissive = false;
   public TextureAtlasSprite spriteEmissive = null;
   public int animationIndex = -1;
   public boolean animationActive = false;
   public String iconName;

   public TextureAtlasSprite(String str, boolean flag) {
      this.iconName = str;
      this.isSpriteSingle = flag;
   }

   public TextureAtlasSprite(String spriteName) {
      this.iconName = spriteName;
      if (Config.isMultiTexture()) {
         this.spriteSingle = new TextureAtlasSprite(this.getIconName() + ".spriteSingle", true);
      }
   }

   public static TextureAtlasSprite makeAtlasSprite(ResourceLocation spriteResourceLocation) {
      String str = spriteResourceLocation.toString();
      return locationNameClock.equals(str)
         ? new TextureClock(str)
         : (locationNameCompass.equals(str) ? new TextureCompass(str) : new TextureAtlasSprite(str));
   }

   public static void setLocationNameClock(String clockName) {
      locationNameClock = clockName;
   }

   public static void setLocationNameCompass(String compassName) {
      locationNameCompass = compassName;
   }

   public void initSprite(int inX, int inY, int originInX, int originInY, boolean rotatedIn) {
      this.originX = originInX;
      this.originY = originInY;
      this.rotated = rotatedIn;
      float f = (float)(0.01F / (double)inX);
      float f2 = (float)(0.01F / (double)inY);
      this.minU = originInX / (float)inX + f;
      this.maxU = (originInX + this.width) / (float)inX - f;
      this.minV = (float)originInY / inY + f2;
      this.maxV = (float)(originInY + this.height) / inY - f2;
      this.baseU = Math.min(this.minU, this.maxU);
      this.baseV = Math.min(this.minV, this.maxV);
      if (this.spriteSingle != null) {
         this.spriteSingle.initSprite(this.width, this.height, 0, 0, false);
      }

      if (this.spriteNormal != null) {
         this.spriteNormal.copyFrom(this);
      }

      if (this.spriteSpecular != null) {
         this.spriteSpecular.copyFrom(this);
      }
   }

   public void copyFrom(TextureAtlasSprite atlasSpirit) {
      this.originX = atlasSpirit.originX;
      this.originY = atlasSpirit.originY;
      this.width = atlasSpirit.width;
      this.height = atlasSpirit.height;
      this.rotated = atlasSpirit.rotated;
      this.minU = atlasSpirit.minU;
      this.maxU = atlasSpirit.maxU;
      this.minV = atlasSpirit.minV;
      this.maxV = atlasSpirit.maxV;
      if (atlasSpirit != Config.getTextureMap().getMissingSprite()) {
         this.indexInMap = atlasSpirit.indexInMap;
      }

      this.baseU = atlasSpirit.baseU;
      this.baseV = atlasSpirit.baseV;
      this.sheetWidth = atlasSpirit.sheetWidth;
      this.sheetHeight = atlasSpirit.sheetHeight;
      this.glSpriteTextureId = atlasSpirit.glSpriteTextureId;
      this.mipmapLevels = atlasSpirit.mipmapLevels;
      if (this.spriteSingle != null) {
         this.spriteSingle.initSprite(this.width, this.height, 0, 0, false);
      }

      this.animationIndex = atlasSpirit.animationIndex;
   }

   public int getOriginX() {
      return this.originX;
   }

   public int getOriginY() {
      return this.originY;
   }

   public int getIconWidth() {
      return this.width;
   }

   public int getIconHeight() {
      return this.height;
   }

   public float getMinU() {
      return this.minU;
   }

   public float getMaxU() {
      return this.maxU;
   }

   public float getInterpolatedU(double u) {
      float f = this.maxU - this.minU;
      return this.minU + f * (float)u / 16.0F;
   }

   public float getMinV() {
      return this.minV;
   }

   public float getMaxV() {
      return this.maxV;
   }

   public float getInterpolatedV(double v) {
      float f = this.maxV - this.minV;
      return this.minV + f * ((float)v / 16.0F);
   }

   public String getIconName() {
      return this.iconName;
   }

   public void updateAnimation() {
      if (this.animationMetadata != null) {
         this.animationActive = SmartAnimations.isActive() ? SmartAnimations.isSpriteRendered(this.animationIndex) : true;
         this.tickCounter++;
         if (this.tickCounter >= this.animationMetadata.getFrameTimeSingle(this.frameCounter)) {
            int frameIndex = this.animationMetadata.getFrameIndex(this.frameCounter);
            int n = this.animationMetadata.getFrameCount() == 0 ? this.framesTextureData.size() : this.animationMetadata.getFrameCount();
            this.frameCounter = (this.frameCounter + 1) % n;
            this.tickCounter = 0;
            int frameIndex2 = this.animationMetadata.getFrameIndex(this.frameCounter);
            boolean flag = false;
            boolean isSpriteSingle = this.isSpriteSingle;
            if (!this.animationActive) {
               return;
            }

            if (frameIndex != frameIndex2 && frameIndex2 >= 0 && frameIndex2 < this.framesTextureData.size()) {
               TextureUtil.uploadTextureMipmap(this.framesTextureData.get(frameIndex2), this.width, this.height, this.originX, this.originY, flag, isSpriteSingle);
            }
         } else if (this.animationMetadata.isInterpolate()) {
            if (!this.animationActive) {
               return;
            }

            this.updateAnimationInterpolated();
         }
      }
   }

   public void updateAnimationInterpolated() {
      double d = 1.0 - (double)this.tickCounter / this.animationMetadata.getFrameTimeSingle(this.frameCounter);
      int frameIndex = this.animationMetadata.getFrameIndex(this.frameCounter);
      int n = this.animationMetadata.getFrameCount() == 0 ? this.framesTextureData.size() : this.animationMetadata.getFrameCount();
      int frameIndex2 = this.animationMetadata.getFrameIndex((this.frameCounter + 1) % n);
      if (frameIndex != frameIndex2 && frameIndex2 >= 0 && frameIndex2 < this.framesTextureData.size()) {
         int[][] intses = this.framesTextureData.get(frameIndex);
         int[][] intses2 = this.framesTextureData.get(frameIndex2);
         if (this.interpolatedFrameData == null || this.interpolatedFrameData.length != intses.length) {
            this.interpolatedFrameData = new int[intses.length][];
         }

         for (int i = 0; i < intses.length; i++) {
            if (this.interpolatedFrameData[i] == null) {
               this.interpolatedFrameData[i] = new int[intses[i].length];
            }

            if (i < intses2.length && intses2[i].length == intses[i].length) {
               for (int j = 0; j < intses[i].length; j++) {
                  int n2 = intses[i][j];
                  int n3 = intses2[i][j];
                  int n4 = (int)(((n2 & 0xFF0000) >> 16) * d + ((n3 & 0xFF0000) >> 16) * (1.0 - d));
                  int n5 = (int)(((n2 & 0xFF00) >> 8) * d + ((n3 & 0xFF00) >> 8) * (1.0 - d));
                  int n6 = (int)((n2 & 0xFF) * d + (n3 & 0xFF) * (1.0 - d));
                  this.interpolatedFrameData[i][j] = n2 & 0xFF000000 | n4 << 16 | n5 << 8 | n6;
               }
            }
         }

         TextureUtil.uploadTextureMipmap(this.interpolatedFrameData, this.width, this.height, this.originX, this.originY, false, false);
      }
   }

   public int[][] getFrameTextureData(int index) {
      return this.framesTextureData.get(index);
   }

   public int getFrameCount() {
      return this.framesTextureData.size();
   }

   public void setIconWidth(int newWidth) {
      this.width = newWidth;
      if (this.spriteSingle != null) {
         this.spriteSingle.setIconWidth(this.width);
      }
   }

   public void setIconHeight(int newHeight) {
      this.height = newHeight;
      if (this.spriteSingle != null) {
         this.spriteSingle.setIconHeight(this.height);
      }
   }

   public void loadSprite(BufferedImage[] images, AnimationMetadataSection meta) throws IOException {
      this.resetSprite();
      int width = images[0].getWidth();
      int height = images[0].getHeight();
      this.width = width;
      this.height = height;
      if (this.spriteSingle != null) {
         this.spriteSingle.width = this.width;
         this.spriteSingle.height = this.height;
      }

      int[][] intses = new int[images.length][];

      for (int i = 0; i < images.length; i++) {
         BufferedImage bufferedImage = images[i];
         if (bufferedImage != null) {
            if (this.width >> i != bufferedImage.getWidth()) {
               bufferedImage = TextureUtils.scaleImage(bufferedImage, this.width >> i);
            }

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

      if (meta == null) {
         if (height != width) {
            throw new RuntimeException("broken aspect ratio and not an animation");
         }

         this.framesTextureData.add(intses);
      } else {
         int n = height / width;
         int n2 = width;
         int n3 = width;
         this.height = this.width;
         if (meta.getFrameCount() > 0) {
            for (int n4 : meta.getFrameIndexSet()) {
               if (n4 >= n) {
                  throw new RuntimeException("invalid frameindex " + n4);
               }

               this.allocateFrameTextureData(n4);
               this.framesTextureData.set(n4, getFrameTextureData(intses, n2, n3, n4));
            }

            this.animationMetadata = meta;
         } else {
            ArrayList newArrayList = Lists.newArrayList();

            for (int i = 0; i < n; i++) {
               this.framesTextureData.add(getFrameTextureData(intses, n2, n3, i));
               newArrayList.add(new AnimationFrame(i, -1));
            }

            this.animationMetadata = new AnimationMetadataSection(newArrayList, this.width, this.height, meta.getFrameTime(), meta.isInterpolate());
         }
      }

      if (!this.isShadersSprite) {
         if (Config.isShaders()) {
            this.loadShadersSprites();
         }

         for (int i = 0; i < this.framesTextureData.size(); i++) {
            int[][] intses2 = this.framesTextureData.get(i);
            if (intses2 != null && !this.iconName.startsWith("minecraft:blocks/leaves_")) {
               for (int j = 0; j < intses2.length; j++) {
                  int[] ints = intses2[j];
                  this.fixTransparentColor(ints);
               }
            }
         }

         if (this.spriteSingle != null) {
            this.spriteSingle.loadSprite(images, meta);
         }
      }
   }

   public void generateMipmaps(int level) {
      ArrayList newArrayList = Lists.newArrayList();

      for (int i = 0; i < this.framesTextureData.size(); i++) {
         final int[][] intses = this.framesTextureData.get(i);
         if (intses != null) {
            try {
               newArrayList.add(TextureUtil.generateMipmapData(level, this.width, intses));
            } catch (Throwable e) {
               CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Generating mipmaps for frame");
               CrashReportCategory makeCategory = makeCrashReport.makeCategory("Frame being iterated");
               makeCategory.addCrashSection("Frame index", i);
               makeCategory.addCrashSectionCallable("Frame sizes", new Callable<String>() {
                  public String call() throws Exception {
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
      if (this.spriteSingle != null) {
         this.spriteSingle.generateMipmaps(level);
      }
   }

   public void allocateFrameTextureData(int index) {
      if (this.framesTextureData.size() <= index) {
         for (int size = this.framesTextureData.size(); size <= index; size++) {
            this.framesTextureData.add((int[][])null);
         }
      }

      if (this.spriteSingle != null) {
         this.spriteSingle.allocateFrameTextureData(index);
      }
   }

   public static int[][] getFrameTextureData(int[][] data, int rows, int columns, int n) {
      int[][] intses = new int[data.length][];

      for (int i = 0; i < data.length; i++) {
         int[] ints = data[i];
         if (ints != null) {
            intses[i] = new int[(rows >> i) * (columns >> i)];
            System.arraycopy(ints, n * intses[i].length, intses[i], 0, intses[i].length);
         }
      }

      return intses;
   }

   public void clearFramesTextureData() {
      if (this.spriteSingle != null) {
         this.spriteSingle.clearFramesTextureData();
      }
   }

   public boolean hasAnimationMetadata() {
      return this.animationMetadata != null;
   }

   public void setFramesTextureData(List<int[][]> newFramesTextureData) {
      this.framesTextureData = newFramesTextureData;
      if (this.spriteSingle != null) {
         this.spriteSingle.setFramesTextureData(newFramesTextureData);
      }
   }

   public void resetSprite() {
      this.animationMetadata = null;
      this.setFramesTextureData(Lists.newArrayList());
      this.frameCounter = 0;
      this.tickCounter = 0;
      if (this.spriteSingle != null) {
         this.spriteSingle.resetSprite();
      }
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

   public boolean hasCustomLoader(IResourceManager resourceManager, ResourceLocation resourceLocation) {
      return false;
   }

   public boolean load(IResourceManager resourceManager, ResourceLocation resourceLocation) {
      return true;
   }

   public int getIndexInMap() {
      return this.indexInMap;
   }

   public void setIndexInMap(int n) {
      this.indexInMap = n;
   }

   public void updateIndexInMap(CounterInt counterInt) {
      if (this.indexInMap < 0) {
         this.indexInMap = counterInt.nextValue();
      }
   }

   public int getAnimationIndex() {
      return this.animationIndex;
   }

   public void setAnimationIndex(int n) {
      this.animationIndex = n;
      if (this.spriteNormal != null) {
         this.spriteNormal.setAnimationIndex(n);
      }

      if (this.spriteSpecular != null) {
         this.spriteSpecular.setAnimationIndex(n);
      }
   }

   public boolean isAnimationActive() {
      return this.animationActive;
   }

   public void fixTransparentColor(int[] ints) {
      if (ints != null) {
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

   public double getSpriteU16(float f) {
      float f2 = this.maxU - this.minU;
      return (f - this.minU) / f2 * 16.0F;
   }

   public double getSpriteV16(float f) {
      float f2 = this.maxV - this.minV;
      return (f - this.minV) / f2 * 16.0F;
   }

   public void bindSpriteTexture() {
      if (this.glSpriteTextureId < 0) {
         this.glSpriteTextureId = TextureUtil.glGenTextures();
         TextureUtil.allocateTextureImpl(this.glSpriteTextureId, this.mipmapLevels, this.width, this.height);
         TextureUtils.applyAnisotropicLevel();
      }

      TextureUtils.bindTexture(this.glSpriteTextureId);
   }

   public void deleteSpriteTexture() {
      if (this.glSpriteTextureId >= 0) {
         TextureUtil.deleteTexture(this.glSpriteTextureId);
         this.glSpriteTextureId = -1;
      }
   }

   public float toSingleU(float f) {
      f -= this.baseU;
      float f2 = (float)this.sheetWidth / this.width;
      return f * f2;
   }

   public float toSingleV(float f) {
      f -= this.baseV;
      float f2 = (float)this.sheetHeight / this.height;
      return f * f2;
   }

   public List<int[][]> getFramesTextureData() {
      ArrayList list = new ArrayList();
      list.addAll(this.framesTextureData);
      return list;
   }

   public AnimationMetadataSection getAnimationMetadata() {
      return this.animationMetadata;
   }

   public void setAnimationMetadata(AnimationMetadataSection animationMetadataSection) {
      this.animationMetadata = animationMetadataSection;
   }

   public void loadShadersSprites() {
      if (Shaders.configNormalMap) {
         String str = this.iconName + "_n";
         ResourceLocation resourceLocation = new ResourceLocation(str);
         resourceLocation = Config.getTextureMap().completeResourceLocation(resourceLocation);
         if (Config.hasResource(resourceLocation)) {
            this.spriteNormal = new TextureAtlasSprite(str);
            this.spriteNormal.isShadersSprite = true;
            this.spriteNormal.copyFrom(this);
            this.spriteNormal.generateMipmaps(this.mipmapLevels);
         }
      }

      if (Shaders.configSpecularMap) {
         String str = this.iconName + "_s";
         ResourceLocation resourceLocation = new ResourceLocation(str);
         resourceLocation = Config.getTextureMap().completeResourceLocation(resourceLocation);
         if (Config.hasResource(resourceLocation)) {
            this.spriteSpecular = new TextureAtlasSprite(str);
            this.spriteSpecular.isShadersSprite = true;
            this.spriteSpecular.copyFrom(this);
            this.spriteSpecular.generateMipmaps(this.mipmapLevels);
         }
      }
   }
}
