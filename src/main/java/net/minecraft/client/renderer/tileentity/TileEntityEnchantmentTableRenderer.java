package net.minecraft.client.renderer.tileentity;

import net.minecraft.client.model.ModelBook;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class TileEntityEnchantmentTableRenderer extends TileEntitySpecialRenderer<TileEntityEnchantmentTable> {
   public static ResourceLocation TEXTURE_BOOK = new ResourceLocation("textures/entity/enchanting_table_book.png");
   public ModelBook bookModel = new ModelBook();

   public void renderTileEntityAt(TileEntityEnchantmentTable te, double x, double y, double z, float partialTicks, int destroyStage) {
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)x + 0.5F, (float)y + 0.75F, (float)z + 0.5F);
      float f = te.tickCount + partialTicks;
      GlStateManager.translate(0.0F, 0.1F + MathHelper.sin(f * 0.1F) * 0.01F, 0.0F);
      float f2 = te.bookRotation - te.bookRotationPrev;

      while (f2 >= (float) Math.PI) {
         f2 -= (float) (Math.PI * 2);
      }

      while (f2 < (float) -Math.PI) {
         f2 += (float) (Math.PI * 2);
      }

      float f3 = te.bookRotationPrev + f2 * partialTicks;
      GlStateManager.rotate(-f3 * 180.0F / (float) Math.PI, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(80.0F, 0.0F, 0.0F, 1.0F);
      this.bindTexture(TEXTURE_BOOK);
      float f4 = te.pageFlipPrev + (te.pageFlip - te.pageFlipPrev) * partialTicks + 0.25F;
      float f5 = te.pageFlipPrev + (te.pageFlip - te.pageFlipPrev) * partialTicks + 0.75F;
      f4 = (f4 - MathHelper.truncateDoubleToInt(f4)) * 1.6F - 0.3F;
      f5 = (f5 - MathHelper.truncateDoubleToInt(f5)) * 1.6F - 0.3F;
      if (f4 < 0.0F) {
         f4 = 0.0F;
      }

      if (f5 < 0.0F) {
         f5 = 0.0F;
      }

      if (f4 > 1.0F) {
         f4 = 1.0F;
      }

      if (f5 > 1.0F) {
         f5 = 1.0F;
      }

      float f6 = te.bookSpreadPrev + (te.bookSpread - te.bookSpreadPrev) * partialTicks;
      GlStateManager.enableCull();
      this.bookModel.render((Entity)null, f, f4, f5, f6, 0.0F, 0.0625F);
      GlStateManager.popMatrix();
   }
}
