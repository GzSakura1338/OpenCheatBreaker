package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.EntityLiving;
import net.optifine.Config;
import net.optifine.shaders.Shaders;

public abstract class RenderLiving<T extends EntityLiving> extends RendererLivingEntity<T> {
   public RenderLiving(RenderManager rendermanagerIn, ModelBase modelbaseIn, float shadowsizeIn) {
      super(rendermanagerIn, modelbaseIn, shadowsizeIn);
   }

   public boolean canRenderName(T entity) {
      return super.canRenderName((T)entity)
         && (entity.getAlwaysRenderNameTagForRender() || entity.hasCustomName() && entity == this.renderManager.pointedEntity);
   }

   public boolean shouldRender(T livingEntity, ICamera camera, double camX, double camY, double camZ) {
      if (super.shouldRender((T)livingEntity, camera, camX, camY, camZ)) {
         return true;
      } else if (livingEntity.getLeashed() && livingEntity.getLeashedToEntity() != null) {
         Entity leashedToEntity = livingEntity.getLeashedToEntity();
         return camera.isBoundingBoxInFrustum(leashedToEntity.getEntityBoundingBox());
      } else {
         return false;
      }
   }

   public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
      super.doRender((T)entity, x, y, z, entityYaw, partialTicks);
      this.renderLeash((T)entity, x, y, z, entityYaw, partialTicks);
   }

   public void setLightmap(T entityLivingIn, float partialTicks) {
      int brightnessForRender = entityLivingIn.getBrightnessForRender(partialTicks);
      int n = brightnessForRender % 65536;
      int n2 = brightnessForRender / 65536;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, n / 1.0F, n2 / 1.0F);
   }

   public double interpolateValue(double start, double end, double pct) {
      return start + (end - start) * pct;
   }

   public void renderLeash(T entityLivingIn, double x, double y, double z, float entityYaw, float partialTicks) {
      if (!Config.isShaders() || !Shaders.isShadowPass) {
         Entity leashedToEntity = entityLivingIn.getLeashedToEntity();
         if (leashedToEntity != null) {
            y -= (1.6 - entityLivingIn.height) * 0.5;
            Tessellator instance = Tessellator.getInstance();
            WorldRenderer worldRenderer = instance.getWorldRenderer();
            double d = this.interpolateValue(leashedToEntity.prevRotationYaw, leashedToEntity.rotationYaw, partialTicks * 0.5F) * (float) (Math.PI / 180.0);
            double d2 = this.interpolateValue(leashedToEntity.prevRotationPitch, leashedToEntity.rotationPitch, partialTicks * 0.5F) * (float) (Math.PI / 180.0);
            double cos = Math.cos(d);
            double sin = Math.sin(d);
            double sin2 = Math.sin(d2);
            if (leashedToEntity instanceof EntityHanging) {
               cos = 0.0;
               sin = 0.0;
               sin2 = -1.0;
            }

            double cos2 = Math.cos(d2);
            double d3 = this.interpolateValue(leashedToEntity.prevPosX, leashedToEntity.posX, partialTicks) - cos * 0.7 - sin * 0.5 * cos2;
            double d4 = this.interpolateValue(leashedToEntity.prevPosY + leashedToEntity.getEyeHeight() * 0.7, leashedToEntity.posY + leashedToEntity.getEyeHeight() * 0.7, partialTicks)
               - sin2 * 0.5
               - 0.25;
            double d5 = this.interpolateValue(leashedToEntity.prevPosZ, leashedToEntity.posZ, partialTicks) - sin * 0.7 + cos * 0.5 * cos2;
            double d6 = this.interpolateValue(entityLivingIn.prevRenderYawOffset, entityLivingIn.renderYawOffset, partialTicks) * (float) (Math.PI / 180.0)
               + (Math.PI / 2);
            cos = Math.cos(d6) * entityLivingIn.width * 0.4;
            sin = Math.sin(d6) * entityLivingIn.width * 0.4;
            double d7 = this.interpolateValue(entityLivingIn.prevPosX, entityLivingIn.posX, partialTicks) + cos;
            double interpolateValue = this.interpolateValue(entityLivingIn.prevPosY, entityLivingIn.posY, partialTicks);
            double d8 = this.interpolateValue(entityLivingIn.prevPosZ, entityLivingIn.posZ, partialTicks) + sin;
            x += cos;
            z += sin;
            double d9 = (float)(d3 - d7);
            double d10 = (float)(d4 - interpolateValue);
            double d11 = (float)(d5 - d8);
            GlStateManager.disableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableCull();
            if (Config.isShaders()) {
               Shaders.beginLeash();
            }

            byte b = 24;
            double d12 = 0.025;
            worldRenderer.begin(5, DefaultVertexFormats.POSITION_COLOR);

            for (int i = 0; i <= 24; i++) {
               float f = 0.5F;
               float f2 = 0.4F;
               float f3 = 0.3F;
               if (i % 2 == 0) {
                  f *= 0.7F;
                  f2 *= 0.7F;
                  f3 *= 0.7F;
               }

               float f4 = i / 24.0F;
               worldRenderer.pos(x + d9 * f4 + 0.0, y + d10 * (f4 * f4 + f4) * 0.5 + ((24.0F - i) / 18.0F + 0.125F), z + d11 * f4)
                  .color(f, f2, f3, 1.0F)
                  .endVertex();
               worldRenderer.pos(x + d9 * f4 + 0.025, y + d10 * (f4 * f4 + f4) * 0.5 + ((24.0F - i) / 18.0F + 0.125F) + 0.025, z + d11 * f4)
                  .color(f, f2, f3, 1.0F)
                  .endVertex();
            }

            instance.draw();
            worldRenderer.begin(5, DefaultVertexFormats.POSITION_COLOR);

            for (int i = 0; i <= 24; i++) {
               float f = 0.5F;
               float f2 = 0.4F;
               float f3 = 0.3F;
               if (i % 2 == 0) {
                  f *= 0.7F;
                  f2 *= 0.7F;
                  f3 *= 0.7F;
               }

               float f4 = i / 24.0F;
               worldRenderer.pos(x + d9 * f4 + 0.0, y + d10 * (f4 * f4 + f4) * 0.5 + ((24.0F - i) / 18.0F + 0.125F) + 0.025, z + d11 * f4)
                  .color(f, f2, f3, 1.0F)
                  .endVertex();
               worldRenderer.pos(x + d9 * f4 + 0.025, y + d10 * (f4 * f4 + f4) * 0.5 + ((24.0F - i) / 18.0F + 0.125F), z + d11 * f4 + 0.025)
                  .color(f, f2, f3, 1.0F)
                  .endVertex();
            }

            instance.draw();
            if (Config.isShaders()) {
               Shaders.endLeash();
            }

            GlStateManager.enableLighting();
            GlStateManager.enableTexture2D();
            GlStateManager.enableCull();
         }
      }
   }
}
