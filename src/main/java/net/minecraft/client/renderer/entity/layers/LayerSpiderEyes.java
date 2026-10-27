package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderSpider;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.util.ResourceLocation;
import net.optifine.Config;
import net.optifine.shaders.Shaders;

public class LayerSpiderEyes implements LayerRenderer<EntitySpider> {
   public static ResourceLocation SPIDER_EYES = new ResourceLocation("textures/entity/spider_eyes.png");
   public RenderSpider spiderRenderer;

   public LayerSpiderEyes(RenderSpider spiderRendererIn) {
      this.spiderRenderer = spiderRendererIn;
   }

   public void doRenderLayer(EntitySpider entitylivingbaseIn, float f, float f2, float partialTicks, float f3, float f4, float f5, float scale) {
      this.spiderRenderer.bindTexture(SPIDER_EYES);
      GlStateManager.enableBlend();
      GlStateManager.disableAlpha();
      GlStateManager.blendFunc(1, 1);
      if (entitylivingbaseIn.isInvisible()) {
         GlStateManager.depthMask(false);
      } else {
         GlStateManager.depthMask(true);
      }

      int n = 61680;
      int n2 = n % 65536;
      int n3 = n / 65536;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, n2 / 1.0F, n3 / 1.0F);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      if (Config.isShaders()) {
         Shaders.beginSpiderEyes();
      }

      Config.getRenderGlobal().renderOverlayEyes = true;
      this.spiderRenderer.getMainModel().render(entitylivingbaseIn, f, f2, f3, f4, f5, scale);
      Config.getRenderGlobal().renderOverlayEyes = false;
      if (Config.isShaders()) {
         Shaders.endSpiderEyes();
      }

      n = entitylivingbaseIn.getBrightnessForRender(partialTicks);
      n2 = n % 65536;
      n3 = n / 65536;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, n2 / 1.0F, n3 / 1.0F);
      this.spiderRenderer.setLightmap(entitylivingbaseIn, partialTicks);
      GlStateManager.disableBlend();
      GlStateManager.enableAlpha();
   }

   @Override
   public boolean shouldCombineTextures() {
      return false;
   }
}
