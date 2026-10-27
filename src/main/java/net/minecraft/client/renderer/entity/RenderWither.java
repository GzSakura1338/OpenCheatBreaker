package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelWither;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerWitherAura;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.util.ResourceLocation;

public class RenderWither extends RenderLiving<EntityWither> {
   public static ResourceLocation witherInvulnerablePng = new ResourceLocation("textures/entity/wither/wither_invulnerable.png");
   public static ResourceLocation witherPng = new ResourceLocation("textures/entity/wither/wither.png");

   public RenderWither(RenderManager renderManagerIn) {
      super(renderManagerIn, new ModelWither(0.0F), 1.0F);
      this.addLayer(new LayerWitherAura(this));
   }

   public void doRender(EntityWither entity, double x, double y, double z, float entityYaw, float partialTicks) {
      BossStatus.setBossStatus(entity, true);
      super.doRender(entity, x, y, z, entityYaw, partialTicks);
   }

   public ResourceLocation getEntityTexture(EntityWither entity) {
      int invulTime = entity.getInvulTime();
      return invulTime <= 0 || invulTime <= 80 && invulTime / 5 % 2 == 1 ? witherPng : witherInvulnerablePng;
   }

   public void preRenderCallback(EntityWither entitylivingbaseIn, float partialTickTime) {
      float f = 2.0F;
      int invulTime = entitylivingbaseIn.getInvulTime();
      if (invulTime > 0) {
         f -= (invulTime - partialTickTime) / 220.0F * 0.5F;
      }

      GlStateManager.scale(f, f, f);
   }
}
