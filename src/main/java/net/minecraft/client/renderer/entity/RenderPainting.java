package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.item.EntityPainting.EnumArt;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class RenderPainting extends Render<EntityPainting> {
   public static ResourceLocation KRISTOFFER_PAINTING_TEXTURE = new ResourceLocation("textures/painting/paintings_kristoffer_zetterstrand.png");

   public RenderPainting(RenderManager renderManagerIn) {
      super(renderManagerIn);
   }

   public void doRender(EntityPainting entity, double x, double y, double z, float entityYaw, float partialTicks) {
      GlStateManager.pushMatrix();
      GlStateManager.translate(x, y, z);
      GlStateManager.rotate(180.0F - entityYaw, 0.0F, 1.0F, 0.0F);
      GlStateManager.enableRescaleNormal();
      this.bindEntityTexture(entity);
      EnumArt art = entity.art;
      float f = 0.0625F;
      GlStateManager.scale(f, f, f);
      this.renderPainting(entity, art.sizeX, art.sizeY, art.offsetX, art.offsetY);
      GlStateManager.disableRescaleNormal();
      GlStateManager.popMatrix();
      super.doRender(entity, x, y, z, entityYaw, partialTicks);
   }

   public ResourceLocation getEntityTexture(EntityPainting entity) {
      return KRISTOFFER_PAINTING_TEXTURE;
   }

   public void renderPainting(EntityPainting painting, int width, int height, int textureU, int textureV) {
      float f = -width / 2.0F;
      float f2 = -height / 2.0F;
      float f3 = 0.5F;
      float f4 = 0.75F;
      float f5 = 0.8125F;
      float f6 = 0.0F;
      float f7 = 0.0625F;
      float f8 = 0.75F;
      float f9 = 0.8125F;
      float f10 = 0.001953125F;
      float f11 = 0.001953125F;
      float f12 = 0.7519531F;
      float f13 = 0.7519531F;
      float f14 = 0.0F;
      float f15 = 0.0625F;

      for (int i = 0; i < width / 16; i++) {
         for (int j = 0; j < height / 16; j++) {
            float f16 = f + (i + 1) * 16;
            float f17 = f + i * 16;
            float f18 = f2 + (j + 1) * 16;
            float f19 = f2 + j * 16;
            this.setLightmap(painting, (f16 + f17) / 2.0F, (f18 + f19) / 2.0F);
            float f20 = (textureU + width - i * 16) / 256.0F;
            float f21 = (textureU + width - (i + 1) * 16) / 256.0F;
            float f22 = (textureV + height - j * 16) / 256.0F;
            float f23 = (textureV + height - (j + 1) * 16) / 256.0F;
            Tessellator instance = Tessellator.getInstance();
            WorldRenderer worldRenderer = instance.getWorldRenderer();
            worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
            worldRenderer.pos(f16, f19, -f3).tex(f21, f22).normal(0.0F, 0.0F, -1.0F).endVertex();
            worldRenderer.pos(f17, f19, -f3).tex(f20, f22).normal(0.0F, 0.0F, -1.0F).endVertex();
            worldRenderer.pos(f17, f18, -f3).tex(f20, f23).normal(0.0F, 0.0F, -1.0F).endVertex();
            worldRenderer.pos(f16, f18, -f3).tex(f21, f23).normal(0.0F, 0.0F, -1.0F).endVertex();
            worldRenderer.pos(f16, f18, f3).tex(f4, f6).normal(0.0F, 0.0F, 1.0F).endVertex();
            worldRenderer.pos(f17, f18, f3).tex(f5, f6).normal(0.0F, 0.0F, 1.0F).endVertex();
            worldRenderer.pos(f17, f19, f3).tex(f5, f7).normal(0.0F, 0.0F, 1.0F).endVertex();
            worldRenderer.pos(f16, f19, f3).tex(f4, f7).normal(0.0F, 0.0F, 1.0F).endVertex();
            worldRenderer.pos(f16, f18, -f3).tex(f8, f10).normal(0.0F, 1.0F, 0.0F).endVertex();
            worldRenderer.pos(f17, f18, -f3).tex(f9, f10).normal(0.0F, 1.0F, 0.0F).endVertex();
            worldRenderer.pos(f17, f18, f3).tex(f9, f11).normal(0.0F, 1.0F, 0.0F).endVertex();
            worldRenderer.pos(f16, f18, f3).tex(f8, f11).normal(0.0F, 1.0F, 0.0F).endVertex();
            worldRenderer.pos(f16, f19, f3).tex(f8, f10).normal(0.0F, -1.0F, 0.0F).endVertex();
            worldRenderer.pos(f17, f19, f3).tex(f9, f10).normal(0.0F, -1.0F, 0.0F).endVertex();
            worldRenderer.pos(f17, f19, -f3).tex(f9, f11).normal(0.0F, -1.0F, 0.0F).endVertex();
            worldRenderer.pos(f16, f19, -f3).tex(f8, f11).normal(0.0F, -1.0F, 0.0F).endVertex();
            worldRenderer.pos(f16, f18, f3).tex(f13, f14).normal(-1.0F, 0.0F, 0.0F).endVertex();
            worldRenderer.pos(f16, f19, f3).tex(f13, f15).normal(-1.0F, 0.0F, 0.0F).endVertex();
            worldRenderer.pos(f16, f19, -f3).tex(f12, f15).normal(-1.0F, 0.0F, 0.0F).endVertex();
            worldRenderer.pos(f16, f18, -f3).tex(f12, f14).normal(-1.0F, 0.0F, 0.0F).endVertex();
            worldRenderer.pos(f17, f18, -f3).tex(f13, f14).normal(1.0F, 0.0F, 0.0F).endVertex();
            worldRenderer.pos(f17, f19, -f3).tex(f13, f15).normal(1.0F, 0.0F, 0.0F).endVertex();
            worldRenderer.pos(f17, f19, f3).tex(f12, f15).normal(1.0F, 0.0F, 0.0F).endVertex();
            worldRenderer.pos(f17, f18, f3).tex(f12, f14).normal(1.0F, 0.0F, 0.0F).endVertex();
            instance.draw();
         }
      }
   }

   public void setLightmap(EntityPainting painting, float f, float f2) {
      int n = MathHelper.floor_double(painting.posX);
      int n2 = MathHelper.floor_double(painting.posY + f2 / 16.0F);
      int n3 = MathHelper.floor_double(painting.posZ);
      EnumFacing facingDirection = painting.facingDirection;
      if (facingDirection == EnumFacing.NORTH) {
         n = MathHelper.floor_double(painting.posX + f / 16.0F);
      }

      if (facingDirection == EnumFacing.WEST) {
         n3 = MathHelper.floor_double(painting.posZ - f / 16.0F);
      }

      if (facingDirection == EnumFacing.SOUTH) {
         n = MathHelper.floor_double(painting.posX - f / 16.0F);
      }

      if (facingDirection == EnumFacing.EAST) {
         n3 = MathHelper.floor_double(painting.posZ + f / 16.0F);
      }

      int combinedLight = this.renderManager.worldObj.getCombinedLight(new BlockPos(n, n2, n3), 0);
      int n4 = combinedLight % 65536;
      int n5 = combinedLight / 65536;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, n4, n5);
      GlStateManager.color(1.0F, 1.0F, 1.0F);
   }
}
