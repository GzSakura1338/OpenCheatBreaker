package net.minecraft.client.renderer.texture;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import net.minecraft.block.material.MapColor;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.optifine.Config;
import net.optifine.shaders.ShadersTex;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LayeredColorMaskTexture extends AbstractTexture {
   public static Logger LOG = LogManager.getLogger();
   public ResourceLocation textureLocation;
   public List<String> names;
   public List<EnumDyeColor> colors;

   public LayeredColorMaskTexture(ResourceLocation textureLocationIn, List<String> list, List<EnumDyeColor> list2) {
      this.textureLocation = textureLocationIn;
      this.names = list;
      this.colors = list2;
   }

   @Override
   public void loadTexture(IResourceManager resourceManager) throws IOException {
      this.deleteGlTexture();

      BufferedImage bufferedImage;
      try {
         BufferedImage readBufferedImage = TextureUtil.readBufferedImage(resourceManager.getResource(this.textureLocation).getInputStream());
         int type = readBufferedImage.getType();
         if (type == 0) {
            type = 6;
         }

         bufferedImage = new BufferedImage(readBufferedImage.getWidth(), readBufferedImage.getHeight(), type);
         Graphics graphics = bufferedImage.getGraphics();
         graphics.drawImage(readBufferedImage, 0, 0, (ImageObserver)null);

         for (int i = 0; i < 17 && i < this.names.size() && i < this.colors.size(); i++) {
            String str = this.names.get(i);
            MapColor mapColor = this.colors.get(i).getMapColor();
            if (str != null) {
               InputStream inputStream = resourceManager.getResource(new ResourceLocation(str)).getInputStream();
               BufferedImage readBufferedImage2 = TextureUtil.readBufferedImage(inputStream);
               if (readBufferedImage2.getWidth() == bufferedImage.getWidth() && readBufferedImage2.getHeight() == bufferedImage.getHeight() && readBufferedImage2.getType() == 6) {
                  for (int j = 0; j < readBufferedImage2.getHeight(); j++) {
                     for (int k = 0; k < readBufferedImage2.getWidth(); k++) {
                        int rGB = readBufferedImage2.getRGB(k, j);
                        if ((rGB & 0xFF000000) != 0) {
                           int n = (rGB & 0xFF0000) << 8 & 0xFF000000;
                           int rGB2 = readBufferedImage.getRGB(k, j);
                           int n2 = MathHelper.multiplyColors(rGB2, mapColor.colorValue) & 16777215;
                           readBufferedImage2.setRGB(k, j, n | n2);
                        }
                     }
                  }

                  bufferedImage.getGraphics().drawImage(readBufferedImage2, 0, 0, (ImageObserver)null);
               }
            }
         }
      } catch (IOException e) {
         LOG.error("Couldn't load layered image", e);
         return;
      }

      if (Config.isShaders()) {
         ShadersTex.loadSimpleTexture(this.getGlTextureId(), bufferedImage, false, false, resourceManager, this.textureLocation, this.getMultiTexID());
      } else {
         TextureUtil.uploadTextureImage(this.getGlTextureId(), bufferedImage);
      }
   }
}
