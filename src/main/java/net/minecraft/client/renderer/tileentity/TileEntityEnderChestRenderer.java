package net.minecraft.client.renderer.tileentity;

import net.minecraft.client.model.ModelChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.util.ResourceLocation;

public class TileEntityEnderChestRenderer extends TileEntitySpecialRenderer<TileEntityEnderChest> {
   public static ResourceLocation ENDER_CHEST_TEXTURE = new ResourceLocation("textures/entity/chest/ender.png");
   public ModelChest model = new ModelChest();

   public void renderTileEntityAt(TileEntityEnderChest te, double x, double y, double z, float partialTicks, int destroyStage) {
      int n = 0;
      if (te.hasWorldObj()) {
         n = te.getBlockMetadata();
      }

      if (destroyStage >= 0) {
         this.bindTexture(DESTROY_STAGES[destroyStage]);
         GlStateManager.matrixMode(5890);
         GlStateManager.pushMatrix();
         GlStateManager.scale(4.0F, 4.0F, 1.0F);
         GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
         GlStateManager.matrixMode(5888);
      } else {
         this.bindTexture(ENDER_CHEST_TEXTURE);
      }

      GlStateManager.pushMatrix();
      GlStateManager.enableRescaleNormal();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
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

      GlStateManager.rotate(s, 0.0F, 1.0F, 0.0F);
      GlStateManager.translate(-0.5F, -0.5F, -0.5F);
      float f = te.prevLidAngle + (te.lidAngle - te.prevLidAngle) * partialTicks;
      f = 1.0F - f;
      f = 1.0F - f * f * f;
      this.model.chestLid.rotateAngleX = -(f * (float) Math.PI / 2.0F);
      this.model.renderAll();
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
