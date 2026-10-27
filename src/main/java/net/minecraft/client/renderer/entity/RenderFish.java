package net.minecraft.client.renderer.entity;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.IntEvent3;
import com.cheatbreaker.client.event.SimpleEvent31;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

public class RenderFish extends Render<EntityFishHook> {
   public static ResourceLocation FISH_PARTICLES = new ResourceLocation("textures/particle/particles.png");

   public RenderFish(RenderManager renderManagerIn) {
      super(renderManagerIn);
   }

   public void doRender(EntityFishHook entity, double x, double y, double z, float entityYaw, float partialTicks) {
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)x, (float)y, (float)z);
      GlStateManager.enableRescaleNormal();
      GlStateManager.scale(0.5F, 0.5F, 0.5F);
      this.bindEntityTexture(entity);
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      boolean flag = true;
      byte b = 2;
      float f = 0.0625F;
      float f2 = 0.125F;
      float f3 = 0.125F;
      float f4 = 0.1875F;
      float f5 = 1.0F;
      float f6 = 0.5F;
      float f7 = 0.5F;
      GlStateManager.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
      worldRenderer.pos(-0.5, -0.5, 0.0).tex(0.0625, 0.1875).normal(0.0F, 1.0F, 0.0F).endVertex();
      worldRenderer.pos(0.5, -0.5, 0.0).tex(0.125, 0.1875).normal(0.0F, 1.0F, 0.0F).endVertex();
      worldRenderer.pos(0.5, 0.5, 0.0).tex(0.125, 0.125).normal(0.0F, 1.0F, 0.0F).endVertex();
      worldRenderer.pos(-0.5, 0.5, 0.0).tex(0.0625, 0.125).normal(0.0F, 1.0F, 0.0F).endVertex();
      instance.draw();
      GlStateManager.disableRescaleNormal();
      GlStateManager.popMatrix();
      if (entity.angler != null) {
         float swingProgress = entity.angler.getSwingProgress(partialTicks);
         float sin = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);
         Vec3 vec3 = new Vec3(-0.36, 0.03, 0.35);
         vec3 = vec3.rotatePitch(
            -(entity.angler.prevRotationPitch + (entity.angler.rotationPitch - entity.angler.prevRotationPitch) * partialTicks) * (float) Math.PI / 180.0F
         );
         vec3 = vec3.rotateYaw(
            -(entity.angler.prevRotationYaw + (entity.angler.rotationYaw - entity.angler.prevRotationYaw) * partialTicks) * (float) Math.PI / 180.0F
         );
         vec3 = vec3.rotateYaw(sin * 0.5F);
         vec3 = vec3.rotatePitch(-sin * 0.7F);
         double d = entity.angler.prevPosX + (entity.angler.posX - entity.angler.prevPosX) * partialTicks + vec3.xCoord;
         double d2 = entity.angler.prevPosY + (entity.angler.posY - entity.angler.prevPosY) * partialTicks + vec3.yCoord;
         double d3 = entity.angler.prevPosZ + (entity.angler.posZ - entity.angler.prevPosZ) * partialTicks + vec3.zCoord;
         double eyeHeight = entity.angler.getEyeHeight();
         if (this.renderManager.options != null && this.renderManager.options.thirdPersonView > 0 || entity.angler != Minecraft.getMinecraft().thePlayer) {
            float f8 = (entity.angler.prevRenderYawOffset + (entity.angler.renderYawOffset - entity.angler.prevRenderYawOffset) * partialTicks)
               * (float) Math.PI
               / 180.0F;
            double sin2 = MathHelper.sin(f8);
            double cos = MathHelper.cos(f8);
            double d4 = 0.35;
            double d5 = 0.8;
            d = entity.angler.prevPosX + (entity.angler.posX - entity.angler.prevPosX) * partialTicks - cos * 0.35 - sin2 * 0.8;
            d2 = entity.angler.prevPosY + eyeHeight + (entity.angler.posY - entity.angler.prevPosY) * partialTicks - 0.45;
            d3 = entity.angler.prevPosZ + (entity.angler.posZ - entity.angler.prevPosZ) * partialTicks - sin2 * 0.35 + cos * 0.8;
            eyeHeight = entity.angler.isSneaking() ? -0.1875 : 0.0;
         }

         double d4 = entity.prevPosX + (entity.posX - entity.prevPosX) * partialTicks;
         double d5 = entity.prevPosY + (entity.posY - entity.prevPosY) * partialTicks + 0.25;
         double d6 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * partialTicks;
         double d7 = (float)(d - d4);
         double d8 = (float)(d2 - d5) + eyeHeight;
         double d9 = (float)(d3 - d6);
         GlStateManager.disableTexture2D();
         GlStateManager.disableLighting();
         worldRenderer.begin(3, DefaultVertexFormats.POSITION_COLOR);
         byte b2 = 16;
         int n = -16777216;
         IntEvent3 intEvent3 = new IntEvent3(n);
         CheatBreaker.getInstance().getEventBus().handleEvent(intEvent3);
         n = intEvent3.getIntValue();

         for (int i = 0; i <= 16; i++) {
            float f8 = i / 16.0F;
            worldRenderer.pos(x + d7 * f8, y + d8 * (f8 * f8 + f8) * 0.5 + 0.25, z + d9 * f8)
               .color(n >> 16 & 0xFF, n >> 8 & 0xFF, n & 0xFF, n >> 24 & 0xFF)
               .endVertex();
         }

         instance.draw();
         CheatBreaker.getInstance().getEventBus().handleEvent(new SimpleEvent31());
         GlStateManager.enableLighting();
         GlStateManager.enableTexture2D();
         super.doRender(entity, x, y, z, entityYaw, partialTicks);
      }
   }

   public ResourceLocation getEntityTexture(EntityFishHook entity) {
      return FISH_PARTICLES;
   }
}
