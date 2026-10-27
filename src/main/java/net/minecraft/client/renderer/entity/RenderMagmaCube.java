package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelMagmaCube;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.util.ResourceLocation;

public class RenderMagmaCube extends RenderLiving<EntityMagmaCube> {
   public static ResourceLocation magmaCubeTextures = new ResourceLocation("textures/entity/slime/magmacube.png");

   public RenderMagmaCube(RenderManager renderManagerIn) {
      super(renderManagerIn, new ModelMagmaCube(), 0.25F);
   }

   public ResourceLocation getEntityTexture(EntityMagmaCube entity) {
      return magmaCubeTextures;
   }

   public void preRenderCallback(EntityMagmaCube entitylivingbaseIn, float partialTickTime) {
      int slimeSize = entitylivingbaseIn.getSlimeSize();
      float f = (entitylivingbaseIn.prevSquishFactor + (entitylivingbaseIn.squishFactor - entitylivingbaseIn.prevSquishFactor) * partialTickTime)
         / (slimeSize * 0.5F + 1.0F);
      float f2 = 1.0F / (f + 1.0F);
      float f3 = slimeSize;
      GlStateManager.scale(f2 * f3, 1.0F / f2 * f3, f2 * f3);
   }
}
