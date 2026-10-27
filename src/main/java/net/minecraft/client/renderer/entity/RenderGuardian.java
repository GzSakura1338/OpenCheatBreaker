package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelGuardian;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

public class RenderGuardian extends RenderLiving<EntityGuardian> {
   public static ResourceLocation GUARDIAN_TEXTURE = new ResourceLocation("textures/entity/guardian.png");
   public static ResourceLocation GUARDIAN_ELDER_TEXTURE = new ResourceLocation("textures/entity/guardian_elder.png");
   public static ResourceLocation GUARDIAN_BEAM_TEXTURE = new ResourceLocation("textures/entity/guardian_beam.png");
   public int intValue = ((ModelGuardian)this.mainModel).constantInt();

   public RenderGuardian(RenderManager renderManagerIn) {
      super(renderManagerIn, new ModelGuardian(), 0.5F);
   }

   public boolean shouldRender(EntityGuardian livingEntity, ICamera camera, double camX, double camY, double camZ) {
      if (super.shouldRender(livingEntity, camera, camX, camY, camZ)) {
         return true;
      }

      if (livingEntity.hasTargetedEntity()) {
         EntityLivingBase targetedEntity = livingEntity.getTargetedEntity();
         if (targetedEntity != null) {
            Vec3 createVec3 = this.createVec3(targetedEntity, targetedEntity.height * 0.5, 1.0F);
            Vec3 createVec32 = this.createVec3(livingEntity, livingEntity.getEyeHeight(), 1.0F);
            if (camera.isBoundingBoxInFrustum(AxisAlignedBB.fromBounds(createVec32.xCoord, createVec32.yCoord, createVec32.zCoord, createVec3.xCoord, createVec3.yCoord, createVec3.zCoord))) {
               return true;
            }
         }
      }

      return false;
   }

   public Vec3 createVec3(EntityLivingBase entityLivingBaseIn, double d, float f) {
      double d2 = entityLivingBaseIn.lastTickPosX + (entityLivingBaseIn.posX - entityLivingBaseIn.lastTickPosX) * f;
      double d3 = d + entityLivingBaseIn.lastTickPosY + (entityLivingBaseIn.posY - entityLivingBaseIn.lastTickPosY) * f;
      double d4 = entityLivingBaseIn.lastTickPosZ + (entityLivingBaseIn.posZ - entityLivingBaseIn.lastTickPosZ) * f;
      return new Vec3(d2, d3, d4);
   }

