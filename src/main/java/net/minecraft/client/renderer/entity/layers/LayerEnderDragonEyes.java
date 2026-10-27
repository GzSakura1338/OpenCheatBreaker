package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderDragon;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.util.ResourceLocation;
import net.optifine.Config;
import net.optifine.shaders.Shaders;

public class LayerEnderDragonEyes implements LayerRenderer<EntityDragon> {
   public static ResourceLocation TEXTURE = new ResourceLocation("textures/entity/enderdragon/dragon_eyes.png");
   public RenderDragon dragonRenderer;

   public LayerEnderDragonEyes(RenderDragon dragonRendererIn) {
      this.dragonRenderer = dragonRendererIn;
   }

   public void doRenderLayer(EntityDragon entitylivingbaseIn, float f, float f2, float partialTicks, float f3, float f4, float f5, float scale) {
      this.dragonRenderer.bindTexture(TEXTURE);
      GlStateManager.enableBlend();
      GlStateManager.disableAlpha();
      GlStateManager.blendFunc(1, 1);
      GlStateManager.disableLighting();
      GlStateManager.depthFunc(514);
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
      this.dragonRenderer.getMainModel().render(entitylivingbaseIn, f, f2, f3, f4, f5, scale);
      Config.getRenderGlobal().renderOverlayEyes = false;
      if (Config.isShaders()) {
         Shaders.endSpiderEyes();
      }

      this.dragonRenderer.setLightmap(entitylivingbaseIn, partialTicks);
      GlStateManager.disableBlend();
      GlStateManager.enableAlpha();
      GlStateManager.depthFunc(515);
   }

   @Override
   public boolean shouldCombineTextures() {
      return false;
   }
}
