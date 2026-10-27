package com.cheatbreaker.client.minimap;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.lwjgl.opengl.GL11;

public class GlBufferedImage extends BufferedImage {
   public int textureId = 0;
   public Object dataLock = new Object();
   public byte[] pixels = ((DataBufferByte)this.getRaster().getDataBuffer()).getData();
   public ByteBuffer pixelBuffer = ByteBuffer.allocateDirect(this.pixels.length).order(ByteOrder.nativeOrder());

   public void shiftX(int n) {
      synchronized (this.dataLock) {
         if (n > 0) {
            System.arraycopy(this.pixels, n * 4, this.pixels, 0, this.pixels.length - n * 4);
         } else if (n < 0) {
            System.arraycopy(this.pixels, 0, this.pixels, -n * 4, this.pixels.length + n * 4);
         }
      }
   }

   @Override
   public void setRGB(int n, int n2, int n3) {
      int n4 = (n + n2 * this.getWidth()) * 4;
      synchronized (this.dataLock) {
         int n5 = n3 >> 24 & 0xFF;
         this.pixels[n4] = (byte)((n3 >> 0 & 0xFF) * n5 / 255);
         this.pixels[n4 + 1] = (byte)((n3 >> 8 & 0xFF) * n5 / 255);
         this.pixels[n4 + 2] = (byte)((n3 >> 16 & 0xFF) * n5 / 255);
         this.pixels[n4 + 3] = -1;
      }
   }

   public GlBufferedImage(int n, int n2, int n3) {
      super(n, n2, n3);
   }

   public void deleteTexture() {
      if (this.textureId != 0) {
         GL11.glDeleteTextures(this.textureId);
      }
   }

   public void shiftZ(int n) {
      synchronized (this.dataLock) {
         if (n > 0) {
            System.arraycopy(this.pixels, n * this.getWidth() * 4, this.pixels, 0, this.pixels.length - n * this.getWidth() * 4);
         } else if (n < 0) {
            System.arraycopy(this.pixels, 0, this.pixels, -n * this.getWidth() * 4, this.pixels.length + n * this.getWidth() * 4);
         }
      }
   }

   public void uploadTexture() {
      if (this.textureId != 0) {
         GL11.glDeleteTextures(this.textureId);
      } else {
         this.textureId = GL11.glGenTextures();
      }

      ((Buffer)this.pixelBuffer).clear();
      synchronized (this.dataLock) {
         this.pixelBuffer.put(this.pixels);
      }

      ((Buffer)this.pixelBuffer).position(0).limit(this.pixels.length);
      if (!GlUtils.stencilSupported && !GlUtils.framebufferSupported) {
         if (MinimapSettings.instance.squareMap) {
            for (int i = 0; i < this.getWidth(); i++) {
               this.pixelBuffer.put(i * 4 + 3, (byte)0);
               this.pixelBuffer.put(i * this.getWidth() * 4 + 3, (byte)0);
            }
         }

         if (MinimapSettings.instance.squareMap && (MinimapSettings.instance.zoom > 0 || AbstractVoxelMap.instance.getMap().getPercentX() > 1.0F)) {
            for (int i = 0; i < this.getWidth(); i++) {
               this.pixelBuffer.put(i * this.getWidth() * 4 + 7, (byte)0);
            }
         }

         if (MinimapSettings.instance.squareMap && (MinimapSettings.instance.zoom > 0 || AbstractVoxelMap.instance.getMap().getPercentY() > 1.0F)) {
            for (int i = 0; i < this.getWidth(); i++) {
               this.pixelBuffer.put(i * 4 + 3 + this.getWidth() * 4, (byte)0);
            }
         }
      }

      GL11.glBindTexture(3553, this.textureId);
      GL11.glTexParameteri(3553, 10241, 9728);
      GL11.glTexParameteri(3553, 10240, 9728);
      GL11.glTexParameteri(3553, 10242, 33071);
      GL11.glTexParameteri(3553, 10243, 33071);
      GL11.glTexImage2D(3553, 0, 6408, this.getWidth(), this.getHeight(), 0, 32993, 5121, this.pixelBuffer);
   }

   public void clear() {
      for (int i = 0; i < this.pixels.length; i++) {
         this.pixels[i] = 0;
      }

      this.uploadTexture();
   }
}
