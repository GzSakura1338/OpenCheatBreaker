package net.minecraft.client.renderer.entity.layers;

import java.util.Random;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.boss.EntityDragon;

public class LayerEnderDragonDeath implements LayerRenderer<EntityDragon> {
   public void doRenderLayer(EntityDragon entitylivingbaseIn, float f, float f2, float partialTicks, float f3, float f4, float f5, float scale) {
      if (entitylivingbaseIn.deathTicks > 0) {
         Tessellator instance = Tessellator.getInstance();
         WorldRenderer worldRenderer = instance.getWorldRenderer();
         RenderHelper.disableStandardItemLighting();
         float f6 = (entitylivingbaseIn.deathTicks + partialTicks) / 200.0F;
         float f7 = 0.0F;
         if (f6 > 0.8F) {
            f7 = (f6 - 0.8F) / 0.2F;
         }

         Random random = new Random(432L);
         GlStateManager.disableTexture2D();
         GlStateManager.shadeModel(7425);
         GlStateManager.enableBlend();
         GlStateManager.blendFunc(770, 1);
         GlStateManager.disableAlpha();
         GlStateManager.enableCull();
         GlStateManager.depthMask(false);
         GlStateManager.pushMatrix();
         GlStateManager.translate(0.0F, -1.0F, -2.0F);

         for (int i = 0; i < (f6 + f6 * f6) / 2.0F * 60.0F; i++) {
            GlStateManager.rotate(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(random.nextFloat() * 360.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(random.nextFloat() * 360.0F + f6 * 90.0F, 0.0F, 0.0F, 1.0F);
            float f8 = random.nextFloat() * 20.0F + 5.0F + f7 * 10.0F;
            float f9 = random.nextFloat() * 2.0F + 1.0F + f7 * 2.0F;
            worldRenderer.begin(6, DefaultVertexFormats.POSITION_COLOR);
            worldRenderer.pos(0.0, 0.0, 0.0).color(255, 255, 255, (int)(255.0F * (1.0F - f7))).endVertex();
            worldRenderer.pos(-0.866 * f9, f8, -0.5F * f9).color(255, 0, 255, 0).endVertex();
            worldRenderer.pos(0.866 * f9, f8, -0.5F * f9).color(255, 0, 255, 0).endVertex();
            worldRenderer.pos(0.0, f8, 1.0F * f9).color(255, 0, 255, 0).endVertex();
            worldRenderer.pos(-0.866 * f9, f8, -0.5F * f9).color(255, 0, 255, 0).endVertex();
            instance.draw();
         }

         GlStateManager.popMatrix();
         GlStateManager.depthMask(true);
         GlStateManager.disableCull();
         GlStateManager.disableBlend();
         GlStateManager.shadeModel(7424);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.enableTexture2D();
         GlStateManager.enableAlpha();
         RenderHelper.enableStandardItemLighting();
      }
   }

   @Override
   public boolean shouldCombineTextures() {
      return false;
   }
}
