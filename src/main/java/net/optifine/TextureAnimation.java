package net.optifine;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Properties;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.util.ResourceLocation;
import net.optifine.util.TextureUtils;
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
   public int currentFrameIndex = 0;
   public boolean interpolate = false;
   public int interpolateSkip = 0;
   public ByteBuffer interpolateData = null;
   public byte[] srcData = null;
   public ByteBuffer imageData = null;
   public boolean active = true;
   public boolean valid = true;

   public TextureAnimation(String str, byte[] bytes, String str2, ResourceLocation resourceLocation, int n, int n2, int n3, int n4, Properties properties) {
      this.srcTex = str;
      this.dstTex = str2;
      this.dstTexLoc = resourceLocation;
      this.dstX = n;
      this.dstY = n2;
      this.frameWidth = n3;
      this.frameHeight = n4;
      int n5 = n3 * n4 * 4;
      if (bytes.length % n5 != 0) {
         Config.warn("Invalid animated texture length: " + bytes.length + ", frameWidth: " + n3 + ", frameHeight: " + n4);
      }

      this.srcData = bytes;
      int n6 = bytes.length / n5;
      if (properties.get("tile.0") != null) {
         for (int i = 0; properties.get("tile." + i) != null; i++) {
            n6 = i + 1;
         }
      }

      String str3 = (String)properties.get("duration");
      int max = Math.max(Config.parseInt(str3, 1), 1);
      this.frames = new TextureAnimationFrame[n6];

      for (int i = 0; i < this.frames.length; i++) {
         String str4 = (String)properties.get("tile." + i);
         int parseInt = Config.parseInt(str4, i);
         String str5 = (String)properties.get("duration." + i);
         int max2 = Math.max(Config.parseInt(str5, max), 1);
         TextureAnimationFrame textureAnimationFrame = new TextureAnimationFrame(parseInt, max2);
         this.frames[i] = textureAnimationFrame;
      }

      this.interpolate = Config.parseBoolean(properties.getProperty("interpolate"), false);
      this.interpolateSkip = Config.parseInt(properties.getProperty("skip"), 0);
      if (this.interpolate) {
         this.interpolateData = GLAllocation.createDirectByteBuffer(n5);
      }
   }

   public boolean nextFrame() {
      TextureAnimationFrame currentFrame = this.getCurrentFrame();
      if (currentFrame == null) {
         return false;
      }

      currentFrame.counter++;
      if (currentFrame.counter < currentFrame.duration) {
         return this.interpolate;
      }

      currentFrame.counter = 0;
      this.currentFrameIndex++;
      if (this.currentFrameIndex >= this.frames.length) {
         this.currentFrameIndex = 0;
      }

      return true;
   }

   public TextureAnimationFrame getCurrentFrame() {
      return this.getFrame(this.currentFrameIndex);
   }

   public TextureAnimationFrame getFrame(int n) {
      if (this.frames.length <= 0) {
         return null;
      }

      if (n < 0 || n >= this.frames.length) {
         n = 0;
      }

      return this.frames[n];
   }

   public int getFrameCount() {
      return this.frames.length;
   }

   public void updateTexture() {
      if (this.valid) {
         if (this.dstTextId < 0) {
            ITextureObject texture = TextureUtils.getTexture(this.dstTexLoc);
            if (texture == null) {
               this.valid = false;
               return;
            }

            this.dstTextId = texture.getGlTextureId();
         }

         if (this.imageData == null) {
            this.imageData = GLAllocation.createDirectByteBuffer(this.srcData.length);
            this.imageData.put(this.srcData);
            ((Buffer)this.imageData).flip();
            this.srcData = null;
         }

         this.active = SmartAnimations.isActive() ? SmartAnimations.isTextureRendered(this.dstTextId) : true;
         if (this.nextFrame() && this.active) {
            int n = this.frameWidth * this.frameHeight * 4;
            TextureAnimationFrame currentFrame = this.getCurrentFrame();
            if (currentFrame != null) {
               int n2 = n * currentFrame.index;
               if (n2 + n <= this.imageData.limit()) {
                  if (!this.interpolate || currentFrame.counter <= 0) {
                     ((Buffer)this.imageData).position(n2);
                     GlStateManager.bindTexture(this.dstTextId);
                     GL11.glTexSubImage2D(3553, 0, this.dstX, this.dstY, this.frameWidth, this.frameHeight, 6408, 5121, this.imageData);
                  } else if (this.interpolateSkip <= 1 || currentFrame.counter % this.interpolateSkip == 0) {
                     TextureAnimationFrame frame = this.getFrame(this.currentFrameIndex + 1);
                     double d = 1.0 * currentFrame.counter / currentFrame.duration;
                     this.updateTextureInerpolate(currentFrame, frame, d);
                  }
               }
            }
         }
      }
   }

   public void updateTextureInerpolate(TextureAnimationFrame textureAnimationFrame, TextureAnimationFrame textureAnimationFrame2, double d) {
      int n = this.frameWidth * this.frameHeight * 4;
      int n2 = n * textureAnimationFrame.index;
      if (n2 + n <= this.imageData.limit()) {
         int n3 = n * textureAnimationFrame2.index;
         if (n3 + n <= this.imageData.limit()) {
            ((Buffer)this.interpolateData).clear();

            for (int i = 0; i < n; i++) {
               int n4 = this.imageData.get(n2 + i) & 255;
               int n5 = this.imageData.get(n3 + i) & 255;
               int mix = this.mix(n4, n5, d);
               byte b = (byte)mix;
               this.interpolateData.put(b);
            }

            ((Buffer)this.interpolateData).flip();
            GlStateManager.bindTexture(this.dstTextId);
            GL11.glTexSubImage2D(3553, 0, this.dstX, this.dstY, this.frameWidth, this.frameHeight, 6408, 5121, this.interpolateData);
         }
      }
   }

   public int mix(int n, int n2, double d) {
      return (int)(n * (1.0 - d) + n2 * d);
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

   public boolean isActive() {
      return this.active;
   }
}
