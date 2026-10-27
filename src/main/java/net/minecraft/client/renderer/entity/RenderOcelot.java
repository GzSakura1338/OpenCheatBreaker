package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.util.ResourceLocation;

public class RenderOcelot extends RenderLiving<EntityOcelot> {
   public static ResourceLocation blackOcelotTextures = new ResourceLocation("textures/entity/cat/black.png");
   public static ResourceLocation ocelotTextures = new ResourceLocation("textures/entity/cat/ocelot.png");
   public static ResourceLocation redOcelotTextures = new ResourceLocation("textures/entity/cat/red.png");
   public static ResourceLocation siameseOcelotTextures = new ResourceLocation("textures/entity/cat/siamese.png");

   public RenderOcelot(RenderManager renderManagerIn, ModelBase modelBaseIn, float shadowSizeIn) {
      super(renderManagerIn, modelBaseIn, shadowSizeIn);
   }

   public ResourceLocation getEntityTexture(EntityOcelot entity) {
      switch (entity.getTameSkin()) {
         case 0:
         default:
            return ocelotTextures;
         case 1:
            return blackOcelotTextures;
         case 2:
            return redOcelotTextures;
         case 3:
            return siameseOcelotTextures;
      }
   }

   public void preRenderCallback(EntityOcelot entitylivingbaseIn, float partialTickTime) {
      super.preRenderCallback(entitylivingbaseIn, partialTickTime);
      if (entitylivingbaseIn.isTamed()) {
         GlStateManager.scale(0.8F, 0.8F, 0.8F);
      }
   }
}
