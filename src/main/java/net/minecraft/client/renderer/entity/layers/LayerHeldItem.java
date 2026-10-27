package net.minecraft.client.renderer.entity.layers;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class LayerHeldItem implements LayerRenderer<EntityLivingBase> {
   public RendererLivingEntity<?> livingEntityRenderer;

   public LayerHeldItem(RendererLivingEntity<?> livingEntityRendererIn) {
      this.livingEntityRenderer = livingEntityRendererIn;
   }

   @Override
   public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float f, float f2, float partialTicks, float f3, float f4, float f5, float scale) {
      ItemStack heldItem = entitylivingbaseIn.getHeldItem();
      if (heldItem != null) {
         GlStateManager.pushMatrix();
         if (this.livingEntityRenderer.getMainModel().isChild) {
            float f6 = 0.5F;
            GlStateManager.translate(0.0F, 0.625F, 0.0F);
            GlStateManager.rotate(-20.0F, -1.0F, 0.0F, 0.0F);
            GlStateManager.scale(f6, f6, f6);
         }

         ((ModelBiped)this.livingEntityRenderer.getMainModel()).postRenderArm(0.0625F);
         GlStateManager.translate(-0.0625F, 0.4375F, 0.0625F);
         if (entitylivingbaseIn instanceof EntityPlayer && ((EntityPlayer)entitylivingbaseIn).fishEntity != null) {
            heldItem = new ItemStack(Items.fishing_rod, 0);
         }

         Item item = heldItem.getItem();
         Minecraft minecraft = Minecraft.getMinecraft();
         if (item instanceof ItemBlock && Block.getBlockFromItem(item).getRenderType() == 2) {
            GlStateManager.translate(0.0F, 0.1875F, -0.3125F);
            GlStateManager.rotate(20.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
            float f6 = 0.375F;
            GlStateManager.scale(-f6, -f6, f6);
         }

         if (entitylivingbaseIn.isSneaking()) {
            GlStateManager.translate(0.0F, 0.203125F, 0.0F);
         }

         minecraft.getItemRenderer().renderItem(entitylivingbaseIn, heldItem, TransformType.THIRD_PERSON);
         GlStateManager.popMatrix();
      }
   }

   @Override
   public boolean shouldCombineTextures() {
      return false;
   }
}
