package net.optifine.model;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

public class ModelSprite {
   public ModelRenderer modelRenderer = null;
   public int textureOffsetX = 0;
   public int textureOffsetY = 0;
   public float posX = 0.0F;
   public float posY = 0.0F;
   public float posZ = 0.0F;
   public int sizeX = 0;
   public int sizeY = 0;
   public int sizeZ = 0;
   public float sizeAdd = 0.0F;
   public float minU = 0.0F;
   public float minV = 0.0F;
   public float maxU = 0.0F;
   public float maxV = 0.0F;

   public ModelSprite(ModelRenderer modelRenderer, int n, int n2, float f, float f2, float f3, int n3, int n4, int n5, float f4) {
      this.modelRenderer = modelRenderer;
      this.textureOffsetX = n;
      this.textureOffsetY = n2;
      this.posX = f;
      this.posY = f2;
      this.posZ = f3;
      this.sizeX = n3;
      this.sizeY = n4;
      this.sizeZ = n5;
      this.sizeAdd = f4;
      this.minU = n / modelRenderer.textureWidth;
      this.minV = n2 / modelRenderer.textureHeight;
      this.maxU = (n + n3) / modelRenderer.textureWidth;
      this.maxV = (n2 + n4) / modelRenderer.textureHeight;
   }

   public void render(Tessellator tessellator, float f) {
      GlStateManager.translate(this.posX * f, this.posY * f, this.posZ * f);
      float minU = this.minU;
      float maxU = this.maxU;
      float minV = this.minV;
      float maxV = this.maxV;
      if (this.modelRenderer.mirror) {
         minU = this.maxU;
         maxU = this.minU;
      }

      if (this.modelRenderer.mirrorV) {
         minV = this.maxV;
         maxV = this.minV;
      }

      renderItemIn2D(tessellator, minU, minV, maxU, maxV, this.sizeX, this.sizeY, f * this.sizeZ, this.modelRenderer.textureWidth, this.modelRenderer.textureHeight);
      GlStateManager.translate(-this.posX * f, -this.posY * f, -this.posZ * f);
   }

   public static void renderItemIn2D(Tessellator tessellator, float f, float f2, float f3, float f4, int n, int n2, float f5, float f6, float f7) {
      if (f5 < 6.25E-4F) {
         f5 = 6.25E-4F;
      }

      float f8 = f3 - f;
      float f9 = f4 - f2;
      double d = MathHelper.abs(f8) * (f6 / 16.0F);
      double d2 = MathHelper.abs(f9) * (f7 / 16.0F);
      WorldRenderer worldRenderer = tessellator.getWorldRenderer();
      GL11.glNormal3f(0.0F, 0.0F, -1.0F);
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
      worldRenderer.pos(0.0, d2, 0.0).tex(f, f4).endVertex();
      worldRenderer.pos(d, d2, 0.0).tex(f3, f4).endVertex();
      worldRenderer.pos(d, 0.0, 0.0).tex(f3, f2).endVertex();
      worldRenderer.pos(0.0, 0.0, 0.0).tex(f, f2).endVertex();
      tessellator.draw();
      GL11.glNormal3f(0.0F, 0.0F, 1.0F);
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
      worldRenderer.pos(0.0, 0.0, f5).tex(f, f2).endVertex();
      worldRenderer.pos(d, 0.0, f5).tex(f3, f2).endVertex();
      worldRenderer.pos(d, d2, f5).tex(f3, f4).endVertex();
      worldRenderer.pos(0.0, d2, f5).tex(f, f4).endVertex();
      tessellator.draw();
      float f10 = 0.5F * f8 / n;
      float f11 = 0.5F * f9 / n2;
      GL11.glNormal3f(-1.0F, 0.0F, 0.0F);
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);

      for (int i = 0; i < n; i++) {
         float f12 = (float)i / n;
         float f13 = f + f8 * f12 + f10;
         worldRenderer.pos(f12 * d, d2, f5).tex(f13, f4).endVertex();
         worldRenderer.pos(f12 * d, d2, 0.0).tex(f13, f4).endVertex();
         worldRenderer.pos(f12 * d, 0.0, 0.0).tex(f13, f2).endVertex();
         worldRenderer.pos(f12 * d, 0.0, f5).tex(f13, f2).endVertex();
      }

      tessellator.draw();
      GL11.glNormal3f(1.0F, 0.0F, 0.0F);
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);

      for (int i = 0; i < n; i++) {
         float f12 = (float)i / n;
         float f13 = f + f8 * f12 + f10;
         float f14 = f12 + 1.0F / n;
         worldRenderer.pos(f14 * d, 0.0, f5).tex(f13, f2).endVertex();
         worldRenderer.pos(f14 * d, 0.0, 0.0).tex(f13, f2).endVertex();
         worldRenderer.pos(f14 * d, d2, 0.0).tex(f13, f4).endVertex();
         worldRenderer.pos(f14 * d, d2, f5).tex(f13, f4).endVertex();
      }

      tessellator.draw();
      GL11.glNormal3f(0.0F, 1.0F, 0.0F);
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);

      for (int i = 0; i < n2; i++) {
         float f12 = (float)i / n2;
         float f13 = f2 + f9 * f12 + f11;
         float f14 = f12 + 1.0F / n2;
         worldRenderer.pos(0.0, f14 * d2, f5).tex(f, f13).endVertex();
         worldRenderer.pos(d, f14 * d2, f5).tex(f3, f13).endVertex();
         worldRenderer.pos(d, f14 * d2, 0.0).tex(f3, f13).endVertex();
         worldRenderer.pos(0.0, f14 * d2, 0.0).tex(f, f13).endVertex();
      }

      tessellator.draw();
      GL11.glNormal3f(0.0F, -1.0F, 0.0F);
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);

      for (int i = 0; i < n2; i++) {
         float f12 = (float)i / n2;
         float f13 = f2 + f9 * f12 + f11;
         worldRenderer.pos(d, f12 * d2, f5).tex(f3, f13).endVertex();
         worldRenderer.pos(0.0, f12 * d2, f5).tex(f, f13).endVertex();
         worldRenderer.pos(0.0, f12 * d2, 0.0).tex(f, f13).endVertex();
         worldRenderer.pos(d, f12 * d2, 0.0).tex(f3, f13).endVertex();
      }

      tessellator.draw();
   }
}
