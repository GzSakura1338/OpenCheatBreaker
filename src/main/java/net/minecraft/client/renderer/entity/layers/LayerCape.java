package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.util.MathHelper;

public class LayerCape implements LayerRenderer<AbstractClientPlayer> {
   public RenderPlayer playerRenderer;

   public LayerCape(RenderPlayer playerRendererIn) {
      this.playerRenderer = playerRendererIn;
   }

   public void doRenderLayer(
      AbstractClientPlayer entitylivingbaseIn, float f, float f2, float partialTicks, float f3, float f4, float f5, float scale
   ) {
      if (entitylivingbaseIn.hasPlayerInfo()
         && !entitylivingbaseIn.isInvisible()
         && entitylivingbaseIn.isWearing(EnumPlayerModelParts.CAPE)
         && entitylivingbaseIn.getLocationCape() != null) {
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         this.playerRenderer.bindTexture(entitylivingbaseIn.getLocationCape());
         GlStateManager.pushMatrix();
         GlStateManager.translate(0.0F, 0.0F, 0.125F);
         double d = entitylivingbaseIn.prevChasingPosX
            + (entitylivingbaseIn.chasingPosX - entitylivingbaseIn.prevChasingPosX) * partialTicks
            - (entitylivingbaseIn.prevPosX + (entitylivingbaseIn.posX - entitylivingbaseIn.prevPosX) * partialTicks);
         double d2 = entitylivingbaseIn.prevChasingPosY
            + (entitylivingbaseIn.chasingPosY - entitylivingbaseIn.prevChasingPosY) * partialTicks
            - (entitylivingbaseIn.prevPosY + (entitylivingbaseIn.posY - entitylivingbaseIn.prevPosY) * partialTicks);
         double d3 = entitylivingbaseIn.prevChasingPosZ
            + (entitylivingbaseIn.chasingPosZ - entitylivingbaseIn.prevChasingPosZ) * partialTicks
            - (entitylivingbaseIn.prevPosZ + (entitylivingbaseIn.posZ - entitylivingbaseIn.prevPosZ) * partialTicks);
         float f6 = entitylivingbaseIn.prevRenderYawOffset + (entitylivingbaseIn.renderYawOffset - entitylivingbaseIn.prevRenderYawOffset) * partialTicks;
         double sin = MathHelper.sin(f6 * (float) Math.PI / 180.0F);
         double d4 = -MathHelper.cos(f6 * (float) Math.PI / 180.0F);
         float f7 = (float)d2 * 10.0F;
         f7 = MathHelper.clamp_float(f7, -6.0F, 32.0F);
         float f8 = (float)(d * sin + d3 * d4) * 100.0F;
         float f9 = (float)(d * d4 - d3 * sin) * 100.0F;
         if (f8 < 0.0F) {
            f8 = 0.0F;
         }

         if (f8 > 165.0F) {
            f8 = 165.0F;
         }

         if (f7 < -5.0F) {
            f7 = -5.0F;
         }

         float f10 = entitylivingbaseIn.prevCameraYaw + (entitylivingbaseIn.cameraYaw - entitylivingbaseIn.prevCameraYaw) * partialTicks;
         f7 += MathHelper.sin(
               (
                     entitylivingbaseIn.prevDistanceWalkedModified
                        + (entitylivingbaseIn.distanceWalkedModified - entitylivingbaseIn.prevDistanceWalkedModified) * partialTicks
                  )
                  * 6.0F
            )
            * 32.0F
            * f10;
         if (entitylivingbaseIn.isSneaking()) {
            f7 += 25.0F;
            GlStateManager.translate(0.0F, 0.142F, -0.0178F);
         }

         GlStateManager.rotate(6.0F + f8 / 2.0F + f7, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotate(f9 / 2.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.rotate(-f9 / 2.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
         this.playerRenderer.getMainModel().renderCape(0.0625F);
         GlStateManager.popMatrix();
      }
   }

   @Override
   public boolean shouldCombineTextures() {
      return false;
   }
}
