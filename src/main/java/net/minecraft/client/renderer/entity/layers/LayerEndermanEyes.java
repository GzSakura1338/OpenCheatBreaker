package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderEnderman;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.util.ResourceLocation;
import net.optifine.Config;
import net.optifine.shaders.Shaders;

public class LayerEndermanEyes implements LayerRenderer<EntityEnderman> {
   public static ResourceLocation RES_ENDERMAN_EYES = new ResourceLocation("textures/entity/enderman/enderman_eyes.png");
   public RenderEnderman endermanRenderer;

   public LayerEndermanEyes(RenderEnderman endermanRendererIn) {
      this.endermanRenderer = endermanRendererIn;
   }

   public void doRenderLayer(EntityEnderman entitylivingbaseIn, float f, float f2, float partialTicks, float f3, float f4, float f5, float scale) {
      this.endermanRenderer.bindTexture(RES_ENDERMAN_EYES);
      GlStateManager.enableBlend();
      GlStateManager.disableAlpha();
      GlStateManager.blendFunc(1, 1);
      GlStateManager.disableLighting();
      GlStateManager.depthMask(!entitylivingbaseIn.isInvisible());
      char c = '\uf0f0';
      int n = c % 65536;
      int n2 = c / 65536;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, n / 1.0F, n2 / 1.0F);
      GlStateManager.enableLighting();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      if (Config.isShaders()) {
         Shaders.beginSpiderEyes();
      }

      Config.getRenderGlobal().renderOverlayEyes = true;
      this.endermanRenderer.getMainModel().render(entitylivingbaseIn, f, f2, f3, f4, f5, scale);
      Config.getRenderGlobal().renderOverlayEyes = false;
      if (Config.isShaders()) {
         Shaders.endSpiderEyes();
      }

      this.endermanRenderer.setLightmap(entitylivingbaseIn, partialTicks);
      GlStateManager.depthMask(true);
      GlStateManager.disableBlend();
      GlStateManager.enableAlpha();
   }

   @Override
   public boolean shouldCombineTextures() {
      return false;
   }
}
