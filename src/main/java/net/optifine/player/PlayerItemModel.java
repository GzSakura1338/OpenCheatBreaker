package net.optifine.player;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.optifine.Config;

public class PlayerItemModel {
   public Dimension textureSize = null;
   public boolean usePlayerTexture = false;
   public PlayerItemRenderer[] modelRenderers = new PlayerItemRenderer[0];
   public ResourceLocation textureLocation = null;
   public BufferedImage textureImage = null;
   public DynamicTexture texture = null;
   public ResourceLocation locationMissing = new ResourceLocation("textures/blocks/wool_colored_red.png");
   public static final int ATTACH_BODY = 0;
   public static final int ATTACH_HEAD = 1;
   public static final int ATTACH_LEFT_ARM = 2;
   public static final int ATTACH_RIGHT_ARM = 3;
   public static final int ATTACH_LEFT_LEG = 4;
   public static final int ATTACH_RIGHT_LEG = 5;
   public static final int ATTACH_CAPE = 6;

   public PlayerItemModel(Dimension dimension, boolean flag, PlayerItemRenderer[] playerItemRenderers) {
      this.textureSize = dimension;
      this.usePlayerTexture = flag;
      this.modelRenderers = playerItemRenderers;
   }

   public void render(ModelBiped modelBiped, AbstractClientPlayer abstractClientPlayer, float f, float f2) {
      TextureManager textureManager = Config.getTextureManager();
      if (this.usePlayerTexture) {
         textureManager.bindTexture(abstractClientPlayer.getLocationSkin());
      } else if (this.textureLocation != null) {
         if (this.texture == null && this.textureImage != null) {
            this.texture = new DynamicTexture(this.textureImage);
            Minecraft.getMinecraft().getTextureManager().loadTexture(this.textureLocation, this.texture);
         }

         textureManager.bindTexture(this.textureLocation);
      } else {
         textureManager.bindTexture(this.locationMissing);
      }

      for (int i = 0; i < this.modelRenderers.length; i++) {
         PlayerItemRenderer playerItemRenderer = this.modelRenderers[i];
         GlStateManager.pushMatrix();
         if (abstractClientPlayer.isSneaking()) {
            GlStateManager.translate(0.0F, 0.2F, 0.0F);
         }

         playerItemRenderer.render(modelBiped, f);
         GlStateManager.popMatrix();
      }
   }

   public static ModelRenderer getAttachModel(ModelBiped modelBiped, int n) {
      switch (n) {
         case 0:
            return modelBiped.bipedBody;
         case 1:
            return modelBiped.bipedHead;
         case 2:
            return modelBiped.bipedLeftArm;
         case 3:
            return modelBiped.bipedRightArm;
         case 4:
            return modelBiped.bipedLeftLeg;
         case 5:
            return modelBiped.bipedRightLeg;
         default:
            return null;
      }
   }

   public BufferedImage getTextureImage() {
      return this.textureImage;
   }

   public void setTextureImage(BufferedImage bufferedImage) {
      this.textureImage = bufferedImage;
   }

   public DynamicTexture getTexture() {
      return this.texture;
   }

   public ResourceLocation getTextureLocation() {
      return this.textureLocation;
   }

   public void setTextureLocation(ResourceLocation resourceLocation) {
      this.textureLocation = resourceLocation;
   }

   public boolean isUsePlayerTexture() {
      return this.usePlayerTexture;
   }
}
