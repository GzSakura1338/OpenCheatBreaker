package net.minecraft.client.renderer.tileentity;

import java.util.Calendar;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.model.ModelLargeChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ResourceLocation;

public class TileEntityChestRenderer extends TileEntitySpecialRenderer<TileEntityChest> {
   public ModelChest modelChest = new ModelChest();
   public static ResourceLocation trappedDoublePng = new ResourceLocation("textures/entity/chest/trapped_double.png");
   public ModelChest modelChest2 = new ModelLargeChest();
   public boolean isChristmas;
   public static ResourceLocation christmasDoublePng = new ResourceLocation("textures/entity/chest/christmas_double.png");
   public static ResourceLocation normalDoublePng = new ResourceLocation("textures/entity/chest/normal_double.png");
   public static ResourceLocation trappedPng = new ResourceLocation("textures/entity/chest/trapped.png");
   public static ResourceLocation christmasPng = new ResourceLocation("textures/entity/chest/christmas.png");
   public static ResourceLocation normalPng = new ResourceLocation("textures/entity/chest/normal.png");

   public TileEntityChestRenderer() {
      Calendar instance = Calendar.getInstance();
      if (instance.get(2) + 1 == 12 && instance.get(5) >= 24 && instance.get(5) <= 26) {
         this.isChristmas = true;
      }
   }

   public void renderTileEntityAt(TileEntityChest te, double x, double y, double z, float partialTicks, int destroyStage) {
      GlStateManager.enableDepth();
      GlStateManager.depthFunc(515);
      GlStateManager.depthMask(true);
      int n;
      if (!te.hasWorldObj()) {
         n = 0;
      } else {
         Block blockType = te.getBlockType();
         n = te.getBlockMetadata();
         if (blockType instanceof BlockChest && n == 0) {
            ((BlockChest)blockType).checkForSurroundingChests(te.getWorld(), te.getPos(), te.getWorld().getBlockState(te.getPos()));
            n = te.getBlockMetadata();
         }

         te.checkForAdjacentChests();
      }

      if (te.adjacentChestZNeg == null && te.adjacentChestXNeg == null) {
         ModelChest modelChest;
         if (te.adjacentChestXPos == null && te.adjacentChestZPos == null) {
            modelChest = this.modelChest;
            if (destroyStage >= 0) {
               this.bindTexture(DESTROY_STAGES[destroyStage]);
               GlStateManager.matrixMode(5890);
               GlStateManager.pushMatrix();
               GlStateManager.scale(4.0F, 4.0F, 1.0F);
               GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
               GlStateManager.matrixMode(5888);
            } else if (this.isChristmas) {
               this.bindTexture(christmasPng);
            } else if (te.getChestType() == 1) {
               this.bindTexture(trappedPng);
            } else {
               this.bindTexture(normalPng);
            }
         } else {
            modelChest = this.modelChest2;
            if (destroyStage >= 0) {
               this.bindTexture(DESTROY_STAGES[destroyStage]);
               GlStateManager.matrixMode(5890);
               GlStateManager.pushMatrix();
               GlStateManager.scale(8.0F, 4.0F, 1.0F);
               GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
               GlStateManager.matrixMode(5888);
            } else if (this.isChristmas) {
               this.bindTexture(christmasDoublePng);
            } else if (te.getChestType() == 1) {
               this.bindTexture(trappedDoublePng);
            } else {
               this.bindTexture(normalDoublePng);
            }
         }

         GlStateManager.pushMatrix();
         GlStateManager.enableRescaleNormal();
         if (destroyStage < 0) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         }

         GlStateManager.translate((float)x, (float)y + 1.0F, (float)z + 1.0F);
         GlStateManager.scale(1.0F, -1.0F, -1.0F);
         GlStateManager.translate(0.5F, 0.5F, 0.5F);
         short s = 0;
         if (n == 2) {
            s = 180;
         }

         if (n == 3) {
            s = 0;
         }

         if (n == 4) {
            s = 90;
         }

         if (n == 5) {
            s = -90;
         }

         if (n == 2 && te.adjacentChestXPos != null) {
            GlStateManager.translate(1.0F, 0.0F, 0.0F);
         }

         if (n == 5 && te.adjacentChestZPos != null) {
            GlStateManager.translate(0.0F, 0.0F, -1.0F);
         }

         GlStateManager.rotate(s, 0.0F, 1.0F, 0.0F);
         GlStateManager.translate(-0.5F, -0.5F, -0.5F);
         float f = te.prevLidAngle + (te.lidAngle - te.prevLidAngle) * partialTicks;
         if (te.adjacentChestZNeg != null) {
            float f2 = te.adjacentChestZNeg.prevLidAngle + (te.adjacentChestZNeg.lidAngle - te.adjacentChestZNeg.prevLidAngle) * partialTicks;
            if (f2 > f) {
               f = f2;
            }
         }

         if (te.adjacentChestXNeg != null) {
            float f2 = te.adjacentChestXNeg.prevLidAngle + (te.adjacentChestXNeg.lidAngle - te.adjacentChestXNeg.prevLidAngle) * partialTicks;
            if (f2 > f) {
               f = f2;
            }
         }

         f = 1.0F - f;
         f = 1.0F - f * f * f;
         modelChest.chestLid.rotateAngleX = -(f * (float) Math.PI / 2.0F);
         modelChest.renderAll();
         GlStateManager.disableRescaleNormal();
         GlStateManager.popMatrix();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         if (destroyStage >= 0) {
            GlStateManager.matrixMode(5890);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
         }
      }
   }
}
