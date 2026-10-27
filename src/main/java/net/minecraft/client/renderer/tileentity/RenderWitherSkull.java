package net.minecraft.client.renderer.tileentity;

import net.minecraft.client.model.ModelSkeletonHead;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.util.ResourceLocation;

public class RenderWitherSkull extends Render<EntityWitherSkull> {
   public static ResourceLocation invulnerableWitherTextures = new ResourceLocation("textures/entity/wither/wither_invulnerable.png");
   public static ResourceLocation witherTextures = new ResourceLocation("textures/entity/wither/wither.png");
   public ModelSkeletonHead skeletonHeadModel = new ModelSkeletonHead();

   public RenderWitherSkull(RenderManager renderManagerIn) {
      super(renderManagerIn);
   }

   public float computeFloat(float f, float f2, float f3) {
      float f4 = f2 - f;

      while (f4 < -180.0F) {
         f4 += 360.0F;
      }

      while (f4 >= 180.0F) {
         f4 -= 360.0F;
      }

      return f + f3 * f4;
   }

   public void doRender(EntityWitherSkull entity, double x, double y, double z, float entityYaw, float partialTicks) {
      GlStateManager.pushMatrix();
      GlStateManager.disableCull();
      float computeFloat = this.computeFloat(entity.prevRotationYaw, entity.rotationYaw, partialTicks);
      float f = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
      GlStateManager.translate((float)x, (float)y, (float)z);
      float f2 = 0.0625F;
      GlStateManager.enableRescaleNormal();
      GlStateManager.scale(-1.0F, -1.0F, 1.0F);
      GlStateManager.enableAlpha();
      this.bindEntityTexture(entity);
      this.skeletonHeadModel.render(entity, 0.0F, 0.0F, 0.0F, computeFloat, f, f2);
      GlStateManager.popMatrix();
      super.doRender(entity, x, y, z, entityYaw, partialTicks);
   }

   public ResourceLocation getEntityTexture(EntityWitherSkull entity) {
      return entity.isInvulnerable() ? invulnerableWitherTextures : witherTextures;
   }
}
