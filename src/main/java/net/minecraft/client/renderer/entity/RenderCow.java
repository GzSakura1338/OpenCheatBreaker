package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.util.ResourceLocation;

public class RenderCow extends RenderLiving<EntityCow> {
   public static ResourceLocation cowTextures = new ResourceLocation("textures/entity/cow/cow.png");

   public RenderCow(RenderManager renderManagerIn, ModelBase modelBaseIn, float shadowSizeIn) {
      super(renderManagerIn, modelBaseIn, shadowSizeIn);
   }

   public ResourceLocation getEntityTexture(EntityCow entity) {
      return cowTextures;
   }
}
