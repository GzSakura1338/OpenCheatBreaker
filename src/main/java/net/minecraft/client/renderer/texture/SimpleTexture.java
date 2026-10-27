package net.minecraft.client.renderer.texture;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.util.ResourceLocation;
import net.optifine.Config;
import net.optifine.EmissiveTextures;
import net.optifine.shaders.ShadersTex;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SimpleTexture extends AbstractTexture {
   public static Logger logger = LogManager.getLogger();
   public ResourceLocation textureLocation;
   public ResourceLocation locationEmissive;
   public boolean isEmissive;

   public SimpleTexture(ResourceLocation textureResourceLocation) {
      this.textureLocation = textureResourceLocation;
   }

   @Override
   public void loadTexture(IResourceManager resourceManager) throws IOException {
      this.deleteGlTexture();
      InputStream inputStream = null;

      try {
         IResource resource = resourceManager.getResource(this.textureLocation);
         inputStream = resource.getInputStream();
         BufferedImage readBufferedImage = TextureUtil.readBufferedImage(inputStream);
         if (readBufferedImage == null) {
            logger.warn("BufferedImage is null for " + resource.getResourceLocation());
         }

         boolean flag = false;
         boolean flag2 = false;
         if (resource.hasMetadata()) {
            try {
               TextureMetadataSection metadata = resource.getMetadata("texture");
               if (metadata != null) {
                  flag = metadata.getTextureBlur();
                  flag2 = metadata.getTextureClamp();
               }
            } catch (RuntimeException e) {
               logger.warn("Failed reading metadata of: " + this.textureLocation, e);
            }
         }

         if (Config.isShaders()) {
            ShadersTex.loadSimpleTexture(this.getGlTextureId(), readBufferedImage, flag, flag2, resourceManager, this.textureLocation, this.getMultiTexID());
         } else {
            TextureUtil.uploadTextureImageAllocate(this.getGlTextureId(), readBufferedImage, flag, flag2);
         }

         if (EmissiveTextures.isActive()) {
            EmissiveTextures.loadTexture(this.textureLocation, this);
         }
      } catch (NullPointerException e) {
         logger.error("Something went null during texture loading: " + e.getMessage());
      } catch (IOException e) {
         logger.error("I/O Error during texture loading: " + e.getMessage());
      } catch (Exception e) {
         logger.error("Something went wrong during texture loading: " + e.getMessage());
      } finally {
         if (inputStream != null) {
            inputStream.close();
         }
      }
   }
}
