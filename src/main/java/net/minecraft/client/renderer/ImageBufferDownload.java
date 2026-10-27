package net.minecraft.client.renderer;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.ImageObserver;

public class ImageBufferDownload implements IImageBuffer {
   public int[] imageData;
   public int imageWidth;
   public int imageHeight;

   @Override
   public BufferedImage parseUserSkin(BufferedImage image) {
      if (image == null) {
         return null;
      }

      this.imageWidth = 64;
      this.imageHeight = 64;
      int width = image.getWidth();
      int height = image.getHeight();

      int n;
      for (n = 1; this.imageWidth < width || this.imageHeight < height; n *= 2) {
         this.imageWidth *= 2;
         this.imageHeight *= 2;
      }

      BufferedImage bufferedImage = new BufferedImage(this.imageWidth, this.imageHeight, 2);
      Graphics graphics = bufferedImage.getGraphics();
      graphics.drawImage(image, 0, 0, (ImageObserver)null);
      if (image.getHeight() == 32 * n) {
         graphics.drawImage(bufferedImage, 24 * n, 48 * n, 20 * n, 52 * n, 4 * n, 16 * n, 8 * n, 20 * n, (ImageObserver)null);
         graphics.drawImage(bufferedImage, 28 * n, 48 * n, 24 * n, 52 * n, 8 * n, 16 * n, 12 * n, 20 * n, (ImageObserver)null);
         graphics.drawImage(bufferedImage, 20 * n, 52 * n, 16 * n, 64 * n, 8 * n, 20 * n, 12 * n, 32 * n, (ImageObserver)null);
         graphics.drawImage(bufferedImage, 24 * n, 52 * n, 20 * n, 64 * n, 4 * n, 20 * n, 8 * n, 32 * n, (ImageObserver)null);
         graphics.drawImage(bufferedImage, 28 * n, 52 * n, 24 * n, 64 * n, 0 * n, 20 * n, 4 * n, 32 * n, (ImageObserver)null);
         graphics.drawImage(bufferedImage, 32 * n, 52 * n, 28 * n, 64 * n, 12 * n, 20 * n, 16 * n, 32 * n, (ImageObserver)null);
         graphics.drawImage(bufferedImage, 40 * n, 48 * n, 36 * n, 52 * n, 44 * n, 16 * n, 48 * n, 20 * n, (ImageObserver)null);
         graphics.drawImage(bufferedImage, 44 * n, 48 * n, 40 * n, 52 * n, 48 * n, 16 * n, 52 * n, 20 * n, (ImageObserver)null);
         graphics.drawImage(bufferedImage, 36 * n, 52 * n, 32 * n, 64 * n, 48 * n, 20 * n, 52 * n, 32 * n, (ImageObserver)null);
         graphics.drawImage(bufferedImage, 40 * n, 52 * n, 36 * n, 64 * n, 44 * n, 20 * n, 48 * n, 32 * n, (ImageObserver)null);
         graphics.drawImage(bufferedImage, 44 * n, 52 * n, 40 * n, 64 * n, 40 * n, 20 * n, 44 * n, 32 * n, (ImageObserver)null);
         graphics.drawImage(bufferedImage, 48 * n, 52 * n, 44 * n, 64 * n, 52 * n, 20 * n, 56 * n, 32 * n, (ImageObserver)null);
      }

      graphics.dispose();
      this.imageData = ((DataBufferInt)bufferedImage.getRaster().getDataBuffer()).getData();
      this.setAreaOpaque(0 * n, 0 * n, 32 * n, 16 * n);
      this.setAreaTransparent(32 * n, 0 * n, 64 * n, 32 * n);
      this.setAreaOpaque(0 * n, 16 * n, 64 * n, 32 * n);
      this.setAreaTransparent(0 * n, 32 * n, 16 * n, 48 * n);
      this.setAreaTransparent(16 * n, 32 * n, 40 * n, 48 * n);
      this.setAreaTransparent(40 * n, 32 * n, 56 * n, 48 * n);
      this.setAreaTransparent(0 * n, 48 * n, 16 * n, 64 * n);
      this.setAreaOpaque(16 * n, 48 * n, 48 * n, 64 * n);
      this.setAreaTransparent(48 * n, 48 * n, 64 * n, 64 * n);
      return bufferedImage;
   }

   @Override
   public void skinAvailable() {
   }

   public void setAreaTransparent(int n, int n2, int n3, int n4) {
      if (!this.hasTransparency(n, n2, n3, n4)) {
         for (int i = n; i < n3; i++) {
            for (int j = n2; j < n4; j++) {
               this.imageData[i + j * this.imageWidth] = this.imageData[i + j * this.imageWidth] & 16777215;
            }
         }
      }
   }

   public void setAreaOpaque(int n, int n2, int n3, int n4) {
      for (int i = n; i < n3; i++) {
         for (int j = n2; j < n4; j++) {
            this.imageData[i + j * this.imageWidth] = this.imageData[i + j * this.imageWidth] | 0xFF000000;
         }
      }
   }

   public boolean hasTransparency(int n, int n2, int n3, int n4) {
      for (int i = n; i < n3; i++) {
         for (int j = n2; j < n4; j++) {
            int n5 = this.imageData[i + j * this.imageWidth];
            if ((n5 >> 24 & 0xFF) < 128) {
               return true;
            }
         }
      }

      return false;
   }
}
