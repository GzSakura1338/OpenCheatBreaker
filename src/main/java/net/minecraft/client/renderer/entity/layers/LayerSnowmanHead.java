package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.RenderSnowMan;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class LayerSnowmanHead implements LayerRenderer<EntitySnowman> {
   public RenderSnowMan snowManRenderer;

   public LayerSnowmanHead(RenderSnowMan snowManRendererIn) {
      this.snowManRenderer = snowManRendererIn;
   }

   public void doRenderLayer(EntitySnowman entitylivingbaseIn, float f, float f2, float partialTicks, float f3, float f4, float f5, float scale) {
      if (!entitylivingbaseIn.isInvisible()) {
         GlStateManager.pushMatrix();
         this.snowManRenderer.getMainModel().head.postRender(0.0625F);
         float f6 = 0.625F;
         GlStateManager.translate(0.0F, -0.34375F, 0.0F);
         GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.scale(f6, -f6, -f6);
         Minecraft.getMinecraft().getItemRenderer().renderItem(entitylivingbaseIn, new ItemStack(Blocks.pumpkin, 1), TransformType.HEAD);
         GlStateManager.popMatrix();
      }
   }

   @Override
   public boolean shouldCombineTextures() {
      return true;
   }
}
