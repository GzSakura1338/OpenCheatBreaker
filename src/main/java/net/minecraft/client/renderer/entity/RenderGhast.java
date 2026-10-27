package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelGhast;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.util.ResourceLocation;

public class RenderGhast extends RenderLiving<EntityGhast> {
   public static ResourceLocation ghastTextures = new ResourceLocation("textures/entity/ghast/ghast.png");
   public static ResourceLocation ghastShootingTextures = new ResourceLocation("textures/entity/ghast/ghast_shooting.png");

   public RenderGhast(RenderManager renderManagerIn) {
      super(renderManagerIn, new ModelGhast(), 0.5F);
   }

   public ResourceLocation getEntityTexture(EntityGhast entity) {
      return entity.isAttacking() ? ghastShootingTextures : ghastTextures;
   }

   public void preRenderCallback(EntityGhast entitylivingbaseIn, float partialTickTime) {
      float f = 1.0F;
      float f2 = (8.0F + f) / 2.0F;
      float f3 = (8.0F + 1.0F / f) / 2.0F;
      GlStateManager.scale(f3, f2, f3);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
   }
}
