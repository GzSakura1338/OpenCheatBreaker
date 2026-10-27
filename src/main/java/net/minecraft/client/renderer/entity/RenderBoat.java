package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBoat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class RenderBoat extends Render<EntityBoat> {
   public static ResourceLocation boatTextures = new ResourceLocation("textures/entity/boat.png");
   public ModelBase modelBoat = new ModelBoat();

   public RenderBoat(RenderManager renderManagerIn) {
      super(renderManagerIn);
      this.shadowSize = 0.5F;
   }

   public void doRender(EntityBoat entity, double x, double y, double z, float entityYaw, float partialTicks) {
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)x, (float)y + 0.25F, (float)z);
      GlStateManager.rotate(180.0F - entityYaw, 0.0F, 1.0F, 0.0F);
      float f = entity.getTimeSinceHit() - partialTicks;
      float f2 = entity.getDamageTaken() - partialTicks;
      if (f2 < 0.0F) {
         f2 = 0.0F;
      }

      if (f > 0.0F) {
         GlStateManager.rotate(MathHelper.sin(f) * f * f2 / 10.0F * entity.getForwardDirection(), 1.0F, 0.0F, 0.0F);
      }

      float f3 = 0.75F;
      GlStateManager.scale(f3, f3, f3);
      GlStateManager.scale(1.0F / f3, 1.0F / f3, 1.0F / f3);
      this.bindEntityTexture(entity);
      GlStateManager.scale(-1.0F, -1.0F, 1.0F);
      this.modelBoat.render(entity, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
      GlStateManager.popMatrix();
      super.doRender(entity, x, y, z, entityYaw, partialTicks);
   }

   public ResourceLocation getEntityTexture(EntityBoat entity) {
      return boatTextures;
   }
}
