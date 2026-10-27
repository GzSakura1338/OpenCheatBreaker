package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;

public class LayerDeadmau5Head implements LayerRenderer<AbstractClientPlayer> {
   public RenderPlayer playerRenderer;

   public LayerDeadmau5Head(RenderPlayer playerRendererIn) {
      this.playerRenderer = playerRendererIn;
   }

   public void doRenderLayer(
      AbstractClientPlayer entitylivingbaseIn, float f, float f2, float partialTicks, float f3, float f4, float f5, float scale
   ) {
      if (entitylivingbaseIn.getName().equals("deadmau5") && entitylivingbaseIn.hasSkin() && !entitylivingbaseIn.isInvisible()) {
         this.playerRenderer.bindTexture(entitylivingbaseIn.getLocationSkin());

         for (int i = 0; i < 2; i++) {
            float f6 = entitylivingbaseIn.prevRotationYaw
               + (entitylivingbaseIn.rotationYaw - entitylivingbaseIn.prevRotationYaw) * partialTicks
               - (entitylivingbaseIn.prevRenderYawOffset + (entitylivingbaseIn.renderYawOffset - entitylivingbaseIn.prevRenderYawOffset) * partialTicks);
            float f7 = entitylivingbaseIn.prevRotationPitch + (entitylivingbaseIn.rotationPitch - entitylivingbaseIn.prevRotationPitch) * partialTicks;
            GlStateManager.pushMatrix();
            GlStateManager.rotate(f6, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(f7, 1.0F, 0.0F, 0.0F);
            GlStateManager.translate(0.375F * (i * 2 - 1), 0.0F, 0.0F);
            GlStateManager.translate(0.0F, -0.375F, 0.0F);
            GlStateManager.rotate(-f7, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(-f6, 0.0F, 1.0F, 0.0F);
            float f8 = 1.3333334F;
            GlStateManager.scale(f8, f8, f8);
            this.playerRenderer.getMainModel().renderDeadmau5Head(0.0625F);
            GlStateManager.popMatrix();
         }
      }
   }

   @Override
   public boolean shouldCombineTextures() {
      return true;
   }
}
