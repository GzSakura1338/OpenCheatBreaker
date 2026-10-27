package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelEnderMite;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.util.ResourceLocation;

public class RenderEndermite extends RenderLiving<EntityEndermite> {
   public static ResourceLocation ENDERMITE_TEXTURES = new ResourceLocation("textures/entity/endermite.png");

   public RenderEndermite(RenderManager renderManagerIn) {
      super(renderManagerIn, new ModelEnderMite(), 0.3F);
   }

   public float getDeathMaxRotation(EntityEndermite entityLivingBaseIn) {
      return 180.0F;
   }

   public ResourceLocation getEntityTexture(EntityEndermite entity) {
      return ENDERMITE_TEXTURES;
   }
}