   public void doRender(EntityGuardian entity, double x, double y, double z, float entityYaw, float partialTicks) {
      if (this.intValue != ((ModelGuardian)this.mainModel).constantInt()) {
         this.mainModel = new ModelGuardian();
         this.intValue = ((ModelGuardian)this.mainModel).constantInt();
      }

      super.doRender(entity, x, y, z, entityYaw, partialTicks);
      EntityLivingBase targetedEntity = entity.getTargetedEntity();
      if (targetedEntity != null) {
         float beamProgress = entity.getBeamProgress(partialTicks);
         Tessellator instance = Tessellator.getInstance();
         WorldRenderer worldRenderer = instance.getWorldRenderer();
         this.bindTexture(GUARDIAN_BEAM_TEXTURE);
         GL11.glTexParameterf(3553, 10242, 10497.0F);
         GL11.glTexParameterf(3553, 10243, 10497.0F);
         GlStateManager.disableLighting();
         GlStateManager.disableCull();
         GlStateManager.disableBlend();
         GlStateManager.depthMask(true);
         float f = 240.0F;
         OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, f, f);
         GlStateManager.tryBlendFuncSeparate(770, 1, 1, 0);
         float f2 = (float)entity.worldObj.getTotalWorldTime() + partialTicks;
         float f3 = f2 * 0.5F % 1.0F;
         float eyeHeight = entity.getEyeHeight();
         GlStateManager.pushMatrix();
         GlStateManager.translate((float)x, (float)y + eyeHeight, (float)z);
         Vec3 createVec3 = this.createVec3(targetedEntity, targetedEntity.height * 0.5, partialTicks);
         Vec3 createVec32 = this.createVec3(entity, eyeHeight, partialTicks);
         Vec3 subtract = createVec3.subtract(createVec32);
         double d = subtract.lengthVector() + 1.0;
         subtract = subtract.normalize();
         float acos = (float)Math.acos(subtract.yCoord);
         float atan2 = (float)Math.atan2(subtract.zCoord, subtract.xCoord);
         GlStateManager.rotate(((float) (Math.PI / 2) + -atan2) * (180.0F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(acos * (180.0F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
         byte b = 1;
         double d2 = f2 * 0.05 * (1.0 - (b & 1) * 2.5);
         worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
         float f4 = beamProgress * beamProgress;
         int n = 64 + (int)(f4 * 240.0F);
         int n2 = 32 + (int)(f4 * 192.0F);
         int n3 = 128 - (int)(f4 * 64.0F);
         double d3 = b * 0.2;
         double d4 = d3 * 1.41;
         double d5 = 0.0 + Math.cos(d2 + (Math.PI * 3.0 / 4.0)) * d4;
         double d6 = 0.0 + Math.sin(d2 + (Math.PI * 3.0 / 4.0)) * d4;
         double d7 = 0.0 + Math.cos(d2 + (Math.PI / 4)) * d4;
         double d8 = 0.0 + Math.sin(d2 + (Math.PI / 4)) * d4;
         double d9 = 0.0 + Math.cos(d2 + (Math.PI * 5.0 / 4.0)) * d4;
         double d10 = 0.0 + Math.sin(d2 + (Math.PI * 5.0 / 4.0)) * d4;
         double d11 = 0.0 + Math.cos(d2 + (Math.PI * 7.0 / 4.0)) * d4;
         double d12 = 0.0 + Math.sin(d2 + (Math.PI * 7.0 / 4.0)) * d4;
         double d13 = 0.0 + Math.cos(d2 + Math.PI) * d3;
         double d14 = 0.0 + Math.sin(d2 + Math.PI) * d3;
         double d15 = 0.0 + Math.cos(d2 + 0.0) * d3;
         double d16 = 0.0 + Math.sin(d2 + 0.0) * d3;
         double d17 = 0.0 + Math.cos(d2 + (Math.PI / 2)) * d3;
         double d18 = 0.0 + Math.sin(d2 + (Math.PI / 2)) * d3;
         double d19 = 0.0 + Math.cos(d2 + (Math.PI * 3.0 / 2.0)) * d3;
         double d20 = 0.0 + Math.sin(d2 + (Math.PI * 3.0 / 2.0)) * d3;
         double d21 = 0.0;
         double d22 = 0.4999;
         double d23 = -1.0F + f3;
         double d24 = d * (0.5 / d3) + d23;
         worldRenderer.pos(d13, d, d14).tex(0.4999, d24).color(n, n2, n3, 255).endVertex();
         worldRenderer.pos(d13, 0.0, d14).tex(0.4999, d23).color(n, n2, n3, 255).endVertex();
         worldRenderer.pos(d15, 0.0, d16).tex(0.0, d23).color(n, n2, n3, 255).endVertex();
         worldRenderer.pos(d15, d, d16).tex(0.0, d24).color(n, n2, n3, 255).endVertex();
         worldRenderer.pos(d17, d, d18).tex(0.4999, d24).color(n, n2, n3, 255).endVertex();
         worldRenderer.pos(d17, 0.0, d18).tex(0.4999, d23).color(n, n2, n3, 255).endVertex();
         worldRenderer.pos(d19, 0.0, d20).tex(0.0, d23).color(n, n2, n3, 255).endVertex();
         worldRenderer.pos(d19, d, d20).tex(0.0, d24).color(n, n2, n3, 255).endVertex();
         double d25 = 0.0;
         if (entity.ticksExisted % 2 == 0) {
            d25 = 0.5;
         }

         worldRenderer.pos(d5, d, d6).tex(0.5, d25 + 0.5).color(n, n2, n3, 255).endVertex();
         worldRenderer.pos(d7, d, d8).tex(1.0, d25 + 0.5).color(n, n2, n3, 255).endVertex();
         worldRenderer.pos(d11, d, d12).tex(1.0, d25).color(n, n2, n3, 255).endVertex();
         worldRenderer.pos(d9, d, d10).tex(0.5, d25).color(n, n2, n3, 255).endVertex();
         instance.draw();
         GlStateManager.popMatrix();
      }
   }

   public void preRenderCallback(EntityGuardian entitylivingbaseIn, float partialTickTime) {
      if (entitylivingbaseIn.isElder()) {
         GlStateManager.scale(2.35F, 2.35F, 2.35F);
      }
   }

   public ResourceLocation getEntityTexture(EntityGuardian entity) {
      return entity.isElder() ? GUARDIAN_ELDER_TEXTURE : GUARDIAN_TEXTURE;
   }
}
