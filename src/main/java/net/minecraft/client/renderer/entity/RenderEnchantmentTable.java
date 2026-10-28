package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBook;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderEnchantmentTable extends TileEntitySpecialRenderer {
    public static ResourceLocation BOOK_TEXTURE = new ResourceLocation("textures/entity/enchanting_table_book.png");
    public ModelBook bookModel = new ModelBook();

    public void renderTileEntityAt(TileEntityEnchantmentTable tileEntityEnchantmentTable, double d, double d2, double d3, float f) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)d + 0.5F, (float)d2 + 0.75F, (float)d3 + 0.5F);
        float f2 = tileEntityEnchantmentTable.tickCount + f;
        GL11.glTranslatef(0.0F, 0.1F + MathHelper.sin(f2 * 0.1F) * 0.01F, 0.0F);
        float f3 = tileEntityEnchantmentTable.bookRotation - tileEntityEnchantmentTable.bookRotationPrev;

        while (f3 >= (float) Math.PI) {
            f3 -= (float) (Math.PI * 2);
        }

        while (f3 < (float) -Math.PI) {
            f3 += (float) (Math.PI * 2);
        }

        float f4 = tileEntityEnchantmentTable.bookRotationPrev + f3 * f;
        GL11.glRotatef(-f4 * 180.0F / (float) Math.PI, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(80.0F, 0.0F, 0.0F, 1.0F);
        this.bindTexture(BOOK_TEXTURE);
        float f5 = tileEntityEnchantmentTable.pageFlipPrev + (tileEntityEnchantmentTable.pageFlip - tileEntityEnchantmentTable.pageFlipPrev) * f + 0.25F;
        float f6 = tileEntityEnchantmentTable.pageFlipPrev + (tileEntityEnchantmentTable.pageFlip - tileEntityEnchantmentTable.pageFlipPrev) * f + 0.75F;
        f5 = (f5 - MathHelper.truncateDoubleToInt(f5)) * 1.6F - 0.3F;
        f6 = (f6 - MathHelper.truncateDoubleToInt(f6)) * 1.6F - 0.3F;
        if (f5 < 0.0F) {
            f5 = 0.0F;
        }

        if (f6 < 0.0F) {
            f6 = 0.0F;
        }

        if (f5 > 1.0F) {
            f5 = 1.0F;
        }

        if (f6 > 1.0F) {
            f6 = 1.0F;
        }

        float f7 = tileEntityEnchantmentTable.bookSpreadPrev + (tileEntityEnchantmentTable.bookSpread - tileEntityEnchantmentTable.bookSpreadPrev) * f;
        GL11.glEnable(2884);
        this.bookModel.render(null, f2, f5, f6, f7, 0.0F, 0.0625F);
        GL11.glPopMatrix();
    }

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double d, double d2, double d3, float f) {
        this.renderTileEntityAt((TileEntityEnchantmentTable)tileEntity, d, d2, d3, f);
    }
}
