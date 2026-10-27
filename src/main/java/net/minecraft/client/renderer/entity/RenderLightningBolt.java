package net.minecraft.client.renderer.entity;

import java.util.Random;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.util.ResourceLocation;

public class RenderLightningBolt extends Render<EntityLightningBolt> {
   public RenderLightningBolt(RenderManager renderManagerIn) {
      super(renderManagerIn);
   }

   public void doRender(EntityLightningBolt entity, double x, double y, double z, float entityYaw, float partialTicks) {
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      GlStateManager.disableTexture2D();
      GlStateManager.disableLighting();
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(770, 1);
      double[] doubles = new double[8];
      double[] doubles2 = new double[8];
      double d = 0.0;
      double d2 = 0.0;
      Random random = new Random(entity.boltVertex);

      for (int i = 7; i >= 0; i--) {
         doubles[i] = d;
         doubles2[i] = d2;
         d += random.nextInt(11) - 5;
         d2 += random.nextInt(11) - 5;
      }

      for (int i = 0; i < 4; i++) {
         Random random2 = new Random(entity.boltVertex);

         for (int j = 0; j < 3; j++) {
            int n = 7;
            int n2 = 0;
            if (j > 0) {
               n = 7 - j;
            }

            if (j > 0) {
               n2 = n - 2;
            }

            double d3 = doubles[n] - d;
            double d4 = doubles2[n] - d2;

            for (int k = n; k >= n2; k--) {
               double d5 = d3;
               double d6 = d4;
               if (j == 0) {
                  d3 += random2.nextInt(11) - 5;
                  d4 += random2.nextInt(11) - 5;
               } else {
                  d3 += random2.nextInt(31) - 15;
                  d4 += random2.nextInt(31) - 15;
               }

               worldRenderer.begin(5, DefaultVertexFormats.POSITION_COLOR);
               float f = 0.5F;
               float f2 = 0.45F;
               float f3 = 0.45F;
               float f4 = 0.5F;
               double d7 = 0.1 + i * 0.2;
               if (j == 0) {
                  d7 *= k * 0.1 + 1.0;
               }

               double d8 = 0.1 + i * 0.2;
               if (j == 0) {
                  d8 *= (k - 1) * 0.1 + 1.0;
               }

               for (int m = 0; m < 5; m++) {
                  double d9 = x + 0.5 - d7;
                  double d10 = z + 0.5 - d7;
                  if (m == 1 || m == 2) {
                     d9 += d7 * 2.0;
                  }

                  if (m == 2 || m == 3) {
                     d10 += d7 * 2.0;
                  }

                  double d11 = x + 0.5 - d8;
                  double d12 = z + 0.5 - d8;
                  if (m == 1 || m == 2) {
                     d11 += d8 * 2.0;
                  }

                  if (m == 2 || m == 3) {
                     d12 += d8 * 2.0;
                  }

                  worldRenderer.pos(d11 + d3, y + k * 16, d12 + d4).color(0.45F, 0.45F, 0.5F, 0.3F).endVertex();
                  worldRenderer.pos(d9 + d5, y + (k + 1) * 16, d10 + d6).color(0.45F, 0.45F, 0.5F, 0.3F).endVertex();
               }

               instance.draw();
            }
         }
      }

      GlStateManager.disableBlend();
      GlStateManager.enableLighting();
      GlStateManager.enableTexture2D();
   }

   public ResourceLocation getEntityTexture(EntityLightningBolt entity) {
      return null;
   }
}
