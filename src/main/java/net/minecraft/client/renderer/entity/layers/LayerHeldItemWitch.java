package net.minecraft.client.renderer.entity.layers;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelWitch;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.RenderWitch;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class LayerHeldItemWitch implements LayerRenderer<EntityWitch> {
   public RenderWitch witchRenderer;

   public LayerHeldItemWitch(RenderWitch witchRendererIn) {
      this.witchRenderer = witchRendererIn;
   }

   public void doRenderLayer(EntityWitch entitylivingbaseIn, float f, float f2, float partialTicks, float f3, float f4, float f5, float scale) {
      ItemStack heldItem = entitylivingbaseIn.getHeldItem();
      if (heldItem != null) {
         GlStateManager.color(1.0F, 1.0F, 1.0F);
         GlStateManager.pushMatrix();
         if (this.witchRenderer.getMainModel().isChild) {
            GlStateManager.translate(0.0F, 0.625F, 0.0F);
            GlStateManager.rotate(-20.0F, -1.0F, 0.0F, 0.0F);
            float f6 = 0.5F;
            GlStateManager.scale(f6, f6, f6);
         }

         ((ModelWitch)this.witchRenderer.getMainModel()).villagerNose.postRender(0.0625F);
         GlStateManager.translate(-0.0625F, 0.53125F, 0.21875F);
         Item item = heldItem.getItem();
         Minecraft minecraft = Minecraft.getMinecraft();
         if (item instanceof ItemBlock && minecraft.getBlockRendererDispatcher().isRenderTypeChest(Block.getBlockFromItem(item), heldItem.getMetadata())) {
            GlStateManager.translate(0.0F, 0.0625F, -0.25F);
            GlStateManager.rotate(30.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(-5.0F, 0.0F, 1.0F, 0.0F);
            float f6 = 0.375F;
            GlStateManager.scale(f6, -f6, f6);
         } else if (item == Items.bow) {
            GlStateManager.translate(0.0F, 0.125F, -0.125F);
            GlStateManager.rotate(-45.0F, 0.0F, 1.0F, 0.0F);
            float f6 = 0.625F;
            GlStateManager.scale(f6, -f6, f6);
            GlStateManager.rotate(-100.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(-20.0F, 0.0F, 1.0F, 0.0F);
         } else if (item.isFull3D()) {
            if (item.shouldRotateAroundWhenRendering()) {
               GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
               GlStateManager.translate(0.0F, -0.0625F, 0.0F);
            }

            this.witchRenderer.transformHeldFull3DItemLayer();
            GlStateManager.translate(0.0625F, -0.125F, 0.0F);
            float f6 = 0.625F;
            GlStateManager.scale(f6, -f6, f6);
            GlStateManager.rotate(0.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(0.0F, 0.0F, 1.0F, 0.0F);
         } else {
            GlStateManager.translate(0.1875F, 0.1875F, 0.0F);
            float f6 = 0.875F;
            GlStateManager.scale(f6, f6, f6);
            GlStateManager.rotate(-20.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(-60.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(-30.0F, 0.0F, 0.0F, 1.0F);
         }

         GlStateManager.rotate(-15.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotate(40.0F, 0.0F, 0.0F, 1.0F);
         minecraft.getItemRenderer().renderItem(entitylivingbaseIn, heldItem, TransformType.THIRD_PERSON);
         GlStateManager.popMatrix();
      }
   }

   @Override
   public boolean shouldCombineTextures() {
      return false;
   }
}
