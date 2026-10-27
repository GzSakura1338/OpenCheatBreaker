package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.util.ResourceLocation;

public class RenderCaveSpider extends RenderSpider<EntityCaveSpider> {
   public static ResourceLocation caveSpiderTextures = new ResourceLocation("textures/entity/spider/cave_spider.png");

   public RenderCaveSpider(RenderManager renderManagerIn) {
      super(renderManagerIn);
      this.shadowSize *= 0.7F;
   }

   public void preRenderCallback(EntityCaveSpider entitylivingbaseIn, float partialTickTime) {
      GlStateManager.scale(0.7F, 0.7F, 0.7F);
   }

   public ResourceLocation getEntityTexture(EntityCaveSpider entity) {
      return caveSpiderTextures;
   }
}
