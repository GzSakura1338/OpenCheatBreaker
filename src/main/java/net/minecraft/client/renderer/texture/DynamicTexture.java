package net.minecraft.client.renderer.texture;

import java.awt.image.BufferedImage;
import java.io.IOException;
import net.minecraft.client.resources.IResourceManager;

public class DynamicTexture extends AbstractTexture {
   public int width;
   public int height;
   public int[] dynamicTextureData;

   public DynamicTexture(BufferedImage bufferedImage) {
      this(bufferedImage.getWidth(), bufferedImage.getHeight());
      bufferedImage.getRGB(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), this.dynamicTextureData, 0, bufferedImage.getWidth());
      this.updateDynamicTexture();
   }

   public DynamicTexture(int textureWidth, int textureHeight) {
      this.width = textureWidth;
      this.height = textureHeight;
      this.dynamicTextureData = new int[textureWidth * textureHeight];
      TextureUtil.allocateTexture(this.getGlTextureId(), textureWidth, textureHeight);
   }

   @Override
   public void loadTexture(IResourceManager resourceManager) throws IOException {
   }

   public void updateDynamicTexture() {
      TextureUtil.uploadTexture(this.getGlTextureId(), this.dynamicTextureData, this.width, this.height);
   }

   public int[] getTextureData() {
      return this.dynamicTextureData;
   }
}
