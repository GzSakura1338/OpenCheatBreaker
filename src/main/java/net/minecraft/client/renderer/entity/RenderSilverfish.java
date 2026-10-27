package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelSilverfish;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.util.ResourceLocation;

public class RenderSilverfish extends RenderLiving<EntitySilverfish> {
   public static ResourceLocation silverfishTextures = new ResourceLocation("textures/entity/silverfish.png");

   public RenderSilverfish(RenderManager renderManagerIn) {
      super(renderManagerIn, new ModelSilverfish(), 0.3F);
   }

   public float getDeathMaxRotation(EntitySilverfish entityLivingBaseIn) {
      return 180.0F;
   }

   public ResourceLocation getEntityTexture(EntitySilverfish entity) {
      return silverfishTextures;
   }
}
