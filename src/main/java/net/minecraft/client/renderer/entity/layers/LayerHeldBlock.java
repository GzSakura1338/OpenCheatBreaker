package net.minecraft.client.renderer.entity.layers;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderEnderman;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.monster.EntityEnderman;

public class LayerHeldBlock implements LayerRenderer<EntityEnderman> {
   public RenderEnderman endermanRenderer;

   public LayerHeldBlock(RenderEnderman endermanRendererIn) {
      this.endermanRenderer = endermanRendererIn;
   }

   public void doRenderLayer(EntityEnderman entitylivingbaseIn, float f, float f2, float partialTicks, float f3, float f4, float f5, float scale) {
      IBlockState heldBlockState = entitylivingbaseIn.getHeldBlockState();
      if (heldBlockState.getBlock().getMaterial() != Material.air) {
         BlockRendererDispatcher blockRendererDispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
         GlStateManager.enableRescaleNormal();
         GlStateManager.pushMatrix();
         GlStateManager.translate(0.0F, 0.6875F, -0.75F);
         GlStateManager.rotate(20.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.translate(0.25F, 0.1875F, 0.25F);
         float f6 = 0.5F;
         GlStateManager.scale(-f6, -f6, f6);
         int brightnessForRender = entitylivingbaseIn.getBrightnessForRender(partialTicks);
         int n = brightnessForRender % 65536;
         int n2 = brightnessForRender / 65536;
         OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, n / 1.0F, n2 / 1.0F);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         this.endermanRenderer.bindTexture(TextureMap.locationBlocksTexture);
         blockRendererDispatcher.renderBlockBrightness(heldBlockState, 1.0F);
         GlStateManager.popMatrix();
         GlStateManager.disableRescaleNormal();
      }
   }

   @Override
   public boolean shouldCombineTextures() {
      return false;
   }
}
