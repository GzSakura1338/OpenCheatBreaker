package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.util.ResourceLocation;

public class RenderSquid extends RenderLiving<EntitySquid> {
   public static ResourceLocation squidTextures = new ResourceLocation("textures/entity/squid.png");

   public RenderSquid(RenderManager renderManagerIn, ModelBase modelBaseIn, float shadowSizeIn) {
      super(renderManagerIn, modelBaseIn, shadowSizeIn);
   }

   public ResourceLocation getEntityTexture(EntitySquid entity) {
      return squidTextures;
   }

   public void rotateCorpse(EntitySquid bat, float f, float f2, float partialTicks) {
      float f3 = bat.prevSquidPitch + (bat.squidPitch - bat.prevSquidPitch) * partialTicks;
      float f4 = bat.prevSquidYaw + (bat.squidYaw - bat.prevSquidYaw) * partialTicks;
      GlStateManager.translate(0.0F, 0.5F, 0.0F);
      GlStateManager.rotate(180.0F - f2, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(f3, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate(f4, 0.0F, 1.0F, 0.0F);
      GlStateManager.translate(0.0F, -1.2F, 0.0F);
   }

   public float handleRotationFloat(EntitySquid livingBase, float partialTicks) {
      return livingBase.lastTentacleAngle + (livingBase.tentacleAngle - livingBase.lastTentacleAngle) * partialTicks;
   }
}
