package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.optifine.Config;
import net.optifine.CustomColors;

public class RenderXPOrb extends Render<EntityXPOrb> {
   public static ResourceLocation experienceOrbTextures = new ResourceLocation("textures/entity/experience_orb.png");

   public RenderXPOrb(RenderManager renderManagerIn) {
      super(renderManagerIn);
      this.shadowSize = 0.15F;
      this.shadowOpaque = 0.75F;
   }

   public void doRender(EntityXPOrb entity, double x, double y, double z, float entityYaw, float partialTicks) {
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)x, (float)y, (float)z);
      this.bindEntityTexture(entity);
      int textureByXP = entity.getTextureByXP();
      float f = (textureByXP % 4 * 16 + 0) / 64.0F;
      float f2 = (textureByXP % 4 * 16 + 16) / 64.0F;
      float f3 = (textureByXP / 4 * 16 + 0) / 64.0F;
      float f4 = (textureByXP / 4 * 16 + 16) / 64.0F;
      float f5 = 1.0F;
      float f6 = 0.5F;
      float f7 = 0.25F;
      int brightnessForRender = entity.getBrightnessForRender(partialTicks);
      int n = brightnessForRender % 65536;
      int n2 = brightnessForRender / 65536;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, n / 1.0F, n2 / 1.0F);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      float f8 = 255.0F;
      float f9 = (entity.xpColor + partialTicks) / 2.0F;
      if (Config.isCustomColors()) {
         f9 = CustomColors.getXpOrbTimer(f9);
      }

      n2 = (int)((MathHelper.sin(f9 + 0.0F) + 1.0F) * 0.5F * 255.0F);
      short s = 255;
      int n3 = (int)((MathHelper.sin(f9 + (float) (Math.PI * 4.0 / 3.0)) + 1.0F) * 0.1F * 255.0F);
      GlStateManager.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
      float f10 = 0.3F;
      GlStateManager.scale(0.3F, 0.3F, 0.3F);
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
      int n4 = n2;
      int n5 = 255;
      int n6 = n3;
      if (Config.isCustomColors()) {
         int xpOrbColor = CustomColors.getXpOrbColor(f9);
         if (xpOrbColor >= 0) {
            n4 = xpOrbColor >> 16 & 0xFF;
            n5 = xpOrbColor >> 8 & 0xFF;
            n6 = xpOrbColor >> 0 & 0xFF;
         }
      }

      worldRenderer.pos(0.0F - f6, 0.0F - f7, 0.0).tex(f, f4).color(n4, n5, n6, 128).normal(0.0F, 1.0F, 0.0F).endVertex();
      worldRenderer.pos(f5 - f6, 0.0F - f7, 0.0).tex(f2, f4).color(n4, n5, n6, 128).normal(0.0F, 1.0F, 0.0F).endVertex();
      worldRenderer.pos(f5 - f6, 1.0F - f7, 0.0).tex(f2, f3).color(n4, n5, n6, 128).normal(0.0F, 1.0F, 0.0F).endVertex();
      worldRenderer.pos(0.0F - f6, 1.0F - f7, 0.0).tex(f, f3).color(n4, n5, n6, 128).normal(0.0F, 1.0F, 0.0F).endVertex();
      instance.draw();
      GlStateManager.disableBlend();
      GlStateManager.disableRescaleNormal();
      GlStateManager.popMatrix();
      super.doRender(entity, x, y, z, entityYaw, partialTicks);
   }

   public ResourceLocation getEntityTexture(EntityXPOrb entity) {
      return experienceOrbTextures;
   }
}
