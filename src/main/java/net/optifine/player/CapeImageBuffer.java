package net.optifine.player;

import java.awt.image.BufferedImage;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.util.ResourceLocation;

public class CapeImageBuffer extends ImageBufferDownload {
   public AbstractClientPlayer player;
   public ResourceLocation resourceLocation;
   public boolean elytraOfCape;

   public CapeImageBuffer(AbstractClientPlayer abstractClientPlayer, ResourceLocation resourceLocation) {
      this.player = abstractClientPlayer;
      this.resourceLocation = resourceLocation;
   }

   @Override
   public BufferedImage parseUserSkin(BufferedImage bufferedImage) {
      BufferedImage parseCape = CapeUtils.parseCape(bufferedImage);
      this.elytraOfCape = CapeUtils.isElytraCape(bufferedImage, parseCape);
      return parseCape;
   }

   @Override
   public void skinAvailable() {
      if (this.player != null) {
         this.player.setLocationOfCape(this.resourceLocation);
         this.player.setElytraOfCape(this.elytraOfCape);
      }

      this.cleanup();
   }

   public void cleanup() {
      this.player = null;
   }

   public boolean isElytraOfCape() {
      return this.elytraOfCape;
   }
}
