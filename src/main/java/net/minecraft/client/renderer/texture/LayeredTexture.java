package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.optifine.Config;
import net.optifine.shaders.ShadersTex;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LayeredTexture extends AbstractTexture {
   public static Logger logger = LogManager.getLogger();
   public List<String> layeredTextureNames;
   public ResourceLocation textureLocation;

   public LayeredTexture(String... textureNames) {
      this.layeredTextureNames = Lists.newArrayList(textureNames);
      if (textureNames.length > 0 && textureNames[0] != null) {
         this.textureLocation = new ResourceLocation(textureNames[0]);
      }
   }

   @Override
   public void loadTexture(IResourceManager resourceManager) throws IOException {
      this.deleteGlTexture();
      BufferedImage bufferedImage = null;

      try {
         for (String str : this.layeredTextureNames) {
            if (str != null) {
               InputStream inputStream = resourceManager.getResource(new ResourceLocation(str)).getInputStream();
               BufferedImage readBufferedImage = TextureUtil.readBufferedImage(inputStream);
               if (bufferedImage == null) {
                  bufferedImage = new BufferedImage(readBufferedImage.getWidth(), readBufferedImage.getHeight(), 2);
               }

               bufferedImage.getGraphics().drawImage(readBufferedImage, 0, 0, (ImageObserver)null);
            }
         }
      } catch (IOException e) {
         logger.error("Couldn't load layered image", e);
         return;
      }

      if (Config.isShaders()) {
         ShadersTex.loadSimpleTexture(this.getGlTextureId(), bufferedImage, false, false, resourceManager, this.textureLocation, this.getMultiTexID());
      } else {
         TextureUtil.uploadTextureImage(this.getGlTextureId(), bufferedImage);
      }
   }
}
