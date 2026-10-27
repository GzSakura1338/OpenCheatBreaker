package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class RenderChicken extends RenderLiving<EntityChicken> {
   public static ResourceLocation chickenTextures = new ResourceLocation("textures/entity/chicken.png");

   public RenderChicken(RenderManager renderManagerIn, ModelBase modelBaseIn, float shadowSizeIn) {
      super(renderManagerIn, modelBaseIn, shadowSizeIn);
   }

   public ResourceLocation getEntityTexture(EntityChicken entity) {
      return chickenTextures;
   }

   public float handleRotationFloat(EntityChicken livingBase, float partialTicks) {
      float f = livingBase.prevFlapProgress + (livingBase.wingRotation - livingBase.prevFlapProgress) * partialTicks;
      float f2 = livingBase.prevMaxWingDeviation + (livingBase.destPos - livingBase.prevMaxWingDeviation) * partialTicks;
      return (MathHelper.sin(f) + 1.0F) * f2;
   }
}
